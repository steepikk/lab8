package utility;

import network.requests.Request;
import network.responses.Response;

/**
 * Интерфейс для всех выполняемых команд.
 * @author steepikk
 */
public interface Executable {
    /**
     * Выполнить что-либо.
     * @param request запрос с данными для выполнения команды
     * @return результат выполнения
     */
    Response apply(Request request);
}
