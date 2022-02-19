import java.util.ArrayList;

public class Epic extends Task{

    private ArrayList<Subtask> subtaskList;  //список субтасков эпика

    public Epic(String name, String detail, Status status) {
        super(name, detail, status);
        subtaskList = new ArrayList<>();
    }

    @Override
    public String toString() {
        return  "Epic{" +
                "name='" + name + '\'' +
                ", detail='" + detail + '\'' +
                ", status=" + status + '\'' +
                ", subtaskList=" + subtaskList + '}' + "\n";
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
        this.subtaskList.add(subtask);
    }

    public ArrayList<Subtask> getSubtaskList() {
        return subtaskList;
    }
}
