// Variável para rastrear qual modal está ativo no momento
let modalTipoAtual = null;

document.addEventListener("DOMContentLoaded", () => {
  // 1. Ajusta a data do seletor para o dia atual
  const dataInput = document.getElementById("rotina-data-input");
  if (dataInput) {
    const hojeIso = new Date().toISOString().split("T")[0];
    dataInput.value = hojeIso;
    
    // Dispara carregamento das atividades do dia selecionado
    dataInput.addEventListener("change", (e) => carregarAtividadesDoDia(e.target.value));
    carregarAtividadesDoDia(hojeIso);
  }
});

// Abertura dinâmica do modal conforme o botão clicado
function abrirModal(tipo) {
  modalTipoAtual = tipo;
  const modal = document.getElementById("modal-registro");
  const titulo = document.getElementById("modal-titulo");
  const container = document.getElementById("campos-dinamicos");
  const agora = new Date().toTimeString().slice(0, 5);

  container.innerHTML = "";

  if (tipo === "alimentacao") {
    titulo.innerText = "🍼 Registrar Alimentação";
    container.innerHTML = `
      <div class="form-group">
        <label>Tipo de Alimentação</label>
        <select class="form-control" id="reg-tipo-alimento">
          <option value="MAMADEIRA">Mamadeira (Fórmula / Leite)</option>
          <option value="PEITO">Amamentação (Peito)</option>
          <option value="SOLIDO">Papinha / Comida Sólida</option>
        </select>
      </div>
      <div class="form-group">
        <label>Quantidade (ml ou gramas)</label>
        <input type="number" class="form-control" id="reg-qtd" placeholder="Ex: 150" min="0">
      </div>
      <div class="form-group">
        <label>Horário</label>
        <input type="time" class="form-control" id="reg-hora" value="${agora}" required>
      </div>
    `;
  } else if (tipo === "fralda") {
    titulo.innerText = "🧷 Registrar Troca de Fralda";
    container.innerHTML = `
      <div class="form-group">
        <label>Condição da Fralda</label>
        <select class="form-control" id="reg-fralda-tipo">
          <option value="XIXI">Apenas Xixi</option>
          <option value="COCO">Cocô</option>
          <option value="AMBOS">Xixi e Cocô</option>
          <option value="LIMPA">Limpa</option>
        </select>
      </div>
      <div class="form-group">
        <label>Horário da Troca</label>
        <input type="time" class="form-control" id="reg-hora" value="${agora}" required>
      </div>
      <div class="form-group">
        <label>Observação (Opcional)</label>
        <input type="text" class="form-control" id="reg-obs" placeholder="Ex: Vermelhidão leve, pomada passada...">
      </div>
    `;
  } else if (tipo === "sono") {
    titulo.innerText = "💤 Registrar Período de Sono";
    container.innerHTML = `
      <div class="form-group">
        <label>Início do Sono</label>
        <input type="time" class="form-control" id="reg-sono-inicio" value="${agora}" required>
      </div>
      <div class="form-group">
        <label>Fim do Sono (ou deixe em branco se dormindo)</label>
        <input type="time" class="form-control" id="reg-sono-fim">
      </div>
    `;
  }

  modal.classList.add("open");
}

function fecharModal() {
  document.getElementById("modal-registro").classList.remove("open");
  modalTipoAtual = null;
}

// Filtro da Timeline
function filtrarTimeline(categoria, botao) {
  document.querySelectorAll(".filter-tab").forEach(tab => tab.classList.remove("active"));
  botao.classList.add("active");

  const itens = document.querySelectorAll("#lista-atividades .timeline-item");
  itens.forEach(item => {
    if (categoria === "todos" || item.dataset.tipo === categoria) {
      item.style.display = "flex";
    } else {
      item.style.display = "none";
    }
  });
}

// ----------------------------------------------------
// INTEGRAÇÃO COM A API / ENVIO DE DADOS
// ----------------------------------------------------

async function salvarRegistro(e) {
  e.preventDefault();

  const dataSel = document.getElementById("rotina-data-input")?.value || new Date().toISOString().split("T")[0];
  let payload = {
    bebeId: 1,
    dataRegistro: dataSel,
    tipo: modalTipoAtual
  };

  // Coleta dados específicos conforme o tipo aberto
  if (modalTipoAtual === "alimentacao") {
    const subTipo = document.getElementById("reg-tipo-alimento").value;
    const qtd = parseFloat(document.getElementById("reg-qtd").value) || 0;
    const hora = document.getElementById("reg-hora").value;

    payload.detalhe = {
      subTipo: subTipo,
      quantidade: qtd,
      horario: `${dataSel}T${hora}:00`
    };
  } else if (modalTipoAtual === "fralda") {
    const condicao = document.getElementById("reg-fralda-tipo").value;
    const hora = document.getElementById("reg-hora").value;
    const obs = document.getElementById("reg-obs").value.trim();

    payload.detalhe = {
      condicao: condicao,
      horario: `${dataSel}T${hora}:00`,
      observacao: obs
    };
  } else if (modalTipoAtual === "sono") {
    const horaInicio = document.getElementById("reg-sono-inicio").value;
    const horaFim = document.getElementById("reg-sono-fim").value;

    payload.detalhe = {
      inicio: `${dataSel}T${horaInicio}:00`,
      fim: horaFim ? `${dataSel}T${horaFim}:00` : null
    };
  }

  try {
    // Se o ApiService estiver disponível, envia ao Spring Boot
    if (window.ApiService && window.ApiService.rotina) {
      await ApiService.rotina.registrarAtividade(payload);
    }
    
    // Adiciona visualmente na timeline imediatamente
    renderizarItemNaTimeline(payload);
    fecharModal();
  } catch (err) {
    console.warn("Backend indisponível ou erro na requisição:", err);
    // Renderiza em tela mesmo se o backend estiver desligado (fallback offline para testes)
    renderizarItemNaTimeline(payload);
    fecharModal();
  }
}

// Insere o card na lista HTML dinamicamente
function renderizarItemNaTimeline(item) {
  const lista = document.getElementById("lista-atividades");
  if (!lista) return;

  const card = document.createElement("div");
  card.className = "timeline-item";
  card.dataset.tipo = item.tipo;

  if (item.tipo === "alimentacao") {
    const sub = item.detalhe.subTipo === "PEITO" ? "Amamentação (Peito)" : "Mamadeira";
    const hora = (item.detalhe.horario || "").split("T")[1]?.slice(0, 5) || "--:--";
    card.innerHTML = `
      <div class="timeline-dot" style="background: var(--primary);"></div>
      <div style="flex: 1;">
        <div style="display: flex; justify-content: space-between;">
          <strong style="font-size: 0.9rem;">${hora} • ${sub}</strong>
          <span style="font-size: 0.75rem; color: var(--text-muted);">${item.detalhe.quantidade ? item.detalhe.quantidade + ' ml' : ''}</span>
        </div>
        <p style="font-size: 0.8rem; color: var(--text-muted); margin-top: 2px;">Registro de alimentação salvo.</p>
      </div>
    `;
  } else if (item.tipo === "fralda") {
    const hora = (item.detalhe.horario || "").split("T")[1]?.slice(0, 5) || "--:--";
    card.innerHTML = `
      <div class="timeline-dot" style="background: var(--secondary);"></div>
      <div style="flex: 1;">
        <div style="display: flex; justify-content: space-between;">
          <strong style="font-size: 0.9rem;">${hora} • Troca de Fralda</strong>
          <span class="metric-badge">${item.detalhe.condicao}</span>
        </div>
        <p style="font-size: 0.8rem; color: var(--text-muted); margin-top: 2px;">${item.detalhe.observacao || 'Sem observações adicionais.'}</p>
      </div>
    `;
  } else if (item.tipo === "sono") {
    const inicio = (item.detalhe.inicio || "").split("T")[1]?.slice(0, 5) || "--:--";
    const fim = item.detalhe.fim ? (item.detalhe.fim.split("T")[1]?.slice(0, 5)) : "Dormindo";
    card.innerHTML = `
      <div class="timeline-dot" style="background: var(--accent-orange, #f59e0b);"></div>
      <div style="flex: 1;">
        <div style="display: flex; justify-content: space-between;">
          <strong style="font-size: 0.9rem;">${inicio} às ${fim} • Soneca</strong>
          <span style="font-size: 0.75rem; color: var(--text-muted);">Sono</span>
        </div>
        <p style="font-size: 0.8rem; color: var(--text-muted); margin-top: 2px;">Período registrado.</p>
      </div>
    `;
  }

  // Insere no topo da timeline
  lista.prepend(card);
}

// Busca atividades salvas na API para a data informada
async function carregarAtividadesDoDia(dataIso) {
  if (!window.ApiService || !window.ApiService.rotina) return;
  try {
    const itens = await ApiService.rotina.listarPorData(1, dataIso);
    if (Array.isArray(itens) && itens.length > 0) {
      const lista = document.getElementById("lista-atividades");
      lista.innerHTML = "";
      itens.forEach(renderizarItemNaTimeline);
    }
  } catch (e) {
    console.info("Nenhuma atividade encontrada ou backend offline.");
  }
}