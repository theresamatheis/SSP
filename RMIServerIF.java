/**
 * 
 */
package SSP_interface;

import java.rmi.*;

/**
 * @author Theri
 * @since  28.06.2016
 */
public interface RMIServerIF extends Remote {
	static final int SCHERE_GEWAEHLT = 0;
	final int STEIN_GEWAEHLT = 1;
	final int PAPIER_GEWAEHLT = 2;

	/**
	 * Ein Client kann sich hiermit am Chat-Server
	 * anmelden. Ist sein Nickname bereits vergeben,
	 * so wird eine ChatException geworfen. 
	 * @param client Referenz auf das Remote-Interface des Client
	 **/
	public void anmeldenCS(RMIClientIF client)
			throws RemoteException, ChatException;
	
	/**
	 * Ein angemeldeter Client ruft diese Methode
	 * @param client
	 * @param msg
	 * @throws RemoteException
	 * @throws ChatException
	 * auf, um eine Nachricht an alle Chat-Teilnehmer
	 * zu senden. Der Server verteilt die Nachrichten
	 * dann nach dem Publisher-Subscriber-Prinzip
	 */
	public void sendeNachrichtCS(RMIClientIF client, String msg)
			throws RemoteException, ChatException;
	
	/**
	 * Angemeldete Clients melden sich mit Aufruf
	 * dieser Methode vom Chat-Server ab.
	 * @param client 
	 */
	public void abmeldenCS(RMIClientIF client)
			throws RemoteException, ChatException;


	/**
	 * .
	 * @param client 
	 */
	public void zugMeldenCS(RMIClientIF client, int zug)
			throws RemoteException;


} // RMIServerIF
