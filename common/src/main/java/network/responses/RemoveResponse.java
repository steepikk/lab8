package network.responses;

import utility.Commands;

public class RemoveResponse extends Response {
    public RemoveResponse(String error) {
        super(Commands.REMOVE_BY_ID, error);
    }
}
