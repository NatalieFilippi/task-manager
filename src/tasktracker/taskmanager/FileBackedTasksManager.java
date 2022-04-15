package tasktracker.taskmanager;

import historymanager.InMemoryHistoryManager;
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
                "Заклеить всё на свете");
        Subtask subtask11 = new Subtask("Брошь",
                "Валяется на верхней полке", TaskStatus.NEW, 12); //поменять id эпика на 3, если
        Subtask subtask12 = new Subtask("Магнитик корову",              //запускать с пустым файлом
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

        Subtask subtask14 = new Subtask("Рамку для фотографий",
                "И убрать в шкаф", TaskStatus.DONE, 12);
        subtask14.setId(15);
        taskManager.updateSubtask(subtask14);

        boolean check = verifyData();

        FileBackedTasksManager taskManager2 = new FileBackedTasksManager(file);
    }

    private void loadFromFile(){
        long maxId = 0;
        List<String> content = readFileContentsOrNull();
        if (!content.isEmpty()) {
            for (int k = 1; k < content.size() - 1; k++) {
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
                    currentEpic.addEpicList(subtask);
                }

            }
            incrementalId = (int) maxId;
            historyFromString(content.get(content.size() - 1));
        }
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

    private static boolean verifyData() {
        List<String> content = readFileContentsOrNull();
        boolean b1 = verifyHistory(content.get(content.size() - 1));
        boolean b2 = verifyTasks(content);
        return b1 && b2;
    }

    private static boolean verifyTasks(List<String> content) {

        if (content.isEmpty() && taskMap.isEmpty() && epicMap.isEmpty() && subtaskMap.isEmpty()) {
            return true;
        }
        for (int k = 1; k < content.size() - 1; k++) {
            String[] line = content.get(k).split(",");

            if (line[1].equals("TASK")) {
                Task task = Task.fromFile(content.get(k));
                if (taskMap.containsKey(task.getId())) {
                    if (!taskMap.get(task.getId()).equals(task)) {
                        return false;
                    }
                }

                } else if (line[1].equals("EPIC")) {
                    Epic epic = Epic.fromFile(content.get(k));
                    if (epicMap.containsKey(epic.getId())) {
                        if (!epicMap.get(epic.getId()).equals(epic)) {
                            return false;
                        }
                    }

                } else if (line[1].equals("SUBTASK")) {
                    Subtask subtask = Subtask.fromFile(content.get(k));
                    if (subtaskMap.containsKey(subtask.getId())) {
                        if (!subtaskMap.get(subtask.getId()).equals(subtask)) {
                            return false;
                        }
                    }
                }

            }
        return true;
    }

    private static boolean verifyHistory(String historyFile) {

        List<Task> historyMemory = history.getHistory();

        String[] split = historyFile.split(",");

        if (historyMemory.size() != split.length) {
            return false;
        }
        for (int i=0; i < historyMemory.size(); i++) {
            if (Long.parseLong(split[i]) != historyMemory.get(i).getId()) {
                return false;
            }
        }
        return true;
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
        Task task = taskMap.get(id);
        if(task != null) {
            history.add(task);
        }
        save();
        return task;
    }

    @Override
    public Epic getEpicByID(long id) {
        Epic epic = epicMap.get(id);
        if (epic != null) {
            history.add(epic);
        }
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskByID(long id) {
        Subtask subtask = subtaskMap.get(id);
        if (subtask != null) {
            history.add(subtask);
        }
        save();
        return subtask;
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

