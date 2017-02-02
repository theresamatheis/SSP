package SSP_client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

import SSP_interface.ChatException;
import SSP_interface.RMIClientIF;
import SSP_interface.RMIServerIF;

/**
 * 
 * @author Theri
 *
 */
public class RMIClientImpl extends UnicastRemoteObject implements RMIClientIF {

	private String username;
	private ClientController clientController;

	public RMIClientImpl(ClientController clientController, String username) throws RemoteException {
		super(); // Kann RemoteException ausloesen
		this.clientController = clientController;
		this.username = username;
	}

	
	/**
	 * Implementierung der Methode getName()
	 * aus der Schnittstelle RMIClientInterface
	 * @return 
	 */
	public String getNameSC() {
		return username;
	}

	/**
	 * Implementierung der Methode sendeNachricht() aus der Schnitt-
	 * stelle RMIClientInterface. Der Server ruft sendeNachricht()
	 * auf, um dem Client eine Chat-Nachricht mitzuteilen, die ein
	 * anderer Chat-Teilnehmer eingegeben hat.
	 * @param msg
	 * @throws RemoteException
	 */
	public void sendeNachrichtSC(String msg) throws RemoteException {
		this.clientController.chatPanel.setzeAusgabe(msg);
	}

}
