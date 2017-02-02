/**
 * 
 */
package SSP_client;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.List;
import java.rmi.RemoteException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import SSP_interface.ChatException;

/**
 * @author Theri
 *
 */
public class GamePanel extends JPanel {

	String ueberschriftText = "Schere, Stein, Papier";
	String[] bilderDateiNamen = {
			"./img/schere.png",
			"./img/stein.png",
			"./img/papier.png",
	};
	int nSpieler = 2;
	int nOptionen = bilderDateiNamen.length;
	
	ClientController clientController;
	JLabel ueberschriftLbl;
	JButton knoepfe[][] = new JButton[nSpieler][nOptionen];
	JTextArea infoField;
	boolean eingefaerbt = false;
	
	
	/**
	 * Konstruktor erstellt das GUI.
	 */
	public GamePanel(ClientController clientController) {
		this.clientController = clientController;
		
		Dimension bildDimension = new Dimension(100, 100);
		GridBagLayout gridBagLayout = new GridBagLayout();
		this.setLayout(gridBagLayout);
		Insets insets = new Insets(5, 5, 5, 5);
		GridBagConstraints c[] = new GridBagConstraints[5];
		int c_index = 0;
		for(c_index = 0; c_index < c.length; c_index++) {
			c[c_index] = new GridBagConstraints();
			c[c_index].insets = insets;
			c[c_index].gridy = c_index;
			c[c_index].gridx = 0;
			switch(c_index) {
				case 0:
					c[c_index].anchor = GridBagConstraints.LINE_START;
					break;
				case 1:
					break;
				case 2:
					break;
				case 3:
					break;
				case 4:
					c[c_index].fill = GridBagConstraints.BOTH;
					c[c_index].gridwidth = GridBagConstraints.REMAINDER;
					break;
			}
		}
		
		// Ueberschrift als Label erstellen
		c_index = 0;
		this.ueberschriftLbl = new JLabel(ueberschriftText);
		this.add(ueberschriftLbl, c[c_index]);

		// Alle Optionen erzeugen: Schere, Stein, Papier,
		//  dabei zuerst als Knopf fuer den lokalen Spieler,
		//  dann als Knopf fuer den entfernten Spieler
		for(int bildIndex = 0; bildIndex < nOptionen; bildIndex++) {
			c_index = bildIndex + 1;
			for(int spielerIndex = 0; spielerIndex < nSpieler; spielerIndex++) {
				c[c_index].gridx = spielerIndex;
				knoepfe[spielerIndex%nSpieler][bildIndex] = 
						erzeugeKnopf(bilderDateiNamen[bildIndex], bildDimension, true);
				this.add(knoepfe[spielerIndex%nSpieler][bildIndex], c[c_index]);
				System.out.println("spielerIndex = " + spielerIndex);
				System.out.println("bildIndex = " + bildIndex);
				System.out.println("c_index = " + c_index);
			}
		}
		c_index++; 

		// Info-Bereich 
		int rows = 1;
		int columns = 30;
		this.infoField = new JTextArea(rows, columns);
		this.add(infoField, c[c_index]);
		this.infoField.setText("Informationen zum Spielverlauf");
	}
	
	
	/**
	 * Diese Methode erstellt einen der Knoepfe entweder
	 * fuer den eigenen Spielzug oder fuer denjenigen
	 * des Gegeners.
	 */
	private JButton erzeugeKnopf(String bildname, Dimension dimension,
			boolean mitActionListenerFlag) {
		ImageIcon imageIcon = new ImageIcon(bildname);
		if (imageIcon.getImageLoadStatus() != MediaTracker.COMPLETE) {
			System.err.println(this.getClass().getName() + ": Bild " + 
					bildname + " konnte nicht geladen werden.");
			System.exit(1);
		}
		imageIcon.setImage(imageIcon.getImage().getScaledInstance(
				dimension.width, dimension.height, Image.SCALE_DEFAULT));
		JButton button = new JButton(imageIcon);
		if(mitActionListenerFlag)
			button.addActionListener(new KnopfDruck());
		return button;
	}
	
	
	/**
	 * Diese innere Klasse reagiert auf das Betaetigen eines
	 * der Knoepfe.
	 */
	class KnopfDruck implements ActionListener  {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			//Auswahl "Schere" an Server senden
			JButton btn = (JButton)e.getSource();
			einfaerben(btn);
			System.out.println("Knopf aktiviert: " + btn.getName());
		}

	}


	/**
	 * 
	 */
	Action eingabeFertig = new AbstractAction() {
		
	    public void actionPerformed(ActionEvent e) {
	    	chatNachrichtVerarbeiten();
	    }
	};

	
	/**
	 * 
	 */
	void chatNachrichtVerarbeiten() {
		RMIClientImpl rmiClientImpl = clientController.connectionPanel.rmiClientImpl;
		String meldung;
		try {
			clientController.server.sendeNachrichtCS(rmiClientImpl, infoField.getText());
		} catch (RemoteException e) {
			meldung = "RemoteException beim Anmelden am Server!\n";
			clientController.connectionPanel.meldungsTextAusgeben(meldung);
			e.printStackTrace();
		} catch (ChatException e) {
			meldung = "ChatException beim Anmelden am Server!\n";
			clientController.connectionPanel.meldungsTextAusgeben(meldung);
			e.printStackTrace();
		}
		// setzeAusgabe("\n" + InfoField.getText());
    	// Zum Testen:
		// InfoField.setText("");
	}

	public void einfaerben(JButton btn){
		if(eingefaerbt == false){
			btn.setBackground(Color.PINK);
			eingefaerbt = true;
		}
	}

}
