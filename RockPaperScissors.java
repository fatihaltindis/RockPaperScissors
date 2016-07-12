import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

public class RockPaperScissors {
	
	public static void main(String[] args) throws IOException {
		boolean isServer = args.length>1 ? false:true;
		
		if(!isServer){
			while(true){
				String messageToServer = "SHAPE\r\n";
				int numberOfRounds;
				Scanner scanner = new Scanner(System.in);
				print("Number of rounds (type -1 to quit):");
				numberOfRounds = scanner.nextInt();
				if(numberOfRounds == -1){
					System.exit(0);
				}

				Socket cSocket = new Socket(args[0], Integer.parseInt(args[1]));
				DataOutputStream dos = new DataOutputStream(cSocket.getOutputStream());
				BufferedReader br = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));
				
				println("Please type  rock | paper | scissors");
				for(int i=0; i<numberOfRounds; i++)
				{
					print("Round "+(i+1)+" :");
					String input = scanner.next();
					if(input.equals("rock"))
						messageToServer += ("ROCK\r\n");
					else if(input.equals("paper"))
						messageToServer += ("PAPER\r\n");
					else if(input.equals("scissors"))
						messageToServer += ("SCISSORS\r\n");
				}
				messageToServer += "\r\n";
				//send request to server
				dos.write(messageToServer.getBytes());
				//get result from server
				String line = br.readLine();
				while(!line.equals(""))
				{
					println(line);
					line = br.readLine();
				}
			}
		}else	// server side
		{
			println("Server Running ...");
			ServerSocket socket = new ServerSocket(Integer.parseInt(args[0]));
			Socket conSock;
			BufferedReader reader;
			while(true)
			{
				String resultMessage = "RESULT\r\n";
				conSock = socket.accept();
				reader = new BufferedReader(new InputStreamReader(
						conSock.getInputStream()));
				DataOutputStream dos = new DataOutputStream(conSock.getOutputStream());
				int [] clientShapes = new int[100];
				int [] serverShapes = new int[100];
				int i = 0;
				String line = reader.readLine();
				while(!line.equals(""))				// compaering lines
				{
					if(line.equals("ROCK"))			// 1 for ROCK
					{
						clientShapes[i] = 1;
						i++;
					}else if(line.equals("PAPER"))	// 2 for PAPER
					{
						clientShapes[i] = 2; 
						i++;
					}else if(line.equals("SCISSORS"))	// 3 for SCISSOR
					{
						clientShapes[i] = 3;
						i++;
					}
					line = reader.readLine();
				}
				Random random = new Random();
				int randomNumber;
				for(int j=0; j<i; j++){ // choose random shapes for server
					randomNumber = random.nextInt(3000);
					if(randomNumber<1000)
						serverShapes[j] = 1;
					else if(randomNumber<2000)
						serverShapes[j] = 2;
					else
						serverShapes[j] = 3;
				}
				
				// deciding the winner of the turn
				int serverWins = 0, clientWins =0, tie = 0;
				for(int j=0; j<i; j++)
				{
					if(serverShapes[j] == clientShapes[j]) 	// if tie
						tie++;
					else if(serverShapes[j] == 1)		   	// if server is rock
					{
						if(clientShapes[j] == 2) 			// if client is paper
							clientWins++;					// client wins
						else
							serverWins++;					// if client is scissor server wins
					}
					else if(serverShapes[j] == 2)			// if server is paper
					{
						if(clientShapes[j] == 1)			// if client is rock
							serverWins++;					// server wins
						else
							clientWins++;					// if client is scissors client wins
					}
					else if(serverShapes[j] == 3)			// if server is scissor
					{
						if(clientShapes[j] == 1)			// if client is rock
							clientWins++;					// client wins
						else
							serverWins++;					// if client is paper server wins
					}
				}
				
				for(int j=0; j<i; j++)						// printing result strings
				{
					if(serverShapes[j] == 1)
						resultMessage += ("ROCK\r\n");
					else if(serverShapes[j] == 2)
						resultMessage += ("PAPER\r\n");
					else if(serverShapes[j] == 3)
						resultMessage += ("SCISSORS\r\n");
				}
				resultMessage += "CLIENT="+clientWins+"\r\n";
				resultMessage += "TIE="+tie+"\r\n";
				resultMessage += "SERVER="+serverWins+"\r\n\r\n";
				
				//send result to the client
				dos.write(resultMessage.getBytes());
			}
		}
		
	}
	

	private static void print(String s)
	{
		System.out.print(s);
	}
	
	private static void println(String s)
	{
		System.out.println(s);
	}
}
