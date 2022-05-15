package test.test;

import tasktracker.taskmanager.InMemoryTaskManager;
import test.test.TaskManagerTest;

import java.io.IOException;

public class InMemoryTaskManagerTest extends TaskManagerTest {

    public InMemoryTaskManagerTest() throws IOException {
        super(new InMemoryTaskManager());
    }
}
