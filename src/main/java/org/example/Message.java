package org.example;

import java.util.*;


/**
 * Класс для обработки сообщений пользователя
 */
public class Message {

    private Storage storage;
    private QuestionGame questionGame;
    private boolean puzzleMode;
    private boolean awaitingRating;
    private boolean awaitingAuthor;
    private boolean awaitingAuthorForGamesByAuthor;
    private boolean awaitingYearForGamesByYear;
    private boolean awaitingGameNumberForRemoval;
    private boolean awaitingGameNumberForEdit;
    private String lastAddedGameTitle;
    private String lastAddedGameAuthor;
    private int lastAddedGameYear;
    private int lastAddedGameNumber;
    private long lastAddedGameChatId;
    private boolean awaitingTitle;
    private boolean awaitingYear;
    private boolean awaitingEditYear;
    private boolean awaitingEditAuthor;
    private boolean awaitingEditTitle;
    private boolean awaitngStart;
    private boolean awaitingcancel;



    /**
     * Конструктор класса MessageHandling. Инициализирует объекты Storage и PuzzleGame,
     * а также устанавливает начальное значение режима головоломки как false.
     */
    public Message() {
        storage = new Storage();
        questionGame = new QuestionGame();
        puzzleMode = false;
        awaitingRating = false;
        awaitingAuthor = false;
        awaitingTitle = false;
        awaitingYear = false;
        awaitingAuthorForGamesByAuthor = false;
        awaitingYearForGamesByYear = false;
        awaitingGameNumberForRemoval = false;
        awaitingGameNumberForEdit = false;
        awaitingEditYear  = false;
        awaitingEditAuthor = false;
        awaitingEditTitle = false;
        awaitngStart = false;
        awaitingcancel = false;
    }


    /**
     * Метод для обработки входящего текстового сообщения от пользователя.
     *
     * @param textMsg Входящий текстовый запрос от пользователя.
     * @param chatId  Идентификатор чата пользователя.
     * @return Ответ на запрос пользователя в виде строки.
     */

    public String parseMessage(String textMsg, long chatId) {
        String response;

        if (awaitingEditYear){
            response = handleEditYear(textMsg, chatId);
        }else if (awaitingEditAuthor) {
            response = handleEditAuthor(textMsg, chatId);
        }else if (awaitingEditTitle) {
            response = handleEditTitle(textMsg, chatId);
        } else if (awaitingGameNumberForEdit){
            response = handleEditNumber(textMsg, chatId);
        }else if (awaitingTitle) {
            response = handleAddTitle(textMsg, chatId);
        } else if (awaitingAuthor) {
            response = handleAddAuthor(textMsg, chatId);
        } else if (awaitingYear) {
            response = handleAddYear(textMsg, chatId);
        } else if (awaitingRating) {
            response = handleRating(textMsg);
        } else if (awaitingGameNumberForRemoval) {
            response = handleRemoveGame(textMsg, chatId);
        } else if (puzzleMode) {
            response = handleQuestionMode(textMsg, chatId);
        } else {
            response = handleDefaultMode(textMsg, chatId);
            awaitngStart = true;
            awaitingcancel = false;
        }

        return response;
    }


    /**
     * Обработчик сообщений в режиме головоломки.
     * @param textMsg Входящий текстовый запрос от пользователя.
     * @param chatId  Идентификатор чата пользователя.
     * @return Ответ на запрос пользователя в режиме головоломки.
     */
    private String handleQuestionMode(String textMsg, long chatId) {
        String response;
        if ((textMsg.equalsIgnoreCase("дай подсказку"))||(textMsg.equals("/gethint"))) {
            response = questionGame.getHint();
        } else if (textMsg.equals("/stoppuzzle")) {
            response = "Режим головоломки завершен.\n" + questionGame.getStatistics(chatId);

            puzzleMode = false; // Выход из режима головоломки
        }else if (textMsg.startsWith("/checkanswer")) {
            // Извлеките ответ пользователя из сообщения
            String userAnswer = textMsg.substring("/checkanswer".length()).trim();
            // Передайте ответ пользователя в PuzzleGame для проверки
            response = questionGame.checkAnswer(chatId, userAnswer);
        } else {
            response = questionGame.checkAnswer(chatId, textMsg);
        }
        return response;
    }

    /**
     * Позволяет отменить действие с помощью кнопки
     *
     * @param textMsg текст сообщения с командой
     * @param chatId id беседы, в которой пришло сообщение
     * @return Ответ на отмену действия
     */
    private String CancelButton(String textMsg, long chatId){
        String response = null;
        awaitingcancel = true;
        if (textMsg.equals("Отменить")){
            awaitingRating = false;
            awaitingAuthor = false;
            awaitingTitle = false;
            awaitingYear = false;
            awaitingAuthorForGamesByAuthor = false;
            awaitingYearForGamesByYear = false;
            awaitingGameNumberForRemoval = false;
            awaitingGameNumberForEdit = false;
            awaitingEditYear  = false;
            awaitingEditAuthor = false;
            awaitingEditTitle = false;
            awaitngStart = true;
            response = "Процедура отменена";
        }
        return response;
    }

    /**
     * Обрабатывает команду добавления названия новой игры.
     *
     * @param textMsg текст сообщения с командой и данными о новой игре
     * @param chatId id беседы, в которой пришло сообщение
     * @return стринговый ответ для пользователя
     */
    public String handleAddTitle(String textMsg, long chatId) {
        String response;
        awaitingcancel = true;
        if (!awaitingTitle) {
            response = "Введите название игры:";
            awaitingTitle = true;
        }else if(textMsg.equals("Отменить")){
            awaitingTitle = false;
            response = "Отменено";
            awaitngStart = true;
            awaitingcancel = false;
        }
        else {
            if (textMsg.contains("\n") || (textMsg.contains("  "))){
                response = "Неверный формат. Введите название игры";
            }
            else {
                CancelButton(textMsg, chatId);
                awaitingTitle = false;
                lastAddedGameTitle = textMsg.trim();
                response = "Введите издателя игры:";
                awaitingAuthor = true;
            }
        }

        return response;
    }


    /**
     * Обрабатывает команду добавления издателя к новой игре.
     *
     * @param textMsg текст сообщения с командой и данными о новой игре
     * @param chatId id беседы, в которой пришло сообщение
     * @return стринговый ответ для пользователя
     */
    public String handleAddAuthor(String textMsg, long chatId) {
        String response;
        CancelButton(textMsg, chatId);
        if (!awaitingAuthor) {
            response = "Введите издателя игры";
            awaitingAuthor = true;
        } else {
            if (textMsg.contains("\n") || (textMsg.contains("  "))){
                response = "Неверный формат. Введите издателя игры";
            }
            else {
                awaitingAuthor = false;
                lastAddedGameAuthor = textMsg.trim();
                lastAddedGameChatId = chatId;
                response = "Введите год выхода игры:";
                awaitingYear = true;
            }
        }

        return response;
    }


    /**
     * Обрабатывает команду добавления года к новой игре.
     *
     * @param textMsg текст сообщения с командой и данными о новой игре
     * @param chatId id беседы, в которой пришло сообщение
     * @return стринговый ответ для пользователя
     */
    public String handleAddYear(String textMsg, long chatId) {
        String title = lastAddedGameTitle;
        String response;
        String author = lastAddedGameAuthor;
        CancelButton(textMsg, chatId);

        if (!awaitingYear) {
            response = "Введите год выхода игры.";
            awaitingYear = true;
            // Проверка формата
        }else if (!textMsg.matches("\\d{4}") || textMsg.matches("")) {
            response = "Некорректный формат года. Пожалуйста, введите четыре цифры без букв.";
            return response;
        }
        int year = Integer.parseInt(textMsg.trim());
        if (storage.gameExists(title, author, year, chatId)) {
            awaitingTitle = true;
            awaitingAuthor = false;
            awaitingYear = false;
            awaitingRating = false;
            response = "Игра с таким названием, автором и годом уже существует. Пожалуйста, введите название заново:";
        } else {
            lastAddedGameYear = year;
            lastAddedGameChatId = chatId;
            awaitingYear = false;
            awaitingRating = true;
            response = "Игра '" + title + "' издателя " + author + " (" + year + ") успешно добавлена!\nОцените игру от 1 до 5:";
        }
        return response;
    }


    /**
     * Обрабатывает ввод рейтинга для недавно добавленной игры.
     *
     * @param textMsg текст сообщения с введённым рейтингом
     * @return стринговый ответ для пользователя
     */
    public String handleRating(String textMsg) {
        String response;

        try {
            int rating = Integer.parseInt(textMsg.trim());

            // Добавьте дополнительную проверку на допустимые значения рейтинга
            if (rating >= 1 && rating <= 5) {
                // Обновьте базу данных с рейтингом
                storage.addPlayedGame(lastAddedGameTitle, lastAddedGameAuthor, lastAddedGameYear, rating, lastAddedGameChatId);
                awaitingRating = false;
                response = "Отзыв " + rating;
            } else {
                response = "Пожалуйста, введите оценку от 1 до 5.";
            }
        } catch (NumberFormatException e) {
            response = "Некорректный формат оценки. Пожалуйста, введите числовое значение от 1 до 5.";
        }

        return response;
    }


    // Метод обработки сообщения для удаления игры
    public String handleRemoveGame(String textMsg, long chatId) {
        String response;
        CancelButton(textMsg, chatId);

        if (awaitingGameNumberForRemoval) {
            try {
                int gameNumber = Integer.parseInt(textMsg.trim());
                List<String> playedGames = storage.getPlayedGames(chatId);

                if (gameNumber >= 1 && gameNumber <= playedGames.size()) {
                    String removedGame = playedGames.remove(gameNumber - 1);
                    storage.updatePlayedGames(chatId, playedGames);
                    awaitingGameNumberForRemoval = false;
                    response = "Игра '" + removedGame + "' успешно удалена из списка пройденных!";
                } else {
                    response = "Указанный номер игры не существует.";
                }
            } catch (NumberFormatException e) {
                response = "Некорректный формат номера игры. Пожалуйста, введите число.";
            }

        } else {
            // Запросим номер игры для удаления
            response = "Введите номер игры, которую вы хотите удалить из списка пройденных:";
            awaitingGameNumberForRemoval = true;
        }

        return response;
    }



    public String handleEditNumber(String textMsg, long chatId) {
        String response;
        CancelButton(textMsg, chatId);
        int gameNumber = Integer.parseInt(textMsg.trim());
        List<String> playedGames = storage.getPlayedGames(chatId);

        if (!awaitingGameNumberForEdit) {
            response = "Введите номер игры";
            awaitingGameNumberForEdit = true;
        } else {
            if (gameNumber >= 1 && gameNumber <= playedGames.size()) {
                lastAddedGameNumber = gameNumber;
                awaitingGameNumberForEdit = false;
                lastAddedGameTitle = textMsg.trim();
                response = "Введите новое название игры.";
                awaitingEditTitle = true;
            }
            else {
                response = "Неверный формат. Введите номер игры из списка /getplayed";
            }
        }

        return response;
    }


    /**
     * Обрабатывает команду добавления названия новой игры.
     *
     * @param textMsg текст сообщения с командой и данными о новой игре
     * @param chatId id беседы, в которой пришло сообщение
     * @return стринговый ответ для пользователя
     */
    public String handleEditTitle(String textMsg, long chatId) {
        String response;
        CancelButton(textMsg, chatId);

        if (!awaitingEditTitle) {
            response = "Введите новое название игры";
            awaitingEditTitle = true;
        } else {
            if (textMsg.contains("\n") || (textMsg.contains("  "))){
                response = "Неверный формат. Введите название игры";
            }
            else {
                awaitingEditTitle = false;
                lastAddedGameTitle = textMsg.trim();
                response = "Введите нового издателя игры.";
                awaitingEditAuthor = true;
            }
        }

        return response;
    }


    /**
     * Обрабатывает команду добавления издателя к новой игре.
     *
     * @param textMsg текст сообщения с командой и данными о новой игре
     * @param chatId id беседы, в которой пришло сообщение
     * @return стринговый ответ для пользователя
     */
    public String handleEditAuthor(String textMsg, long chatId) {
        String response;
        CancelButton(textMsg, chatId);
        if (!awaitingEditAuthor) {
            response = "Введите нового издателя игры";
            awaitingEditAuthor = true;
        } else {
            if (textMsg.contains("\n") || (textMsg.contains("  "))){
                response = "Неверный формат. Введите издателя игры";
            }
            else {
                awaitingEditAuthor = false;
                lastAddedGameAuthor = textMsg.trim();
                lastAddedGameChatId = chatId;
                response = "Введите год новый выхода игры.";
                awaitingEditYear = true;
            }
        }

        return response;
    }


    /**
     * Обрабатывает команду изменения года к новой игре.
     *
     * @param textMsg текст сообщения с командой и данными о новой игре
     * @param chatId id беседы, в которой пришло сообщение
     * @return стринговый ответ для пользователя
     */
    public String handleEditYear(String textMsg, long chatId) {
        CancelButton(textMsg, chatId);
        int gameNumber;
        String newTitle;
        String newAuthor;
        int newYear;
        newTitle = lastAddedGameTitle;
        newAuthor = lastAddedGameAuthor;
        // Проверка формата
        if (!textMsg.matches("\\d{4}") || textMsg.matches(".*[a-zA-Z].*")) {
            return "Некорректный формат года. Пожалуйста, введите четыре цифры без букв.";
        }
        newYear = Integer.parseInt(textMsg.trim());
        if (storage.gameExists(newTitle, newAuthor, newYear, chatId)) {
            awaitingEditTitle = true;
            awaitingEditAuthor = false;
            awaitingEditYear = false;
            return "Игра с таким названием, автором и годом уже существует. Пожалуйста, введите название заново:";
        } else {
            gameNumber = lastAddedGameNumber;
            List<String> playedGames = storage.getAllValues(chatId);
            String[] oldGameParts = playedGames.get(gameNumber - 1).split("\n");
            String oldTitle = oldGameParts[0];
            String oldAuthor = oldGameParts[1];
            int oldYear = Integer.parseInt(oldGameParts[2]);

            // Заменяем книгу в базе данных
            storage.editPlayedGame(oldTitle, oldAuthor, oldYear, newTitle, newAuthor, newYear, chatId);

            awaitingEditYear = false;
            return "Игра '" + oldTitle + "' успешно заменена на игру '" + newTitle + "' от издателя " + newAuthor + " (" + newYear + ") в списке пройденных!";
        }
    }




    /**
     * Обработчик сообщений в режиме по умолчанию.
     *
     * @param textMsg Входящий текстовый запрос от пользователя.
     * @param chatId  Идентификатор чата пользователя.
     * @return Ответ на запрос пользователя в режиме по умолчанию.
     */
    private String handleDefaultMode(String textMsg, long chatId) {
        String response;
        awaitngStart = true;
        // Сравниваем текст пользователя с командами, на основе этого формируем ответ
        if (textMsg.equals("/start") || textMsg.equals("Старт")) {
            response = "Привет, я игровой бот. Жми /help, чтобы узнать что я могу.";
        } else if (textMsg.equals("/help") || textMsg.equals("Помощь")){
            response = """
                    Привет, я умею:
                    /addgame - Добавить игру, которую ты уже прошел
                    /getplayed - Список пройденных игр
                    /playpuzzle - Быстрый квиз по разным темам для развлечения
                    """
            ;


        }else if (textMsg.startsWith("/addgame") || textMsg.equals("Добавить_игру")){
            String titleRequest = "Введите название игры:";
            response = titleRequest;
            awaitingTitle = true;
            awaitingcancel = true;

        } else if (textMsg.equals("/clearplayed")) {
            // Очищаем список пройденных игр
            storage.clearPlayedGames(chatId);
            response = "Список пройденных игр очищен!";


        } else if (textMsg.equals("/getplayed") || textMsg.equals("Список_игр")) {
            // Получаем список пройденных игр с уникальными номерами
            List<String> playedGames = storage.getPlayedGames(chatId);
            if (playedGames.isEmpty()) {
                response = "Список пройденных игр пуст.";
            } else {
                StringBuilder responseBuilder = new StringBuilder("Пройденные игры:\n");
                for (int i = 0; i < playedGames.size(); i++) {
                    responseBuilder.append(i + 1).append(". ").append(playedGames.get(i)).append("\n");
                }
                response = responseBuilder.toString();
            }


        } else if (textMsg.startsWith("/getbyauthor")) {
            String authorRequest = "Введите имя автора:";
            response = authorRequest;
            awaitingcancel = true;
            awaitingAuthorForGamesByAuthor = true; // Флаг ожидания имени автора для команды /getbyauthor


        } else if (textMsg.startsWith("/getbyyear")) {
            String yearRequest = "Введите год (не более 4 цифр):";
            response = yearRequest;
            awaitingcancel = true;
            awaitingYearForGamesByYear = true; // Флаг ожидания года для команды /getbyyear


        } else if (textMsg.startsWith("/getbyrating")) {
            // Обработка команды /getbyrating
            List<String> gamesByRating = storage.getGamesByAverageRating(chatId);

            if (!gamesByRating.isEmpty()) {
                response = "Список игр по среднему рейтингу:\n";
                for (String game : gamesByRating) {
                    response += game + "\n";
                }
            } else {
                response = "Нет данных о среднем рейтинге игр.";
            }


        } else if (textMsg.startsWith("/removegame")) {
            String removeRequest = "Введите номер игры, которую нужно удалить:";
            response = removeRequest;
            awaitingcancel = true;
            awaitingGameNumberForRemoval = true; // Флаг ожидания года для команды /getbyyear



        } else if (textMsg.equals("/playpuzzle") || textMsg.equals("Загадки")) {
            // Вход в режим головоломки
            puzzleMode = true;
            awaitingcancel = true;
            response = questionGame.startPuzzle(chatId);
        }

        else {
            response = textMsg;
        }
        return response;
    }


}