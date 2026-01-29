package com.eventastic.model;

import com.eventastic.enums.TipoInscricao;
import com.eventastic.enums.TipoFase;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

// Classe que representa uma fase de inscrição de um evento
public final class FaseInscricao {

    private final TipoFase tipoFase;
    private final LocalDate dataInicio;
    private final LocalDate dataFim;
    private final Map<TipoInscricao, ConfiguracaoPreco> configuracoes;

    // Construtor
    public FaseInscricao(TipoFase tipoFase, LocalDate dataInicio, LocalDate dataFim,
                         Map<TipoInscricao, ConfiguracaoPreco> configuracoes) {
        this.tipoFase = Objects.requireNonNull(tipoFase, "tipoFase");
        this.dataInicio = Objects.requireNonNull(dataInicio, "dataInicio");
        this.dataFim = Objects.requireNonNull(dataFim, "dataFim");
        
        if (dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("dataInicio deve ser <= dataFim");
        }

        if (configuracoes == null || configuracoes.isEmpty()) {
            throw new IllegalArgumentException("configuracoes nao pode ser vazio");
        }
        
        // Garantir que tem preços para ambos os tipos
        if (!configuracoes.containsKey(TipoInscricao.ESTUDANTE) || 
            !configuracoes.containsKey(TipoInscricao.NAO_ESTUDANTE)) {
            throw new IllegalArgumentException("configuracoes deve ter precos para ESTUDANTE e NAO_ESTUDANTE");
        }

        this.configuracoes = Map.copyOf(configuracoes);
    }

    public TipoFase getTipoFase() {
        return tipoFase;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public float obterPreco(TipoInscricao tipo) {
        ConfiguracaoPreco config = configuracoes.get(tipo);
        if (config == null) {
            throw new IllegalArgumentException("Sem preco configurado para " + tipo);
        }
        return config.getPreco();
    }

    public Map<TipoInscricao, ConfiguracaoPreco> getConfiguracoes() {
        return configuracoes;
    }

    @Override
    public String toString() {
        return "FaseInscricao{" + tipoFase + ", " + dataInicio + " - " + dataFim + "}";
    }
}
