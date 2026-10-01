document.addEventListener("DOMContentLoaded", () => {
  // Ajusta a data do seletor para o dia atual
  const dataInput = document.getElementById("rotina-data-input");
  if (dataInput) {
    dataInput.value = new Date().toISOString().split("T")[0];
  }
});

// Abertura dinâmica do modal conforme o botão clicado
function abrirModal(tipo) {
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
        <input type="number" class="form-control" id="reg-qtd" placeholder="Ex: 150">
      </div>
      <div class="form-group">
        <label>Horário</label>
        <input type="time" class="form-control" id="reg-hora" value="${agora}">
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
        <input type="time" class="form-control" id="reg-hora" value="${agora}">
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
        <input type="time" class="form-control" id="reg-sono-inicio" value="${agora}">
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

// Salva e insere na timeline
function salvarRegistro(e) {
  e.preventDefault();
  // Fecha o modal e recarrega a exibição (ou envia para o backend via fetch/api.js)
  fecharModal();
}