import java.util.Map;

import fileConfiguration.YamlConfiguration;
import py4j.GatewayServer;

public class saveToFile {
	public static boolean save(Map<String, Object> map, String path) {
		YamlConfiguration yml_word = new YamlConfiguration();
		System.out.println("loading");
		for (String key : map.keySet()) {
			yml_word.set(key, map.get(key));
		}
		System.out.println("saving");
		yml_word.new_save(path);
		return true;
	}

	public static void main(String[] args) {
		System.out.println("started");
		saveToFile app = new saveToFile();
		// app is now the gateway.entry_point
		GatewayServer server = new GatewayServer(app, 25565);
		server.start();
	}
}
