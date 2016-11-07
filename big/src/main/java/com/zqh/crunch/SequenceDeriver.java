package com.zqh.crunch;

import java.io.File;
import java.io.Serializable;
import java.util.Iterator;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.generic.GenericData.Record;
import org.apache.avro.io.DatumReader;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.crunch.DoFn;
import org.apache.crunch.Emitter;
import org.apache.crunch.MapFn;
import org.apache.crunch.PCollection;
import org.apache.crunch.PTable;
import org.apache.crunch.Pair;
import org.apache.crunch.Pipeline;
import org.apache.crunch.PipelineResult;
import org.apache.crunch.impl.mr.MRPipeline;
import org.apache.crunch.io.From;
import org.apache.crunch.io.To;
import org.apache.crunch.lib.SecondarySort;
import org.apache.crunch.types.avro.Avros;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * http://blog.cloudera.com/blog/2014/05/how-to-process-time-series-data-using-apache-crunch/
 * https://github.com/jeremybeard/tradesequence
 *
 * 1. 生成avro文件
 * 数据源是json文件, 通过trade_raw.avsc映射, 序列化为testdata.avro文件
 * java -jar ~/soft/cdh5.2.0/avro-1.7.6-cdh5.2.0/dist/java/avro-tools-1.7.6-cdh5.2.0.jar fromjson --schema-file trade_raw.avsc testdata.json > testdata.avro
 *
 * 2. 上传文件到hdfs
 * hadoop fs -put testdata.avro /input/avro
 *
 * 3. input参数指定testdata.avro的路径
 */
@SuppressWarnings("serial")
public class SequenceDeriver extends Configured implements Tool, Serializable {
    public int run(String[] args) throws Exception {
        /**
        if (args.length != 2) {
            System.err.println("Usage: hadoop jar " + this.getClass().getName() + " input output");
            System.err.println();
            GenericOptionsParser.printGenericCommandUsage(System.err);
            return 1;
        }*/

        args = new String[]{"hdfs://localhost:9000/input/avro/testdata.avro",
                "hdfs://localhost:9000/output/avro/tradeseq"};

        // get the HDFS paths from the command line arguments
        String inputPath = args[0];
        String outputPath = args[1];
        
        // create a Crunch pipeline object to coordinate the job
        Pipeline pipeline = new MRPipeline(SequenceDeriver.class, "Derive Trade Sequence", getConf());
        
        // load the Avro schema for a trade. this includes the optional sequence number, which is not in the input data
        Schema tradeSchema = new Schema.Parser().parse(
                new File("/home/hadoop/IdeaProjects/go-bigdata/helloworld/data/tradeseq/trade.avsc"));
        
        // read the Avro files from HDFS using the trade schema
        PCollection<Record> trades = pipeline.read(From.avroFile(inputPath, Avros.generics(tradeSchema)));
        
        // attach the stock symbol and the trade time to each trade record.
        // we use those values to correspondingly group and sort the trades
        PTable<String, Pair<Long, Record>> keyedTrades =
            trades.parallelDo(new MapFn<Record, Pair<String, Pair<Long, Record>>>() {
                @Override
                public Pair<String, Pair<Long, Record>> map(Record trade) {
                    String stock_symbol = trade.get("stock_symbol").toString();
                    Long trade_time = (Long)trade.get("trade_time");
                    return Pair.of(stock_symbol, Pair.of(trade_time, new Record(trade, true)));
                }
            }, Avros.tableOf(Avros.strings(), Avros.pairs(Avros.longs(), Avros.generics(tradeSchema))));
        
        // group and sort the trades so we can iterate over each trade for a stock symbol in trade time order.
        // execute that iteration and populate the sequence number field with incrementing numbers for each stock's trade
        PCollection<Record> sequencedTrades = 
            SecondarySort.sortAndApply(keyedTrades, new DoFn<Pair<String, Iterable<Pair<Long, Record>>>, Record>() {
                @Override
                public void process(Pair<String, Iterable<Pair<Long, Record>>> symbolTrades, Emitter<Record> emitter) {
                    Iterator<Pair<Long, Record>> trades = symbolTrades.second().iterator();
                    Integer seq = 0;
                    while (trades.hasNext()) {
                        Record trade = trades.next().second();
                        trade.put("sequence_num", ++seq);
                        emitter.emit(new Record(trade, true));
                    }
                }
            }, Avros.generics(tradeSchema));
        
        // write the processed trade records to Avro files on HDFS
        sequencedTrades.write(To.avroFile(outputPath));

        // run the pipeline!
        PipelineResult result = pipeline.done();

        //TODO demo print
        //sequencedTrades.parallelDo()

        return result.succeeded() ? 0 : 1;
    }
    
    public static void main(String[] args) throws Exception {
        int result = ToolRunner.run(new Configuration(), new SequenceDeriver(), args);
        System.exit(result);
    }
}
