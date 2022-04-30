package tasktracker.taskmanager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktracker.tasks.Epic;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTasksManagerTest {

    final static File file = new File("resources" + File.separator + "task manager test.csv");
    final static File fileEmpty = new File("resources" + File.separator + "task manager empty test.csv");
    private static FileBackedTasksManager taskManager;
    final static Epic epic1 = new Epic("Test addNewEpic1", "Test addNewEpic description1");
    final static Epic epic2 = new Epic("Test addNewEpic2", "Test addNewEpic description2");

    @AfterEach
    void clear() {
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubtasks();
    }

    @Test
    void EmptyFile() {
        taskManager = new FileBackedTasksManager(fileEmpty);
        assertEquals(0, taskManager.getTaskMap().size());
        assertEquals(0, taskManager.getEpicMap().size());
        assertEquals(0, taskManager.getSubtaskMap().size());
        assertEquals(0, taskManager.history().size());

        FileBackedTasksManager taskManager2 = new FileBackedTasksManager(fileEmpty);
        assertEquals(0, taskManager2.getTaskMap().size());
        assertEquals(0, taskManager2.getEpicMap().size());
        assertEquals(0, taskManager2.getSubtaskMap().size());
        assertEquals(0, taskManager2.history().size());
    }

    @Test
    void EmptyTasks() {
        taskManager = new FileBackedTasksManager(file);

        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubtasks();

        FileBackedTasksManager taskManager2 = new FileBackedTasksManager(fileEmpty);
        assertEquals(0, taskManager2.getTaskMap().size());
        assertEquals(0, taskManager2.getEpicMap().size());
        assertEquals(0, taskManager2.getSubtaskMap().size());
        assertEquals(0, taskManager2.history().size());
    }

    @Test
    void OnlyEpic() {
        taskManager = new FileBackedTasksManager(fileEmpty);

        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.getEpicByID(epic1.getId());
        taskManager.getEpicByID(epic2.getId());

        FileBackedTasksManager taskManager2 = new FileBackedTasksManager(fileEmpty);
        assertEquals(0, taskManager2.getTaskMap().size());
        assertEquals(2, taskManager2.getEpicMap().size());
        assertEquals(0, taskManager2.getSubtaskMap().size());
        assertEquals(2, taskManager2.history().size());
    }


}