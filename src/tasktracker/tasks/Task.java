package tasktracker.tasks;

import tasktracker.TaskStatus;

public class Task{
    private String name;      //название задачи
    private String detail;    //описание задачи
    private TaskStatus status; //статус задачи
    private long id;           //id задачи, исправила

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
                "name='" + getName() + '\'' +
                ", detail='" + getDetail() + '\'' +
                ", status='" + getStatus() + '}' + "\n";

    }

    public void setId(long id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return this.status;
    }

    public long getId() {
        return id;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getName() {
        return name;
    }

    public String getDetail() {
        return detail;
    }
}

