import Listener.{CloseCallback, Join, GetControllerUC, Login}
import akka.actor.{ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
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


  def onClickLogin(mouseEvent: MouseEvent): Unit = {
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
                 warn-about-java-serializer-usage = false
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
      listener ! Join(seed, name)
      listener ! Login(controller, listener)
      LoginController.Chat(stage, listener, name, ip, system)
    }
  }
}

object LoginController {
  def Chat(stage: Stage, actor: ActorRef, name: String, ip: String, system: ActorSystem): Unit = {
    val resource = getClass.getResource("main.fxml")
    val loader = new FXMLLoader(resource)
    val root = loader.load[Parent]
    val controller: UserController = loader.getController[UserController]
    actor ! GetControllerUC(controller)
    stage.setTitle(s"Chat -  $name : ($ip)")
    stage.setScene(new Scene(root, 600, 400))
    stage.setOnCloseRequest(_ => {
      actor ! CloseCallback
      stage.close()
      system.terminate()
    })
  }
}
