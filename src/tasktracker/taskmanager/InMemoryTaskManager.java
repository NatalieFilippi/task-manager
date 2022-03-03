package tasktracker.taskmanager;

import tasktracker.TaskStatus;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;
import historymanager.InMemoryHistoryManager;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager{
    private int incrementalId; //сквозной счётчик-генератор ID задач, не противоречит ТЗ
    private HashMap<Integer, Task> taskMap;
    private HashMap<Integer, Epic> epicMap;
    private HashMap<Integer, Subtask> subtaskMap;
    private InMemoryHistoryManager historyList;

    public InMemoryTaskManager() {
        incrementalId = 0;
        taskMap = new HashMap<>();
        epicMap = new HashMap<>();
        subtaskMap = new HashMap<>();
        historyList = new InMemoryHistoryManager();
    }

    //~~~~~~~~~ Получить список задач + ~~~~~~~~~~~
    @Override
    public HashMap<Integer, Task> getTaskMap(){      //Получить список всех тасков
        return new HashMap<Integer, Task>(taskMap);
    }
    @Override
    public HashMap<Integer, Epic> getEpicMap(){      //Получить список всех эпиков
        return new HashMap<Integer, Epic>(epicMap);
    }
    @Override
    public HashMap<Integer, Subtask> getSubtaskMap(){   //Получить список всех субтасков
        return new HashMap<Integer, Subtask>(subtaskMap);
    }

    //~~~~~~~~~ Удалить все задачи + ~~~~~~~~~~~
    @Override
    public void deleteAllTasks(){       //Удалить все таски
        taskMap.clear();
    }
    @Override
    public void deleteAllEpics(){       //Удалить все эпики
        epicMap.clear();
        deleteAllSubtasks();
    }
    @Override
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
    @Override
    public Task getTaskById(int id){      //Получить по идентификатору таск
        historyList.add(taskMap.get(id));
        return taskMap.get(id);
    }
    @Override
    public Epic getEpicByID(int id){      //Получить по идентификатору эпик
        historyList.add(epicMap.get(id));
        return epicMap.get(id);
    }
    @Override
    public Subtask getSubtaskByID(int id){   //Получить по идентификатору субтаск
        historyList.add(subtaskMap.get(id));
        return subtaskMap.get(id);
    }

    //~~~~~~~~~ Создать задачу + ~~~~~~~~~~~
    @Override
    public void createTask(Task task){       //Создать таск
        int id = getCode();
        task.setId(id);
        taskMap.put(id, task);
    }
    @Override
    public void createEpic(Epic epic){       //Создать эпик
        int id = getCode();
        epic.setId(id);
        epicMap.put(id, epic);
    }
    @Override
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
    @Override
    public void updateTask(Task task){       //Обновить таск
        taskMap.put(task.getId(), task);
    }
    @Override
    public void updateEpic(Epic epic){       //Обновить эпик
        if (epicMap.get(epic.getId()).getStatus() != epic.getStatus()) { //нельзя обновить статус эпика
            return;
        } else {
            epicMap.put(epic.getId(), epic);
        }
    }
    @Override
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
    @Override
    public void deleteByIDTask(int id){       //Удалить по идентификатору таск
        taskMap.remove(id);
    }
    @Override
    public void deleteByIDEpic(int id){       //Удалить по идентификатору эпик
        ArrayList<Subtask> sbEpic = epicMap.get(id).getSubtasks();
        for (Subtask sb : sbEpic) {
            subtaskMap.remove(sb.getId());
        }
        epicMap.remove(id);
    }
    @Override
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

    //~~~~~~~~~~~~~~~~ История ~~~~~~~~~~~~~~~~~~~~~~~~~
    /* @Override
    public List<Task> history() {
        return new ArrayList<Task>(historyList);
    }*/
    @Override
    public void printHistory() {
        historyList.print();
    }

    //~~~~~~~~~ДОПОЛНИТЕЛЬНЫЕ МЕТОДЫ ~~~~~~~~~~~

    private int getCode(){

        return ++incrementalId;

    }

}
