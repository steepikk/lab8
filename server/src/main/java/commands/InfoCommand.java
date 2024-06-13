package commands;

import managers.CollectionManager;
import network.requests.Request;
import network.responses.InfoResponse;
import network.responses.Response;

/**
 * Команда 'info'. Выводит информацию о коллекции.
 *
 * @author steepikk
 */
public class InfoCommand extends Command {
    private final CollectionManager collectionManager;

    public InfoCommand(CollectionManager collectionManager) {
        super("info", "вывести информацию о коллекции");
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду
     *
     * @return Успешность выполнения команды.
     */
    @Override
    public Response apply(Request request) {
        var lastInitTime = collectionManager.getLastInitTime();
        var lastSaveTime = collectionManager.getLastSaveTime();
        return new InfoResponse(collectionManager.getType(), String.valueOf(collectionManager.getSize()), lastSaveTime, lastInitTime, null);
    }
}
