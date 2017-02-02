/**
 * 
 */
package SSP_client;

import java.awt.BorderLayout;

import SSP_interface.RMIServerIF;

/**
 * @author Theri
 *
 */
public class ClientController {

	SSPClient sSPClient;
	GamePanel gamePanel;
	ChatPanel chatPanel;
	ConnectionPanel connectionPanel;
	RMIClientImpl rmiClientImpl;
	RMIServerIF server;
	
	
	/**
	 * 
	 */
	public ClientController(SSPClient sSPClient) {
		this.sSPClient = sSPClient;
		this.gamePanel = new GamePanel(this);
		this.sSPClient.add(gamePanel, BorderLayout.WEST);
		this.chatPanel = new ChatPanel(this);
		this.sSPClient.add(chatPanel, BorderLayout.SOUTH);
		this.connectionPanel = new ConnectionPanel(this);
		this.sSPClient.add(connectionPanel, BorderLayout.EAST);
	}

	/**
	 * Setzt den Eingabe-Fokus auf ein bestimmtes Widget.
	 */
	public void setFokus() {
		chatPanel.setFokus();
	}
}
