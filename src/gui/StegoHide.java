package gui;

import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

@SuppressWarnings("serial")
public class StegoHide extends JFrame {

	private static ImageIcon errorIcon;
	private static ImageIcon infoIcon;
	private static ImageIcon safe1Icon;
	
	private JFileChooser imageChooser;
	
	static {
		errorIcon = new ImageIcon(StegoHide.class.getResource("resources/error.png"));
		infoIcon = new ImageIcon(StegoHide.class.getResource("resources/info.png"));
		safe1Icon = new ImageIcon(StegoHide.class.getResource("resources/safe1.png"));
	}
	
	public StegoHide() {
		super("Stego-Hide");
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			
		}
		
		imageChooser = new JFileChooser();
		imageChooser.setAcceptAllFileFilterUsed(false);
		imageChooser.addChoosableFileFilter(new FileNameExtensionFilter("PNG files", "png"));
		imageChooser.addChoosableFileFilter(new FileNameExtensionFilter("BMP files", "bmp"));
		imageChooser.addChoosableFileFilter(new FileNameExtensionFilter("GIF files", "gif"));
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.add("  Embed  ", new EmbedPanel());
		tabbedPane.add("  Extract  ", new ExtractPanel());
		tabbedPane.setFocusable(false);
		tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		setIconImage(safe1Icon.getImage());
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		add(tabbedPane);
		pack();
		setLocationRelativeTo(null);
	}
	
	public File selectImage() {
		int returnVal = imageChooser.showDialog(null, "Open");
	    if (returnVal == JFileChooser.APPROVE_OPTION)
	    	return imageChooser.getSelectedFile();
	    
	    return null;
	}
	
	public void showErrorMessage(String error) {
		JOptionPane.showMessageDialog(null, error,  "Error", JOptionPane.ERROR_MESSAGE, errorIcon);
	}
	
	public void showInformationMessage(String message) {
		JOptionPane.showMessageDialog(null, message,  "Information", JOptionPane.INFORMATION_MESSAGE, infoIcon);
	}
	
	public static void main(String[] args) {
		new StegoHide().setVisible(true);
	}

}
