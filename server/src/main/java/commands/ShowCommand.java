package commands;

import managers.CollectionManager;
import network.requests.Request;
import network.responses.Response;
import network.responses.ShowResponse;

/**
 * Команда 'show'. Выводит все элементы коллекции.
 *
 * @author steepikk
 */
public class ShowCommand extends Command {
    private final CollectionManager collectionManager;

    public ShowCommand(CollectionManager collectionManager) {
        super("show", "вывести все элементы коллекции");
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
            return new ShowResponse(collectionManager.sorted(), null);
        } catch (Exception e) {
            return new ShowResponse(null, e.toString());
        }
    }
}
