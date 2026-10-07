/**
 * saude.js - Gerenciador de Saúde, Biometria e Vacinas
 * Projeto Integrador: Babá Premium
 */

const STORAGE_KEY_SAUDE = "baba_premium_saude";

// Dados iniciais de fallback / demonstração
const dadosSaudePadrao = {
  biometria: {
    peso: "7,450 kg",
    comprimento: "65,5 cm",
    perimetroCefalico: "42,0 cm",
    dataConsulta: "12/Nov - 14:30",
    medico: "Dra. Camila - Pediatra"
  },
  vacinas: [
    { id: 1, nome: "BCG & Hepatite B", dose: "Ao nascer", local: "Maternidade", aplicada: true },
    { id: 2, nome: "Pentavalente & Rotavírus (1ª Dose)", dose: "2 meses", local: "UBS Central", aplicada: true },
    { id: 3, nome: "Meningocócica C (1ª Dose)", dose: "3 meses", local: "UBS Central", aplicada: true },
    { id: 4, nome: "Pentavalente, VIP & Rotavírus (2ª Dose)", dose: "4 meses", local: "Prevista p/ 28/Nov", aplicada: false },
    { id: 5, nome: "Meningocócica C (2ª Dose)", dose: "5 meses", local: "Prevista p/ Dezembro", aplicada: false }
  ],
  medicamentos: [
    { id: 1, nome: "Vitamina D (Ad-til / Colecalciferol)", posologia: "2 gotas ao dia pela manhã", tipo: "Uso contínuo" }
  ]
};

function obterDadosSaude() {
  const salvo = localStorage.getItem(STORAGE_KEY_SAUDE);
  if (!salvo) {
    localStorage.setItem(STORAGE_KEY_SAUDE, JSON.stringify(dadosSaudePadrao));
    return dadosSaudePadrao;
  }
  return JSON.parse(salvo);
}

function salvarDadosSaude(dados) {
  localStorage.setItem(STORAGE_KEY_SAUDE, JSON.stringify(dados));
}

// Alternar status da vacina (Aplicada <-> Pendente)
function alternarStatusVacina(id) {
  const dados = obterDadosSaude();
  const vacina = dados.vacinas.find(v => v.id === id);
  if (vacina) {
    vacina.aplicada = !vacina.aplicada;
    salvarDadosSaude(dados);
    renderizarVacinas();

    // Notifica backend se api.js estiver ativo
    if (window.ApiService && window.ApiService.saude) {
      window.ApiService.saude.marcarVacinaAplicada(id).catch(() => {});
    }
  }
}

// Renderiza a lista de vacinas na tela
function renderizarVacinas() {
  const container = document.getElementById("lista-vacinas");
  if (!container) return;

  const dados = obterDadosSaude();
  container.innerHTML = "";

  dados.vacinas.forEach(v => {
    const item = document.createElement("div");
    item.className = "vaccine-item";
    item.innerHTML = `
      <div>
        <strong>${v.nome}</strong>
        <p style="font-size: 0.8rem; color: var(--text-muted); margin-top: 2px;">${v.dose} • ${v.local}</p>
      </div>
      <button 
        type="button"
        class="${v.aplicada ? 'badge-applied' : 'badge-pending'}" 
        style="border: none; cursor: pointer;"
        onclick="alternarStatusVacina(${v.id})"
        title="Clique para alternar status">
        ${v.aplicada ? "✓ Aplicada" : "⏳ Pendente"}
      </button>
    `;
    container.appendChild(item);
  });
}

// Atualizar biometria
function atualizarBiometria(peso, altura) {
  const dados = obterDadosSaude();
  dados.biometria.peso = `${peso} kg`;
  dados.biometria.comprimento = `${altura} cm`;
  salvarDadosSaude(dados);

  if (window.ApiService && window.ApiService.saude) {
    window.ApiService.saude.registrarBiometria({ bebeId: 1, peso, altura }).catch(() => {});
  }
}

document.addEventListener("DOMContentLoaded", () => {
  renderizarVacinas();
});