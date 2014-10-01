package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import engine.StegoImage;

@SuppressWarnings("serial")
public class EmbedPanel extends JPanel {
	
	private StegoImage stegoImg;
	private JTextField imgName;
	private String imgFormat;
	private JTextField seed;
	private File embedFile;
	
	public EmbedPanel() {
		super(new BorderLayout(0, 10));
		
		imgName = new JTextField(15);
		imgName.setEnabled(false);
		
		final JTextField capacity = new JTextField(15);
		capacity.setEnabled(false);
		
		final JTextField fileName = new JTextField(15);
		fileName.setEnabled(false);
		
		final JTextField fileSize = new JTextField(15);
		fileSize.setEnabled(false);
		
		seed = new JTextField(15);
		
		JButton browseImg = new JButton("Browse");
		browseImg.setFocusable(false);
		browseImg.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				File image = ((StegoHide)getTopLevelAncestor()).selectImage();
				if (image == null)
					return;
				
				try {
					stegoImg = new StegoImage(ImageIO.read(image), StegoImage.EMBED_MODE);
					imgName.setText(image.getName());
					capacity.setText(stegoImg.getMaxHideCapacity() + " Bytes");
					imgFormat = getImageFormat(image);
				} catch (Exception e) {
					
				}
			}
			
		});
		
		JButton browseFile = new JButton("Browse");
		browseFile.setFocusable(false);
		browseFile.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser jfc = new JFileChooser();
				int returnVal = jfc.showDialog(null, "Open");
			    if (returnVal != JFileChooser.APPROVE_OPTION)
			    	return;
			    
			    embedFile = jfc.getSelectedFile();
			    fileName.setText(embedFile.getName());
			    fileSize.setText(embedFile.length() + " Bytes");
			}
			
		});
		
		JButton embed = new JButton("Embed File");
		embed.setFocusable(false);
		embed.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				embed();
			}
			
		});
		
		JPanel p1 = new JPanel(new GridLayout(5, 1, 0, 5));
		p1.add(new JLabel("Cover Image"));
		p1.add(new JLabel("Hide Capacity"));
		p1.add(new JLabel("Seed/Key"));
		p1.add(new JLabel("File"));
		p1.add(new JLabel("File Size"));
		p1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
		
		JPanel p2 = new JPanel(new GridLayout(5, 1, 0, 5));
		p2.add(imgName);
		p2.add(capacity);
		p2.add(seed);
		p2.add(fileName);
		p2.add(fileSize);
		
		JPanel p3 = new JPanel(new GridLayout(5, 1, 0, 5));
		p3.add(browseImg);
		p3.add(Box.createGlue());
		p3.add(Box.createGlue());
		p3.add(browseFile);
		p3.add(Box.createGlue());
		
		JPanel p4 = new JPanel(new BorderLayout(10, 0));
		p4.add(p1, BorderLayout.WEST);
		p4.add(p2);
		p4.add(p3, BorderLayout.EAST);
		
		add(p4);
		add(embed, BorderLayout.SOUTH);
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}
	
	public void embed() {		
		if (stegoImg == null) {
			((StegoHide)getTopLevelAncestor()).showErrorMessage("Please select a cover image!");
			return;
		}
		
		int seedValue;
		try {
			seedValue = Integer.parseInt(seed.getText());
		} catch(NumberFormatException ex) {
			((StegoHide)getTopLevelAncestor()).showErrorMessage("Please enter a valid seed number!");
			return;
		}
		
		if (embedFile == null) {
			((StegoHide)getTopLevelAncestor()).showErrorMessage("Please select the desired file to be hidden!");
			return;
		}
				
		try {
			byte[] fileBytes = Files.readAllBytes(embedFile.toPath());
			String ext = embedFile.getName().substring(embedFile.getName().lastIndexOf('.') + 1);
			byte[] extBytes = String.format("%-5s", ext).substring(0, 5).getBytes();
			byte[] bytes = new byte[fileBytes.length + extBytes.length];
			System.arraycopy(extBytes, 0, bytes, 0, extBytes.length);
			System.arraycopy(fileBytes, 0, bytes, extBytes.length, fileBytes.length);
			
			BufferedImage newImg = stegoImg.embed(bytes, seedValue);
			String newName = imgName.getText().substring(0, imgName.getText().lastIndexOf('.'));
			newName += "Stego." + imgFormat;
			
			JFileChooser jfc = new JFileChooser();
			jfc.setAcceptAllFileFilterUsed(false);
			jfc.addChoosableFileFilter(new FileNameExtensionFilter(imgFormat.toUpperCase() + " files", imgFormat));
			jfc.setSelectedFile(new File(newName));

			int result = jfc.showSaveDialog(this);
			if (result != JFileChooser.APPROVE_OPTION)
				return;

			ImageIO.write(newImg, imgFormat, jfc.getSelectedFile());
			((StegoHide)getTopLevelAncestor()).showInformationMessage("The embedding process is successful!");
		} catch(IllegalArgumentException ex) {
			((StegoHide)getTopLevelAncestor()).showErrorMessage("The selected file's size exceeds the image's hide capacity!");
		} catch(IOException ex) {
			((StegoHide)getTopLevelAncestor()).showErrorMessage("Could not save the image file!");
		} catch(Exception ex) {
			((StegoHide)getTopLevelAncestor()).showErrorMessage("Could not embed the text in the image!");
		}
	}
	
	public static String getImageFormat(File file) {
		try {
			ImageInputStream iis = ImageIO.createImageInputStream(file);
			Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
			if (!iter.hasNext()) 
				return null;

			String format = iter.next().getFormatName();
			iis.close();
			return format;
		} catch (Exception ex) {
			return null;
		}
	}
	
}
