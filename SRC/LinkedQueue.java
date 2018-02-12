import java.util.NoSuchElementException;

/**
 * Created by chad on 2/16/17.
 */
public class LinkedQueue<T> implements QueueADT<T>
{
    private LinearNode<T> head;
    private LinearNode<T> tail;
    private int count;

    public LinkedQueue()
    {
        head = null;
        tail = null;
        count = 0;
    }

    /**
     * 0(1)
     * @param element
     */
    @Override
    public void enqueue(T element)
    {
        LinearNode<T> newNode = new LinearNode<T>(element);
        if (isEmpty())
        {
            head = newNode;
        }
        else
            {
                tail.setNext(newNode);
            }
        tail = newNode;
        count++;
    }

    /**
     * O(1)
     * @return
     */
    @Override
    public T dequeue()
    {
        if (isEmpty())
        {
            throw new NoSuchElementException("Empty Queue");
        }

        T toReturn = head.getElement();

        if (head == tail)
        {
            tail = null;
        }

        head = head.getNext();

        count--;

        return toReturn;
    }

    /**
     * 0(1)
     * @return
     */
    @Override
    public T first()
    {
        if (isEmpty())
        {
            throw new NoSuchElementException("Empty Queue");
        }
        return head.getElement();
    }

    /**
     * 0(1)
     * @return
     */
    @Override
    public int size()
    {
        return count;
    }

    /**
     * 0(1)
     * @return
     */
    @Override
    public boolean isEmpty()
    {
        return count == 0;
    }

    /**
     * 0(n)
     * @return
     */
    public String toString()
    {
        LinearNode<T> current = head;
        String str = "";
        while (current != null)
        {
            str = str + " " + current.getElement();
            current = current.getNext();
        }
        return str;
    }
}
