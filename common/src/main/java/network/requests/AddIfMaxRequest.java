package network.requests;

import data.Dragon;
import user.User;
import utility.Commands;

public class AddIfMaxRequest extends Request {
    public final Dragon dragon;

    public AddIfMaxRequest(Dragon dragon, User user) {
        super(Commands.ADD_IF_MAX, user);
        this.dragon = dragon;
    }
}
