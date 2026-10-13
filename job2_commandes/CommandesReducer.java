import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * Job 2 — Reducer : somme des commandes par mois
 * Sortie : YYYY-MM \t nb_commandes
 */
public class CommandesReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

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
