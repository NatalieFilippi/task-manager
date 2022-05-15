package test.test;

import httpserver.HttpTaskServer;
import httpserver.KVServer;
import httpserver.KVTaskClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import tasktracker.TaskStatus;
import tasktracker.taskmanager.FileBackedTasksManager;
import tasktracker.taskmanager.HTTPTaskManager;
import tasktracker.taskmanager.TaskManager;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    public TaskManagerTest(T taskManager) throws IOException {
        this.taskManager = taskManager;
    }

    private static Epic epic;
    private static Task task1;
    private static Task task2;
    private static Task task3;
    private static Task taskNull;
    private static Subtask s1;
    private static Subtask s2;
    private static KVServer kvServer;
    private HttpTaskServer httpTaskServer;
    private KVTaskClient kvTaskClient;

    @BeforeEach
    void beforeEach() throws IOException {
        epic = new Epic("Epic", "Test addNewEpic description");
        task1 = new Task("Task 1", "Test 1 addNewTask description", TaskStatus.NEW,
                Duration.ofHours(5).plus(Duration.ofMinutes(20)),
                LocalDateTime.of(2022, Month.NOVEMBER,14,12,00));
        task2 = new Task("Task 2", "Test 2 addNewTask description", TaskStatus.NEW,
                Duration.ofHours(1).plus(Duration.ofMinutes(10)),
                LocalDateTime.of(2022, Month.NOVEMBER,10,10,00));

        task3 = new Task("Task 3", "Test 3 addNewTask description", TaskStatus.NEW,
                Duration.ofHours(2).plus(Duration.ofMinutes(05)),
                LocalDateTime.of(2022, Month.NOVEMBER,5,12,00));

        taskNull = new Task("Task 4", "Test 4 addNewTask description", TaskStatus.NEW);
        s1 = new Subtask("Subtask 1", "subtask test 1", TaskStatus.NEW, 1);
        s2 = new Subtask("Subtask 2", "subtask test 2", TaskStatus.NEW, 1);

        kvServer = new KVServer();
        kvServer.start();
        httpTaskServer = new HttpTaskServer();
        kvTaskClient = new KVTaskClient("http://localhost:8078/");
    }

    @AfterEach
    void clear() {
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubtasks();
        kvServer.stop();
        httpTaskServer.stop();
    }

    @Test
    void addNewTask() {

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        final Task savedTask = taskManager.getTaskById(task1.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task1, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTaskMap();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(task1, tasks.get(0), "Задачи не совпадают.");


        taskManager.updateTask(null);
        assertArrayEquals(tasks.toArray(), taskManager.getTaskMap().toArray(), "Задача не должна менять состояние менеджера");

        taskManager.updateTask(new Task("Test task", "Detail", TaskStatus.NEW));
        assertArrayEquals(tasks.toArray(), taskManager.getTaskMap().toArray(), "Задача не должна менять состояние менеджера");

        savedTask.setName("Test 1");
        taskManager.updateTask(savedTask);
        assertEquals(savedTask, taskManager.getTaskMap().get(0), "Задачи не совпадают.");


        taskManager.deleteByIDTask(897l);
        assertEquals(2, taskManager.getTaskMap().size(), "Неверное количество задач.");

        taskManager.deleteByIDTask(savedTask.getId());
        assertEquals(1, taskManager.getTaskMap().size(), "Неверное количество задач.");
    }

    @Test
    void addNewEpic() {
        taskManager.createEpic(epic);

        final Epic savedEpic = taskManager.getEpicByID(epic.getId());

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");

        final List<Epic> epics = taskManager.getEpicMap();

        assertNotNull(epics, "Задачи на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");

        taskManager.updateEpic(null);
        assertArrayEquals(epics.toArray(), taskManager.getEpicMap().toArray(), "Задача не должна менять состояние менеджера");

        taskManager.updateTask(new Epic("Test epic", "Detail"));
        assertArrayEquals(epics.toArray(), taskManager.getEpicMap().toArray(), "Задача не должна менять состояние менеджера");

        savedEpic.setName("Test 1");
        taskManager.updateTask(savedEpic);
        assertEquals(savedEpic, epics.get(0), "Задачи не совпадают.");


        taskManager.createSubtask(new Subtask("Subtask test", "subtask test 1", TaskStatus.NEW, 1));
        taskManager.createSubtask(new Subtask("Subtask test", "subtask test 2", TaskStatus.IN_PROGRESS, 1));
        assertEquals(savedEpic.getStatus(), TaskStatus.IN_PROGRESS);
        taskManager.deleteAllSubtasks();
        assertEquals(savedEpic.getStatus(), TaskStatus.NEW);
        taskManager.createSubtask(new Subtask("Subtask test", "subtask test 3", TaskStatus.DONE, 1));
        assertEquals(savedEpic.getStatus(), TaskStatus.DONE);

        taskManager.deleteByIDEpic(897l);
        assertEquals(1, taskManager.getEpicMap().size(), "Неверное количество задач.");

        taskManager.deleteByIDEpic(savedEpic.getId());
        assertEquals(0, taskManager.getEpicMap().size(), "Неверное количество задач.");
    }

    @Test
    void addNewSubtask() {
        taskManager.createSubtask(s1);
        final Subtask subtaskNull = taskManager.getSubtaskByID(s1.getId());
        assertNull(subtaskNull, "Задача не должна быть создана.");

        taskManager.createEpic(epic);
        taskManager.createSubtask(s2);
        final Subtask savedSubtask = taskManager.getSubtaskByID(s2.getId());

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(s2, savedSubtask, "Задачи не совпадают.");

        final List<Subtask> subtasks = taskManager.getSubtaskMap();

        assertNotNull(subtasks, "Задачи на возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(s2, subtasks.get(0), "Задачи не совпадают.");

        taskManager.updateSubtask(null);
        assertArrayEquals(subtasks.toArray(), taskManager.getSubtaskMap().toArray(), "Задача не должна менять состояние менеджера");

        taskManager.updateSubtask(new Subtask("Test subtask", "Detail",TaskStatus.NEW,1));
        assertArrayEquals(subtasks.toArray(), taskManager.getSubtaskMap().toArray(), "Задача не должна менять состояние менеджера");

        savedSubtask.setName("Test 1");
        taskManager.updateTask(savedSubtask);
        assertEquals(savedSubtask, subtasks.get(0), "Задачи не совпадают.");


        taskManager.deleteByIDSubtask(897l);
        assertEquals(1, taskManager.getSubtaskMap().size(), "Неверное количество задач.");

        taskManager.deleteByIDSubtask(savedSubtask.getId());
        assertEquals(0, taskManager.getSubtaskMap().size(), "Неверное количество задач.");
    }

    @Test
    void deleteAll() {

        taskManager.createTask(task1);
        taskManager.createEpic(epic);
        taskManager.createSubtask(s1);

        delete();

        assertEquals(0, taskManager.getTaskMap().size());
        assertEquals(0, taskManager.getEpicMap().size());
        assertEquals(0, taskManager.getSubtaskMap().size());
    }

    void delete() {
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubtasks();
    }

    @Test
    void sortingByPriority() {

        taskManager.createEpic(epic);
        taskManager.createSubtask(s1);
        taskManager.createSubtask(s2);
        taskManager.createTask(taskNull);

        taskNull.setStartTime(LocalDateTime.of(2022, Month.APRIL,3,12,00));
        s1.setStartTime(LocalDateTime.of(2022, Month.APRIL,4,12,00));
        s2.setStartTime(LocalDateTime.of(2022, Month.APRIL,5,12,00));

        taskManager.updateTask(taskNull);
        taskManager.updateSubtask(s1);
        taskManager.updateSubtask(s2);

        assertEquals(3, taskManager.getPrioritizedTasks().size());
        assertEquals(taskNull,taskManager.getPrioritizedTasks().get(0));

    }

    @Test
    void sortingByPriorityWithNull() {

        taskManager.createTask(taskNull);
        taskManager.createTask(task2);

        taskManager.createTask(task1);
        taskManager.createTask(task3);

        assertEquals(4, taskManager.getPrioritizedTasks().size());
        assertEquals(taskManager.getPrioritizedTasks().get(0), task3);

        taskManager.getTaskMap().get(0).setStartTime(LocalDateTime.of(2022, Month.NOVEMBER,4,12,00));
        taskManager.updateTask(taskManager.getTaskMap().get(0));
        assertEquals(taskManager.getPrioritizedTasks().get(0), taskNull);

        assertEquals(4, taskManager.getPrioritizedTasks().size());
    }

    @Test
    void checkPriority() {

        taskManager.createTask(new Task("Task 1", "Test 1 description", TaskStatus.NEW,
                Duration.ofHours(5).plus(Duration.ofMinutes(20)),
                LocalDateTime.of(2022, Month.NOVEMBER,14,12,00)));
        Task taskTest = new Task("Task 2", "Test 2 description", TaskStatus.NEW,
                Duration.ofHours(5).plus(Duration.ofMinutes(20)),
                LocalDateTime.of(2022, Month.NOVEMBER,14,12,10));
        assertEquals(1, taskManager.getTaskMap().size());

        taskTest.setStartTime(LocalDateTime.of(2022, Month.NOVEMBER,14,10,00));
        taskTest.setDuration(Duration.ofHours(1).plus(Duration.ofMinutes(20)));
        taskManager.createTask(taskTest);
        assertEquals(2, taskManager.getTaskMap().size());

        taskTest.setDuration(Duration.ofHours(2).plus(Duration.ofMinutes(20)));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, ()->taskManager.updateTask(taskTest));
        assertEquals(ex.getMessage(), "Задача не может пересекаться с другими задачами!");
    }
}


/*class FileBackedTaskManagerTest2 extends TaskManagerTest {

    public FileBackedTaskManagerTest2() {
        super(new FileBackedTasksManager(fileEmpty));
    }

    final static File fileEmpty = new File("resources" + File.separator + "task manager empty test.csv");
    final static File file = new File("resources" + File.separator + "task manager test.csv");
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

}*/