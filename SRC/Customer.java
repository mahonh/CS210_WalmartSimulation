import java.util.ArrayList;
import java.util.Random;

public class Customer ///Class for Customer Object
{
    private ArrayList<Cashier> cashiersInStore;
    private Random itemGen;
    private int items;
    private long departureTime;
    private long waitingTime;

    public Customer(int maxItems, int minItems, long departureTime, ArrayList<Cashier> cashiersInStore) ///Constructor for Customer Object
    {
        this.departureTime = departureTime;
        itemGen = new Random();
        items = itemGen.nextInt(maxItems - minItems + 1) + minItems; //Creates a random number of items for the customer
        this.cashiersInStore = cashiersInStore;
    }

    public boolean timeToCheckOut() ///Checks to see if it is time for the customer to check out, called by Dispatch. Calls findCashier if it is time - Performance O(1)
    {
        if ((System.currentTimeMillis() - (departureTime + Dispatch.simulationPauseTime)) >= 0)
        {
            findCashier();
            return true;
        }
        return false;
    }

    private void findCashier() ///Logic for the customer to find an appropriate cashier - Performance O(n)
    {

        int fewestItems = cashiersInStore.get(0).getItemsInLine();
        int fewestCustomers = cashiersInStore.get(0).getNumCustomers();
        int index = -1;
        int indexFewestCust = 0;
        int indexFewestItem = 0;
        int cashiersGreaterThan10 = 0;
        boolean greaterThan20Items = false;

        for (Cashier cashier : cashiersInStore) //goes through each cashier
        {
            index++;

            if (cashier.isEmpty()) //if a cashier is empty, the customer picks it first
            {
                cashier.addCustomer(this);
                return;
            }

            if (cashier.getNumCustomers() < fewestCustomers && cashier.getNumCustomers() < 10) //updates with the cashier of the lowest customers which is not above 10
            {
                fewestCustomers = cashier.getNumCustomers();
                indexFewestCust = index;
            }

            if (cashier.getMaxItems() < fewestItems && cashier.getNumCustomers() < 10) //updates with the cashier of the lowest items and less than 10 customers
            {
                fewestItems = cashier.getItemsInLine();
                indexFewestItem = index;
            }

            if (cashier.getNumCustomers() >= 10) //updates the count of how many cashiers have 10 or more customers
                cashiersGreaterThan10++;

            if (cashier.getMaxItems() > 20) //updates the boolean if a single cashier has a customer with more than 20 items
                greaterThan20Items = true;
        }

        if (cashiersGreaterThan10 == cashiersInStore.size()) //if all of the cashiers have 10 or more customers, the current customer leaves
        {
            Reporting.updateCustomersLeftUnhappy();
            Reporting.updateItemsAbandoned(items);
            return;
        }

        if (greaterThan20Items) //if a single customer has more than 20 items, the current customer picks the cashier with fewest items and less than 10 customers
        {
            cashiersInStore.get(indexFewestItem).addCustomer(this);
        } else {
            cashiersInStore.get(indexFewestCust).addCustomer(this); //otherwise the customer picks the cashier with fewest customers that's also less than 10
        }

    }

    public void setWaitingTime() ///Sets start of waiting time when a customer goes to the cashier, called by Cashier - Performance O(1)
    {
        waitingTime = System.currentTimeMillis();
    }

    public long getWaitingTime() ///Calculates how long the customer has been waiting, called by Cashier - Performance O(1)
    {
        if (waitingTime != 0)
        {
            long result = System.currentTimeMillis() - waitingTime;
            return result;
        } else
            return 0;
    }

    public int getItems() ///Returns the amount of items the customer has - Performance O(1)
    {
        return items;
    }
}
