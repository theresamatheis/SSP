/**
 * 
 */
package SSP_client;

import java.awt.Container;
import java.net.*;
import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * @author Theri
 *
 */
public class SSPClient extends JFrame {
    
	ClientController clientController;
	
	/**
	 * 
	 */
	public SSPClient() {
	     //Create and set up the window.
		this.setTitle("Schere, Stein, Papier Client 1.0");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

 
	    this.clientController = new ClientController(this);
 
        //Display the window.
        this.pack();
        // Controller hier bitten, den Eingabe-Fokus auf ein
        //  bestimmtes Widget zu setzen
        this.clientController.setFokus();
        this.setVisible(true);
	}
	

	/**
	 * @param args Kommandozeilenparameter. Hier muss als 1. Parameter der
	 * 	gewuenschte Anwendername angegeben werden.
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			args = new String[1];
			args[0] = "Tom";
			System.out.println("Aufruf: RMIChat <Nickname>");
			//System.exit(1);
		}
		
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new SSPClient();
            }
        });
        
	} // main
	
}
