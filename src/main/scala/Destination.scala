import Destination.{get_Controllers, get_Ucontrol, vpg_control}
import Sender.Message_To
import akka.actor.{Actor, ActorLogging}
import akka.cluster.pubsub.DistributedPubSub
import scala.collection.JavaConverters._

class Destination extends Actor with ActorLogging {

  import akka.cluster.pubsub.DistributedPubSubMediator.Put

  val mediator = DistributedPubSub(context.system).mediator

  mediator ! Put(self)


  var User_controller: UserController = _
  var control: ViewPagerController = _


  def receive = {
    case Message_To(name, text) =>
      User_controller.tabPane.getTabs.asScala.find(_.getText == name)
        .fold(User_controller.addTabs(name))(User_controller.tabPane.getSelectionModel.select)
      control.post(name, text)


    case get_Controllers(controller) =>
      control = controller

    case get_Ucontrol(userController) =>
      User_controller = userController

    case vpg_control(viewPagerController) =>
      control = viewPagerController
  }
}

object Destination {

  case class get_Controllers(viewPagerController: ViewPagerController)

  case class get_Ucontrol(userController: UserController)

  case class vpg_control(viewPagerController: ViewPagerController)

}
