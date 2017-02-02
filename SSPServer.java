/**
 * 
 */
package SSP_server;

import java.rmi.*;
import java.rmi.server.*;
import java.net.*;
import java.util.*;

import SSP_interface.ChatException;
import SSP_interface.RMIClientIF;
import SSP_interface.RMIServerIF;

/**
 * Registry starten:
 * set CLASSPATH=.;C:\Users\Theri\workspace\SSP_server\bin;C:\Users\Theri\workspace\SSP_interface\bin
 * "c:\Program Files\Java\jdk1.8.0_92\bin\rmiregistry.exe"
 * 
 * Server starten:
 * cd C:\Users\Theri\workspace\SSP_server\bin
 * java -cp .;C:\Users\Theri\workspace\SSP_interface\bin\ SSP_server.SSPServer
 * 
 * Client starten:
 * cd C:\Users\Theri\workspace\SSP_client\bin
 * java -cp .;C:\Users\Theri\workspace\SSP_interface\bin SSP_client.SSPClient theri
 * 
 * @author Theri
 *
 */
public class SSPServer extends UnicastRemoteObject implements RMIServerIF {

	private static final String HOST = "localhost";
	private static final String SERVICE_NAME = "theresa_und_meike_server"; // "SSPServer";
	// Von alle angemeldeten spielerVector wird die
	// Referenz in diesem Vector<T>-Objekt gespeichert
	private Vector<Spieler> spielerVector = null;
	private Vector<Spiel> spieleVector = null; 

	public SSPServer() throws RemoteException {
		String bindURL = null;
		try {
			bindURL = "rmi://" + HOST + "/" + SERVICE_NAME;
			Naming.rebind(bindURL, this);
			spielerVector = new Vector<Spieler>();
			spieleVector = new Vector<Spiel>();
			System.out.println("RMI-Server gebunden unter Namen: " + SERVICE_NAME);
			System.out.println("RMI-Server ist bereit ...");
		} catch (MalformedURLException e) {
			System.out.println("Ungültige URL: " + bindURL);
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}

	
	/**
	 *  Die Methoden des Servers sind alle synchronisiert, weil diese
	 * von mehreren spielerVector gleichzeitig aufgerufen werden können.
	 * Methode zum Anmelden
	 */
	public synchronized void anmeldenCS(RMIClientIF client) throws RemoteException, ChatException {
		String msg = null;
		String username = client.getNameSC();
		// Prüfen, ob der Nickname schon vergeben ist
		if (angemeldet(username)) {
			msg = username + " schon vergeben.";
			throw new ChatException(msg);
		}
		// Neuer Spieler-Objekt erstellen
		Spieler spieler = new Spieler(username, client);
		// Neuen Client dem Vector hinzufügen
		spielerVector.add(spieler);
		// Willkommensnachricht senden
		msg = "Willkommen auf RMIChat. " + "Zum Abmelden \"Exit\" eingeben.";
		client.sendeNachrichtSC(msg);
		// Alle angemeldeten spielerVector über
		// neuen Chat-Teilnehmer informieren
		msg = "\n" + spieler.getUsername() + " hat sich angemeldet.";
		this.sendeNachrichtAnAlleClients(msg);
		// ### Das muessen die beiden frei waehlen duerfen!!!
		if(spielerVector.size() == 2) {
			System.out.println("### Zwangsverheiratung...");
			Spieler spielerPaar[] = { spielerVector.get(0), spielerVector.get(1) }; 
			Spiel spiel = new Spiel(spielerPaar[0], spielerPaar[1]);
			spielerPaar[0].setSpiel(spiel);
			spielerPaar[1].setSpiel(spiel);
			spieleVector.add(spiel);
			String text = spielerPaar[0].getUsername() + " spielt mit "
					+ spielerPaar[1].getUsername() + ".";
			msg = "\n" + text;
			this.sendeNachrichtAnAlleClients(msg);
		}
			
		printStatus();
	}

	
	/**
	 * Methode zum Senden einer Chat-Nachricht an alle Teilnehmer
	 * @param client
	 * @param nachricht
	 * @throws RemoteException
	 * @throws ChatException
	 */
	public synchronized void sendeNachrichtCS(RMIClientIF client, String nachricht)
			throws RemoteException, ChatException {
		String msg = null;
		// Prüfen, ob der Client angemeldet ist
		if (!angemeldet(client.getNameSC())) {
			msg = "Client " + client.getNameSC() + " nicht angemeldet.";
			throw new ChatException(msg);
		}
		msg = client.getNameSC() + " schreibt: " + nachricht;
		// An alle angemeldeten Chat-Teilnehmer
		// die Nachricht des Senders publizieren
		msg = "\n" + nachricht;
		this.sendeNachrichtAnAlleClients(msg);
	}

	
	/**
	 * Methoden zum Abmelden vom Chat-Server
	 * @param client
	 * @throws RemoteException
	 * @throws ChatException
	 */
	public synchronized void abmeldenCS(RMIClientIF client) throws RemoteException, ChatException {
		String msg = null;
		// Ist der Chat-Teilnehmer überhaupt angemeldet?
		String name = client.getNameSC(); // ### Effizienz??
		if (!angemeldet(name)) {
			msg = "Client " + client.getNameSC() + " nicht angemeldet.";
			throw new ChatException(msg);
		}
		// Referenz auf den Chat-Client entfernen
		Spieler spielerZuLoeschen = null;
		for(Spieler spieler : spielerVector) {
			if(spieler.getRMIClientIF().equals(client)) {
				spielerZuLoeschen = spieler;
				break;
			}
		}
		// Ggf. zugehoeriges Spiel loeschen
		Spiel spiel = spielerZuLoeschen.getSpiel();
		if(spiel != null) {
			// Ueber das Spiel den Partner herausfinden
			 Spieler spielerPaar[] = spiel.getSpielerPaar();
			 // Beim Partner und dem zu loeschenden Spieler
			 //  das Spiel loeschen
			 spielerPaar[0].setSpiel(null);
			 spielerPaar[1].setSpiel(null);
			 // Das Spiel aus der Spieleliste entfernen
			 spieleVector.remove(spiel);
		}
		// Den Spieler loeschen
		spielerVector.remove(spielerZuLoeschen);
		// Alle noch verbleibenden Chat-Teilnehmer informieren
		msg = "\n" + spielerZuLoeschen.getUsername() + " hat sich abgemeldet.";
		this.sendeNachrichtAnAlleClients(msg);
		printStatus();
	}
	
	/**
	 * 
	 */
	public synchronized void zugMeldenCS(RMIClientIF client, int zug)
			throws RemoteException {
		
	}
	
	/**
	 * Sende Nachricht an alle Clients.
	 * #### Das muesste noch parallelisiert werden.
	 */
	private void sendeNachrichtAnAlleClients(String msg) throws RemoteException {
		for (Spieler s : spielerVector) {
			s.getRMIClientIF().sendeNachrichtSC(msg);
		}
	}


	
	/**
	 * Ausgabe, welche spielerVector momentan angemeldet sind
	 * @throws RemoteException
	 */
	private void printStatus() throws RemoteException {
		Calendar cal = GregorianCalendar.getInstance();
		String msg = cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND)
				+ " Uhr: ";
		msg += spielerVector.size() + " User aktuell online: ";
		for (Spieler spieler : spielerVector) {
			msg += spieler.getUsername() + " ";
		}
		System.out.println(msg);
	}

	
	/**
	 * Überprüfung, ob der übergebene Nickname schon vergeben ist
	 * @param name
	 * @return
	 * @throws RemoteException
	 */
	private boolean angemeldet(String name) throws RemoteException {
		for (Spieler spieler : spielerVector) {
			if (name.equalsIgnoreCase(spieler.getUsername())) {
				return true;
			}
		}
		return false;
	}

	
	/**
	 * Hauptprogramm.
	 * @param args 
	 */
	public static void main(String[] args) {
		try {
			System.out.println("SSPServer [main]: Start");
			new SSPServer();
			System.out.println("SSPServer [main]: nach Instanzieren");
			//throw new RemoteException();
		} catch (RemoteException e) {
			System.out.println("RemoteException ....");
			System.out.println(e.getMessage());
			System.exit(1);
		}
		System.out.println("SSPServer [main]: Ende");
	} // main
	
}
