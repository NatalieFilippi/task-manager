package tasktracker.tasks;


import tasktracker.TaskStatus;

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
                ", epicID=" + epicID + '}' + "\n";
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
                Objects.equals(getEpicID(), subtask.getEpicID());
    }

    public long getEpicID() {
        return epicID;
    }

    @Override
    public String stringToFile() {

        return  String.format("%d,%s,%s,%s,%s,%d\n", getId(),"SUBTASK",getName(),getStatus().toString(),getDetail(),getEpicID());

    }

    public static Subtask fromFile(String line) {

        String[] split = line.split(",");

        Subtask newSubtask = new Subtask(split[2], split[4], TaskStatus.getStatus(split[3]),Long.parseLong(split[5]));
        newSubtask.setId(Long.parseLong(split[0]));
        return newSubtask;

    }
}
