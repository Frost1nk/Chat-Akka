import Publisher.{Message, Msg}
import akka.actor.{Actor, ActorRef}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Publish

class Publisher(name: String) extends Actor {
  // activate the extension
  val mediator: ActorRef = DistributedPubSub(context.system).mediator

  def receive: PartialFunction[Any, Unit] = {
    case Message(text) ⇒
      mediator ! Publish("content", Msg(name, text))
  }
}

object Publisher {

  case class Message(text: String)

  case class Msg(name: String, text: String)

}