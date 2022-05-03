package test.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktracker.TaskStatus;
import tasktracker.taskmanager.FileBackedTasksManager;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

class EpicTest {


    final static File file = new File("resources" + File.separator + "task manager.csv");
    private static FileBackedTasksManager taskManager;
    private Epic epic;

    @AfterEach
    void clear() {
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubtasks();
    }

    @BeforeEach
    void beforeEach() {
        taskManager = new FileBackedTasksManager(file);
        epic = new Epic("Test addNewEpic", "Test addNewTask description");
        taskManager.createEpic(epic);
    }


    @Test
    void addNewTask() {
        final Task task = new Task("Test addNewTask", "Test addNewTask description", TaskStatus.NEW);
        taskManager.createTask(task);

        final Task savedTask = taskManager.getTaskById(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTaskMap();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void EpicWithoutSubtasks() {

        final Epic savedEpic = taskManager.getEpicByID(epic.getId());
        assertEquals(savedEpic.getStatus(), TaskStatus.NEW);
    }

    @Test
    void EpicWithSubtasksNew() {

        taskManager.createSubtask(new Subtask("Subtask test", "subtask test 1", TaskStatus.NEW, epic.getId()));
        taskManager.createSubtask(new Subtask("Subtask test", "subtask test 2", TaskStatus.NEW, epic.getId()));
        taskManager.createSubtask(new Subtask("Subtask test", "subtask test 3", TaskStatus.NEW, epic.getId()));

        final Epic savedEpic = taskManager.getEpicByID(epic.getId());
        assertEquals(savedEpic.getStatus(), TaskStatus.NEW);
    }

    @Test
    void EpicWithSubtasksDone() {

        taskManager.createSubtask(new Subtask("Subtask test", "subtask test 1", TaskStatus.DONE, epic.getId()));
        taskManager.createSubtask(new Subtask("Subtask test", "subtask test 2", TaskStatus.DONE, epic.getId()));
        taskManager.createSubtask(new Subtask("Subtask test", "subtask test 3", TaskStatus.DONE, epic.getId()));

        final Epic savedEpic = taskManager.getEpicByID(epic.getId());
        assertEquals(savedEpic.getStatus(), TaskStatus.DONE);
    }

    @Test
    void EpicWithSubtasksNewAndDone() {

        taskManager.createSubtask(new Subtask("Subtask test", "subtask test 1", TaskStatus.DONE, epic.getId()));
        taskManager.createSubtask(new Subtask("Subtask test", "subtask test 2", TaskStatus.NEW, epic.getId()));
        taskManager.createSubtask(new Subtask("Subtask test", "subtask test 3", TaskStatus.DONE, epic.getId()));

        final Epic savedEpic = taskManager.getEpicByID(epic.getId());
        assertEquals(savedEpic.getStatus(), TaskStatus.IN_PROGRESS);
    }

    @Test
    void EpicWithSubtasksInProgress() {

        taskManager.createSubtask(new Subtask("Subtask test", "subtask test 1", TaskStatus.IN_PROGRESS, epic.getId()));
        taskManager.createSubtask(new Subtask("Subtask test", "subtask test 2", TaskStatus.IN_PROGRESS, epic.getId()));
        taskManager.createSubtask(new Subtask("Subtask test", "subtask test 3", TaskStatus.IN_PROGRESS, epic.getId()));

        final Epic savedEpic = taskManager.getEpicByID(epic.getId());
        assertEquals(savedEpic.getStatus(), TaskStatus.IN_PROGRESS);
    }

    @Test
    void timeCalculateStart() {
        taskManager.createSubtask(new Subtask("Subtask test", "subtask test 1", TaskStatus.IN_PROGRESS, epic.getId()));
        taskManager.getSubtaskByID(2).setStartTime(LocalDateTime.of(2022, Month.NOVEMBER,24,12,00));
        taskManager.getEpicByID(1).timeCalculation();
        assertEquals(taskManager.getEpicByID(1).getStartTime(), taskManager.getSubtaskByID(2).getStartTime());
    }

    @Test
    void timeCalculateStartNull() {
        taskManager.createSubtask(new Subtask("Subtask test", "subtask test 1", TaskStatus.IN_PROGRESS, epic.getId()));
        taskManager.getSubtaskByID(2).setStartTime(null);
        taskManager.getEpicByID(epic.getId()).timeCalculation();
        assertEquals(taskManager.getEpicByID(epic.getId()).getStartTime(), taskManager.getSubtaskByID(2).getStartTime());
    }

    @Test
    void timeCalculateStart2Subtask() {
        Subtask s1 = new Subtask("Subtask test1", "subtask test 1", TaskStatus.IN_PROGRESS, epic.getId());
        Subtask s2 = new Subtask("Subtask test2", "subtask test 2", TaskStatus.IN_PROGRESS, epic.getId());
        taskManager.createSubtask(s1);
        taskManager.createSubtask(s2);
        s1.setStartTime(LocalDateTime.of(2022, Month.NOVEMBER,24,12,00));
        s2.setStartTime(LocalDateTime.of(2022, Month.NOVEMBER,22,12,00));
        taskManager.updateSubtask(s1);
        taskManager.updateSubtask(s2);
        assertEquals(taskManager.getEpicByID(1).getStartTime(), s2.getStartTime());
    }

    @Test
    void timeCalculateStart2SubtaskWithNull() {
        Subtask s1 = new Subtask("Subtask test1", "subtask test 1", TaskStatus.IN_PROGRESS, epic.getId());
        Subtask s2 = new Subtask("Subtask test2", "subtask test 2", TaskStatus.IN_PROGRESS, epic.getId());
        taskManager.createSubtask(s1);
        taskManager.createSubtask(s2);
        s1.setStartTime(LocalDateTime.of(2022, Month.NOVEMBER,24,12,00));
        s2.setStartTime(null);
        taskManager.updateSubtask(s1);
        taskManager.updateSubtask(s2);
        assertEquals(taskManager.getEpicByID(epic.getId()).getStartTime(), s1.getStartTime());
    }

    @Test
    void timeCalculateDuration() {
        taskManager.createSubtask(new Subtask("Subtask test", "subtask test 1", TaskStatus.IN_PROGRESS, epic.getId()));
        taskManager.getSubtaskByID(2).setDuration(Duration.ofHours(5).plus(Duration.ofMinutes(20)));
        taskManager.getEpicByID(epic.getId()).timeCalculation();
        assertEquals(taskManager.getEpicByID(epic.getId()).getDuration(), taskManager.getSubtaskByID(2).getDuration());
    }

    @Test
    void timeCalculateDurationNull() {
        taskManager.createSubtask(new Subtask("Subtask test", "subtask test 1", TaskStatus.IN_PROGRESS, epic.getId()));
        taskManager.getSubtaskByID(2).setDuration(null);
        taskManager.getEpicByID(epic.getId()).timeCalculation();
        assertEquals(taskManager.getEpicByID(epic.getId()).getDuration(), Duration.ZERO);
    }

    @Test
    void timeCalculateDuration2Subtask() {
        taskManager.createSubtask(new Subtask("Subtask test 1", "subtask test 1", TaskStatus.IN_PROGRESS, epic.getId()));
        taskManager.createSubtask(new Subtask("Subtask test 2", "subtask test 2", TaskStatus.IN_PROGRESS, epic.getId()));
        taskManager.getSubtaskByID(2).setDuration(Duration.ofHours(5).plus(Duration.ofMinutes(20)));
        taskManager.getSubtaskByID(3).setDuration(Duration.ofHours(1).plus(Duration.ofMinutes(50)));
        taskManager.getEpicByID(epic.getId()).timeCalculation();
        assertEquals(taskManager.getEpicByID(epic.getId()).getDuration(), Duration.ofHours(7).plus(Duration.ofMinutes(10)));
    }

    @Test
    void timeCalculateDuration2SubtaskWithNull() {
        taskManager.createSubtask(new Subtask("Subtask test 1", "subtask test 1", TaskStatus.IN_PROGRESS, epic.getId()));
        taskManager.createSubtask(new Subtask("Subtask test 2", "subtask test 2", TaskStatus.IN_PROGRESS, epic.getId()));
        taskManager.getSubtaskByID(2).setDuration(null);
        taskManager.getSubtaskByID(3).setDuration(Duration.ofHours(1).plus(Duration.ofMinutes(50)));
        taskManager.getEpicByID(epic.getId()).timeCalculation();
        assertEquals(taskManager.getEpicByID(epic.getId()).getDuration(), Duration.ofHours(1).plus(Duration.ofMinutes(50)));
    }

    @Test
    void timeCalculateEnd2SubtaskWithNull() {
        Subtask s1 = new Subtask("Subtask test1", "subtask test 1", TaskStatus.IN_PROGRESS, epic.getId());
        Subtask s2 = new Subtask("Subtask test2", "subtask test 2", TaskStatus.IN_PROGRESS, epic.getId());
        taskManager.createSubtask(s1);
        taskManager.createSubtask(s2);
        s1.setStartTime(LocalDateTime.of(2022, Month.NOVEMBER,24,12,00));
        s1.setDuration(Duration.ofHours(1).plus(Duration.ofMinutes(50)));
        taskManager.updateSubtask(s1);
        taskManager.updateSubtask(s2);
        assertEquals(taskManager.getEpicByID(epic.getId()).getEndTime(), s1.getEndTime());
    }

    @Test
    void timeCalculateEnd() {
        Subtask s1 = new Subtask("Subtask test1", "subtask test 1", TaskStatus.IN_PROGRESS, epic.getId());
        Subtask s2 = new Subtask("Subtask test2", "subtask test 2", TaskStatus.IN_PROGRESS, epic.getId());
        taskManager.createSubtask(s1);
        taskManager.createSubtask(s2);
        s1.setStartTime(LocalDateTime.of(2022, Month.NOVEMBER,24,12,00));
        s1.setDuration(Duration.ofHours(1).plus(Duration.ofMinutes(50)));

        s2.setStartTime(LocalDateTime.of(2022, Month.NOVEMBER,21,10,00));
        s2.setDuration(Duration.ofHours(4));

        taskManager.updateSubtask(s1);
        taskManager.updateSubtask(s2);
        assertEquals(taskManager.getEpicByID(epic.getId()).getEndTime(), s1.getEndTime());
        assertEquals(taskManager.getEpicByID(epic.getId()).getStartTime(), s2.getStartTime());
        assertEquals(taskManager.getEpicByID(epic.getId()).getDuration(), Duration.ofHours(5).plus(Duration.ofMinutes(50)));
    }
}