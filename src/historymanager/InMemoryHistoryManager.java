package historymanager;

import tasktracker.tasks.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private final HashMap<Long,Node> nodeTable;
    private Node head;
    private Node tail;

    public InMemoryHistoryManager() {
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

        //записать соответствие в таблицу
        nodeTable.put(task.getId(), newNode);

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

        if (prev == null) {
            head = next;
        } else {
            prev.setNext(next);
        }

        if (next == null) {
            tail = prev;
        } else {
            next.setPrev(prev);
        }



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

    @Override
    public String stringToFile() {
        List<Task> tasks = getTasks();
        String line = "";
        for (Task task : tasks) {
            line += task.getId() +",";
        }
        if (!line.isEmpty()) {
            line = line.substring(0,line.length()-1);
        }
        return line; //чтобы не передавать последнюю запятую
    }




    //метод для тестирования
    @Override
    public void print() {
        List<Task> history = getHistory();
        for (Task task : history) {
            System.out.println(task);
        }

    }
}
