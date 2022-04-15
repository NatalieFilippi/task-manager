package historymanager;

import java.util.List;
import tasktracker.tasks.Task;

public interface HistoryManager {

    public void add(Task task);

    public List<Task> getHistory();

    public void remove(Long id);

    public String stringToFile();

    public void print();

}
