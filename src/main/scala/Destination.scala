import akka.actor.{Actor, ActorLogging}
import akka.cluster.pubsub.DistributedPubSub

class Destination extends Actor with ActorLogging {
  import akka.cluster.pubsub.DistributedPubSubMediator.Put
  val mediator = DistributedPubSub(context.system).mediator
  // register to the path
  mediator ! Put(self)

  def receive = {
    case s: String ⇒
      log.info("Got {}", s)
  }
}
