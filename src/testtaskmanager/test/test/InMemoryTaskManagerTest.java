package test.test;

import tasktracker.taskmanager.InMemoryTaskManager;
import test.test.TaskManagerTest;

public class InMemoryTaskManagerTest extends TaskManagerTest {

    public InMemoryTaskManagerTest() {
        super(new InMemoryTaskManager());
    }
}
