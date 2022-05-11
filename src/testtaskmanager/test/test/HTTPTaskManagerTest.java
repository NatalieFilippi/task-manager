package test.test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import httpserver.KVTaskClient;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import httpserver.HttpTaskServer;
import httpserver.KVServer;
import org.junit.jupiter.api.*;
import tasktracker.TaskStatus;
import tasktracker.taskmanager.HTTPTaskManager;
import tasktracker.taskmanager.Managers;
import tasktracker.taskmanager.TaskManager;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

class HTTPTaskManagerTest {

    private static Task task1 = new Task("Task 1", "Test 1 description", TaskStatus.NEW,
                                       Duration.ofHours(5).plus(Duration.ofMinutes(20)),
            LocalDateTime.of(2022, Month.NOVEMBER,14,12,00));
    private static Epic epic1 = new Epic("Epic1", "Test 1 description");
    private static Subtask s1 = new Subtask("Subtask 1", "subtask test 1", TaskStatus.NEW, 1);
    HttpClient client = HttpClient.newHttpClient();
    Gson gson = new Gson();
    private static KVServer kvServer;
    private HttpTaskServer httpTaskServer;
    private KVTaskClient kvTaskClient;

    @BeforeEach
    void beforeEach() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        httpTaskServer = new HttpTaskServer();
        kvTaskClient = new KVTaskClient("http://localhost:8078/");
    }

    @AfterEach
    void afterEach() throws IOException {
        kvServer.stop();
        httpTaskServer.stop();
    }

    @Test
    void checkTask() throws IOException, InterruptedException {
        System.out.println("Тестируем таски");
        //проверяем POST
        URI url = URI.create("http://localhost:8080/tasks/task/");
        String json = gson.toJson(task1);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        task1.setId(1);
        String result = kvTaskClient.load("tasks");
        Task taskResult = gson.fromJson(JsonParser.parseString(result).getAsString(), Task.class);
        assertEquals(taskResult, task1);

        //проверяем GET
        url = URI.create("http://localhost:8080/tasks/task/?id=1");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task getTask = gson.fromJson(response.body(),Task.class);
        assertEquals(getTask, task1);
        url = URI.create("http://localhost:8080/tasks/task/");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type type = new TypeToken<List<Task>>(){}.getType();
        List<Task> taskList = gson.fromJson(response.body(),type);
        assertEquals(taskList.toArray().length, 1);

        //проверяем DELETE
        url = URI.create("http://localhost:8080/tasks/task/?id=1");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        result = kvTaskClient.load("tasks");
        assertEquals(result, "\"\"");
    }

    @Test
    void checkEpic() throws IOException, InterruptedException {
        System.out.println("Тестируем эпики");
        //проверяем POST
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(epic1);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        epic1.setId(1);
        epic1.setStatus(TaskStatus.NEW);

        String result = kvTaskClient.load("epics");
        Epic epicResult = gson.fromJson(JsonParser.parseString(result).getAsString(), Epic.class);
        assertEquals(epicResult, epic1);

        //проверяем GET
        url = URI.create("http://localhost:8080/tasks/epic/?id=1");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic getEpic = gson.fromJson(response.body(),Epic.class);
        assertEquals(getEpic, epic1);
        url = URI.create("http://localhost:8080/tasks/epic/");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type type = new TypeToken<List<Epic>>(){}.getType();
        List<Epic> epicList = gson.fromJson(response.body(),type);
        assertEquals(epicList.toArray().length, 1);

        //проверяем DELETE
        url = URI.create("http://localhost:8080/tasks/epic/?id=1");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        result = kvTaskClient.load("epics");
        assertEquals(result, "\"\"");
    }

    @Test
    void checkSubtask() throws IOException, InterruptedException {
        System.out.println("Тестируем сабтаски");
        //проверяем POST
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(epic1);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/subtask/");
        json = gson.toJson(s1);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        s1.setId(2);

        String result = kvTaskClient.load("subtasks");
        Subtask subtaskResult = gson.fromJson(JsonParser.parseString(result).getAsString(), Subtask.class);
        assertEquals(subtaskResult, s1);

        //проверяем GET
        url = URI.create("http://localhost:8080/tasks/subtask/?id=2");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Subtask getSubtask = gson.fromJson(response.body(),Subtask.class);
        assertEquals(getSubtask, s1);
        url = URI.create("http://localhost:8080/tasks/subtask/");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type type = new TypeToken<List<Subtask>>(){}.getType();
        List<Subtask> subtaskList = gson.fromJson(response.body(),type);
        assertEquals(subtaskList.toArray().length, 1);

        //проверяем DELETE
        url = URI.create("http://localhost:8080/tasks/subtask/?id=2");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        result = kvTaskClient.load("subtasks");
        assertEquals(result, "\"\"");
    }

    @Test
    void checkHistory() throws IOException, InterruptedException {
        TaskManager taskManager = Managers.getDefault();

        taskManager.createEpic(epic1);
        taskManager.createSubtask(s1);
        taskManager.createTask(task1);

        taskManager.getTaskById(3);
        taskManager.getEpicByID(1);
        taskManager.getSubtaskByID(2);

        String resultHistory = kvTaskClient.load("history");
        assertEquals(resultHistory, "3,1,2");

        URI url = URI.create("http://localhost:8080/tasks/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type type = new TypeToken<List<Task>>(){}.getType();
        List<Task> historyList = gson.fromJson(response.body(),type);
        assertEquals(historyList.size(), taskManager.history().size());
    }

}