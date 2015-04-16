package com.zqh.mahout.helloworld;

import com.zqh.util.Common;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.io.File;
import java.util.List;

/**
 * Created by hadoop on 14-12-23.
 *
 * http://mahout.apache.org/users/recommender/userbased-5-minutes.html
 *
 * **********output**********
 INFO - Creating FileDataModel for file /home/hadoop/IdeaProjects/go-bigdata/helloworld/data/dataset.csv
 INFO - Reading file info...
 INFO - Read lines: 32
 INFO - Processed 4 users
 RecommendedItem[item:12, value:4.8328104]
 RecommendedItem[item:13, value:4.6656213]
 RecommendedItem[item:14, value:4.331242]
 */
public class SampleRecommender {

    public static void main(String[] args) throws Exception{
        DataModel model = new FileDataModel(new File(Common.filePath("data/mahout/dataset.csv")));

        UserSimilarity similarity = new PearsonCorrelationSimilarity(model);

        UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);

        UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);

        List<RecommendedItem> recommendations = recommender.recommend(2, 3);
        for (RecommendedItem recommendation : recommendations) {
            System.out.println(recommendation);
        }
    }
}
