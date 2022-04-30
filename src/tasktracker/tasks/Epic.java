package tasktracker.tasks;

import tasktracker.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class Epic extends Task{

    private ArrayList<Subtask> subtasks;  //список субтасков эпика
    private LocalDateTime endTime;

    public Epic(String name, String detail) {
        super(name, detail);
        subtasks = new ArrayList<>();
    }

    @Override
    public String toString() {
        return  "Main.Epic{" +
                "name='" + getName() + '\'' +
                ", detail='" + getDetail() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", subtasks=" + subtasks + '\'' +
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
        return hash;
    }

    public void addEpicList(Subtask subtask) {
        this.subtasks.add(subtask);
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
        return  String.format("%d,%s,%s,%s,%s,%s,%d,%s\n", getId(),"EPIC",getName(),getStatus().toString(),
                getDetail(),start,duration,end);

    }

    public static Epic fromFile(String line) {

        String[] split = line.split(",");

        Epic newEpic = new Epic(split[2], split[4]);
        newEpic.setId(Long.parseLong(split[0]));
        newEpic.setStatus(TaskStatus.getStatus(split[3]));
        if (!split[5].equals("-"))  {
            newEpic.setStartTime(LocalDateTime.parse(split[5]));
        }
        if (!split[6].equals("0")) {
            newEpic.setDuration(Duration.ofSeconds(Long.parseLong(split[6])));
        }
        if (!split[7].equals("-"))  {
            newEpic.setEndTime(LocalDateTime.parse(split[7]));
        }
        return newEpic;

    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }



    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void timeCalculation() {

        timeCalculationStart();
        timeCalculationDuration();
        timeCalculationEnd();

    }

    private void timeCalculationStart() {

        LocalDateTime start = getStartTime();
        if (start != null) {
            for (Subtask subtask : subtasks) {
                if ((subtask.getStartTime() != null) && (subtask.getStartTime().isBefore(start))) {
                    start = subtask.getStartTime();
                }
            }
        } else {
            for (Subtask subtask : subtasks) {
                if (subtask.getStartTime() != null) {
                    start = subtask.getStartTime();
                    break;
                }
            }
        }
        if(start != null) {
            setStartTime(start);
        }
    }

    private void timeCalculationDuration() {
        Duration durationCalculate = Duration.ZERO;
        for (Subtask subtask : subtasks) {
            if (subtask.getDuration() != null) {
                durationCalculate = durationCalculate.plus(subtask.getDuration());
            }
        }
        setDuration(durationCalculate);
    }

    public void timeCalculationEnd() {

        LocalDateTime end = getEndTime();
        if (end != null) {
            for (Subtask subtask : subtasks) {
                if ((subtask.getEndTime() != null) && (subtask.getEndTime().isAfter(end))) {
                    end = subtask.getEndTime();
                }
            }
        } else {
            for (Subtask subtask : subtasks) {
                if (subtask.getEndTime() != null) {
                    end = subtask.getEndTime();
                    break;
                }
            }
        }
        if(end != null) {
            setEndTime(end);
        }
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

}
