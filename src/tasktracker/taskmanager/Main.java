package tasktracker.taskmanager;


import tasktracker.TaskStatus;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;

public class Main {
    public static void main(String[] args) {

        TaskManager TaskManager = new TaskManager();
        //создаём 2 простые задачи
        Task task1 = new Task("Записать Катю к врачу",
                "Надо по телефону, сайт не работает", TaskStatus.NEW);
        TaskManager.createTask(task1);
        Task task2 = new Task("Выбрать подарок Максу",
                "Зонт, очередную игру PS или билеты на матч Зенита", TaskStatus.NEW);
        TaskManager.createTask(task2);

        //создаём эпик с двумя подзадачами
        Epic epic1 = new Epic("Связать шапку",
                "ОГ 55, с двумя отворотами, макушка клиньями",TaskStatus.NEW );
        Subtask subtask11 = new Subtask("Выбрать пряжу",
                "Лучше чистый меринос, но смесовка тоже пойдет", TaskStatus.NEW, 3);
        Subtask subtask12 = new Subtask("Связать изделие",
                "Узор: резинка 2х2, спицы 5, 80 петель", TaskStatus.NEW, 3);
        TaskManager.createEpic(epic1);
        TaskManager.createSubtask(subtask11);
        TaskManager.createSubtask(subtask12);

        //создаём эпик с одной подзадачей
        Epic epic2 = new Epic("Сходить на фотосессию",
                "семейная фотосессия",TaskStatus.NEW );
        Subtask subtask21 = new Subtask("Выбрать фотографа",
                "Люська советует @best_of_the_best_photo, поиск начать с него", TaskStatus.NEW, 6);
        TaskManager.createEpic(epic2);
        TaskManager.createSubtask(subtask21);

        //печатаем все дела
        System.out.println(TaskManager.getTaskMap());
        System.out.println(TaskManager.getEpicMap());
        System.out.println(TaskManager.getSubtaskMap());

        //меняем статусы
        task1.setStatus(TaskStatus.DONE);
        TaskManager.updateTask(task1);
        task2.setStatus(TaskStatus.IN_PROGRESS);
        TaskManager.updateTask(task2);
        subtask11.setStatus(TaskStatus.IN_PROGRESS);
        TaskManager.updateSubtask(subtask11);
        subtask21.setStatus(TaskStatus.DONE);
        TaskManager.updateSubtask(subtask21);

        //ещё раз печатаем все дела
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println(TaskManager.getTaskMap());
        System.out.println(TaskManager.getEpicMap());
        System.out.println(TaskManager.getSubtaskMap());

        TaskManager.deleteByIDTask(2);
        TaskManager.deleteByIDSubtask(4);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println(TaskManager.getTaskMap());
        System.out.println(TaskManager.getEpicMap());
        System.out.println(TaskManager.getSubtaskMap());

    }
}

