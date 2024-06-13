package network.requests;

import user.User;
import utility.Commands;

public class CountLessThanAgeRequest extends Request{
    public final String age;

    public CountLessThanAgeRequest(String age, User user){
        super(Commands.COUNT_LESS_THAN_AGE, user);
        this.age = age;
    }
}
