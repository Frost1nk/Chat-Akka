import Destination.{GetControllers, GetUcontrol, Vpgcontrol}
import Sender.Message_To
import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.cluster.pubsub.DistributedPubSub
import javafx.application.Platform

import scala.collection.JavaConverters._

class Destination extends Actor with ActorLogging {

  import akka.cluster.pubsub.DistributedPubSubMediator.Put

  val mediator: ActorRef = DistributedPubSub(context.system).mediator

  mediator ! Put(self)


  var User_controller: UserController = _

  var control: ViewPagerController = _


  def receive: PartialFunction[Any, Unit] = {
    case Message_To(name, text) =>
      User_controller.tabPane.getTabs.asScala.find(_.getText == name)
        .fold(User_controller.AddTabs(name))(User_controller.tabPane.getSelectionModel.select)
      Platform.runLater(() => {
        control.Post(name, text)
      })


    case GetControllers(controller) =>
      control = controller

    case GetUcontrol(userController) =>
      User_controller = userController

    case Vpgcontrol(viewPagerController) =>
      control = viewPagerController
  }
}

object Destination {

  case class GetControllers(viewPagerController: ViewPagerController)

  case class GetUcontrol(userController: UserController)

  case class Vpgcontrol(viewPagerController: ViewPagerController)

}
