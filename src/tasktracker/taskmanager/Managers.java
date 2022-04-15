package tasktracker.taskmanager;
import historymanager.InMemoryHistoryManager;

import java.io.File;

public class Managers {

    private static InMemoryHistoryManager historyManager;

    private Managers() {
        historyManager = new InMemoryHistoryManager();
    }

    public static TaskManager getDefault() {
        final String PATH = "resources" + File.separator + "task manager.csv";
        return new FileBackedTasksManager(new File(PATH));
        //return new InMemoryTaskManager();
    }

    public static InMemoryHistoryManager getDefaultHistory() {
        return historyManager;
    }
}
