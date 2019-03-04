import Listener.GetcontrollerTab
import akka.actor.ActorRef
import javafx.application.Platform
import javafx.beans.value.ObservableValue
import javafx.collections.{FXCollections, ObservableList}
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

  var tabControl: ViewPagerController = _

  @FXML
  var listUsers: ListView[String] = _
  var list: ObservableList[String] = FXCollections.observableArrayList()

  @FXML
  def initialize(): Unit = {
    val resource = UserUI.getClass.getResource("viewPager.fxml")
    val loader = new FXMLLoader(resource)
    val root: VBox = loader.load()
    val tab: Tab = new Tab()
    tab.setText("Общий")
    tab.setClosable(false)
    tab.setContent(root)
    val controller: ViewPagerController = loader.getController[ViewPagerController]
    control = controller
    tabPane.getTabs.add(tab)
    listUsers.setItems(list)
    val change: javafx.beans.value.ChangeListener[String] = (observable: ObservableValue[_ <: String], oldValue: String, newValue: String) => {
      AddTabs(newValue)
    }

    listUsers.setOnMouseClicked(event => {
      val selectedItem = listUsers.getSelectionModel.getSelectedItem
      if (selectedItem == null) {
        event.consume()
      } else {
        tabPane.getTabs.asScala.find(_.getText == selectedItem)
          .fold(AddTabs(selectedItem))(tabPane.getSelectionModel.select)
      }

    })
  }

  def DeleteTAb(name: String): Unit = {
    Platform.runLater(() => {
      list.forEach(i => print(i + ", "))
      listUsers.getItems.forEach(i => print(i + ", "))

      tabPane.getTabs.removeIf(_.getText == name)
      list.removeIf(_ == name)
    })
  }


  def AddTabs(name: String): Unit = {
    Platform.runLater(() => {
      var resource = UserUI.getClass.getResource("viewPager.fxml")
      var loader = new FXMLLoader(resource)
      var root: VBox = loader.load()
      var tab: Tab = new Tab()
      var controller: ViewPagerController = loader.getController[ViewPagerController]
      actor ! GetcontrollerTab(controller)
      controller.path = name
      tab.setText(name)
      tab.setClosable(true)
      tab.setContent(root)
      tabPane.getTabs.add(tab)
    })
  }


  def addUser(name: String): Unit = {
    Platform.runLater(() => {
      list.add(name)
    })
  }

}
