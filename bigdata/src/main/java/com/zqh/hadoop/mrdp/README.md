mapreducepatterns
=================

Repository for MapReduce Design Patterns (O'Reilly 2012) example source code

**2-1 Numerical summarizations should be used when both of the following are true:**

• You are dealing with numerical data or counting.

• The data can be grouped by specific fields.


**2-2 Inverted indexes should be used when**

quick search query responses are required.

The results of such a query can be preprocessed and ingested into a database.


**2-3 Counting with counters should be used when:**

• You have a desire to gather counts or summations over large data sets.

• The number of counters you are going to create is small—in the double digits.


**3-1 Filtering is very widely applicable.**

The only requirement is that the data can be parsed into “records” that can

be categorized through some well-specified criterion determining whether they are to be kept.


**3-2 Bloom Filter**

**3-3 Top Ten**
• This pattern requires a comparator function ability between two records. That is,

  we must be able to compare one record to another to determine which is “larger.”

• The number of output records should be significantly fewer than the number of

  input records because at a certain point it just makes more sense to do a total ordering of the data set.


**3-4 Distinct**

**5-1 A reduce side join should be used when:**

• Multiple large data sets are being joined by a foreign key. If all but one of the data

sets can be fit into memory, try using the replicated join.

• You want the flexibility of being able to execute any join operation.


**5-2 A replicated join should be used when:**

• The type of join to execute is an inner join or a left outer join, with the large input

data set being the “left” part of the operation.

• All of the data sets, except for the large one, can be fit into main memory of each map task.


**5-3 A composite join should be used when:**

• An inner or full outer join is desired.

• All the data sets are sufficiently large.

• All data sets can be read with the foreign key as the input key to the mapper.

• All data sets have the same number of partitions.

• Each partition is sorted by foreign key, and all the foreign keys reside in the

associated partition of each data set. That is, partition X of data sets A and B contain

the same foreign keys and these foreign keys are present only in partition X.

• The data sets do not change often (if they have to be prepared).


**6-1 Join Chaining**


**6-2 Chain Folding**

There are a number of patterns in chains to look for to determine what to fold.

1. Take a look at the map phases in the chain. If multiple map phases are adjacent,
merge them into one phase. This would be the case if you had a map-only job (such
as a replicated join), followed by a numerical aggregation. In this step, we are re‐
ducing the amount of times we’re hitting the disks. Consider a two-job chain in
which the first job is a map-only job, which is then followed by a traditional Map‐
Reduce job with a map phase and a reduce phase. Without this optimization, the
first map-only job will write its output out to the distributed file system, and then
that data will be loaded by the second job.
多个Map阶段是相邻的, 则可以将他们合并为一个阶段. 比如Map-->MapReduce ==> MapMapReduce
示例: Job1.ReplicatedJoin(Filter)+Job2.MapReduce ==> Put Job1.Filter to Job2.Map

Instead, if we merge the map phase of the map-only job and the traditional job, that
temporary data never gets written, reducing the I/O significantly. Also, fewer tasks
are started, reducing overhead of task management. Chaining many map tasks to‐
gether is an even more drastic optimization. In this case, there really isn’t any
downside to do this other than having to possibly alter already existing code.

2. If the job ends with a map phase (combined or otherwise), push that phase into the
reducer right before it. This is a special case with the same performance benefits as
the previous step. It removes the I/O of writing temporary data out and then running
a map-only job on it. It also reduces the task start-up overhead.
作业的最后一个阶段是map,则可以把它合并到reduce的右边. MapReduce-->Map ==> MapReduceMap
示例: Job1.MapReduce+Job2.Map(Enrich) ==> put Job2.Enrich to Job1.Reduce

3. Note that the the first map phase of the chain cannot benefit from this next optimization.
As much as possible, split up each map phase (combined or otherwise) between
operations that decrease the amount of data (e.g., filtering) and 过滤器会减少数量
operations that increase the amount of data (e.g., enrichment).增强会增加数量
In some cases, this is not possible because you may need some enrichment data in order to do the filtering.
In these cases, look at dependent phases as one larger phase that cumulatively increases or decreases the amount of data.
Push the processes that decrease the amount of data into the previous reducer,
while keeping the processes that increase the amount of data where they are.

原则: 尽可能前面的Job完成filter, 这样传输给后面的Job的数据量就会少. 后面的Job完成Enrichment(比如ReplicatedJoin)


**6-2 Job Merging**
