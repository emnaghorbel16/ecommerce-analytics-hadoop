import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * Job 2 — Driver : Nombre de commandes par mois
 *
 * Usage:
 *   hadoop jar ecommerce-analytics.jar CommandesDriver \
 *     /ecommerce/cosmetics/raw \
 *     /ecommerce/cosmetics/output/commandes_par_mois
 */
public class CommandesDriver {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: CommandesDriver <input_path> <output_path>");
            System.exit(1);
        }

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Commandes par mois");

        job.setJarByClass(CommandesDriver.class);
        job.setMapperClass(CommandesMapper.class);
        job.setReducerClass(CommandesReducer.class);
        job.setCombinerClass(CommandesReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        job.setNumReduceTasks(1); // résultat trié chronologiquement

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
