package test.test;

import org.junit.jupiter.api.Test;
import tasktracker.TaskStatus;
import tasktracker.taskmanager.FileBackedTasksManager;
import tasktracker.taskmanager.TaskManager;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest{

    public FileBackedTasksManagerTest() throws IOException {
        super(new FileBackedTasksManager(fileEmpty));
    }
    final static File fileEmpty = new File("resources" + File.separator + "task manager empty test.csv");
    final static Epic epic1 = new Epic("Test addNewEpic1", "Test addNewEpic description1");
    final static Epic epic2 = new Epic("Test addNewEpic2", "Test addNewEpic description2");
    private static FileBackedTasksManager taskManager1;

    @Test
    void saveFromFile() {

        taskManager.createEpic(epic1);
        Subtask s = new Subtask("Subtask 1", "subtask test 1", TaskStatus.NEW, 1);
        s.setDuration(Duration.ofHours(5).plus(Duration.ofMinutes(20)));
        s.setStartTime(LocalDateTime.of(2022, Month.NOVEMBER,24,12,00));
        taskManager.createSubtask(s);
        taskManager.getSubtaskByID(2);
        taskManager.getEpicByID(1);

        FileBackedTasksManager taskManager2 = new FileBackedTasksManager(fileEmpty);
        assertEquals(2, taskManager2.history().size());
        assertEquals(s.getStartTime(), taskManager2.history().get(0).getStartTime());

    }

    @Test
    void EmptyFile() {

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