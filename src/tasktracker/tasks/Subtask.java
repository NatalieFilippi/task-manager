package tasktracker.tasks;


import tasktracker.TaskStatus;

public class Subtask extends Task{

    private int epicID;  //ID эпика

    public Subtask(String name, String detail, TaskStatus status, int epicID) {
        super(name, detail, status);
        this.epicID = epicID;
    }

    @Override
    public String toString() {
        return  "Main.Subtask{" +
                "name='" + name + '\'' +
                ", detail='" + detail + '\'' +
                ", status='" + status + '\'' +
                ", epicID=" + epicID + '}' + "\n";
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
        return hash + epicID;
    }

    public int getEpicID() {
        return epicID;
    }
}
