import Publisher.{Message, Msg}
import akka.actor.Actor
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Publish

class Publisher(name:String) extends Actor {
  // activate the extension
  val mediator = DistributedPubSub(context.system).mediator

  def receive = {
    case Message(text) ⇒
      mediator ! Publish("content", Msg(name,text))
  }
}

object Publisher {

  case class Message(text: String)

  case class Msg(name:String,text:String)

}