package gui;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.io.File;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

@SuppressWarnings("serial")
public class StegoHide extends JFrame {

	private static final ImageIcon ERROR_ICON;
	private static final ImageIcon INFO_ICON;
	private static final ImageIcon SAFE1_ICON;
	private static final ImageIcon SAFE2_ICON;
	private static final ImageIcon SAFE3_ICON;
	private static final String ABOUT_TEXT;
	
	static {
		ERROR_ICON = new ImageIcon(StegoHide.class.getResource("resources/error.png"));
		INFO_ICON = new ImageIcon(StegoHide.class.getResource("resources/info.png"));
		SAFE1_ICON = new ImageIcon(StegoHide.class.getResource("resources/safe1.png"));
		SAFE2_ICON = new ImageIcon(StegoHide.class.getResource("resources/safe2.png"));
		SAFE3_ICON = new ImageIcon(StegoHide.class.getResource("resources/safe3.png"));
		ABOUT_TEXT = readTextFile("resources/about.txt");
	}
	
	public StegoHide() {
		super("Stego-Hide");
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			
		}
		
		JTextPane textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.setFocusable(false);
		textPane.setOpaque(false);
		textPane.setText(ABOUT_TEXT);
		
		StyledDocument doc = textPane.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		
		JLabel icon = new JLabel(SAFE3_ICON);
		icon.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		JPanel p1 = new JPanel(new BorderLayout());
		p1.add(icon, BorderLayout.NORTH);
		p1.add(textPane);
		
		JPanel aboutPanel = new JPanel(new GridBagLayout());
		aboutPanel.add(p1);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.add("  Hide  ", new HidePanel());
		tabbedPane.add("  Extract  ", new ExtractPanel());
		tabbedPane.add("  About  ", aboutPanel);
		tabbedPane.setFocusable(false);
		tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		setIconImages(Arrays.asList(SAFE1_ICON.getImage(), SAFE2_ICON.getImage()));
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		add(tabbedPane);
		pack();
		setLocationRelativeTo(null);
	}
	
	public File selectImage() {
		JFileChooser imageChooser = new JFileChooser();
		imageChooser.setAcceptAllFileFilterUsed(false);
		imageChooser.addChoosableFileFilter(new FileNameExtensionFilter("PNG files", "png"));
		imageChooser.addChoosableFileFilter(new FileNameExtensionFilter("BMP files", "bmp"));
		imageChooser.addChoosableFileFilter(new FileNameExtensionFilter("GIF files", "gif"));
		
		int returnVal = imageChooser.showDialog(this, "Open");
	    if (returnVal == JFileChooser.APPROVE_OPTION)
	    	return imageChooser.getSelectedFile();
	    
	    return null;
	}
	
	public void showErrorMessage(String error) {
		JOptionPane.showMessageDialog(null, error,  "Error", JOptionPane.ERROR_MESSAGE, ERROR_ICON);
	}
	
	public void showInformationMessage(String message) {
		JOptionPane.showMessageDialog(null, message,  "Information", JOptionPane.INFORMATION_MESSAGE, INFO_ICON);
	}
	
	public static String readTextFile(String path) {
		String text = "";
		Scanner sc = null;
		try {
			sc = new Scanner(StegoHide.class.getResourceAsStream(path));
			while (sc.hasNext()) {
				text += sc.nextLine();
				if (sc.hasNext()) {
					text += "\n";
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (sc != null) {
				sc.close();
			}	
		}
		return text;
	}
	
	public static void main(String[] args) {
		new StegoHide().setVisible(true);
	}

}
