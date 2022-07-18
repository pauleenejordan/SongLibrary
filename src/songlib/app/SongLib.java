package songlib.app;

import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;
import songlib.view.ListController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;


public class SongLib extends Application {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws IOException {
		
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/songlib/view/mySongLayout.fxml"));
		//AnchorPane root = (AnchorPane)loader.load();
		VBox root = (VBox)loader.load();
		ListController listController = loader.getController();
		listController.start(primaryStage);
		Scene scene = new Scene(root, 650, 450);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
		
	}

}
