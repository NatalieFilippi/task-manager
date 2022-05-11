package tasktracker.taskmanager;
import historymanager.HistoryManager;
import historymanager.InMemoryHistoryManager;

import java.io.File;


public class Managers {
    private static final String PATH = "resources" + File.separator + "task manager.csv";
    private static final String url = "http://localhost:8078/";
    private static HistoryManager historyManager = new InMemoryHistoryManager();

    private Managers() {
    }

    public static HistoryManager getHistoryManager() {
        return historyManager;
    }

    public static TaskManager getDefault() {
        return new HTTPTaskManager(url);
    }

}
