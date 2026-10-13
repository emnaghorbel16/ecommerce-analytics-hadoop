import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * Job 1 — Reducer : somme des prix par catégorie
 * Sortie : catégorie \t chiffre_d_affaires_total
 */
public class CAReducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {

    private final DoubleWritable result = new DoubleWritable();

    @Override
    protected void reduce(Text key, Iterable<DoubleWritable> values, Context context)
            throws IOException, InterruptedException {

        double totalCA = 0.0;
        for (DoubleWritable val : values) {
            totalCA += val.get();
        }

        result.set(Math.round(totalCA * 100.0) / 100.0); // arrondi 2 décimales
        context.write(key, result);
    }
}
