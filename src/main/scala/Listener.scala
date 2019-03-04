import Destination.{GetUcontrol, Vpgcontrol}
import Listener._
import Subscriber.GetController
import akka.actor.{Actor, ActorLogging, ActorRef, AddressFromURIString, Props, RootActorPath}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe}


class Listener(name: String) extends Actor with ActorLogging {

  private val cluster = Cluster(context.system)

  //ссылка на созданый актор менеджер,приват
  var listener, privateActors, destinationActors: ActorRef = _

  var loginController: LoginController = _

  var userController: UserController = _

  var vpgcontrol: ViewPagerController = _

  var username: String = _

  var address = "akka.tcp://chat@"

  var connect: String = _

  val mediator: ActorRef = DistributedPubSub(context.system).mediator

  mediator ! Subscribe("Delete", self)

  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember], classOf[MemberRemoved], classOf[MemberUp], classOf[MemberJoined])
  }

  override def postStop(): Unit = cluster.unsubscribe(self)


  def receive: PartialFunction[Any, Unit] = {

    case MemberUp(member) =>
      val ref = context.actorSelection(RootActorPath(member.address) + s"/user/Manager")
      ref ! AddUser(name)

    case CloseCallback =>
      mediator ! Publish("Delete", Deleteuser(name))

    case Deleteuser(name: String) if this.name != name =>
      userController.DeleteTAb(name)

    case Join(seed, name) =>
      connect = address + seed
      cluster.join(AddressFromURIString(connect))
      username = name


    case Login(controller, actor) =>
      loginController = controller
      listener = actor


    case GetControllerUC(controller) =>
      userController = controller
      userController.actor = listener
      vpgcontrol = userController.control
      vpgcontrol.userController = userController
      val subscriber = context.actorOf(Props[Subscriber])
      subscriber ! GetController(vpgcontrol)
      val publicActor = context.actorOf(Props(classOf[Publisher], name))
      val destination = context.actorOf(Props[Destination], username)
      destinationActors = destination
      destination ! GetUcontrol(userController)
      userController.private_actor = destination
      val privateActor = context.actorOf(Props(classOf[Sender], name))
      privateActors = privateActor
      vpgcontrol.publicActor = publicActor

    case GetcontrollerTab(viewPagerController) =>
      destinationActors ! Vpgcontrol(viewPagerController)
      viewPagerController.check_Status = true
      viewPagerController.privateActor = privateActors
      viewPagerController.name = username


    case AddUser(name: String) =>
      if (vpgcontrol == null || username == name) {
      } else {
        userController.addUser(name)
      }

  }
}

object Listener {

  case class Login(controller: LoginController, actor: ActorRef)

  case class GetControllerUC(controller: UserController)

  case class Join(seed: String, name: String)

  case class AddUser(name: String)

  case class Deleteuser(name: String)

  case class GetcontrollerTab(viewPagerController: ViewPagerController)

  case object CloseCallback

}
