import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * Job 4 — Taux de conversion : vues → achats par catégorie
 *
 * Pour chaque événement "view" ou "purchase" :
 *   Clé émise  : category_code (niveau racine)
 *   Valeur émise: "view,1"  ou  "purchase,1"
 *
 * Le Reducer calculera : taux = nb_purchase / nb_view * 100
 */
public class ConversionMapper extends Mapper<LongWritable, Text, Text, Text> {

    private final Text categoryKey = new Text();
    private final Text eventVal    = new Text();

    @Override
    protected void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {

        String line = value.toString();
        if (line.startsWith("event_time")) return;

        String[] fields = line.split(",", -1);
        if (fields.length < 7) return;

        String eventType    = fields[1].trim();
        String categoryCode = fields[4].trim();

        // Seuls "view" et "purchase" nous intéressent
        if (!"view".equals(eventType) && !"purchase".equals(eventType)) return;

        String category = categoryCode.isEmpty() ? "unknown"
                : categoryCode.split("\\.")[0];

        categoryKey.set(category);
        eventVal.set(eventType + ",1");
        context.write(categoryKey, eventVal);
    }
}
