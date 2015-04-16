package com.zqh.hadoop.dataguru.week07_token;

import java.io.IOException;
import java.io.StringReader;

import com.zqh.hadoop.Counter;
import net.paoding.analysis.analyzer.PaodingAnalyzer;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
  
public class TokenizeMapper extends Mapper<Text, Text, Text, Text> {

	private Text outKey = new Text();
	private Text outValue = new Text();
	PaodingAnalyzer analyzer = new PaodingAnalyzer();

	
	public void map(Text key, Text value, Context context)
					throws IOException, InterruptedException {
		
		// set key
		outKey.set(key);		
		
		// set value
		String line = value.toString();
		StringReader sr = new StringReader(line);
		TokenStream ts = analyzer.tokenStream("", sr); 
		StringBuilder sb = new StringBuilder();     
		try{
			while (ts.incrementToken()) {
				CharTermAttribute ta = ts.getAttribute(CharTermAttribute.class);
				sb.append(ta.toString());
				sb.append(" ");
			}
		}catch(Exception e){
			context.getCounter(Counter.FAILDOCS).increment(1);
		}
		outValue.set(sb.toString());     
		
		//  output keyvalue pair
		context.write(outKey, outValue);
	}
	
}
