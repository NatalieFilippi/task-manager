package tasktracker;

public enum TaskStatus {
    NEW,
    IN_PROGRESS,
    DONE;

    public static TaskStatus getStatus(String status) {
        switch (status) {
            case "NEW":
                return NEW;
            case "IN_PROGRESS":
                return IN_PROGRESS;
            case "DONE":
                return DONE;
            default:
                return null;
        }
    }
}
