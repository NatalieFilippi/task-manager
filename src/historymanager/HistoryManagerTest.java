package historymanager;

import org.junit.jupiter.api.BeforeEach;
import tasktracker.TaskStatus;
import tasktracker.taskmanager.InMemoryTaskManager;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    private static InMemoryTaskManager taskManager;

    final static Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
    final static Task task1 = new Task("Test 1 addNewTask", "Test 1 addNewTask description", TaskStatus.NEW);
    final static Task task2 = new Task("Test 2 addNewTask", "Test 2 addNewTask description", TaskStatus.NEW);
    final static Subtask s1 = new Subtask("Subtask test 1", "subtask test 1", TaskStatus.NEW, 2);
    final static Subtask s2 = new Subtask("Subtask test 2", "subtask test 2", TaskStatus.NEW, 2);

    @BeforeEach
    void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }
    @AfterEach
    void clear() {
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubtasks();
    }

    @Test
    void emptyStory() {
        taskManager.createTask(task1);
        taskManager.createSubtask(s1);
        assertEquals(true, taskManager.history().isEmpty());
    }
    @Test

    void add() {
        taskManager.createTask(task1);
        taskManager.getTaskById(task1.getId());
        assertNotNull(taskManager.history(), "История не пустая.");
        assertEquals(1, taskManager.history().size(),"История не пустая.");
    }

    @Test
    void duplicateRequest() {
        taskManager.createTask(task1);
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());
        assertEquals(1, taskManager.history().size());
    }
    @Test
    void deletingFromBeginning() {
        taskManager.createTask(task1);
        taskManager.createEpic(epic);
        taskManager.createSubtask(s1);

        taskManager.getTaskById(task1.getId());
        taskManager.getEpicByID(epic.getId());
        taskManager.getSubtaskByID(s1.getId());

        taskManager.deleteByIDTask(1);
        assertEquals(2, taskManager.history().size());
    }

    @Test
    void deletingFromEnd() {
        taskManager.createTask(task1);
        taskManager.createEpic(epic);
        taskManager.createSubtask(s1);

        taskManager.getTaskById(task1.getId());
        taskManager.getEpicByID(epic.getId());
        taskManager.getSubtaskByID(s1.getId());

        taskManager.deleteByIDSubtask(s1.getId());
        assertEquals(2, taskManager.history().size());
    }

    @Test
    void deletingFromMiddle() {
        taskManager.createTask(task1);
        taskManager.createEpic(epic);
        taskManager.createSubtask(s1);

        taskManager.getTaskById(task1.getId());
        taskManager.getEpicByID(epic.getId());
        taskManager.getSubtaskByID(s1.getId());

        taskManager.deleteByIDEpic(epic.getId());
        assertEquals(1, taskManager.history().size()); //сабтаски эпика тоже удаляются
    }

}