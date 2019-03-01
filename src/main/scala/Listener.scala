import Listener._
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSelection, Address, AddressFromURIString, Props, RootActorPath}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import Subscriber.getController

import scala.collection.mutable


class Listener(name: String) extends Actor with ActorLogging {

  private val cluster = Cluster(context.system)

  //ссылка на созданый актор менеджер
  var listener: ActorRef = _

  var LoginController: LoginController = _

  var UserController: UserController = _

  var VPGcontrol: ViewPagerController = _

  var User_name: String = _

  var address = "akka.tcp://chat@"

  var connect: String = _

  var refs = Set.empty[ActorSelection]

  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember], classOf[MemberRemoved], classOf[MemberUp], classOf[MemberJoined])
  }

  override def postStop(): Unit = cluster.unsubscribe(self)


  def receive = {

    case MemberUp(member) =>
      val ref = context.actorSelection(RootActorPath(member.address) + s"/user/Manager")
      log.info(s"Reference: $ref")
      ref ! AddUser(name)

    case UnreachableMember(member) =>
      log.info("Member is unreachable: ", member)

    case MemberRemoved(member, previousStatus) =>
//      refs.foreach(e => if(e.anchorPath == member.address){refs.})

    case Join(seed, name, ip) =>
      connect = address + seed
      cluster.join(AddressFromURIString(connect))
      User_name = name


    case login(controller, actor) =>
      LoginController = controller
      listener = actor


    case getUController(controller) =>
      UserController = controller
      UserController.actor = listener
      VPGcontrol = UserController.control
      val subscriber = context.actorOf(Props[Subscriber])
      subscriber ! getController(VPGcontrol, UserController)
      val publicActor = context.actorOf(Props[Publisher], User_name)
      VPGcontrol.publicActor = publicActor


    case Message(name: String, text: String) =>
      if (VPGcontrol == null) {
        Thread.sleep(3000)
      } else {
        VPGcontrol.post(name, text)
        if (name != this.name)
          log.info(s"Message from $name ===== $text")
      }

    case AddUser(name: String) =>
      if (VPGcontrol == null || User_name == name) {
        Thread.sleep(2000)
      }else{
        UserController.addUser(name)
      }
  }
}

object Listener {

  case class login(controller: LoginController, actor: ActorRef)

  case class getUController(controller: UserController)

  case class Message(name: String, text: String)

  case class Join(seed: String, name: String, ip: String)

  case class AddUser(name: String)

}
