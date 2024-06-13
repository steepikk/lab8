package network.requests;

import user.User;
import utility.Commands;

public class RemoveRequest extends Request {
    public final int id;

    public RemoveRequest(int id, User user) {
        super(Commands.REMOVE_BY_ID, user);
        this.id = id;
    }
}
