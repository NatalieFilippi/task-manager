package tasktracker.taskmanager;

import tasktracker.TaskStatus;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int incrementalId; //сквозной счётчик-генератор ID задач, не противоречит ТЗ
    private HashMap<Integer, Task> taskMap;
    private HashMap<Integer, Epic> epicMap;
    private HashMap<Integer, Subtask> subtaskMap;

    public TaskManager() {
        incrementalId = 0;
        taskMap = new HashMap<>();
        epicMap = new HashMap<>();
        subtaskMap = new HashMap<>();
    }

    //~~~~~~~~~ Получить список задач + ~~~~~~~~~~~

    public HashMap<Integer, Task> getTaskMap(){      //Получить список всех тасков
        return new HashMap<Integer, Task>(taskMap);
    }

    public HashMap<Integer, Epic> getEpicMap(){      //Получить список всех эпиков
        return new HashMap<Integer, Epic>(epicMap);
    }

    public HashMap<Integer, Subtask> getSubtaskMap(){   //Получить список всех субтасков
        return new HashMap<Integer, Subtask>(subtaskMap);
    }

    //~~~~~~~~~ Удалить все задачи + ~~~~~~~~~~~

    public void deleteAllTasks(){       //Удалить все таски
        taskMap.clear();
    }

    public void deleteAllEpics(){       //Удалить все эпики
        epicMap.clear();
        deleteAllSubtasks();
    }

    public void deleteAllSubtasks(){    //Удалить все субтаски
        subtaskMap.clear();

        ArrayList<Subtask> stList = new ArrayList<>(); //удалить все субтаски из эпиков
        for (Integer i : epicMap.keySet()) {
           stList = epicMap.get(i).getSubtasks();
           epicMap.get(i).setStatus(TaskStatus.NEW); // если у эпика нет подзадач, то он новый
           stList.clear();
        }
    }

    //~~~~~~~~~ Получить по идентификатору + ~~~~~~~~~~~

    public Task getTaskById(int id){      //Получить по идентификатору таск
        return taskMap.get(id);
    }

    public Epic getEpicByID(int id){      //Получить по идентификатору эпик
        return epicMap.get(id);
    }

    public Subtask getSubtaskByID(int id){   //Получить по идентификатору субтаск
        return subtaskMap.get(id);
    }

    //~~~~~~~~~ Создать задачу + ~~~~~~~~~~~

    public void createTask(Task task){       //Создать таск
        int id = getCode();
        task.setId(id);
        taskMap.put(id, task);
    }

    public void createEpic(Epic epic){       //Создать эпик
        int id = getCode();
        epic.setId(id);
        epicMap.put(id, epic);
    }

    public void createSubtask(Subtask subtask){    //Создать субтаск
        int id = getCode();
        subtask.setId(id);
        subtaskMap.put(id, subtask);

        Epic currentEpic = epicMap.get(subtask.getEpicID());
        currentEpic.setEpicList(subtask);
        if (currentEpic.getStatus() == TaskStatus.DONE) {
            currentEpic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    //~~~~~~~~~ Обновить задачу + ~~~~~~~~~~~

    public void updateTask(Task task){       //Обновить таск
        taskMap.put(task.getId(), task);
    }

    public void updateEpic(Epic epic){       //Обновить эпик
        if (epicMap.get(epic.getId()).getStatus() != epic.getStatus()) { //нельзя обновить статус эпика
            return;
        } else {
            epicMap.put(epic.getId(), epic);
        }
    }

    public void updateSubtask(Subtask subtask){    //Обновить субтаск
        subtaskMap.put(subtask.getId(), subtask);

        Epic currentEpic = epicMap.get(subtask.getEpicID());

        if (subtask.getStatus() == TaskStatus.DONE) {
            ArrayList<Subtask> allSubtask = currentEpic.getSubtasks();
            boolean isDone = true;
            for (Subtask st : allSubtask) {
                if (st.getStatus() != TaskStatus.DONE) {
                    isDone = false;
                    return;
                }
            }
            if (isDone) {
                currentEpic.setStatus(TaskStatus.DONE);
            }
        } else if (subtask.getStatus() == TaskStatus.IN_PROGRESS) {
            currentEpic.setStatus(TaskStatus.IN_PROGRESS);
        } else if (subtask.getStatus() == TaskStatus.NEW) {
            //оставим возможность обнулять эпик, если обнулили сабтаск при условии, что остальные сабтаски новые
            ArrayList<Subtask> allSubtask = currentEpic.getSubtasks();
            boolean isNew = true;
            for (Subtask st : allSubtask) {
                if (st.getStatus() != TaskStatus.NEW) {
                    isNew = false;
                    return;
                }
            }
            if (isNew) {
                currentEpic.setStatus(TaskStatus.NEW);
            }
        }
    }

    //~~~~~~~~~ Удалить по идентификатору + ~~~~~~~~~~~

    public void deleteByIDTask(int id){       //Удалить по идентификатору таск
        taskMap.remove(id);
    }

    public void deleteByIDEpic(int id){       //Удалить по идентификатору эпик
        ArrayList<Subtask> sbEpic = epicMap.get(id).getSubtasks();
        for (Subtask sb : sbEpic) {
            subtaskMap.remove(sb.getId());
        }
        epicMap.remove(id);
    }

    public void deleteByIDSubtask(int id){    //Удалить по идентификатору субтаск
        Subtask subtask = subtaskMap.get(id);
        int epicID = subtask.getEpicID();
        ArrayList<Subtask> stList = epicMap.get(epicID).getSubtasks();
        stList.remove(subtask); //удалить субтаск из эпика
        if (stList.isEmpty()) { //если у эпика не осталось подзадач, то эпик переходит в статус NEW
            epicMap.get(epicID).setStatus(TaskStatus.NEW);
        } else {
            boolean isDone = true;
            boolean isNew = true;
            for (Subtask st : stList) {
                if (st.getStatus() != TaskStatus.DONE) {
                    isDone = false;
                }
                if (st.getStatus() != TaskStatus.NEW) {
                    isNew = false;
                }
            }
            if (isDone) {
                epicMap.get(epicID).setStatus(TaskStatus.DONE);
            } else if (isNew) {
                epicMap.get(epicID).setStatus(TaskStatus.NEW);
            } else epicMap.get(epicID).setStatus(TaskStatus.IN_PROGRESS);

        }
        subtaskMap.remove(id);
    }

    //~~~~~~~~~ДОПОЛНИТЕЛЬНЫЕ МЕТОДЫ ~~~~~~~~~~~

    private int getCode(){

        return ++incrementalId;

    }

}
