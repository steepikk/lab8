package commands;


import managers.CollectionManager;
import network.requests.RemoveGreaterRequest;
import network.requests.Request;
import network.responses.RemoveGreaterResponse;
import network.responses.Response;

/**
 * Команда 'remove_greater'. Удаляет из коллекции всех элементов больше заданного.
 *
 * @author steepikk
 */
public class RemoveGreaterCommand extends Command {
    private final CollectionManager collectionManager;

    public RemoveGreaterCommand(CollectionManager collectionManager) {
        super("remove_greater", "удаляет из коллекции всех элементов больше заданного");
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду
     *
     * @return Успешность выполнения команды.
     */
    @Override
    public Response apply(Request request) {
        var req = (RemoveGreaterRequest) request;

        try {
            var removedCount = collectionManager.removeGreater(req.getUser(), req.dragon, req.dragon.getCreatorId());
            if (removedCount <= 0) {
                return new RemoveGreaterResponse("Ничего не удалено!");
            }
            return new RemoveGreaterResponse(null);
        } catch (Exception e) {
            return new RemoveGreaterResponse(e.toString());
        }
    }
}
