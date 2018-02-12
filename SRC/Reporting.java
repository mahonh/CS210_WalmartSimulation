import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class Reporting ///Reporting controller class. Also handles final calculations for stats
{
    private static int customersLeftUnhappy = 0;
    private static int totalCustomersInStore = 0;
    private static int abandonedItems = 0;
    private static int totalItemsPurchased = 0;
    private static int totalCustomersCheckOut = 0;
    private static int totalCashiers = 0;
    private static long averageWaitingTimes = 0;
    private static long sumTotalWorkTime = 0;
    private static long calculatedTotalWorkTime = 0;
    private static long calculatedAverageWaitTime = 0;
    private static String averageWait = "Data available at Simulation End";
    private static String totalWorkTime = "Data available at Simulation End";

    private AnimationTimer reportingUpdate;

    @FXML
    private Label totalCustomersCheckOutEDIT;

    @FXML
    private Label totalCustomersLeftEarlyEDIT;

    @FXML
    private Label totalItemsEDIT;

    @FXML
    private Label totalItemsAbandEDIT;

    @FXML
    private Label customersInStoreEDIT;

    @FXML
    private Label averageCustomerWaitEDIT;

    @FXML
    private Label averageCashierWorkEDIT;

    @FXML
    private Button buttonClose;


    public void initialize() ///Establishes the the animation timer to update the Reporting window and assigns variables to labels - Performance O(1)
    {
        reportingUpdate = new AnimationTimer() {
            @Override
            public void handle(long now) {
                totalCustomersLeftEarlyEDIT.setText(Integer.toString(customersLeftUnhappy));
                customersInStoreEDIT.setText(Integer.toString(totalCustomersInStore));
                totalItemsAbandEDIT.setText(Integer.toString(abandonedItems));
                totalItemsEDIT.setText(Integer.toString(totalItemsPurchased));
                totalCustomersCheckOutEDIT.setText(Integer.toString(totalCustomersCheckOut));
                averageCustomerWaitEDIT.setText(averageWait);
                averageCashierWorkEDIT.setText(totalWorkTime);
            }
        };
        reportingUpdate.start();
    }

    public void evenCloseButton()
    {
        Stage stage = (Stage) buttonClose.getScene().getWindow();
        stage.close();
    }

    public static void updateCustomersLeftUnhappy() ///Updates customers that left early - Performance O(1)
    {
        customersLeftUnhappy++;
    }

    public static void updateTotalCustomersInStoreAdd() ///Updates with total customers in store - Performance O(1)
    {
        totalCustomersInStore++;
    }

    public static void updateTotalCustomersInStoreSub() ///Updates when a customer is checking out and no longer in the store - Performance O(1)
    {
        totalCustomersInStore--;
    }

    public static void updateItemsAbandoned(int items) ///Updates when a customer leaves early and doesn't checkout - Performance O(1)
    {
        abandonedItems = abandonedItems + items;
    }

    public static void updateTotalItemsPurchased(int items) ///Updates with total items of customers who checked out - Performance O(1)
    {
        totalItemsPurchased = totalItemsPurchased + items;
    }

    public static void updateTotalCustomersCheckOut() ///Updates with total customers who check out - Performance O(1)
    {
        totalCustomersCheckOut++;
    }

    public static void updateAverageWaitingTimes(long time) ///Updates with a total of all average waiting times from each cashier - Performance O(1)
    {
        averageWaitingTimes = averageWaitingTimes + time;
    }

    public static void updateTotalCashiers(int cashiers) ///Total cashiers in store - Performance O(1)
    {
        totalCashiers = cashiers;
    }

    public static void updateTotalWorkTime(long time) ///Updates total time all cashiers have worked - Performance O(1)
    {
        sumTotalWorkTime = sumTotalWorkTime + time;
    }

    public static void calculateTotalWorkTime() ///Calculates the average work time for cashiers - Performance O(1)
    {
        calculatedTotalWorkTime = sumTotalWorkTime / totalCashiers;

        long second = 0;
        long minute = 0;
        long hour = 0;

        if (calculatedTotalWorkTime > 1000)
        {
            second = (calculatedTotalWorkTime / 1000) % 60;
            minute = (calculatedTotalWorkTime / (1000 * 60)) % 60;
            hour = (calculatedTotalWorkTime / (1000 * 60 * 60)) % 24;
        }


        else if (calculatedTotalWorkTime > 100)
        {
            second = (calculatedTotalWorkTime / 100) % 60;
            minute = (calculatedTotalWorkTime / (100 * 60)) % 60;
            hour = (calculatedTotalWorkTime / (100 * 60 * 60)) % 24;
        }


        else if (calculatedTotalWorkTime > 10)
        {
            second = (calculatedTotalWorkTime / 10) % 60;
            minute = (calculatedTotalWorkTime / (10 * 60)) % 60;
            hour = (calculatedTotalWorkTime / (10 * 60 * 60)) % 24;
        }


        totalWorkTime = String.format("%02d Hours :%02d Minutes :%02d Seconds :%d", hour, minute, second, calculatedTotalWorkTime);
    }

    public static void calculateAverageWaitingTime() ///Calculates the average wait time from the total averages - Performance O(1)
    {
        calculatedAverageWaitTime = averageWaitingTimes / totalCashiers;

        long second = 0;
        long minute = 0;
        long hour = 0;

        if (calculatedAverageWaitTime > 1000)
        {
            second = (calculatedTotalWorkTime / 1000) % 60;
            minute = (calculatedTotalWorkTime / (1000 * 60)) % 60;
            hour = (calculatedTotalWorkTime / (1000 * 60 * 60)) % 24;
        }

        else if (calculatedAverageWaitTime > 100)
        {
            second = (calculatedTotalWorkTime / 100) % 60;
            minute = (calculatedTotalWorkTime / (100 * 60)) % 60;
            hour = (calculatedTotalWorkTime / (100 * 60 * 60)) % 24;
        }

        else if (calculatedAverageWaitTime > 10)
        {
            second = (calculatedTotalWorkTime / 10) % 60;
            minute = (calculatedTotalWorkTime / (10 * 60)) % 60;
            hour = (calculatedTotalWorkTime / (10 * 60 * 60)) % 24;
        }

        averageWait = String.format("%02d Hours :%02d Minutes :%02d Seconds :%d", hour, minute, second, calculatedAverageWaitTime);
    }
}
