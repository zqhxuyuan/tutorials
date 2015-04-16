package com.zqh.mahout.helloworld;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.io.File;

/**
 * Created by hadoop on 14-12-23.
 *
 * **********output**********
 *
 INFO - Creating FileDataModel for file /home/hadoop/IdeaProjects/go-bigdata/helloworld/data/dataset.csv
 INFO - Reading file info...
 INFO - Read lines: 32
 INFO - Processed 4 users
 INFO - Beginning evaluation using 0.9 of FileDataModel[dataFile:/home/hadoop/IdeaProjects/go-bigdata/helloworld/data/dataset.csv]
 INFO - Processed 4 users
 INFO - Beginning evaluation of 2 users
 INFO - Starting timing of 2 tasks in 4 threads
 INFO - Average time per recommendation: 1ms
 INFO - Approximate memory used: 18MB / 187MB
 INFO - Unable to recommend in 1 cases
 INFO - Evaluation result: 0.3482532501220703
 0.3482532501220703
 */
public class EvaluateRecommender {

    public static void main(String[] args) throws Exception{
        DataModel model = new FileDataModel(new File("/home/hadoop/IdeaProjects/go-bigdata/helloworld/data/dataset.csv"));
        RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
        RecommenderBuilder builder = buildMyRecommender();
        double result = evaluator.evaluate(builder, null, model, 0.9, 1.0);
        System.out.println(result);
    }

    public static RecommenderBuilder buildMyRecommender(){
        RecommenderBuilder builder = new RecommenderBuilder(){
            @Override
            public Recommender buildRecommender(DataModel dataModel) throws TasteException {
                UserSimilarity similarity = new PearsonCorrelationSimilarity(dataModel);
                UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, dataModel);
                return new GenericUserBasedRecommender(dataModel, neighborhood, similarity);
            }
        };
        return builder;
    }
}
