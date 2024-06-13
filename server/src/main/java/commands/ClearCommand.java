package commands;

import managers.CollectionManager;
import network.requests.Request;
import network.responses.ClearResponse;
import network.responses.Response;

/**
 * Команда 'clear'. Очищает коллекцию.
 *
 * @author steepikk
 */
public class ClearCommand extends Command {
    private final CollectionManager collectionManager;

    public ClearCommand(CollectionManager collectionManager) {
        super("clear", "очистить коллекцию");
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду
     *
     * @return Успешность выполнения команды.
     */
    @Override
    public Response apply(Request request) {
        try {
            collectionManager.clearCollection(request.getUser());
            return new ClearResponse(null);
        } catch (Exception e) {
            return new ClearResponse(e.toString());
        }
    }
}
