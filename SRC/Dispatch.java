import java.util.*;

public class Dispatch ///Dispatch in the primary class in the simulation. It controls the Simulation Loop, dispatches customers and assigns their times, and checks conditions to see when the simulation is complete.
{
    private Timer dispatch; //main timer object
    private TimerTask simLoop; //main timer task, the simulation loop
    private boolean buffered;
    private ArrayList<Customer> customersInStore; //keeps track of customers still in the store
    private ArrayList<Cashier> cashiersInStore; //keeps track of all the cashiers

    private int minItems;
    private int maxItems;
    private int minCustomers;
    private int maxCustomers;
    private int numberCashiers;

    private Random customerGen;
    private Random departureTimeGen;
    private int customerDispatchCount; //keeps track of how many "groups" of customers have been dispatched
    private SimulationGUI mainWindow; //main simulation GUI
    private long simulationPauseStart; //if the simulation is paused, this is the time the "pause" started
    private boolean simulationPauseStatus; //condition whether or not simulation is paused
    public static boolean simComplete;
    public static long simulationPauseTime; //the amount of time the simulation is paused
    private long customerDispatchTime; //time in the future when a new "group" of customers will be dispatched
    private long simulationEndTime; //time in the future when the simulation is scheduled to end
    private long simulationTimeInterval = 3600000; //period of time for the simulation to run, adjusted according to simulation speed
    private int simulationSpeed; //speed for the simulation

    public Dispatch(int minItems, int maxItems, int minCustomers, int maxCustomers, int numberCashiers, int simSpeed, boolean buffered) ///Constructor for Dispatch
    {
        this.buffered = buffered;
        simulationSpeed = simSpeed;
        customersInStore = new ArrayList<>();
        cashiersInStore = new ArrayList<>();
        dispatch = new Timer();
        customerGen = new Random();
        departureTimeGen = new Random();
        customerDispatchCount = 0;
        simulationPauseTime = 0;
        simulationPauseStatus = false;
        simComplete = false;

        this.minItems = minItems;
        this.maxItems = maxItems;
        this.minCustomers = minCustomers;
        this.maxCustomers = maxCustomers;
        this.numberCashiers = numberCashiers;

        simulationTimeInterval = simulationTimeInterval / simulationSpeed; //updated period for the simulation run time, based on simulation speed
        long simulationStartTime = System.currentTimeMillis();
        simulationEndTime = simulationStartTime + simulationTimeInterval; //scheduled end time for the simulation

        simulationStart();
    }

    private void simulationStart() ///Creates all the cashier objects, customer objects, GUI, and starts the Simulation Loop
    {
        simulationSetup();
        customerSetup();
        guiSetup();
        simulationLoop();
        mainWindow.simComplete();
    }

    private void simulationSetup() ///Creates the cashier objects based on the user inputs - Performance O(n)
    {
        int column = 0; //references the column in the SimulationGUI, relates the cashier object to the GUI

        if (buffered) //if it is a buffered simulation
        {
            for (int i = 0; i < numberCashiers; i++) {
                Cashier cashier = new Cashier(simulationSpeed, column);
                cashiersInStore.add(cashier);
                column++;
            }
        } else { //if it is an unbuffered simulation
            for (int i = 0; i < numberCashiers; i++) {
                Cashier cashier = new Cashier(simulationSpeed, column, buffered);
                cashiersInStore.add(cashier);
                column++;
            }
        }

        Reporting.updateTotalCashiers(numberCashiers); //reports the number of cashiers to the Reporting Class
    }

    private void customerSetup() ///Creates a random number of customers based on the user's inputs - Performance O(n)
    {
        customerDispatchCount++; //increments the amount of times customerSetup has run

        long departureTimeInterval = simulationTimeInterval / 6;

        customerDispatchTime = System.currentTimeMillis() + departureTimeInterval; //the time set in the future for the next batch of customers to be released (run customerSetup again)

        int numberCustomers = customerGen.nextInt(maxCustomers - minCustomers + 1) + minCustomers;

        for (int j = 0; j < numberCustomers; j++) //loop to create customers
        {
            long departureTime = 0;
            int departureTimeMult = 0;

            departureTimeMult = departureTimeGen.nextInt(20 - 1) + 1; //random interval to give each customer a random time
            departureTime = departureTimeInterval / departureTimeMult; //gives a random time within the interval
            departureTime = departureTime + System.currentTimeMillis(); //sets the random time in the future
            Customer customer = new Customer(maxItems, minItems, departureTime, cashiersInStore); //creates the customer object
            customersInStore.add(customer); //customer objects are in an arrayList until they check out, these are the customers in the store
            Reporting.updateTotalCustomersInStoreAdd(); //reports to Reporting class that a new customer is in the store
        }
    }

    private void guiSetup() ///Creates the main GUI window  - Performance O(1)
    {
        mainWindow = new SimulationGUI(numberCashiers, this);
    }


    /*
        Main simulation loop. Controls when customers dispatch to cashiers, when cashiers are complete with checking out customers
        end simulation calculations, and when simulation is complete. Runs as a timer task on a timer, which is a separate thread
        from the JavaFX thread. Loops through every 5ms to check customer and cashier conditions.
     */

    private void simulationLoop() ///Main simulation loop, controls times of customers, cashiers, and simulation end
    {
        simLoop = new TimerTask() { //new timer task on for timer on a new Timer Thread
            @Override
            public void run() {

                int totalItem = 0;

                if (customerDispatchCount <= 6 && System.currentTimeMillis() - customerDispatchTime >= 0) //checks to see if it is time to dispatch new customers
                    customerSetup();


                /*
                    Iterates through both cashier and customer arrayList to check their scheduled time for task against current time
                 */
                Iterator<Customer> customerIter = customersInStore.iterator();
                Iterator<Cashier> cashierIter = cashiersInStore.iterator();

                while (customerIter.hasNext())
                {
                    Customer element = customerIter.next();

                    if (element.timeToCheckOut()) //checks to see if it is time for the customer to check out
                    {
                        customerIter.remove(); //removes them from "store" arrayList if it is
                        Reporting.updateTotalCustomersInStoreSub(); //reports to Reporting class
                    }
                }

                while (cashierIter.hasNext())
                {
                    Cashier element = cashierIter.next();
                    element.checkOut(); //checks to see if the cashier is completed checking a customer out
                    totalItem = totalItem + element.getItemsInLine();
                }

                /*
                    End conditions to be checked. All customers must be out of the store, no customers checking out, and the designated
                    time up. TotalItem is used to determine if there are any customers in the cashiers
                 */

                if (System.currentTimeMillis() - simulationEndTime >= 0 && totalItem == 0 && customersInStore.isEmpty())
                {
                    for (Cashier cashier : cashiersInStore) { //runs through and does simulation end calculations for each cashier
                        cashier.reportAverageWaitingTime();
                        cashier.reportTotalWorkTime();
                    }

                    Reporting.calculateAverageWaitingTime(); //calculates the average waiting time for customers
                    Reporting.calculateTotalWorkTime(); //calculates the average working time for cashiers
                    dispatch.cancel();
                    simComplete = true;
                }

                if (simulationPauseStatus) //checks to see if the simulation has been paused, if so the current timer is cancelled.
                    dispatch.cancel();
            }
        };

        dispatch.scheduleAtFixedRate(simLoop, 0, 5);
    }

    public void pauseSimulation() ///Pauses the simulation by setting a boolean value, picked up by simulationLoop, and calculating the time between pause and resume  - Performance O(1)
    {
        if (!simulationPauseStatus)
        {
        simulationPauseStatus = true;
        simulationPauseStart = System.currentTimeMillis();
        }
    }

    public void resumeSimulation() ///Resumes the simulation by creating a new Timer Object, starting the time task (simulationLoop) and calculating the time between pause and resume  - Performance O(1)
    {
        if (simulationPauseStatus)
        {
        simulationPauseTime = System.currentTimeMillis() - simulationPauseStart;

        if (simulationSpeed == 2)
            simulationPauseTime = simulationPauseTime / 2;

        else if (simulationSpeed == 60)
            simulationPauseTime = simulationPauseTime / 60;

        dispatch = new Timer();
        simulationPauseStatus = false;
        simulationLoop();
        }
    }
}
