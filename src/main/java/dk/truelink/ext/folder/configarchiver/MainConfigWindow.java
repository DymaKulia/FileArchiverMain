package dk.truelink.ext.folder.configarchiver;

import java.awt.Button;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

public class MainConfigWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Button emailConfig;
	Button archiverConfig;

	public MainConfigWindow() {
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);		
		emailConfig = new Button("Set email configuration");
		emailConfig.setPreferredSize(new Dimension(200, 50));		
		emailConfig.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				MailConfigWindow mailConfigWindow = new MailConfigWindow();
				mailConfigWindow.setSize(300, 300);
				mailConfigWindow.setLocationRelativeTo(null);   
				mailConfigWindow.setVisible(true);
				mailConfigWindow.setResizable(false);
			}			
		});
		archiverConfig = new Button("Set archiver configuration");				
		archiverConfig.setPreferredSize(new Dimension(200, 50));
		archiverConfig.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				ArchiverConfigWindow archiverConfigWindow = new ArchiverConfigWindow();
				archiverConfigWindow.setSize(700, 300);
				archiverConfigWindow.setLocationRelativeTo(null);   
				archiverConfigWindow.setVisible(true);
				archiverConfigWindow.setResizable(false);				
			}			
		});
		Container c = getContentPane();	
		c.setLayout(new FlowLayout());
		c.add(emailConfig);
		c.add(archiverConfig);
	}
}
