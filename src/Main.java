
public class Main {
    public static void main(String[] args) {

        Manager manager = new Manager();

        //создаём 2 простые задачи
        Task task1 = new Task("Записать Катю к врачу",
                "Надо по телефону, сайт не работает", Status.NEW);
        manager.createTask(task1);
        Task task2 = new Task("Выбрать подарок Максу",
                "Зонт, очередную игру PS или билеты на матч Зенита", Status.NEW);
        manager.createTask(task2);

        //создаём эпик с двумя подзадачами
        Epic epic1 = new Epic("Связать шапку",
                "ОГ 55, с двумя отворотами, макушка клиньями",Status.NEW );
        Subtask subtask11 = new Subtask("Выбрать пряжу",
                "Лучше чистый меринос, но смесовка тоже пойдет", Status.NEW, 3);
        Subtask subtask12 = new Subtask("Связать изделие",
                "Узор: резинка 2х2, спицы 5, 80 петель", Status.NEW, 3);
        manager.createEpic(epic1);
        manager.createSubtask(subtask11);
        manager.createSubtask(subtask12);

        //создаём эпик с одной подзадачей
        Epic epic2 = new Epic("Сходить на фотосессию",
                "семейная фотосессия",Status.NEW );
        Subtask subtask21 = new Subtask("Выбрать фотографа",
                "Люська советует @best_of_the_best_photo, поиск начать с него", Status.NEW, 6);
        manager.createEpic(epic2);
        manager.createSubtask(subtask21);

        //печатаем все дела
        System.out.println(manager.getTaskMap());
        System.out.println(manager.getEpicMap());
        System.out.println(manager.getSubtaskMap());

        //меняем статусы
        task1.status = Status.DONE;
        manager.updateTask(task1);
        task2.status = Status.IN_PROGRESS;
        manager.updateTask(task2);
        subtask11.status = Status.IN_PROGRESS;
        manager.updateSubtask(subtask11);
        subtask21.status = Status.DONE;
        manager.updateSubtask(subtask21);

        //ещё раз печатаем все дела
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println(manager.getTaskMap());
        System.out.println(manager.getEpicMap());
        System.out.println(manager.getSubtaskMap());

        manager.deleteByIDTask(2);
        manager.deleteByIDSubtask(4);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println(manager.getTaskMap());
        System.out.println(manager.getEpicMap());
        System.out.println(manager.getSubtaskMap());

    }
}

enum Status {
    NEW,
    IN_PROGRESS,
    DONE
}
