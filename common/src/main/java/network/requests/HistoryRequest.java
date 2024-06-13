package network.requests;

import user.User;
import utility.Commands;

public class HistoryRequest extends Request {
    public HistoryRequest(User user) {
        super(Commands.HISTORY, user);
    }
}
