package tasktracker.tasks;

import tasktracker.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends Task{

    private ArrayList<Subtask> subtasks;  //список субтасков эпика

    public Epic(String name, String detail) {
        super(name, detail);
        subtasks = new ArrayList<>();
        setStatus(TaskStatus.NEW);
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

    @Override
    public String stringToFile() {

        return  String.format("%d,%s,%s,%s,%s\n", getId(),"EPIC",getName(),getStatus().toString(),getDetail());

    }

    public static Epic fromFile(String line) {

        String[] split = line.split(",");

        Epic newEpic = new Epic(split[2], split[4]);
        newEpic.setId(Long.parseLong(split[0]));
        newEpic.setStatus(TaskStatus.getStatus(split[3]));
        return newEpic;

    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }
}
