import Listener._
import akka.actor.{Actor, ActorLogging, ActorRef, Address, AddressFromURIString, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import Subscriber.getController

import scala.collection.mutable


class Listener extends Actor with ActorLogging {

  val cluster = Cluster(context.system)

  //ссылка на созданый актор менеджер
  var listener: ActorRef = _

  var LoginController: LoginController = _

  var UserController: UserController = _

  var VPGcontrol: ViewPagerController = _

  var storage = mutable.Map[String, String]().empty

  var name: String = _

  var address = "akka.tcp://chat@"

  var connect: String = _

  var refs = Set.empty[ActorRef]

  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember], classOf[MemberRemoved], classOf[MemberUp], classOf[MemberJoined])
  }

  override def postStop(): Unit = cluster.unsubscribe(self)


  def receive = {

    case MemberUp(member) =>
      refs += listener
      refs.foreach(e=>println(e))

    case MemberJoined(member)=>


    case Join(seed) =>
      connect = address + seed
      cluster.join(AddressFromURIString(connect))


    case login(controller, actor) =>
      LoginController = controller
      listener = actor


    case Registration(name, ip, seed) =>
//      storage += (name -> ip)
      this.name = name


    case getUController(controller) =>
      UserController = controller
      UserController.actor = listener
      UserController.addUser(name)
      VPGcontrol = UserController.control
      val subscriber = context.actorOf(Props[Subscriber])
      subscriber ! getController(VPGcontrol, UserController)
      val publicActor = context.actorOf(Props[Publisher], name)
      VPGcontrol.publicActor = publicActor


    case Message(text) =>
      VPGcontrol.post(text)
      log.info("Исполнилось")

  }
}

object Listener {

  case class login(controller: LoginController, actor: ActorRef)

  case class Registration(name: String, ip: String, seed: String)

  case class getUController(controller: UserController)

  case class Message(text: String)

  case class Join(seed: String)

  case class CheckClusterSize(msg:String)

}
