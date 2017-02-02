/**
 * 
 */
package SSP_client;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import SSP_interface.ChatException;
import SSP_interface.RMIClientIF;
import SSP_interface.RMIServerIF;

/**
 * @author Theri
 *
 */
public class ConnectionPanel extends JPanel implements Serializable {

	/**
	 * Wenn bei clientController das Attribut transient hinzugesetzt wird, funktioniert das
	 * Serialisieren der inneren Klasse HostUsernameKlasse; es ist naemlich so,
	 * dass beim Serialisieren einer inneren Klasse auch die aeussere Klasse 
	 * serialisiert wird. Da bei uns zuerst clientController noch nicht
	 * als Serializable gekennzeichnet war, schlug daher das Serialisieren
	 * stets fehl.
	 * Die andere Loesung besteht darin, das Konstrukt serialPersistentFields
	 * zu verwenden, das hier einfach leer bleibt; das ist hier die wohl
	 * beste Loesung, da man ja eigentlich nur die innere Klasse serialisieren 
	 * moechte und die aeussere nur ein Abfallprodukt von Java ist.
	 * Eine weitere Loesung besteht darin, die innere Klasse zu einer eigenen
	 * Klasse umzuformen, wodurch das Problem mit der aeusseren Klasse
	 * grundsaetzlich entfaellt. (Alle Varianten haben wir getestet!)
	 */
	 private static final ObjectStreamField[] serialPersistentFields = new
	 ObjectStreamField[] {
			 // new ObjectStreamField( "hostUserVector", String.class ), // Testdaten ...
	 };
	
	/** Vorgaben fuer die Host-Namen-Auswahl-ComboBox und
	 * die zugehoerigen User-Namen. Der 1. Eintrag erscheint als
	 * der vorausgewaehlte.
	 */
	String hostVorgaben[][] = {
			{	"localhost",		"theri"		},
			{	"127.0.0.1",		"meike"		}
	};
	/** Name der Datei, in der die Host-Namen und die
	 *  User-Namen gespeichert werden. Sie wird bei jeder
	 *  Neu-Eingabe geschrieben und beim Starten des Programmes
	 *  eingelesen; falls das misslingt, wird stattdessen das
	 *  Feld "hostVorgaben" verwendet. */
	String hostnamenDatei = "hostnamen.ser";
	/** Dieser Vector enthaelt die Paare von einem Host und 
	 * einem User-Namen. Dieser Vector wird nach jeder Aenderung 
	 * serialisiert und in einer Datei abgelegt; bei jedem
	 * Programmstart wird der Vector wieder einzulesen versucht. */
	Vector<HostUsernameKlasse> hostUserVector = new Vector<HostUsernameKlasse>();

	Dimension minimumSize;

	ClientController clientController;
	RMIClientImpl rmiClientImpl;
	
	// Die Widgets des JPanels
	JLabel ueberschriftLbl;
	JComboBox<String> hostComboBox;
	JTextField userNameTextField;
	JScrollPane ausgabeTextScrollPane;
	JTextArea ausgabenTextArea;
	JButton clearBtn;
	JButton verbindenBtn;
	JButton trennenBtn;

	/**
	 * Konstruktor erstellt das GUI.
	 */
	@SuppressWarnings("unchecked")
	public ConnectionPanel(ClientController clientController) {
		this.clientController = clientController;

		GridBagLayout gridBagLayout = new GridBagLayout();
		this.setLayout(gridBagLayout);
		Insets insets = new Insets(5, 5, 5, 5);
		GridBagConstraints c[] = new GridBagConstraints[7];
		int c_index = 0;
		for (c_index = 0; c_index < c.length; c_index++) {
			c[c_index] = new GridBagConstraints();
			c[c_index].anchor = GridBagConstraints.FIRST_LINE_START;
			c[c_index].fill = GridBagConstraints.HORIZONTAL;
			c[c_index].gridx = 0;
			c[c_index].gridy = c_index;
			c[c_index].insets = insets;
			c[c_index].weightx = 0.0;
			switch (c_index) {
			case 3: // Ausgabebereich fuer Meldungen darf beliebig wachsen
				c[c_index].fill = GridBagConstraints.BOTH;
				c[c_index].weighty = 1.0;
				break;
			}
		}

		c_index = 0;
		this.ueberschriftLbl = new JLabel("Verbinden mit Server ...");
		this.add(ueberschriftLbl, c[c_index]);

		c_index++;
		String selectedItem = hostVorgaben[0][0];
		Vector<String> hostVorgabenVector = new Vector<String>();
		ObjectInputStream ois = null;
		FileInputStream fis = null;
		try {
			// Versuche, die von einem frueheren Lauf des Programmes
			//  vielleicht vorhandene Datei mit den Hostnamen 
			//  einzulesen.
			fis = new FileInputStream(hostnamenDatei);
			ois = new ObjectInputStream(fis);
			hostUserVector = (Vector<HostUsernameKlasse>)ois.readObject();
			hostVorgabenVector = new HostUsernameKlasse().getVorgabenVector(hostUserVector);
			selectedItem = (String)ois.readObject();
			System.out.println("selectedItem = " + selectedItem);
		} catch (IOException | ClassNotFoundException e) {
			// Wenn die serialisierte Datei mit dem Hostnamen-Vector
			//   nicht gefunden wird oder nicht gelesen werden kann,
			//   werden die vorkonfigurierten Standardnamen verwendet
			for(int i = 0; i < hostVorgaben.length; i++) {
				hostVorgabenVector.addElement(hostVorgaben[i][0]);
				hostUserVector.addElement(
						new HostUsernameKlasse(hostVorgaben[i][0], hostVorgaben[i][1]));
			}
		}
		finally {
			try {
				if (ois != null)
					ois.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		this.hostComboBox = new JComboBox<>(hostVorgabenVector);
		this.add(hostComboBox, c[c_index]);
		this.hostComboBox.setEditable(true);
		this.hostComboBox.setSelectedItem(selectedItem);
		this.hostComboBox.addActionListener(new KonfigSchreiber());
		
		c_index++;
		this.userNameTextField = new JTextField();
		this.userNameTextField.setText(new HostUsernameKlasse().getUsernameAusHostname(selectedItem));
		this.userNameTextField.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "eingabeFertig");
		this.userNameTextField.getActionMap().put("eingabeFertig", eingabeFertig);
		this.userNameTextField.setToolTipText("Eingabe unbedingt mit <Return>-Taste abschließen!");
		this.add(userNameTextField, c[c_index]);
		
		c_index++;
		int rows = 7;
		int columns = 1;
		this.ausgabenTextArea = new JTextArea(rows, columns);
		this.ausgabenTextArea.setEditable(false);
		this.ausgabeTextScrollPane = new JScrollPane(this.ausgabenTextArea);
		this.add(ausgabeTextScrollPane, c[c_index]);

		c_index++;
		this.clearBtn = new JButton("Meldungen löschen");
		this.clearBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ausgabenTextArea.setText("");
			}
		});
		this.add(clearBtn, c[c_index]);

		c_index++;
		this.verbindenBtn = new JButton("Verbinden");
		this.verbindenBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String hostname = (String)hostComboBox.getSelectedItem();
				String username = new HostUsernameKlasse().getUsernameAusHostname(hostname);
				try {
					rmiClientImpl = new RMIClientImpl(clientController, username);
				} catch (RemoteException e1) {
					String meldung = "Fehler beim Instanzieren von UnicastRemoteObject??!!!";
					meldungsTextAusgeben(meldung);
					e1.printStackTrace();
				}
				Thread thread = new Thread( new Runnable() {
				    @Override
				    public void run() {
						clientController.server = null;
						String BIND_NAME = "theresa_und_meike_server";
						String bindURL = "rmi://" + hostname + "/" + BIND_NAME;
						String meldung = "";
						// Verbindung aufbauen
						try {
							clientController.server = (RMIServerIF) Naming.lookup(bindURL);
						} catch (NotBoundException e) {
							// Wenn der Server nicht registriert ist ...
							meldung = "Läuft der Server?\n";							
							System.out.println(meldung +  e.getMessage());
						} catch (MalformedURLException e) {
					 		// Wenn die URL falsch angegeben wurde ...
							meldung = "URL "+ bindURL + " ist ungültig.\n";
							System.out.println(meldung + e.getMessage());
						} catch (RemoteException e) {
							// Wenn während der Kommunikation ein Fehler auftritt
							meldung = "Allgemeiner Kommunikationsfehler\n";
							System.out.println(meldung + e.getMessage());
						}
						if(clientController.server == null) {
							meldungsTextAusgeben(meldung);
							return;
						}
						// Anmeldung am Chat-Server
						try {
							clientController.server.anmeldenCS(rmiClientImpl);
						} catch (java.rmi.ConnectException e) {
							meldung = "java.rmi.ConnectException - Laeuft Ihr Server ueberhaupt?";
							meldungsTextAusgeben(meldung);
							clientController.server = null;
							e.printStackTrace();
						} catch (RemoteException e) {
							meldung = "RemoteException beim Anmelden am Server!\n";
							meldungsTextAusgeben(meldung);
							clientController.server = null;
							e.printStackTrace();
							return;
						} catch (ChatException e) {
							meldung = "Username schon vergeben! --> ChatException beim Anmelden am Server!\n";
							meldungsTextAusgeben(meldung);
							clientController.server = null;
							e.printStackTrace();
							return;
						}
						Runnable eingabeAktivierenRunnable = new Runnable() {
						     public void run() {
									hostComboBox.setEnabled(false);
									userNameTextField.setEnabled(false);
									clientController.chatPanel.eingabeTextField.setEnabled(true);
									clientController.chatPanel.textSendenBtn.setEnabled(true);
						     }
						};
						SwingUtilities.invokeLater(eingabeAktivierenRunnable);		

				    } // run
				    
				    });
				thread.start();
			}
		});
		this.add(verbindenBtn, c[c_index]);

		c_index++;
		this.trennenBtn = new JButton("Verbindung trennen");
		this.trennenBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				hostComboBox.setEnabled(true);
				userNameTextField.setEnabled(true);
				clientController.chatPanel.eingabeTextField.setEnabled(false);
				clientController.chatPanel.textSendenBtn.setEnabled(false);
				// Dimension dimension = new Dimension(minimumSize.width*2, minimumSize.height);
				// System.out.println(dimension);
				//setPreferredSize(dimension);
				// setMinimumSize(dimension);
				if(clientController.server != null) {
					try {
						clientController.server.abmeldenCS(rmiClientImpl);
					} catch (RemoteException | ChatException e1) {
						String meldung = "RemoteException beim Abmelden vom Server!";
						meldungsTextAusgeben(meldung);
						e1.printStackTrace();
					}
				}
				
			}
		});
		this.add(trennenBtn, c[c_index]);
		
		minimumSize = this.getMinimumSize();
		System.out.println("this.getMinimumSize(): " + minimumSize);
		
		// Erstellen eines Threads, der staendig alle Spieler
		//  durchprueft, ob sie noch online sind.
		new Thread(new Runnable() {
			public void run() {
				
			}
		}).start();
	} // Konstruktor

	
	/**
	 * Meldung synchron im GUI-Thread in das Textfenster ausgeben.
	 */
	void meldungsTextAusgeben(String meldung) {
		Runnable ausgebenRunnable = new Runnable() {
		     public void run() {
		    	 ausgabenTextArea.append(meldung);
		     }
		 };
		 SwingUtilities.invokeLater(ausgebenRunnable);		
	}
	
	/**
	 * 
	 */
	Action eingabeFertig = new AbstractAction() {

		public void actionPerformed(ActionEvent e) {			
			ausgabenTextArea.append(hostComboBox.getSelectedItem() + " --> "+ userNameTextField.getText() + "\n"); //##
			new KonfigSchreiber().actionPerformed(e);
		}
	};

	
	/**
	 * 
	 */
	class HostUsernameKlasse implements Serializable {
		String hostname;
		String username;
		
		HostUsernameKlasse() {			
		}
		
		HostUsernameKlasse(String hostname, String username) {
			HostUsernameKlasse.this.hostname = hostname;
			HostUsernameKlasse.this.username = username;
		}
		
		private void writeObject(ObjectOutputStream oos) throws IOException {
			System.out.println(this.hostname + " " + this.username);
			oos.defaultWriteObject();
		}
		 
		/**
		 * 
		 */
		Vector<String> getVorgabenVector(Vector<HostUsernameKlasse> hostUserVector) {
			Vector<String> hostVorgabenVector = new Vector<String>();
			for(int i = 0; i < hostUserVector.size(); i++)
				hostVorgabenVector.addElement(hostUserVector.get(i).hostname);
			return hostVorgabenVector;
		}
		
		/**
		 * Diese Methode liefert den Usernamen fuer einen gegebenen
		 * Hostnamen aus dem Vector zurueck.
		 */
		String getUsernameAusHostname(String hostname) {
			for(int i = 0; i < hostUserVector.size(); i++) {
				if (hostname.equals(hostUserVector.elementAt(i).hostname)) {
					return hostUserVector.elementAt(i).username;
				}
			}
			return "";
		}
		 
	}

	/**
	 * 
	 */
	class KonfigSchreiber implements ActionListener, Serializable {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// Entweder der ausgewaehlte oder der neu eingetippte Eintrag
			String hostname = (String)hostComboBox.getSelectedItem();
			Vector<String> hostVorgabenVector =
					new HostUsernameKlasse().getVorgabenVector(hostUserVector);
			String username;
			if(hostVorgabenVector.contains(hostname)) {
				int itemIndex = hostVorgabenVector.indexOf(hostname);
				if(e.getSource() == hostComboBox) {
					// In der ComboBox wurde das Ereignis ausgelöst
					// Wenn es ein schon vorhandener Host-Name ist,
					//  wird der zugehörige Benutzername herausgesucht und
					//  in das entsprechende Text-Feld eingefuegt
					username = hostUserVector.elementAt(itemIndex).username;
					userNameTextField.setText(username);
				}
				else {
					// Im Text-Eingabefeld fuer den User-Namen wurde
					//  ein Name eingegeben
					username = userNameTextField.getText();
					hostUserVector.setElementAt(
							new HostUsernameKlasse(hostname, username), itemIndex);
				}
			}
			else {
				// Der Anwender hat einen neuen Host-Namen eingetippt!
				hostComboBox.addItem(hostname);
				// Der zuletzt vorhandene Username wird weiterverwendet
				hostUserVector.addElement(
						new HostUsernameKlasse(hostname, userNameTextField.getText()));
			}
			FileOutputStream fos = null;
			ObjectOutputStream oos = null;
			try {
				fos = new FileOutputStream(hostnamenDatei);					
				oos = new ObjectOutputStream(fos);
				System.out.println("Vector NOCH NICHT geschrieben");
				oos.writeObject(hostUserVector);
				System.out.println("Vector geschrieben");
				oos.writeObject(hostname);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			finally{
				try {
					if(oos != null)
						oos.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			ausgabenTextArea.append("Schreiben OK!\n");
			ausgabenTextArea.append("   item = " + hostname + "\n");
			ausgabenTextArea.append("   userNameTextField.getText() = " + 
					userNameTextField.getText() + "\n");
		}
		
	}
	
	
	/**
	 * 
	 */
	void chatNachrichtVerarbeiten() {
		//setzeAusgabe("\n" + hostComboBox.getText());
		//hostComboBox.setText("");
	}

	/**
	 * Setze den Fokus auf ein Widget.
	 */
	public void setFokus() {
		this.hostComboBox.requestFocusInWindow();
	}

	/**
	 * 
	 */
	public void setzeAusgabe(String text) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ausgabenTextArea.append(text);
			}
		});
	}

}

