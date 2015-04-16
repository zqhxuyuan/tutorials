processed = load '/output/hadoop/week07/part-r-00000' as (category:chararray, doc:chararray);
test = sample processed 0.2;
store test into '/output/hadoop/week08/digital/test';
jnt = join processed by(category, doc) left outer, test by(category, doc);
filt_test = filter jnt by test::category is null; 
train = foreach filt_test generate processed::category as category, processed::doc as doc;
store train into '/output/hadoop/week08/digital/train';
test_ct = foreach(group test by category) generate group, COUNT(test.category); 
train_ct = foreach(group train by category) generate group , COUNT(train.category);
dump test_ct;
(MP3,308)
(camera,273)
(mobile,472)
(computer,283)
(household,304)
dump train_ct;
(MP3,1199)
(camera,1012)
(mobile,1911)
(computer,1222)
(household,1199)

processed = load '/output/hadoop/week07/part-r-00000' as (category:chararray, doc:chararray);
test = sample processed 0.2;
jnt = join processed by(category, doc) left outer, test by(category, doc);
filt_test = filter jnt by test::category is null; 
train = foreach filt_test generate processed::category as category, processed::doc as doc;
test_ct = foreach(group test by category) generate group, COUNT(test.category); 
train_ct = foreach(group train by category) generate group , COUNT(train.category);
dump test_ct;
dump train_ct;

mahout trainclassifier -i /output/hadoop/week08/digital/train -o  /output/hadoop/week08/digital/model-bayes -type bayes -ng 1 -source hdfs

