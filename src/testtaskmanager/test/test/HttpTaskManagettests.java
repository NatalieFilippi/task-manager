package test.test;

import httpserver.KVServer;
import tasktracker.taskmanager.HTTPTaskManager;
import tasktracker.taskmanager.TaskManager;

import java.io.IOException;

class HttpTaskManagettests extends TaskManagerTest<HTTPTaskManager>{

    private static KVServer kvServer;

    public HttpTaskManagettests() throws IOException {
        super(new HTTPTaskManager("http://localhost:8078/"));
    }
}
