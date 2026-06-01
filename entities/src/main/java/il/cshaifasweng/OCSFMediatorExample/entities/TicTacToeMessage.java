package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class TicTacToeMessage implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -8224097662914849956L;

    public enum Type
    {
        JOIN, MOVE
    }

    private Type type;
    private int row;
    private int col;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public TicTacToeMessage(Type type) {
        this.type = type;
    }

    public TicTacToeMessage(Type type, String msg) {
        this.message = msg;
        this.type = type;
    }

    public static TicTacToeMessage join()
    {
        return new TicTacToeMessage(Type.JOIN);
    }

    public static TicTacToeMessage move(int row, int col)
    {
        TicTacToeMessage msg = new TicTacToeMessage(Type.MOVE);
        msg.setRow(row);
        msg.setCol(col);
        return msg;
    }

    public int getRow()
    {
        return row;
    }
    public int getCol()
    {
        return col;
    }
    public void setRow(int row)
    {
        this.row = row;
    }
    public void setCol(int col)
    {
        this.col = col;
    }
    public Type getType()
    {
        return type;
    }
}
