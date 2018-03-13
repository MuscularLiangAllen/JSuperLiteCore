package com.liangtee.jsuperlite.core.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Encoder {

	 public static String getHash(String source, String hashType) {
		    StringBuilder sb = new StringBuilder();
		    MessageDigest md5 = null;
		    try {
		      md5 = MessageDigest.getInstance(hashType);
		      md5.update(source.getBytes());
		      for (byte b : md5.digest()) {
		        sb.append(String.format("%02X", b)); // 10进制转16进制，X 表示以十六进制形式输出，02 表示不足两位前面补0输出
		      }
		      return sb.toString();
		    } catch (NoSuchAlgorithmException e) {
		      e.printStackTrace();
		    }
		    return null;
		  }

	public static String get2RoundsHash(String source, String hashType) {
	 	return getHash(getHash(source, "MD5"), "MD5");
	}
	
}
