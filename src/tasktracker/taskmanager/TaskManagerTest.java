package tasktracker.taskmanager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.IllegalArgumentException;
import tasktracker.TaskStatus;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;




class TaskManagerTest {

    final static File file = new File("resources" + File.separator + "task manager.csv");
    private static FileBackedTasksManager taskManagerFile;
    private static InMemoryTaskManager taskManagerMemory;
    private static Epic epic;
    private static Task task1;
    private static Task task2;
    private static Task task3;
    private static Task taskNull;
    private static Subtask s1;
    private static Subtask s2;

    @BeforeEach
    void beforeEach() {
        taskManagerFile = new FileBackedTasksManager(file);
        taskManagerMemory = new InMemoryTaskManager();
        taskManagerFile.incrementalId = 0;
        taskManagerMemory.incrementalId = 0;

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
    }

    @AfterEach
    void clear() {
        taskManagerFile.deleteAllTasks();
        taskManagerFile.deleteAllEpics();
        taskManagerFile.deleteAllSubtasks();

        taskManagerMemory.deleteAllTasks();
        taskManagerMemory.deleteAllEpics();
        taskManagerMemory.deleteAllSubtasks();
    }

    @Test
    void taskTestFile() {
        addNewTask(taskManagerFile);
    }

    @Test
    void taskTestMemory() {
        addNewTask(taskManagerMemory);
    }

    private static void addNewTask(TaskManager taskManager) {
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
    void epicTestFile() {
        addNewEpic(taskManagerFile);
    }

    @Test
    void epicTestMemory() {
        addNewEpic(taskManagerMemory);
    }

    private static void addNewEpic(TaskManager taskManager) {
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
    void subtaskTestFile() {
        addNewSubtask(taskManagerFile);
    }

    @Test
    void subtaskTestMemory() {
        addNewSubtask(taskManagerMemory);
    }

    private static void addNewSubtask(TaskManager taskManager) {
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
    void deleteAllFile() {
        deleteAll(taskManagerFile);
    }

   @Test
    void deleteAllMemory() {
       deleteAll(taskManagerMemory);
    }

    private static void deleteAll(TaskManager taskManager) {

        taskManager.createTask(task1);
        taskManager.createEpic(epic);
        taskManager.createSubtask(s1);

        delete(taskManager);

        assertEquals(0, taskManager.getTaskMap().size());
        assertEquals(0, taskManager.getEpicMap().size());
        assertEquals(0, taskManager.getSubtaskMap().size());
    }

    private static void delete(TaskManager taskManager) {
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubtasks();
    }

    @Test
    void saveFromFile() {
        taskManagerFile.createEpic(epic);
        taskManagerFile.createTask(task1);
        taskManagerFile.createSubtask(s1);

        s1.setDuration(Duration.ofHours(5).plus(Duration.ofMinutes(20)));
        s1.setStartTime(LocalDateTime.of(2022, Month.NOVEMBER,24,12,00));
        taskManagerFile.updateSubtask(s1);

    }

    @Test
    void sortingByPriority() {

        taskManagerFile.createEpic(epic);
        taskManagerFile.createSubtask(s1);
        taskManagerFile.createSubtask(s2);
        taskManagerFile.createTask(taskNull);

        taskNull.setStartTime(LocalDateTime.of(2022, Month.APRIL,3,12,00));
        s1.setStartTime(LocalDateTime.of(2022, Month.APRIL,4,12,00));
        s2.setStartTime(LocalDateTime.of(2022, Month.APRIL,5,12,00));

        taskManagerFile.updateTask(taskNull);
        taskManagerFile.updateSubtask(s1);
        taskManagerFile.updateSubtask(s2);

        assertEquals(3, taskManagerFile.getPrioritizedTasks().size());
        assertEquals(taskNull,taskManagerFile.getPrioritizedTasks().get(0));

    }

    @Test
    void sortingByPriorityWithNull() {

        taskManagerFile.createTask(taskNull);
        taskManagerFile.createTask(task2);

        taskManagerFile.createTask(task1);
        taskManagerFile.createTask(task3);

        assertEquals(4, taskManagerFile.getPrioritizedTasks().size());
        assertEquals(taskManagerFile.getPrioritizedTasks().get(0), task3);

        taskManagerFile.getTaskMap().get(0).setStartTime(LocalDateTime.of(2022, Month.NOVEMBER,4,12,00));
        taskManagerFile.updateTask(taskManagerFile.getTaskMap().get(0));
        assertEquals(taskManagerFile.getPrioritizedTasks().get(0), taskNull);

        assertEquals(4, taskManagerFile.getPrioritizedTasks().size());
    }

    @Test
    void checkPriority() {

        taskManagerFile.createTask(new Task("Task 1", "Test 1 description", TaskStatus.NEW,
                Duration.ofHours(5).plus(Duration.ofMinutes(20)),
                LocalDateTime.of(2022, Month.NOVEMBER,14,12,00)));
        Task taskTest = new Task("Task 2", "Test 2 description", TaskStatus.NEW,
                Duration.ofHours(5).plus(Duration.ofMinutes(20)),
                LocalDateTime.of(2022, Month.NOVEMBER,14,12,10));
        assertEquals(1, taskManagerFile.getTaskMap().size());

        taskTest.setStartTime(LocalDateTime.of(2022, Month.NOVEMBER,14,10,00));
        taskTest.setDuration(Duration.ofHours(1).plus(Duration.ofMinutes(20)));
        taskManagerFile.createTask(taskTest);
        assertEquals(2, taskManagerFile.getTaskMap().size());

        taskTest.setDuration(Duration.ofHours(2).plus(Duration.ofMinutes(20)));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, ()->taskManagerFile.updateTask(taskTest));
        assertEquals(ex.getMessage(), "Задача не может пересекаться с другими задачами!");
    }
}