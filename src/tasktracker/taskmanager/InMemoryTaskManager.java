package tasktracker.taskmanager;

import tasktracker.TaskStatus;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;
import historymanager.InMemoryHistoryManager;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;


public class InMemoryTaskManager implements TaskManager{
    private int incrementalId; //сквозной счётчик-генератор ID задач, не противоречит ТЗ
    private HashMap<Long, Task> taskMap;
    private HashMap<Long, Epic> epicMap;
    private HashMap<Long, Subtask> subtaskMap;
    private InMemoryHistoryManager history;

    public InMemoryTaskManager() {
        incrementalId = 0;
        taskMap = new HashMap<>();
        epicMap = new HashMap<>();
        subtaskMap = new HashMap<>();
        history = new InMemoryHistoryManager();
    }

    //~~~~~~~~~ Получить список задач + ~~~~~~~~~~~
    @Override
    public ArrayList<Task> getTaskMap(){      //Получить список всех тасков
        ArrayList<Task> tasks = new ArrayList<>();
        for (Task value : taskMap.values()) {
            tasks.add(value);
        }
        return tasks;
    }
    @Override
    public ArrayList<Epic> getEpicMap(){      //Получить список всех эпиков
        ArrayList<Epic> epics = new ArrayList<>();
        for (Epic value : epicMap.values()) {
            epics.add(value);
        }
        return epics;
}
    @Override
    public ArrayList<Subtask> getSubtaskMap(){   //Получить список всех субтасков
        ArrayList<Subtask> subtasks = new ArrayList<>();
        for (Subtask value : subtaskMap.values()) {
            subtasks.add(value);
        }
        return subtasks;
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
        for (Long i : epicMap.keySet()) {
           stList = epicMap.get(i).getSubtasks();
           epicMap.get(i).setStatus(TaskStatus.NEW); // если у эпика нет подзадач, то он новый
           stList.clear();
        }
    }

    //~~~~~~~~~ Получить по идентификатору + ~~~~~~~~~~~
    @Override
    public Task getTaskById(long id){      //Получить по идентификатору таск
        history.add(taskMap.get(id));
        return taskMap.get(id);
    }
    @Override
    public Epic getEpicByID(long id){      //Получить по идентификатору эпик
        history.add(epicMap.get(id));
        return epicMap.get(id);
    }
    @Override
    public Subtask getSubtaskByID(long id){   //Получить по идентификатору субтаск
        history.add(subtaskMap.get(id));
        return subtaskMap.get(id);
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
        epicMap.put(id, epic);
    }
    @Override
    public void createSubtask(Subtask subtask){    //Создать субтаск
        long id = getCode();
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
        changeEpicStatus(epicMap.get(subtask.getEpicID()));
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

    private int getCode(){
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
