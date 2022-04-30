package tasktracker.tasks;


import tasktracker.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task{

    private long epicID;  //ID эпика

    public Subtask(String name, String detail, TaskStatus status, long epicID) {
        super(name, detail, status);
        this.epicID = epicID;
    }

    @Override
    public String toString() {
        return  "Main.Subtask{" +
                "name='" + getName() + '\'' +
                ", detail='" + getDetail() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", epicID=" + epicID + '\'' +
                ", startTime='" + getStartTime() + '\'' +
                ", duration='" + getDuration() + '}' +"\n";
    }

    @Override
    public int hashCode() {

        int hash = 17;
        if (getName() != null) {
            hash = hash + getName().hashCode();
        }
        hash = hash * 31;

        if (getDetail() != null) {
            hash = hash + getDetail().hashCode();
        }

        if (getStatus() != null) {
            hash = hash + getStatus().hashCode();
        }
        return hash + (int) epicID;
    }

    @Override
    public boolean equals(Object o) { // добавили и переопределили equals
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subtask subtask = (Subtask) o;
        return Objects.equals(getName(), subtask.getName()) &&
                Objects.equals(getDetail(), subtask.getDetail()) &&
                Objects.equals(getStatus(), subtask.getStatus()) &&
                Objects.equals(getId(), subtask.getId()) &&
                Objects.equals(getEpicID(), subtask.getEpicID()) &&
                Objects.equals(getStartTime(), subtask.getStartTime()) &&
                Objects.equals(getDuration(), subtask.getDuration());
    }

    public long getEpicID() {
        return epicID;
    }

    @Override
    public String stringToFile() {
        String start = "-";
        if (getStartTime() != null) {
            start = getStartTime().toString();
        };
        long duration = 0;
        if (getDuration() != null) {
            duration = getDuration().getSeconds();
        };
        String end = "-";
        if (getEndTime() != null) {
            end = getEndTime().toString();
        };
        return  String.format("%d,%s,%s,%s,%s,%d,%s,%d,%s\n", getId(),"SUBTASK",getName(),getStatus().toString(),
                getDetail(),getEpicID(),start,duration,end);

    }

    public static Subtask fromFile(String line) {

        String[] split = line.split(",");

        Subtask newSubtask = new Subtask(split[2], split[4], TaskStatus.getStatus(split[3]),Long.parseLong(split[5]));
        newSubtask.setId(Long.parseLong(split[0]));
        if (!split[6].equals("-"))  {
            newSubtask.setStartTime(LocalDateTime.parse(split[6]));
        }
        if (!split[7].equals("0")) {
            newSubtask.setDuration(Duration.ofSeconds(Long.parseLong(split[7])));
        }
        return newSubtask;

    }
}
