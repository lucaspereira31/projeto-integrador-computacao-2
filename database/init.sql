-- 1. TIPOS ENUM
CREATE TYPE tipo_usuario_enum AS ENUM ('pai', 'mae', 'responsavel', 'baba');
CREATE TYPE sexo_enum AS ENUM ('M', 'F', 'outro');
CREATE TYPE papel_usuario_bebe_enum AS ENUM ('proprietario', 'cuidador_autorizado', 'baba');
CREATE TYPE permissoes_enum AS ENUM ('leitura_escrita', 'apenas_leitura');
CREATE TYPE categoria_rotina_enum AS ENUM ('medicamento', 'alimentacao', 'sono', 'higiene', 'outro');
CREATE TYPE qualidade_sono_enum AS ENUM ('tranquilo', 'agitado', 'interrompido');
CREATE TYPE lado_peito_enum AS ENUM ('esquerdo', 'direito', 'ambos');
CREATE TYPE tipo_conteudo_mamadeira_enum AS ENUM ('leite_materno', 'formula', 'agua', 'cha', 'suco');
CREATE TYPE aceitacao_solida_enum AS ENUM ('excelente', 'boa', 'regular', 'recusou');
CREATE TYPE tipo_fralda_enum AS ENUM ('xixi', 'coco', 'misto', 'limpa');
CREATE TYPE consistencia_coco_enum AS ENUM ('liquido', 'pastoso', 'duro', 'normal');
CREATE TYPE humor_enum AS ENUM ('muito_feliz', 'feliz', 'calmo', 'choroso', 'irritado', 'sonolento');
CREATE TYPE tipo_higiene_enum AS ENUM ('banho', 'lavagem_nariz', 'corte_unhas', 'escovacao_dentes', 'troca_roupa', 'outro');
CREATE TYPE status_denticao_enum AS ENUM ('nascendo', 'erupcionado', 'caiu');
CREATE TYPE tipo_midia_enum AS ENUM ('foto', 'audio', 'video');

-- 2. FUNÇÃO E TRIGGER PARA DATA DE ATUALIZAÇÃO
CREATE OR REPLACE FUNCTION atualiza_timestamp_modificado()
RETURNS TRIGGER AS $$
BEGIN
    NEW.atualizado_em = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 3. USUÁRIOS
CREATE TABLE IF NOT EXISTS usuarios (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    telefone VARCHAR(20) NULL,
    tipo_usuario tipo_usuario_enum NOT NULL DEFAULT 'responsavel',
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER trg_usuarios_atualizado_em
    BEFORE UPDATE ON usuarios
    FOR EACH ROW
    EXECUTE FUNCTION atualiza_timestamp_modificado();

-- 4. BEBÊS
CREATE TABLE IF NOT EXISTS bebes (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    data_nascimento DATE NOT NULL,
    sexo sexo_enum NOT NULL,
    peso_nascimento_kg NUMERIC(5,3) NULL,
    altura_nascimento_cm NUMERIC(4,1) NULL,
    foto_perfil_url VARCHAR(255) NULL,
    criado_em TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- 5. VÍNCULO USUÁRIO x BEBÊ
CREATE TABLE IF NOT EXISTS usuario_bebe (
    id SERIAL PRIMARY KEY,
    usuario_id INT NOT NULL,
    bebe_id INT NOT NULL,
    papel papel_usuario_bebe_enum NOT NULL DEFAULT 'baba',
    permissoes permissoes_enum NOT NULL DEFAULT 'leitura_escrita',
    vinculado_em TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ub_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_ub_bebe FOREIGN KEY (bebe_id) REFERENCES bebes(id) ON DELETE CASCADE,
    CONSTRAINT uk_usuario_bebe UNIQUE (usuario_id, bebe_id)
);

-- 6. ROTINA E LEMBRETES
CREATE TABLE IF NOT EXISTS rotina_lembretes (
    id SERIAL PRIMARY KEY,
    bebe_id INT NOT NULL,
    criado_por_usuario_id INT NOT NULL,
    titulo VARCHAR(100) NOT NULL,
    categoria categoria_rotina_enum NOT NULL,
    horario_previsto TIME NOT NULL,
    dias_semana VARCHAR(20) DEFAULT '1,2,3,4,5,6,7',
    ativo BOOLEAN DEFAULT TRUE,
    CONSTRAINT fk_rotina_bebe FOREIGN KEY (bebe_id) REFERENCES bebes(id) ON DELETE CASCADE,
    CONSTRAINT fk_rotina_usuario FOREIGN KEY (criado_por_usuario_id) REFERENCES usuarios(id)
);

-- 7. REGISTRO DE SONO
CREATE TABLE IF NOT EXISTS registro_sono (
    id SERIAL PRIMARY KEY,
    bebe_id INT NOT NULL,
    usuario_id INT NOT NULL,
    data_hora_inicio TIMESTAMPTZ NOT NULL,
    data_hora_fim TIMESTAMPTZ NOT NULL,
    qualidade qualidade_sono_enum DEFAULT 'tranquilo',
    local VARCHAR(60) NULL,
    observacoes TEXT NULL,
    CONSTRAINT fk_sono_bebe FOREIGN KEY (bebe_id) REFERENCES bebes(id) ON DELETE CASCADE,
    CONSTRAINT fk_sono_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- 8. ALIMENTAÇÃO: PEITO
CREATE TABLE IF NOT EXISTS registro_alimentacao_peito (
    id SERIAL PRIMARY KEY,
    bebe_id INT NOT NULL,
    usuario_id INT NOT NULL,
    data_hora_inicio TIMESTAMPTZ NOT NULL,
    duracao_esq_minutos SMALLINT DEFAULT 0,
    duracao_dir_minutos SMALLINT DEFAULT 0,
    lado_final lado_peito_enum NULL,
    observacoes TEXT NULL,
    CONSTRAINT fk_peito_bebe FOREIGN KEY (bebe_id) REFERENCES bebes(id) ON DELETE CASCADE,
    CONSTRAINT fk_peito_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- 9. ALIMENTAÇÃO: MAMADEIRA
CREATE TABLE IF NOT EXISTS registro_alimentacao_mamadeira (
    id SERIAL PRIMARY KEY,
    bebe_id INT NOT NULL,
    usuario_id INT NOT NULL,
    data_hora TIMESTAMPTZ NOT NULL,
    tipo_conteudo tipo_conteudo_mamadeira_enum NOT NULL,
    quantidade_ml NUMERIC(5,1) NOT NULL,
    observacoes TEXT NULL,
    CONSTRAINT fk_mamadeira_bebe FOREIGN KEY (bebe_id) REFERENCES bebes(id) ON DELETE CASCADE,
    CONSTRAINT fk_mamadeira_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- 10. ALIMENTAÇÃO: SÓLIDOS
CREATE TABLE IF NOT EXISTS registro_alimentacao_solida (
    id SERIAL PRIMARY KEY,
    bebe_id INT NOT NULL,
    usuario_id INT NOT NULL,
    data_hora TIMESTAMPTZ NOT NULL,
    descricao_alimento VARCHAR(255) NOT NULL,
    quantidade_gramas NUMERIC(5,1) NULL,
    aceitacao aceitacao_solida_enum DEFAULT 'boa',
    reacao_alergica BOOLEAN DEFAULT FALSE,
    observacoes TEXT NULL,
    CONSTRAINT fk_solida_bebe FOREIGN KEY (bebe_id) REFERENCES bebes(id) ON DELETE CASCADE,
    CONSTRAINT fk_solida_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- 11. EXTRAÇÃO DE LEITE
CREATE TABLE IF NOT EXISTS registro_extracao_leite (
    id SERIAL PRIMARY KEY,
    bebe_id INT NOT NULL,
    usuario_id INT NOT NULL,
    data_hora TIMESTAMPTZ NOT NULL,
    lado lado_peito_enum NOT NULL,
    quantidade_ml NUMERIC(5,1) NOT NULL,
    duracao_minutos SMALLINT NULL,
    observacoes TEXT NULL,
    CONSTRAINT fk_extracao_bebe FOREIGN KEY (bebe_id) REFERENCES bebes(id) ON DELETE CASCADE,
    CONSTRAINT fk_extracao_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- 12. FRALDA
CREATE TABLE IF NOT EXISTS registro_fralda (
    id SERIAL PRIMARY KEY,
    bebe_id INT NOT NULL,
    usuario_id INT NOT NULL,
    data_hora TIMESTAMPTZ NOT NULL,
    tipo tipo_fralda_enum NOT NULL,
    consistencia_coco consistencia_coco_enum NULL,
    cor_coco VARCHAR(40) NULL,
    houve_vazamento BOOLEAN DEFAULT FALSE,
    observacoes TEXT NULL,
    CONSTRAINT fk_fralda_bebe FOREIGN KEY (bebe_id) REFERENCES bebes(id) ON DELETE CASCADE,
    CONSTRAINT fk_fralda_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- 13. HUMOR
CREATE TABLE IF NOT EXISTS registro_humor (
    id SERIAL PRIMARY KEY,
    bebe_id INT NOT NULL,
    usuario_id INT NOT NULL,
    data_hora TIMESTAMPTZ NOT NULL,
    humor humor_enum NOT NULL,
    observacoes TEXT NULL,
    CONSTRAINT fk_humor_bebe FOREIGN KEY (bebe_id) REFERENCES bebes(id) ON DELETE CASCADE,
    CONSTRAINT fk_humor_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- 14. HIGIENE
CREATE TABLE IF NOT EXISTS registro_higiene (
    id SERIAL PRIMARY KEY,
    bebe_id INT NOT NULL,
    usuario_id INT NOT NULL,
    data_hora TIMESTAMPTZ NOT NULL,
    tipo tipo_higiene_enum NOT NULL,
    observacoes TEXT NULL,
    CONSTRAINT fk_higiene_bebe FOREIGN KEY (bebe_id) REFERENCES bebes(id) ON DELETE CASCADE,
    CONSTRAINT fk_higiene_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- 15. SAÚDE & SINTOMAS
CREATE TABLE IF NOT EXISTS registro_saude (
    id SERIAL PRIMARY KEY,
    bebe_id INT NOT NULL,
    usuario_id INT NOT NULL,
    data_hora TIMESTAMPTZ NOT NULL,
    temperatura_celsius NUMERIC(3,1) NULL,
    sintomas VARCHAR(255) NULL,
    observacoes TEXT NULL,
    CONSTRAINT fk_saude_bebe FOREIGN KEY (bebe_id) REFERENCES bebes(id) ON DELETE CASCADE,
    CONSTRAINT fk_saude_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- 16. MEDICAMENTOS
CREATE TABLE IF NOT EXISTS registro_medicamento (
    id SERIAL PRIMARY KEY,
    bebe_id INT NOT NULL,
    usuario_id INT NOT NULL,
    data_hora TIMESTAMPTZ NOT NULL,
    nome_medicamento VARCHAR(120) NOT NULL,
    dosagem VARCHAR(50) NOT NULL,
    motivo VARCHAR(150) NULL,
    observacoes TEXT NULL,
    CONSTRAINT fk_med_bebe FOREIGN KEY (bebe_id) REFERENCES bebes(id) ON DELETE CASCADE,
    CONSTRAINT fk_med_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- 17. VACINAS
CREATE TABLE IF NOT EXISTS registro_vacina (
    id SERIAL PRIMARY KEY,
    bebe_id INT NOT NULL,
    usuario_id INT NOT NULL,
    nome_vacina VARCHAR(120) NOT NULL,
    dose VARCHAR(30) NOT NULL,
    data_aplicacao DATE NOT NULL,
    data_proxima_dose DATE NULL,
    lote VARCHAR(50) NULL,
    local_aplicacao VARCHAR(100) NULL,
    CONSTRAINT fk_vacina_bebe FOREIGN KEY (bebe_id) REFERENCES bebes(id) ON DELETE CASCADE,
    CONSTRAINT fk_vacina_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- 18. DENTIÇÃO
CREATE TABLE IF NOT EXISTS registro_denticao (
    id SERIAL PRIMARY KEY,
    bebe_id INT NOT NULL,
    usuario_id INT NOT NULL,
    identificador_dente VARCHAR(40) NOT NULL,
    status status_denticao_enum NOT NULL,
    data_registro DATE NOT NULL,
    observacoes TEXT NULL,
    CONSTRAINT fk_dente_bebe FOREIGN KEY (bebe_id) REFERENCES bebes(id) ON DELETE CASCADE,
    CONSTRAINT fk_dente_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- 19. CRESCIMENTO & ESTATÍSTICAS
CREATE TABLE IF NOT EXISTS registro_crescimento (
    id SERIAL PRIMARY KEY,
    bebe_id INT NOT NULL,
    usuario_id INT NOT NULL,
    data_medicao DATE NOT NULL,
    peso_kg NUMERIC(5,3) NOT NULL,
    altura_cm NUMERIC(4,1) NOT NULL,
    perimetro_cefalico_cm NUMERIC(4,1) NULL,
    observacoes TEXT NULL,
    CONSTRAINT fk_cresc_bebe FOREIGN KEY (bebe_id) REFERENCES bebes(id) ON DELETE CASCADE,
    CONSTRAINT fk_cresc_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- 20. DIÁRIO DE NOTAS
CREATE TABLE IF NOT EXISTS diario_notas (
    id SERIAL PRIMARY KEY,
    bebe_id INT NOT NULL,
    usuario_id INT NOT NULL,
    data_hora TIMESTAMPTZ NOT NULL,
    titulo VARCHAR(150) NOT NULL,
    conteudo TEXT NOT NULL,
    CONSTRAINT fk_notas_bebe FOREIGN KEY (bebe_id) REFERENCES bebes(id) ON DELETE CASCADE,
    CONSTRAINT fk_notas_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- 21. FOTOS E GRAVAÇÕES (MÍDIA)
CREATE TABLE IF NOT EXISTS registro_midia (
    id SERIAL PRIMARY KEY,
    bebe_id INT NOT NULL,
    usuario_id INT NOT NULL,
    data_hora TIMESTAMPTZ NOT NULL,
    tipo_midia tipo_midia_enum NOT NULL,
    url_arquivo VARCHAR(255) NOT NULL,
    duracao_segundos INT NULL,
    descricao VARCHAR(255) NULL,
    CONSTRAINT fk_midia_bebe FOREIGN KEY (bebe_id) REFERENCES bebes(id) ON DELETE CASCADE,
    CONSTRAINT fk_midia_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- 22. OUTROS REGISTROS
CREATE TABLE IF NOT EXISTS registro_outros (
    id SERIAL PRIMARY KEY,
    bebe_id INT NOT NULL,
    usuario_id INT NOT NULL,
    data_hora TIMESTAMPTZ NOT NULL,
    categoria VARCHAR(80) NOT NULL,
    descricao TEXT NOT NULL,
    CONSTRAINT fk_outros_bebe FOREIGN KEY (bebe_id) REFERENCES bebes(id) ON DELETE CASCADE,
    CONSTRAINT fk_outros_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- 23. ÍNDICES DE CONSULTA
CREATE INDEX IF NOT EXISTS idx_sono_bebe_data ON registro_sono(bebe_id, data_hora_inicio);
CREATE INDEX IF NOT EXISTS idx_fralda_bebe_data ON registro_fralda(bebe_id, data_hora);
CREATE INDEX IF NOT EXISTS idx_mamadeira_bebe_data ON registro_alimentacao_mamadeira(bebe_id, data_hora);
CREATE INDEX IF NOT EXISTS idx_peito_bebe_data ON registro_alimentacao_peito(bebe_id, data_hora_inicio);
CREATE INDEX IF NOT EXISTS idx_crescimento_bebe_data ON registro_crescimento(bebe_id, data_medicao);