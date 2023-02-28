package fileConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;

public class YamlConfiguration {
	private Map<String, Object> map;
	private Yaml yml;

	public YamlConfiguration(File file) {
		DumperOptions options = new DumperOptions();
		options.setIndent(2);
		options.setPrettyFlow(true);
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

		LoaderOptions loaderOptions = new LoaderOptions();
		loaderOptions.setCodePointLimit(1000 * 1024 * 1024); // 1000 MB
		/*
		 * YAMLFactory yamlFactory = YAMLFactory.builder().
		 * .loaderOptions(loaderOptions) .build(); YAMLMapper mapper = new
		 * YAMLMapper(yamlFactory);
		 */
		Yaml yaml = new Yaml(loaderOptions);
		yml = yaml;
		InputStream inputStream;
		try {
			inputStream = new FileInputStream(file);
			map = yaml.load(inputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public YamlConfiguration() {
		DumperOptions options = new DumperOptions();
		options.setIndent(2);
		options.setPrettyFlow(true);
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		Yaml yaml = new Yaml(options);
		yml = yaml;
		map = new HashMap<String, Object>();
	}

	public int getInt(String key) {
		if (map.containsKey(key)) {
			return (int) map.get(key);
		}
		return 0;
	}

	public String get(String key) {
		if (map.containsKey(key)) {
			return map.get(key).toString();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Object> getList(String key) {
		if (map.containsKey(key)) {
			return (List<Object>) map.get(key);
		}
		return new ArrayList<Object>();
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getMap(String key) {
		if (map.containsKey(key)) {
			return (Map<String, Object>) map.get(key);
		}
		return new HashMap<String, Object>();
	}

	public Map<String, Object> asMap() {
		return map;
	}

	public void set(String key, Object o) {
		if (o != null) {
			map.put(key, o);
		} else {
			map.remove(key);
		}
	}

	public Set<String> getKeySet() {
		return map.keySet();
	}

	@SuppressWarnings("unchecked")
	public void new_save(String path) {
		FileWriter writer;
		Map<Character, Map<String, Object>> temp = new HashMap<Character, Map<String, Object>>();// <char,<word,<doc,list>>>
		for (String str : map.keySet()) {// word <word,<doc,list>>
			char c = str.charAt(0);
			if (temp.containsKey(c)) {
				Map<String, Object> m = temp.get(c);
				m.put(str, map.get(str));
			} else {
				Map<String, Object> m = new HashMap<String, Object>();
				m.put(str, map.get(str));
				temp.put(c, m);
			}
		}
		map.clear();
		try {
			for (char c : temp.keySet()) {
				char p = c;
				if (p > 122 || p < 97) {
					// System.out.println(p + ": " + (int) p);
					p = '#';
				}
				File folder = new File(path + File.separator + p);
				folder.mkdirs();
				Map<String, Object> wordmap = temp.get(c);// <word,<doc,list>>
				Set<String> words = wordmap.keySet();

				for (String word : words) {
					Map<String, List<Integer>> m = ((Map<String, List<Integer>>) wordmap.get(word));
					Set<String> ids = m.keySet();
					for (String id : ids) {

						File file = new File(
								path + File.separator + p + File.separator + word + File.separator + id + ".yml");
						File f = new File(path + File.separator + p + File.separator + word);
						f.mkdirs();

						if (!file.exists()) {
							file.createNewFile();
							writer = new FileWriter(file);
							Map<String, Object> data = new HashMap<String, Object>();
							data.put("index", m.get(id));
							data.put("NumOccurrences", m.get(id).size());
							yml.dump(data, writer);
						} else {
							YamlConfiguration y = new YamlConfiguration(file);
							List<Object> fromfile = y.getList("index");

							List<Integer> newdata = m.get(id);
							List<Integer> result = new ArrayList<Integer>();

							for (Object index : fromfile) {
								result.add((Integer) index);
							}

							for (int index : newdata) {
								result.add((Integer) index);
							}
							Map<String, Object> data = new HashMap<String, Object>();
							data.put("index", result);
							data.put("NumOccurrences", result.size());

							writer = new FileWriter(file);
							yml.dump(data, writer);

						}

					}

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
/*
	@SuppressWarnings("unchecked")
	public void save() {
		FileWriter writer;
		Map<Character, Map<String, Object>> temp = new HashMap<Character, Map<String, Object>>();
		for (String str : map.keySet()) {
			char c = str.charAt(0);
			if (temp.containsKey(c)) {
				Map<String, Object> m = temp.get(c);
				m.put(str, map.get(str));

			} else {
				Map<String, Object> m = new HashMap<String, Object>();
				m.put(str, map.get(str));
				temp.put(c, m);
			}
		}
		map.clear();
		try {
			for (char c : temp.keySet()) {
				if (97 < c || c > 122) {
					c = '#';
				}
				File folder = new File(path + File.separator + c);
				folder.mkdirs();
				Map<String, Object> wordmap = temp.get(c);
				Set<String> words = wordmap.keySet();

				for (String word : words) {
					File file = new File(path + File.separator + c + File.separator + word + ".yml");
					// System.out.println(word);//word
					if (!file.exists()) {
						file.createNewFile();
						writer = new FileWriter(file);
						yml.dump(wordmap.get(word), writer);
					} else {
						YamlConfiguration y = new YamlConfiguration(file);
						Map<String, Object> fromfile = y.asMap();

						Map<String, Object> newdata = (Map<String, Object>) wordmap.get(word);
						Map<String, Object> result = new HashMap<String, Object>();

						for (String url : fromfile.keySet()) {
							// int result = (int) y.getInt(url) + (int) newdata.get(url);
							List<Integer> data = (List<Integer>) fromfile.get(url);
							for (int i : data) {
								if (result.containsKey(url)) {
									List<Integer> list = (List<Integer>) result.get(url);
									list.add(i);
									result.put(url, list);
								} else {
									List<Integer> list = new ArrayList<Integer>();
									list.add(i);
									result.put(url, list);
								}
							}

							// fromfile.put(url, newdata);
						}

						for (String url : newdata.keySet()) {
							List<Integer> data = (List<Integer>) newdata.get(url);
							for (int i : data) {
								if (result.containsKey(url)) {
									List<Integer> list = (List<Integer>) result.get(url);
									list.add(i);
									result.put(url, list);
								} else {
									List<Integer> list = new ArrayList<Integer>();
									list.add(i);
									result.put(url, list);
								}
							}

						}
						writer = new FileWriter(file);
						yml.dump(result, writer);

					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
*/
	public void save(File file) {
		FileWriter writer;
		try {
			writer = new FileWriter(file);
			yml.dump(map, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
