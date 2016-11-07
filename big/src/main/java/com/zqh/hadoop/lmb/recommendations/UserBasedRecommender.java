package com.zqh.hadoop.lmb.recommendations;

import java.util.ArrayList;
import java.util.List;
import java.io.File;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.AveragingPreferenceInferrer;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import com.zqh.hadoop.lmb.mapreduce.Constant;

public class UserBasedRecommender {
	private DataModel model;
	private UserSimilarity similarity;
	private UserNeighborhood neighborhood;
	private Recommender recommender;
	private CachingRecommender cachingRecommender;
	private ArrayList<String> recommendations = new ArrayList<String>(2000);
	private final static String FILEPATH = Constant.FILEPATH_RATING + "/" + Constant.MR_OUTPUT_FILENAME;
	private final static int RECOMMEND_FACTOR = 10; // The number of recommendations for each user
	
	public UserBasedRecommender() {
		try {
			this.init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<String> generateRecommendations() throws TasteException {
		LongPrimitiveIterator iterator = getUserIds();
		System.out.println("Start generate user based recommendations");
		long start = System.currentTimeMillis();
		while (iterator.hasNext()) {
			long userid = iterator.nextLong();
			List<RecommendedItem> items = recommend(userid);
			recommendations.add(wrapRecommendations(userid, items));
		}
		long end = System.currentTimeMillis();
		System.out.println("Finish generate user based recommendations with time = " + (end - start));
		return recommendations;
	}
	
	public LongPrimitiveIterator getUserIds() throws TasteException {
		return model.getUserIDs();
	}
	
	private void init () throws Exception {
		model = new FileDataModel(new File(FILEPATH));
		similarity = new PearsonCorrelationSimilarity(model);
		similarity.setPreferenceInferrer(new AveragingPreferenceInferrer(model));
		neighborhood = new NearestNUserNeighborhood(20, similarity, model);
		recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
		cachingRecommender = new CachingRecommender(recommender);
	}
	
	private List<RecommendedItem> recommend(long userid) throws TasteException {
		return cachingRecommender.recommend(userid, RECOMMEND_FACTOR);
	}
	
	private String wrapRecommendations(long userid, List<RecommendedItem> items) {
		String res = userid + "\t";
		for (int i = 0; i < items.size(); i++) {
			if (i < items.size() - 1) {
				res = res + items.get(i).getItemID() + ",";
			} else {
				res = res + items.get(i).getItemID();
			}
		}
		return res;
	}
	
	
	public static void main(String[] args) throws Exception {
		UserBasedRecommender recommender = new UserBasedRecommender();
		
		long start = System.currentTimeMillis();
		ArrayList<String> recommendations = recommender.generateRecommendations();
		long end = System.currentTimeMillis();
		System.out.println("The number = " + recommendations.size());		
		System.out.println("Time is :" + (end - start));
	}
}
