package com.eventastic.model;

import com.eventastic.enums.TipoInscricao;

import java.util.Objects;

// Classe que representa a configuração de preço para um tipo de inscrição
public final class ConfiguracaoPreco {

    private final TipoInscricao tipoInscricao;
    private final float preco;

    // Construtor
    public ConfiguracaoPreco(TipoInscricao tipoInscricao, float preco) {
        this.tipoInscricao = Objects.requireNonNull(tipoInscricao, "tipoInscricao");
        if (preco < 0) {
            throw new IllegalArgumentException("preco nao pode ser negativo");
        }
        this.preco = preco;
    }

    public TipoInscricao getTipoInscricao() {
        return tipoInscricao;
    }

    public float getPreco() {
        return preco;
    }

    @Override
    public String toString() {
        return "ConfiguracaoPreco{" + tipoInscricao + "=" + preco + "}";
    }
}
