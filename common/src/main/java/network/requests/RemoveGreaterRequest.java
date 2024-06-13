package network.requests;

import data.Dragon;
import user.User;
import utility.Commands;

public class RemoveGreaterRequest extends Request {
    public final Dragon dragon;

    public RemoveGreaterRequest(Dragon dragon, User user) {
        super(Commands.REMOVE_GREATER, user);
        this.dragon = dragon;
    }
}
