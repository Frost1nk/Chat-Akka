import Publisher.Msg
import Subscriber.GetController
import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.cluster.pubsub.DistributedPubSub


class Subscriber extends Actor with ActorLogging {

  import akka.cluster.pubsub.DistributedPubSubMediator.{Subscribe, SubscribeAck}

  val mediator: ActorRef = DistributedPubSub(context.system).mediator
  // subscribe to the topic named "content"
  mediator ! Subscribe("content", self)

  var controller: ViewPagerController = _

  def receive: PartialFunction[Any, Unit] = {

    case SubscribeAck(Subscribe("content", None, `self`)) ⇒
      log.info("subscribing {}", self)

    case GetController(controller) =>
      this.controller = controller


    case Msg(name, text) =>
      controller.Post(name,text)

  }
}

object Subscriber {

  case class GetController(controller: ViewPagerController)


}