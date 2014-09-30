package engine;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class StegoImage {

	private BufferedImage image;
	private byte[][][] pixels;
	private List<List<int[]>> hideBlocksList;
	private int window;
	private int swaps;
	private int mode;
	
	public static final int EMBED_MODE = 0;
	public static final int EXTRACT_MODE = 1;
	private static final int[][] WINDOW_SIZES = {{1, 3}, {3, 1}, {3, 3}, {1, 5}, {5, 1}, {5, 5}, {3, 5}, {5, 3}};
	
	public StegoImage(BufferedImage image, int mode) {
		this.image = image;
		this.mode = mode;
		pixels = getPixels();
		hideBlocksList = new ArrayList<List<int[]>>();
		if (mode == EMBED_MODE) {
			window = -1;
			for (int i = 0; i < WINDOW_SIZES.length; i++) {
				hideBlocksList.add(getHideBlocks(i));
			}
		} else {
			window = extractWindow();
			hideBlocksList.add(getHideBlocks(window));
		}	
	}
	
	public BufferedImage embed(byte[] bytes, int seed) throws Exception {
		if (mode != EMBED_MODE)
			throw new IllegalStateException();
		
		String bits = StegoTools.toBinaryString(bytes.length, 16) + StegoTools.toBitStream(bytes);
		int min = Integer.MAX_VALUE;
		List<int[]> hideBlocks;
		for (int i = 0; i < hideBlocksList.size(); i++) {
			hideBlocks = hideBlocksList.get(i);
			if (hideBlocks.size() > bits.length() && min > hideBlocks.size()) {
				min = hideBlocks.size();
				window = i;
			}
		}
		
		if (window == -1)
			throw new IllegalArgumentException();
		
		hideBlocks = new ArrayList<int[]>(hideBlocksList.get(window));
		pixels = getPixels();
		embedWindow();
		
		swaps = 0;
		Random gen = new Random(seed);
		int index;
		for (int i = 0; i < bits.length(); i++) {
			index = gen.nextInt(hideBlocks.size());
			embedHelper(hideBlocks.remove(index), bits.charAt(i) == '1');
		}
		return getImage();
	}
	
	private void embedHelper(int[] block, boolean bit) {
		int median = getMedian(block);
		int middle = pixels[block[0]][block[1]][block[2]] & 0xFF;
		if (!bit && middle != median)
			return;
		if (bit && middle == median)
			return;
		
		int value, minDiff = Integer.MAX_VALUE;
		int[] swapBlock = null;
		for (int i = -WINDOW_SIZES[window][0] / 2; i <= WINDOW_SIZES[window][0] / 2; i++) {
			for (int j = -WINDOW_SIZES[window][1] / 2; j <= WINDOW_SIZES[window][1] / 2; j++) {
				value = pixels[block[0]][block[1] + i][block[2] + j] & 0xFF;
				if (bit && value == median) {
					swapBlock = new int[]{block[0], block[1] + i, block[2] + j};
					break;
				}
				if (!bit && value != median && minDiff > Math.abs(median - value)) {
					minDiff = Math.abs(middle - value);
					swapBlock = new int[]{block[0], block[1] + i, block[2] + j};
				}
			}
		}
		byte temp = pixels[block[0]][block[1]][block[2]];
		pixels[block[0]][block[1]][block[2]] = pixels[swapBlock[0]][swapBlock[1]][swapBlock[2]];
		pixels[swapBlock[0]][swapBlock[1]][swapBlock[2]] = temp;
		swaps++;
	}
	
	public byte[] extract(int seed) throws Exception {
		if (mode != EXTRACT_MODE)
			throw new IllegalStateException();
		
		int length = Integer.parseInt(extractHelper(2, seed), 2);
		return StegoTools.toBytes(extractHelper(length + 2, seed).substring(16));
	}
	
	private String extractHelper(int nBytes, int seed) throws Exception {
		List<int[]> hideBlocks = new ArrayList<int[]>(hideBlocksList.get(0));
		Random gen = new Random(seed);
		int index, middle, median;
		int[] block;
		char[] bits = new char[nBytes * 8];
		for (int i = 0; i < nBytes * 8; i++) {
			index = gen.nextInt(hideBlocks.size());
			block = hideBlocks.remove(index);
			middle = pixels[block[0]][block[1]][block[2]] & 0xFF;
			median = getMedian(block);
			bits[i] = middle == median ? '1' : '0';
		}
		return new String(bits);
	}
	
	private void embedWindow() {
		if (pixels.length < 3) {
			pixels[0][0][0] = (byte)((pixels[0][0][0] & 0xF8) | window);
			return;
		}
		for (int i = 0; i < 3; i++) {
			pixels[i][0][0] = (byte)(pixels[i][0][0] & 0xFE | ((window >> (2 - i)) & 0x01));
		}
	}
	
	private int extractWindow() {
		if (pixels.length < 3)
			return pixels[0][0][0] & 0x07;
		
		int window = 0;
		for (int i = 0; i < 3; i++) {
			window |= (byte)(pixels[i][0][0] & 0x01);
			window <<= (i == 2)? 0 : 1;
		}
		return window;
	}
	
	private List<int[]> getHideBlocks(int window) {
		List<int[]> blocks = new ArrayList<int[]>();
		int[] block;
		for (int i = 0; i < pixels.length; i++) {
			for (int j = WINDOW_SIZES[window][0] / 2; j < pixels[0].length - WINDOW_SIZES[window][0] / 2; j += WINDOW_SIZES[window][0]) {
				for (int k = WINDOW_SIZES[window][1] / 2 + 1; k < pixels[0][0].length - WINDOW_SIZES[window][1] / 2; k += WINDOW_SIZES[window][1]) {
					block = new int[]{i, j, k};
					if (!areBlockPixelsEqual(block, window)) {
						blocks.add(block);
					}
				}
			}
		}
		return blocks;
	}
	
	private boolean areBlockPixelsEqual(int[] block, int window) {
		int middle = pixels[block[0]][block[1]][block[2]];
		for (int i = -WINDOW_SIZES[window][0] / 2; i <= WINDOW_SIZES[window][0] / 2; i++) {
			for (int j = -WINDOW_SIZES[window][1] / 2; j <= WINDOW_SIZES[window][1] / 2; j++) {
				if (pixels[block[0]][block[1] + i][block[2] + j] != middle)
					return false;
			}
		}
		return true; 
	}
	
	private int getMedian(int[] block) {
		List<Integer> values = new ArrayList<Integer>();
		for (int i = -WINDOW_SIZES[window][0] / 2; i <= WINDOW_SIZES[window][0] / 2; i++) {
			for (int j = -WINDOW_SIZES[window][1] / 2; j <= WINDOW_SIZES[window][1] / 2; j++) {
				values.add(pixels[block[0]][block[1] + i][block[2] + j] & 0xFF);
			}
		}
		Collections.sort(values);
		return values.get(values.size() / 2);
	}
	
	private byte[][][] getPixels() {
		byte[][][] pixels = new byte[image.getColorModel().getNumComponents()][image.getHeight()][image.getWidth()];
		for (int i = 0; i < pixels.length; i++) {
			for (int j = 0; j < pixels[0].length; j++) {
				for (int k = 0; k < pixels[0][0].length; k++) {
					pixels[i][j][k] = (byte) image.getRaster().getSample(k, j, i);
				}
			}
		}
		return pixels;
	}
	
	public BufferedImage getImage() {
		BufferedImage newImage = StegoTools.copyImage(image);
		for (int i = 0; i < pixels.length; i++) {
			for (int j = 0; j < pixels[0].length; j++) {
				for (int k = 0; k < pixels[0][0].length; k++) {
					newImage.getRaster().setSample(k, j, i, pixels[i][j][k]);
				}
			}
		}
		return newImage;
	}
	
	public int getMaxHideCapacity() {
		int size, max = -1;
		for (int i = 0; i < hideBlocksList.size(); i++) {
			size = hideBlocksList.get(i).size();
			if (max < size)
				max = size;
		}
		int capacity = (max - 16) / 8; 
		return capacity < 0 ? 0 : capacity;
	}
	
	public int getSwaps() {
		return swaps;
	}
}
