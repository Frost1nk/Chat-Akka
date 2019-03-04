import Destination.{get_Controllers, get_Ucontrol}
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
      val publicActor = context.actorOf(Props(classOf[Publisher],name))
      val destination = context.actorOf(Props[Destination],User_name)
      destination_Actor = destination
      destination ! get_Ucontrol(UserController)
      UserController.private_actor = destination
      val privateActor = context.actorOf(Props(classOf[Sender],name))
      private_Actor = privateActor
      VPGcontrol.publicActor = publicActor

    case get_controller_Tab(viewPagerController)=>
      viewPagerController.check_Status = true
      viewPagerController.privateActor = private_Actor
      viewPagerController.name = User_name


    case AddUser(name: String) =>
      if (VPGcontrol == null || User_name == name) {

      } else {
        UserController.addUser(name)
      }

    case Deleteuser(name:String)=>
      UserController.deleteTAb(name)


  }
}

object Listener {

  case class login(controller: LoginController, actor: ActorRef)

  case class getUController(controller: UserController)

  case class Join(seed: String, name: String, ip: String)

  case class AddUser(name: String)

  case class Deleteuser(name:String)

  case class get_controller_Tab(viewPagerController: ViewPagerController)

}
