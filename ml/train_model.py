import os
import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestRegressor
from sklearn.preprocessing import StandardScaler
from sklearn.pipeline import Pipeline
from sklearn.metrics import mean_absolute_error, r2_score
import joblib

def treinar_modelo():
    caminho_csv = os.path.join(os.path.dirname(__file__), "dataset_sono.csv")
    df = pd.read_csv(caminho_csv)

    # Features (X) e Target (y)
    X = df[["idade_meses", "duracao_ultima_soneca_min", "volume_leite_ml", "nivel_agitacao_1a5"]]
    y = df["janela_sono_min"]

    # Divisão Treino (80%) e Teste (20%)
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

    # Pipeline: Normalização + Regressor
    pipeline = Pipeline([
        ("scaler", StandardScaler()),
        ("regressor", RandomForestRegressor(n_estimators=100, random_state=42))
    ])

    # Treinamento
    pipeline.fit(X_train, y_train)

    # Avaliação
    predicoes = pipeline.predict(X_test)
    mae = mean_absolute_error(y_test, predicoes)
    r2 = r2_score(y_test, predicoes)

    print("=== Métricas do Modelo ===")
    print(f"Erro Médio Absoluto (MAE): {mae:.2f} minutos")
    print(f"Coeficiente de Determinação (R²): {r2:.3f}")

    # Salva o artefato serializado
    caminho_saida = os.path.join(os.path.dirname(__file__), "modelo_sono.joblib")
    joblib.dump(pipeline, caminho_saida)
    print(f"\nModelo salvo com sucesso em: {caminho_saida}")

if __name__ == "__main__":
    treinar_modelo()