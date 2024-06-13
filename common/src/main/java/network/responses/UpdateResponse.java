package network.responses;

import utility.Commands;

public class UpdateResponse extends Response {
    public UpdateResponse(String error) {
        super(Commands.UPDATE, error);
    }
}
