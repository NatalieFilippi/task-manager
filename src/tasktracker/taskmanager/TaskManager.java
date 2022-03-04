package tasktracker.taskmanager;

import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public interface TaskManager {

    //~~~~~~~~~ Получить список задач + ~~~~~~~~~~~

    public ArrayList<Task> getTaskMap();

    public ArrayList<Epic> getEpicMap();

    public ArrayList<Subtask> getSubtaskMap();

    //~~~~~~~~~ Удалить все задачи + ~~~~~~~~~~~

    public void deleteAllTasks();

    public void deleteAllEpics();

    public void deleteAllSubtasks();

    //~~~~~~~~~ Получить по идентификатору + ~~~~~~~~~~~

    public Task getTaskById(long id);

    public Epic getEpicByID(long id);

    public Subtask getSubtaskByID(long id);

    //~~~~~~~~~ Создать задачу + ~~~~~~~~~~~

    public void createTask(Task task);

    public void createEpic(Epic epic);

    public void createSubtask(Subtask subtask);

    //~~~~~~~~~ Обновить задачу + ~~~~~~~~~~~

    public void updateTask(Task task);

    public void updateEpic(Epic epic);

    public void updateSubtask(Subtask subtask);

    //~~~~~~~~~ Удалить по идентификатору + ~~~~~~~~~~~

    public void deleteByIDTask(long id);

    public void deleteByIDEpic(long id);

    public void deleteByIDSubtask(long id);

    //~~~~~~~~~~~~~~~~ История ~~~~~~~~~~~~~~~~~~~~~~~~~

    public void printHistory();

}

