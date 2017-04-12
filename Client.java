import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Davorin on 4/12/2017.
 */
public class Client extends JFrame {

    private JTextArea chatWindow;
    private JTextField userText;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private String message = "";
    private String serverIP;
    private Socket connection;

    //constructor
    public Client(String host){
        super("Client");
        serverIP = host;
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
        add(new JScrollPane(chatWindow), BorderLayout.CENTER);
        setSize(300,150);
        setVisible(true);
    }

    //connect to server
    public void startRunning(){
        try{
            connectToServer();
            setupStreams();
            whileChatting();
        }catch(EOFException eofException){
            showMessage("\n Client closed the connection");
        }catch(IOException ioException){
            ioException.printStackTrace();
        }finally{
            closeCrap();
        }
    }

    //connect to server
    private void connectToServer() throws IOException{
        showMessage("Attempting to connect... \n");
        connection = new Socket(InetAddress.getByName(serverIP), 6789);
        showMessage("Now connected to: " + connection.getInetAddress().getHostName() );
    }

    //set up streams to send and receive messages
    private void setupStreams() throws IOException{
        outputStream = new ObjectOutputStream(connection.getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(connection.getInputStream());
        showMessage("\n Streams are now good to go! \n");
    }

    //while chatting with server
    private void whileChatting() throws IOException{
        ableToType(true);
        do{
            try{
                message = (String) inputStream.readObject();
                showMessage("\n" + message);
            }catch(ClassNotFoundException classNotfoundException){
                showMessage("\n Cannot read that");
            }
        }while(!message.equals("SERVER - END"));
    }

    //close the streams and sockets
    private void closeCrap(){
        showMessage("\n Closing down...");
        ableToType(false);
        try{
            outputStream.close();
            inputStream.close();
            connection.close();
        }catch(IOException ioException){
            ioException.printStackTrace();
        }
    }

    //send messages to server
    private void sendMessage(String message){
        try{
            outputStream.writeObject("CLIENT - " + message);
            outputStream.flush();
            showMessage("\nCLIENT - " + message);
        }catch(IOException ioException){
            chatWindow.append("\n Error sending message!");
        }
    }

    //change/update chatWindow
    private void showMessage(final String m){
        SwingUtilities.invokeLater(
                new Runnable(){
                    public void run(){
                        chatWindow.append(m);
                    }
                }
        );
    }

    //gives user permission to type crap into the text box
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
