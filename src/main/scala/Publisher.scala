import Publisher.Message
import akka.actor.Actor
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Publish

class Publisher extends Actor {
  // activate the extension
  val mediator = DistributedPubSub(context.system).mediator

  def receive = {
    case Message(text) ⇒
      mediator ! Publish("content", text)
  }
}

object Publisher {

  case class Message(text: String)

}