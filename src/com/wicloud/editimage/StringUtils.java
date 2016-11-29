/**
 * $RCSfile: StringUtils.java,v $ $Revision: 1.11.2.2 $ $Date: 2001/01/16
 * 06:06:07 $ Copyright (C) 2000 CoolServlets.com. All rights reserved.
 * =================================================================== The
 * Apache Software License, Version 1.1 Redistribution and use in source and
 * binary forms, with or without modification, are permitted provided that the
 * following conditions are met: 1. Redistributions of source code must retain
 * the above copyright notice, this list of conditions and the following
 * disclaimer. 2. Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution. 3. The
 * end-user documentation included with the redistribution, if any, must include
 * the following acknowledgment: "This product includes software developed by
 * CoolServlets.com (http://www.coolservlets.com)." Alternately, this
 * acknowledgment may appear in the software itself, if and wherever such
 * third-party acknowledgments normally appear. 4. The names "Jive" and
 * "CoolServlets.com" must not be used to endorse or promote products derived
 * from this software without prior written permission. For written permission,
 * please contact webmaster@coolservlets.com. 5. Products derived from this
 * software may not be called "Jive", nor may "Jive" appear in their name,
 * without prior written permission of CoolServlets.com. THIS SOFTWARE IS
 * PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL COOLSERVLETS.COM OR ITS
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ==================================================================== This
 * software consists of voluntary contributions made by many individuals on
 * behalf of CoolServlets.com. For more information on CoolServlets.com, please
 * see <http://www.coolservlets.com>.
 */

package com.wicloud.editimage;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;
import android.util.Log;


/**
 * String基本处理方式 Utility class to peform common String manipulation algorithms.
 */
public class StringUtils {

	public final static String NULL = "null";
	public final static String TRUE = "1";
	public final static String FALSE = "0";

	public static final String lineSeparator = System.getProperty("line.separator");

	/**
	 * Initialization lock for the whole class. Init's only happen once per
	 * class load so this shouldn't be a bottleneck.
	 */
	private static Object initLock = new Object();

	/** 十六进制数字 */
	public final static String[] hexDigits = {
			"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"
	};

	/**
	 * convert byte array to hex string.
	 * 将字节数组转换为十六进制字符串
	 *
	 * @param byteArr
	 * @return
	 */
	public static String byteArrayToHexString(byte[] byteArr) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < byteArr.length; i++) {
			resultSb.append(byteToHexString(byteArr[i]));
		}

		return resultSb.toString();
	}

	/**
	 * 将字节数转换为十六进制字符串
	 *
	 * @param b
	 * @return
	 */
	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0) n = 256 + n;
		int d1 = n / 16;
		int d2 = n % 16;

		return hexDigits[d1] + hexDigits[d2];
	}

	/**
	 * 加密字节数组
	 * 
	 * @param bytes
	 * @return byte[]
	 */
	public static byte[] encrypt(byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			return bytes;
		}

		MessageDigest md = getMessageDigest();
		byte[] newBytes;
		synchronized (md) {
			md.reset();

			// MD5 的计算结果是一个 128 位的长整数
			newBytes = md.digest(bytes);
		}

		return newBytes;
	}

	/**
	 * 获得MD5加密实例
	 * 
	 * @return MessageDigest
	 */
	private synchronized static MessageDigest getMessageDigest() {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("找不到需要的加密算法:MD5");
		}

		return messageDigest;

	}

	/**
	 * 字符串加密
	 * 
	 * @param str
	 * @return String
	 */
	public static String encrypt(String str) {
		if (str == null || str.length() == 0) {
			return "";
		}

		byte[] newBytes = encrypt(str.getBytes());
		// 返回16进制表示的字符串,一个byte使用2个字符表示,如:9 => '09'
		StringBuffer buf = new StringBuffer(newBytes.length * 2);
		for (int i = 0; i < newBytes.length; i++) {
			// 因为byte是有符号的,扩展为int后,会使用1来补充,现在的目的就是把其认为是无符号的
			String c = Integer.toHexString(newBytes[i] & 0xff);
			if (c.length() < 2) {
				buf.append("0").append(c);
			} else {
				buf.append(c);
			}
		}

		return buf.toString();
	}

	/**
	 * MD5 Encode
	 *
	 * @param origin
	 * @return
	 */
	public static String MD5Encode(String origin) {
		String resultString = null;

		try {
			resultString = new String(origin);
			MessageDigest md = MessageDigest.getInstance("MD5");
			resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
		} catch (Exception ex) {
		}

		return resultString;
	}

	/**
	 * 判断是否包含字符集
	 *
	 * @param aStr
	 * @param bStr
	 * @param ignoreCase 忽略大小写
	 * @return
	 */
	public static boolean containsStr(String aStr, String bStr, boolean ignoreCase) {
		if (aStr == bStr) return true;

		try {
			String tmpAstr = aStr;
			String tmpBstr = bStr;
			if (ignoreCase) {
				if (!StringUtils.isNull(aStr)) {
					tmpAstr = aStr.toLowerCase();
				}
				if (!StringUtils.isNull(bStr)) {
					tmpBstr = bStr.toLowerCase();
				}
			}

			return tmpAstr.contains(tmpBstr);
		} catch (Exception e) {
		}

		return false;
	}

	/**
	 * 修剪字符串
	 *
	 * @param source
	 * @return
	 */
	public static String trim(String source) {
		return source == null ? null : source.trim();
	}

	/**
	 * 删除左边第一个空格
	 *
	 * @param source
	 * @return
	 */
	public static String ltrim(String source) {
		try {
			return source.replaceFirst("^\\s", "");
		} catch (Exception e) {
		}
		return source;
	}

	/**
	 * 删除右边最后一个空格
	 *
	 * @param source
	 * @return
	 */
	public static String rtrim(String source) {
		try {
			return source.replaceFirst("\\s+$", "");
		} catch (Exception e) {
		}
		return source;
	}

	/**
	 * 删除左右边最后一个空格
	 *
	 * @param source
	 * @return
	 */
	public static String lrtrim(String source) {
		try {
			return source.replaceAll("^\\s+.*(\\s+)$", "");
		} catch (Exception e) {
		}
		return source;
	}

	/**
	 * 生成SQL
	 *
	 * @param keyword
	 * @param value
	 * @return
	 */
	public static String buildSqlInClause(String keyword, String value) {
		StringBuilder sb = new StringBuilder();
		sb.append(" (").append(keyword).append(" is not null and (");
		sb.append(keyword + "='" + value + "'");
		sb.append(" or ").append(keyword + " like '%," + value + ",%'");
		sb.append(" or ").append(keyword + " like '%," + value + "'");
		sb.append(" or ").append(keyword + " like '" + value + ",%'");
		sb.append(")) ");
		return sb.toString();
	}

	/**
	 * 生成SQL
	 *
	 * @param keyword
	 * @param value
	 * @return
	 */
	public static String buildSqlNotInClause(String keyword, String value) {
		return " not " + buildSqlInClause(keyword, value);
	}

	/**
	 * 第一个参数为以逗号分隔各个元素的字符串，第二个参数为不含逗号的字符串， 返回第一个参数字符串是否包含与第二个参数字符串相等的元素
	 * 
	 * @param ids example:"x,y,z"
	 * @param id example:"x"
	 * @return
	 */
	public static boolean contains(String ids, String id) {
		if (ids != null) {
			String[] idArray = ids.split(",");
			for (String idStr : idArray) {
				if (id.equals(idStr)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 将Long列表转换成逗号分隔的字符
	 *
	 * @param longs
	 * @return
	 */
	public static String convertLongListToCommaDelimitedString(List<Long> longs) {
		// 如果List中分别放着1、2、3，则toString后为[1, 2,3]，
		// 去掉[,空格,]就变成了1,2,3。这里\\s表示空格，用了正则表达式，[]表示其中任意一个
		return longs.toString().replaceAll("[\\s\\]\\[]", "");

	}

	/**
	 * 将中文数字转换为0-10
	 * 
	 * @Description
	 * @param num
	 * @return
	 */
	public static int convertChineseNum2Numeral(String num, int defValue) {
		if ("零".equals(num)) {
			return 0;
		}

		if ("一".equals(num)) {
			return 1;
		}

		if ("二".equals(num)) {
			return 2;
		}

		if ("三".equals(num)) {
			return 3;
		}

		if ("四".equals(num)) {
			return 4;
		}

		if ("五".equals(num)) {
			return 5;
		}

		if ("六".equals(num)) {
			return 6;
		}

		if ("七".equals(num)) {
			return 7;
		}

		if ("八".equals(num)) {
			return 8;
		}

		if ("九".equals(num)) {
			return 9;
		}

		if ("十".equals(num)) {
			return 10;
		}

		return defValue;
	}

	/**
	 * 将中文数字转换为0-10
	 * 
	 * @Description
	 * @param num
	 * @return
	 */
	public static int convertChineseNum2Numeral(String num) {
		return convertChineseNum2Numeral(num, -1);
	}

	/**
	 * 将0-10的数字转换为中文数字
	 * 
	 * @Description
	 * @param num
	 * @return
	 */
	public static String convertNumeral2ChineseNum(int num) {
		if (num < 0) {
			return "";
		}

		String result = "";
		switch (num) {
			case 0:
			result = "零";
				break;

			case 1:
			result = "一";
				break;

			case 2:
			result = "二";
				break;

			case 3:
			result = "三";
				break;

			case 4:
			result = "四";
				break;

			case 5:
			result = "五";
				break;

			case 6:
			result = "六";
				break;

			case 7:
			result = "七";
				break;

			case 8:
			result = "八";
				break;

			case 9:
			result = "九";
				break;

			case 10:
			result = "十";
				break;

			default:
				break;
		}

		return result;
	}

	/**
	 * 将0-10的数字转换为中文数字
	 *
	 * @param num
	 * @return
	 */
	public static String convertNumeral2ChineseNum(String num) {
		if (null == num || num.length() == 0) {
			return "";
		}

		String result = "";
		if (num.equals("0")) {
			result = "零";
		} else if (num.equals("1")) {
			result = "一";
		} else if (num.equals("2")) {
			result = "二";
		} else if (num.equals("3")) {
			result = "三";
		} else if (num.equals("4")) {
			result = "四";
		} else if (num.equals("5")) {
			result = "五";
		} else if (num.equals("6")) {
			result = "六";
		} else if (num.equals("7")) {
			result = "七";
		} else if (num.equals("8")) {
			result = "八";
		} else if (num.equals("9")) {
			result = "九";
		} else if (num.equals("10")) {
			result = "十";
		}

		return result;
	}

	/**
	 * 格式化
	 *
	 * @param str
	 * @param len
	 * @param key
	 * @return
	 */
	public static String formatCode(String str, int len, String key) {
		String formatStr = "";
		if (!StringUtils.isNotNullString(str) || len == 0) {
			return formatStr;
		}
		if (str.length() > len) {
			return str;
		}
		int strLen = str.length();
		formatStr = str;
		for (int i = strLen; i < len; i++) {
			formatStr = key + formatStr;
		}

		return formatStr;
	}

	/**
	 * Replaces all instances of oldString with newString in line.
	 * 
	 * @param line the String to search to perform replacements on
	 * @param oldString the String that should be replaced by newString
	 * @param newString the String that will replace all instances of oldString
	 * @return a String will all instances of oldString replaced by newString
	 */
	public static final String replace(String line, String oldString, String newString) {
		if (line == null) {
			return null;
		}
		int i = 0;
		if ((i = line.indexOf(oldString, i)) >= 0) {
			char[] line2 = line.toCharArray();
			char[] newString2 = newString.toCharArray();
			int oLength = oldString.length();
			StringBuffer buf = new StringBuffer(line2.length);
			buf.append(line2, 0, i).append(newString2);
			i += oLength;
			int j = i;
			while ((i = line.indexOf(oldString, i)) > 0) {
				buf.append(line2, j, i - j).append(newString2);
				i += oLength;
				j = i;
			}
			buf.append(line2, j, line2.length - j);
			return buf.toString();
		}
		return line;
	}

	/**
	 * 
	 *
	 * @param s__Text
	 * @param s__Src
	 * @param s__Dest
	 * @return
	 */
	public static String replaceSubstring(String s__Text, String s__Src, String s__Dest) {
		try {
			if (s__Text == null || s__Src == null || s__Dest == null) return null;

			int i = 0;
			int i_SrcLength = s__Src.length();
			int i_DestLength = s__Dest.length();
			do {
				int j = s__Text.indexOf(s__Src, i);
				if (-1 == j) break;
				s__Text = s__Text.substring(0, j).concat(s__Dest).concat(s__Text.substring(j + i_SrcLength));
				i = j + i_DestLength;
			} while (true);
		} catch (Exception e) {

		}
		return s__Text;
	}

	/**
	 * Replaces all instances of oldString with newString in line with the added
	 * feature that matches of newString in oldString ignore case.
	 * @param line the String to search to perform replacements on
	 * @param oldString the String that should be replaced by newString
	 * @param newString the String that will replace all instances of oldString
	 * @return a String will all instances of oldString replaced by newString
	 */
	public static final String replaceIgnoreCase(String line, String oldString, String newString) {
		if (line == null) {
			return null;
		}
		String lcLine = line.toLowerCase();
		String lcOldString = oldString.toLowerCase();
		int i = 0;
		if ((i = lcLine.indexOf(lcOldString, i)) >= 0) {
			char[] line2 = line.toCharArray();
			char[] newString2 = newString.toCharArray();
			int oLength = oldString.length();
			StringBuffer buf = new StringBuffer(line2.length);
			buf.append(line2, 0, i).append(newString2);
			i += oLength;
			int j = i;
			while ((i = lcLine.indexOf(lcOldString, i)) > 0) {
				buf.append(line2, j, i - j).append(newString2);
				i += oLength;
				j = i;
			}
			buf.append(line2, j, line2.length - j);
			return buf.toString();
		}
		return line;
	}

	/**
	 * Replaces all instances of oldString with newString in line. The count
	 * Integer is updated with number of replaces.
	 * @param line the String to search to perform replacements on
	 * @param oldString the String that should be replaced by newString
	 * @param newString the String that will replace all instances of oldString
	 * @return a String will all instances of oldString replaced by newString
	 */
	public static final String replace(String line, String oldString, String newString, int[] count) {
		if (line == null) {
			return null;
		}
		int i = 0;
		if ((i = line.indexOf(oldString, i)) >= 0) {
			int counter = 0;
			counter++;
			char[] line2 = line.toCharArray();
			char[] newString2 = newString.toCharArray();
			int oLength = oldString.length();
			StringBuffer buf = new StringBuffer(line2.length);
			buf.append(line2, 0, i).append(newString2);
			i += oLength;
			int j = i;
			while ((i = line.indexOf(oldString, i)) > 0) {
				counter++;
				buf.append(line2, j, i - j).append(newString2);
				i += oLength;
				j = i;
			}
			buf.append(line2, j, line2.length - j);
			count[0] = counter;
			return buf.toString();
		}
		return line;
	}

	/**
	 * This method takes a string which may contain HTML tags (ie, &lt;b&gt;,
	 * &lt;table&gt;, etc) and converts the '&lt'' and '&gt;' characters to
	 * their HTML escape sequences.
	 * @param input the text to be converted.
	 * @return the input string with the characters '&lt;' and '&gt;' replaced
	 *         with their HTML escape sequences.
	 */
	public static final String escapeHTMLTags(String input) {
		// Check if the string is null or zero length -- if so, return
		// what was sent in.
		if (input == null || input.length() == 0) {
			return input;
		}
		// Use a StringBuffer in lieu of String concatenation -- it is
		// much more efficient this way.
		StringBuffer buf = new StringBuffer(input.length());
		char ch = ' ';
		for (int i = 0; i < input.length(); i++) {
			ch = input.charAt(i);
			if (ch == '<') {
				buf.append("&lt;");
			} else if (ch == '>') {
				buf.append("&gt;");
			} else {
				buf.append(ch);
			}
		}
		return buf.toString();
	}

	/**
	 * Used by the hash method.
	 */
	private static MessageDigest digest = null;

	/**
	 * Hashes a String using the Md5 algorithm and returns the result as a
	 * String of hexadecimal numbers. This method is synchronized to avoid
	 * excessive MessageDigest object creation. If calling this method becomes a
	 * bottleneck in your code, you may wish to maintain a pool of MessageDigest
	 * objects instead of using this method.
	 * <p>
	 * A hash is a one-way function -- that is, given an input, an output is
	 * easily computed. However, given the output, the input is almost
	 * impossible to compute. This is useful for passwords since we can store
	 * the hash and a hacker will then have a very hard time determining the
	 * original password.
	 * <p>
	 * In Jive, every time a user logs in, we simply take their plain text
	 * password, compute the hash, and compare the generated hash to the stored
	 * hash. Since it is almost impossible that two passwords will generate the
	 * same hash, we know if the user gave us the correct password or not. The
	 * only negative to this system is that password recovery is basically
	 * impossible. Therefore, a reset password method is used instead.
	 * @param data the String to compute the hash of.
	 * @return a hashed version of the passed-in String
	 */
	public synchronized static final String hash(String data) {
		if (digest == null) {
			try {
				digest = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException nsae) {
				System.err.println("Failed to load the MD5 MessageDigest. " + "Jive will be unable to function normally.");
				nsae.printStackTrace();
			}
		}
		if (digest == null) {
			return null;
		}
		// Now, compute hash.
		if (data != null) {
			digest.update(data.getBytes());
		}

		return toHex(digest.digest());
	}

	/**
	 * Turns an array of bytes into a String representing each byte as an
	 * unsigned hex number.
	 * <p>
	 * Method by Santeri Paavolainen, Helsinki Finland 1996<br>
	 * (c) Santeri Paavolainen, Helsinki Finland 1996<br>
	 * Distributed under LGPL.
	 * @param hash an rray of bytes to convert to a hex-string
	 * @return generated hex string
	 */
	public static final String toHex(byte hash[]) {
		StringBuffer buf = new StringBuffer(hash.length * 2);
		int i;

		for (i = 0; i < hash.length; i++) {
			if (((int) hash[i] & 0xff) < 0x10) {
				buf.append("0");
			}
			buf.append(Long.toString((int) hash[i] & 0xff, 16));
		}
		return buf.toString();
	}

	/**
	 * Converts a line of text into an array of lower case words. Words are
	 * delimited by the following characters: , .\r\n:/\+
	 * <p>
	 * In the future, this method should be changed to use a
	 * BreakIterator.wordInstance(). That class offers much more fexibility.
	 * @param text a String of text to convert into an array of words
	 * @return text broken up into an array of words.
	 */
	public static final String[] toLowerCaseWordArray(String text) {
		if (text == null || text.length() == 0) {
			return new String[0];
		}
		StringTokenizer tokens = new StringTokenizer(text, " ,\r\n.:/\\+");
		String[] words = new String[tokens.countTokens()];
		for (int i = 0; i < words.length; i++) {
			words[i] = tokens.nextToken().toLowerCase();
		}
		return words;
	}

	/**
	 * A list of some of the most common words. For searching and indexing, we
	 * often want to filter out these words since they just confuse searches.
	 * The list was not created scientifically so may be incomplete :)
	 */
	private static final String[] commonWords = new String[] {
			"a", "and", "as", "at", "be", "do", "i", "if", "in", "is", "it", "so", "the", "to"
	};
	private static Map<String, String> commonWordsMap = null;

	/**
	 * Returns a new String array with some of the most common English words
	 * removed. The specific words removed are: a, and, as, at, be, do, i, if,
	 * in, is, it, so, the, to
	 */
	public static final String[] removeCommonWords(String[] words) {
		// See if common words map has been initialized. We don't statically
		// initialize it to save some memory. Even though this a small savings,
		// it adds up with hundreds of classes being loaded.
		synchronized (initLock) {
			if (commonWordsMap == null) {
				commonWordsMap = new HashMap<String, String>();
				for (int i = 0; i < commonWords.length; i++) {
					commonWordsMap.put(commonWords[i], commonWords[i]);
				}
			}
		}
		// Now, add all words that aren't in the common map to results
		ArrayList<String> results = new ArrayList<String>(words.length);
		for (int i = 0; i < words.length; i++) {
			if (!commonWordsMap.containsKey(words[i])) {
				results.add(words[i]);
			}
		}
		return (String[]) results.toArray(new String[results.size()]);
	}

	/**
	 * Pseudo-random number generator object for use with randomString(). The
	 * Random class is not considered to be cryptographically secure, so only
	 * use these random Strings for low to medium security applications.
	 */
	private static Random randGen = null;

	/**
	 * Array of numbers and letters of mixed case. Numbers appear in the list
	 * twice so that there is a more equal chance that a number will be picked.
	 * We can use the array to get a random number or letter by picking a random
	 * array index.
	 */
	private static char[] numbersAndLetters = null;

	/**
	 * Returns a random String of numbers and letters of the specified length.
	 * The method uses the Random class that is built-in to Java which is
	 * suitable for low to medium grade security uses. This means that the
	 * output is only pseudo random, i.e., each number is mathematically
	 * generated so is not truly random.
	 * <p>
	 * For every character in the returned String, there is an equal chance that
	 * it will be a letter or number. If a letter, there is an equal chance that
	 * it will be lower or upper case.
	 * <p>
	 * The specified length must be at least one. If not, the method will return
	 * null.
	 * @param length the desired length of the random String to return.
	 * @return a random String of numbers and letters of the specified length.
	 */
	public static final String randomString(int length) {
		if (length < 1) {
			return null;
		}
		// Init of pseudo random number generator.
		synchronized (initLock) {
			randGen = new Random();
			// Also initialize the numbersAndLetters array
			numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz" + "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
		}
		// Create a char buffer to put random letters and numbers in.
		char[] randBuffer = new char[length];
		for (int i = 0; i < randBuffer.length; i++) {
			randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
		}
		return new String(randBuffer);
	}

	/**
	 * Intelligently chops a String at a word boundary (whitespace) that occurs
	 * at the specified index in the argument or before. However, if there is a
	 * newline character before <code>length</code>, the String will be chopped
	 * there. If no newline or whitespace is found in <code>string</code> up to
	 * the index <code>length</code>, the String will chopped at
	 * <code>length</code>.
	 * <p>
	 * For example, chopAtWord("This is a nice String", 10) will return
	 * "This is a" which is the first word boundary less than or equal to 10
	 * characters into the original String.
	 * @param string the String to chop.
	 * @param length the index in <code>string</code> to start looking for a
	 *            whitespace boundary at.
	 * @return a substring of <code>string</code> whose length is less than or
	 *         equal to <code>length</code>, and that is chopped at whitespace.
	 */
	public static final String chopAtWord(String string, int length) {
		if (string == null) {
			return string;
		}

		char[] charArray = string.toCharArray();
		int sLength = string.length();
		if (length < sLength) {
			sLength = length;
		}

		// First check if there is a newline character before length; if so,
		// chop word there.
		for (int i = 0; i < sLength - 1; i++) {
			// Windows
			if (charArray[i] == '\r' && charArray[i + 1] == '\n') {
				return string.substring(0, i);
			}
			// Unix
			else if (charArray[i] == '\n') {
				return string.substring(0, i);
			}
		}
		// Also check boundary case of Unix newline
		if (charArray[sLength - 1] == '\n') {
			return string.substring(0, sLength - 1);
		}

		// Done checking for newline, now see if the total string is less than
		// the specified chop point.
		if (string.length() < length) {
			return string;
		}

		// No newline, so chop at the first whitespace.
		for (int i = length - 1; i > 0; i--) {
			if (charArray[i] == ' ') {
				return string.substring(0, i).trim();
			}
		}

		// Did not find word boundary so return original String chopped at
		// specified length.
		return string.substring(0, length);
	}

	/**
	 * Highlights words in a string. Words matching ignores case. The actual
	 * higlighting method is specified with the start and end higlight tags.
	 * Those might be beginning and ending HTML bold tags, or anything else.
	 * @param string the String to highlight words in.
	 * @param words an array of words that should be highlighted in the string.
	 * @param startHighlight the tag that should be inserted to start
	 *            highlighting.
	 * @param endHighlight the tag that should be inserted to end highlighting.
	 * @return a new String with the specified words highlighted.
	 */
	public static final String highlightWords(String string, String[] words, String startHighlight, String endHighlight) {
		if (string == null || words == null || startHighlight == null || endHighlight == null) {
			return null;
		}

		// Iterate through each word.
		for (int x = 0; x < words.length; x++) {
			// we want to ignore case.
			String lcString = string.toLowerCase();
			// using a char [] is more efficient
			char[] string2 = string.toCharArray();
			String word = words[x].toLowerCase();

			// perform specialized replace logic
			int i = 0;
			if ((i = lcString.indexOf(word, i)) >= 0) {
				int oLength = word.length();
				StringBuffer buf = new StringBuffer(string2.length);

				// we only want to highlight distinct words and not parts of
				// larger words. The method used below mostly solves this. There
				// are a few cases where it doesn't, but it's close enough.
				boolean startSpace = false;
				char startChar = ' ';
				if (i - 1 > 0) {
					startChar = string2[i - 1];
					if (!Character.isLetter(startChar)) {
						startSpace = true;
					}
				}
				boolean endSpace = false;
				char endChar = ' ';
				if (i + oLength < string2.length) {
					endChar = string2[i + oLength];
					if (!Character.isLetter(endChar)) {
						endSpace = true;
					}
				}
				if ((startSpace && endSpace) || (i == 0 && endSpace)) {
					buf.append(string2, 0, i);
					if (startSpace && startChar == ' ') {
						buf.append(startChar);
					}
					buf.append(startHighlight);
					buf.append(string2, i, oLength).append(endHighlight);
					if (endSpace && endChar == ' ') {
						buf.append(endChar);
					}
				} else {
					buf.append(string2, 0, i);
					buf.append(string2, i, oLength);
				}

				i += oLength;
				int j = i;
				while ((i = lcString.indexOf(word, i)) > 0) {
					startSpace = false;
					startChar = string2[i - 1];
					if (!Character.isLetter(startChar)) {
						startSpace = true;
					}

					endSpace = false;
					if (i + oLength < string2.length) {
						endChar = string2[i + oLength];
						if (!Character.isLetter(endChar)) {
							endSpace = true;
						}
					}
					if ((startSpace && endSpace) || i + oLength == string2.length) {
						buf.append(string2, j, i - j);
						if (startSpace && startChar == ' ') {
							buf.append(startChar);
						}
						buf.append(startHighlight);
						buf.append(string2, i, oLength).append(endHighlight);
						if (endSpace && endChar == ' ') {
							buf.append(endChar);
						}
					} else {
						buf.append(string2, j, i - j);
						buf.append(string2, i, oLength);
					}
					i += oLength;
					j = i;
				}
				buf.append(string2, j, string2.length - j);
				string = buf.toString();
			}
		}
		return string;
	}

	/**
	 * Escapes all necessary characters in the String so that it can be used in
	 * an XML doc.
	 * @param string the string to escape.
	 * @return the string with appropriate characters escaped.
	 */
	public static final String escapeForXML(String string) {
		// Check if the string is null or zero length -- if so, return
		// what was sent in.
		if (string == null || string.length() == 0) {
			return string;
		}
		char[] sArray = string.toCharArray();
		StringBuffer buf = new StringBuffer(sArray.length);
		char ch;
		for (int i = 0; i < sArray.length; i++) {
			ch = sArray[i];
			if (ch == '<') {
				buf.append("&lt;");
			} else if (ch == '>') {
				buf.append("&gt;");
			} else if (ch == '&') {
				buf.append("&amp;");
			} else {
				buf.append(ch);
			}
		}
		return buf.toString();
	}

	/**
	 * 删除字符串空格
	 *
	 * @param string
	 * @return
	 */
	public static final String deleteWhiteSpace(String string) {
		if (string == null || string.length() == 0) {
			return string;
		}
		char[] sArray = string.toCharArray();
		StringBuffer buf = new StringBuffer(sArray.length);
		char ch;
		for (int i = 0; i < sArray.length; i++) {
			ch = sArray[i];
			if ((ch != '\u0020') && (ch != '\u0009')) {
				buf.append(ch);
			}
		}
		return buf.toString();
	}

	/**
	 * 中文编码格式字符
	 *
	 * @param string
	 * @return
	 */
	public static final String getChnString_ms(String string) {
		try {
			byte[] temp_t = string.getBytes("ISO8859-1");
			String temp = new String(temp_t);
			return temp;
		} catch (Exception e) {
			return "null";
		}
	}

	/**
	 * 分隔解析
	 *
	 * @param string
	 * @param chNum
	 * @return
	 */
	public static final String dumpNextLine(String string, int chNum) {
		String chStr = string;
		if (string == null || chNum == 0) {
			return "";
		}
		StringTokenizer sTk = new StringTokenizer(chStr, "<br>");
		String endStr = "";
		String tStr = "";
		while (sTk.hasMoreTokens()) {
			tStr = sTk.nextToken();
			if (tStr.length() < chNum) {
				endStr = endStr + tStr + "<br>";
			}
			if (tStr.length() > chNum) {
				endStr = endStr + tStr.substring(0, chNum) + "<br>";
			}
			int k = 0;
			int tK = 1;
			for (k = 0; k < tStr.length(); k++) {
				if (k % chNum == 0 && k != 0 && k + chNum <= tStr.length()) {
					endStr = endStr + tStr.substring(k, k + chNum) + "<br>";
					tK++;
				}
			}
			if (tStr.length() - tK * chNum > 0) {
				endStr = endStr + tStr.substring(tK * chNum);
			}
		}
		return endStr;
	}

	/**
	 * 
	 *
	 * @param charNum
	 * @param detail
	 * @return
	 */
	public static final String great_summary_3(int charNum, String detail) {
		StringBuffer detail_temp = new StringBuffer();
		int len = 0;
		int pos = 0;
		int pos2 = 0;
		int pos3 = 0;
		int num = 0;
		int j = 0;
		char ch;
		String tagStr = "";
		String tagStr2 = "";
		String tmp = "";
		String tabStr1 = "<table";
		String tabStr2 = "</table";
		String tdStr1 = "<td";
		String tdStr2 = "</td";
		String trStr1 = "<tr";
		String trStr2 = "</tr";
		String thStr1 = "<th";
		String thStr2 = "<th";
		if (detail != null) {
			detail = detail + " ";
			len = detail.length();
			for (int i = 0; i < len; i++) {
				ch = detail.charAt(i);
				while (ch == '<') {
					pos = detail.indexOf('>', i);
					if (pos > i) {
						tagStr = detail.substring(i, pos + 1);
						if (tagStr.trim().indexOf(tabStr1) == -1 && tagStr.trim().indexOf(tabStr2) == -1 && tagStr.trim().indexOf(tdStr1) == -1
								&& tagStr.trim().indexOf(tdStr2) == -1 && tagStr.trim().indexOf(trStr1) == -1 && tagStr.trim().indexOf(trStr2) == -1
								&& tagStr.trim().indexOf(thStr1) == -1 && tagStr.trim().indexOf(thStr2) == -1) {
							detail_temp.append(tagStr);
						}
						if (pos < len - 1) {
							i = pos + 1;
							ch = detail.charAt(i);

							if (j < charNum) {
								pos2 = detail.indexOf("</", i); /*
																 * 锟斤拷锟斤拷锟斤拷锟狡ワ拷锟侥斤拷锟斤拷Tag
																 * , 锟斤�?/FONT>
																 */
								if (pos2 > i) /* 锟斤拷锟斤拷匹锟斤拷慕锟斤拷锟絋ag */
								{
									pos3 = detail.indexOf('>', pos2);
									if (pos3 > pos2) {
										tagStr2 = detail.substring(pos2, pos3 + 1);

									}

									if (pos2 - pos > charNum - j) /*
																	* 锟斤拷锟斤拷剩锟铰碉拷锟街凤拷锟斤拷锟斤
																	* �?
																	*/
									{
										num = pos2 - pos - charNum + j;
										tmp = detail.substring(i, i + num + 1);
										detail_temp.append(tmp);
										if (tagStr2.trim().indexOf(tabStr1) == -1 && tagStr2.trim().indexOf(tabStr2) == -1 && tagStr2.trim().indexOf(tdStr1) == -1
												&& tagStr2.trim().indexOf(tdStr2) == -1 && tagStr2.trim().indexOf(trStr1) == -1 && tagStr2.trim().indexOf(trStr2) == -1
												&& tagStr2.trim().indexOf(thStr1) == -1 && tagStr2.trim().indexOf(thStr2) == -1) {
											detail_temp.append(tagStr2);
										}
										return detail_temp.toString();
									}
								}

							}
						}

					} else
					/* pos <= i */
					{
						break;
					}
				}
				/* end while */

				j++;
				detail_temp.append(ch);
				if (j <= charNum) continue;
				detail_temp.append("\u2026\u2026");
				break;
			}

			return detail_temp.toString();
		} else {
			return "\u65E0";
		}
	}

	public static final boolean eval_IntNumber(String param) { // 校锟斤拷锟角凤拷为锟斤拷锟斤�?
		boolean isIntNum = true;
		char[] nums = new char[10];
		for (int j = 0; j < 9; j++) {
			nums[j] = (String.valueOf(j)).charAt(0);
		}
		for (int i = 0; i < param.length(); i++) {
			for (int k = 0; k < 9; k++) {
				if (param.charAt(i) == nums[k]) break;
				if (k == 8) return false;
			}
		}
		return isIntNum;
	}

	public static final boolean eval_Real(String param) { // 校锟斤拷锟角凤拷为实锟斤�?
		boolean isReal = true;
		if (param.length() <= 0) return false;
		for (int i = 0; i < param.length(); i++) {
			int intHash = param.substring(i, i + 1).hashCode();
			if (intHash < 48 || intHash > 57) {
				isReal = false;
				if (param.substring(i, i + 1).equals(".")) {
					isReal = true;
				}
				if (!isReal) break;
			}

		}
		return isReal;
	}

	public static String floadToint(double input) {
		int dInt = 0;
		int dDec = 0;
		String doubleStr = null;
		Double db = Double.valueOf(input + 0.05);
		dInt = db.intValue();
		dDec = ((Double.valueOf((input + 0.05) * 10)).intValue()) % 10;
		doubleStr = String.valueOf(dInt) + "." + String.valueOf(dDec);
		return doubleStr;
	}

	/**
	 * 是否为Image格式
	 *
	 * @param filename
	 * @return
	 */
	public static boolean isImage(String filename) {
		StringTokenizer token = new StringTokenizer(filename);
		String name = "";
		String end_pref = "";
		int index = 0;
		String[] filetype = {
				"jpg", "gif", "bmp", "tif", "png", "ico"
		};
		boolean is = false;
		while (token.hasMoreTokens()) {
			name = token.nextToken();
		}
		index = name.indexOf(".");
		end_pref = name.substring(index + 1);
		for (int i = 0; i < filetype.length; i++) {
			if (end_pref.equalsIgnoreCase(filetype[i])) {
				is = true;
				break;
			}
		}
		return is;
	}

	/**
	 * 时间
	 *
	 * @return
	 */
	public static String[] getTimes() {
		final String[] TM = new String[48];
		TM[0] = "00:00";
		TM[1] = "00:30";
		TM[2] = "01:00";
		TM[3] = "01:30";
		TM[4] = "02:00";
		TM[5] = "02:30";
		TM[6] = "03:00";
		TM[7] = "03:30";
		TM[8] = "04:00";
		TM[9] = "04:30";
		TM[10] = "05:00";
		TM[11] = "05:30";
		TM[12] = "06:00";
		TM[13] = "06:30";
		TM[14] = "07:00";
		TM[15] = "07:30";
		TM[16] = "08:00";
		TM[17] = "08:30";
		TM[18] = "09:00";
		TM[19] = "09:30";
		TM[20] = "10:00";
		TM[21] = "10:30";
		TM[22] = "11:00";
		TM[23] = "11:30";
		TM[24] = "12:00";
		TM[25] = "12:30";
		TM[26] = "13:00";
		TM[27] = "13:30";
		TM[28] = "14:00";
		TM[29] = "14:30";
		TM[30] = "15:00";
		TM[31] = "15:30";
		TM[32] = "16:00";
		TM[33] = "16:30";
		TM[34] = "17:00";
		TM[35] = "17:30";
		TM[36] = "18:00";
		TM[37] = "18:30";
		TM[38] = "19:00";
		TM[39] = "19:30";
		TM[40] = "20:00";
		TM[41] = "20:30";
		TM[42] = "21:00";
		TM[43] = "21:30";
		TM[44] = "22:00";
		TM[45] = "22:30";
		TM[46] = "23:00";
		TM[47] = "23:30";
		return TM;
	}

	/**
	 * convert Null
	 *
	 * @param input
	 * @return
	 */
	public static String convertNull(String input) {
		String NULL_TAG = " ";
		String result = StringUtils.replace(input, "\r\n", NULL_TAG);
		result = StringUtils.replace(result, "	", NULL_TAG);
		return StringUtils.replace(result, "\n", NULL_TAG);
	}

	/**
	 * convert Line
	 *
	 * @param input
	 * @return
	 */
	public static String convertLine(String input) {
		String BR_TAG = "<br>";
		String result = StringUtils.replace(input, "\r\n", BR_TAG);
		return StringUtils.replace(result, "\n", BR_TAG);
	}

	public static String dumpNew(String input, int lineNum) {
		String input1 = convertLine(input);
		// int[] poses = new int[input1.length()];
		int pos1 = 0;
		int pos2 = 0;
		int j = 0;
		StringBuffer strTemp = new StringBuffer();
		for (int i = 0; i < input1.length(); i++) {
			pos1 = input1.indexOf("<br>", i);
			System.out.print("pos1=" + pos1);
			if (pos1 - i > lineNum) {
				strTemp.append(input1.substring(i, i + lineNum) + "<br>");
				i = i + lineNum - 1;
			} else if (pos1 >= 0 && pos1 - i <= lineNum) {
				strTemp.append(input1.substring(i, pos1 + 4));
				i = pos1 + 3;
			} else if (pos1 == -1 && input1.length() - i > lineNum) {
				strTemp.append(input1.substring(i, i + lineNum) + "<br>");
				i = i + lineNum - 1;
			} else if (pos1 == -1 && input1.length() - i <= lineNum) {
				strTemp.append(input1.substring(i));
				break;
			}
		}
		return strTemp.toString();

	}

	/**
	 * 解析IP
	 *
	 * @param IP
	 * @return
	 */
	public int[] parseIP(String IP) {
		int invalida[] = {
				-1, -1, -1, -1
		};
		int ia[] = {
				-1, -1, -1, -1
		};
		if (IP == null) return invalida;
		int len = IP.length();
		int i = 0;
		int b = 0;
		int dot = 0;
		while (i < len) {
			char c = IP.charAt(i++);
			if (c >= '0' && c <= '9')
				b = (b * 10 + c) - 48;
			else if (c == '.') {
				ia[dot] = b;
				if (++dot >= 4) return invalida;
				b = 0;
			} else {
				return invalida;
			}
		}
		if (dot == 3) {
			ia[dot] = b;
			return ia;
		} else {
			return invalida;
		}
	}

	/**
	 * 
	 *
	 * @param aClass
	 * @return
	 */
	public static String packageOf(Class aClass) {
		if (aClass == null) {
			throw new IllegalArgumentException("StringUtils: Argument \"aClass\" cannot be null.");
		}
		String result = "";
		int index = aClass.getName().lastIndexOf(".");
		if (index >= 0) {
			result = aClass.getName().substring(0, index);
		}
		return result;
	}

	public static String nameOf(Class aClass) {
		if (aClass == null) {
			throw new IllegalArgumentException("StringUtils: Argument \"aClass\" cannot be null.");
		}
		String className = aClass.getName();
		int index = className.lastIndexOf(".");
		if (index >= 0) {
			className = className.substring(index + 1);
		}
		return className;
	}

	/**
	 * 解码十六进制
	 *
	 * @param hex
	 * @return
	 */
	public static final byte[] decodeHex(String hex) {
		char[] chars = hex.toCharArray();
		// byte[] bytes = new byte[chars.length/2];
		byte[] bytes = new byte[hex.length() / 2];
		System.out.println("hex.length()/2=" + (hex.length() / 2));
		int byteCount = 0;
		for (int i = 0; i < chars.length; i += 2) {
			byte newByte = 0x00;
			newByte |= hexCharToByte(chars[i]);
			newByte <<= 4;
			newByte |= hexCharToByte(chars[i + 1]);
			bytes[byteCount] = newByte;
			byteCount++;
		}
		return bytes;
	}

	/**
	 * 十六进制字符转换为Byte
	 *
	 * @param ch
	 * @return
	 */
	private static final byte hexCharToByte(char ch) {
		switch (ch) {
			case '0':
				return 0x00;
			case '1':
				return 0x01;
			case '2':
				return 0x02;
			case '3':
				return 0x03;
			case '4':
				return 0x04;
			case '5':
				return 0x05;
			case '6':
				return 0x06;
			case '7':
				return 0x07;
			case '8':
				return 0x08;
			case '9':
				return 0x09;
			case 'a':
				return 0x0A;
			case 'b':
				return 0x0B;
			case 'c':
				return 0x0C;
			case 'd':
				return 0x0D;
			case 'e':
				return 0x0E;
			case 'f':
				return 0x0F;
			case 'A':
				return 0x0A;
			case 'B':
				return 0x0B;
			case 'C':
				return 0x0C;
			case 'D':
				return 0x0D;
			case 'E':
				return 0x0E;
			case 'F':
				return 0x0F;

		}
		return 0x00;
	}

	/**
	 * 
	 *
	 * @param in
	 * @return
	 */
	public static String toDB(String in) {
		if (in == null) return null;
		String out = "";
		int i = 0;
		int len = in.length();
		try {
			while (i < len) {
				char c;
				switch (c = in.charAt(i)) {
					case 34: // '"'
						out = out.concat("&quot;");
						break;

					case 39: // '\''
						out = out.concat("&apos;");
						break;

					case 63: // '?'
						out = out.concat("&qst;");
						break;

					case 38: // '&'
						out = out.concat("&amp;");
						break;

					case 60: // '<'
						out = out.concat("&lt;");
						break;

					case 62: // '>'
						out = out.concat("&gt;");
						break;

					case 36: // '$'
						out = out.concat("$$");
						break;

					default:
						if (c >= '~') {
							out = out.concat("&#" + (int) c);
							out = out.concat(";");
							break;
						}
						if (c < ' ') {
							out = out.concat("&#" + (int) c);
							out = out.concat(";");
						} else {
							out = out.concat(Character.toString(c));
						}
						break;
				}
				i++;
			}
		} catch (Exception e) {

		}
		return out;
	}

	public static String fromDB(String in) {
		if (in == null) return null;
		String out = in;
		out = replaceSubstring(out, "&amp;", "&");
		out = replaceSubstring(out, "&apos;", "'");
		out = replaceSubstring(out, "&quot;", "\"");
		out = replaceSubstring(out, "&qst;", "?");
		out = replaceSubstring(out, "&lt;", "<");
		out = replaceSubstring(out, "&gt;", ">");
		out = replaceSubstring(out, "$$", "$");
		int i = 0;
		do {
			i = 0;
			int j = out.indexOf("&#", i);
			int k = out.indexOf(";", j);
			if (-1 != j && -1 != k) {
				String number = out.substring(j + 2, k);
				int i_number = (Integer.valueOf(number)).intValue();
				char c = (char) i_number;
				String dest = Character.toString(c);
				out = out.substring(0, j).concat(dest).concat(out.substring(k + 1));
			} else {
				return out;
			}
		} while (true);
	}

	/*
	 * Get XML String of utf-8
	 * @return XML-Formed string
	 */
	public static String getUTF8XMLString(String xml) {
		// A StringBuffer Object
		StringBuffer sb = new StringBuffer();
		sb.append(xml);
		String xmString = "";
		String xmlUTF8 = "";
		try {
			xmString = new String(sb.toString().getBytes("UTF-8"));
			xmlUTF8 = URLEncoder.encode(xmString, "UTF-8");
			// System.out.println("utf-8 编码：" + xmlUTF8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return xmlUTF8;
	}

	/**
	 * to GBK
	 *
	 * @param s
	 * @return
	 */
	public static String toGBK(String s) {
		if (s == null || s.length() == 0) return s;

		byte[] b;

		try {
			b = s.getBytes("ISO8859_1");
			for (int i = 0; i < b.length; i++)
				if (b[i] + 0 < 0) return new String(b, "GBK");
			b = s.getBytes("GBK");
			for (int i = 0; i < b.length; i++)
				if (b[i] + 0 < 0) return new String(b, "GBK");
		} catch (Exception e) {
		}

		return s;
	}

	public static void newLine(StringBuffer buffer, int indent) {
		buffer.append(StringUtils.lineSeparator);
		indent(buffer, indent);
	}

	public static void indent(StringBuffer buffer, int indent) {
		for (int i = 0; i < indent; i++)
			buffer.append(' ');
	}

	/**
	 * 将null转化为“”.
	 * 
	 * @param str 指定的字符串
	 * @return 字符串的String类型
	 */
	public static String parseEmpty(String str) {
		if (str == null || "null".equals(str.trim())) {
			str = "";
		}
		return str.trim();
	}

	/**
	 * 字符串不为"null"
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNotNullString(String str) {
		return !"".equals(str) && !str.equalsIgnoreCase("null");
	}

	/**
	 * 字符串经过trim处理之后是否为空
	 *
	 * @param str
	 * @return
	 */
	public static boolean isNull(String str) {
		boolean b = false;
		if (str == null || str.trim().length() == 0) b = true;

		return b;
	}

	/**
	 * 字符串是否为空
	 *
	 * @param str
	 * @return
	 */
	public static boolean isNullNotTrim(String str) {
		boolean b = false;
		if (str == null) b = true;

		return b;
	}

	/**
	 * 字符串是否为null
	 *
	 * @param str
	 * @return
	 */
	public static boolean isNULL(String str) {
		boolean b = false;
		if (str == null) b = true;

		return b;
	}

	/**
	 * 
	 *
	 * @param str
	 * @param bValidNullString
	 * @return
	 */
	public static boolean isNull(String str, boolean bValidNullString) {
		boolean b = false;
		if (str == null || str.trim().length() == 0) b = true;
		if (!b && bValidNullString) {
			if (str != null && str.equalsIgnoreCase("null")) b = true;
		}
		return b;
	}

	/**
	 * 比较2个字符是否相等
	 *
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static boolean isEquals(String str1, String str2) {
		if (isNull(str1) && isNull(str2)) {
			return true;
		}

		if (isNull(str1) && !isNull(str2)) {
			return false;
		}

		if (!isNull(str1) && isNull(str2)) {
			return false;
		}

		if (str1.equals(str2)) {
			return true;
		}

		return false;
	}

	/**
	 * 比较2个字符是否相等
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean equals(CharSequence a, CharSequence b) {
		if (a == b) return true;

		int length;
		if (a != null && b != null && (length = a.length()) == b.length()) {
			if (a instanceof String && b instanceof String) {
				return a.equals(b);
			} else {
				for (int i = 0; i < length; i++) {
					if (a.charAt(i) != b.charAt(i)) return false;
				}
				return true;
			}
		}

		return false;
	}

	/**
	 * 比较2个字符是否相等
	 *
	 * @param aStr
	 * @param bStr
	 * @param ignoreCase 忽略大小写
	 * @return
	 */
	public static boolean equalsStr(String aStr, String bStr, boolean ignoreCase) {
		if (aStr == bStr) return true;

		try {
			if (ignoreCase) {
				return aStr.equalsIgnoreCase(bStr);
			} else {
				return aStr.equals(bStr);
			}
		} catch (Exception e) {
		}

		return false;
	}

	/**
	 * 是否为URL
	 *
	 * @param str
	 * @return
	 */
	public static boolean isUrl(String str) {
		if (isNull(str)) return false;
		return str.matches("^http://(\\w+(-\\w+)*)(\\.(\\w+(-\\w+)*))*(\\?\\S*)?$");
	}

	/**
	 * to Boolean
	 *
	 * @param s
	 * @return
	 */
	public static boolean str2Boolean(String s) {
		return str2Boolean(s, false);
	}

	/**
	 * to Boolean
	 *
	 * @param s
	 * @param defaultV 默认值
	 * @return
	 */
	public static boolean str2Boolean(String s, boolean defaultV) {
		if (StringUtils.isNull(s)) return defaultV;
		if (s != null && s.equalsIgnoreCase("true")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * to Int
	 *
	 * @param s
	 * @return
	 */
	public static int str2Int(String s) {
		return str2Int(s, 0);
	}

	/**
	 * to Int
	 *
	 * @param s
	 * @param defaultV 默认值
	 * @return
	 */
	public static int str2Int(String s, int defaultV) {
		if (s != null && !s.equals("")) {
			int num = defaultV;
			try {
				num = Integer.parseInt(s);
			} catch (Exception ignored) {
			}
			return num;
		} else {
			return defaultV;
		}
	}

	/**
	 * to Short
	 *
	 * @param s
	 * @param defaultV 默认值
	 * @return
	 */
	public static short str2Short(String s) {
		return str2Short(s, (short) 0);
	}

	/**
	 * to Short
	 *
	 * @param s
	 * @param defaultV 默认值
	 * @return
	 */
	public static short str2Short(String s, short defaultV) {
		if (s != null && !s.equals("")) {
			short num = defaultV;
			try {
				num = Short.parseShort(s);
			} catch (Exception ignored) {
			}
			return num;
		} else {
			return defaultV;
		}
	}

	/**
	 * to Long
	 *
	 * @param s
	 * @return
	 */
	public static long str2Long(String s) {
		return str2Long(s, 0);
	}

	/**
	 * to Long
	 *
	 * @param s
	 * @param defaultV 默认值
	 * @return
	 */
	public static long str2Long(String s, long defaultV) {
		if (s != null && !s.equals("")) {
			long num = defaultV;
			try {
				num = Long.parseLong(s);
			} catch (Exception ignored) {
			}
			return num;
		} else {
			return defaultV;
		}
	}

	/**
	 * to Double
	 *
	 * @param s
	 * @return
	 */
	public static double str2Double(String s) {
		return str2Double(s, 0);
	}

	/**
	 * to Double
	 *
	 * @param s
	 * @param defaultV
	 * @return
	 */
	public static double str2Double(String s, double defaultV) {
		if (s != null && !s.equals("")) {
			double num = defaultV;
			try {
				num = Double.parseDouble(s);
			} catch (Exception ignored) {
			}
			return num;
		} else {
			return defaultV;
		}
	}

	/**
	 * to Float
	 *
	 * @param s
	 * @return
	 */
	public static float str2Float(String s) {
		return str2Float(s, 0f);
	}

	/**
	 * to Float
	 *
	 * @param s
	 * @param defaultV
	 * @return
	 */
	public static float str2Float(String s, float defaultV) {
		if (s != null && !s.equals("")) {
			float num = defaultV;
			try {
				num = Float.parseFloat(s);
			} catch (Exception ignored) {
			}
			return num;
		} else {
			return defaultV;
		}
	}

	public static String valueOf(Long lon, String defValue) {
		if (lon == null) {
			return defValue;
		}

		String result = defValue;
		try {
			result = String.valueOf(lon);
		} catch (Exception e) {
			result = defValue;
		}

		return result;
	}

	public static String valueOf(Integer in, String defValue) {
		if (in == null) {
			return defValue;
		}

		String result = defValue;
		try {
			result = String.valueOf(in);
		} catch (Exception e) {
			result = defValue;
		}

		return result;
	}

	/**
	 * 获取文件扩展名
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getFileExt(String fileName) {
		if (fileName == null) {
			return null;
		}
		fileName = fileName.trim();
		if (fileName.length() == 0) {
			return null;
		}
		int index = fileName.lastIndexOf(".");

		if (index < 0 || (index + 2) > fileName.length()) {
			return null;
		}

		return fileName.substring(index + 1);
	}

	/**
	 * 
	 *
	 * @param url
	 * @return
	 */
	public static boolean isBlank(String url) {
		url = url == null ? "" : url.trim();
		if (url.length() > 0) {
			return false;
		}
		return true;
	}

	/**
	 * get a domain for a url
	 * 
	 * @param url
	 * @return
	 */
	public static String getDomain(String url) {
		if (StringUtils.isBlank(url)) return null;
		String domain;
		int pos = url.indexOf("//");
		domain = url.substring(pos + 2);
		int endpos = domain.indexOf("/");

		if (url.indexOf("http") > -1) {
			domain = "http://" + domain.substring(0, endpos);
		} else {
			domain = "https://" + domain.substring(0, endpos);
		}
		Log.i("domain: ", "domain: " + domain);
		return domain;
	}

	/**
	 * 截取某个字符之前的字符串
	 * 
	 * @param srcString
	 * @param c
	 * @return
	 */
	public static String getString(String srcString, char c) {

		if (TextUtils.isEmpty(srcString)) {
			return null;
		}
		return srcString.substring(0, srcString.lastIndexOf(c));
	}


	/**
	 * 获取一段字符串的字符个数（包含中英文，一个中文算2个字符）
	 * 
	 * @param content
	 * @return
	 */
	public static int getCharacterNum(final String content) {
		if (null == content || "".equals(content)) {
			return 0;
		} else {
			return (content.length() + getChineseNum(content));
		}
	}

	/**
	 * 返回字符串里中文字或者全角字符的个数
	 * @param s
	 * @return
	 */
	public static int getChineseNum(String s) {

		int num = 0;
		char[] myChar = s.toCharArray();
		for (int i = 0; i < myChar.length; i++) {
			if ((char) (byte) myChar[i] != myChar[i]) {
				num++;
			}
		}
		return num;
	}

	/**
	 * 根据分隔符取得单个字符串里面的各个子项
	 * @param s
	 * @param c
	 * @return
	 */
	public static String[] getStringArrayByString(String s, String c) {
		String[] resultString = null;
		if (!StringUtils.isNull(s)) {
			resultString = s.split(c);
			if (resultString == null) {
				return null;
			}
		}
		return resultString;
	}

	/**
	 * 
	 *
	 * @param c
	 * @return
	 */
	public static boolean IsChinese(char c) {
		return (int) c >= 0x4E00 && (int) c <= 0x9FA5;
	}

	/**
	 * 判断字符串是否包含中文
	 * @Description
	 * @param str
	 * @return
	 */
	public static boolean hasChinese(String str) {
		if (str == null || str.length() == 0) {
			return false;
		}

		char[] c = str.toCharArray();
		for (int i = 0; i < c.length; i++) {
			boolean f = isChinese2(c[i]);
			if (f) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 判断字符是否是中文
	 * @Description
	 * @param c
	 * @return
	 */
	public static boolean isChinese2(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);

		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}

		return false;
	}

	/**
	 * 字符串是否是数字
	 * @Description
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		if (null == str || str.length() == 0) {
			return false;
		}

		try {
			Pattern pattern = Pattern.compile("[0-9]*");
			Matcher isNum = pattern.matcher(str);
			if (!isNum.matches()) {
				return false;
			}
		} catch (Exception e) {
		}

		return true;
	}

	/**
	 * 计算中文字符个数
	 * @Description
	 * @param str
	 * @return
	 */
	public static int computCharacterChar(String str) {
		if (StringUtils.isNull(str)) {
			return 0;
		}

		int chineseNum = 0;
		String s = "[^\\x00-\\xff]";
		Pattern pattern = Pattern.compile(s);
		Matcher ma = pattern.matcher(str);

		while (ma.find()) { // 中文字符个数
			chineseNum++;
		}

		return chineseNum;
	}

	/**
	 * 根据英文计算字符串长度
	 * @Description 一个中文长度为2
	 * @param str
	 * @return
	 */
	public static int lengthByEnglish(String str) {
		if (StringUtils.isNull(str)) {
			return 0;
		}

		int len = str.length();
		String s = "[^\\x00-\\xff]";
		Pattern pattern = Pattern.compile(s);
		Matcher ma = pattern.matcher(str);

		while (ma.find()) { // 为中文长度再加1
			len++;
		}

		return len;
	}

	/**
	 * 计算String的长度，一个中文字符为1，2个非中文字符长度为1
	 * @param str
	 * @return
	 */
	public static int count(String str) {
		if (StringUtils.isNull(str)) return 0;

		int singelC = 0;
		int doubleC = 0;
		String s = "[^\\x00-\\xff]";
		Pattern pattern = Pattern.compile(s);
		Matcher ma = pattern.matcher(str);

		while (ma.find()) { // 计算中文长度
			doubleC++;
		}
		singelC = str.length() - doubleC; // 非中文字符长度
		if (singelC % 2 != 0) {
			doubleC += (singelC + 1) / 2;
		} else {
			doubleC += singelC / 2;
		}

		return doubleC;
	}

	/**
	 * 计算String的长度，一个中文字符为2，1个非中文字符长度为1
	 * @param str
	 * @return
	 */
	public static int characterCount(String str) {
		if (StringUtils.isNull(str)) return 0;

		int chineseNum = 0;
		String s = "[^\\x00-\\xff]";
		Pattern pattern = Pattern.compile(s);
		Matcher ma = pattern.matcher(str);

		while (ma.find()) { // 计算中文个数
			chineseNum++;
		}

		return chineseNum + str.length();
	}

	public static int getMiniNum(int... num) {
		if (num == null || num.length == 0) {
			return -1;
		}

		// 对指定的 int 型数组按数字升序进行排序
		Arrays.sort(num);

		return num[0];
	}

	public static int getMaxNum(int... num) {
		if (num == null || num.length == 0) {
			return -1;
		}

		// 对指定的 int 型数组按数字升序进行排序
		Arrays.sort(num);

		return num[num.length - 1];
	}

	/**
	 * 最小正数
	 * @param num
	 * @return 若没有正数 return -1
	 */
	public static int getMiniPositiveNum(int... num) {
		if (num == null || num.length == 0) {
			return -1;
		}

		int result = -1;

		Arrays.sort(num);

		for (int i = 0; i < num.length; i++) {
			if (num[i] >= 0) {
				result = num[i];
				break;
			}
		}

		return result;
	}

	public static String getStringByIndex(int index, String... s) {
		if (s == null || s.length == 0) return "";

		if (index < 0 || index >= s.length) return "";

		return s[index];
	}

	public static boolean containsOne(String body, String... s) {
		if (isNull(body)) return false;

		if (s == null || s.length == 0) return false;

		for (int i = 0; i < s.length; i++) {
			if (body.contains(s[i])) return true;
		}

		return false;
	}

	public static boolean containsOne(String body, boolean isIgnoreCase, String... s) {
		if (isNull(body)) return false;

		if (!isIgnoreCase) {
			return containsOne(body, s);
		}

		body = body.toLowerCase();

		if (s == null || s.length == 0) return false;

		for (int i = 0; i < s.length; i++) {
			if (body.contains(s[i].toLowerCase())) return true;
		}

		return false;
	}

	public static short calculateNotifyType(short... types) {
		short result = 0;
		for (short t : types) {
			result |= t;
		}
		return result;
	}

	/**
	 * 按字节截取的字符串
	 * @param content
	 * @param size
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	public static String getSubString(String content, int size, String encoding) throws Exception {
		if (encoding == null || encoding.equals("")) {
			throw new Exception("字符编码不能为空");
		}
		if (content == null) {
			throw new Exception("字符串内容不能为空");
		}
		if (size < 0) {
			throw new Exception("获取的字节数不能小于0");
		}
		if (content.getBytes().length < size) {
			size = content.getBytes().length;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int index = 0; // 字符串的字符位置
		int count = 0; // 目前的字节数
		while (count < size) {
			// 当前的字节数目
			count += content.substring(index, index + 1).getBytes(encoding).length;
			if (count <= size) {
				out.write(content.substring(index, index + 1).getBytes(encoding));
			}
			index++;
		}
		String result = new String(out.toByteArray(), encoding);
		out.close();
		return result;
	}

	/**
	 * 截取一个包含汉字的字符串的前n个字节
	 * @param str
	 * @param len
	 * @return
	 */
	public static String subStringByByte(String str, int len) {
		byte[] b = str.getBytes();
		if (len == 1) {// 当只取1位时
			if (b[0] < 0)
				return new String(b, 0, 2);
			else
				return new String(b, 0, len);
		} else {

			if (b[len - 1] < 0 && b[len - 2] > 0) { // 判断最后一个字节是否为一个汉字的第一个字节

				return new String(b, 0, len - 1);
			}
		}
		return new String(b, 0, len);
	}

	/**
	 * 按字节数截取字符串
	 * @param orignal 原始字符串
	 * @param count 截取字节个数
	 * @return 截取后的字符串
	 */
	public static String substring(String orignal, int count) {
		if (isNull(orignal)) return orignal;

		// 要截取的字节数大于0，且小于原始字符串的字节数
		if (count <= 0 || count >= orignal.getBytes().length) return orignal;

		char c;
		int n = 0;
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < orignal.length() && n < count; i++) {
			c = orignal.charAt(i);

			if (StringUtils.isChinese2(c)) {
				buff.append(c);
				if (buff.toString().getBytes().length > count) {
					buff = buff.deleteCharAt(buff.toString().lastIndexOf(c));
					break;
				} else {
					n += String.valueOf(c).getBytes().length;
				}
			} else {
				n += String.valueOf(c).getBytes().length;
				buff.append(c);
			}
		}

		return buff.toString();
	}

	/**
	 * 会议持续时间
	 * @return
	 */
	public static String getVConfDuration(long startTime, long endTime) {
		long duration = endTime - startTime;
		if (duration <= 0) {
			return "00:00:00";
		}
		long h = duration / 60 / 60 / 1000;
		long moH = duration % (60 * 60 * 1000);
		long m = moH / 60 / 1000;
		long moM = moH % (60 * 1000);
		long s = moM / 1000;
		StringBuffer sb = new StringBuffer();
		if (h < 10) {
			sb.append(0);
		}
		sb.append(h);
		sb.append(":");
		if (m < 10) {
			sb.append(0);
		}
		sb.append(m);
		sb.append(":");
		if (s < 10) {
			sb.append(0);
		}
		sb.append(s);
		return sb.toString();
	}
}
