import java.time.LocalDate

import Destination.{get_Controllers, vpg_control}
import Listener.get_controller_Tab
import akka.actor.ActorRef
import javafx.application.Platform
import javafx.beans.value.ObservableValue
import javafx.collections.{FXCollections,  ObservableList}
import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.control.{ListView, Tab, TabPane}
import javafx.scene.layout.VBox

import scala.collection.JavaConverters._

class UserController {
  var actor: ActorRef = _

  var private_actor: ActorRef = _

  @FXML
  var tabPane: TabPane = _

  var control: ViewPagerController = _

  var tab_control: ViewPagerController = _

  @FXML
  var listUsers: ListView[String] = _
  var list: ObservableList[String] = FXCollections.observableArrayList()

  @FXML
  def initialize(): Unit = {
    var resource = UserUI.getClass.getResource("viewPager.fxml")
    var loader = new FXMLLoader(resource)
    var root: VBox = loader.load()
    var tab: Tab = new Tab()
    tab.setText("Общий")
    tab.setClosable(false)
    tab.setContent(root)
    var controller: ViewPagerController = loader.getController[ViewPagerController]
    control = controller
    tabPane.getTabs.add(tab)
    listUsers.setItems(list)
    val change: javafx.beans.value.ChangeListener[String] = (observable: ObservableValue[_ <: String], oldValue: String, newValue: String) => {
      addTabs(newValue)
    }

    listUsers.setOnMouseClicked(event => {
      val selectedItem = listUsers.getSelectionModel.getSelectedItem
      if (selectedItem == null) {
        event.consume()
      } else {
        tabPane.getTabs.asScala.find(_.getText == selectedItem)
          .fold(addTabs(selectedItem))(tabPane.getSelectionModel.select)
      }

    })
  }

  def deleteTAb(name: String) = {
    Platform.runLater(() => {
      tabPane.getTabs.removeIf(_.getText == name)
      listUsers.getItems.removeIf(_ == name)
    })

  }

  def `(_/(-_-)\_)рисуйСообщеньку`(name: String, сообщенька: String) = {
    val messageListView = tabPane.getTabs.get(1).getContent.asInstanceOf[ListView[String]]
    messageListView.getItems.add(s"$name: $сообщенька")
  }

  def addTabs(name: String): Unit = {
    Platform.runLater(() => {
      var resource = UserUI.getClass.getResource("viewPager.fxml")
      var loader = new FXMLLoader(resource)
      var root: VBox = loader.load()
      var tab: Tab = new Tab()
      var controller: ViewPagerController = loader.getController[ViewPagerController]
      actor ! get_controller_Tab(controller)
      private_actor ! vpg_control(controller)
      controller.path = name
      tab.setText(name)
      tab.setClosable(true)
      tab.setContent(new ListView())
//      tab.setContent(root)

      tabPane.getTabs.add(tab)
    })
  }


  def addUser(name: String): Unit = {
    Platform.runLater(() => {
      list.add(name)
    })
  }

}
