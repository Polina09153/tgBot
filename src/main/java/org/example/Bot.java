package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class Bot extends TelegramLongPollingBot {
    boolean f = false;
    String var = "";

    public void counter(String args[], SendMessage message, String var, Update update) {
        double res = 0.0;
        double[] num = new double[args.length];
        for (int i = 0; i < args.length; i++) {
            num[i] = Double.parseDouble(args[i]);
        }
        message.setChatId(update.getMessage().getChatId().toString());

        switch (var) {
            case ("1"): {
                res = 5 * Math.pow(num[0], num[3] * num[4]);
                res = res / (num[1] * num[2]);
                res -= Math.sqrt(Math.abs(Math.pow(Math.cos(num[4]), 3)));
                chat(message,"Вариант 1 " + "\na = " + num[0] + "\nb = " + num[1] + "\nc = " + num[2] + "\nn = " + num[3] + "\nx = " + num[4]);
            }
            break;
            case ("2"): {
                res = Math.abs(num[2] - num[3]) / Math.pow((1.0 + 2.0 * num[2]), num[0]);
                res -= Math.pow(Math.E, Math.sqrt(1.0 + num[1]));
                chat(message, "Вариант 2 " + "\na = " + num[0] + "\nw = " + num[1] + "\nx = " + num[2] + "\ny = " + num[3]);
            }
            break;
            case ("3"): {
                res = Math.sqrt(num[0] + num[1] * num[3] + num[2] * Math.pow(Math.abs(Math.sin(num[3])), 1.0 / 3.0));
                chat(message, "Вариант 3 " + "\na0 = " + num[0] + "\na1 = " + num[1] + "\na2 = " + num[2] + "\nx = " + num[3]);
            }
            break;
            case ("4"): {
                res = Math.log10(Math.abs(Math.pow(num[0], 7)));
                res += Math.atan(num[1] * num[1]);
                res += Math.PI / Math.sqrt(Math.abs(num[0] + num[1]));
                chat(message, "Вариант 4 " + "\na = " + num[0] + "\nx = " + num[1]);
            }
            break;
            case ("5"): {
                res = Math.pow((num[0] + num[1]), 2) / (num[2] + num[3]);
                res += Math.pow(Math.E, Math.sqrt(num[4] + 1));
                res = Math.pow(res, 1.0 / 5.0);
                chat(message, "Вариант 5 " + "\na = " + num[0] + "\nb = " + num[1] + "\nc = " + num[2] + "\nd = " + num[3] + "\nx = " + num[4]);
            }
            break;
            case ("6"): {
                double a = (2.0 * Math.sin(4 * num[0]) + Math.pow(Math.cos(num[0] * num[0]), 2));
                a = a / (3.0 * num[0]);
                res = Math.pow(Math.E, a);
                chat(message, "Вариант 6 " + "\nx = " + num[0]);
            }
            break;
            case ("7"): {
                res = 0.25 * (((1 + Math.pow(num[0], 2)) / (1 - num[0])) + 0.5 * Math.tan(num[0]));
                chat(message, "Вариант 7 " + "\nx = " + num[0]);

            }
            break;
        }
        chat(message, "Результат: " + res);
    }

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
//        boolean f = false;
        SendMessage message = new SendMessage();
        if (update.hasMessage() && update.getMessage().hasText()) {
            message.setChatId(update.getMessage().getChatId().toString());
            String answer = update.getMessage().getText();
            if (!f) {
                switch (answer) {
                    case ("/start"): {
                        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                        replyKeyboardMarkup.setResizeKeyboard(true);
                        replyKeyboardMarkup.setOneTimeKeyboard(true); //скрываем после использования

                        //Создаем список с рядами кнопок
                        List<KeyboardRow> keyboardRows = new ArrayList<>();
                        //Создаем один ряд кнопок и добавляем его в список
                        KeyboardRow row = new KeyboardRow();
                        row.add("1");
                        row.add("2");
                        row.add("3");
                        keyboardRows.add(row);
                        row = new KeyboardRow();
                        row.add("4");
                        row.add("5");
                        row.add("6");
                        keyboardRows.add(row);
                        row = new KeyboardRow();
                        row.add("7");
                        keyboardRows.add(row);
                        //добавляем лист с одним рядом кнопок в главный объект
                        replyKeyboardMarkup.setKeyboard(keyboardRows);
                        message.setReplyMarkup(replyKeyboardMarkup);
                        chat(message, "Выберете вариант (1-7)");
                        break;
                    }
                    case ("1"): {
                        chat(message, "Введите аргументы a, b, c, n, x через пробел");
                        f = true;
                        var = "1";
                        break;
                    }
                    case ("2"): {
                        chat(message, "Введите аргументы a, w, x, y через пробел");
                        f = true;
                        var = "2";
                        break;
                    }
                    case ("3"): {
                        chat(message, "Введите аргументы a0, a1, a2, x через пробел");
                        f = true;
                        var = "3";
                        break;
                    }
                    case ("4"): {
                        chat(message, "Введите аргументы a, x через пробел");
                        f = true;
                        var = "4";
                        break;
                    }
                    case ("5"): {
                        chat(message, "Введите аргументы a, b, c, d, x через пробел");
                        f = true;
                        var = "5";
                        break;
                    }
                    case ("6"): {
                        chat(message, "Введите аргумент x");
                        f = true;
                        var = "6";
                        break;
                    }
                    case ("7"): {
                        chat(message, "Введите x");
                        f = true;
                        var = "7";
                        break;
                    }
                    default:
                        chat(message, "Ошибка, выберете вариант");
                }
            } else {
                String [] args = answer.split(" ");
                if (check(var, args))
                    counter(args, message, var, update);
                else
                    chat(message, "Ошибка, начните заново");
                f = false;
                var = "";
                chat(message, "Выберете вариант (1-7)");
            }
        }
    }

    public boolean check(String var, String [] args){
        boolean f1 = false;
        switch (var){
            case ("1"):{
                if (args.length == 5)
                    f1 = true;
            }
            case ("2"):{
                if (args.length == 4)
                    f1 = true;
            }
            case ("3"):{
                if (args.length == 4)
                    f1 = true;
            }
            case ("4"):{
                if (args.length == 2)
                    f1 = true;
            }
            case ("5"):{
                if (args.length == 5)
                    f1 = true;
            }
            case ("6"):{
                if (args.length == 1)
                    f1 = true;
            }
            case ("7"):{
                if (args.length == 1)
                    f1 = true;
            }
        }
        for (int i = 0; i < args.length; i++){
            try {
                Double.parseDouble(args[i]);
            } catch (NumberFormatException e) {
                f1 = false;
            }
        }
        return f1;
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