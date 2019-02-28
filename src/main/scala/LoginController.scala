import Listener.{Join, Registration, getUController, login}
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.cluster.ClusterEvent.MemberUp
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
      val system = ActorSystem("chat")
      val listener = system.actorOf(Props(classOf[Listener], name),"Manager")
      listener ! Join(seed,name,ip)
      listener ! login(controller, listener)
      LoginController.chat(stage, listener)
    }
  }
}

object LoginController {
  def chat(stage: Stage, actor: ActorRef) = {
    val resource = getClass.getResource("main.fxml")
    val loader = new FXMLLoader(resource)
    val root = loader.load[Parent]
    val controller: UserController = loader.getController[UserController]
    actor ! getUController(controller)
    stage.setTitle("Chat")
    stage.setScene(new Scene(root, 773, 283))
  }
}
