package network.responses;

import data.Dragon;
import utility.Commands;

import java.util.List;

public class PrintAscendingResponse extends Response {
    public final List<Dragon> filteredDragons;

    public PrintAscendingResponse(List<Dragon> filteredDragons, String error) {
        super(Commands.PRINT_ASCENDING, error);
        this.filteredDragons = filteredDragons;
    }
}
