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

    public String stringToFile() {

        return  String.format("%d,%s,%s,%s,%s\n", getId(),"TASK",getName(),getStatus().toString(),getDetail());

    }

    public static Task fromFile(String line) {

        String[] split = line.split(",");

        Task newTask = new Task(split[2], split[4], TaskStatus.getStatus(split[3]));
        newTask.setId(Long.parseLong(split[0]));
        return newTask;

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

