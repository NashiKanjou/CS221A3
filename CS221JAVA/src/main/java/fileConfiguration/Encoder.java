package fileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Encoder {

	@SuppressWarnings("unchecked")
	public static byte[] encode(Map<String, Object> map) {
		String temp = "";
		for (String key : map.keySet()) {
			temp += key + ":";
			Object obj = map.get(key);
			if (obj instanceof Double) {
				temp += "_d_" + obj + ";";
			} else if (obj instanceof Integer) {
				temp += "_i_" + obj + ";";
			} else if (obj instanceof List) {
				temp += "_l_";
				List<String> list = ((List<String>) obj);
				for (int i = 0; i < list.size(); i++) {
					String str = list.get(i);
					if (i == list.size() - 1) {
						temp += str + ";";
					} else {
						temp += str + ",";
					}
				}
			} else {
				temp += "_s_" + obj + ";";
			}
		}
		//System.out.println(temp);
		return encode(temp);
	}

	public static byte[] encode(String str) {// from 16 bit to 8bit
		byte[] result = new byte[str.length()];
		for (int i = 0; i < str.length(); i++) {
			byte b = 0;
			char c = str.charAt(i);
			if (c >= 48 && c <= 57) {// 0-9
				b = (byte) (c + 4);
			} else if (c >= 65 && c <= 90) {// A-Z
				b = (byte) (c - 65);
			} else if (c >= 97 && c <= 122) {// a-z
				b = (byte) (c - 71);
			} else if (c == 39) {// "'"
				b = (byte) (63);
			} else if (c == 44) {// "%"
				b = (byte) (64);
			} else if (c == 36) {// "$"
				b = (byte) (65);
			} else if (c == 8364) {// "€"
				b = (byte) (66);
			} else if (c == 163) {// "£"
				b = (byte) (67);
			} else if (c == 165) {// "¥"
				b = (byte) (68);
			} else if (c == 58) {// ":"
				b = (byte) (69);
			} else if (c == 44) {// "," list sep
				b = (byte) (70);
			} else if (c == 59) {// ";" end of obj
				b = (byte) (71);
			} else if (c == 95) {// "_" type def
				b = (byte) (72);
			} else if (c == 46) {// "." for double
				b = (byte) (73);
			}else {
				continue;
			}
			result[i] = b;
		}
		return result;
	}

	public static Map<String, Object> decode(byte[] data) {
		Map<String, Object> map = new HashMap<String, Object>();
		boolean readType = false;
		String key = "";
		String result = "";
		char type = 0;
		List<String> obj = null;
		for (int i = 0; i < data.length; i++) {
			byte b = data[i];
			char c = 0;
			if (b == 72) {// _type_
				if (readType) {
					switch (type) {
					case 'l':
						obj = new ArrayList<String>();
						break;
					case 'i':
					case 'd':
					case 's':
					default:
						break;
					}
				}
				readType = !readType;
				continue;
			} else if (b == 71) {
				switch (type) {//;
				case 'l':
					map.put(key, obj);
					obj = new ArrayList<String>();
					break;
				case 'i':
					map.put(key, Integer.parseInt(result));
					break;
				case 'd':
					map.put(key, Double.parseDouble(result));
					break;
				case 's':
				default:
					map.put(key, result);
					break;
				}
				key = "";
				result = "";
				continue;
			} else if (b == 70) {//,
				//System.out.println("T:"+result);
				obj.add(result);
				result = "";
				continue;
			} else if (b == 73) {
				c = '.';
			} else if (b == 69) {
				key = result;
				result = "";
				continue;
			} else if (b == 68) {
				c = '¥';
			} else if (b == 67) {
				c = '£';
			} else if (b == 66) {
				c = '€';
			} else if (b == 65) {
				c = '$';
			} else if (b == 64) {
				c = '%';
			} else if (b == 63) {
				c = '\'';
			} else if (b > 51) {
				c = (char) (b - 4);
			} else if (b > 25) {
				c = (char) (b + 71);
			} else {
				c = (char) (b + 65);
			}
			if (readType) {
				type = c;
			} else {
				switch (type) {
				case 'l':
					obj = new ArrayList<String>();
					break;
				case 'i':
				case 'd':
				case 's':
				default:
					result += c;
					break;
				}
			}
		}
		if (key.length() > 0) {
			map.put(key, result);
		}
		return map;
	}
}
