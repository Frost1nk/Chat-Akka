import akka.actor.Actor
import akka.cluster.pubsub.DistributedPubSub

class Sender extends Actor {
  import akka.cluster.pubsub.DistributedPubSubMediator.Send
  // activate the extension
  val mediator = DistributedPubSub(context.system).mediator

  def receive = {
    case in: String ⇒
      val out = in.toUpperCase
      mediator ! Send(path = "/user/Manager/", msg = out, localAffinity = true)
  }
}