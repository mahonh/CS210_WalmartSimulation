
public class Cashier ///Class for Cashier Object
{
    private LinkedQueue<Customer> line;

    private int column;

    private int[] itemCount;

    private int maxItems;
    private int itemsInLine;
    private int numCustomers;
    private int totalCustomers;
    private long totalWorkTime;
    private long totalWorkTimeInterval;
    private long totalWait;
    private long finishCheckout;
    private long checkoutWait;
    private long time = 1000;
    private boolean buffered = true;

    public Cashier(int speed, int column) ///Constructor for Cashier Object, Buffered Simulation
    {
        time = time / speed;
        itemsInLine = 0;
        maxItems = 0;
        numCustomers = 0;
        this.column = column;
        itemCount = new int[10];
        line = new LinkedQueue<Customer>();
    }

    public Cashier(int speed, int column, boolean buffered) ///Constructor Cashier, Unbuffered Simulation. A queue is created, but customer count is set to 9, thus giving the appearance to the customers that they queue is full, or there is not one at all
    {
        this.buffered = buffered;
        time = time / speed;
        itemsInLine = 0;
        maxItems = 0;
        numCustomers = 9;
        this.column = column;
        itemCount = new int[10];
        line = new LinkedQueue<Customer>();
    }

    public boolean isEmpty() ///Checks to see if the cashier's queue is empty - Performance O(1)
    {
        return line.isEmpty();
    }

    public void addCustomer(Customer customer) ///Adds a customer to the line - Performance O(1)
    {
        if (line.isEmpty()) //checks to see if the line is empty, if it is the customer's checkout time is calculated
        {
            checkoutWait = customer.getItems() * time;
            finishCheckout = System.currentTimeMillis() + checkoutWait;
            totalWorkTimeStart();
        }

        if (!line.isEmpty()) //if the line is not empty then the customer's wait time is started
            customer.setWaitingTime();

        line.enqueue(customer); //customer added to line

        numCustomers++; //current customers
        totalCustomers++; //total customers that have ever been to the line

        itemCount[numCustomers - 1] = customer.getItems(); //array keeps track of each customers item count

        if (buffered)
            SimulationGUI.updateCustomerVisibility(numCustomers, column, true); //sets the visibility true in SimulationGUI for the customer's position (buffered sim)
        else
            SimulationGUI.updateCustomerVisibility(1, column, true); //sets the visibility true in SimulationGUI for the customer's position (unbuffered)

        if (customer.getItems() > maxItems) //updates the max items, so the cashier knows what the max count is
            maxItems = customer.getItems();

        itemsInLine = itemsInLine + customer.getItems(); //updates the total number of items in the line
    }

    public int getMaxItems() ///Returns the current number of max items in the line - Performance O(1)
    {
        return maxItems;
    }

    public int getNumCustomers() ///Returns the number of customers in the cashier - Performance O(1)
    {
        return numCustomers;
    }

    public int getItemsInLine() ///Returns the total number of items in the cashier - Performance O(1)
    {
        return itemsInLine;
    }

    public void totalWorkTimeStart() ///Starts the calculation for the work time of the cashier - Performance O(1)
    {
        totalWorkTimeInterval = System.currentTimeMillis();
    }

    public void totalWorkTimeEnd() ///Ends the time and calculates the work time for the cashier - Performance O(1)
    {
        long endTime = System.currentTimeMillis() - totalWorkTimeInterval;
        totalWorkTime = totalWorkTime + endTime;
    }

    public void averageWaitingTime() ///Retrieves the waiting time from the customer - Performance O(1)
    {
        totalWait = totalWait + line.first().getWaitingTime();
    }

    public void reportTotalWorkTime() ///Reports the total work time to the Reporting class, called at simulation end - Performance O(1)
    {
        Reporting.updateTotalWorkTime(totalWorkTime);
    }

    public void reportAverageWaitingTime() ///Reports the average waiting time for the customer to the Reporting class, called at simulation end - Performance O(1)
    {
        if (totalCustomers != 0)
        {
        long result = totalWait / totalCustomers;
        Reporting.updateAverageWaitingTimes(result);
        }
    }

    private void customerTimeUpdate() ///Calculates the new checkout time for the next customer in line, called by checkOut - Performance O(1)
    {
        finishCheckout = 0;
        checkoutWait = 0;
        if (!line.isEmpty()) //checks to see if there is another customer in line
        {
            checkoutWait = line.first().getItems() * time;
            finishCheckout = System.currentTimeMillis() + checkoutWait;
        }
    }

    private void repositionItemCount() ///When a customer is removed from the cashier, the item counts for the remaining customers is updated to reflect the change, only ran in buffered sim - Performance O(n)
    {
        if (buffered)
        {
            itemCount[numCustomers - 1] = 0;

            int lastPosition = 0;

            for (int i = 0; i < itemCount.length - 1; i++) {
                itemCount[i + 1] = itemCount[i];
                lastPosition = i;
            }

            itemCount[lastPosition] = 0;
        }
    }

    private void updateMaxItems() ///When a customer is removed, the max item is checked to see if it needs to be updated, only ran in buffered sim - Performance O(n)
    {
        if (buffered)
        {
            maxItems = 0;

            for (int i = 0; i < itemCount.length - 1; i++) {
                if (itemCount[i] > maxItems)
                    maxItems = itemCount[i];
            }
        }
    }

    public void checkOut() ///Checks out a customer - Performance O(1)
    {
        if (!line.isEmpty())
        {
            if (System.currentTimeMillis() - (finishCheckout + Dispatch.simulationPauseTime) >= 0) //checks to see if it is time for the customer to check out
            {
                itemsInLine = itemsInLine - line.first().getItems(); //removes the first customers item count from the cashier total

                Reporting.updateTotalCustomersCheckOut(); //reports to the Reporting Class that a customer checked out
                Reporting.updateTotalItemsPurchased(line.first().getItems()); //reports to the Reporting class the total items purchased by this customer

                averageWaitingTime(); //updates the customer's average waiting time

                line.dequeue(); //removes the customer

                repositionItemCount(); //repositions the item counts to reflect a customer leaving
                updateMaxItems(); //checks to see if there is a new max item to be updated, since the customer who left may have had the max items

                if (line.isEmpty()) //if there are no more customers then the time is updated to show the cashier is not currently working
                    totalWorkTimeEnd();

                if (buffered)
                    SimulationGUI.updateCustomerVisibility(numCustomers, column, false); //updates the visibility of the customer in the SimulationGUI (buffered sim)
                else
                    SimulationGUI.updateCustomerVisibility(1, column, false); //updates the visibility of the customer in the SimulationGUI (unbuffered)

                numCustomers--; //updates the customer count
                customerTimeUpdate(); //updates the checkout time for the next customer in line
            }
        }
    }
}
