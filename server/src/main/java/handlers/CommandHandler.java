package handlers;

import main.App;
import managers.AuthManager;
import managers.CommandManager;
import network.requests.Request;
import network.responses.BadCredentialsResponse;
import network.responses.ErrorResponse;
import network.responses.NoSuchCommandResponse;
import network.responses.Response;

import java.sql.SQLException;

/**
 * Класс для обработки команд, поступающих от клиента.
 *
 * @author steepikk
 */
public class CommandHandler {
    private final CommandManager manager;
    private final AuthManager authManager;

    /**
     * Конструктор класса CommandHandler.
     * Создает новый объект CommandHandler.
     *
     * @param manager     объект CommandManager, используемый для управления командами.
     * @param authManager объект AuthManager, используемый для аутентификации пользователей.
     */
    public CommandHandler(CommandManager manager, AuthManager authManager) {
        this.manager = manager;
        this.authManager = authManager;
    }

    /**
     * Обрабатывает запрос, поступающий от клиента.
     * Выполняет аутентификацию пользователя и запускает соответствующую команду.
     *
     * @param request объект Request, представляющий запрос от клиента.
     * @return объект Response, представляющий ответ на запрос.
     */
    public Response handle(Request request) throws InterruptedException {
        if (!request.isAuth()) {
            var user = request.getUser();
            try {
                if (user == null || authManager.authenticateUser(user.getName(), user.getPassword()) <= 0) {
                    return new BadCredentialsResponse("Неверные учетные данные. Пожалуйста, войдите в свой аккаунт. \n \nДля того, чтобы войти в аккаунт воспользуйтесь командой 'authenticate'. Если вы ещё не зарегистрированы, воспользуйтесь командой 'register'.");
                }
            } catch (SQLException e) {
                App.logger.error("Невозможно выполнить запрос к БД о аутентификации пользователя.", e);
                return new ErrorResponse("sql_error", "Невозможно выполнить запрос к БД о аутентификации пользователя.");
            }
        }

        if (!request.getName().equals("show") && !request.getName().equals("authenticate") && !request.getName().equals("register")) {
            manager.addToHistory(request.getName());
        }
        var command = manager.getCommands().get(request.getName());
        if (command == null) return new NoSuchCommandResponse(request.getName());
        return command.apply(request);
    }
}
