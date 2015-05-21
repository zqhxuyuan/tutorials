package com.zqh.hadoop.mr.joins.msj;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.MapFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class MapperMapSideJoinDCacheMapFile extends Mapper<LongWritable, Text, Text, Text> {

    private MapFile.Reader deptMapReader = null;
    private Text txtMapOutputKey = new Text("");
    private Text txtMapOutputValue = new Text("");
    private Text txtMapLookupKey = new Text("");
    private Text txtMapLookupValue = new Text("");

    enum MYCOUNTER {
        RECORD_COUNT, FILE_EXISTS, LOAD_MAP_ERROR
    }

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        Path[] cacheFilesLocal = DistributedCache.getLocalCacheArchives(context.getConfiguration());

        for (Path eachPath : cacheFilesLocal) {
            if (eachPath.getName().toString().trim().equals("departments_map.tar.gz")) {
                URI uriUncompressedFile = new File(eachPath.toString() + "/departments_map").toURI();
                context.getCounter(MYCOUNTER.FILE_EXISTS).increment(1);
                loadDepartmentsMap(uriUncompressedFile, context);
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void loadDepartmentsMap(URI uriUncompressedFile, Context context) throws IOException {
        FileSystem dfs = FileSystem.get(context.getConfiguration());
        try {
            deptMapReader = new MapFile.Reader(dfs, uriUncompressedFile.toString(), context.getConfiguration());
        } catch (Exception e) {
            context.getCounter(MYCOUNTER.LOAD_MAP_ERROR).increment(1);
            e.printStackTrace();
        }
    }

    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        context.getCounter(MYCOUNTER.RECORD_COUNT).increment(1);

        if (value.toString().length() > 0) {
            String arrEmpAttributes[] = value.toString().split("\\t");
            txtMapLookupKey.set(arrEmpAttributes[6].toString());

            try {
                //txtMapLookupKey = deptNo
                //txtMapLookupValue = deptName
                deptMapReader.get(txtMapLookupKey, txtMapLookupValue);
            } finally {
                txtMapLookupValue.set((txtMapLookupValue.equals(null) || txtMapLookupValue
                        .equals("")) ? "NOT-FOUND" : txtMapLookupValue.toString());
            }

            txtMapOutputKey.set(arrEmpAttributes[0].toString());    // empNo --> joinKey
            txtMapOutputValue.set(arrEmpAttributes[1].toString() + "\t"
                    + arrEmpAttributes[1].toString() + "\t"
                    + arrEmpAttributes[2].toString() + "\t"
                    + arrEmpAttributes[3].toString() + "\t"
                    + arrEmpAttributes[4].toString() + "\t"
                    + arrEmpAttributes[5].toString() + "\t"
                    + arrEmpAttributes[6].toString() + "\t"         //deptNo
                    + txtMapLookupValue.toString());                //deptName
        }
        context.write(txtMapOutputKey, txtMapOutputValue);
        txtMapLookupValue.set("");
        txtMapLookupKey.set("");
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        deptMapReader.close();
    }
}