package com.eventastic.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

// Classe que representa um evento
public final class Event {

    // Todos os campos (exceto idEvento) podem ser editados
    private final int idEvento;
    private String nome;
    private String descricao;
    private String local;
    private LocalDate dataInicioEvento;
    private LocalDate dataFimEvento;
    private LocalTime horaInicioEvento;
    private LocalTime horaFimEvento;
    private int maxParticipantes;
    private LocalDate dataInicioInscricoes;
    private LocalDate dataFimInscricoes;
    private boolean active = true;
    private List<FaseInscricao> fases;
    private List<OpcaoAdicional> opcoes;

    // Construtor
    public Event(int idEvento, String nome, String descricao, String local,
          LocalDate dataInicioEvento, LocalDate dataFimEvento,
          LocalTime horaInicioEvento, LocalTime horaFimEvento,
          int maxParticipantes, LocalDate dataInicioInscricoes,
          LocalDate dataFimInscricoes, List<FaseInscricao> fases,
          List<OpcaoAdicional> opcoes) {

        this.idEvento = idEvento;
        this.nome = requireNonBlank(nome, "nome");
        this.descricao = requireNonBlank(descricao, "descricao");
        this.local = requireNonBlank(local, "local");
        this.dataInicioEvento = Objects.requireNonNull(dataInicioEvento, "dataInicioEvento");
        this.dataFimEvento = Objects.requireNonNull(dataFimEvento, "dataFimEvento");
        this.horaInicioEvento = Objects.requireNonNull(horaInicioEvento, "horaInicioEvento");
        this.horaFimEvento = Objects.requireNonNull(horaFimEvento, "horaFimEvento");
        this.maxParticipantes = maxParticipantes;
        this.dataInicioInscricoes = Objects.requireNonNull(dataInicioInscricoes, "dataInicioInscricoes");
        this.dataFimInscricoes = Objects.requireNonNull(dataFimInscricoes, "dataFimInscricoes");
        this.fases = Objects.requireNonNull(fases, "fases");
        this.opcoes = Objects.requireNonNull(opcoes, "opcoes");
    }

    // Getters e Setters
    public int getIdEvento() { return idEvento; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public String getLocal() { return local; }
    public LocalDate getDataInicioEvento() { return dataInicioEvento; }
    public LocalDate getDataFimEvento() { return dataFimEvento; }
    public LocalTime getHoraInicioEvento() { return horaInicioEvento; }
    public LocalTime getHoraFimEvento() { return horaFimEvento; }
    public int getMaxParticipantes() { return maxParticipantes; }
    public LocalDate getDataInicioInscricoes() { return dataInicioInscricoes; }
    public LocalDate getDataFimInscricoes() { return dataFimInscricoes; }
    public List<FaseInscricao> getFases() { return List.copyOf(fases); }
    public List<OpcaoAdicional> getOpcoes() { return List.copyOf(opcoes); }
    public boolean getActive() { return active; }

    public void setNome(String nome){this.nome = requireNonBlank(nome, "nome");}
    public void setDescricao(String descricao){this.descricao = requireNonBlank(descricao, "descricao");}
    public void setLocal(String local){this.local = requireNonBlank(local, "local");}
    public void setDataInicioEvento(LocalDate dataInicioEvento) {this.dataInicioEvento = Objects.requireNonNull(dataInicioEvento, "dataInicioEvento");}
    public void setDataFimEvento(LocalDate dataFimEvento) {this.dataFimEvento = Objects.requireNonNull(dataFimEvento, "dataFimEvento");}
    public void setHoraInicioEvento(LocalTime horaInicioEvento) {this.horaInicioEvento = Objects.requireNonNull(horaInicioEvento, "horaInicioEvento");}
    public void setHoraFimEvento(LocalTime horaFimEvento) {this.horaFimEvento = Objects.requireNonNull(horaFimEvento, "horaFimEvento");}
    public void setMaxParticipantes(int maxParticipantes){this.maxParticipantes = maxParticipantes;}
    public void setDataInicioInscricoes(LocalDate dataInicioInscricoes) {this.dataInicioInscricoes = Objects.requireNonNull(dataInicioInscricoes, "dataInicioInscricoes");}
    public void setDataFimInscricoes(LocalDate dataFimInscricoes) {this.dataFimInscricoes = Objects.requireNonNull(dataFimInscricoes, "dataFimInscricoes");}
    public void setFases(List<FaseInscricao> fases) {this.fases = List.copyOf(fases);}
    public void setOpcoes(List<OpcaoAdicional> opcoes) {this.opcoes = List.copyOf(opcoes);}
    public void setActive(boolean active) {this.active = active;}

    @Override
    public String toString() {
        return "Event{id=" + idEvento + ", nome='" + nome + "'}";
    }

    // Método auxiliar para validar strings não vazias
    private static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " não pode ser vazio");
        }
        return value;
    }
    
}
