package {service.namespace}.utils.images.thumbnails;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ThumbnailConfig {

	private String name;
	private int height;
	private int width;
	private Optional<ThumbnailPictureFormat> convertToFormat;
	private SpeedQualityIndicator speed;
	private boolean clearMetadata;
	private String rawConfigSettings;

	public ThumbnailConfig(String name, String configSettings) {
		name(name);
		clearMetadata(true);

		rawConfigSettings(configSettings);
		
		List<String> settings = Arrays.asList(configSettings.toLowerCase().split("\\s+"));
		for (String setting : settings) {
			if (setting.contains("x")) {
				String[] sizeString = setting.split("x");
				width(Integer.parseInt(sizeString[0]));
				height(Integer.parseInt(sizeString[1]));
				break;
			}
		}

		if (settings.contains(SpeedQualityIndicator.FAST.name().toLowerCase())) {
			speed(SpeedQualityIndicator.FAST);
		} else if (settings.contains(SpeedQualityIndicator.BALANCED.name().toLowerCase())) {
			speed(SpeedQualityIndicator.BALANCED);
		} else if (settings.contains(SpeedQualityIndicator.QUALITY.name().toLowerCase())) {
			speed(SpeedQualityIndicator.QUALITY);
		} else {
			speed(SpeedQualityIndicator.FAST);
		}

		if (settings.contains(ThumbnailPictureFormat.JPEG.getFormat())) {
			convertToFormat(ThumbnailPictureFormat.JPEG);
		} else if (settings.contains(ThumbnailPictureFormat.PNG.getFormat())) {
			convertToFormat(ThumbnailPictureFormat.PNG);
		} else {
			convertToFormat(Optional.empty());
		}
	}

	public ThumbnailConfig rawConfigSettings(String rawConfigSettings) {
		this.rawConfigSettings = rawConfigSettings;
		return this;
	}
	
	public String getRawConfigSettings() {
		return this.rawConfigSettings;
	}
	
	public ThumbnailPictureFormat getTargetFormat() {
		return null;
	}

	public String getName() {
		return name;
	}

	public ThumbnailConfig name(String name) {
		this.name = name;
		return this;
	}

	public int getHeight() {
		return height;
	}

	public ThumbnailConfig height(int height) {
		this.height = height;
		return this;
	}

	public int getWidth() {
		return width;
	}

	public ThumbnailConfig width(int width) {
		this.width = width;
		return this;
	}

	public Optional<ThumbnailPictureFormat> getConvertToFormat() {
		return convertToFormat;
	}

	public ThumbnailConfig convertToFormat(ThumbnailPictureFormat format) {
		return convertToFormat(Optional.of(format));
	}

	public ThumbnailConfig convertToFormat(Optional<ThumbnailPictureFormat> format) {
		this.convertToFormat = format;
		return this;
	}

	public SpeedQualityIndicator getSpeed() {
		return speed;
	}

	public ThumbnailConfig speed(SpeedQualityIndicator speed) {
		this.speed = speed;
		return this;
	}

	public boolean isClearMetadata() {
		return clearMetadata;
	}

	public ThumbnailConfig clearMetadata(boolean clearMetadata) {
		this.clearMetadata = clearMetadata;
		return this;
	}

	@Override
	public String toString() {
		return "StormThumbnailConfig [name=" + name + ", height=" + height + ", width=" + width + ", format="
				+ convertToFormat + ", speed=" + speed + ", clearMetadata=" + clearMetadata + "]";
	}

}