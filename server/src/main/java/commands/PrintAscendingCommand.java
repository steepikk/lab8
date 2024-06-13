package commands;

import managers.CollectionManager;
import network.requests.PrintAscendingRequest;
import network.requests.Request;
import network.responses.PrintAscendingResponse;
import network.responses.Response;

/**
 * Команда 'print_ascending'. Выводит коллекцию в порядке возрастания.
 *
 * @author steepikk
 */
public class PrintAscendingCommand extends Command {
    private final CollectionManager collectionManager;

    public PrintAscendingCommand(CollectionManager collectionManager) {
        super("print_ascending", "выводит коллекцию в порядке возрастания");
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду
     *
     * @return Успешность выполнения команды.
     */
    @Override
    public Response apply(Request request) {
        var req = (PrintAscendingRequest) request;
        try {
            var list = collectionManager.printAscending();
            return new PrintAscendingResponse(list, null);
        } catch (Exception e) {
            return new PrintAscendingResponse(null, e.toString());
        }
    }
}
