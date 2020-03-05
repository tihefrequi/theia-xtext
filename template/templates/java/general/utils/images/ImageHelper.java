package {service.namespace}.utils.images;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import {service.namespace}.utils.images.thumbnails.ThumbnailPictureFormat;
import {service.namespace}.utils.images.thumbnails.ResizingAlgorithms;
import {service.namespace}.utils.images.thumbnails.SpeedQualityIndicator;

public class ImageHelper {

	private static Logger LOG = LoggerFactory.getLogger(ImageHelper.class);
	
	public static BufferedImage resizeImage(BufferedImage sourceImage, int height, int width, SpeedQualityIndicator speed,
			ThumbnailPictureFormat targetFormat) {

		//TODO targetFormat
		
		int imageType = sourceImage.getType();
		if (imageType == BufferedImage.TYPE_BYTE_INDEXED) {
			if (sourceImage.getAlphaRaster() != null) {
				imageType = BufferedImage.TYPE_INT_ARGB;
			} else {
				imageType = BufferedImage.TYPE_INT_RGB;
			}
		}
		// warning formatting to unsupported sourceMimeType
		int targetWidth = 0;
		int targetHeight = 0;

		int imageWidth = sourceImage.getWidth();
		int imageHeight = sourceImage.getHeight();

		targetWidth = Math.min(width, imageWidth);
		targetHeight = Math.min(height, imageHeight);

		targetWidth = Math.min(targetWidth, Math.round((float) (targetHeight * imageWidth) / (float) imageHeight));
		targetHeight = Math.min(targetHeight, Math.round((float) (targetWidth * imageHeight) / (float) imageWidth));

		BufferedImage targetImage = null;

		if (targetWidth == imageWidth && targetHeight == imageHeight) {
			targetImage = sourceImage;
		} else {

			switch (speed) {
			case FAST:
				targetImage = ResizingAlgorithms.getScaledInstanceFAST(sourceImage, imageType, targetWidth,
						targetHeight);
				break;
			case BALANCED:
				targetImage = ResizingAlgorithms.getScaledInstanceBALANCED(sourceImage, imageType, targetWidth,
						targetHeight);
				break;
			case QUALITY:
				targetImage = ResizingAlgorithms.getScaledInstanceQUALITY(sourceImage, imageType, targetWidth,
						targetHeight);
				break;
			default:
				targetImage = ResizingAlgorithms.getScaledInstanceFAST(sourceImage, imageType, targetWidth,
						targetHeight);
				break;
			}
		}
		return targetImage;

	}

	public static ThumbnailPictureFormat detectType(InputStream img) throws IOException {
		
		try (ImageInputStream iis = ImageIO.createImageInputStream(img)){
			
			Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);

			if (iter.hasNext()) {
				ImageReader reader = iter.next();
				String imageFormat = reader.getFormatName();

				for (ThumbnailPictureFormat allowedFormat : ThumbnailPictureFormat.values()) {
					if (allowedFormat.getFormat().equals(imageFormat)) {
						return allowedFormat;
					}
				}
			}
			throw new RuntimeException("Unable to detect source MimeType. stream="+img);
		} catch (IOException e) {
			throw new IOException("Failed to detect image type. stream="+img);
		}finally {
			try {
				img.close();
			} catch (IOException e) {
				throw new IOException("Failed to close Inputstream. stream="+img);
			}
		}
	}
	
	
	 public static BufferedImage convertImage(BufferedImage image, int type) {
	        BufferedImage result = image;

	        if (type != image.getType()) {
	            result = new BufferedImage(image.getWidth(), image.getHeight(), type);
	            Graphics2D graphics = result.createGraphics();
	            graphics.drawImage(image, 0, 0, null);
	            graphics.dispose();
	        }

	        return result;
	    }
	
	
    public static BufferedImage getBufferedImage(InputStream stream) throws IOException {
		try {
			return ImageIO.read(stream);
		} catch (IOException e) {
			throw new IOException("Failed to get BufferedImage from stream="+stream);
		}finally {
			try {
				stream.close();
			} catch (IOException e) {
				throw new IOException("Failed to close Inputstream. stream="+stream);
			}
		}
    }
    
    public static BufferedImage getBufferedImage(Image img) {
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		} else {
			BufferedImage bImage = new BufferedImage(img.getWidth(null), img.getHeight(null),
					BufferedImage.TYPE_INT_RGB);

			Graphics2D bGr = bImage.createGraphics();
			bGr.drawImage(img, 0, 0, null);
			bGr.dispose();
			return bImage;
		}
	}

}
