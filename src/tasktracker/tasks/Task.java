package tasktracker.tasks;

import tasktracker.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.*;

public class Task implements Comparable<Task>{
    private String name;      //название задачи
    private String detail;    //описание задачи
    private TaskStatus status; //статус задачи
    private long id;           //id задачи, исправила
    private Duration duration;
    private LocalDateTime startTime;

    public Task(String name, String detail, TaskStatus status, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.detail = detail;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String name, String detail, TaskStatus status) {
        this.name = name;
        this.detail = detail;
        this.status = status;
        //ID присвоит менеджер
        this.duration = Duration.ZERO;
    }

    public Task(String name, String detail) {
        this.name = name;
        this.detail = detail;
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
    public boolean equals(Object o) { // добавили и переопределили equals
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name) &&
                Objects.equals(detail, task.detail) &&
                Objects.equals(status, task.status) &&
                Objects.equals(id, task.id) &&
                Objects.equals(startTime, task.startTime) &&
                Objects.equals(duration, task.duration);
    }

    @Override
    public String toString() {

        return  "Main.Task{" +
                "name='" + getName() + '\'' +
                ", detail='" + getDetail() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", startTime='" + getStartTime() + '\'' +
                ", duration='" + getDuration() + '}' +"\n";

    }

    public String stringToFile() {
        String start = "-";
        if (getStartTime() != null) {
            start = getStartTime().toString();
        }
        long duration = 0;
        if (getDuration() != null) {
            duration = getDuration().getSeconds();
        }
        String end = "-";
        if (getEndTime() != null) {
            end = getEndTime().toString();
        }

        return  String.format("%d,%s,%s,%s,%s,%s,%d,%s\n", getId(),"TASK",getName(),getStatus().toString(),
                getDetail(),start,duration,end);

    }

    public static Task fromFile(String line) {

        String[] split = line.split(",");

        Task newTask = new Task(split[2], split[4], TaskStatus.valueOf(split[3]));
        newTask.setId(Long.parseLong(split[0]));
        if (!split[5].equals("-"))  {
            newTask.setStartTime(LocalDateTime.parse(split[5]));
        }
        if (!split[6].equals("0")) {
            newTask.setDuration(Duration.ofSeconds(Long.parseLong(split[6])));
        }

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

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) {
           return null;
        }
        return startTime.plus(duration);
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    @Override
    public int compareTo(Task o) {

        if (this.getStartTime() == null || o.getStartTime() == null) {
            return o.getStartTime() != null ? 1 : (this.getStartTime() != null ? -1 : 0);
        }
        return this.getStartTime().compareTo(o.getStartTime());
    }
}

