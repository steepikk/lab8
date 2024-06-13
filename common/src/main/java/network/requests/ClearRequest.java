package network.requests;

import user.User;
import utility.Commands;

public class ClearRequest extends Request {
    public ClearRequest(User user) {
        super(Commands.CLEAR, user);
    }
}
