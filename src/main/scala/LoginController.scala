import Listener.{Join, getUController, login}
import akka.actor.{ActorRef, ActorSystem, Props, RootActorPath}
import com.typesafe.config.{Config, ConfigFactory}
import io.aeron.Aeron.Configuration
import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.{Parent, Scene}
import javafx.scene.control.Alert.AlertType
import javafx.scene.control._
import javafx.scene.input.MouseEvent
import javafx.stage.Stage


class LoginController {

  var stage: Stage = _

  var controller: LoginController = _

  @FXML
  var loginBtn: Button = _

  @FXML
  var fieldSeedIP, fieldIP, fieldName: TextField = _

  def initialize() {
    loginBtn.setOnMouseClicked(onClickLogin)
  }


  def onClickLogin(mouseEvent: MouseEvent) = {
    val name: String = fieldName.getText()
    val seed: String = fieldSeedIP.getText()
    val ip: String = fieldIP.getText()
    if (name == "" || seed == "" || ip == "") {
      val alert: Alert = new Alert(AlertType.INFORMATION)
      alert.setTitle("Ошибка")
      alert.setHeaderText("Пустые поля")
      alert.setContentText("Заполните пустые поля")
      alert.showAndWait()
    } else {
      val hostname = ip.substring(0, 9)
      val port = ip.substring(10).toInt
      val customConf = ConfigFactory.parseString(
        s"""akka {
               actor {
                 provider = "akka.cluster.ClusterActorRefProvider"
               }
             remote {
               log-remote-lifecycle-events = off
               netty.tcp {
                 hostname = $hostname
                 port = $port
               }
             }
             cluster {
               akka.cluster.log-info = off
               seed-nodes = ["akka.tcp://chat@127.0.0.1:2551"]
               auto-down-unreachable-after = 5s
             }
           }""")
      val system = ActorSystem("chat", ConfigFactory.load(customConf))
      val listener = system.actorOf(Props(classOf[Listener], name), "Manager")
      listener ! Join(seed, name, ip)
      listener ! login(controller, listener)
      LoginController.chat(stage, listener,name,ip,system)
    }
  }
}

object LoginController {
  def chat(stage: Stage, actor: ActorRef,name:String,ip:String,system: ActorSystem) = {
    val resource = getClass.getResource("main.fxml")
    val loader = new FXMLLoader(resource)
    val root = loader.load[Parent]
    val controller: UserController = loader.getController[UserController]
    actor ! getUController(controller)
    stage.setTitle(s"Chat -  $name : ($ip)")
    stage.setScene(new Scene(root, 773, 283))
    stage.setOnCloseRequest(event => {
      stage.close()
      system.terminate()
    })
  }
}
