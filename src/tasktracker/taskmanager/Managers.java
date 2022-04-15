package tasktracker.taskmanager;
import historymanager.HistoryManager;
import historymanager.InMemoryHistoryManager;


public class Managers {

    private static HistoryManager historyManager = new InMemoryHistoryManager();
    private static TaskManager taskManager = new InMemoryTaskManager();

    private Managers() {
    }

    public static HistoryManager getHistoryManager() {
        return historyManager;
    }

    public static TaskManager getDefault() {
        return taskManager;
    }

}
