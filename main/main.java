package lk.ijse.fx.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lk.ijse.fx.util.ManageUser;
import lk.ijse.fx.util.User;
import java.io.IOException;

public class main extends Application {

    static {
        ManageUser.userDatabase.add(new User("System","system","system"));
        ManageUser.userDatabase.add(new User("Admin","admin","admin"));
        ManageUser.userDatabase.add(new User("User","user","user"));
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/lk/ijse/fx/view/LoginPage.fxml"));
        Scene mainScene = new Scene(root);
        primaryStage.setScene(mainScene);
        primaryStage.show();
        primaryStage.setTitle("LogIn Page");
    }
}
