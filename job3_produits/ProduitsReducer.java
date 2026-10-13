import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * Job 3 — Reducer : compte les ventes par produit
 * Sortie : product_id|brand|category_code \t nb_ventes
 *
 * NB : pour obtenir le TOP N, lancer un second job de tri
 * ou utiliser la sortie avec `sort -t$'\t' -k2 -rn | head -50`
 */
public class ProduitsReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

    private final IntWritable result = new IntWritable();

    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context)
            throws IOException, InterruptedException {

        int total = 0;
        for (IntWritable val : values) {
            total += val.get();
        }
        result.set(total);
        context.write(key, result);
    }
}
