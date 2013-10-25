package milestone1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class EchoClient implements Runnable {
	private BufferedReader br;
	private OutputStream outputStream;
	private InputStream inputStream;
	private Socket socket;
	private String ipAddress = "";
	private String message = "";
	private int port = 0;
	private int command = 0;
	private String ori_command = "";
	private Logger log;

	private final int CONNECT_COMMAND = 1;
	private final int SEND_COMMAND = 2;
	private final int DISCONNECT_COMMAND = 3;
	private final int QUIT_COMMAND = 4;
	private final int ERROR_COMMAND = 5;
	private final int HELP_COMMAND = 6;

	public static void main(String[] args) {
		try {
			System.out
					.println("EchoClient> "
							+ "Welcome to Echo Cliend, please type in command or type help!");
			new Thread(new EchoClient()).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public EchoClient() throws IOException {
		log = LogManager.getLogger(EchoClient.class);
		log.info("Client Start!");
	}

	@Override
	public void run() {
		try {
			while (true) {
				System.out.print("EchoClient> ");
				command = this.readCommand();
				switch (command) {
				case 1:
					this.socket = new Socket("131.159.52.1", 50000);
					// this.socket = new Socket(this.ipAddress, this.port);
					this.outputStream = this.socket.getOutputStream();
					this.inputStream = this.socket.getInputStream();
					BufferedReader output = new BufferedReader(
							new InputStreamReader(inputStream));
					System.out.println("EchoClient> " + output.readLine());
					log.info("Success to connect to the echo server!");
					break;
				case 2:
					if (!socket.isClosed()) {
						PrintWriter pw = new PrintWriter(outputStream, true);
						pw.println(this.message);
						BufferedReader output1 = new BufferedReader(
								new InputStreamReader(inputStream));
						System.out.println("EchoClient> " + output1.readLine());
						System.out.println("EchoClient> " + output1.readLine());
						log.info("Success to send message:" + message);
					} else {
						System.out
								.println("EchoClient> "
										+ "Error! Not connected! Using command \"help\" to get help!");
						log.warn("Send message failed! Have not connected to the echo server!");
					}
					break;
				case 3:
					if (!socket.isClosed()) {
						System.out.println("EchoClient> "
								+ "Connection terminated:"
								+ socket.getInetAddress() + "/"
								+ socket.getPort());
						this.inputStream.close();
						this.outputStream.close();
						this.socket.close();
						log.info("Disconnect with echo server!");
					} else {
						System.out
								.println("EchoClient> "
										+ "Error! Not connected! Using command \"help\" to get help!");
						log.warn("Disconnect failed! Have not connected to the echo server!");
					}
					break;
				case 4:
					System.out.println("EchoClient> " + "Application exit!");
					if (!socket.isClosed()) {
						this.inputStream.close();
						this.outputStream.close();
						this.socket.close();
						log.info("Quit Application!");
					}
					System.exit(-1);
					break;
				case 5:
					System.out
							.println("EchoClient> "
									+ "Error command! Using command \"help\" to get help!");
					log.warn("Error command!" + ori_command);
					break;
				case 6:
					System.out.println("EchoClient> "
							+ "Connect Command: connect 'ip' 'port'");
					System.out.println("            "
							+ "Send Command: send 'message'");
					System.out.println("            "
							+ "Disconnect Command: disconnect");
					System.out.println("            " + "Quit Command: quit");
					log.info("Get help!");
					break;
				default:
					log.warn("What happened?");
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
	}

	private int readCommand() {
		this.br = new BufferedReader(new InputStreamReader(System.in));

		try {
			ori_command = br.readLine();
			String temp[] = ori_command.split(" ");
			if (temp[0].equalsIgnoreCase("connect")) {
				if (temp.length > 2) {
					this.ipAddress = temp[1].toString();
					this.port = Integer.valueOf(temp[2].toString());
					return CONNECT_COMMAND;
				}
			} else if (temp[0].equalsIgnoreCase("send")) {
				this.message = ori_command.substring(5);
				return SEND_COMMAND;
			} else if (temp[0].equalsIgnoreCase("disconnect")) {
				return DISCONNECT_COMMAND;
			} else if (temp[0].equalsIgnoreCase("quit")) {
				return QUIT_COMMAND;
			} else if (temp[0].equalsIgnoreCase("help")) {
				return HELP_COMMAND;
			} else {
				return ERROR_COMMAND;
			}
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getMessage());
			return ERROR_COMMAND;
		}
		return ERROR_COMMAND;
	}
}