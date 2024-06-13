package exceptions;

/**
 * Выбрасывается, если пользователь пытается изменить чужого дракона.
 *
 * @author steepikk
 */
public class BadOwnerException extends Exception {
    private final String message;

    public BadOwnerException(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
