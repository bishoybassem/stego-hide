package engine;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;


public class StegoTools {

	public static BufferedImage copyImage(BufferedImage image) {
		ColorModel cm = image.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = image.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
	
	public static String toBinaryString(int number, int length) {
		String binary = Integer.toBinaryString(number);
		if (binary.length() > length)
			return binary.substring(binary.length() - length);
		if (binary.length() < length)
			return String.format("%0" + (length - binary.length()) + "d", 0) + binary;
		return binary;
	}
	
	public static String toBitStream(byte[] bytes) throws Exception {
		String bits = "";
		for (int i = 0; i < bytes.length; i++) {
			bits += toBinaryString(bytes[i], 8);
		}
		return bits;
	}
	
	public static byte[] toBytes(String bits) {
		byte[] bytes = new byte[bits.length() / 8];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) Integer.parseInt(bits.substring(i * 8, (i + 1) * 8), 2);
		}
		return bytes;
	}
	
}
