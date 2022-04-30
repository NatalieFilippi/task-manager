package tasktracker.taskmanager;

import historymanager.HistoryManager;
import tasktracker.TaskStatus;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;

import java.time.LocalDateTime;
import java.util.TreeMap;

import java.util.*;


public class InMemoryTaskManager implements TaskManager{
    protected static int incrementalId; //сквозной счётчик-генератор ID задач, не противоречит ТЗ
    protected static HashMap<Long, Task> taskMap;
    protected static HashMap<Long, Epic> epicMap;
    protected static HashMap<Long, Subtask> subtaskMap;
    protected static HistoryManager history;
    protected static TreeMap<LocalDateTime,Task> prioritizedTasks;
    protected static HashMap<Long,Task> prioritizedTasksNull;

    public InMemoryTaskManager() {
        taskMap = new HashMap<>();
        epicMap = new HashMap<>();
        subtaskMap = new HashMap<>();
        this.history = Managers.getHistoryManager();

        prioritizedTasks = new TreeMap<>();
        prioritizedTasksNull = new HashMap<>();
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
        for (Long id : taskMap.keySet()) {
            if (taskMap.get(id).getStartTime() != null) {
                if (prioritizedTasks.containsKey(taskMap.get(id).getStartTime())) {
                    prioritizedTasks.remove(taskMap.get(id).getStartTime());
                }
            }
            if (prioritizedTasksNull.containsKey(id)) {
                prioritizedTasksNull.remove(taskMap.get(id));
            }
            history.remove(id);
        }
        taskMap.clear();
    }
    @Override
    public void deleteAllEpics(){       //Удалить все эпики
       for (Long id : epicMap.keySet()) {
            history.remove(id);
            if (epicMap.get(id).getStartTime() != null) {
                if (prioritizedTasks.containsKey(epicMap.get(id).getStartTime())) {
                    prioritizedTasks.remove(epicMap.get(id).getStartTime());
                }
            }
            if (prioritizedTasksNull.containsKey(id)) {
               prioritizedTasksNull.remove(epicMap.get(id));
            }
        }
        epicMap.clear();
        deleteAllSubtasks();
    }
    @Override
    public void deleteAllSubtasks(){    //Удалить все субтаски
        for (Long id : subtaskMap.keySet()) {
            history.remove(id);
            if (subtaskMap.get(id).getStartTime() != null) {
                if (prioritizedTasks.containsKey(subtaskMap.get(id).getStartTime())) {
                    prioritizedTasks.remove(subtaskMap.get(id).getStartTime());
                }
            }
            if (prioritizedTasksNull.containsKey(id)) {
                prioritizedTasksNull.remove(subtaskMap.get(id));
            }
        }
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
        if (periodCheck(task)) {
            long id = getCode();
            task.setId(id);
            taskMap.put(id, task);

            if (task.getStartTime() != null) {
                prioritizedTasks.put(task.getStartTime(), task);
            } else {
                prioritizedTasksNull.put(id, task);
            }
        } else {
            throw new IllegalArgumentException("Задача не может пересекаться с другими задачами!");
        }
    }
    @Override
    public void createEpic(Epic epic){       //Создать эпик
        long id = getCode();
        epic.setId(id);
        changeEpicStatus(epic);
        epicMap.put(id, epic);
        if (epic.getStartTime() != null) {
            prioritizedTasks.put(epic.getStartTime(),epic);
        } else {
            prioritizedTasksNull.put(id,epic);
        }
    }
    @Override
    public void createSubtask(Subtask subtask){    //Создать субтаск
        if (periodCheck(subtask)) {
            if (epicMap.containsKey(subtask.getEpicID())) {
                long id = getCode();
                subtask.setId(id);
                subtaskMap.put(id, subtask);

                Epic currentEpic = epicMap.get(subtask.getEpicID());
                currentEpic.addEpicList(subtask);

                changeEpicStatus(currentEpic);
                currentEpic.timeCalculation();
                if (subtask.getStartTime() != null) {
                    prioritizedTasks.put(subtask.getStartTime(), subtask);
                } else {
                    prioritizedTasksNull.put(id, subtask);
                }
            }
        } else {
            throw new IllegalArgumentException("Подзадача не может пересекаться с другими задачами!");
        }
    }

    //~~~~~~~~~ Обновить задачу + ~~~~~~~~~~~
    @Override
    public void updateTask(Task task){       //Обновить таск
        if (task != null) {
            if (periodCheck(task)) {
                if (taskMap.containsKey(task.getId())) {
                    taskMap.put(task.getId(), task);
                }
                updatePriority(task);
            } else {
                throw new IllegalArgumentException("Задача не может пересекаться с другими задачами!");
            }
        }
    }
    @Override
    public void updateEpic(Epic epic){       //Обновить эпик
        if (epic != null) {
            if (epicMap.containsKey(epic.getId())) {
                if (epicMap.get(epic.getId()).getStatus() != epic.getStatus()) { //нельзя обновить статус эпика
                    return;
                } else {
                    epicMap.put(epic.getId(), epic);
                    updatePriority(epic);
                }
            }
        }
    }
    @Override
    public void updateSubtask(Subtask subtask){    //Обновить субтаск
        if (subtask != null) {
            if (periodCheck(subtask)) {
                if (subtaskMap.containsKey(subtask.getId())) {
                    Subtask oldSubtask = subtaskMap.get(subtask.getId());
                    Epic epic = epicMap.get(subtask.getEpicID());
                    if (oldSubtask != null) {
                        subtaskMap.put(subtask.getId(), subtask);
                        epic.getSubtasks().remove(oldSubtask);
                        epic.getSubtasks().add(subtask);
                        changeEpicStatus(epic);
                        epic.timeCalculation();
                    }
                    updatePriority(subtask);
                    updatePriority(epic);
                }
            } else {
                throw new IllegalArgumentException("Подзадача не может пересекаться с другими задачами!");
            }
        }
    }

    //~~~~~~~~~ Удалить по идентификатору + ~~~~~~~~~~~
    @Override
    public void deleteByIDTask(long id){       //Удалить по идентификатору таск
        if (taskMap.containsKey(id)) {
            if (taskMap.get(id).getStartTime() != null) {
                if (prioritizedTasks.containsKey(taskMap.get(id).getStartTime())) {
                    prioritizedTasks.remove(taskMap.get(id).getStartTime());
                }
            }
            if (prioritizedTasksNull.containsKey(id)) {
                prioritizedTasksNull.remove(id);
            }
            taskMap.remove(id);
            history.remove(id);

        }
    }
    @Override
    public void deleteByIDEpic(long id){       //Удалить по идентификатору эпик
        if (epicMap.containsKey(id)) {
            ArrayList<Subtask> sbEpic = epicMap.get(id).getSubtasks();
            for (Subtask sb : sbEpic) {
                if (sb.getStartTime() != null) {
                    if (prioritizedTasks.containsKey(sb.getStartTime())) {
                        prioritizedTasks.remove(sb.getStartTime());
                    }
                }
                if (prioritizedTasksNull.containsKey(id)) {
                    prioritizedTasksNull.remove(id);
                }
                subtaskMap.remove(sb.getId());
                history.remove(sb.getId());

            }

            if (epicMap.get(id).getStartTime() != null) {
                if (prioritizedTasks.containsKey(epicMap.get(id).getStartTime())) {
                    prioritizedTasks.remove(epicMap.get(id).getStartTime());
                }
            }
            if (prioritizedTasksNull.containsKey(id)) {
                prioritizedTasksNull.remove(id);
            }
            epicMap.remove(id);
            history.remove(id);
        }
    }
    @Override
    public void deleteByIDSubtask(long id){    //Удалить по идентификатору субтаск
        if (subtaskMap.containsKey(id)) {
            Subtask subtask = subtaskMap.get(id);
            long epicID = subtask.getEpicID();
            epicMap.get(epicID).getSubtasks().remove(subtask);//удалить субтаск из эпика
            changeEpicStatus(epicMap.get(epicID));  //обновить статус эпика
            if (subtaskMap.get(id).getStartTime() != null) {
                if (prioritizedTasks.containsKey(subtaskMap.get(id).getStartTime())) {
                    prioritizedTasks.remove(subtaskMap.get(id).getStartTime());
                }
            }
            if (prioritizedTasksNull.containsKey(id)) {
                prioritizedTasksNull.remove(id);
            }
            subtaskMap.remove(id);
            history.remove(id);

        }
    }

    //~~~~~~~~~~~~~~~~ Приоритет ~~~~~~~~~~~~~~~~~~~~~~~~~

    public List<Task> getPrioritizedTasks() {
        ArrayList<Task> buildPrioritizedTasks = new ArrayList<>(prioritizedTasks.values());
        buildPrioritizedTasks.addAll(prioritizedTasksNull.values());
        return buildPrioritizedTasks;
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
    private void updatePriority(Task task) {
        if(task.getStartTime() == null) {
            prioritizedTasksNull.put(task.getId(),task);
            for (Task value : prioritizedTasks.values()) { //если пользватель обнулил дату старта
                if (value.equals(task)) {                   //удалим задачу из сортированной мапы
                    prioritizedTasks.remove(value.getStartTime());
                }
                break;
            }
        } else {
            if (prioritizedTasksNull.containsKey(task.getId())) {
                prioritizedTasksNull.remove(task.getId());
            }
            for (Task value : prioritizedTasks.values()) { //если пользователь обновил дату, надо таск удалить из мапы
                if (value.equals(task)) {
                    prioritizedTasks.remove(value.getStartTime());
                }
                break;
            }
            prioritizedTasks.put(task.getStartTime(),task);
        }
    }
    @Override
    public void printHistory() { //этот метод тоже для тестирования
        history.print();
    }

    public boolean periodCheck(Task task) {
        boolean check = true;
        if (task.getStartTime() != null) {
            if (task.getDuration() != null) {
                for (Task currentTask : getPrioritizedTasks()) {
                    if (!currentTask.equals(task)) {
                        if (currentTask.getEndTime() != null) {
                            check = (task.getStartTime().isBefore(currentTask.getStartTime()) &&
                                    task.getEndTime().isBefore(currentTask.getEndTime()) ||
                                    (task.getStartTime().isAfter(currentTask.getStartTime()) &&
                                            task.getEndTime().isAfter(currentTask.getEndTime())));
                            if (!check) {
                                return false;
                            }
                        }
                    }
                }
            } else {
                for (Task currentTask : getPrioritizedTasks()) {
                    if(currentTask.getEndTime()!= null) {
                        check = (task.getStartTime().isBefore(currentTask.getStartTime()) ||
                                task.getStartTime().isAfter(currentTask.getEndTime()));
                        if (!check) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
}
