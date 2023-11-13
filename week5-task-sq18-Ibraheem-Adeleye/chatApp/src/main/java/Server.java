import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.net.ServerSocket;
import java.net.Socket;

@NoArgsConstructor
@AllArgsConstructor


public class Server {
    private ServerSocket serverSocket;
    public void startServer() {
        try {
            while(!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("New User:"  + " has connected!");
                UserHandler userHandler = new UserHandler(socket);

                Thread thread = new Thread(userHandler);
                thread.start();

            }
        } catch (Exception e) {
            System.out.println("Error in server: " + e.getMessage());
        }
    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(1410);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}


