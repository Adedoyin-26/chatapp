import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class UserHandler implements Runnable {

    public static ArrayList<UserHandler> userHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String userUserName;

    public UserHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.userUserName = bufferedReader.readLine();
            userHandlers.add(this);
            broadCastMessage("SERVER: " + userUserName + " has entered the chat");
        } catch (Exception e) {
            closeEverything(socket, bufferedWriter, bufferedReader);
        }
    }

    @Override
    public void run() {
     String messageFromUser;

     while(socket.isConnected()) {
         try {
             messageFromUser = bufferedReader.readLine();
             broadCastMessage(messageFromUser);
         } catch (Exception e) {
             closeEverything(socket, bufferedWriter, bufferedReader);
             break;
         }
     }
    }
    public void broadCastMessage(String messageToSend) {
        for (UserHandler userHandler : userHandlers) {
            try {
                if (!userHandler.userUserName.equals(userUserName)) {
                    userHandler.bufferedWriter.write(messageToSend);
                    userHandler.bufferedWriter.newLine();
                    userHandler.bufferedWriter.flush();
                }
            } catch (Exception e) {
                closeEverything(socket, bufferedWriter, bufferedReader);
            }

        }
    }
    public  void removeUserHandler() {
        userHandlers.remove(this);
        broadCastMessage("SERVER: " + userUserName + " has left the chat");

    }
    public void closeEverything(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
        removeUserHandler();
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
