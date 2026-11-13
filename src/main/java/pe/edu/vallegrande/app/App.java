package pe.edu.vallegrande.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage s) throws Exception {
        s.setScene(new Scene(new FXMLLoader(getClass().getResource("/pe/edu/vallegrande/app/view/producto-view.fxml")).load(), 860, 550));
        s.setTitle("Ferreteria - Valle Grande");
        s.show();
    }

    public static void main(String[] args) {
        launch();
    }
}