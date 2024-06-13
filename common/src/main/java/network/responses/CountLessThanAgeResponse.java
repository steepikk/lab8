package network.responses;

import utility.Commands;

public class CountLessThanAgeResponse extends Response {
    public final int countDragons;

    public CountLessThanAgeResponse(int countDragons, String error) {
        super(Commands.COUNT_LESS_THAN_AGE, error);
        this.countDragons = countDragons;
    }
}
