/**
 * 
 */
package SSP_interface;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Theri
 *
 */
public interface RMIClientIF extends Remote {

	/**
	 *  Der Server ruft diese Methode auf, um die eingegangenen
	 *  Chat-Nachrichten an die Clients zu publizieren.
	 * @param msg NAchricht an den Client
	 * @throws RemoteException
	 */
	void sendeNachrichtSC(String msg) throws RemoteException;
	
	/**
	 * Gibt den Namen des Clients zurück
	 * @return Name des Teilnehmers
	 * @throws RemoteException
	 */
	public String getNameSC() throws RemoteException;

} // RMIClientIF
