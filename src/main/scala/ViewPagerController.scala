import Publisher.Message
import Sender.Private_Message
import akka.actor.ActorRef
import javafx.application.Platform
import javafx.collections.{FXCollections, ObservableList}
import javafx.fxml.FXML
import javafx.scene.control.{Button, ListView, SelectionMode, TextArea}


class ViewPagerController {
  var publicActor, privateActor: ActorRef = _


  var message_control: ViewPagerController = _

  var userController: UserController = _

  var path, name: String = _

  var check_Status: Boolean = false

  @FXML
  var listMessages: ListView[String] = _
  var list: ObservableList[String] = FXCollections.observableArrayList()

  @FXML
  var btnSend: Button = _

  @FXML
  var textArea: TextArea = _

  def initialize(): Unit = {
    btnSend.setOnAction(_ => {
      OnClickSend()
    })
    listMessages.setItems(list)
    listMessages.getSelectionModel.setSelectionMode(SelectionMode.SINGLE)
  }

  def OnClickSend(): Unit = {
    val text: String = textArea.getText()
    if (text.nonEmpty && !check_Status) {
      publicActor ! Message(text)
    } else if (text.nonEmpty && check_Status) {
      privateActor ! Private_Message(path, text)
      list.add(name + " : " + text)
    }
    textArea.clear()
  }


  def Post(name: String, text: String): Unit = {
    Platform.runLater(() => {
      list.add(name + ": " + text)
    })
  }

}
