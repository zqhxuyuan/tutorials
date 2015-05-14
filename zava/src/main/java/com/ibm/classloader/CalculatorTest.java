package com.ibm.classloader;

public class CalculatorTest {

	public static void main(String[] args) {
		String url = "http://localhost:8080/ClassloaderTest/classes";
		NetworkClassLoader ncl = new NetworkClassLoader(url);
		String basicClassName = "com.example.CalculatorBasic";
		String advancedClassName = "com.example.CalculatorAdvanced";
		try {
			Class<?> clazz = ncl.loadClass(basicClassName);
			ICalculator calculator = (ICalculator) clazz.newInstance();
			System.out.println(calculator.getVersion());
			clazz = ncl.loadClass(advancedClassName);
			calculator = (ICalculator) clazz.newInstance();
			System.out.println(calculator.getVersion());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
