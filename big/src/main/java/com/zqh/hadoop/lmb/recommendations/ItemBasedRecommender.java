package com.zqh.hadoop.lmb.recommendations;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemBasedRecommender {
	private static final Logger log = LoggerFactory.getLogger(ItemBasedRecommender.class);
	
	public static void main(String[] args) throws Exception {
		
		DataModel model = new FileDataModel(new File("src/main/resources/part-r-00000"));
		ItemSimilarity similarity = new PearsonCorrelationSimilarity(model);
		Recommender recommender = new GenericItemBasedRecommender(model, similarity);		
		CachingRecommender cachingRecommender = new CachingRecommender(recommender);
		long start = System.currentTimeMillis();
		List<RecommendedItem>  recommendations = cachingRecommender.recommend(4184250, 20);
		long end = System.currentTimeMillis();
		for (Iterator<RecommendedItem> iterator = recommendations.iterator(); iterator.hasNext(); ) {
			RecommendedItem item = iterator.next();
			System.out.println(item.getItemID() + "-" + item.getValue());
		}	
		log.info("time : " + (end -  start));
	}
}
