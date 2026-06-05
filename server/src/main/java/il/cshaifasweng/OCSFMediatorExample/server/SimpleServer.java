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
	private boolean player1WantsRematch = false;
	private boolean player2WantsRematch = false;

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
			else if (ticTacToeMessage.getType() == TicTacToeMessage.Type.REMATCH)
			{
				handleRematch(client);
			}
			else if (ticTacToeMessage.getType() == TicTacToeMessage.Type.DISCONNECT)
			{
				handleDisconnect(client);
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
		player1WantsRematch = false;
		player2WantsRematch = false;
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

	private boolean checkWinner(char symbol)
	{
		for (int i = 0; i < 3; i++)
		{
			if ((board[i][0] == symbol) && (board[i][1] == symbol) && (board[i][2] == symbol))
			{
				return true;
			}
		}
		for (int i = 0; i < 3; i++)
		{
			if ((board[0][i] == symbol) && (board[1][i] == symbol) && (board[2][i] == symbol))
			{
				return true;
			}
		}
		if ((board[0][0] == symbol) && (board[1][1] == symbol) && (board[2][2] == symbol))
		{
			return true;
		}

		if ((board[0][2] == symbol) && (board[1][1] == symbol) && (board[2][0] == symbol))
		{
			return true;
		}
		return false;
	}

	private boolean checkDraw()
	{
		for (int row = 0; row < 3; row++)
		{
			for (int col = 0; col < 3; col++)
			{
				if (board[row][col] != 'X' && board[row][col] != 'O')
				{
					return false;
				}
			}
		}
		return true;
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

		System.out.println(symbol + " selected cell [" + row + "][" + col + "]");

		if (checkWinner(symbol))
		{
			gameOver = true;
			sendGameMessage(player1, player1Symbol, TicTacToeMessage.Type.GAME_OVER, symbol + " won");
			sendGameMessage(player2, player2Symbol, TicTacToeMessage.Type.GAME_OVER, symbol + " won");
			return;
		}

		if (checkDraw())
		{
			gameOver = true;
			sendGameMessage(player1, player1Symbol, TicTacToeMessage.Type.GAME_OVER, "Draw!");
			sendGameMessage(player2, player2Symbol, TicTacToeMessage.Type.GAME_OVER, "Draw!");
			return;
		}

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

	private void handleRematch(ConnectionToClient client)
	{
		if (!gameOver)
		{
			sendGameMessage(client, getPlayerSymbol(client), TicTacToeMessage.Type.STATUS, "Game is not over yet.");
			return;
		}

		if (client == player1)
		{
			player1WantsRematch = true;
			sendGameMessage(player1, player1Symbol, TicTacToeMessage.Type.STATUS, "Waiting for your opponent to accept a rematch.");
			sendGameMessage(player2, player2Symbol, TicTacToeMessage.Type.STATUS, "Your opponent wants a rematch.");
		}
		else if (client == player2)
		{
			player2WantsRematch = true;
			sendGameMessage(player2, player2Symbol, TicTacToeMessage.Type.STATUS, "Waiting for your opponent to accept a rematch.");
			sendGameMessage(player1, player1Symbol, TicTacToeMessage.Type.STATUS, "Your opponent wants a rematch.");
		}

		if (player1WantsRematch && player2WantsRematch)
		{
			startGame();
		}
	}

	private void handleDisconnect(ConnectionToClient client)
	{
		try
		{
			if (client == player1)
			{
				if (player2 != null)
				{
					sendGameMessage(player2, player2Symbol, TicTacToeMessage.Type.WAITING, "Your opponent disconnected. Waiting for a new opponent...");
					player1 = player2;
					player2 = null;
				}
				else
					player1 = null;
			}
			else if (client == player2)
			{
				if (player1 != null)
				{
					sendGameMessage(player1, player1Symbol, TicTacToeMessage.Type.WAITING, "Your opponent disconnected. Waiting for a new opponent...");
				}
				player2 = null;
			}
			client.close();
			gameStarted = false;
			gameOver = false;
			player1WantsRematch = false;
			player2WantsRematch = false;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
