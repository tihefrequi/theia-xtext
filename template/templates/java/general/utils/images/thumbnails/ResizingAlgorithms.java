package {service.namespace}.utils.images.thumbnails;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import {service.namespace}.utils.images.ImageHelper;

public class ResizingAlgorithms {

	/**
	 * Incremental resizing. 
	 * 
	 * 
	 * @param image
	 * @param imageType
	 * @param width
	 * @param height
	 * @return
	 */
	public static BufferedImage getScaledInstanceFAST(BufferedImage image, int imageType, int width, int height) {

		int w = image.getWidth();
		int h = image.getHeight();

		BufferedImage scaled = ImageHelper.convertImage(image, imageType);
		while (w != width && h != height) {

			w = Math.max(w / 2, width);
			h = Math.max(h / 2, height);

			BufferedImage temp = new BufferedImage(w, h, imageType);
			Graphics2D graphics = temp.createGraphics();
			graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			graphics.drawImage(scaled, 0, 0, w, h, null);
			graphics.dispose();

			scaled = temp;
		}
		return scaled;
	}
	
	/**
	 * 
	 * @param image
	 * @param imageType
	 * @param width
	 * @param height
	 * @return
	 */
	public static BufferedImage getScaledInstanceBALANCED(BufferedImage image, int imageType, int width, int height) {
		return ImageHelper.getBufferedImage(
				image.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING));
	}
	
	
	/**
	 * 
	 * @param image
	 * @param imageType
	 * @param width
	 * @param height
	 * @return
	 */
	public static BufferedImage getScaledInstanceQUALITY(BufferedImage image, int imageType, int width, int height) {
		return ImageHelper.getBufferedImage(
				image.getScaledInstance(width, height, Image.SCALE_SMOOTH));
	}	
	
	
}
