import Sender.{Message_To, Private_Message}
import akka.actor.{Actor, ActorLogging}
import akka.cluster.pubsub.DistributedPubSub

class Sender(name:String) extends Actor with ActorLogging{

  import akka.cluster.pubsub.DistributedPubSubMediator.Send

  // activate the extension
  val mediator = DistributedPubSub(context.system).mediator

  def receive = {
    case Private_Message(path, text) ⇒
      mediator ! Send(path = "/user/Manager/"+path, Message_To(name,text), localAffinity = true)

  }
}

object Sender {

  case class Private_Message(path: String, text: String)

  case class Message_To(name:String,msg:String)

}