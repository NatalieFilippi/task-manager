package historymanager;

import java.util.List;
import tasktracker.tasks.Task;

public interface HistoryManager {

    public void add(Task task);

    public List<Task> getHistory();

}
