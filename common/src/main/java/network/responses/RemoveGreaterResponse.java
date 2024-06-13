package network.responses;

import utility.Commands;

public class RemoveGreaterResponse extends Response {
    public RemoveGreaterResponse(String error) {
        super(Commands.REMOVE_GREATER, error);
    }
}
