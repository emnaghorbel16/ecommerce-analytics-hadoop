import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * Job 4 — Reducer : calcule le taux de conversion par catégorie
 *
 * Sortie : catégorie \t nb_vues \t nb_achats \t taux_%
 *
 * Formule : taux = (nb_purchase / nb_view) * 100
 */
public class ConversionReducer extends Reducer<Text, Text, Text, Text> {

    private final Text result = new Text();

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context)
            throws IOException, InterruptedException {

        long nbViews     = 0;
        long nbPurchases = 0;

        for (Text val : values) {
            String v = val.toString();
            if (v.startsWith("view")) {
                nbViews++;
            } else if (v.startsWith("purchase")) {
                nbPurchases++;
            }
        }

        double taux = (nbViews == 0) ? 0.0
                : Math.round((nbPurchases * 10000.0 / nbViews)) / 100.0;

        // Format : nb_vues \t nb_achats \t taux%
        result.set(nbViews + "\t" + nbPurchases + "\t" + taux + "%");
        context.write(key, result);
    }
}
