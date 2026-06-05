package il.cshaifasweng.OCSFMediatorExample.client;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import javafx.application.Platform;

import java.io.IOException;
import il.cshaifasweng.OCSFMediatorExample.entities.TicTacToeMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class PrimaryController {
	//variable that tracks if the game ended
	private boolean gameEnded = false;
	//initializing by disabling the board before the game starts and hiding the rematch button
	@FXML
	void initialize(){
		EventBus.getDefault().register(this);
		try
		{
			SimpleClient.getClient().sendToServer(TicTacToeMessage.join());
			signLabel.setText("");
			disableBoard(true);
			rematchButton.setVisible(false);
		}
		catch (IOException e)
		{
			statusLabel.setText("Failed to join the game.");
			e.printStackTrace();
		}
	}
	//helper function for sending a move to server for the board buttons
	private void sendMove(int row, int col)
	{
		try
		{
			SimpleClient.getClient().sendToServer(TicTacToeMessage.move(row, col));
		}
		catch (IOException e)
		{
			statusLabel.setText("Failed to send the move.");
			e.printStackTrace();
		}
	}
	//helper function for translating the character of the symbol to the text on the button for updating the board
	private String charToText(char c)
	{
		if (c == 'X' || c == 'O')
		{
			return String.valueOf(c);
		}
		return "";
	}
	//method for updating the board usually after a move
	private void updateBoard(char[][] board)
	{
		if (board == null)
		{
			return;
		}
		button00.setText(charToText(board[0][0]));
		button01.setText(charToText(board[0][1]));
		button02.setText(charToText(board[0][2]));
		button10.setText(charToText(board[1][0]));
		button11.setText(charToText(board[1][1]));
		button12.setText(charToText(board[1][2]));
		button20.setText(charToText(board[2][0]));
		button21.setText(charToText(board[2][1]));
		button22.setText(charToText(board[2][2]));
	}
	//method for disabling a board usually when it's the opponents turn
	private void disableBoard(boolean disabled)
	{
		button00.setDisable(disabled);
		button01.setDisable(disabled);
		button02.setDisable(disabled);
		button10.setDisable(disabled);
		button11.setDisable(disabled);
		button12.setDisable(disabled);
		button20.setDisable(disabled);
		button21.setDisable(disabled);
		button22.setDisable(disabled);
	}
	//method that receives updates and handles the GUI accordingly
	@Subscribe
	public void onTicTacToeEvent(TicTacToeEvent event)
	{
		Platform.runLater(() -> {
			TicTacToeMessage msg = event.getMessage();
			//updates board
			updateBoard(msg.getBoard());
			//updates the label according to the current symbol
			if (msg.getSymbol() == 'X' || msg.getSymbol() == 'O')
			{
				signLabel.setText("You are: " + msg.getSymbol());
			}
			//shows a message to the client in the label if there is one
			if (msg.getMessage() != null)
			{
				statusLabel.setText(msg.getMessage());
			}
			//if there's one player connected - keeps the board disabled
			if (msg.getType() == TicTacToeMessage.Type.WAITING)
			{
				gameEnded = false;
				signLabel.setText("");
				disableBoard(true);
				rematchButton.setVisible(false);
				return;
			}
			//if the game is over - shows the rematch button and disables the board
			if (msg.getType() == TicTacToeMessage.Type.GAME_OVER)
			{
				gameEnded = true;
				disableBoard(true);
				rematchButton.setVisible(true);
			}
			//if the game starts - hides the rematch button and enables the board of the starting player
			else if (msg.getType() == TicTacToeMessage.Type.START)
			{
				gameEnded = false;
				rematchButton.setVisible(false);

				if (msg.getCurrentTurn() == msg.getSymbol())
				{
					disableBoard(false);
				}
				else
				{
					disableBoard(true);
				}
			}
			//otherwise - enable the player whose turn it is and disable the others
			else if (msg.getSymbol() == 'X' || msg.getSymbol() == 'O')
			{
				if (!gameEnded)
				{
					rematchButton.setVisible(false);

					if (msg.getCurrentTurn() == msg.getSymbol())
					{
						disableBoard(false);
					}
					else
					{
						disableBoard(true);
					}
				}
			}
		});
	}

	@FXML
	private Button button00;

	@FXML
	private Button button01;

	@FXML
	private Button button02;

	@FXML
	private Button button10;

	@FXML
	private Button button11;

	@FXML
	private Button button12;

	@FXML
	private Button button20;

	@FXML
	private Button button21;

	@FXML
	private Button button22;

	@FXML
	private Button dcButton;

	@FXML
	private Button rematchButton;

	@FXML
	private Label signLabel;

	@FXML
	private Label statusLabel;

	@FXML
	void cell00(ActionEvent event) {
		sendMove(0,0);
	}

	@FXML
	void cell01(ActionEvent event) {
		sendMove(0,1);
	}

	@FXML
	void cell02(ActionEvent event) {
		sendMove(0,2);
	}

	@FXML
	void cell10(ActionEvent event) {
		sendMove(1,0);
	}

	@FXML
	void cell11(ActionEvent event) {
		sendMove(1,1);
	}

	@FXML
	void cell12(ActionEvent event) {
		sendMove(1,2);
	}

	@FXML
	void cell20(ActionEvent event) {
		sendMove(2,0);
	}

	@FXML
	void cell21(ActionEvent event) {
		sendMove(2,1);
	}

	@FXML
	void cell22(ActionEvent event) {
		sendMove(2,2);
	}
	//sends rematch request to server once the button is clicked
	@FXML
	void reClicked(ActionEvent event)
	{
		try
		{
			SimpleClient.getClient().sendToServer(TicTacToeMessage.rematch());
		}
		catch (IOException e)
		{
			statusLabel.setText("Failed to request rematch.");
			e.printStackTrace();
		}
	}
	//sends disconnect request to server once the button is clicked
	@FXML
	void dcClicked(ActionEvent event)
	{
		try
		{
			SimpleClient.getClient().sendToServer(TicTacToeMessage.disconnect());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		Platform.exit();
	}
}
