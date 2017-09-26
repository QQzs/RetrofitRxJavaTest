package com.zs.demo.retrofitrxjavatest.util;


import com.qiniu.android.utils.UrlSafeBase64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class NewAES {
	public static String encrypt(String strEncpt, String pwd) {
		try {
			byte[] bytIn = strEncpt.getBytes("UTF-8");
			SecretKeySpec skeySpec = new SecretKeySpec(pwd.getBytes("UTF-8"), "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			byte[] bytOut = cipher.doFinal(bytIn);
			String ecrOut = new String(UrlSafeBase64.encodeToString(bytOut));
			return ecrOut;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static String decrypt(String strDencpt, String pwd) {
		try {
			byte[] bytIn;
			String ecrOut = "";
			bytIn = UrlSafeBase64.decode(strDencpt);
			SecretKeySpec skeySpec = new SecretKeySpec(pwd.getBytes("UTF-8"), "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			byte[] bytOut = cipher.doFinal(bytIn);
			ecrOut = new String(bytOut, "UTF-8");
			return ecrOut;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
