import Publisher.Message
import Sender.Private_Message
import akka.actor.ActorRef
import javafx.application.Platform
import javafx.collections.{FXCollections, ObservableList}
import javafx.fxml.FXML
import javafx.scene.control.{Button, ListView, SelectionMode, TextArea}
import javafx.scene.input.{MouseEvent}
import scala.collection.JavaConverters._

class ViewPagerController {
  var publicActor: ActorRef = _
  var privateActor: ActorRef = _

  var userController:UserController = _

  var path:String = _

  var check_Status: Boolean = false

  @FXML
  var listMessages: ListView[String] = _
  var list: ObservableList[String] = FXCollections.observableArrayList()

  @FXML
  var btnSend: Button = _

  @FXML
  var textArea: TextArea = _

  def initialize(): Unit = {
    btnSend.setOnMouseClicked(onClickSend)
    listMessages.setItems(list)
    listMessages.getSelectionModel.setSelectionMode(SelectionMode.SINGLE)
  }

  def onClickSend(mouseEvent: MouseEvent): Unit = {
    val text: String = textArea.getText()
    if (text.nonEmpty && check_Status == false) {
      publicActor ! Message(text)
    } else if (text.nonEmpty && check_Status == true) {
      privateActor ! Private_Message(path,text)
      list.add(privateActor.path.name+" : "+text)
    }
    textArea.clear()
  }


  def post(name: String, text: String): Unit = {
    Platform.runLater(() => {
      list.add(name + ":" + text)
    })
  }

}
