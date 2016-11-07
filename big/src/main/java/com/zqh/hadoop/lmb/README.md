Lamborghini
===========

The recommendation algorithm for Ali Recommendation Algorithm Competition

Installation
=======
1. Download Eclipse JavaEE
2. git clone https://github.com/lgrcyanny/Lamborghini.git
3. run maven install

Algorithm
========
1. Calculate predict ratings for each user of each brand with MapReduce<BR>
please run "RatingsGenerator.java" in eclipse
2. Calculate repeat buy records with MapReduce<BR>
please run "RepeatBuyGenerator.java"
3. Calculate Recommendations with Collaborative Filtering User Based Algorithm
4. Combine User Based results with repeat buy records to give the final result<BR>
please run "RecommendationsGenerator.java"
