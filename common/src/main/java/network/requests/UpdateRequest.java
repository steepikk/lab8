package network.requests;

import data.Dragon;
import user.User;
import utility.Commands;

public class UpdateRequest extends Request {
    public final int id;
    public final Dragon updatedDragon;

    public UpdateRequest(int id, Dragon updatedDragon, User user) {
        super(Commands.UPDATE, user);
        this.id = id;
        this.updatedDragon = updatedDragon;
    }
}
