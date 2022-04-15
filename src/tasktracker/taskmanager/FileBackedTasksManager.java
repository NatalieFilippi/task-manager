package tasktracker.taskmanager;

import tasktracker.TaskStatus;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;

import java.io.IOException;
import java.io.Writer;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.io.FileReader;
import java.io.BufferedReader;

public class FileBackedTasksManager extends InMemoryTaskManager{

    private static final String PATH = "resources" + File.separator + "task manager.csv";
    private static File fileBacked;

    public FileBackedTasksManager(File fileBacked) {

        this.fileBacked = fileBacked;
        if (fileBacked.exists()) {
            loadFromFile();
        }


    }

    public static void main(String[] args) {
        File file = new File(PATH);
        FileBackedTasksManager taskManager = new FileBackedTasksManager(file);

        Task task1 = new Task("Купить продукты",
                "Молоко картошку чипсы", TaskStatus.NEW);
        taskManager.createTask(task1);
        Task task2 = new Task("Сделать поделку в сад",
                "Тема космос", TaskStatus.NEW);
        taskManager.createTask(task2);
        Epic epic1 = new Epic("Горячий клей",
                "Заклеить всё на свете",TaskStatus.NEW );
        Subtask subtask11 = new Subtask("Брошь",
                "Валяется на верхней полке", TaskStatus.NEW, 12);
        Subtask subtask12 = new Subtask("Магнитик корову",
                "Голова в игрушках", TaskStatus.NEW, 12);
        Subtask subtask13 = new Subtask("Рамку для фотографий",
                "И убрать подальше", TaskStatus.NEW, 12);
        taskManager.createEpic(epic1);
        taskManager.createSubtask(subtask11);
        taskManager.createSubtask(subtask12);
        taskManager.createSubtask(subtask13);

        taskManager.getEpicByID(3);
        taskManager.getTaskById(11);
        taskManager.getSubtaskByID(13);
        taskManager.getEpicByID(7);
        taskManager.getTaskById(10);
        taskManager.getSubtaskByID(15);
        taskManager.getEpicByID(12);


        FileBackedTasksManager taskManager2 = new FileBackedTasksManager(file);
        //System.out.println(taskManager.getTaskMap());
    }

    private void loadFromFile(){
        long maxId = 0;
        List<String> content = readFileContentsOrNull();
        for (int k = 1; k < content.size()-1; k++) {
            String[] line = content.get(k).split(",");
            if (line[1].equals("TASK")) {
                Task task = Task.fromFile(content.get(k));
                taskMap.put(task.getId(), task);
                if (task.getId() > maxId) {
                    maxId = task.getId();
                }
            } else if (line[1].equals("EPIC")) {
                Epic epic = Epic.fromFile(content.get(k));
                epicMap.put(epic.getId(), epic);
                if (epic.getId() > maxId) {
                    maxId = epic.getId();
                }
            } else if (line[1].equals("SUBTASK")) {
                Subtask subtask = Subtask.fromFile(content.get(k));
                subtaskMap.put(subtask.getId(), subtask);
                if (subtask.getId() > maxId) {
                    maxId = subtask.getId();
                }
                Epic currentEpic = epicMap.get(subtask.getEpicID());
                currentEpic.setEpicList(subtask);
            }

        }
        incrementalId = (int) maxId;
        historyFromString(content.get(content.size()-1));
    }

    private void save() {

        try (Writer fileWriter = new FileWriter(fileBacked)) {

            fileWriter.write(getFirstLine());   //записать первую строку заголовков
            for (Task task: taskMap.values()) { //записать все таски
                fileWriter.write(task.stringToFile());
            }

            for (Epic epic: epicMap.values()) { //записать эпики
                fileWriter.write(epic.stringToFile());
                ArrayList<Subtask> subtasks = epic.getSubtasks();
                if (!subtasks.isEmpty()) {  //если у эпика есть сабтаски, то записать их
                    for (Subtask subtask : subtasks) {
                        fileWriter.write(subtask.stringToFile());
                    }
                }
            }
            fileWriter.write("\n");
            fileWriter.write(history.stringToFile());

        } catch (IOException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Не удалось сохранить изменения.");
        }
    }

    private static String getFirstLine() {
        return "id,type,name,status,description,epic\n";
    }

    public void historyFromString(String value) {
        String[] split = value.split(",");
        for (String s : split) {
            long id = Long.parseLong(s);
            if (taskMap.containsKey(id)) {
                history.add(taskMap.get(id));
            } else if (subtaskMap.containsKey(id)) {
                history.add(subtaskMap.get(id));
            } else if (epicMap.containsKey(id)) {
                history.add(epicMap.get(id));
            }
        }
    }

    private static List<String> readFileContentsOrNull() {

        try (BufferedReader fileReader = new BufferedReader(new FileReader(fileBacked))) {

            List<String> file = new ArrayList<>();
            while (fileReader.ready()) {
                String line = fileReader.readLine();
                if (!line.isEmpty()) {
                    file.add(line);
                }
            }
            fileReader.close();
            return file;
        } catch (Throwable e) {
            System.out.println("Произошла ошибка во время чтения файла.");
        }
        return null;
    }


    @Override
    public ArrayList<Task> getTaskMap() {
        return super.getTaskMap();
    }

    @Override
    public ArrayList<Epic> getEpicMap() {
        return super.getEpicMap();
    }

    @Override
    public ArrayList<Subtask> getSubtaskMap() {
        return super.getSubtaskMap();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public Task getTaskById(long id) {
        history.add(taskMap.get(id));
        save();
        return taskMap.get(id);
    }

    @Override
    public Epic getEpicByID(long id) {
        history.add(epicMap.get(id));
        save();
        return epicMap.get(id);
    }

    @Override
    public Subtask getSubtaskByID(long id) {
        history.add(subtaskMap.get(id));
        save();
        return subtaskMap.get(id);
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteByIDTask(long id) {
        super.deleteByIDTask(id);
        save();
    }

    @Override
    public void deleteByIDEpic(long id) {
        super.deleteByIDEpic(id);
        save();
    }

    @Override
    public void deleteByIDSubtask(long id) {
        super.deleteByIDSubtask(id);
        save();
    }

    @Override
    public List<Task> history() {
        return super.history();
    }

    @Override
    public void printHistory() {
        super.printHistory();
    }
}

