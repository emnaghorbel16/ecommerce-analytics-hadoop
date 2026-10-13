import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * Job 1 — Chiffre d'affaires par catégorie de produit
 *
 * Colonnes CSV (index 0-based):
 *   0: event_time | 1: event_type | 2: product_id | 3: category_id
 *   4: category_code | 5: brand | 6: price | 7: user_id | 8: user_session
 *
 * Logique : on ne comptabilise que les événements "purchase"
 * Clé émise  : category_code (ex: "electronics.smartphone") ou "unknown" si vide
 * Valeur émise: price
 */
public class CAMapper extends Mapper<LongWritable, Text, Text, DoubleWritable> {

    private final Text category = new Text();
    private final DoubleWritable price = new DoubleWritable();

    @Override
    protected void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {

        String line = value.toString();

        // Ignorer l'en-tête
        if (line.startsWith("event_time")) return;

        String[] fields = line.split(",", -1);
        if (fields.length < 7) return;

        String eventType   = fields[1].trim();
        String categoryCode = fields[4].trim();
        String priceStr    = fields[6].trim();

        // Seuls les achats comptent pour le CA
        if (!"purchase".equals(eventType)) return;

        // Catégorie : extraire le niveau principal (ex: "electronics.smartphone" → "electronics")
        if (categoryCode.isEmpty()) {
            category.set("unknown");
        } else {
            String[] parts = categoryCode.split("\\.");
            category.set(parts[0]); // niveau racine
        }

        try {
            price.set(Double.parseDouble(priceStr));
            context.write(category, price);
        } catch (NumberFormatException e) {
            // Ignorer les lignes avec prix invalide
        }
    }
}
