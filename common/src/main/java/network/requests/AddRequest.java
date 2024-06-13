package network.requests;

import data.Dragon;
import user.User;
import utility.Commands;

public class AddRequest extends Request {
    public final Dragon dragon;

    public AddRequest(Dragon dragon, User user) {
        super(Commands.ADD, user);
        this.dragon = dragon;
    }
}
