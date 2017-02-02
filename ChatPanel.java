/**
 * 
 */
package SSP_client;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.rmi.RemoteException;

import javax.swing.AbstractAction;
import javax.swing.Action;
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
public class ChatPanel extends JPanel {

	ClientController clientController;
	JLabel ueberschriftLbl;
	JScrollPane ausgabeTextScrollPane;
	JTextArea ausgabenTextArea;
	JTextField eingabeTextField;
	JButton textSendenBtn;
	JButton ausgabenClearBtn;
	
	/**
	 * Konstruktor erstellt das GUI.
	 */
	public ChatPanel(ClientController clientController) {
		this.clientController = clientController;
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		this.setLayout(gridBagLayout);
		Insets insets = new Insets(5, 5, 5, 5);
		GridBagConstraints c[] = new GridBagConstraints[3];
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
				c[c_index].anchor = GridBagConstraints.FIRST_LINE_START;
				c[c_index].fill = GridBagConstraints.BOTH;
				c[c_index].gridwidth = GridBagConstraints.REMAINDER;
				c[c_index].weightx = 1.0;
				c[c_index].weighty = 1.0;
				break;
			case 2:
				c[c_index].anchor = GridBagConstraints.LINE_START;
				c[c_index].fill = GridBagConstraints.HORIZONTAL;
				c[c_index].weightx = 1.0;
				break;
			}
		}
		
		c_index = 0;
		this.ueberschriftLbl = new JLabel("Chat");
		this.add(ueberschriftLbl, c[c_index]);
		
		c[c_index].gridx = 1;
		this.ausgabenClearBtn = new JButton("Löschen");
		this.ausgabenClearBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ausgabenTextArea.setText("");				
			}
		});
		this.add(ausgabenClearBtn, c[c_index]);
		
		
		c_index++;
		int rows = 7;
		int columns = 40;
		this.ausgabenTextArea = new JTextArea(rows, columns);
		this.ausgabenTextArea.setEditable(false);
		this.ausgabeTextScrollPane = new JScrollPane(this.ausgabenTextArea);
		this.add(ausgabeTextScrollPane, c[c_index]);
		
		c_index++;
		this.eingabeTextField = new JTextField(columns);
		
		this.eingabeTextField.getInputMap().put(KeyStroke.getKeyStroke("ENTER"),
		                            "eingabeFertig");
		this.eingabeTextField.getActionMap().put("eingabeFertig",
				eingabeFertig);
		this.eingabeTextField.setEnabled(false);
		this.add(eingabeTextField, c[c_index]);
		
		c[c_index].gridx = 1;
		c[c_index].weightx = 0.0;
		this.textSendenBtn = new JButton("Senden");
		this.textSendenBtn.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				chatNachrichtVerarbeiten();				
			}
		});
		this.textSendenBtn.setEnabled(false);
		this.add(textSendenBtn, c[c_index]); 
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
			clientController.server.sendeNachrichtCS(rmiClientImpl, eingabeTextField.getText());
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
		//  InfoField.setText("");
	}
	
	/** 
	 * Setze den Fokus auf ein Widget.
	 */
	public void setFokus() {
		this.eingabeTextField.requestFocusInWindow();
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
