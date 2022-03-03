package tasktracker.tasks;

import tasktracker.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends Task{

    private ArrayList<Subtask> subtasks;  //список субтасков эпика

    public Epic(String name, String detail, TaskStatus status) {
        super(name, detail, status);
        subtasks = new ArrayList<>();
    }

    @Override
    public String toString() {
        return  "Main.Epic{" +
                "name='" + name + '\'' +
                ", detail='" + detail + '\'' +
                ", status='" + status + '\'' +
                ", subtasks=" + subtasks + '}' + "\n";
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

    public void setEpicList(Subtask subtask) {
        this.subtasks.add(subtask);
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<Subtask>(subtasks);
    }
}
