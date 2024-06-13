package network.requests;

import user.User;
import utility.Commands;

public class ShowRequest extends Request {
    public ShowRequest(User user) {
        super(Commands.SHOW, user);
    }
}
