package network.requests;

import user.User;
import utility.Commands;

public class InfoRequest extends Request {
    public InfoRequest(User user) {
        super(Commands.INFO, user);
    }
}
