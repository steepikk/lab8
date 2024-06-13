package network.responses;

import utility.Commands;

import java.util.List;

public class HistoryResponse extends Response {
    public final List<String> historyMessage;

    public HistoryResponse(List<String> historyMessage, String error) {
        super(Commands.HISTORY, error);
        this.historyMessage = historyMessage;
    }
}
