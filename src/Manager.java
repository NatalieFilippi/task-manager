import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private int generatedCode; //сквозной счётчик-генератор ID задач, не противоречит ТЗ
    private HashMap<Integer, Task> taskMap;
    private HashMap<Integer, Epic> epicMap;
    private HashMap<Integer, Subtask> subtaskMap;

    public Manager() {
        generatedCode = 0;
        taskMap = new HashMap<>();
        epicMap = new HashMap<>();
        subtaskMap = new HashMap<>();
    }

    //~~~~~~~~~ Получить список задач + ~~~~~~~~~~~

    public String getTaskMap(){      //Получить список всех тасков
        return taskMap.toString();
    }

    public String getEpicMap(){      //Получить список всех эпиков
        return epicMap.toString();
    }

    public String getSubtaskMap(){   //Получить список всех субтасков
        return subtaskMap.toString();
    }

    //~~~~~~~~~ Удалить все задачи + ~~~~~~~~~~~

    public void deleteTask(){       //Удалить все таски
        taskMap.clear();
    }

    public void deleteEpic(){       //Удалить все эпики
        epicMap.clear();
        deleteSubtask();
    }

    public void deleteSubtask(){    //Удалить все субтаски
        subtaskMap.clear();

        ArrayList<Subtask> stList = new ArrayList<>(); //удалить все субтаски из эпиков
        for (Integer i : epicMap.keySet()) {
           stList = epicMap.get(i).getSubtaskList();
           epicMap.get(i).status = Status.NEW; // если у эпика нет подзадач, то он новый
           stList.clear();
        }
    }

    //~~~~~~~~~ Получить по идентификатору + ~~~~~~~~~~~

    public Task getByIDTask(int id){      //Получить по идентификатору таск
        return taskMap.get(id);
    }

    public Epic getByIDEpic(int id){      //Получить по идентификатору эпик
        return epicMap.get(id);
    }

    public Subtask getByIDSubtask(int id){   //Получить по идентификатору субтаск
        return subtaskMap.get(id);
    }

    //~~~~~~~~~ Создать задачу + ~~~~~~~~~~~

    public void createTask(Task task){       //Создать таск
        taskMap.put(getCode(), task);
    }

    public void createEpic(Epic epic){       //Создать эпик
        epicMap.put(getCode(), epic);
    }

    public void createSubtask(Subtask subtask){    //Создать субтаск
        subtaskMap.put(getCode(), subtask);

        Epic currentEpic = epicMap.get(subtask.getEpicID());
        currentEpic.setEpicList(subtask);
        if (currentEpic.status == Status.DONE) {
            currentEpic.status = Status.IN_PROGRESS;
        }
    }

    //~~~~~~~~~ Обновить задачу + ~~~~~~~~~~~

    public void updateTask(Task task){       //Обновить таск
        int id = getIDTask(task);
        taskMap.put(id, task);
    }

    public void updateEpic(Epic epic){       //Обновить эпик
        int id = getIDEpic(epic);
        if (epicMap.get(id).status != epic.status) { //нельзя обновить статус эпика
            return;
        } else {
            epicMap.put(id, epic);
        }
    }

    public void updateSubtask(Subtask subtask){    //Обновить субтаск
        int id = getIDSubtask(subtask);
        subtaskMap.put(id, subtask);

        Epic currentEpic = epicMap.get(subtask.getEpicID());

        if (subtask.status == Status.DONE) {
            ArrayList<Subtask> allSubtask = currentEpic.getSubtaskList();
            boolean isDone = true;
            for (Subtask st : allSubtask) {
                if (st.status != Status.DONE) {
                    isDone = false;
                    return;
                }
            }
            if (isDone) {
                currentEpic.status = Status.DONE;
            }
        } else if (subtask.status == Status.IN_PROGRESS) {
            currentEpic.status = Status.IN_PROGRESS;
        } else if (subtask.status == Status.NEW) {
            //оставим возможность обнулять эпик, если обнулили сабтаск при условии, что остальные сабтаски новые
            ArrayList<Subtask> allSubtask = currentEpic.getSubtaskList();
            boolean isNew = true;
            for (Subtask st : allSubtask) {
                if (st.status != Status.NEW) {
                    isNew = false;
                    return;
                }
            }
            if (isNew) {
                currentEpic.status = Status.NEW;
            }
        }
    }

    //~~~~~~~~~ Удалить по идентификатору + ~~~~~~~~~~~

    public void deleteByIDTask(int id){       //Удалить по идентификатору таск
        taskMap.remove(id);
    }

    public void deleteByIDEpic(int id){       //Удалить по идентификатору эпик
        ArrayList<Subtask> sbEpic = epicMap.get(id).getSubtaskList();
        for (Subtask sb : sbEpic) {
            subtaskMap.remove(getIDSubtask(sb));
        }
        epicMap.remove(id);
    }

    public void deleteByIDSubtask(int id){    //Удалить по идентификатору субтаск
        Subtask subtask = subtaskMap.get(id);
        int epicID = subtask.getEpicID();
        ArrayList<Subtask> stList = epicMap.get(epicID).getSubtaskList();
        stList.remove(subtask); //удалить субтаск из эпика
        if (stList.isEmpty()) { //если у эпика не осталось подзадач, то эпик переходит в статус NEW
            epicMap.get(epicID).status = Status.NEW;
        } else {
            boolean isDone = true;
            boolean isNew = true;
            for (Subtask st : stList) {
                if (st.status != Status.DONE) {
                    isDone = false;
                }
                if (st.status != Status.NEW) {
                    isNew = false;
                }
            }
            if (isDone) {
                epicMap.get(epicID).status = Status.DONE;
            } else if (isNew) {
                epicMap.get(epicID).status = Status.NEW;
            } else epicMap.get(epicID).status = Status.IN_PROGRESS;

        }
        subtaskMap.remove(id);
    }

    //~~~~~~~~~ДОПОЛНИТЕЛЬНЫЕ МЕТОДЫ ~~~~~~~~~~~

    public ArrayList<Subtask> getSubtaskByEpic(int id){  //Получить список подзадач определённого эпика
        return epicMap.get(id).getSubtaskList();
    }

    private int getCode(){

        return ++generatedCode;

    }
    
    private int getIDEpic(Epic epic) {
        int id = 0;
        for (Integer i : epicMap.keySet()) {
            if (epicMap.get(i) == epic) {
                id = i;
                break;
            }
        }
        return id;
    }

    private int getIDTask(Task task) {
        int id = 0;
        for (Integer i : taskMap.keySet()) {
            if (taskMap.get(i) == task) {
                id = i;
                break;
            }
        }
        return id;
    }

    private int getIDSubtask(Subtask subtask) {
        int id = 0;
        for (Integer i : subtaskMap.keySet()) {
            if (subtaskMap.get(i) == subtask) {
                id = i;
                break;
            }
        }
        return id;
    }
}
