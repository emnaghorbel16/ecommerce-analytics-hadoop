import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * Job 3 — Produits les plus vendus (top ventes)
 *
 * Colonnes CSV:
 *   1: event_type
 *   2: product_id
 *   5: brand
 *   4: category_code
 *
 * Clé émise  : "product_id|brand|category_code"
 * Valeur émise: 1
 */
public class ProduitsMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    private final Text productKey = new Text();
    private final IntWritable one = new IntWritable(1);

    @Override
    protected void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {

        String line = value.toString();
        if (line.startsWith("event_time")) return;

        String[] fields = line.split(",", -1);
        if (fields.length < 6) return;

        String eventType    = fields[1].trim();
        String productId    = fields[2].trim();
        String categoryCode = fields[4].trim();
        String brand        = fields[5].trim();

        if (!"purchase".equals(eventType)) return;
        if (productId.isEmpty()) return;

        // Clé composite : product_id | brand | category_code
        String compositeKey = productId
                + "|" + (brand.isEmpty() ? "unknown" : brand)
                + "|" + (categoryCode.isEmpty() ? "unknown" : categoryCode);

        productKey.set(compositeKey);
        context.write(productKey, one);
    }
}
