package com.eventastic.api;

import com.eventastic.model.Event;
import com.eventastic.model.FaseInscricao;
import com.eventastic.model.Inscricao;
import com.eventastic.model.OpcaoAdicional;
import com.eventastic.enums.TipoInscricao;
import com.eventastic.service.EventService;
import com.eventastic.service.InscricaoService;
import com.eventastic.service.PagamentoService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;

/**
 * EventasticAPI - API de acesso público à biblioteca Eventastic
 * 
 * Classe que agrega todos os métodos públicos dos serviços para facilitar
 * a utilização da biblioteca por aplicações externas.
 */
public class EventasticAPI {

    private final EventService eventService;
    private final InscricaoService inscricaoService;
    private final PagamentoService pagamentoService;

    /**
     * Construtor que inicializa todos os serviços internos
     */
    public EventasticAPI() {
        this.eventService = new EventService();
        this.inscricaoService = new InscricaoService();
        this.pagamentoService = new PagamentoService(inscricaoService);
    }

    // ============= MÉTODOS DE EVENTOS =============

    /**
     * Cria um novo evento
     */
    public Event criarEvento(String nome, String descricao, String local,
                             LocalDate dataInicioEvento, LocalDate dataFimEvento,
                             LocalTime horaInicioEvento, LocalTime horaFimEvento,
                             int maxParticipantes, List<FaseInscricao> fases,
                             List<OpcaoAdicional> opcoes) {
        return eventService.criarEvento(nome, descricao, local, dataInicioEvento, 
                dataFimEvento, horaInicioEvento, horaFimEvento, maxParticipantes, fases, opcoes);
    }

    /**
     * Edita um evento existente
     */
    public void editarEvento(int idEvento, String nome, String descricao, String local,
                             LocalDate dataInicioEvento, LocalDate dataFimEvento,
                             LocalTime horaInicioEvento, LocalTime horaFimEvento,
                             int maxParticipantes, List<FaseInscricao> fases,
                             List<OpcaoAdicional> opcoes) {
        eventService.editarEvento(idEvento, nome, descricao, local, dataInicioEvento,
                dataFimEvento, horaInicioEvento, horaFimEvento, maxParticipantes, fases, opcoes);
    }

    /**
     * Inativa um evento
     */
    public void inativarEvento(int idEvento) {
        eventService.inativarEvento(idEvento, inscricaoService);
    }

    /**
     * Obtém a lista de todos os eventos ativos
     */
    public List<Event> obterListaEventos() {
        return eventService.obterListaEventos(inscricaoService);
    }

    /**
     * Obtém lista de eventos disponíveis para inscrição
     * (ativos, com inscrições abertas e com vagas)
     */
    public List<Event> consultarEventosDisponiveis() {
        return eventService.consultarEventosDisponiveis(inscricaoService);
    }

    /**
     * Exibe detalhes completos de um evento
     */
    public void detalhesEvento(int idEvento) {
        eventService.detalhesEvento(idEvento);
    }

    /**
     * Procura um evento ativo por ID
     */
    public Event procurarEvento(int idEvento) {
        return eventService.findEventoById(idEvento, inscricaoService);
    }

    // ============= MÉTODOS DE INSCRIÇÕES =============

    /**
     * Cria uma nova inscrição
     */
    public Inscricao inscrever(Event evento, String nome, String email, Integer nif,
                               TipoInscricao tipoInscricao, Integer numAluno,
                               List<OpcaoAdicional> opcoesEscolhidas) {
        return inscricaoService.inscrever(evento, nome, email, nif, tipoInscricao, 
                numAluno, opcoesEscolhidas);
    }

    /**
     * Lista todas as inscrições
     */
    public List<Inscricao> listarInscricoes() {
        return inscricaoService.listarInscricoes();
    }

    /**
     * Consulta e exibe detalhes de uma inscrição
     */
    public void consultarInscricao(int idInscricao, String email) {
        inscricaoService.consultarInscricao(idInscricao, email, eventService);
    }

    /**
     * Obtém lista de participantes de um evento
     */
    public List<Inscricao> obterListaParticipantes(int idEvento) {
        return inscricaoService.obterListaParticipantes(idEvento);
    }

    /**
     * Procura participantes de um evento por critérios
     */
    public List<Inscricao> procurarParticipante(Event evento, String nome, 
                                                 String email, Integer idInscricao) {
        return inscricaoService.procurarParticipante(evento, nome, email, idInscricao);
    }

    /**
     * Exporta participantes para ficheiro CSV
     */
    public void exportarParticipantesParaCSV(int idEvento, String caminhoFicheiro) throws IOException {
        inscricaoService.exportarParticipantesParaCSV(idEvento, caminhoFicheiro);
    }

    // ============= MÉTODOS DE PAGAMENTOS =============

    /** 
     * Consulta detalhes de pagamento de uma inscrição 
     */
    public void consultarPagamento(int idInscricao) {
        pagamentoService.consultarPagamento(idInscricao);
    }

    /** 
     * Regista/atualiza os dados de um pagamento 
     */
    public void registarPagamento(int idInscricao, float valorTransferido, LocalDateTime dataTransferencia, String notasInternas) {
        pagamentoService.registarPagamento(idInscricao, valorTransferido, dataTransferencia, notasInternas);
    }
}
