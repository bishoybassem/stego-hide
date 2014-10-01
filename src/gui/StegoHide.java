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

	private static ImageIcon errorIcon;
	private static ImageIcon infoIcon;
	private static ImageIcon safe1Icon;
	private static ImageIcon safe2Icon;
	private static ImageIcon safe3Icon;
	private static String aboutText;
	
	static {
		errorIcon = new ImageIcon(StegoHide.class.getResource("resources/error.png"));
		infoIcon = new ImageIcon(StegoHide.class.getResource("resources/info.png"));
		safe1Icon = new ImageIcon(StegoHide.class.getResource("resources/safe1.png"));
		safe2Icon = new ImageIcon(StegoHide.class.getResource("resources/safe2.png"));
		safe3Icon = new ImageIcon(StegoHide.class.getResource("resources/safe3.png"));
		aboutText = readTextFile("resources/about.txt");
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
		textPane.setText(aboutText);
		
		StyledDocument doc = textPane.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		
		JLabel icon = new JLabel(safe3Icon);
		icon.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		JPanel p1 = new JPanel(new BorderLayout());
		p1.add(icon, BorderLayout.NORTH);
		p1.add(textPane);
		
		JPanel aboutPanel = new JPanel(new GridBagLayout());
		aboutPanel.add(p1);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.add("  Embed  ", new EmbedPanel());
		tabbedPane.add("  Extract  ", new ExtractPanel());
		tabbedPane.add("  About  ", aboutPanel);
		tabbedPane.setFocusable(false);
		tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		setIconImages(Arrays.asList(safe1Icon.getImage(), safe2Icon.getImage()));
		
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
