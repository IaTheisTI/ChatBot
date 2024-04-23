package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


/**
 * Класс для реализации Телеграмм-бота
 */
public class TelegramBot extends TelegramLongPollingBot {

    /**
     * Экземпляр класса MessageHandling.
     * Эта переменная используется для хранения экземпляра обработки сообщения.
     */
    private Message message;

    /**
     * Конструктор класса TelegramBot, который инициализирует объекты Storage и MessageHandling.
     * Storage используется для управления базой данных с прочитанными книгами,
     * а MessageHandling - для обработки входящих сообщений от пользователя.
     */
    public TelegramBot() {
        message = new Message();
    }


    @Override
    public String getBotUsername() {
        return "QuestionBot";
    }

    @Override
    public String getBotToken() {
        return "6132329633:AAGUxR0T3UenS8dLqb70t_gQyBVoSVqOrSQ";
    }



    /**
     * Получение и Отправка сообщения в чат пользователю
     */
    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                // Извлекаем из объекта сообщение пользователя
                org.telegram.telegrambots.meta.api.objects.Message message = update.getMessage();
                String userMessage = message.getText();
                // Достаем из inMess id чата пользователя
                long chatId = message.getChatId();

                // Выводим сообщение пользователя в консоль
                System.out.println("TG User Message: " + userMessage);

                // Получаем текст сообщения пользователя, отправляем в написанный нами обработчик
                String response = this.message.parseMessage(userMessage, chatId);

                // Выводим ответ бота в консоль
                System.out.println("TG Bot Response: " + response);

                // Создаем объект класса SendMessage - наш будущий ответ пользователю
                SendMessage outMess = new SendMessage();
                // Добавляем в наше сообщение id чата, а также наш ответ
                outMess.setChatId(String.valueOf(chatId));
                outMess.setText(response);
                // Проверяем флаг awaitingRating

                // Отправка в чат
                execute(outMess);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}