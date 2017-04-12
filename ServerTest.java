import javax.swing.JFrame;

/**
 * Created by Davorin on 4/12/2017.
 */
public class ServerTest {
    public static void main(String[] args) {
        Server Davorin = new Server();
        Davorin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Davorin.startRunning();
    }
}
