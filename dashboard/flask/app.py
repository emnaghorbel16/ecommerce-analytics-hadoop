from flask import Flask, jsonify, render_template_string
import subprocess
import os

app = Flask(__name__)

HDFS_PATHS = {
    "ca":       "/ecommerce/cosmetics/output/ca_par_categorie/part-*",
    "monthly":  "/ecommerce/cosmetics/output/commandes_par_mois/part-*",
    "products": "/ecommerce/cosmetics/output/top_produits/part-*",
}

def read_hdfs(path):
    """Lit un fichier HDFS via subprocess et retourne les lignes."""
    try:
        result = subprocess.run(
            ["hdfs", "dfs", "-cat", path],
            capture_output=True, text=True, timeout=30
        )
        if result.returncode != 0:
            return None, result.stderr
        lines = [l.strip() for l in result.stdout.strip().split("\n") if l.strip()]
        return lines, None
    except Exception as e:
        return None, str(e)

def parse_kv(lines):
    """Parse les lignes 'clé\\tvaleur' en liste de dicts."""
    data = []
    for line in lines:
        parts = line.split("\t")
        if len(parts) == 2:
            key, val = parts
            try:
                data.append({"label": key.strip(), "value": float(val.strip())})
            except ValueError:
                pass
    return data

# ── Routes API ────────────────────────────────────────────────

@app.route("/api/ca")
def api_ca():
    lines, err = read_hdfs(HDFS_PATHS["ca"])
    if err:
        return jsonify({"error": err}), 500
    data = parse_kv(lines)
    data.sort(key=lambda x: x["value"], reverse=True)
    return jsonify(data)

@app.route("/api/monthly")
def api_monthly():
    lines, err = read_hdfs(HDFS_PATHS["monthly"])
    if err:
        return jsonify({"error": err}), 500
    data = parse_kv(lines)
    data.sort(key=lambda x: x["label"])   # tri chronologique
    return jsonify(data)

@app.route("/api/products")
def api_products():
    lines, err = read_hdfs(HDFS_PATHS["products"])
    if err:
        return jsonify({"error": err}), 500
    data = parse_kv(lines)
    data.sort(key=lambda x: x["value"], reverse=True)
    return jsonify(data[:10])              # top 10

@app.route("/")
def dashboard():
    with open("dashboard.html", "r", encoding="utf-8") as f:
        html = f.read()
    return render_template_string(html)

# ── Health check ──────────────────────────────────────────────

@app.route("/api/health")
def health():
    return jsonify({"status": "ok"})

if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=5000)
