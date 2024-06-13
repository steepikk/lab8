package network.responses;

import data.Dragon;
import utility.Commands;

import java.util.List;

public class ShowResponse extends Response {
    public final List<Dragon> dragons;

    public ShowResponse(List<Dragon> dragons, String error) {
        super(Commands.SHOW, error);
        this.dragons = dragons;
    }
}