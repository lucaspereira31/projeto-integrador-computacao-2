/**
 * auth.js - Gerenciador de Autenticação e Sessão
 * Projeto Integrador: Babá Premium
 */

const TOKEN_KEY = "baba_premium_token";
const USER_KEY = "baba_premium_user";
const AUTH_API_URL = "http://localhost:8080/api/auth";

const AuthService = {
  /**
   * Realiza login no backend Spring Boot
   * @param {string} email 
   * @param {string} senha 
   * @returns {Promise<object>} Dados do usuário autenticado e token
   */
  async login(email, senha) {
    try {
      const response = await fetch(`${AUTH_API_URL}/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, senha })
      });

      if (!response.ok) {
        const errorMsg = await response.text();
        throw new Error(errorMsg || "Credenciais inválidas");
      }

      const data = await response.json();
      
      // Salva o token JWT e as informações básicas do usuário
      if (data.token) {
        localStorage.setItem(TOKEN_KEY, data.token);
      }
      if (data.usuario) {
        localStorage.setItem(USER_KEY, JSON.stringify(data.usuario));
      }

      return data;
    } catch (error) {
      console.error("[Auth] Erro no login:", error.message);
      throw error;
    }
  },

  /**
   * Cadastra um novo responsável
   * @param {object} dadosCadastro - { nome, email, senha, nomeBebe, dataNascimentoBebe }
   */
  async cadastrar(dadosCadastro) {
    try {
      const response = await fetch(`${AUTH_API_URL}/cadastrar`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(dadosCadastro)
      });

      if (!response.ok) {
        const errorMsg = await response.text();
        throw new Error(errorMsg || "Falha ao realizar cadastro");
      }

      return await response.json();
    } catch (error) {
      console.error("[Auth] Erro no cadastro:", error.message);
      throw error;
    }
  },

  /**
   * Encerra a sessão atual e redireciona para o login
   */
  logout() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    window.location.href = "login.html";
  },

  /**
   * Retorna o token JWT salvo
   * @returns {string|null}
   */
  getToken() {
    return localStorage.getItem(TOKEN_KEY);
  },

  /**
   * Retorna o usuário logado
   * @returns {object|null}
   */
  getUsuarioLogado() {
    const raw = localStorage.getItem(USER_KEY);
    return raw ? JSON.parse(raw) : null;
  },

  /**
   * Verifica se há um token ativo
   * @returns {boolean}
   */
  isAuthenticated() {
    return !!this.getToken();
  },

  /**
   * Protege páginas restritas (chamar no topo de dashboard.html, rotina.html, etc.)
   * @param {string} rotaLogin Caminho da tela de login
   */
  protegerRota(rotaLogin = "login.html") {
    // Permite bypass temporário em desenvolvimento caso queira testar sem backend rodando
    const devMode = false; // Mude para true se quiser desativar a trava no front durante testes visuais

    if (!devMode && !this.isAuthenticated()) {
      window.location.href = rotaLogin;
    }
  }
};

// Disponibiliza globalmente
window.AuthService = AuthService;