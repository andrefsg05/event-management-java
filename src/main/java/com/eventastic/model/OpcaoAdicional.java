package com.eventastic.model;

import java.util.Objects;

// Classe que representa uma opção adicional para um evento
public final class OpcaoAdicional {

    private final String nome;
    private final String descricao;
    private final float preco;
    private final boolean obrigatoria;

    // Construtor
    public OpcaoAdicional(String nome, String descricao, float preco, boolean obrigatoria) {
        if (preco <= 0) {
            throw new IllegalArgumentException("Preço deve ser positivo");
        }
        this.nome = requireNonBlank(nome, "nome");
        this.descricao = requireNonBlank(descricao, "descricao");
        this.preco = preco;
        this.obrigatoria = obrigatoria;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public float getPreco() {
        return preco;
    }

    public boolean isObrigatoria() {
        return obrigatoria;
    }

    @Override
    public String toString() {
        return "OpcaoAdicional{" + nome + ", " + preco + "€" + (obrigatoria ? ", obrigatória" : "") + "}";
    }

    // Método auxiliar para validar strings não vazias
    private static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " não pode ser vazio");
        }
        return value;
    }
}
