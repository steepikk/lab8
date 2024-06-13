package network.requests;

import user.User;
import utility.Commands;

public class AuthenticateRequest extends Request {
    public AuthenticateRequest(User user) {
        super(Commands.AUTHENTICATE, user);
    }

    @Override
    public boolean isAuth() {
        return true;
    }
}
