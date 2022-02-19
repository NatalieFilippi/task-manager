public class Task {
    protected String name;      //название задачи
    protected String detail;    //описание задачи
    protected Status status;    //статус задачи


    public Task(String name, String detail, Status status) {
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

        return  "Task{" +
                "name='" + name + '\'' +
                ", detail='" + detail + '\'' +
                ", status=" + status + '}' + "\n";

    }
}
