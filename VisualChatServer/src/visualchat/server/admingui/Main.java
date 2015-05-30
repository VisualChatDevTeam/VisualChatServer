package visualchat.server.admingui;
import javax.swing.JFrame;

// Main : Starting Class //

public class Main {
	public static void main(String args[]) {
		ServerFrame serverFrame = new ServerFrame();
		serverFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        // Method for Closing the Windows // 
		serverFrame.setSize(800, 800);                                     // Set Size of Server Windows//
		serverFrame.setVisible(true);                                      // Make Server Windows become visible //   
	}
}
