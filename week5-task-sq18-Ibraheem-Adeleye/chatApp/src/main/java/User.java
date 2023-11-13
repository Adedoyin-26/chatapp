import lombok.Getter;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
@Getter
public class User {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String userName;

   public User(Socket socket, String userName) {
       try {
           this.socket = socket;
           this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
           this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
           this.userName = userName;
       } catch (Exception e) {
           closeEverything(socket, bufferedWriter, bufferedReader);
       }
   }
   public void sendMessage() {
       try {
           bufferedWriter.write(userName);
           bufferedWriter.newLine();
           bufferedWriter.flush();

           Scanner scanner = new Scanner(System.in);
           while (socket.isConnected()) {
               String messageToSend = scanner.nextLine();
               bufferedWriter.write(userName + ": " + messageToSend);
               bufferedWriter.newLine();
               bufferedWriter.flush();
           }
       } catch (IOException e) {
           closeEverything(socket, bufferedWriter, bufferedReader);
       }
   }
   public void listenForMessage() {
       new Thread(new Runnable() {
           @Override
           public void run() {
               String messageFromForum;
               while (socket.isConnected()) {
                   try {
                       messageFromForum = bufferedReader.readLine();
                       System.out.println(messageFromForum);
                   } catch (IOException e) {
                       closeEverything(socket,bufferedWriter, bufferedReader);
                   }
               }
           }
       }).start();
   }
   public void closeEverything(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
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

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Input your username for the Forum: ");
        String username = scanner.nextLine();
        Socket socket = new Socket("localhost", 1410);
        User user = new User(socket, username);
        user.listenForMessage();
        user.sendMessage();
    }
}
