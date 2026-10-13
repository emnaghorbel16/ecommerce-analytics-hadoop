import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * Job 3 — Driver : Produits les plus vendus
 *
 * Usage:
 *   hadoop jar ecommerce-analytics.jar ProduitsDriver \
 *     /ecommerce/cosmetics/raw \
 *     /ecommerce/cosmetics/output/top_produits
 *
 * Après exécution, trier côté HDFS :
 *   hdfs dfs -cat /ecommerce/cosmetics/output/top_produits/part-r-00000 \
 *     | sort -t$'\t' -k2 -rn | head -50
 */
public class ProduitsDriver {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: ProduitsDriver <input_path> <output_path>");
            System.exit(1);
        }

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Top produits vendus");

        job.setJarByClass(ProduitsDriver.class);
        job.setMapperClass(ProduitsMapper.class);
        job.setReducerClass(ProduitsReducer.class);
        job.setCombinerClass(ProduitsReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        job.setNumReduceTasks(1);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
