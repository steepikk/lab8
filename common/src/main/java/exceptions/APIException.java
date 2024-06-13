package exceptions;

/**
 * Выбрасывается, если в ответе сервера ошибка
 *
 * @author steepikk
 */
public class APIException extends Exception {
    public APIException(String message) {
        super(message);
    }
}