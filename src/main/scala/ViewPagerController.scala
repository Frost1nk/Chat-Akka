import Publisher.Message
import Sender.Private_Message
import akka.actor.ActorRef
import javafx.application.Platform
import javafx.collections.{FXCollections, ObservableList}
import javafx.event.{ActionEvent, EventHandler}
import javafx.fxml.FXML
import javafx.scene.control.{Button, ListView, SelectionMode, TextArea}
import javafx.scene.input.MouseEvent


class ViewPagerController {
  var publicActor: ActorRef = _
  var privateActor: ActorRef = _

  var message_control:ViewPagerController = _

  var userController:UserController = _

  var path,name:String = _

  var check_Status: Boolean = false

  @FXML
  var listMessages: ListView[String] = _
  var list: ObservableList[String] = FXCollections.observableArrayList()

  @FXML
  var btnSend: Button = _

  @FXML
  var textArea: TextArea = _

  def initialize(): Unit = {
    btnSend.setOnAction(event=>{onClickSend()})
    listMessages.setItems(list)
    listMessages.getSelectionModel.setSelectionMode(SelectionMode.SINGLE)
  }

  def onClickSend(): Unit = {
    val text: String = textArea.getText()
    if (text.nonEmpty && check_Status == false) {
      publicActor ! Message(text)
    } else if (text.nonEmpty && check_Status == true) {
      privateActor ! Private_Message(path,text)
      list.add(name+" : "+text)
    }
    textArea.clear()
  }


  def post(name: String, text: String): Unit = {
    Platform.runLater(() => {
      list.add(name + ": " + text)
    })
  }

}
