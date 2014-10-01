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
	
	public static boolean[] toBitArray(int number, int length) {
		boolean[] bits = new boolean[length];
		boolean negative = number < 0;
		number = Math.abs(number);
		boolean reverse = false;
		for (int i = bits.length - 1; i >= 0; i--) {
			bits[i] = number % 2 == (reverse ? 0 : 1);
			number = number / 2 ;
			if (negative && bits[i])
				reverse = true;
		}
		return bits;
	}
	
	public static boolean[] toBitArray(byte[] bytes) throws Exception {
		boolean[] bits = new boolean[bytes.length * 8];
		for (int i = 0; i < bytes.length; i++) {
			System.arraycopy(toBitArray(bytes[i], 8), 0, bits, i * 8, 8);
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
