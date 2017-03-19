import java.io.IOException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class CrisisAnalysis {

	//public static String keywords;
   // public static String prayers;
    public static ArrayList<String> keyword = new ArrayList<String>();
    public static List<String> prayer = new ArrayList<String>();
	
    public static String urlPattern = "^(http:\\/\\/|https:\\/\\/)?(www.)?([a-zA-Z0-9]+).[a-zA-Z0-9]*.[a-z]{3}.?([a-z]+)?$";
    public static Pattern urlmatch = Pattern.compile(urlPattern,Pattern.CASE_INSENSITIVE);

  public static class TokenizerMapper
       extends Mapper<Object, Text, Text, IntWritable>{

    private final static IntWritable one = new IntWritable(1);
    private Text word = new Text();
	        String crisis;
	

    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
	String val1 = new String("terrorist");	
	keyword.add(val1);	
	keyword.add("earthquake");
	keyword.add("murder");
	keyword.add("crisis");
	keyword.add("rain");
	keyword.add("avalanche");
	keyword.add("fire");
	keyword.add("gun");
	keyword.add("terrorist");
	keyword.add("terrorism");
	keyword.add("kill");
	keyword.add("killing");
	keyword.add("border");
	keyword.add("firing");
	keyword.add("flood");
	keyword.add("hurricane");
	keyword.add("cyclone");
	keyword.add("draught");
	keyword.add("attack");
	keyword.add("tsunami");
	keyword.add("floods");
	keyword.add("tornado");
	keyword.add("sandstorm");
	keyword.add("landslide");
	keyword.add("accident");
	keyword.add("blast");
	keyword.add("theft");
	keyword.add("bomb");
	keyword.add("danger");
	keyword.add("thunderstorm");
	keyword.add("storm");
	keyword.add("duststorm");
	keyword.add("rains");
	keyword.add("militants");
	keyword.add("nuclear");
	keyword.add("reactor");


	prayer.add("rip"); 
	prayer.add("condolences");
	prayer.add("restinpeace");
	prayer.add("pray");
	prayer.add("RIP");	


	String line = value.toString();
	String[] data = line.split("~%~");
	
	if(data.length>6){
	String language = data[0];
        String  text = data[1];
                String  url = data[2];
                String  latitude = data[3];
                String  longitude = data[4];
                String  retweet_status = data[5];
                String  place = data[6];
                if (language.equals("en"))
                {

                 if(retweet_status.equals("false"))
                    {
                      String[] words = text.split("\\s+");
			
                        if(words.length<15)
                        {
                          Matcher match = urlmatch.matcher(text);
                            int i = 0;
                            while (match.find()) {
                                i++;
                            }
                            if(i==0)
                            {
                              int flag=0;
                                for(String temp: words) {
                                   
				
				
                                    if(prayer.contains(temp))
                                        flag=1;
                                }
                                if(flag==0) {
				String[] words1 = text.split("\\s+");
				for(int index=0; index<words1.length; index++){
			
                                  String tem = words1[index];
				
                                    tem.toLowerCase();
		
		
				if(keyword.contains(tem))
				{       
				
                                                                  
                                
                            
                        
	String location;
	place = place.replaceAll(","," ");
	 if(latitude.equals("null"))
                   location = new String(place);
	 else
            location = new String(latitude + " " + longitude);

               context.write(new Text(location+","+tem+","), one);
							}//found crisi
						}//for crisis
					}//no prayer
				}//url match
			}//word length
                    }//retweet

                }//lang
	}
	
    }
  }

  public static class IntSumReducer
       extends Reducer<Text,IntWritable,Text,IntWritable> {
    private IntWritable result = new IntWritable();

    public void reduce(Text key, Iterable<IntWritable> values,
                       Context context
                       ) throws IOException, InterruptedException {
      int sum = 0;
      for (IntWritable val : values) {
        sum += val.get();
      }
      result.set(sum);
      context.write(key, result);
    }
  }

  public static void main(String[] args) throws Exception {


 
  
	Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "CrisisAnalysis");
    job.setJarByClass(CrisisAnalysis.class);
    job.setMapperClass(TokenizerMapper.class);
    job.setCombinerClass(IntSumReducer.class);
    job.setReducerClass(IntSumReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
