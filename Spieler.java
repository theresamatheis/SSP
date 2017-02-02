/**
 * 
 */
package SSP_server;

import SSP_interface.RMIClientIF;

/**
 * @author konrad
 * Aber dieser Ersatzkommentar ist viel besser!
 */
public class Spieler {

	private String username;
	private RMIClientIF rmiClientIF;
	/** Referenz auf das Spiel, an dem der Spiel vielleicht
	 * gerade teilnimmmt.
	 */
	private Spiel spiel;
	
	/**
	 * Konstruktor
	 */
	public Spieler(String username, RMIClientIF rmiClientIF) {
		this.username = username;
		this.rmiClientIF = rmiClientIF;
	}
	
	
	/**
	 * 
	 */
	public String getUsername() {
		return this.username;
	}
	
	
	/**
	 * 
	 */
	public RMIClientIF getRMIClientIF() {
		return this.rmiClientIF;
	}
	
	/**
	 * Das Spiel setzen, an dem der Spieler ggf. teilnimmt.
	 */
	public void setSpiel(Spiel spiel) {
		this.spiel = spiel;
	}
	
	/**
	 * Das Spiel erfragen, an dem der Spieler ggf. teilnimmt.
	 */
	public Spiel getSpiel() {
		return this.spiel;
	}
	
} // class Spieler

