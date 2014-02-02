package dk.truelink.ext.folder.configarchiver;


public class BeginMainConfigWindow {
	
	public static void main(String[] args) {
		
		MainConfigWindow mainConfigWindow = new MainConfigWindow();
        mainConfigWindow.setSize(250, 150);
        mainConfigWindow.setLocationRelativeTo(null);              
        mainConfigWindow.setVisible(true);
        mainConfigWindow.setResizable(false);
	}	
}
