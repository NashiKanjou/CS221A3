
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

import org.apache.commons.text.StringEscapeUtils;

import fileConfiguration.YamlConfiguration;

public class Indexer {
	public static final String filename_Words = "";
	public static final String filename_URLs = "";

	public static File file_Words = new File(filename_Words);
	public static File file_URLs = new File(filename_URLs);

	public static YamlConfiguration yml;

	public static void main(String args[]) {
		yml = new YamlConfiguration(new File("test.yml"));
		/*
		 * System.out.println(yml.getInteger("testint")); for (String str :
		 * yml.getList("testlist")) { System.out.println(str); } List<String> test = new
		 * ArrayList<String>(); test.add("test1"); test.add("test2"); test.add("test3");
		 * yml.set("testofListWriting", test); yml.set("test.path", 2); yml.save(new
		 * File("test.yml"));
		 */
		listFilesForFolder(new File("DEV"));
		yml.save(new File("test.yml"));
	}

	private static void listFilesForFolder(final File folder) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
				return;
			} else {
				if (fileEntry.getName().endsWith(".json")) {
					try {
						scraper(fileEntry);
						return;
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static Map<String, Integer> wordCount = new HashMap<String, Integer>();
	public static List<String> urls = new ArrayList<String>();
	public static int maxWord = 0;
	public static String longestSite = "";

	private static void scraper(File file) throws FileNotFoundException {
		String raw = "";

		// get info

		int count = 0;
		Scanner sc = new Scanner(file);
		String b_raw;
		b_raw = sc.nextLine();

		raw = StringEscapeUtils.unescapeHtml4(b_raw).replaceAll("%09", " ").replaceAll("\t", "").replaceAll("\r", "").replaceAll("\n", " ");

		List<String> list_raw = new ArrayList<String>();
		boolean bool_read = true;
		String str_word = "";
		Stack<String> html_stack = new Stack<String>();
		boolean bool_comment = false;
		for (int i = 0; i < raw.length(); i++) {
			char c = raw.charAt(i);
			if (c == '<' && bool_read) {
				if (str_word.length() > 0 && !bool_comment) {
					list_raw.add(str_word);
				}
				str_word = "";
				bool_read = false;
				continue;
			}
			if (c == '>' && !bool_read) {
				if (str_word.endsWith("--")) {
					bool_comment = false;
					str_word = "";
					continue;
				}
				bool_read = true;
				if (str_word.startsWith("/")) {
					str_word = "";
					if (html_stack.size() > 0) {
						html_stack.pop();
					}
					continue;
				}
				if (!bool_comment) {
					html_stack.push(str_word);
				}
				str_word = "";
				continue;
			}
			String str_type = "";
			if (html_stack.size() > 0) {
				str_type = html_stack.peek();
			}
			if (bool_read && !str_type.startsWith("/")
					&& !(str_type.startsWith("a ") || str_type == "a" || str_type.contains("span")
							|| str_type.equals("p") || str_type.equals("br") || str_type.equals("b")
							|| str_type.equals("i") || str_type.equals("q") || str_type.startsWith("h")
							|| str_type.startsWith("p style"))) {
				continue;
			}
			str_word += c;
			if (str_word == "!--" && !bool_read) {
				bool_comment = true;
				str_word = "";
			}
		}

		sc.close();

		for (String rawline : list_raw) {
			String[] list_replaced = rawline.replaceAll("\n", " ").replaceAll("\\t", " ").replaceAll("\\r", " ").replaceAll("\\n", " ").replaceAll("[^A-Za-z0-9'% ]", "")
					.split(" ");
			/*
			 * String[] list_replaced = rawline.replaceAll(("" + (char) 9),
			 * "").replaceAll("=", "").replaceAll(">", "") .replaceAll("<",
			 * "").replaceAll("*", "").replaceAll("?", "").replaceAll(";",
			 * "").replaceAll(":", "") .replaceAll("!", "").replaceAll("/",
			 * " ").replaceAll("“", "").replaceAll("”", "") .replaceAll("-",
			 * "").replaceAll(",", "").replaceAll("(", "").replaceAll(")",
			 * "").replaceAll(".", "") .replaceAll("[", "").replaceAll("]",
			 * "").replaceAll("&", "").replaceAll("\"", "").split(" ");
			 */
			for (String word : list_replaced) {
				word = word.replaceAll(" ", "");
				if (word == " " || word.length() <= 1 && !(word == "i" || word == "a")) {
					continue;
				}
				count += 1;
				if (!(wordCount.keySet().contains(word))) {
					wordCount.put(word, 1);
				} else {
					wordCount.put(word, wordCount.get(word) + 1);
				}
			}
		}
		if (maxWord < count) {
			maxWord = count;
			longestSite = file.getName();
		}
		yml.set("WordCount", wordCount);
		/*
		 * links = extract_next_links(url, resp)
		 * 
		 * consideredLinks = [] discardedLinks = []
		 * 
		 * for link in links: if (is_valid(link)): consideredLinks.append(link) else:
		 * discardedLinks.append(link)
		 * 
		 * return consideredLinks, int_count, dict_words, discardedLinks
		 */
	}
	
	public static class Text{
		public Text() {
			
		}
	}
}
