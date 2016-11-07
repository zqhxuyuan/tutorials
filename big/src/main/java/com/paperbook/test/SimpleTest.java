package com.paperbook.test;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class SimpleTest {
	
	public static void main(String[] args) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTimeInMillis(System.currentTimeMillis() - (long)3600 * 1000 * 24 * 365);
		//System.out.println((System.currentTimeMillis() - 3600 * 1000 * 24 * 7));
		System.out.println(gc.get(Calendar.YEAR) + "-" + gc.get(Calendar.MONTH) + "-" + gc.get(Calendar.DAY_OF_MONTH));
		System.out.println(System.currentTimeMillis());
	}

}
