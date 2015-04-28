package com.paperbook.batchimport2;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.GregorianCalendar;


public class Utils {
	
	public static int random(int m, int n) {
		return (int)(Math.random() * (n - m + 1) + m);
	}
	
	public static String md5(String str) {
		MessageDigest md;
		StringBuilder res = new StringBuilder();
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte[] data = md.digest();
			for (int i = 0; i < data.length; i++) {
				String hex = Integer.toHexString(data[i] & 0xff);
				if (hex.length() == 1) {
					hex = "0" + hex;
				}
				res.append(hex);
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return res.toString();
	}
	
	/**
	 * 
	 * @param literatureYear  The publication year of a literature
	 * @return the upload or comment timestamp by the user
	 */
	public static long getRandomTimeStamp(int literatureYear) {
		int year = Utils.random(literatureYear, 2014);
		int month = Utils.random(1, 12);
		int day = Utils.random(1, 28);
		GregorianCalendar gc = new GregorianCalendar(year, month, day);
		return gc.getTimeInMillis();
	}

}
