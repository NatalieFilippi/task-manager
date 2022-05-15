package httpserver;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import tasktracker.taskmanager.Managers;
import tasktracker.taskmanager.TaskManager;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private final HttpServer httpServer;
    private static Gson gson = new Gson();
    private static TaskManager taskManager;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public HttpTaskServer() throws IOException {
        taskManager = Managers.getDefault();
        this.httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks/task", new TaskHandler());
        httpServer.createContext("/tasks/epic", new EpicHandler());
        httpServer.createContext("/tasks/subtask", new SubtaskHandler());

        httpServer.createContext("/tasks/history", new History());

        System.out.println("Запускаем сервер на порту " + PORT);
        httpServer.start(); // запускаем сервер
    }


    class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            String response = "";
            String path = httpExchange.getRequestURI().toString();
            String method = httpExchange.getRequestMethod();

            switch (method) {
                case "GET":
                    if (path.contains("=")) {
                        long id = Long.parseLong(path.split("=")[1]);
                        response = gson.toJson(taskManager.getTaskById(id));
                    } else {
                        response = gson.toJson(taskManager.getTaskMap());
                    }
                    break;
                case "POST":
                    Task task = gson.fromJson(new String(httpExchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET), Task.class);
                    if (path.contains("=")) {
                        taskManager.updateTask(task);
                    } else {
                        taskManager.createTask(task);
                    }
                    break;

                case "DELETE":
                    if (path.contains("=")) {
                        long id = Long.parseLong(path.split("=")[1]);
                        taskManager.deleteByIDTask(id);
                    } else {
                        taskManager.deleteAllTasks();
                    }

                    break;
                default:
                    response = "Не удалось идентифицировать запрос.";
                    //throw new IllegalArgumentException("Не удалось идентифицировать запрос.");
            }
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
    class EpicHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String response = "";
            String path = httpExchange.getRequestURI().toString();
            String method = httpExchange.getRequestMethod();

            switch (method) {
                case "GET":
                    if (path.contains("=")) {
                        long id = Long.parseLong(path.split("=")[1]);
                        response = gson.toJson(taskManager.getEpicByID(id));
                    } else {
                        response = gson.toJson(taskManager.getEpicMap());
                    }
                    break;
                case "POST":
                    Epic epic = gson.fromJson(new String(httpExchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET), Epic.class);
                    if (path.contains("=")) {
                        taskManager.updateEpic(epic);
                    } else {
                        taskManager.createEpic(epic);
                    }
                    break;

                case "DELETE":
                    if (path.contains("=")) {
                        long id = Long.parseLong(path.split("=")[1]);
                        taskManager.deleteByIDEpic(id);
                    } else {
                        taskManager.deleteAllEpics();
                    }

                    break;
                default:
                    response = "Не удалось идентифицировать запрос.";
                    //throw new IllegalArgumentException("Не удалось идентифицировать запрос.");
            }
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    class SubtaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String response = "";
            String path = httpExchange.getRequestURI().toString();
            String method = httpExchange.getRequestMethod();

            switch (method) {
                case "GET":
                    if (path.contains("=")) {
                        long id = Long.parseLong(path.split("=")[1]);
                        response = gson.toJson(taskManager.getSubtaskByID(id));
                    } else {
                        response = gson.toJson(taskManager.getSubtaskMap());
                    }
                    break;
                case "POST":
                    Subtask subtask = gson.fromJson(new String(httpExchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET), Subtask.class);
                    if (path.contains("=")) {
                        taskManager.updateSubtask(subtask);
                    } else {
                        taskManager.createSubtask(subtask);
                    }
                    break;

                case "DELETE":
                    if (path.contains("=")) {
                        long id = Long.parseLong(path.split("=")[1]);
                        taskManager.deleteByIDSubtask(id);
                    } else {
                        taskManager.deleteAllSubtasks();
                    }

                    break;
                default:
                    response = "Не удалось идентифицировать запрос.";
                    //throw new IllegalArgumentException("Не удалось идентифицировать запрос.");
            }
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    class History implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            String response = "";
            response = gson.toJson(taskManager.history());
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new HttpTaskServer();
    }

    public void stop() {
        System.out.println("Останавливаем сервер на порту " + PORT);
        httpServer.stop(1);
    }
}

