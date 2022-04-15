package tasktracker.taskmanager;


import tasktracker.TaskStatus;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        //InMemoryTaskManager taskManager = new InMemoryTaskManager();
        //создаём 2 простые задачи
        Task task1 = new Task("Записать Катю к врачу",
                "Надо по телефону, сайт не работает", TaskStatus.NEW);
        taskManager.createTask(task1);
        Task task2 = new Task("Выбрать подарок Максу",
                "Зонт, очередную игру PS или билеты на матч Зенита", TaskStatus.NEW);
        taskManager.createTask(task2);

        //создаём эпик с двумя подзадачами
        Epic epic1 = new Epic("Связать шапку",
                "ОГ 55, с двумя отворотами, макушка клиньями");
        Subtask subtask11 = new Subtask("Выбрать пряжу",
                "Лучше чистый меринос, но смесовка тоже пойдет", TaskStatus.NEW, 3);
        Subtask subtask12 = new Subtask("Связать изделие",
                "Узор: резинка 2х2, спицы 5, 80 петель", TaskStatus.NEW, 3);
        Subtask subtask13 = new Subtask("Сделать ВТО",
                "Стирка только вручную!", TaskStatus.NEW, 3);
        taskManager.createEpic(epic1);
        taskManager.createSubtask(subtask11);
        taskManager.createSubtask(subtask12);
        taskManager.createSubtask(subtask13);

        //создаём эпик с одной подзадачей
        Epic epic2 = new Epic("Сходить на фотосессию",
                "семейная фотосессия");
//        Subtask subtask21 = new Subtask("Выбрать фотографа",
//                "Люська советует @best_of_the_best_photo, поиск начать с него", TaskStatus.NEW, 6);
        taskManager.createEpic(epic2);
        //taskManager.createSubtask(subtask21);

        //печатаем все дела
        /*System.out.println(taskManager.getTaskMap());
        System.out.println(taskManager.getEpicMap());
        System.out.println(taskManager.getSubtaskMap());*/


        //проверим историю
        System.out.println("~~~~~~~~~~~~~~~~~Проверим историю~~~~~~~~~~~~~~~~~~~~~");
        taskManager.getTaskById(1);
        taskManager.getEpicByID(3);
        taskManager.getSubtaskByID(4);
        System.out.println("~~~~~~~~~~~~~~~~~таск, эпик, субтаск~~~~~~~~~~~~~~~~~~~~~");
        taskManager.printHistory(); //распечатать
        taskManager.getTaskById(2);
        taskManager.getSubtaskByID(5);
        taskManager.getEpicByID(7);
        System.out.println("~~~~~~~~~~~~~~таск, эпик, субтаск, таск, субтаск, эпик~~~~~~~~");
        taskManager.printHistory(); //распечатать
        taskManager.getSubtaskByID(5);
        taskManager.getEpicByID(7);
        taskManager.getTaskById(2);
        taskManager.getTaskById(2);
        taskManager.getTaskById(2);
        System.out.println("~~~~~таск, эпик, субтаск, таск, субтаск, эпик, субтаск, эпик, 3 таска~~~");
        taskManager.printHistory(); //распечатать

        System.out.println("~~~~~Удалить таск~~~");
        taskManager.deleteByIDTask(1);
        taskManager.printHistory();

        System.out.println("~~~~~Удалить эпик~~~");
        taskManager.deleteByIDEpic(3);
        taskManager.printHistory();

    }
}

