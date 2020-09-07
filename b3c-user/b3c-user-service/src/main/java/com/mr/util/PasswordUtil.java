package com.mr.util;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.bouncycastle.util.encoders.Base64;

public class PasswordUtil {

	/**
	 * 
	 * @Description: 管理员密码加密
	 * @author shenyaqi
	 * @date 2016-5-5 上午11:00:03
	 * @Title encode
	 *
	 */
	public static String encode(String password) {
		SecureRandom randomGen;
		try {
			randomGen = SecureRandom.getInstance("SHA1PRNG");

			byte salt[] = new byte[32];
			randomGen.nextBytes(salt);

			String saltString = new String(Base64.encode(salt));
			String hashString = encode(password, salt);
			return saltString + ":" + hashString;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return password;
	}

	/**
	 * 
	 * @Description: 管理员密码加密
	 * @author shenyaqi
	 * @date 2016-5-5 上午11:00:56
	 * @Title encode
	 *
	 */
	public static String encode(String password, byte[] salt)
			throws UnsupportedEncodingException, NoSuchAlgorithmException {
		byte[] passwordBytes = password.getBytes("UTF-8");
		byte[] hashSource = new byte[passwordBytes.length + salt.length];
		System.arraycopy(passwordBytes, 0, hashSource, 0, passwordBytes.length);
		System.arraycopy(salt, 0, hashSource, passwordBytes.length, salt.length);
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(hashSource);
		byte[] digest = md.digest();
		return new String(Base64.encode(digest));
	}

	/**
	 * 密码比较
	 * @param newPwd
	 * @param oldPwd
	 * @return
	 */
	public static boolean comparePassword(String newPwd, String oldPwd) {

		String realPassword = new String("000000000000000000000000000=");
		byte[] salt = new String("0000000000000000000000000000000=").getBytes();

		String storedPassword[] = oldPwd.split(":");
		if (storedPassword.length == 2) {
			realPassword = storedPassword[1];
			salt = Base64.decode(storedPassword[0]);
			String hashedPassword = null;
			try {
				if (newPwd.length() == 89) {
					hashedPassword = newPwd.substring(newPwd.indexOf(":") + 1);
				} else {
					hashedPassword = PasswordUtil.encode(newPwd, salt);
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return constantTimeEquals(realPassword, hashedPassword);
		}
		return false;
	}
	
	private static boolean constantTimeEquals(String a, String b) {
        byte[] aBytes = a.getBytes();
        byte[] bBytes = b.getBytes();
        int result = aBytes.length ^ bBytes.length;
        for (int i = 0; i < aBytes.length && i < bBytes.length; i++) {
            result |= aBytes[i] ^ bBytes[i];
        }
        return result == 0;
    }

}
