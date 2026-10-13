import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * Job 4 — Driver : Taux de conversion vues → achats par catégorie
 *
 * Usage:
 *   hadoop jar ecommerce-analytics.jar ConversionDriver \
 *     /ecommerce/cosmetics/raw \
 *     /ecommerce/cosmetics/output/taux_conversion
 *
 * Sortie (colonnes séparées par \t) :
 *   catégorie | nb_vues | nb_achats | taux_%
 */
public class ConversionDriver {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: ConversionDriver <input_path> <output_path>");
            System.exit(1);
        }

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Taux de conversion");

        job.setJarByClass(ConversionDriver.class);
        job.setMapperClass(ConversionMapper.class);
        job.setReducerClass(ConversionReducer.class);

        // Pas de Combiner ici : on a besoin de compter views et purchases séparément
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setNumReduceTasks(1);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
