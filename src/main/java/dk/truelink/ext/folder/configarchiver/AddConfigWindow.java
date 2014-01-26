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
import javax.swing.JLabel;
import javax.swing.JTextField;

public class AddConfigWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	private JLabel sourseFolderLabel;
	private JTextField sourse;
	private JLabel sourseFolderError;

	private JLabel destFolderLabel;
	private JTextField dest;
	private JLabel destFolderError;

	private JLabel tempFolderLabel;
	private JTextField temp;
	private JLabel tempFolderError;

	private JLabel ageModifyLabel;
	private JTextField age;
	private JLabel ageModifyError;

	private Button ok;
	private Button cansel;

	private ArrayList<Entry> configuration;
	private ConfigArchiverTableModel configArchiverTableModel;

	public AddConfigWindow(ArrayList<Entry> configuration,
			ConfigArchiverTableModel configArchiverTableModel) {

		this.configArchiverTableModel = configArchiverTableModel;
		this.configuration = configuration;

		createAndConfigureWindowElements();
		addToContainer();
	}

	private void createAndConfigureWindowElements() {
		sourseFolderLabel = new JLabel("Sourse");
		sourseFolderLabel.setPreferredSize(new Dimension(80, 30));
		sourse = new JTextField();
		sourse.setPreferredSize(new Dimension(100, 30));
		sourseFolderError = new JLabel();
		sourseFolderError.setPreferredSize(new Dimension(50, 30));

		destFolderLabel = new JLabel("dest");
		destFolderLabel.setPreferredSize(new Dimension(80, 30));
		dest = new JTextField();
		dest.setPreferredSize(new Dimension(100, 30));
		destFolderError = new JLabel();
		destFolderError.setPreferredSize(new Dimension(50, 30));

		tempFolderLabel = new JLabel("temp");
		tempFolderLabel.setPreferredSize(new Dimension(80, 30));
		temp = new JTextField();
		temp.setPreferredSize(new Dimension(100, 30));
		tempFolderError = new JLabel();
		tempFolderError.setPreferredSize(new Dimension(50, 30));

		ageModifyLabel = new JLabel("age modify");
		ageModifyLabel.setPreferredSize(new Dimension(80, 30));
		age = new JTextField();
		age.setPreferredSize(new Dimension(100, 30));
		ageModifyError = new JLabel();
		ageModifyError.setPreferredSize(new Dimension(50, 30));

		ok = new Button("Ok");
		ok.setPreferredSize(new Dimension(50, 30));
		ok.addActionListener(new OkAction());

		cansel = new Button("Cansel");
		cansel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				AddConfigWindow.this.dispose();
			}
		});
		cansel.setPreferredSize(new Dimension(50, 30));

	}

	private void addToContainer() {

		Container c = getContentPane();
		c.setLayout(new FlowLayout());

		c.add(sourseFolderLabel);
		c.add(sourse);
		c.add(sourseFolderError);

		c.add(destFolderLabel);
		c.add(dest);
		c.add(destFolderError);

		c.add(tempFolderLabel);
		c.add(temp);
		c.add(tempFolderError);

		c.add(ageModifyLabel);
		c.add(age);
		c.add(ageModifyError);

		c.add(ok);
		c.add(cansel);
	}

	private class OkAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent addAction) {

			String[] informationFromElements = new String[4];
			if (readInformationFromElements(informationFromElements)) {
			
				Entry entry = new Entry();
				entry.setSourseFolder(informationFromElements[0]);
				entry.setDestFolder(informationFromElements[1]);
				entry.setTempFolder(informationFromElements[2]);
				entry.setAgeModify(informationFromElements[3]);
				
				configuration.add(entry);
				configArchiverTableModel.fireTableStructureChanged();
			
				AddConfigWindow.this.dispose();
			}
		}

		private boolean readInformationFromElements(
				String[] informationFromElements) {
			
			int countErrors = 0;
			if (sourse.getText().length() == 0) {
				countErrors++;
				sourseFolderError.setText("Empty");
			} else {
				informationFromElements[0] = sourse.getText();
				sourseFolderError.setText("");
			}

			if (dest.getText().length() == 0) {
				countErrors++;
				destFolderError.setText("Empty");
			} else {
				informationFromElements[1] = dest.getText();
				destFolderError.setText("");
			}

			if (temp.getText().length() == 0) {
				countErrors++;
				tempFolderError.setText("Empty");
			} else {
				informationFromElements[2] = temp.getText();
				tempFolderError.setText("");
			}

			if (age.getText().length() == 0) {
				countErrors++;
				ageModifyError.setText("Empty");
			} else {
				informationFromElements[3] = age.getText();
				ageModifyError.setText("");
			}

			if (countErrors == 0) {
				return true;
			} else {
				return false;
			}
		}
	}
}
