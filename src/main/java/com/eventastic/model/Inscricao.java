package com.eventastic.model;

import com.eventastic.enums.EstadoInscricao;
import com.eventastic.enums.TipoInscricao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

// Classe que representa uma inscrição em um evento
public final class Inscricao {

    private static final String IBAN_FIXO = "PT50 1234 4321 12345678901 72";

    // Número de aluno válido entre 58000 e 58999
    private static final int NUMALUNO_MIN = 58000;
    private static final int NUMALUNO_MAX = 58999;

    private final int id;
    private final int idEvento;
    private final String nome;
    private final String email;
    private final Integer nif; // Nif é opcional
    private final TipoInscricao tipoInscricao;
    private final Integer numAluno; // numAluno pode ser null se não for estudante
    private final List<OpcaoAdicional> opcoesEscolhidas;
    private final float valorTotal;
    private final String iban;
    private final String descricaoTransferencia;
    private final EstadoInscricao estado;
    private final LocalDateTime dataCriacao;
    private final Pagamento pagamento; // Cada inscrição tem um único pagamento associado

    // Construtor
    public Inscricao(int id, int idEvento, String nome, String email, Integer nif,
              TipoInscricao tipoInscricao, Integer numAluno,
              List<OpcaoAdicional> opcoesEscolhidas, float precoFase,
              EstadoInscricao estado, LocalDateTime dataCriacao) {
        
        this.id = id;
        this.idEvento = idEvento;
        this.nome = requireNonBlank(nome, "nome");
        this.email = requireNonBlank(email, "email");
        this.nif = nif;
        this.tipoInscricao = Objects.requireNonNull(tipoInscricao, "tipoInscricao");
        
        // Validar número de aluno
        if (tipoInscricao == TipoInscricao.ESTUDANTE) {
            if (numAluno == null) {
                throw new IllegalArgumentException("Número de aluno é obrigatório para inscrições de estudante");
            }
            validarNumeroAluno(numAluno);
            this.numAluno = numAluno;
        } else {
            if (numAluno != null) {
                throw new IllegalArgumentException("Número de aluno só é permitido para inscrições de estudante");
            }
            this.numAluno = null;
        }

        this.opcoesEscolhidas = Objects.requireNonNull(opcoesEscolhidas, "opcoesEscolhidas");
        this.valorTotal = calcularValorTotal(precoFase, opcoesEscolhidas);
        this.iban = IBAN_FIXO;
        this.descricaoTransferencia = gerarDescricaoTransferencia(id, idEvento);
        this.estado = Objects.requireNonNull(estado, "estado");
        this.dataCriacao = Objects.requireNonNull(dataCriacao, "dataCriacao");
        this.pagamento = new Pagamento(id);
    }

    // Getters
    public int getId() { return id; }
    public int getIdEvento() { return idEvento; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public Integer getNif() { return nif; }
    public TipoInscricao getTipoInscricao() { return tipoInscricao; }
    public Integer getNumAluno() { return numAluno; }
    public List<OpcaoAdicional> getOpcoesEscolhidas() { return List.copyOf(opcoesEscolhidas); }
    public float getValorTotal() { return valorTotal; }
    public String getIban() { return iban; }
    public String getDescricaoTransferencia() { return descricaoTransferencia; }
    public EstadoInscricao getEstado() { return estado; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public Pagamento getPagamento() { return pagamento; }

    @Override
    public String toString() {
        return "Inscricao{id=" + id + ", evento=" + idEvento + ", participante=" + nome + "}";
    }

    // Método auxiliar para calcular o valor total da inscrição
    private static float calcularValorTotal(float precoFase, List<OpcaoAdicional> opcoes) {
        float total = precoFase;
        for (OpcaoAdicional opcao : opcoes) {
            total += opcao.getPreco();
        }
        return total;
    }

    // Método auxiliar para gerar a descrição da transferência
    private static String gerarDescricaoTransferencia(int idInscricao, int idEvento) {
        return "Transferência relacionada com a inscrição: " + idInscricao + ", evento: " + idEvento;
    }

    // Método auxiliar para validar número de aluno
    private static void validarNumeroAluno(Integer numAluno) {
        if (numAluno < NUMALUNO_MIN || numAluno > NUMALUNO_MAX) {
            throw new IllegalArgumentException(
                "Número de aluno inválido."
            );
        }
    }

    // Método auxiliar para validar strings não vazias
    private static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " não pode ser vazio");
        }
        return value;
    }
}
