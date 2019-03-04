import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.{Parent, Scene}
import javafx.stage.Stage

object UserUI {
  def main(args: Array[String]): Unit = {
    Application.launch(classOf[UserUI], args: _*)
  }


  class UserUI extends Application {
    override def start(primaryStage: Stage): Unit = {
      val resource = getClass.getResource("login.fxml")
      val loader = new FXMLLoader(resource)
      val root = loader.load[Parent]
      val controller: LoginController = loader.getController[LoginController]
      controller.controller=controller
      controller.stage = primaryStage
      primaryStage.setTitle("Login")
      primaryStage.setScene(new Scene(root, 411, 301))
      primaryStage.show()
      primaryStage.setOnCloseRequest{_ =>
        primaryStage.close()
      }
    }


  }


}
