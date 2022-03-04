package tasktracker.tasks;


import tasktracker.TaskStatus;

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

    public long getEpicID() {
        return epicID;
    }
}
