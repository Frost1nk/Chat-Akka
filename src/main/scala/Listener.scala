import Destination.get_Controllers
import Listener._
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSelection, Address, AddressFromURIString, Props, RootActorPath}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import Subscriber.getController

import scala.collection.mutable


class Listener(name: String) extends Actor with ActorLogging {

  private val cluster = Cluster(context.system)

  //ссылка на созданый актор менеджер,приват
  var listener,private_Actor,destination_Actor: ActorRef = _

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
      log.info("Member is unreachable: {}", member)
      val ref = context.actorSelection(RootActorPath(cluster.selfAddress)+s"/user/Manager")
      ref ! Delete_User(User_name)

    case MemberDowned(member)=>
      val ref = context.actorSelection(RootActorPath(cluster.selfAddress)+s"/user/Manager")
      ref ! Delete_User(User_name)

    case MemberRemoved(member, previousStatus) =>
//      val ref = context.actorSelection(RootActorPath(member.address)+s"/user/Manager")
//      ref ! Delete_User(User_name)

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
      VPGcontrol.userController = UserController
      val subscriber = context.actorOf(Props[Subscriber])
      subscriber ! getController(VPGcontrol, UserController)
      val publicActor = context.actorOf(Props[Publisher], User_name)
      val destination = context.actorOf(Props[Destination],User_name.toUpperCase)
      destination_Actor = destination
      UserController.private_actor = destination
      val privateActor = context.actorOf(Props[Sender],User_name.toLowerCase)
      println(privateActor.path)
      private_Actor = privateActor
      VPGcontrol.publicActor = publicActor

    case get_controller_Tab(viewPagerController)=>
      destination_Actor ! get_Controllers(viewPagerController)
      viewPagerController.check_Status = true
      viewPagerController.privateActor = private_Actor


    case AddUser(name: String) =>
      if (VPGcontrol == null || User_name == name) {
        Thread.sleep(2000)
      } else {
        UserController.addUser(name)
      }

    case Delete_User(name)=>
      UserController.deleteUser(name)
  }
}

object Listener {

  case class login(controller: LoginController, actor: ActorRef)

  case class getUController(controller: UserController)

  case class Join(seed: String, name: String, ip: String)

  case class AddUser(name: String)

  case class get_controller_Tab(viewPagerController: ViewPagerController)

  case class Delete_User(name:String)

}
