import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * Job 2 — Nombre de commandes (purchases) par mois
 *
 * Colonnes CSV:
 *   0: event_time (format: "2019-10-01 00:00:00 UTC")
 *   1: event_type
 *
 * Clé émise  : "YYYY-MM"  (ex: "2019-10")
 * Valeur émise: 1 (une commande)
 */
public class CommandesMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    private final Text moisKey = new Text();
    private final IntWritable one = new IntWritable(1);

    @Override
    protected void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {

        String line = value.toString();
        if (line.startsWith("event_time")) return;

        String[] fields = line.split(",", -1);
        if (fields.length < 2) return;

        String eventTime = fields[0].trim();  // "2019-10-01 00:00:00 UTC"
        String eventType = fields[1].trim();

        if (!"purchase".equals(eventType)) return;

        // Extraire YYYY-MM depuis event_time
        if (eventTime.length() < 7) return;
        String mois = eventTime.substring(0, 7); // "2019-10"

        moisKey.set(mois);
        context.write(moisKey, one);
    }
}
