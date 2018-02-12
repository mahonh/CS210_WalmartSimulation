import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class SimulationGUI ///Creates the Main Simulation GUI and Reporting GUI
{
    private AnimationTimer simComplete;

    private Stage thirdStage;

    private GridPane gridPane = new GridPane();
    private HBox hbox = new HBox();
    private BorderPane border = new BorderPane();

    private String status;
    private int cashiers;
    private Dispatch dispatch;

    private Label statusLabel;

    private static ImageView[][] customerVisibility;

    public SimulationGUI(int cashiers, Dispatch dispatch) ///Constructor for SimulationGUI
    {
        this.cashiers = cashiers;
        this.dispatch = dispatch;
        status = "RUNNING";

        customerVisibility = new ImageView[11][cashiers];

        newSimWindow(cashiers);
    }

    private void newSimWindow(int cashiers) ///Creates the Main Simulation Window - Performance O(1)
    {
        int width;

        HBox hbox = addHBox();
        border.setBottom(hbox);

        border.setCenter(addGridPane(cashiers));

        if (cashiers < 15)
            width = 700;
        else
            width = cashiers * 50;

        Scene scene = new Scene(border, width, 550);
        Stage secondaryStage = new Stage();
        secondaryStage.setTitle("Walmart Simulation");
        secondaryStage.setScene(scene);
        secondaryStage.show();

        try {
            createReportingWindow();
        } catch (Exception e) {}
    }

    private HBox addHBox() ///Creates an HBox to hold buttons and their actions - Performance O(1)
    {
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        hbox.setStyle("-fx-background-color: #336699;");

        Button buttonResume = new Button("Resume");
        buttonResume.setPrefSize(100, 20);

        Button buttonPause = new Button("Pause");
        buttonPause.setPrefSize(100, 20);

        Button buttonExit = new Button("Exit");
        buttonExit.setPrefSize(100, 20);

        Button buttonReport = new Button("Report");
        buttonReport.setPrefSize(100, 20);

        statusLabel = new Label("     Status:   " + status);
        statusLabel.setPrefSize(150, 20);

        buttonExit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert exit = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to exit?", ButtonType.YES, ButtonType.NO);
                exit.showAndWait();

                if (exit.getResult() == ButtonType.YES)
                    System.exit(0);
            }
        });

        buttonResume.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dispatch.resumeSimulation();
                status = "RUNNING";
                statusLabel.setText("     Status:   " + status);
            }
        });

        buttonPause.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dispatch.pauseSimulation();
                status = "PAUSED";
                statusLabel.setText("     Status:   " + status);
            }
        });

        buttonReport.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showReportingWindow();
            }
        });

        hbox.getChildren().addAll(buttonPause, buttonResume, buttonReport, buttonExit, statusLabel);

        return hbox;
    }

    private GridPane addGridPane(int cashiers) ///Creates a GridPane which holds all of the customers during simulation play. Expands to the needed size - Performance O(n^2)
    {
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.setPadding(new Insets(5,5,5,5));

        int column = 0;

        for (int i = 0; i < cashiers; i++) //creates all of the cashier lines
        {
            Rectangle rectangle = new Rectangle(40, 440);
            rectangle.setFill(Color.DARKGRAY);
            gridPane.add(rectangle, column, 0, 1, 11);
            column++;
        }

        for (int p = 0; p < cashiers; p++) //creates all of the images and assigns locations on the gridpane to them
        {
            int row = 1;

            for (int k = 0; k < 10; k++)
            {
                Image customer = new Image("customer.png");
                ImageView imv = new ImageView((customer));

                customerVisibility[row][p] = imv;

                imv.setFitHeight(40);
                imv.setFitWidth(40);

                imv.setVisible(false);

                gridPane.add(imv, p, row);
                row++;
            }
        }
        return gridPane;
    }

    private void createReportingWindow() throws Exception ///Creates the Reporting Window - Performance O(1)
    {
        Parent root = FXMLLoader.load(getClass().getResource("Walmart Report.fxml")); //loads from FXML file

        thirdStage = new Stage();

        Scene scene = new Scene(root);

        thirdStage.setTitle("Reporting");

        thirdStage.setScene(scene);
    }

    public void showReportingWindow() ///Shows the Reporting Window - Performance O(1)
    {
        thirdStage.show();
        if (Dispatch.simComplete)
            simComplete.stop();
    }

    public static void updateCustomerVisibility(int row, int column, boolean condition) ///Updates the customer images to visible when they are checking out and not visible when they are not - Performance O(1)
    {
        customerVisibility[row][column].setVisible(condition);
    }

    public void simComplete() ///Creates an AnimationTimer which waits for the simulation to complete before updating this status as such
    {
        simComplete = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (Dispatch.simComplete)
                {
                    status = "DONE!";
                    statusLabel.setText("     Status:   " + status);
                    showReportingWindow();
                }
            }
        };
        simComplete.start();
    }
}
