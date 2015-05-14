package com.zqh.hadoop.lmb.mapreduce;

public class Constant {

	public static final int CLICK = 0;
	public static final int BUY = 1;
	public static final int COLLECT = 2;
	public static final int CART = 3;
	public final static String FILEPATH_COUNTBUY = "src/main/resources/countbuy-output";
	public final static String FILEPATH_RATING = "src/main/resources/ratings-output";
	public final static String FILEPATH_DATA_SOURCE = "src/main/resources/t_alibaba_data.csv";
	public final static String MR_OUTPUT_FILENAME = "part-r-00000";
	public final static String FILEPATH_RECOMMENDATIONS_OUTPUT = "src/main/resources/tmall_predict.txt";
	
}
