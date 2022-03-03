package tasktracker.taskmanager;
import historymanager.InMemoryHistoryManager;
import historymanager.HistoryManager;

public class Managers {

    private static InMemoryHistoryManager historyManager;

    public Managers() {
        historyManager = new InMemoryHistoryManager();
    }

    public TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static InMemoryHistoryManager getDefaultHistory() {
        return historyManager;
    }
}
