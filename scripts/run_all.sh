#!/bin/bash
# ============================================================
#  run_all.sh — Compile et lance les 4 jobs MapReduce
#  Usage : bash scripts/run_all.sh
# ============================================================

INPUT="/ecommerce/cosmetics/raw"
OUTPUT_BASE="/ecommerce/cosmetics/output"
JAR="ecommerce-analytics.jar"

echo "======================================"
echo "  Ecommerce Analytics — MapReduce"
echo "======================================"

# ── 1. Compiler tous les fichiers Java ──
echo ""
echo "[1/5] Compilation Java..."
mkdir -p build

javac -source 11 -target 11 -cp $(hadoop classpath) \
  job1_ca/*.java \
  job2_commandes/*.java \
  job3_produits/*.java \
  job4_conversion/*.java \
  -d build

if [ $? -ne 0 ]; then echo "ERREUR: Compilation échouée"; exit 1; fi

# ── 2. Créer le JAR ──
echo "[2/5] Création du JAR..."
jar -cf $JAR -C build .
echo "  → $JAR créé"

# ── 3. Job 1 — CA par catégorie ──
echo ""
echo "[3/5] Job 1 : CA par catégorie..."
hdfs dfs -rm -r -f $OUTPUT_BASE/ca_par_categorie
hadoop jar $JAR CADriver $INPUT $OUTPUT_BASE/ca_par_categorie
echo "  Résultat :"
hdfs dfs -cat $OUTPUT_BASE/ca_par_categorie/part-r-00000

# ── 4. Job 2 — Commandes par mois ──
echo ""
echo "[4/5] Job 2 : Commandes par mois..."
hdfs dfs -rm -r -f $OUTPUT_BASE/commandes_par_mois
hadoop jar $JAR CommandesDriver $INPUT $OUTPUT_BASE/commandes_par_mois
echo "  Résultat :"
hdfs dfs -cat $OUTPUT_BASE/commandes_par_mois/part-r-00000

# ── 5. Job 3 — Top produits ──
echo ""
echo "[5/5] Job 3 : Top produits vendus..."
hdfs dfs -rm -r -f $OUTPUT_BASE/top_produits
hadoop jar $JAR ProduitsDriver $INPUT $OUTPUT_BASE/top_produits
echo "  Top 10 :"
hdfs dfs -cat $OUTPUT_BASE/top_produits/part-r-00000 | sort -t$'\t' -k2 -rn | head -10

# ── 6. Job 4 — Taux de conversion ──
echo ""
echo "[6/6] Job 4 : Taux de conversion..."
hdfs dfs -rm -r -f $OUTPUT_BASE/taux_conversion
hadoop jar $JAR ConversionDriver $INPUT $OUTPUT_BASE/taux_conversion
echo "  Résultat :"
hdfs dfs -cat $OUTPUT_BASE/taux_conversion/part-r-00000

echo ""
echo "======================================"
echo "  Tous les jobs sont terminés !"
echo "======================================"
echo ""
echo "Sorties HDFS disponibles dans :"
echo "  $OUTPUT_BASE/ca_par_categorie/"
echo "  $OUTPUT_BASE/commandes_par_mois/"
echo "  $OUTPUT_BASE/top_produits/"
echo "  $OUTPUT_BASE/taux_conversion/"
