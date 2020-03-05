package {service.namespace}.utils.images.thumbnails;

import java.util.Optional;

public enum ThumbnailPictureFormat {

	JPEG("jpeg", "image/jpeg"), PNG("png", "image/png");

	private ThumbnailPictureFormat(String format, String mimeType) {
		this.format = format;
		this.mimeType = mimeType;
	}

	private String format;
	private String mimeType;

	public String getMimeType() {
		return this.mimeType;
	}

	public String getFormat() {
		return this.format;
	}

	public static boolean isAllowedMimeType(String mimeType) {
		return byMimeType(mimeType).isPresent();
	}
	
	public static ThumbnailPictureFormat byMimeTypeOrThrow(String mimeType) {
			
		return byMimeType(mimeType).orElseThrow(() -> new RuntimeException(
				"The mimeType retrival of the sourceImage is not supported or was unsuccessful. sourceMimeType="
						+ mimeType + ". Currently supported formats are .jpeg and .png."));
	}
	
	public static Optional<ThumbnailPictureFormat> byMimeType(String mimeType){
		if(mimeType != null) {
			for (ThumbnailPictureFormat format : ThumbnailPictureFormat.values()) {
				if (mimeType.toLowerCase().equals(format.getMimeType().toLowerCase())) {
					return Optional.of(format);
				}
			}	
		}
		return Optional.empty();
	}

}

