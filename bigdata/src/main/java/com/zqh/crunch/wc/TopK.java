package com.zqh.crunch.wc;

import java.io.Serializable;

import org.apache.crunch.DoFn;
import org.apache.crunch.Emitter;
import org.apache.crunch.PCollection;
import org.apache.crunch.PGroupedTable;
import org.apache.crunch.PTable;
import org.apache.crunch.Pair;
import org.apache.crunch.fn.Aggregators;
import org.apache.crunch.types.avro.Avros;
import org.apache.crunch.util.CrunchTool;
import org.apache.hadoop.util.ToolRunner;

public class TopK extends CrunchTool implements Serializable {

    @Override
    public int run(String[] args) throws Exception {
        int k = 10;

        PCollection<String> lines = readTextFile(args[0]);
        PTable<String, Long> table = lines.parallelDo(
            new DoFn<String, Pair<String, Long>>() {
                @Override
                public void process(String line, Emitter<Pair<String, Long>> emitter) {
                    if (line.length() < 3) return;
                    String[] split = line.substring(1, line.length() - 1).split(",");
                    long count = 0;
                    try {
                        count = Long.parseLong(split[1]);
                    } catch (Exception e) {
                    }
                    emitter.emit(Pair.of(split[0], count));
                }
            }, Avros.tableOf(Avros.strings(), Avros.longs()));

        PGroupedTable<String, Long> groupedRecords = table.groupByKey();

        PTable<String, Long> counts = groupedRecords.combineValues(Aggregators.SUM_LONGS()).top(20);

        //PTable<String, Long> topk = Aggregate.top(counts, k, true);
        writeTextFile(counts, args[1]);

        return run().succeeded() ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        int rc = ToolRunner.run(new TopK(), args);
        System.exit(rc);
    }
}