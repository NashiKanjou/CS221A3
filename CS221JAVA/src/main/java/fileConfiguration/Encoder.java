package fileConfiguration;

public class Encoder {
	public static byte[] Encode(String str) {// to 6bit
		byte[] result = new byte[str.length()];
		for (int i = 0; i < str.length(); i++) {
			byte b = 0;
			char c = str.charAt(i);
			if (c >= 48 && c <= 57) {
				b = (byte) (b << 1);
				System.out.println(b);
			} else {

			}
		}
		return result;
	}
}
