package dk.truelink.ext.folder.configarchiver;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JFrame;

public class MainConfigWindow extends JFrame {

	Button emailConfig;
	Button archiverConfig;

	public MainConfigWindow() {
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		emailConfig = new Button("Set email configuration");
		archiverConfig = new Button("Set archiver configuration");
		archiverConfig.setPreferredSize(new Dimension(200, 50));
		emailConfig.setPreferredSize(new Dimension(200, 50));
		Container c = getContentPane();	
		c.setLayout(new FlowLayout());
		c.add(emailConfig);
		c.add(archiverConfig);
	}
}
