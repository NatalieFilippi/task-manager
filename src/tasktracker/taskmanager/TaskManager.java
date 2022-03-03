package tasktracker.taskmanager;

import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;

import java.util.HashMap;

public interface TaskManager {

    //~~~~~~~~~ Получить список задач + ~~~~~~~~~~~

    public HashMap<Integer, Task> getTaskMap();

    public HashMap<Integer, Epic> getEpicMap();

    public HashMap<Integer, Subtask> getSubtaskMap();

    //~~~~~~~~~ Удалить все задачи + ~~~~~~~~~~~

    public void deleteAllTasks();

    public void deleteAllEpics();

    public void deleteAllSubtasks();

    //~~~~~~~~~ Получить по идентификатору + ~~~~~~~~~~~

    public Task getTaskById(int id);

    public Epic getEpicByID(int id);

    public Subtask getSubtaskByID(int id);

    //~~~~~~~~~ Создать задачу + ~~~~~~~~~~~

    public void createTask(Task task);

    public void createEpic(Epic epic);

    public void createSubtask(Subtask subtask);

    //~~~~~~~~~ Обновить задачу + ~~~~~~~~~~~

    public void updateTask(Task task);

    public void updateEpic(Epic epic);

    public void updateSubtask(Subtask subtask);

    //~~~~~~~~~ Удалить по идентификатору + ~~~~~~~~~~~

    public void deleteByIDTask(int id);

    public void deleteByIDEpic(int id);

    public void deleteByIDSubtask(int id);

    //~~~~~~~~~~~~~~~~ История ~~~~~~~~~~~~~~~~~~~~~~~~~

    public void printHistory();

}

