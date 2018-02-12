import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class IntroController ///Controller class for the Intro/Setup window
{
    private Dispatch runSim;
    private String errorMessage;
    private int minItems;
    private int maxItems;
    private int minCust;
    private int maxCust;
    private int cashier;
    private int simSpeed;
    private boolean error;
    private boolean parseError;

    @FXML
    private TextField minItemsField;

    @FXML
    private TextField minCustomersField;

    @FXML
    private TextField numberOfCashiersField;

    @FXML
    private TextField maxItemsField;

    @FXML
    private RadioButton bufferedRadioButton;

    @FXML
    private RadioButton unbufferedRadioButton;

    @FXML
    private TextField maxCustomersField;

    @FXML
    private RadioButton speedTurtleRadioButton;

    @FXML
    private RadioButton speedRabbitRadioButton;

    @FXML
    private RadioButton speedCheetahRadioButton;

    @FXML
    private Button startButton;

    public void eventExitButton() ///Exit button, closes the program - Performance O(1)
    {
        System.exit(0);
    }

    public void eventStartButton() ///Start button, catches errors of user input and passes inputs to Dispatch - Performance O(1)
    {
        try {
            minItems = Integer.parseInt(minItemsField.getText());
            maxItems = Integer.parseInt(maxItemsField.getText());
            minCust = Integer.parseInt(minCustomersField.getText());
            maxCust = Integer.parseInt(maxCustomersField.getText());
            cashier = Integer.parseInt(numberOfCashiersField.getText());
        } catch (Exception e) {
            errorMessage = "All inputs must be integers and have values";
            parseError = true;
        }

        if (minItems <= 0 && !parseError)
        {
            errorMessage = "Min Items can not be 0 or less";
            error = true;
        }

        else if (maxItems <= 0 && !parseError)
        {
            errorMessage = "Max Items can not be 0 or less";
            error = true;
        }

        else if (maxItems < minItems && !parseError)
        {
            errorMessage = "Min Items can not be greater than Max Items";
            error = true;
        }

        else if (minCust <= 0 && !parseError)
        {
            errorMessage = "Min Customers can not be 0 or less";
            error = true;
        }

        else if (maxCust <= 0 && !parseError)
        {
            errorMessage = "Max Customers can not be 0 or less";
            error = true;
        }

        else if (maxCust < minCust && !parseError)
        {
            errorMessage = "Min Customers can not be greater than Max Customers";
            error = true;
        }

        else if (cashier <= 0 && !parseError)
        {
            errorMessage = "Cashier can not be 0 or less";
            error = true;
        }


        if (speedTurtleRadioButton.isSelected())
            simSpeed = 1;

        else if (speedRabbitRadioButton.isSelected())
            simSpeed = 2;

        else if (speedCheetahRadioButton.isSelected())
            simSpeed = 60;


        if (error || parseError)
        {
            Alert basicAlert = new Alert(Alert.AlertType.ERROR, errorMessage, ButtonType.CLOSE);
            basicAlert.showAndWait();
            parseError = false;
            error = false;
        }

        else if (unbufferedRadioButton.isSelected())
            runSim = new Dispatch(minItems, maxItems, minCust, maxCust, cashier, simSpeed, false);

        else if (bufferedRadioButton.isSelected())
            runSim = new Dispatch(minItems, maxItems, minCust, maxCust, cashier, simSpeed, true);

        Stage stage = (Stage) startButton.getScene().getWindow();
        stage.close();
    }
}
