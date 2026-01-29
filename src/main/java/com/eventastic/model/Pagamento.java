package com.eventastic.model;

import com.eventastic.enums.EstadoPagamento;

import java.time.LocalDateTime;
import java.util.Objects;

// Classe que representa o pagamento de uma inscrição
public final class Pagamento {

    private final int idInscricao;
    private float valorTransferido;
    private LocalDateTime dataTransferencia;
    private String notasInternas;
    private EstadoPagamento estado;

    // Construtor
    public Pagamento(int idInscricao) {
        this.idInscricao = idInscricao;
        this.valorTransferido = 0.00f;
        this.dataTransferencia = null;
        this.notasInternas = null;
        this.estado = EstadoPagamento.PENDENTE;
    }

    // Getters e Setters
    public int getIdInscricao() { return idInscricao; }
    public float getValorTransferido() { return valorTransferido; }
    public LocalDateTime getDataTransferencia() { return dataTransferencia; }
    public String getNotasInternas() { return notasInternas; }
    public EstadoPagamento getEstado() { return estado; }

    public void setValorTransferido(float valorTransferido) {
        if (valorTransferido < 0) {
            throw new IllegalArgumentException("Valor transferido não pode ser negativo");
        }
        this.valorTransferido = valorTransferido;
    }

    public void setDataTransferencia(LocalDateTime dataTransferencia) {
        this.dataTransferencia = dataTransferencia;
    }

    public void setNotasInternas(String notasInternas) {
        this.notasInternas = notasInternas;
    }

    public void setEstado(EstadoPagamento estado) {
        this.estado = Objects.requireNonNull(estado, "estado não pode ser nulo");
    }

    @Override
    public String toString() {
        return "Pagamento{inscricao=" + idInscricao + ", estado=" + estado + "}";
    }
}
