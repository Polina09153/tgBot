package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;


public class Student {
    String name;
    String login;
    int group;
    int id;

    public Student(String name, String login, int group, int id) {
        this.name = name;
        this.login = login;
        this.group = group;
        this.id = id;
    }

    //    public void choose(SendMessage message){
//        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
//        replyKeyboardMarkup.setResizeKeyboard(true);
//        replyKeyboardMarkup.setOneTimeKeyboard(true); //скрываем после использования
//
//        //Создаем список с рядами кнопок
//        List<KeyboardRow> keyboardRows = new ArrayList<>();
//        //Создаем один ряд кнопок и добавляем его в список
//        KeyboardRow row = new KeyboardRow();
//        row.add("Выбрать тест");
//        row.add("Просмотреть отчет");
//        row.add("Просмотреть отчет по одному тесту");
//        keyboardRows.add(row);
//        //добавляем лист с одним рядом кнопок в главный объект
//        replyKeyboardMarkup.setKeyboard(keyboardRows);
//        message.setReplyMarkup(replyKeyboardMarkup);
//
//    }
}
