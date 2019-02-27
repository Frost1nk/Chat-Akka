import Subscriber.{getController}
import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.cluster.pubsub.DistributedPubSub


class Subscriber extends Actor with ActorLogging {

  import akka.cluster.pubsub.DistributedPubSubMediator.{Subscribe, SubscribeAck}

  val mediator = DistributedPubSub(context.system).mediator
  // subscribe to the topic named "content"
  mediator ! Subscribe("content", self)

  var subscribers = Map.empty[String, Set[ActorRef]].withDefaultValue(Set.empty)

  var controller: ViewPagerController = _

  var UIController: UserController = _

  def receive = {
    case s: String ⇒
      log.info("Got {}", s)
      controller.post(s)

    case SubscribeAck(Subscribe("content", None, `self`)) ⇒
      //      subscribers += "content" -> (subscribers("content") + self)
      log.info("subscribing {}", self)
    //      subscribers.foreach(e => println(e))

    case getController(controller, controllerUc) =>
      this.controller = controller
      UIController = controllerUc


  }
}

object Subscriber {

  case class getController(controller: ViewPagerController, controllerUc: UserController)

}