package fileConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class YamlConfiguration {
	private Map<String, Object> map;
	private Yaml yml;

	public YamlConfiguration(File file) {
		DumperOptions options = new DumperOptions();
		options.setIndent(2);
		options.setPrettyFlow(true);
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		Yaml yaml = new Yaml(options);
		yml = yaml;
		InputStream inputStream;
		try {
			inputStream = new FileInputStream(file);
			map = yaml.load(inputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public int getInteger(String key) {
		if (map.containsKey(key)) {
			return (int) map.get(key);
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public List<String> getList(String key) {
		if (map.containsKey(key)) {
			return (List<String>) map.get(key);
		}
		return new ArrayList<String>();
	}

	public void set(String key, Object o) {
		if (o != null) {
			map.put(key, o);
		} else {
			map.remove(key);
		}
	}

	public Set<String> getKeySets() {
		return map.keySet();
	}

	public void save(File file) {
		FileWriter writer;
		try {
			writer = new FileWriter(file);
			yml.dump(map, writer);
			// Yaml(options).dump(map, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
