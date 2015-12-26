package com.amazon.movies.project;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.LazyOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class TitleCrawl {

	public static class Map extends Mapper<LongWritable, Text, Text, DoubleWritable> {
		
		/*
		 * Here we are extracting the productid field and review score field and sending it as 
		 * key value pair to reducer.
		 * 
		 */

		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			try {
				JSONObject row = new JSONObject(value.toString());
				Text prodID;
				try{
					prodID = new Text(row.getString("product/productid").trim());
				}catch(JSONException e){
					prodID = new Text("EMPTY");
				}
				double score = 0.0;
				try{
					score = row.getDouble("review/score");
				}catch(JSONException e){
					score = 0.0;
				}
				
				context.write(prodID, new DoubleWritable(score));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		}

		public static class Reduce extends Reducer<Text, DoubleWritable, Text, Text> {
			
			private int cnt;
			
			/*
			 * cnt variable is used to maintain count of requests made inorder to avoid 
			 * server crash
			 * 
			 */

			public void setup(Context context){
				cnt = 0;
			}

			/*
			 * Here the recieved product id is used to fetch the title from Amazon using product
			 * search API, and also simultaneously we are calculating the average review score
			 * for each id. 
			 * 
			 */
			public void reduce(Text key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException {
//			if(!key.toString().equals("EMPTY")){
				double avgscore=0;
				ProductSearch p = new ProductSearch();
				Node title=null;
				int i = 0;
				for(DoubleWritable avg : values){
					i++;
					avgscore+=avg.get();
				}
				avgscore = avgscore/i;
				if(cnt < 2){
					try {
						title = p.getMovieDetails(key.toString());
					} catch (SAXException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParserConfigurationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					context.write(key, new Text(title.getTextContent()+"\t"+avgscore));
					cnt++;
				}else{
					cnt=0;
					Thread.sleep(1000);
				}
//			}
		}
	}


		public static void main(String args[]) throws IOException, ClassNotFoundException, InterruptedException{
			Configuration conf = new Configuration();


			@SuppressWarnings("deprecation")
			Job job = new Job(conf, "Title Crawler");
			job.setJarByClass(TitleCrawl.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(DoubleWritable.class);

			job.setNumReduceTasks(10);

			job.setMapperClass(Map.class);
			job.setReducerClass(Reduce.class);

			job.setInputFormatClass(TextInputFormat.class);
			LazyOutputFormat.setOutputFormatClass(job, TextOutputFormat.class);
			//        job.setOutputFormatClass(TextOutputFormat.class);

			FileInputFormat.addInputPath(job, new Path(args[0]));
			FileOutputFormat.setOutputPath(job, new Path(args[1]));


			job.waitForCompletion(true);
		}

	}
