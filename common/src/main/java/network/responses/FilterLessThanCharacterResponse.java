package network.responses;

import data.Dragon;
import utility.Commands;

import java.util.List;

public class FilterLessThanCharacterResponse extends Response{
    public final List<Dragon> filteredDragons;
    public FilterLessThanCharacterResponse(List<Dragon> filteredDragons, String error){
        super(Commands.FILTER_LESS_THAN_CHARACTER, error);
        this.filteredDragons = filteredDragons;
    }
}
