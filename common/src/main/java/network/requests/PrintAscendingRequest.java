package network.requests;

import user.User;
import utility.Commands;

public class PrintAscendingRequest extends Request {
    public PrintAscendingRequest(User user) {
        super(Commands.PRINT_ASCENDING, user);
    }
}
