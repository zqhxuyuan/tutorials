package com.zqh.hadoop.mr.pagerank;

/**
 * Created by zqhxuyuan on 15-3-4.
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;

public class PageRank {

    public static void createInitialRankVector(String directory, long n) throws IOException
    {
        File dir = new File(directory);
        FileUtils.deleteQuietly(dir);
        FileUtils.forceMkdir(dir);
        File file = new File(directory + "/part-r-00000");
        // if file doesnt exists, then create it
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
        for (int i = 1; i <= n; i++) {
            bw.write(i + " " + new Double(1.0 / n).toString());
            bw.newLine();
        }
        bw.close();
    }

    public static boolean checkConvergence(String initialDir, String iterationDir, double epsilon) throws IOException
    {
        List<Double> initialVector = new ArrayList<Double>();
        List<Double> iterationVector = new ArrayList<Double>();

        //Read initialDir
        InputStream ips=new FileInputStream(initialDir+"/part-r-00000");
        InputStreamReader ipsr = new InputStreamReader(ips);
        BufferedReader br = new BufferedReader(ipsr);
        String ligne;
        while ((ligne=br.readLine())!=null){
            String[] splits  = ligne.toString().split("\\s+");
            //Stock value in the list
            initialVector.add(Double.parseDouble(splits[1]));
        }
        br.close();
        //Read iterationDir
        ips=new FileInputStream(iterationDir+"/part-r-00000");
        ipsr = new InputStreamReader(ips);
        br = new BufferedReader(ipsr);
        while ((ligne=br.readLine())!=null){
            String[] splits  = ligne.toString().split("\\s+");
            //Stock value in the list
            iterationVector.add(Double.parseDouble(splits[1]));
        }
        br.close();

        Double sum = 0.0;

        for (int i = 0 ; i < initialVector.size(); i++) {
            Double abs = Math.abs(initialVector.get(i) - iterationVector.get(i));
            sum += abs;
        }

        if (sum < epsilon) {
            return true;
        } else {
            return false;
        }
    }

    public static void avoidSpiderTraps(String vectorDir, long nNodes, double beta)
    {
        HashMap<Integer,Double> iterationVector = new HashMap<Integer,Double>();

        //Read vectorDir
        vectorDir = vectorDir+"/part-r-00000";
        try{
            InputStream ips=new FileInputStream(vectorDir);
            InputStreamReader ipsr = new InputStreamReader(ips);
            BufferedReader br = new BufferedReader(ipsr);
            String ligne;
            while ((ligne=br.readLine())!=null){
                String[] splits  = ligne.toString().split("\\s+");
                //Stock value in the list
                iterationVector.put(Integer.parseInt(splits[0]),Double.parseDouble(splits[1]));
            }
            br.close();
        }
        catch (Exception e){}

        //Write in the file vectorDir
        try {
            File file = new File(vectorDir);
            FileUtils.forceDelete(file);
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(vectorDir);
            BufferedWriter bw = new BufferedWriter(fw);
            for (int i = 0; i < iterationVector.size(); i++) {
                Double val = iterationVector.get(i+1);
                Double newVal = new Double((beta*val) + ((double) (1.0 - beta)/nNodes ));
                bw.write((i+1) + " " + newVal.toString() );
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void iterativePageRank(Configuration conf)
            throws IOException, InterruptedException, ClassNotFoundException
    {


        String initialVector = conf.get("initialRankVectorPath");
        String currentVector = conf.get("currentRankVectorPath");

        String finalVector = conf.get("finalRankVectorPath");
		/*here the testing system will seach for the final rank vector*/

        Double epsilon = conf.getDouble("epsilon", 0.1);
        Double beta = conf.getDouble("beta", 0.8);

        //Launch remove dead ends jobs
        RemoveDeadends.job(conf);

        //Create initial vector
        Long nNodes = conf.getLong("numNodes", 1);
        createInitialRankVector(initialVector, conf.getLong("numNodes", 1));

        //Create stochastic matrix
        GraphToMatrix.job(conf);

        boolean converg = false;
        //multiplication M * r
        MatrixVectorMult.job(conf);

        avoidSpiderTraps(currentVector, nNodes, beta);


        while(!converg) {
            FileUtils.deleteQuietly(new File(initialVector));
            FileUtils.moveFile(new File(currentVector+"/part-r-00000"), new File(initialVector+"/part-r-00000"));

            //multiplication M * r
            MatrixVectorMult.job(conf);

            avoidSpiderTraps(currentVector, nNodes, beta);

            converg = checkConvergence(initialVector, currentVector, epsilon);
        }

        FileUtils.copyDirectory(new File(currentVector), new File(finalVector));

    }
}
