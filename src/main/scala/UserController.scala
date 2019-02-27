import akka.actor.ActorRef
import javafx.application.Platform
import javafx.beans.value.ObservableValue
import javafx.collections.{FXCollections, ObservableList}
import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.{Alert, ListView, Tab, TabPane}
import javafx.scene.layout.VBox

class UserController {
  var actor: ActorRef = _

  @FXML
  var tabPane: TabPane = _

  var control: ViewPagerController = _

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
      if (list.isEmpty) {} else {
        addTabs(listUsers.getSelectionModel.getSelectedItem)
      }
    })
  }


  def addTabs(name: String): Unit = {
    var resource = UserUI.getClass.getResource("viewPager.fxml")
    var loader = new FXMLLoader(resource)
    var root: VBox = loader.load()
    var tab: Tab = new Tab()
    var controller: ViewPagerController = loader.getController[ViewPagerController]
    tab.setText(name)
    tab.setClosable(true)
    tab.setContent(root)
    tabPane.getTabs.add(tab)
  }

  def addUser(name: String): Unit = {
    Platform.runLater(() => {
      list.add(name)
    })
  }
}
