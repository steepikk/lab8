package commands;

import managers.CollectionManager;
import network.requests.Request;
import network.requests.UpdateRequest;
import network.responses.Response;
import network.responses.UpdateResponse;

/**
 * Команда 'update'. Обновляет элемент коллекции.
 *
 * @author steepikk
 */
public class UpdateCommand extends Command {
    private final CollectionManager collectionManager;

    public UpdateCommand(CollectionManager collectionManager) {
        super("update <ID> {element}", "обновить значение элемента коллекции по ID");
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду
     *
     * @return Успешность выполнения команды.
     */
    @Override
    public Response apply(Request request) {
        var req = (UpdateRequest) request;
        try {
            if (!collectionManager.checkExist(req.id)) {
                return new UpdateResponse("Дркона с таким ID в коллекции нет!");
            }
            if (!req.updatedDragon.validate()) {
                return new UpdateResponse("Поля дракона не валидны! Дракон не обновлен!");
            }

            //collectionManager.getById(req.id).update(req.updatedDragon);
            collectionManager.update(req.getUser(), req.updatedDragon);
            return new UpdateResponse(null);
        } catch (Exception e) {
            return new UpdateResponse(e.toString());
        }
    }
}
