PaperBook-MapReduce
===================

Build MapReduce Job for PaperBook, like inverted index for search, statistics

Requirements For Inverted Index
============
1. Generate 20000 literature records randomly, and insert them into HBase<BR>
The strategy is: <br>
<ul>
  <li>Get a dictionary with 2000 words from online resources, it has inclued in "resources/words.txt"</li>
  <li>Literature title length is 2~10, each word in dictionary appear only once in a title. The maxmium number of a word appear accross all titles is 50</li>
  <li>Literature authors is made up of 2 ~3 words from dictionary</li>
  <li>Literature publication is randomly seletec from the 50 publications in the file "resources/publications.txt"</li>
  <li>Literature year is randomly from 1990 to 2014.</li>
</ul>

2.Use map reduce job to generate inverted index table in hbase to improve search


Installation for Inverted Index
===========
1. Please install Hadoop 2.2.0 and HBase 0.96.1
2. git clone https://github.com/lgrcyanny/PaperBook-MapReduce
3. Import the project into eclipse
4. Add all of the jars under "hbase-install-dir/lib" to your reference library by configuring Build Path
5. Add "hbase-install-dir/conf" to your reference library as step 4
6. Start hadoop hand hbase
```sh
  $ start-dfs.sh
  $ start-yarn.sh
  $ start-hbase.sh
  $ jps
  43807 HQuorumPeer
  44116 ResourceManager
  43881 HMaster
  43509 DataNode
  44193 NodeManager
  43435 NameNode
  43976 HRegionServer
  43601 SecondaryNameNode
  74209 Jps
```
7.Create table in HBase<BR>
```sh
  $ hbase shell
  $ create 'pb_index_title', 'info'
  $ create 'pb_index_year', 'info'
  $ create 'pb_index_authors', 'info'
  $ create 'pb_index_publication', 'info'
```
8.Import random data into hbase<BR>
We import 20000 literature into hbase, please run com.paperbook.batchimport.LiteratureGenerator.java

9.Run MapReduce jobs in eclipse<BR>
com.paperbook.mapreduce.InvertedIndexTitle.java<BR>
com.paperbook.mapreduce.InvertedIndexAuthors.java<BR>
com.paperbook.mapreduce.InvertedIndexPublication.java<BR>
com.paperbook.mapreduce.InvertedIndexYear.java<BR>

Configure for Statistics
==========================
1.create table in HBase<br>
```sh
  $ hbase shell
  $ create 'pb_mr_literatures', 'info'
  $ create 'pb_mr_literatures_repeat', 'info'
  $ create 'pb_mr_comments', 'info'
  $ create 'pb_stat_comments', 'info'
  $ create 'pb_stat_literatures', 'info'
  $ create 'pb_stat_comments_count', 'info'
```

2.Import data
run BatchJob.java import 100000 literatures and roughly 1380000 comments<BR>
run BatchJobRepeatLiteratures.java to import 100000 literatures and roughly 350 repeat records<BR>

3.Run mapreduce jobs<BR>
LiteraturesStat.java<BR>
CommentsStat.java<BR>
CommentsCount.java<BR>
SecondarySort.java<BR>
LiteratureRepeatCount.java<BR>








