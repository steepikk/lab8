package exceptions;

import network.responses.ErrorResponse;

/**
 * Выбрасывается, если возвращена ошибка.
 *
 * @author steepikk
 */
public class ErrorResponseException extends Exception {
    private final ErrorResponse response;

    public ErrorResponseException(ErrorResponse response) {
        this.response = response;
    }

    public ErrorResponse getResponse() {
        return response;
    }

    @Override
    public String getMessage() {
        return response.getError();
    }
}
