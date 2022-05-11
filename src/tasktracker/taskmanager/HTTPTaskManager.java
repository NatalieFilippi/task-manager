package tasktracker.taskmanager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import httpserver.KVTaskClient;
import tasktracker.taskmanager.FileBackedTasksManager;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HTTPTaskManager extends FileBackedTasksManager {
    private KVTaskClient client;
    private static Gson gson = new Gson();

    public HTTPTaskManager(String url) {
        super(url);
        this.client = new KVTaskClient(url);
    }

    @Override
    protected void save() {
        String json;

        json = "";
        for (Task task : taskMap.values()) {
            json += gson.toJson(task);
        }
        client.put("tasks", gson.toJson(json));



        json = "";
        for (Epic epic : epicMap.values()) {
            json += gson.toJson(epic);

        }
        client.put("epics", gson.toJson(json));



        json = "";
        for (Subtask subtask : subtaskMap.values()) {
            json += gson.toJson(subtask);
        }
        client.put("subtasks", gson.toJson(json));


        client.put("history", history.stringToFile());

    }

    private void loadFromServer() {
        String json = client.load("tasks");
        if (json != null) {
            Type type = new TypeToken<List<Task>>(){}.getType();
            List<Task> taskList = gson.fromJson(json,type);
            for (Task task : taskList) {
                createTask(task);
            }
        }

        json = client.load("epics");
        if (json != null) {
            Type type = new TypeToken<List<Epic>>(){}.getType();
            List<Epic> taskList = gson.fromJson(json,type);
            for (Epic epic : taskList) {
                createEpic(epic);
            }
        }

        json = client.load("subtasks");
        if (json != null) {
            Type type = new TypeToken<List<Subtask>>(){}.getType();
            List<Subtask> taskList = gson.fromJson(json,type);
            for (Subtask subtask : taskList) {
                createSubtask(subtask);
            }
        }

        json = client.load("history");
        if (json != null) {
            historyFromString(json);
        }
    }

    @Override
    public ArrayList<Task> getTaskMap() {
        return super.getTaskMap();
    }

    @Override
    public ArrayList<Epic> getEpicMap() {
        return super.getEpicMap();
    }

    @Override
    public ArrayList<Subtask> getSubtaskMap() {
        return super.getSubtaskMap();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
    }

    @Override
    public Task getTaskById(long id) {
        return super.getTaskById(id);
    }

    @Override
    public Epic getEpicByID(long id) {
        return super.getEpicByID(id);
    }

    @Override
    public Subtask getSubtaskByID(long id) {
        return super.getSubtaskByID(id);
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
    }

    @Override
    public void deleteByIDTask(long id) {
        super.deleteByIDTask(id);
    }

    @Override
    public void deleteByIDEpic(long id) {
        super.deleteByIDEpic(id);
    }

    @Override
    public void deleteByIDSubtask(long id) {
        super.deleteByIDSubtask(id);
    }

    @Override
    public List<Task> history() {
        return super.history();
    }
}
