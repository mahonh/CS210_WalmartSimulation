public class LinearNode<T> ///LinearNode class
{
    private T element;
    private LinearNode<T> next;

    public LinearNode() ///Creates a new, empty LinearNode
    {
        element = null;
        next = null;
    }

    public LinearNode(T element) ///O(1)
    {
        this.element = element;
        next = null;
    }

    public T getElement() ///O(1)
    {
        return element;
    }

    public void setElement(T element) ///O(1)
    {
        this.element = element;
    }

    public LinearNode<T> getNext() ///O(1)
    {
        return next;
    }

    public void setNext(LinearNode<T> node) ///O(1)
    {
        next = node;
    }

}
