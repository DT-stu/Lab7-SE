package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.TicTacToeMessage;
public class TicTacToeEvent {
    private TicTacToeMessage message;

    public TicTacToeMessage getMessage() {
        return message;
    }

    public TicTacToeEvent(TicTacToeMessage message) {
        this.message = message;
    }
}
