package cn.bmob.so;

import java.security.MessageDigest;

/**
  * @ClassName: EncryptMD5
  * @Description: 加密类
  * @author smile
  * @date 2014-12-5 上午11:25:46
  */
public class EncryptMD5 {
	
	public final static String setMD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] strTemp = s.getBytes();
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}

	}

	public final static String setPatternMD5(byte[] strTemp) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try {

			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}

	}

	//用于应用程序签名解密
	private static final char[] hexDigits = { 48, 49, 50, 51, 52, 53, 54, 55,
			56, 57, 97, 98, 99, 100, 101, 102 };

	public static String hexdigest(String paramString) {
		try {
			String str = hexdigest(paramString.getBytes());
			return str;
		} catch (Exception localException) {
		}
		return null;
	}

	public static String hexdigest(byte[] paramArrayOfByte) {
		try {
			MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
			localMessageDigest.update(paramArrayOfByte);
			byte[] arrayOfByte = localMessageDigest.digest();
			char[] arrayOfChar = new char[32];
			int i = 0;
			int j = 0;
			while (true) {
				if (i >= 16)
					return new String(arrayOfChar);
				int k = arrayOfByte[i];
				int m = j + 1;
				arrayOfChar[j] = hexDigits[(0xF & k >>> 4)];
				j = m + 1;
				arrayOfChar[m] = hexDigits[(k & 0xF)];
				i++;
			}
		} catch (Exception localException) {
		}
		return null;
	}
}
