package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;

import il.cshaifasweng.OCSFMediatorExample.entities.TicTacToeMessage;
import java.util.Random;

public class SimpleServer extends AbstractServer
{
	private ConnectionToClient player1 = null;
	private ConnectionToClient player2 = null;
	private char[][] board = new char[3][3];
	private char player1Symbol;
	private char player2Symbol;
	private char currentTurn;
	private boolean gameStarted = false;
	private boolean gameOver = false;
	private Random random = new Random();

	public SimpleServer(int port) {
		super(port);
		
	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client)
	{

		if (msg instanceof TicTacToeMessage)
		{
			TicTacToeMessage ticTacToeMessage = (TicTacToeMessage) msg;

			if (ticTacToeMessage.getType() == TicTacToeMessage.Type.JOIN)
			{
				handleJoin(client);
			}
			else if (ticTacToeMessage.getType() == TicTacToeMessage.Type.MOVE)
			{
				handleMove(ticTacToeMessage, client);
			}
			return;
		}
	}

	private void handleJoin(ConnectionToClient client)
	{
		try
		{
			if (player1 == null)
			{
				player1 = client;

				TicTacToeMessage waitingMessage = new TicTacToeMessage(TicTacToeMessage.Type.WAITING, "Waiting for your opponent...");
				client.sendToClient(waitingMessage);
				System.out.println("Player 1 joined.");
			}
			else if (player2 == null && client != player1)
			{
				player2 = client;

				System.out.println("Player 2 joined.");
				System.out.println("2 players connected. Starting the game...");
				startGame();
			}
			else
			{
				TicTacToeMessage fullMessage = new TicTacToeMessage(TicTacToeMessage.Type.WAITING, "Game already has 2 players.");
				client.sendToClient(fullMessage);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void startGame()
	{
		board = new char[3][3];
		gameStarted = true;
		gameOver = false;
		if (random.nextBoolean())
		{
			player1Symbol = 'X';
			player2Symbol = 'O';
		}
		else
		{
			player1Symbol = 'O';
			player2Symbol = 'X';
		}
		if (random.nextBoolean())
		{
			currentTurn = 'X';
		}
		else
		{
			currentTurn = 'O';
		}
		System.out.println("Player 1 is " + player1Symbol);
		System.out.println("Player 2 is " + player2Symbol);
		System.out.println(currentTurn + " starts.");
		sendGameMessage(player1, player1Symbol, TicTacToeMessage.Type.START, "Game started. " + currentTurn + " starts.");
		sendGameMessage(player2, player2Symbol, TicTacToeMessage.Type.START, "Game started. " + currentTurn + " starts.");
	}

	private void sendGameMessage(ConnectionToClient player, char symbol, TicTacToeMessage.Type type, String message)
	{
		try
		{
			TicTacToeMessage gameMessage = new TicTacToeMessage(type, message);
			gameMessage.setBoard(board);
			gameMessage.setSymbol(symbol);
			gameMessage.setCurrentTurn(currentTurn);

			player.sendToClient(gameMessage);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private char getPlayerSymbol(ConnectionToClient client)
	{
		if (client == player1)
		{
			return player1Symbol;
		}
		else if (client == player2)
		{
			return player2Symbol;
		}
		else
		{
			return ' ';
		}
	}

	private void handleMove(TicTacToeMessage ticTacToeMessage, ConnectionToClient client)
	{
		char symbol = getPlayerSymbol(client);

		if (!gameStarted)
		{
			sendGameMessage(client, symbol, TicTacToeMessage.Type.STATUS, "The game has not started yet.");
			return;
		}
		if (gameOver)
		{
			sendGameMessage(client, symbol, TicTacToeMessage.Type.STATUS, "The game is already over.");
			return;
		}
		if (symbol != currentTurn)
		{
			sendGameMessage(client, symbol, TicTacToeMessage.Type.STATUS, "It is not your turn.");
			return;
		}

		int row = ticTacToeMessage.getRow();
		int col = ticTacToeMessage.getCol();

		if (board[row][col] == 'X' || board[row][col] == 'O')
		{
			sendGameMessage(client, symbol, TicTacToeMessage.Type.STATUS, "Cell is already taken.");
			return;
		}

		board[row][col] = symbol;

		System.out.println("player" + symbol + " selected cell [" + row + "][" + col + "]");

		if (currentTurn == 'X')
		{
			currentTurn = 'O';
		}
		else
		{
			currentTurn = 'X';
		}

		sendGameMessage(player1, player1Symbol, TicTacToeMessage.Type.STATUS, "Turn: " + currentTurn);
		sendGameMessage(player2, player2Symbol, TicTacToeMessage.Type.STATUS, "Turn: " + currentTurn);
	}
}
