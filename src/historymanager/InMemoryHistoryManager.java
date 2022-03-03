package historymanager;

import tasktracker.tasks.Task;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private final List<Task> historyList;
    private final static byte historySize = 10;

    public InMemoryHistoryManager() {
        historyList = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        if (historyList.size() < historySize) {
            historyList.add(task);
        } else {
            historyList.remove(0);
            historyList.add(task);
        }
    }

    @Override
    public List<Task> getHistory(){
        return new ArrayList<Task>(historyList);
    }

    public void print() {
        for (Task task : historyList) {
            System.out.println(task);
        }

    }
}
