import sys
import os
import json
import pandas as pd
import joblib

def prever_janela(idade_meses, duracao_soneca, volume_leite, agitacao):
    caminho_modelo = os.path.join(os.path.dirname(__file__), "modelo_sono.joblib")
    
    if not os.path.exists(caminho_modelo):
        raise FileNotFoundError("O arquivo modelo_sono.joblib não foi encontrado. Execute train_model.py primeiro.")

    modelo = joblib.load(caminho_modelo)

    entrada = pd.DataFrame([{
        "idade_meses": float(idade_meses),
        "duracao_ultima_soneca_min": float(duracao_soneca),
        "volume_leite_ml": float(volume_leite),
        "nivel_agitacao_1a5": float(agitacao)
    }])

    resultado = modelo.predict(entrada)[0]
    return round(float(resultado), 1)

if __name__ == "__main__":
    # Permite passar parâmetros via linha de comando: python predict.py <idade> <soneca> <leite> <agitacao>
    if len(sys.argv) >= 5:
        idade = float(sys.argv[1])
        soneca = float(sys.argv[2])
        leite = float(sys.argv[3])
        agitacao = float(sys.argv[4])
    else:
        # Valores padrão para testes manuais
        idade, soneca, leite, agitacao = 4, 60, 140, 2

    try:
        janela = prever_janela(idade, soneca, leite, agitacao)
        resposta = {
            "status": "sucesso",
            "janelaSonoEstimadaMinutos": janela,
            "mensagem": f"O bebê deve precisar dormir em aproximadamente {janela} minutos."
        }
        print(json.dumps(resposta, ensure_ascii=False))
    except Exception as e:
        print(json.dumps({"status": "erro", "mensagem": str(e)}, ensure_ascii=False))