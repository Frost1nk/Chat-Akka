import Destination.get_Controllers
import akka.actor.{Actor, ActorLogging}
import akka.cluster.pubsub.DistributedPubSub

class Destination extends Actor with ActorLogging {
  import akka.cluster.pubsub.DistributedPubSubMediator.Put
  val mediator = DistributedPubSub(context.system).mediator
  // register to the path
  mediator ! Put(self)

  var control:ViewPagerController = _



  def receive = {
    case s: String ⇒
      log.info("Got {}", s)
      control.post(sender().path.name,s)

    case get_Controllers(controller)=>
      control = controller

  }
}

object Destination{

  case class get_Controllers(viewPagerController: ViewPagerController)
}
