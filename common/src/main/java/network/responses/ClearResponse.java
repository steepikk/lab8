package network.responses;

import utility.Commands;

public class ClearResponse extends Response {
    public ClearResponse(String error) {
        super(Commands.CLEAR, error);
    }
}
