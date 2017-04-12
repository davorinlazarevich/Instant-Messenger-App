import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Davorin on 4/12/2017.
 */
public class Server extends JFrame {

    private JTextArea chatWindow;
    private JTextField userText;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private ServerSocket serverSocket;
    private Socket socket;

    //constructor
    public Server(){
        super("Instant Messenger");
        userText = new JTextField();
        userText.setEditable(false);
        userText.addActionListener(
                new ActionListener(){
                    public void actionPerformed(ActionEvent event){
                        sendMessage(event.getActionCommand());
                        userText.setText("");
                    }
                }
        );
        add(userText, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow));
        setSize(300,150);
        setVisible(true);
    }

    //set up and run the server
    public void startRunning(){
        try{
            serverSocket = new ServerSocket(6789, 100);
            while(true){
                try{
                    waitForConnection();
                    setupStreams();
                    whileChatting();
                }catch(EOFException eofException){
                    showMessage("\n Server ended the connection! ");
                }finally{
                    closeCrap();
                }
            }
        }catch(IOException ioException){
            ioException.printStackTrace();
        }
    }

    //wait for connection, then display connection information
    private void waitForConnection() throws IOException{
        showMessage(" Waiting for someone to connect... \n");
        socket = serverSocket.accept();
        showMessage(" Now connected to " + socket.getInetAddress().getHostName());
    }

    //get stream to send and receive data
    private void setupStreams() throws IOException{
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(socket.getInputStream());
        showMessage("\n Streams are now setup! \n");
    }

    //during the chat conversation
    private void whileChatting() throws IOException{
        String message = " You are now connected! ";
        sendMessage(message);
        ableToType(true);
        do{
            try{
                message = (String) inputStream.readObject();
                showMessage("\n" + message);
            }catch(ClassNotFoundException classNotFoundException){
                showMessage("\n Unable to read!");
            }
        }while(!message.equals("CLIENT - END"));
    }

    //close streams and sockets after you are done chatting
    private void closeCrap(){
        showMessage("\n Closing connections... \n");
        ableToType(false);
        try{
            outputStream.close();
            inputStream.close();
            socket.close();
        }catch(IOException ioException){
            ioException.printStackTrace();
        }
    }

    //send a message to client
    private void sendMessage(String message){
        try{
            outputStream.writeObject("SERVER - " + message);
            outputStream.flush();
            showMessage("\nSERVER - " + message);
        }catch(IOException ioException){
            chatWindow.append("\n Cannot send message!");
        }
    }

    //updates chatWindow
    private void showMessage(final String text){
        SwingUtilities.invokeLater(
                new Runnable(){
                    public void run(){
                        chatWindow.append(text);
                    }
                }
        );
    }

    //let the user type stuff into their box
    private void ableToType(final boolean tof){
        SwingUtilities.invokeLater(
                new Runnable(){
                    public void run(){
                        userText.setEditable(tof);
                    }
                }
        );
    }

}
