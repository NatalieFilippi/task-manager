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
                "name='" + getName() + '\'' +
                ", detail='" + getDetail() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", subtasks=" + subtasks + '}' + "\n";
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
        return hash;
    }

    public void setEpicList(Subtask subtask) {
        this.subtasks.add(subtask);
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }
}
