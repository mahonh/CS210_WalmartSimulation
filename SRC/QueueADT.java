/**
 * Created by chad on 2/16/17.
 */
public interface QueueADT<T>
{
    public void enqueue(T element); ///add element to rear of queue
    public T dequeue(); ///remove and return the first element
    public T first(); ///returns the first element in the queue, without removing
    public int size(); ///returns size of the queue
    public boolean isEmpty(); ///checks to see if the queue is empty
}
