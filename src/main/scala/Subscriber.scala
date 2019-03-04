import Publisher.Msg
import Subscriber.getController
import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.cluster.pubsub.DistributedPubSub


class Subscriber extends Actor with ActorLogging {

  import akka.cluster.pubsub.DistributedPubSubMediator.{Subscribe, SubscribeAck}

  val mediator = DistributedPubSub(context.system).mediator
  // subscribe to the topic named "content"
  mediator ! Subscribe("content", self)

  var controller: ViewPagerController = _

  var UIController: UserController = _

  def receive = {

    case SubscribeAck(Subscribe("content", None, `self`)) ⇒
      log.info("subscribing {}", self)

    case getController(controller, controllerUc) =>
      this.controller = controller
      UIController = controllerUc

    case Msg(name, text) =>
      controller.post(name,text)

  }
}

object Subscriber {

  case class getController(controller: ViewPagerController, controllerUc: UserController)


}