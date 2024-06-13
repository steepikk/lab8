package commands;

import managers.AuthManager;
import network.requests.RegisterRequest;
import network.requests.Request;
import network.responses.RegisterResponse;
import network.responses.Response;

/**
 * Команда 'register'. Регистрирует пользователя.
 *
 * @author steepikk
 */
public class RegisterCommand extends Command {
    private final AuthManager authManager;
    private final int MAX_USERNAME_LENGTH = 40;

    public RegisterCommand(AuthManager authManager) {
        super("register", "зарегистрировать пользователя");
        this.authManager = authManager;
    }

    /**
     * Выполняет команду
     *
     * @param request Запрос к серверу.
     * @return Ответ сервера.
     */
    @Override
    public Response apply(Request request) {
        var req = (RegisterRequest) request;
        var user = req.getUser();
        if (user.getName().length() >= MAX_USERNAME_LENGTH) {
            return new RegisterResponse(user, "Длина имени пользователя должна быть < " + MAX_USERNAME_LENGTH);
        }

        try {
            var newUserId = authManager.registerUser(user.getName(), user.getPassword());

            if (newUserId <= 0) {
                return new RegisterResponse(user, "Не удалось создать пользователя.");
            } else {
                return new RegisterResponse(user.copy(newUserId), null);
            }
        } catch (Exception e) {
            return new RegisterResponse(user, e.toString());
        }
    }
}
