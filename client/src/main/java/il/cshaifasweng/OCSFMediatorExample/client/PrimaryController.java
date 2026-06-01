package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import il.cshaifasweng.OCSFMediatorExample.entities.TicTacToeMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class PrimaryController {

	@FXML
	void initialize(){
		try {
			SimpleClient.getClient().sendToServer(TicTacToeMessage.join());
			statusLabel.setText("Joined the game.");
		} catch (IOException e) {
			statusLabel.setText("Failed to join the game.");
			e.printStackTrace();
		}
	}

	private void sendMove(int row, int col)
	{
		try {
			SimpleClient.getClient().sendToServer(TicTacToeMessage.move(row, col));
			statusLabel.setText("Move sent.");
		} catch (IOException e) {
			statusLabel.setText("Failed to send the move.");
			e.printStackTrace();
		}
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
}
