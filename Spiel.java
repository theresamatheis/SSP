/**
 * 
 */
package SSP_server;

import SSP_interface.RMIClientIF;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author konrad
 *
 */
public class Spiel {

	private Spieler spielerPaar[];
	
	/** Ab Java 1.8 verfuegbar. */
	private static AtomicInteger anzahlSpiele = new AtomicInteger();
	
	/**
	 * Konstruktor.
	 */
	public Spiel(Spieler spieler1, Spieler spieler2) {
		spielerPaar = new Spieler[2];
		this.spielerPaar[0] = spieler1;
		this.spielerPaar[1] = spieler2;
		// Geschieht atomar und damit synchronisiert!
		anzahlSpiele.incrementAndGet();
	} // Konstruktor
	
	/**
	 * Feld der Spieler erfragen.
	 */
	public Spieler[] getSpielerPaar() {
		return this.spielerPaar;
	}
	
} // class Spiel

