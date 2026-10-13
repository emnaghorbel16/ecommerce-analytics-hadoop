import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * Job 1 — Driver : Chiffre d'affaires par catégorie de produit
 *
 * Usage:
 *   hadoop jar ecommerce-analytics.jar CADriver \
 *     /ecommerce/cosmetics/raw \
 *     /ecommerce/cosmetics/output/ca_par_categorie
 */
public class CADriver {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: CADriver <input_path> <output_path>");
            System.exit(1);
        }

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "CA par categorie");

        job.setJarByClass(CADriver.class);
        job.setMapperClass(CAMapper.class);
        job.setReducerClass(CAReducer.class);

        // Combiner = même logique que le Reducer → optimise le shuffle
        job.setCombinerClass(CAReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);

        // Nombre de reducers : 1 pour un résultat trié dans un seul fichier
        job.setNumReduceTasks(1);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
