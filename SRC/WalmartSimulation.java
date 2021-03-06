import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class WalmartSimulation extends Application ///Creates intro window and starts simulation
{
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("Intro.fxml"));

        Scene scene = new Scene(root);

        primaryStage.setTitle("Walmart Simulation Intro");

        primaryStage.setScene(scene);

        primaryStage.show();
    }
}
