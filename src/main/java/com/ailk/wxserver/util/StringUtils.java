/*
 * Created on 2005-6-20
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ailk.wxserver.util;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * Utility class to peform common String manipulation algorithms.
 */
public class StringUtils {

	// Constants used by escapeHTMLTags
	private static final char[] QUOTE_ENCODE = "&quot;".toCharArray();

	private static final char[] AMP_ENCODE = "&amp;".toCharArray();

	private static final char[] LT_ENCODE = "&lt;".toCharArray();

	private static final char[] GT_ENCODE = "&gt;".toCharArray();

	/**
	 * Replaces all instances of oldString with newString in line.
	 * 
	 * @param line
	 *            the String to search to perform replacements on
	 * @param oldString
	 *            the String that should be replaced by newString
	 * @param newString
	 *            the String that will replace all instances of oldString
	 * 
	 * @return a String will all instances of oldString replaced by newString
	 */
	public static final String replace(String line, String oldString,
			String newString) {
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
	 * Replaces all instances of oldString with newString in line with the added
	 * feature that matches of newString in oldString ignore case.
	 * 
	 * @param line
	 *            the String to search to perform replacements on
	 * @param oldString
	 *            the String that should be replaced by newString
	 * @param newString
	 *            the String that will replace all instances of oldString
	 * 
	 * @return a String will all instances of oldString replaced by newString
	 */
	public static final String replaceIgnoreCase(String line, String oldString,
			String newString) {
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
	 * Replaces all instances of oldString with newString in line with the added
	 * feature that matches of newString in oldString ignore case. The count
	 * paramater is set to the number of replaces performed.
	 * 
	 * @param line
	 *            the String to search to perform replacements on
	 * @param oldString
	 *            the String that should be replaced by newString
	 * @param newString
	 *            the String that will replace all instances of oldString
	 * @param count
	 *            a value that will be updated with the number of replaces
	 *            performed.
	 * 
	 * @return a String will all instances of oldString replaced by newString
	 */
	public static final String replaceIgnoreCase(String line, String oldString,
			String newString, int[] count) {
		if (line == null) {
			return null;
		}
		String lcLine = line.toLowerCase();
		String lcOldString = oldString.toLowerCase();
		int i = 0;
		if ((i = lcLine.indexOf(lcOldString, i)) >= 0) {
			int counter = 0;
			char[] line2 = line.toCharArray();
			char[] newString2 = newString.toCharArray();
			int oLength = oldString.length();
			StringBuffer buf = new StringBuffer(line2.length);
			buf.append(line2, 0, i).append(newString2);
			i += oLength;
			int j = i;
			while ((i = lcLine.indexOf(lcOldString, i)) > 0) {
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
	 * Replaces all instances of oldString with newString in line. The count
	 * Integer is updated with number of replaces.
	 * 
	 * @param line
	 *            the String to search to perform replacements on
	 * @param oldString
	 *            the String that should be replaced by newString
	 * @param newString
	 *            the String that will replace all instances of oldString
	 * 
	 * @return a String will all instances of oldString replaced by newString
	 */
	public static final String replace(String line, String oldString,
			String newString, int[] count) {
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
	 * 
	 * @param in
	 *            the text to be converted.
	 * @return the input string with the characters '&lt;' and '&gt;' replaced
	 *         with their HTML escape sequences.
	 */
	public static final String escapeHTMLTags(String in) {
		if (in == null) {
			return null;
		}
		char ch;
		int i = 0;
		int last = 0;
		char[] input = in.toCharArray();
		int len = input.length;
		StringBuffer out = new StringBuffer((int) (len * 1.3));
		for (; i < len; i++) {
			ch = input[i];
			if (ch > '>') {
				continue;
			} else if (ch == '<') {
				if (i > last) {
					out.append(input, last, i - last);
				}
				last = i + 1;
				out.append(LT_ENCODE);
			} else if (ch == '>') {
				if (i > last) {
					out.append(input, last, i - last);
				}
				last = i + 1;
				out.append(GT_ENCODE);
			}
		}
		if (last == 0) {
			return in;
		}
		if (i > last) {
			out.append(input, last, i - last);
		}
		return out.toString();
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
	 * 
	 * @param data
	 *            the String to compute the hash of.
	 * @return a hashed version of the passed-in String
	 */
	public synchronized static final String hash(String data) {
		if (digest == null) {
			try {
				digest = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException nsae) {
				System.err.println("Failed to load the MD5 MessageDigest. "
						+ "Jive will be unable to function normally.");
				// TODO Auto-generated catch block
			}
		}
		// Now, compute hash.
		digest.update(data.getBytes());
		return encodeHex(digest.digest());
	}

	/**
	 * Turns an array of bytes into a String representing each byte as an
	 * unsigned hex number.
	 * <p>
	 * Method by Santeri Paavolainen, Helsinki Finland 1996 <br>
	 * (c) Santeri Paavolainen, Helsinki Finland 1996 <br>
	 * Distributed under LGPL.
	 * 
	 * @param bytes
	 *            an array of bytes to convert to a hex-string
	 * @return generated hex string
	 */
	public static final String encodeHex(byte[] bytes) {
		StringBuffer buf = new StringBuffer(bytes.length * 2);
		int i;

		for (i = 0; i < bytes.length; i++) {
			if (((int) bytes[i] & 0xff) < 0x10) {
				buf.append("0");
			}
			buf.append(Long.toString((int) bytes[i] & 0xff, 16));
		}
		return buf.toString();
	}

	/**
	 *  
	 */
	public static final String encodeHex(String str) {
		byte[] bytes = str.getBytes();
		return encodeHex(bytes);
	}

	/**
	 * Turns a hex encoded string into a byte array. It is specifically meant to
	 * "reverse" the toHex(byte[]) method.
	 * 
	 * @param hex
	 *            a hex encoded String to transform into a byte array.
	 * @return a byte array representing the hex String[
	 */
	public static final byte[] decodeHex(String hex) {
		char[] chars = hex.toCharArray();
		byte[] bytes = new byte[chars.length / 2];
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
	 * Returns the the byte value of a hexadecmical char (0-f). It's assumed
	 * that the hexidecimal chars are lower case as appropriate.
	 * 
	 * @param ch
	 *            a hexedicmal character (0-f)
	 * @return the byte value of the character (0x00-0x0F)
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
		}
		return 0x00;
	}

	//*********************************************************************
	//* Base64 - a simple base64 encoder and decoder.
	//*
	//* Copyright (c) 1999, Bob Withers - bwit@pobox.com
	//*
	//* This code may be freely used for any purpose, either personal
	//* or commercial, provided the authors copyright notice remains
	//* intact.
	//*********************************************************************

	/**
	 * Encodes a String as a base64 String.
	 * 
	 * @param data
	 *            a String to encode.
	 * @return a base64 encoded String.
	 */
	public static String encodeBase64(String data) {
		return encodeBase64(data.getBytes());
	}

	/**
	 * Encodes a byte array into a base64 String.
	 * 
	 * @param data
	 *            a byte array to encode.
	 * @return a base64 encode String.
	 */
	public static String encodeBase64(byte[] data) {
		int c;
		int len = data.length;
		StringBuffer ret = new StringBuffer(((len / 3) + 1) * 4);
		for (int i = 0; i < len; ++i) {
			c = (data[i] >> 2) & 0x3f;
			ret.append(cvt.charAt(c));
			c = (data[i] << 4) & 0x3f;
			if (++i < len)
				c |= (data[i] >> 4) & 0x0f;

			ret.append(cvt.charAt(c));
			if (i < len) {
				c = (data[i] << 2) & 0x3f;
				if (++i < len)
					c |= (data[i] >> 6) & 0x03;

				ret.append(cvt.charAt(c));
			} else {
				++i;
				ret.append((char) fillchar);
			}

			if (i < len) {
				c = data[i] & 0x3f;
				ret.append(cvt.charAt(c));
			} else {
				ret.append((char) fillchar);
			}
		}
		return ret.toString();
	}

	/**
	 * Decodes a base64 String.
	 * 
	 * @param data
	 *            a base64 encoded String to decode.
	 * @return the decoded String.
	 */
	public static String decodeBase64(String data) {
		return decodeBase64(data.getBytes());
	}

	/**
	 * Decodes a base64 aray of bytes.
	 * 
	 * @param data
	 *            a base64 encode byte array to decode.
	 * @return the decoded String.
	 */
	public static String decodeBase64(byte[] data) {
		int c, c1;
		int len = data.length;
		StringBuffer ret = new StringBuffer((len * 3) / 4);
		for (int i = 0; i < len; ++i) {
			c = cvt.indexOf(data[i]);
			++i;
			c1 = cvt.indexOf(data[i]);
			c = ((c << 2) | ((c1 >> 4) & 0x3));
			ret.append((char) c);
			if (++i < len) {
				c = data[i];
				if (fillchar == c)
					break;

				c = cvt.indexOf((char) c);
				c1 = ((c1 << 4) & 0xf0) | ((c >> 2) & 0xf);
				ret.append((char) c1);
			}

			if (++i < len) {
				c1 = data[i];
				if (fillchar == c1)
					break;

				c1 = cvt.indexOf((char) c1);
				c = ((c << 6) & 0xc0) | c1;
				ret.append((char) c);
			}
		}
		return ret.toString();
	}

	private static final int fillchar = '=';

	private static final String cvt = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
			+ "abcdefghijklmnopqrstuvwxyz" + "0123456789+/";

	/**
	 * Converts a line of text into an array of lower case words using a
	 * BreakIterator.wordInstance().
	 * <p>
	 * 
	 * This method is under the Jive Open Source Software License and was
	 * written by Mark Imbriaco.
	 * 
	 * @param text
	 *            a String of text to convert into an array of words
	 * @return text broken up into an array of words.
	 */
	public static final String[] toLowerCaseWordArray(String text) {
		if (text == null || text.length() == 0) {
			return new String[0];
		}

		ArrayList wordList = new ArrayList();
		BreakIterator boundary = BreakIterator.getWordInstance();
		boundary.setText(text);
		int start = 0;

		for (int end = boundary.next(); end != BreakIterator.DONE; start = end, end = boundary
				.next()) {
			String tmp = text.substring(start, end).trim();
			// Remove characters that are not needed.
			tmp = replace(tmp, "+", "");
			tmp = replace(tmp, "/", "");
			tmp = replace(tmp, "\\", "");
			tmp = replace(tmp, "#", "");
			tmp = replace(tmp, "*", "");
			tmp = replace(tmp, ")", "");
			tmp = replace(tmp, "(", "");
			tmp = replace(tmp, "&", "");
			if (tmp.length() > 0) {
				wordList.add(tmp);
			}
		}
		return (String[]) wordList.toArray(new String[wordList.size()]);
	}

	/**
	 * Pseudo-random number generator object for use with randomString(). The
	 * Random class is not considered to be cryptographically secure, so only
	 * use these random Strings for low to medium security applications.
	 */
	private static Random randGen = new Random();

	/**
	 * Array of numbers and letters of mixed case. Numbers appear in the list
	 * twice so that there is a more equal chance that a number will be picked.
	 * We can use the array to get a random number or letter by picking a random
	 * array index.
	 */
	private static char[] numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz"
			+ "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();

	/**
	 * Returns a random String of numbers and letters (lower and upper case) of
	 * the specified length. The method uses the Random class that is built-in
	 * to Java which is suitable for low to medium grade security uses. This
	 * means that the output is only pseudo random, i.e., each number is
	 * mathematically generated so is not truly random.
	 * <p>
	 * 
	 * The specified length must be at least one. If not, the method will return
	 * null.
	 * 
	 * @param length
	 *            the desired length of the random String to return.
	 * @return a random String of numbers and letters of the specified length.
	 */
	public static final String randomString(int length) {
		if (length < 1) {
			return null;
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
	 * newline character before <code>length</code>, the String will be
	 * chopped there. If no newline or whitespace is found in
	 * <code>string</code> up to the index <code>length</code>, the String
	 * will chopped at <code>length</code>.
	 * <p>
	 * For example, chopAtWord("This is a nice String", 10) will return "This is
	 * a" which is the first word boundary less than or equal to 10 characters
	 * into the original String.
	 * 
	 * @param string
	 *            the String to chop.
	 * @param length
	 *            the index in <code>string</code> to start looking for a
	 *            whitespace boundary at.
	 * @return a substring of <code>string</code> whose length is less than or
	 *         equal to <code>length</code>, and that is chopped at
	 *         whitespace.
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
				return string.substring(0, i + 1);
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
	 * Escapes all necessary characters in the String so that it can be used in
	 * an XML doc.
	 * 
	 * @param string
	 *            the string to escape.
	 * @return the string with appropriate characters escaped.
	 */
	public static final String escapeForXML(String string) {
		if (string == null) {
			return null;
		}
		char ch;
		int i = 0;
		int last = 0;
		char[] input = string.toCharArray();
		int len = input.length;
		StringBuffer out = new StringBuffer((int) (len * 1.3));
		for (; i < len; i++) {
			ch = input[i];
			if (ch > '>') {
				continue;
			} else if (ch == '<') {
				if (i > last) {
					out.append(input, last, i - last);
				}
				last = i + 1;
				out.append(LT_ENCODE);
			} else if (ch == '&') {
				if (i > last) {
					out.append(input, last, i - last);
				}
				last = i + 1;
				out.append(AMP_ENCODE);
			} else if (ch == '"') {
				if (i > last) {
					out.append(input, last, i - last);
				}
				last = i + 1;
				out.append(QUOTE_ENCODE);
			}
		}
		if (last == 0) {
			return string;
		}
		if (i > last) {
			out.append(input, last, i - last);
		}
		return out.toString();
	}

	/**
	 * Unescapes the String by converting XML escape sequences back into normal
	 * characters.
	 * 
	 * @param string
	 *            the string to unescape.
	 * @return the string with appropriate characters unescaped.
	 */
	public static final String unescapeFromXML(String string) {
		string = replace(string, "&lt;", "<");
		string = replace(string, "&gt;", ">");
		string = replace(string, "&quot;", "\"");
		return replace(string, "&amp;", "&");
	}

	private static final char[] zeroArray = "0000000000000000".toCharArray();

	/**
	 * Pads the supplied String with 0's to the specified length and returns the
	 * result as a new String. For example, if the initial String is "9999" and
	 * the desired length is 8, the result would be "00009999". This type of
	 * padding is useful for creating numerical values that need to be stored
	 * and sorted as character data. Note: the current implementation of this
	 * method allows for a maximum <tt>length</tt> of 16.
	 * 
	 * @param string
	 *            the original String to pad.
	 * @param length
	 *            the desired length of the new padded String.
	 * @return a new String padded with the required number of 0's.
	 */
	public static final String zeroPadString(String string, int length) {
		if (string == null || string.length() > length) {
			return string;
		}
		StringBuffer buf = new StringBuffer(length);
		buf.append(zeroArray, 0, length - string.length()).append(string);
		return buf.toString();
	}

	/**
	 * Formats a Date as a fifteen character long String made up of the Date's
	 * padded millisecond value.
	 * 
	 * @return a Date encoded as a String.
	 */
	public static final String dateToMillis(Date date) {
		return zeroPadString(Long.toString(date.getTime()), 15);
	}

	/**
	 * This method is used to split the input String. JDK1.4 has this method,but
	 * JDK1.3 does not has this method, so I code this method. if strInput =
	 * "00|||11||||||22" strToken = "|||" then the out put String is this:
	 * string[0] = "00" string[1] = "11" string[2] = "" string[3] = "22"
	 * 
	 * @param strInput
	 * @return String[]
	 */
	public static String[] split(String strInput, String splitToken) {
		int last = 0;
		int index = 0;
		int augment = splitToken.length();
		java.util.ArrayList matchList = new java.util.ArrayList();

		// Get each field name,and put it in arrayList.
		while (index <= strInput.length()) {
			index = strInput.indexOf(splitToken, index);

			// If the token is the last split token.
			if (index == -1) {
				String match = strInput.substring(last, strInput.length());
				matchList.add(match);
				break;
			} else {
				String match = strInput.substring(last, index);
				matchList.add(match);

				// move the index to the next startpoint.
				index += augment;
				last = index;
			}
		}

		// After split the String ,then store it in array.
		String[] fieldNames = new String[matchList.size()];
		for (int i = 0; i < matchList.size(); i++) {
			fieldNames[i] = (String) matchList.get(i);
			//System.out.println("The Splited is|"+fieldNames[i]+"|");
		}

		return fieldNames;
	}

	//??????????????? ?????????????????? ????????????????????? 2006/02/17
	public static String truncateChinese(String strTruncation, int cnt) {
		String ret = "";
		if (strTruncation == null || strTruncation.trim().equals("")
				|| cnt <= 0) {
			ret = "";
		} else {

			byte[] byTemp = strTruncation.getBytes();
			if (cnt >= byTemp.length) {
				ret = strTruncation;
			} else {
				boolean blFlag = false;
				for (int i = 0; i < cnt; i++) {
					//System.out.println(i+" "+(int)byTemp[i]);
					if (byTemp[i] > 128 || byTemp[i] < 0) {
						blFlag = !blFlag;
					}
				}
				if (blFlag) {
					ret = new String(byTemp, 0, cnt - 1);
				} else {
					ret = new String(byTemp, 0, cnt);
				}
			}
		}
		return ret;
	}
	
	public static List<String> getList(String value,String regex){
		
		List<String> list=new ArrayList<String> ();
        StringTokenizer st = new StringTokenizer(value, regex);
        while (st.hasMoreTokens()) {
        	list.add(st.nextToken());
        }
       return list;
		
	}
	
	public static String byteArrayToHexString(byte[] b) {
		String str = "";
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			str += hex.toUpperCase() + " ";
			if (i % 16 == 15) {
				str += "\n";
			}
		}
		str += "\n";
		return str;
	}
	
	public static String byteArrayToHexStringWithoutBlank(byte[] b) {
		String str = "";
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			str += hex.toUpperCase();
		}
		return str;
	}
	/**
	 * ???????????????????????? ?????????????????????????????????????????????????????????????????????
	 * ?????????????????? ????????? ??????????????????????????????
	 * @param type
	 * @param value
	 * @param ds
	 * @return
	 */
	public static String placeHolderReform(int type, String value, Map<String,String> ds) throws Exception {
		String ret = "";
		 String strTemp = "";
		 StringBuffer strOptionTemp =  new StringBuffer();
		if (type == 0) {
			ret = value;
		}else if (type == 1) {
//			byte[] byteArray = value.getBytes();
			int length = value.length();
//			byte[] byteTempArray = new byte[length];
			int tmpLength = 0;
			int lastLength=0;
			boolean placeHolderFlag = false;
			boolean optionFlag = false;
			boolean optionNeedFlag = true;
			char c=0;
			for (int i = 0; i < value.length(); i++) {
				c=value.charAt(i) ;
				if (c == '[') {
					//????????????[???????????????
					if (placeHolderFlag == true) {
						throw new Exception("[99]????????????????????????,??????${ :"+value);
					}else if (optionFlag == true) {
						throw new Exception("[99]????????????????????????,??????[ :"+value);
					}else {
						strOptionTemp.append(value.substring(lastLength, i));
						lastLength=i;
						optionFlag = true;
						optionNeedFlag = true;
					}
				}else if (c== ']') {
					//??????????????????????????????[]???????????????????????????map?????????
					if (placeHolderFlag == true) {
						throw new Exception("[99]????????????????????????,??????${ :"+value);
					}else if (optionFlag == false) {
						throw new Exception("[99]????????????????????????,????????????[ :"+value);
					}else {
						if (optionNeedFlag == true) {
								 strOptionTemp.append(value.substring(lastLength+1, tmpLength-2));
								 strOptionTemp.append(strTemp);
						}
						 lastLength=i+1;
						optionFlag = false;
						optionNeedFlag = true;
					}
				}else if (i+1<length&&"${".equals(value.substring(i, i+2))) {
					//${
					if (placeHolderFlag == true) {
						throw new Exception("[99]????????????????????????,??????${ :"+value);
					}else {
						if(optionFlag==true){
							
						}else{
							strOptionTemp.append(value.substring(lastLength, i));
							lastLength=i;
						}
						tmpLength=i+2;
						placeHolderFlag = true;
					}
				}else if (c == '}') {
					//${}?????????????????????
					if (placeHolderFlag == false) {
						throw new Exception("[99]????????????????????????,????????????${ :"+value);
					}else {
						if (tmpLength > 0) {
							String strTemp1 = ds.get(value.substring(tmpLength, i));
							if (strTemp1 != null) {
								if (optionFlag == true) {
//									strOptionTemp.append(value.substring(lastLength, tmpLength-1));
									strTemp=strTemp1;
								}else {
									strOptionTemp.append(strTemp1);
									lastLength=i+1;
								}
							}else {
								optionNeedFlag = false;
							}
						}
						placeHolderFlag = false;
					}
				}else {
//						strOptionTemp.append(value.substring(lastLength,length));
				}
			}
			if (placeHolderFlag == true) {
				throw new Exception("[99]????????????????????????,????????????${ :"+value);
			}else if (optionFlag == true) {
				throw new Exception("[99]????????????????????????,????????????[ :"+value);
			}else {
				strOptionTemp.append(value.substring(lastLength,length));
			}
		}else{
			throw new Exception("[99]???????????????????????????"+value);
		}
		return strOptionTemp.toString();
	}
	
	/**
	 * ???????????????????????????
	 * 
	 * @param length
	 * @return
	 */
	public static String getRandomString(int length) {
		char[] numbersAndLetters = ("0123456789").toCharArray();
		if (length < 1) {
			return null;
		}
		char[] randBuffer = new char[length];
		for (int i = 0; i < randBuffer.length; i++) {
			randBuffer[i] = numbersAndLetters[randGen.nextInt(10)];
		}
		return new String(randBuffer);
	}
	
	/**
	 * ??????str???????????????????????????
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str){
		return (str == null || str.trim().length() <= 0);
	}	
	
}
