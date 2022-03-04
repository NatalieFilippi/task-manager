package historymanager;

import tasktracker.tasks.Task;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private final List<Task> history;
    private final static byte historySize = 10;

    public InMemoryHistoryManager() {
        history = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        if (history.size() < historySize) {
            history.add(task);
        } else {
            history.remove(0);
            history.add(task);
        }
    }

    @Override
    public List<Task> getHistory(){
        return new ArrayList<Task>(history);
    }

    //метод для тестирования
    public void print() {
        for (Task task : history) {
            System.out.println(task);
        }

    }
}
