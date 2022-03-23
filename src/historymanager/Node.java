package historymanager;

public class Node<Task> {

    private Task task;
    private Node<Task> next;
    private Node<Task> prev;

    public Node(Node<Task> prev, Task data, Node<Task> next) {
        this.task = data;
        this.next = next;
        this.prev = prev;
    }


    public Task getTask() {
        return task;
    }

    public Node<Task> getNext() {
        return next;
    }

    public Node<Task> getPrev() {
        return prev;
    }

    public void setData(Task data) {
        this.task = data;
    }

    public void setNext(Node<Task> next) {
        this.next = next;
    }

    public void setPrev(Node<Task> prev) {
        this.prev = prev;
    }
}
