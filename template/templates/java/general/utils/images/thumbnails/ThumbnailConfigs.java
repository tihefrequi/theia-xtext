package {service.namespace}.utils.images.thumbnails;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ThumbnailConfigs {

	Map<String, ThumbnailConfig> namedConfigSettings = new HashMap<>();

	/**
	 * looks e.g. like this small : 200x200 fast jpg OR medium : 300x300 OR large :
	 * 400x400 png
	 * 
	 * TODO erklärung
	 * 
	 * @param configSetting
	 */
	public ThumbnailConfigs config(String name, String configSettings) {
		ThumbnailConfig config = new ThumbnailConfig(name, configSettings);
		this.namedConfigSettings.put(name, config);
		return this;
	}

	// TODO: improve Logic
	public Optional<ThumbnailConfig> getBestResizingConfig(ThumbnailConfig size, int width, int height) {
		return null;
	}

	public Collection<ThumbnailConfig> getConfigs() {
		return namedConfigSettings.values();
	}

}
