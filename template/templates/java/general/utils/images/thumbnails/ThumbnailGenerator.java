package {service.namespace}.utils.images.thumbnails;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import {service.namespace}.utils.images.ImageHelper;

public class ThumbnailGenerator {
	private String fileName;
	private ThumbnailConfigs configs;
	private ThumbnailPictureFormat sourceFormat;
	private InputStream imageStream;

	public ThumbnailGenerator(String fileName, String mimeType, InputStream image, ThumbnailConfigs configs) {
		this.fileName = fileName;
		this.configs = configs;
		this.sourceFormat = ThumbnailPictureFormat.byMimeTypeOrThrow(mimeType);
		this.imageStream = image;
	}

	public List<ThumbnailOutput> buildThumbnails() throws IOException {
		try {
			List<ThumbnailOutput> outputs = new ArrayList<>();
			BufferedImage originalImage = ImageIO.read(imageStream);

			for (ThumbnailConfig config : configs.getConfigs()) {

				// resize Image using config-settings
				BufferedImage resizedImage = ImageHelper.resizeImage(originalImage, config.getHeight(),
						config.getWidth(), config.getSpeed(), config.getConvertToFormat().orElse(sourceFormat));

				// decide target Format
				ThumbnailPictureFormat targetFormat = config.getConvertToFormat().orElse(sourceFormat);

				// fill Output with resized image and necessary data
				ThumbnailOutput output = new ThumbnailOutput();
				output.config(config);
				output.fileName(fileName);
				output.targetPictureFormat(targetFormat);

				String thumbnailFileName = fileName + "-" + config.getName() + "." + targetFormat.name();
				output.relativePath(getThumbnailBaseFolder() + "/" + thumbnailFileName);

				output.generate(resizedImage, targetFormat);
				outputs.add(output);
			}
			return outputs;

		} catch (IOException e) {
			throw new IOException("Failed to build Thumbnails! fileName=" + fileName + " configs=" + configs, e);

		} finally {
			try {
				imageStream.close();
			} catch (IOException e) {
				throw new IOException(
						"Failed to close source InputStream! fileName=" + fileName + " configs=" + configs, e);
			}
		}

	}

	public String getThumbnailBaseFolder() {
		return fileName + "-thumbnails";
	}

	public String getFileName() {
		return this.fileName;
	}

}
