package historymanager;

import tasktracker.tasks.Task;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    //private final LinkedList<Node> history;
    private final HashMap<Long,Node> nodeTable;
    private final static byte historySize = 10;
    private Node head;
    private Node tail;
    private int size = 0;

    public InMemoryHistoryManager() {
        //InMemoryHistoryManager history = new InMemoryHistoryManager();
        nodeTable = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        linkLast(task);
    }

    private void linkLast(Task task) {

        //поискать дубликат
        remove(task.getId());

        //добавить новую задачу в конец списка
        final Node oldTail = tail;
        final Node newNode = new Node<>(tail, task, null);
        tail = newNode;

        if (oldTail == null)
            head = newNode;
        else
            oldTail.setNext(newNode);

        size++;

        //записать соответствие в таблицу
        nodeTable.put(task.getId(), newNode);

        //если в списке более 10 записей, удалить первую (по условию ТЗ 3-го спринта)
        if (size > historySize) {
            removeNode(head);
        }
    }

    @Override
    public void remove (Long id) {
        if (nodeTable.containsKey(id)) {
            removeNode(nodeTable.get(id));
        }
        //нет смысла удалять из хэш-таблицы, т.к. при добавлении нового просмотра запись просто обновиться
    }

    private void removeNode (Node node) {
        Node next = node.getNext(); //найти следующий просмотр
        Node prev = node.getPrev(); //найти предыдущий просмотр

        if(next == null) {
            tail = prev;
            prev.setNext(null);
        } else {
            next.setPrev(prev);
        }

        if (prev == null) {
            head = next;
            next.setPrev(null);
        } else {
            prev.setNext(next);
        }

        size--;
        node.setPrev(null);
        node.setNext(null);
        node.setData(null);


    }

    private List<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<Task>();
        Node node = head;
        while (node != null) {
            tasks.add((Task) node.getTask());
            node = node.getNext();
        }
        return  tasks;
    }

    @Override
    public List<Task> getHistory(){
        return getTasks();
    }



    //метод для тестирования
    public void print() {
        List<Task> history = getHistory();
        for (Task task : history) {
            System.out.println(task);
        }

    }
}
