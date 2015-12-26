/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package simple;

import java.util.HashMap;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;

public class ReadSparkSql {


	public static void main(String[] args) throws Exception {
		SparkConf sparkConf = new SparkConf()
				.setAppName("ReadSparkSql");
//				.setMaster("local");
				//.setMaster("spark://nasir-HP-Pavilion-15-Notebook-PC:7077");
				//.setMaster("local"); 
		JavaSparkContext ctx = new JavaSparkContext(sparkConf);
		ctx.addJar(args[2]);
		SQLContext sqlContext = new SQLContext(ctx);
 

		
		System.out.println("=== Data source: JSON Dataset ===");
		// A JSON dataset is pointed by path.
		// The path can be either a single text file or a directory storing text files.
		String path1 = args[0];
		String path2 = args[1];

	/*	StructType sparkSchema=new StructType(new StructField[]
				{new StructField("Amazon_Score",DataTypes.DoubleType,true,Metadata.empty()),
						new StructField("Amazon_Title",DataTypes.StringType,true,Metadata.empty()),
						new StructField("ID",DataTypes.StringType,true,Metadata.empty())});
	*/	// Create a DataFrame from the file(s) pointed by path
		DataFrame avgFile = sqlContext.read().format("json").load(path1);
		DataFrame omdbFile = sqlContext.read().format("json").load(path2);
		//DataFrame avgFile =sqlContext.read().schema(sparkSchema).json(path1);
		// Because the schema of a JSON dataset is automatically inferred, to write queries,
		// it is better to take a look at what is the schema.
		// Register this DataFrame as a table.
		avgFile.registerTempTable("avgFile");
		omdbFile.registerTempTable("omdb");

		String[] fieldNames1=avgFile.schema().fieldNames();
		String[] fieldNames2=avgFile.schema().fieldNames();
		HashMap<String,Integer> fieldMap1=new HashMap<String,Integer>();
		HashMap<String,Integer> fieldMap2=new HashMap<String,Integer>();
		int index=0;


		for(String s:fieldNames1){
			fieldMap1.put(s.toLowerCase(), index++);
		}

		index=0;
		for(String s:fieldNames2){
			fieldMap2.put(s.toLowerCase(), index++);
		}
		// SQL statements can be run by using the sql methods provided by sqlContext.
		//    DataFrame teenagers3 = sqlContext.sql("SELECT `product/productid` FROM people WHERE `product/productid` = 'b003ai2vga'");
		String [] temp = sqlContext.tableNames();
		for(String name : temp)
			System.out.println(name);
		DataFrame dfResult = sqlContext.sql("SELECT o.Title,o.Genre,a.Amazon_Score,"
				+ "o.Awards,o.Poster,o.imdbRating,o.Language, o.Year"
				+ " FROM omdb o, avgFile a"
				+ " where o.`Amazon Title` = a.Amazon_Title"
				+ " and   o.Type='movie' "
				+ " order by a.Amazon_Score desc");

		
//		Row[] rows=dfResult.collect();
//		for(Row row:rows){
//			System.out.println(row.toString());
//		}
		
		dfResult.toJSON().saveAsTextFile(args[3]);
//		System.out.println("Printing $$$$####");
		
//		DataFrame peopleWithCity = sqlContext.sql("SELECT name, address.city FROM people2");
		
		
//		Row[] rows=dfResult.collect();
//		FileWriter fw=new FileWriter(args[2]);
//		BufferedWriter bw=new BufferedWriter(fw);
//		int i=0;
//		for(Row row:rows){
////			System.out.println(row.toString());
//			bw.write(++i+"\t"+row.getString(0)+"\t"+row.getString(1)+"\t"+row.getString(2)+"\t"
//			                   +row.getString(3)+"\t"+row.getString(4)+"\t"+row.getString(5)
//			                   +"\t"+row.getString(6)+"\t"+ row.getString(7)+"\n");
//		}
//        bw.close();
//        fw.close();
		ctx.stop();
	}
}
