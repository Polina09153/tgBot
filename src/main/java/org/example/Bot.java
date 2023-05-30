package org.example;

import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.*;
import java.util.*;

public class Bot extends TelegramLongPollingBot {
    ArrayList<Student> students = new ArrayList<>();
    ArrayList<Teacher> teachers = new ArrayList<>();

    String [] testsName = {"Артикли", "Модальные глаголы", "Present Tense", "Past Tense", "Future Tense", "Условные предложения"};
    String [] tablesNames = {"articles", "modalverbs", "presenttense", "pasttense", "futuretense", "condsentence"};
    int testID = 0;
    String id = "";
    Student a;
    Test test = new Test(0);
    String idRand = "";
    Teacher b;
    int idStud1 = 0;
    int k = 0;
    int n = 0;
    boolean f = false;
    String url = "jdbc:mysql://localhost/engtest?serverTimezone=Europe/Moscow&useSSL=false";
    String username = "root";
    String password = "Azicroy45";


    public void chat(SendMessage message, String a) {
        message.setText(a);
        try {
            execute(message); // Call method to send the message
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {

        SendMessage message = new SendMessage();
        if (update.hasMessage() && update.getMessage().hasText()) {
            message.setChatId(update.getMessage().getChatId().toString());
            String answer = update.getMessage().getText();

            //Ввод и проверка логина
            if (k == 1) {
                read();
                for (Student i : students) {
                    if (i.login.equals(answer)) {
                        k = 2;
                        id = "stud";
                        f = true;
                        this.a = new Student(i.name, i.login, i.group, i.id);
                    }
                }
                for (Teacher i : teachers) {
                    if (i.login.equals(answer)) {
                        k = 2;
                        id = "teach";
                        f = true;
                        this.b = new Teacher(i.name, i.login, i.id);
                    }
                }
                if (!id.equals("stud") && !id.equals("teach")){
                    chat(message, "Неверный логин! Попробуйте снова");
                    k = 1;
                    students = new ArrayList<>();
                    teachers = new ArrayList<>();
                }
            }

            if (k == 0) {
                chat(message, "Введите логин");
                answer = update.getMessage().getText();
                k = 1;
            }

            //Отчет по группе
            if (id.equals("teach") && k == 4){
                if (!checkGroup(b.id, Integer.parseInt(answer)))
                    chat(message,"Неверно введена группа");
                else {
                    for (Student i: students){
                        if (i.group == Integer.parseInt(answer)){
                            chat(message, i.name + ": " + "\n" + getReport(i.id));
                        }
                    }
                }
                k = 2;
            }

            //Выбор варианта
            if (k == 2) {
                if (id.equals("stud"))
                    studChoose(message);
                if (id.equals("teach"))
                    teachChoose(message);

                k = 3;
            }
            if (n == 10)
                k = 6;

            //Отправка вопросов
            if (id.equals("stud") && k == 5 && n < 10 ) {
                chat(message, n+1 + ") " + (String) test.question.get(n));
                n += 1;
                if (n > 1){
                    writeDB("INSERT into testsanswers(idTests, numOfQuedtion, answer) VALUES (" + idRand + ", " + (n-1) + ", '" + answer + "')");
                }
            }

            if (id.equals("stud") && k == 4) {
                int n = 0;
                for (String i : testsName){
                    n += 1;
                    if (answer.equals(i)){
                        test = new Test(n);
                        readTest(test, "SELECT * FROM " + tablesNames[n-1]);
                        testID = n;
                        k = 5;
                    }
                }
                chat(message, "Все ответы пишутся без сокращений, необходимо лишь раскрыть скобки/вставить пропущенное слово. Отправьте + если ознакомлены.");
            }
            if (id.equals("stud") && answer.equals("Выбрать тест")) {
                testStud(message);
                k = 4;
                Random rnd = new Random();
                idRand = a.id + String.valueOf(rnd.nextInt(0, 100));
            }


            if (id.equals("stud") &&k == 6){
                writeDB("INSERT into testsanswers(idTests, numOfQuedtion, answer) VALUES (" + idRand + ", " + (n) + ", '" + answer + "')");
                n = 0;
                k = 7;

            }
            //Тест
            if (id.equals("stud") && k == 7){
                int rez = getResult(Integer.parseInt(idRand), testID);
                writeDB("INSERT into tests(idTests, idStudent, numOfTest, Result) VALUES (" + idRand + ", " + a.id + ", " + testID + ", " + rez + ")");
                chat(message,"Ваш результат: " + rez + " из 10");
                k = 3;
                studChoose(message);
            }

            if (id.equals("stud") && answer.equals("Просмотреть отчет")) {
                chat(message, getReport(a.id));
                k = 3;
            }

            if (id.equals("stud") && k == 8) {
                chat(message,getOneTestReport(a.id, Integer.parseInt(answer)));
            }

            if (id.equals("stud") && answer.equals("Просмотреть отчет по одному тесту")) {
                chat(message, getReport(a.id));
                chat(message,"Выеберете номер теста");
                k = 8;
            }


            if (id.equals("teach") && answer.equals("Просмотреть результаты группы")) {
                chat(message, "Введите номер группы");
                k = 4;
            }


            if (id.equals("teach") && answer.equals("Просмотреть результаты одного теста ученика")) {
                chat(message, "Введите имя, напимер Петров П.");
                k = 5;
            }

            if (id.equals("teach") && k == 5){
                for (Student i: students){
                    if (i.name.equals(answer)){
                        chat(message, i.name + ": " + "\n" + getReport(i.id));
                        idStud1 = i.id;
                        if (!getReport(i.id).equals("Результаты: \n" + " Не решен ни один тест"))
                            k = 6;
                        else
                            k = 3;
                    }
                }
            }
            if (id.equals("teach") && k == 6){
                chat(message, "Выберете номер теста");
                k = 7;
            }
            if (id.equals("teach") && k == 7){
                chat(message,getOneTestReport(idStud1, Integer.parseInt(answer)));
                k = 3;
            }
        }
    }


    //Проверка группы
    public boolean checkGroup(int teachID, int group){
        ArrayList gr = new ArrayList();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM schoolgroups WHERE TeacherId = " + teachID);
                while (resultSet.next()) {
                    int r = resultSet.getInt(1);
                    gr.add(r);
                }
            }
        } catch (Exception ex) {
            System.out.println("Connection failed...");
            System.out.println(ex);
        }
        return gr.contains(group);
    }

    //Отчет по одному тесту
    public String getOneTestReport(int studID, int numOfTest){
        String report = "Отчет:";
        int idTest = 0;
        ArrayList answers = new ArrayList();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT idTests FROM tests WHERE idStudent = " + studID + " and numOfTest = " + numOfTest);
                while (resultSet.next()) {
                    int n = resultSet.getInt(1);
                    idTest = n;
                }
            }
            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT answer FROM testsanswers WHERE  idTests = " + idTest);
                while (resultSet.next()) {
                    String s = resultSet.getString(1);
                    answers.add(s);
                }
            }

        } catch (Exception ex) {
            System.out.println("Connection failed...");
            System.out.println(ex);
        }
        String emoji_true = EmojiParser.parseToUnicode(":white_check_mark:");
        String emoji_false = EmojiParser.parseToUnicode(":x:");
        Test t = new Test(numOfTest);
        readTest(t, "SELECT * FROM " + tablesNames[numOfTest-1]);
        for (int i = 0; i < 10; i++){
            report += "\n Вопрос: " + t.question.get(i);
            report += "\n Ваш ответ: " + answers.get(i);
            if (answers.get(i).equals(t.answers.get(i)))
                report += " " + emoji_true + "\n";
            else
                report += " " + emoji_false + "\n";
            report += "Правильный ответ: " + t.answers.get(i) + "\n";

        }
        return report;
    }



    //Отчет
    public String getReport(int studID){
        ArrayList result = new ArrayList();
        ArrayList numOfTest = new ArrayList();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM tests WHERE idStudent = " + studID);
                while (resultSet.next()) {
                    int r = resultSet.getInt(4);
                    int n = resultSet.getInt(3);
                    result.add(r);
                    numOfTest.add(n);
                }
            }
        } catch (Exception ex) {
            System.out.println("Connection failed...");
            System.out.println(ex);
        }
        String report = "Результаты: ";
        int n = 0;
        for (Object i:numOfTest){
            report += "\n"+ i +") " + testsName[((int) i)-1] + ": " + result.get(n) + " из 10";
            n += 1;
        }
        if (result.isEmpty())
            report += "\n Не решен ни один тест";
        return report;
    }


    //Результаты теста
    public int getResult(int idTest, int num) {
        ArrayList answers = new ArrayList();
        int result = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT answer FROM testsanswers WHERE idTests = " + idTest);
                while (resultSet.next()) {
                    String answer = resultSet.getString(1);
                    answers.add(answer);
                }
            }
        } catch (Exception ex) {
            System.out.println("Connection failed...");
            System.out.println(ex);
        }
        Test tests = new Test(num);
        for (int i = 0; i < 6; i++){
            if ((i+1) == num){
                readTest(tests, "SELECT * FROM " + tablesNames[i]);
            }
        }
        for (int i = 0; i < 10; i++){
            if (answers.get(i).equals(tests.answers.get(i)))
                result += 1;
        }
        return result;
    }

    //Запись в бд
    public void writeDB(String SQL) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                Statement statement = conn.createStatement();
                int rows = statement.executeUpdate(SQL);
            }
        } catch (Exception ex) {
            System.out.println("Connection failed...");
            System.out.println(ex);
        }
    }

    //Решить тест
    public void testStud(SendMessage message) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true); //скрываем после использования

        //Создаем список с рядами кнопок
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        //Создаем один ряд кнопок и добавляем его в список
        KeyboardRow row = new KeyboardRow();
        row.add("Артикли");
        row.add("Модальные глаголы");
        row.add("Present Tense");
        keyboardRows.add(row);
        row = new KeyboardRow();
        row.add("Past Tense");
        row.add("Future Tense");
        row.add("Условные предложения");
        keyboardRows.add(row);
        //добавляем лист с одним рядом кнопок в главный объект
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        message.setReplyMarkup(replyKeyboardMarkup);
        chat(message, "Выберете тему");
    }

    //Выбор действия студента
    public void studChoose(SendMessage message) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true); //скрываем после использования

        //Создаем список с рядами кнопок
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        //Создаем один ряд кнопок и добавляем его в список
        KeyboardRow row = new KeyboardRow();
        row.add("Выбрать тест");
        row.add("Просмотреть отчет");
        row.add("Просмотреть отчет по одному тесту");
        keyboardRows.add(row);
        //добавляем лист с одним рядом кнопок в главный объект
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        message.setReplyMarkup(replyKeyboardMarkup);
        chat(message, "Выберете действие");
    }

    //Выбор действия преподователя
    public void teachChoose(SendMessage message) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true); //скрываем после использования

        //Создаем список с рядами кнопок
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        //Создаем один ряд кнопок и добавляем его в список
        KeyboardRow row = new KeyboardRow();
        row.add("Просмотреть результаты одного теста ученика");
        row.add("Просмотреть результаты группы");
        keyboardRows.add(row);
        //добавляем лист с одним рядом кнопок в главный объект
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        message.setReplyMarkup(replyKeyboardMarkup);
        chat(message, "Выберете действие");
    }

    public void read() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM students");
                while (resultSet.next()) {
                    String login = resultSet.getString(2);
                    String name = resultSet.getString(1);
                    int group = resultSet.getInt(3);
                    int id = resultSet.getInt(4);
                    Student a = new Student(login, name, group, id);
                    this.students.add(a);
                }
            }
            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM teachers");
                while (resultSet.next()) {
                    String login = resultSet.getString(1);
                    String name = resultSet.getString(2);
                    int id = resultSet.getInt(3);
                    Teacher a = new Teacher(name, login, id);
                    this.teachers.add(a);
                }
            }
        } catch (Exception ex) {
            System.out.println("Connection failed...");
            System.out.println(ex);
        }
    }

    public void readTest(Test a, String SQL) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery(SQL);
                while (resultSet.next()) {
                    String question = resultSet.getString(2);
                    String answer = resultSet.getString(3);
                    a.question.add(question);
                    a.answers.add(answer);
                }
            }
        } catch (Exception ex) {
            System.out.println("Connection failed...");
            System.out.println(ex);
        }
    }

    @Override
    public String getBotUsername() {
        return "mathLab07_bot";
    }

    @Override
    public String getBotToken() {
        return "6184839923:AAHnW0tWjaIZoU0JcGj5LBnHCbiMDyz6QEo";
    }
}

