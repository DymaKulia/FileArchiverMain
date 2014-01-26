package dk.truelink.ext.folder.configarchiver;

import java.awt.Button;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class ArchiverConfigWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	private Button add;
	private Button delete;

	private Button save;
	private Button cansel;

	private JTable configArchiver;
	private ConfigArchiverTableModel configArchiverTableModel;

	ArrayList<Entry> configuration;

	public ArchiverConfigWindow() {
		createAndConfigureWindowElements();
		fillArchiverConfigWindow();
		addToContainer();
	}

	private void createAndConfigureWindowElements() {
		add = new Button("Add");
		add.setPreferredSize(new Dimension(50, 30));
		add.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				AddConfigWindow archiverConfigWindow = new AddConfigWindow(
						configuration, configArchiverTableModel);
				archiverConfigWindow.setSize(300, 300);
				archiverConfigWindow.setLocationRelativeTo(null);
				archiverConfigWindow.setVisible(true);
				archiverConfigWindow.setResizable(false);
			}
		});

		delete = new Button("Delete");
		delete.setPreferredSize(new Dimension(50, 30));
		delete.addActionListener(new DeleteAction());

		save = new Button("Save");
		save.setPreferredSize(new Dimension(50, 30));
		save.addActionListener(new SaveAction());

		cansel = new Button("Cansel");
		cansel.setPreferredSize(new Dimension(50, 30));
		cansel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ArchiverConfigWindow.this.dispose();
			}
		});
	}

	private void fillArchiverConfigWindow() {

		configuration = new ArrayList<Entry>();
		// Read from Xml and fill configuration
		// ...
		//
		
		Entry entry = new Entry();
		entry.setAgeModify("2");
		entry.setDestFolder("C:/1");
		entry.setSourseFolder("D:/");
		entry.setTempFolder("F:/");

		configuration.add(entry);

		configArchiverTableModel = new ConfigArchiverTableModel(configuration);
		configArchiver = new JTable(configArchiverTableModel);
	}

	private void addToContainer() {
		Container c = getContentPane();
		c.setLayout(new FlowLayout());
		JScrollPane scrollPane = new JScrollPane(configArchiver);
		scrollPane.setPreferredSize(new Dimension(700, 200));
		c.add(scrollPane);
		c.add(add);
		c.add(delete);
		c.add(save);
		c.add(cansel);
	}

	private class DeleteAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent addAction) {

		}
	}

	private class SaveAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent addAction) {

		}
	}

}
