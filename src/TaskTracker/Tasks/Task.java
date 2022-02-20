package TaskTracker.Tasks;

import TaskTracker.TaskStatus;

public class Task {
    protected String name;      //название задачи
    protected String detail;    //описание задачи
    protected TaskStatus status;    //статус задачи
    protected int id;           //id задачи

    public Task(String name, String detail, TaskStatus status) {
        this.name = name;
        this.detail = detail;
        this.status = status;
        //ID присвоит менеджер
    }

    @Override
    public int hashCode() {

        int hash = 17;
        if (name != null) {
            hash = hash + name.hashCode();
        }
        hash = hash * 31;

        if (detail != null) {
            hash = hash + detail.hashCode();
        }

        if (status != null) {
            hash = hash + status.hashCode();
        }
        return hash;
    }

    @Override
    public String toString() {

        return  "Main.Task{" +
                "name='" + name + '\'' +
                ", detail='" + detail + '\'' +
                ", status='" + status + '}' + "\n";

    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return this.status;
    }

    public int getId() {
        return id;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}

