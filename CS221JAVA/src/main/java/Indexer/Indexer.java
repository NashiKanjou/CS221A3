package Indexer;

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
import opennlp.tools.stemmer.PorterStemmer;

public class Indexer {
	public static final String filename_Words = "";
	public static final String filename_URLs = "";

	public static File file_Words = new File(filename_Words);
	public static File file_URLs = new File(filename_URLs);
	public static YamlConfiguration yml_word;
	public static YamlConfiguration yml_url;

	public static void main(String args[]) {
		yml_word = new YamlConfiguration();
		listFilesForFolder(new File("." + File.separator + "DEV"));
		yml_word.save();
	}

	private static void listFilesForFolder(final File folder) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
			} else {
				if (fileEntry.getName().endsWith(".json")) {
					try {
						// System.out.println(fileEntry.getName());
						webcount++;
						System.out.println(webcount);
						scraper(fileEntry);
						// return;
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static int counter = 0;
	public static final int SAVENUM = 1000;
	public static Map<String, Integer> wordCount = new HashMap<String, Integer>();
	public static List<String> urls = new ArrayList<String>();
	public static int maxWord = 0;
	public static String longestSite = "";
	public static int webcount = 0;

	private static void scraper(File file) throws FileNotFoundException {

		yml_url = new YamlConfiguration();
		String filename = file.getPath();
		String raw = "";
		// get info

		int count = 0;
		Scanner sc = new Scanner(file);
		String b_raw;
		b_raw = sc.nextLine();

		raw = StringEscapeUtils.unescapeJson(b_raw).replaceAll("&nbsp", " ").replaceAll(":", " ").replaceAll("-", " ").replaceAll("%09", " ")
				.replaceAll("\t", " ").replaceAll("\r", " ").replaceAll("\n", " ").replaceAll("\\t", " ")
				.replaceAll("\\r", " ").replaceAll("\\n", " ").toLowerCase().replaceAll("ç", "c")
				.replaceAll("[úü]", "u").replaceAll("[óõô]", "o").replaceAll("í", "i").replaceAll("[éê]", "e")
				.replaceAll("[áãâà]", "a");

		List<String> list_raw = new ArrayList<String>();
		boolean bool_read = true;
		String str_word = "";
		Stack<String> html_stack = new Stack<String>();
		boolean bool_comment = false;// false to read
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
		PorterStemmer stemmer = new PorterStemmer();
		for (String rawline : list_raw) {
			String[] list_replaced = rawline.replaceAll("'re", "are").replaceAll("'ll", "will").replaceAll("s'", "s")
					.replaceAll("'s", "").replaceAll("n't", " not").replaceAll("[^a-z0-9 ]", "").split(" ");
			for (String word : list_replaced) {
				word = word.replaceAll(" ", "");
				if (word.length() <= 1 && !(word.equals("i") || word.equals("a"))) {
					continue;
				}
				word = stemmer.stem(word);
				count += 1;
				if (!(wordCount.keySet().contains(word))) {
					Map<String, Integer> map = new HashMap<String, Integer>();
					map.put(filename, 1);
					counter++;
					yml_word.set(word, map);
				} else {
					Map<String, Object> map = yml_word.getMap(word);
					map.put(filename, (int) map.get(filename) + 1);
					yml_word.set(word, map);
				}

				if (counter == SAVENUM) {
					System.out.println(filename + ": saving");
					yml_word.save();
					yml_word = new YamlConfiguration();
					counter = 0;
				}
			}
		}
		if (maxWord < count) {
			maxWord = count;
			longestSite = file.getName();
		}
		// yml_url.set("", wordCount);

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

}
