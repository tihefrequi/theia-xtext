package {service.namespace}.utils.images.thumbnails;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import org.apache.commons.lang3.StringUtils;

public class ThumbnailOutput {

	private String relativePath;
	private ByteArrayInputStream bIn;
	private ThumbnailConfig config;
	private ThumbnailPictureFormat targetPictureFormat;
	private String fileName;

	public String getRelativePath() {
		return relativePath;
	}

	public InputStream getStream() {
		return this.bIn;
	}

	public long getLength() {
		return bIn.available();
	}

	public ThumbnailOutput relativePath(String relativePath) {
		this.relativePath = relativePath;
		return this;
	}

	public ThumbnailConfig getConfig() {
		return config;
	}

	public ThumbnailOutput config(ThumbnailConfig config) {
		this.config = config;
		return this;
	}

	public ThumbnailOutput generate(BufferedImage image, ThumbnailPictureFormat format) throws IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, format.name(), baos);
			this.bIn = new ByteArrayInputStream(baos.toByteArray());
		} catch (IOException e) {
			throw new IOException("Couldn´t generate Thumbnails for Format=" + format.name());
		}

		return this;
	}

	public ThumbnailPictureFormat getTargetPictureFormat() {
		return targetPictureFormat;
	}

	public ThumbnailOutput targetPictureFormat(ThumbnailPictureFormat targetPictureFormat) {
		this.targetPictureFormat = targetPictureFormat;
		return this;
	}

	public ThumbnailOutput fileName(String fileName) {
		this.fileName = fileName;
		return this;
	}

	public String getFileName() {
		
		String splitFileName = StringUtils.substringBeforeLast(fileName, ".");

		return splitFileName + "-" + getConfig().getName()+ "_" + getConfig().getRawConfigSettings().replaceAll("\\s", "_") + "." + getTargetPictureFormat().getFormat();
	}

	@Override
	public String toString() {
		return "ThumbnailOutput [relativePath=" + relativePath + ", config=" + config + ", targetPictureFormat="
				+ targetPictureFormat + "]";
	}

}