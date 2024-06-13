package commands;

import managers.AuthManager;
import network.requests.AuthenticateRequest;
import network.requests.Request;
import network.responses.AuthenticateResponse;
import network.responses.Response;

/**
 * Команда 'authenticate'. Аутентифицирует пользователя по логину и паролю.
 *
 * @author steepikk
 */
public class AuthenticateCommand extends Command {
    private final AuthManager authManager;

    public AuthenticateCommand(AuthManager authManager) {
        super("authenticate", "аутентифицировать пользователя по логину и паролю");
        this.authManager = authManager;
    }

    /**
     * Выполняет команду
     * @param request Запрос к серверу.
     * @return Ответ сервера.
     */
    @Override
    public Response apply(Request request) {
        var req = (AuthenticateRequest) request;
        var user = req.getUser();
        try {
            var userId = authManager.authenticateUser(user.getName(), user.getPassword());

            if (userId <= 0) {
                return new AuthenticateResponse(user, "Нет такого пользователя.");
            } else {
                return new AuthenticateResponse(user.copy(userId), null);
            }
        } catch (Exception e) {
            return new AuthenticateResponse(user, e.toString());
        }
    }
}
