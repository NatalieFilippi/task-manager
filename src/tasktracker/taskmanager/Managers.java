package tasktracker.taskmanager;
import historymanager.InMemoryHistoryManager;

public class Managers {

    private static InMemoryHistoryManager historyManager;

    public Managers() {
        historyManager = new InMemoryHistoryManager();
    }

    public TaskManager getDefault() {
        return new InMemoryTaskManager();
        //return new FileBackedTasksManager("task manager.csv");
    }

    public static InMemoryHistoryManager getDefaultHistory() {
        return historyManager;
    }
}
