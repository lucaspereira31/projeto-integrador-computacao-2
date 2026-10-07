/**
 * api.js - Módulo de integração do Frontend com a API REST Spring Boot
 * Projeto Integrador: Babá Premium
 */

const API_BASE_URL = "http://localhost:8080/api";

/**
 * Função utilitária para requisições HTTP padronizadas
 * @param {string} endpoint - Caminho relativo do endpoint (ex: '/rotina')
 * @param {string} method - 'GET', 'POST', 'PUT', 'DELETE'
 * @param {object|null} data - Corpo da requisição (payload)
 */
async function requestAPI(endpoint, method = "GET", data = null) {
  const url = `${API_BASE_URL}${endpoint}`;
  
  const options = {
    method,
    headers: {
      "Content-Type": "application/json",
      "Accept": "application/json"
    }
  };

  // Se houver token JWT salvo pelo auth.js, anexa automaticamente no cabeçalho
  const token = localStorage.getItem("baba_premium_token");
  if (token) {
    options.headers["Authorization"] = `Bearer ${token}`;
  }

  if (data && (method === "POST" || method === "PUT" || method === "PATCH")) {
    options.body = JSON.stringify(data);
  }

  try {
    const response = await fetch(url, options);

    if (!response.ok) {
      const errorBody = await response.text();
      throw new Error(`Erro ${response.status} na API: ${errorBody || response.statusText}`);
    }

    // Se o retorno for 204 No Content, retorna nulo sem quebrar o parse
    if (response.status === 204) return null;

    return await response.json();
  } catch (error) {
    console.error(`[API Error] Falha na rota ${method} ${endpoint}:`, error.message);
    throw error;
  }
}

// ----------------------------------------------------
// SERVIÇOS ESPECÍFICOS DA APLICAÇÃO
// ----------------------------------------------------

const ApiService = {
  // 1. BEBÊ & PERFIL
  bebe: {
    obterPerfil: (id = 1) => requestAPI(`/bebes/${id}`),
    atualizarPerfil: (id, dados) => requestAPI(`/bebes/${id}`, "PUT", dados),
    obterResumoDashboard: (id = 1) => requestAPI(`/bebes/${id}/dashboard`)
  },

  // 2. ROTINA (Alimentação, Trocas, Sono)
  rotina: {
    listarPorData: (bebeId = 1, dataIso) => requestAPI(`/rotina?bebeId=${bebeId}&data=${dataIso}`),
    registrarAtividade: (dados) => requestAPI("/rotina", "POST", dados),
    excluirAtividade: (id) => requestAPI(`/rotina/${id}`, "DELETE")
  },

  // 3. SAÚDE & VACINAS
  saude: {
    obterBiometria: (bebeId = 1) => requestAPI(`/saude/biometria?bebeId=${bebeId}`),
    listarVacinas: (bebeId = 1) => requestAPI(`/saude/vacinas?bebeId=${bebeId}`),
    registrarBiometria: (dados) => requestAPI("/saude/biometria", "POST", dados),
    marcarVacinaAplicada: (idVacina) => requestAPI(`/saude/vacinas/${idVacina}/aplicar`, "PATCH")
  },

  // 4. DIÁRIO & NOTAS
  notas: {
    listar: (bebeId = 1) => requestAPI(`/notas?bebeId=${bebeId}`),
    salvar: (dados) => requestAPI("/notas", "POST", dados),
    excluir: (id) => requestAPI(`/notas/${id}`, "DELETE")
  },

  // 5. INTELIGÊNCIA ARTIFICIAL (Scikit-Learn)
  ia: {
    /**
     * Solicita a estimativa da próxima janela de sono ao backend
     * @param {object} parametros - { idade_dias, tempo_acordado_min, total_leite_ml, ultima_soneca_min, humor_cod }
     */
    preverProximoSono: (parametros) => requestAPI("/ia/prever-sono", "POST", parametros)
  }
};

// Torna o serviço acessível globalmente nas outras páginas/scripts
window.ApiService = ApiService;