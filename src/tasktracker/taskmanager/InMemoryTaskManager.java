package tasktracker.taskmanager;

import historymanager.HistoryManager;
import tasktracker.TaskStatus;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;
import historymanager.InMemoryHistoryManager;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;


public class InMemoryTaskManager implements TaskManager{
    protected static int incrementalId; //сквозной счётчик-генератор ID задач, не противоречит ТЗ
    protected static HashMap<Long, Task> taskMap;
    protected static HashMap<Long, Epic> epicMap;
    protected static HashMap<Long, Subtask> subtaskMap;
    protected static HistoryManager history;

    public InMemoryTaskManager() {
        taskMap = new HashMap<>();
        epicMap = new HashMap<>();
        subtaskMap = new HashMap<>();
        //this.history = historyManager;
        this.history = Managers.getHistoryManager();
    }

    //~~~~~~~~~ Получить список задач + ~~~~~~~~~~~
    @Override
    public ArrayList<Task> getTaskMap() {      //Получить список всех тасков
        return new ArrayList<>(taskMap.values());
    }
    @Override
    public ArrayList<Epic> getEpicMap() {      //Получить список всех эпиков
        return new ArrayList<>(epicMap.values());
}
    @Override
    public ArrayList<Subtask> getSubtaskMap() {   //Получить список всех субтасков
        return new ArrayList<>(subtaskMap.values());
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

        for (Epic epic : epicMap.values()) {
            epic.getSubtasks().clear();
            epic.setStatus(TaskStatus.NEW);
        }
    }

    //~~~~~~~~~ Получить по идентификатору + ~~~~~~~~~~~
    @Override
    public Task getTaskById(long id) {      //Получить по идентификатору таск
        Task task = taskMap.get(id);
        if(task != null) {
            history.add(task);
        }
        return task;
    }
    @Override
    public Epic getEpicByID(long id) {      //Получить по идентификатору эпик
        Epic epic = epicMap.get(id);
        if (epic != null) {
            history.add(epic);
        }
        return epic;
    }
    @Override
    public Subtask getSubtaskByID(long id) {   //Получить по идентификатору субтаск
        Subtask subtask = subtaskMap.get(id);
        if (subtask != null) {
            history.add(subtask);
        }
        return subtask;
    }

    //~~~~~~~~~ Создать задачу + ~~~~~~~~~~~
    @Override
    public void createTask(Task task){       //Создать таск
        long id = getCode();
        task.setId(id);
        taskMap.put(id, task);
    }
    @Override
    public void createEpic(Epic epic){       //Создать эпик
        long id = getCode();
        epic.setId(id);
        changeEpicStatus(epic);
        epicMap.put(id, epic);
    }
    @Override
    public void createSubtask(Subtask subtask){    //Создать субтаск
        if (epicMap.containsKey(subtask.getEpicID())) {
            long id = getCode();
            subtask.setId(id);
            subtaskMap.put(id, subtask);

            Epic currentEpic = epicMap.get(subtask.getEpicID());
            currentEpic.addEpicList(subtask);

            changeEpicStatus(currentEpic);
        }
    }

    //~~~~~~~~~ Обновить задачу + ~~~~~~~~~~~
    @Override
    public void updateTask(Task task){       //Обновить таск
        if (taskMap.containsKey(task.getId())) {
            taskMap.put(task.getId(), task);
        }
    }
    @Override
    public void updateEpic(Epic epic){       //Обновить эпик
        if (epicMap.containsKey(epic.getId())) {
            if (epicMap.get(epic.getId()).getStatus() != epic.getStatus()) { //нельзя обновить статус эпика
                return;
            } else {
                epicMap.put(epic.getId(), epic);
            }
        }
    }
    @Override
    public void updateSubtask(Subtask subtask){    //Обновить субтаск
        if (subtaskMap.containsKey(subtask.getId())) {
            subtaskMap.put(subtask.getId(), subtask);
            updateSubtasksInEpic(epicMap.get(subtask.getEpicID()), subtask);
            changeEpicStatus(epicMap.get(subtask.getEpicID()));
        }
    }

    private void updateSubtasksInEpic(Epic epic, Subtask subtask) {
        //List<Subtask> subtasks = epic.getSubtasks().;
        for (int i=0; i < epic.getSubtasks().size(); i++) {
            if (epic.getSubtasks().get(i).getId() == subtask.getId()) {
                epic.getSubtasks().remove(i);
                epic.getSubtasks().add(subtask);
                break;
            }
        }
        //epic.getSubtasks();
    }
    //~~~~~~~~~ Удалить по идентификатору + ~~~~~~~~~~~
    @Override
    public void deleteByIDTask(long id){       //Удалить по идентификатору таск
        taskMap.remove(id);
        history.remove(id);
    }
    @Override
    public void deleteByIDEpic(long id){       //Удалить по идентификатору эпик
        ArrayList<Subtask> sbEpic = epicMap.get(id).getSubtasks();
        for (Subtask sb : sbEpic) {
            subtaskMap.remove(sb.getId());
            history.remove(sb.getId());
        }
        epicMap.remove(id);
        history.remove(id);
    }
    @Override
    public void deleteByIDSubtask(long id){    //Удалить по идентификатору субтаск
        Subtask subtask = subtaskMap.get(id);
        long epicID = subtask.getEpicID();
        epicMap.get(epicID).getSubtasks().remove(subtask);//удалить субтаск из эпика
        changeEpicStatus(epicMap.get(epicID));  //обновить статус эпика
        subtaskMap.remove(id);
        history.remove(id);
    }

    //~~~~~~~~~~~~~~~~ История ~~~~~~~~~~~~~~~~~~~~~~~~~

    public List<Task> history() {
        return history.getHistory();
    }

    //~~~~~~~~~ДОПОЛНИТЕЛЬНЫЕ МЕТОДЫ ~~~~~~~~~~~

    private static int getCode(){
        return ++incrementalId;
    }


    private void changeEpicStatus(Epic epic) { //метод обновления статуса эпика
        if (epic.getSubtasks().isEmpty()) { //если у эпика не осталось подзадач,
            epic.setStatus(TaskStatus.NEW); //то эпик переходит в статус NEW
        } else {
            boolean isDone = true;
            boolean isNew = true;
            for (Subtask st : epic.getSubtasks()) {
                if (st.getStatus() != TaskStatus.DONE) {
                    isDone = false;
                }
                if (st.getStatus() != TaskStatus.NEW) {
                    isNew = false;
                }
            }
            if (isDone) {
                epic.setStatus(TaskStatus.DONE);
            } else if (isNew) {
                epic.setStatus(TaskStatus.NEW);
            } else epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }
    @Override
    public void printHistory() { //этот метод тоже для тестирования
        history.print();
    }
}
