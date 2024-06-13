package network.requests;

import user.User;
import utility.Commands;

public class RegisterRequest extends Request {
    public RegisterRequest(User user) {
        super(Commands.REGISTER, user);
    }

    @Override
    public boolean isAuth() {
        return true;
    }
}
