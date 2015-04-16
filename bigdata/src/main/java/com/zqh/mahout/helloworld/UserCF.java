package com.zqh.mahout.helloworld;

import com.zqh.util.Common;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 用户协同过滤
 * Collaborative Filtering
 *
 INFO - Creating FileDataModel for file /home/hadoop/IdeaProjects/go-bigdata/helloworld/data/mahout/item.csv
 INFO - Reading file info...
 INFO - Read lines: 21
 INFO - Processed 5 users
 uid:1(104,4.274336)(106,4.000000)
 uid:2(105,4.055916)
 uid:3(103,3.360987)(102,2.773169)
 uid:4(102,3.000000)
 uid:5

 修改RECOMMENDER_NUM＝１
 uid:1(104,4.274336)
 uid:2(105,4.055916)
 uid:3(103,3.360987)
 uid:4(102,3.000000)
 uid:5
 */
public class UserCF {

    final static int NEIGHBORHOOD_NUM = 2;
    final static int RECOMMENDER_NUM = 1;

    public static void main(String[] args) throws IOException, TasteException {
        // 1. 数据模型: 使用文件系统
        String file = Common.filePath("data/mahout/item.csv");
        DataModel model = new FileDataModel(new File(file));

        // 2. 用户相似度矩阵
        UserSimilarity user = new EuclideanDistanceSimilarity(model);
        // 近邻
        NearestNUserNeighborhood neighbor = new NearestNUserNeighborhood(NEIGHBORHOOD_NUM, user, model);

        // 3. 推荐器
        Recommender r = new GenericUserBasedRecommender(model, neighbor, user);
        LongPrimitiveIterator iter = model.getUserIDs();

        // 4. 推荐结果
        while (iter.hasNext()) {
            long uid = iter.nextLong();
            List<RecommendedItem> list = r.recommend(uid, RECOMMENDER_NUM);
            System.out.printf("uid:%s", uid);
            for (RecommendedItem ritem : list) {
                System.out.printf("(%s,%f)", ritem.getItemID(), ritem.getValue());
            }
            System.out.println();
        }
    }
}
