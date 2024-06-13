package network.requests;

import user.User;
import utility.Commands;

public class HelpRequest extends Request {
    public HelpRequest(User user) {
        super(Commands.HELP, user);
    }
}
