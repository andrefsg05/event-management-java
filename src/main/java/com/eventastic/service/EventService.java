package com.eventastic.service;

import com.eventastic.enums.TipoFase;
import com.eventastic.enums.TipoInscricao;
import com.eventastic.model.Event;
import com.eventastic.model.FaseInscricao;
import com.eventastic.model.Inscricao;
import com.eventastic.model.OpcaoAdicional;
import com.eventastic.model.Pagamento;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

// Classe que gerencia eventos
public class EventService {

    // Simulação de base de dados em memória
    private final List<Event> eventos = new ArrayList<>();
    private int nextEventId = 1; // Simula auto-incremento de IDs

    public Event criarEvento(String nome, String descricao, String local,
                              LocalDate dataInicioEvento, LocalDate dataFimEvento,
                              LocalTime horaInicioEvento, LocalTime horaFimEvento,
                              int maxParticipantes, List<FaseInscricao> fases,
                              List<OpcaoAdicional> opcoes) {
        
        validateFases(fases); // Valida as fases
        validateOpcoes(opcoes); // Valida as opções adicionais
        
        // Determina datas de inscrição a partir das fases
        LocalDate dataInicioInscricoes = fases.get(0).getDataInicio();
        LocalDate dataFimInscricoes = fases.get(fases.size() - 1).getDataFim();
        
        // Valida dados do evento
        validateEventoData(nome, descricao, local, dataInicioEvento, dataFimEvento,
                horaInicioEvento, horaFimEvento, maxParticipantes,
                dataInicioInscricoes, dataFimInscricoes);

        Event evento = new Event(nextEventId++, nome, descricao, local, dataInicioEvento, dataFimEvento,
                horaInicioEvento, horaFimEvento, maxParticipantes,
                dataInicioInscricoes, dataFimInscricoes, List.copyOf(fases), List.copyOf(opcoes));
        eventos.add(evento);
        return evento;
    }

    public void editarEvento(int idEvento, String nome, String descricao, String local,
                              LocalDate dataInicioEvento, LocalDate dataFimEvento,
                              LocalTime horaInicioEvento, LocalTime horaFimEvento,
                              int maxParticipantes, List<FaseInscricao> fases,
                              List<OpcaoAdicional> opcoes){

        validateFases(fases); // Valida as fases
        validateOpcoes(opcoes); // Valida as opções adicionais
        
        // Determina datas de inscrição a partir das fases
        LocalDate dataInicioInscricoes = fases.get(0).getDataInicio();
        LocalDate dataFimInscricoes = fases.get(fases.size() - 1).getDataFim();
        
        // Valida dados do evento
        validateEventoData(nome, descricao, local, dataInicioEvento, dataFimEvento,
                horaInicioEvento, horaFimEvento, maxParticipantes,
                dataInicioInscricoes, dataFimInscricoes);

        Event evento = findEventoByIdSimples(idEvento);
        if (evento == null) {
            throw new IllegalArgumentException("Evento com id " + idEvento + " não encontrado.");
        }

        // Impede edição de eventos já iniciados/terminados
        LocalDate hoje = LocalDate.now();
        if (hoje.isAfter(evento.getDataInicioEvento())) {
            throw new IllegalStateException("Não é possível editar um evento que já começou ou terminou.");
        }

        // Atualiza os campos editáveis
        evento.setNome(nome);
        evento.setDescricao(descricao);
        evento.setLocal(local);
        evento.setDataInicioEvento(dataInicioEvento);
        evento.setDataFimEvento(dataFimEvento);
        evento.setHoraInicioEvento(horaInicioEvento);
        evento.setHoraFimEvento(horaFimEvento);
        evento.setMaxParticipantes(maxParticipantes);
        evento.setFases(List.copyOf(fases));
        evento.setOpcoes(List.copyOf(opcoes));
    }

    // Inativa um evento
    public void inativarEvento(int idEvento, InscricaoService inscricaoService) {
        Event evento = findEventoByIdSimples(idEvento);
        if (evento == null) {
            throw new IllegalArgumentException("Evento com id " + idEvento + " não encontrado.");
        }
        
        LocalDate hoje = LocalDate.now();
        
        // Caso 1: Evento ainda não começou - pode ser inativado
        if (hoje.isBefore(evento.getDataInicioEvento())) {
            evento.setActive(false);
            tratarEventoSeInativo(idEvento, inscricaoService);
            return;
        }
        
        // Caso 2: Evento está a decorrer - não pode ser inativado
        if (!hoje.isAfter(evento.getDataFimEvento())) {
            throw new IllegalStateException("Não é possível inativar um evento que está a decorrer.");
        }
        
        // Caso 3: Evento já terminou (expirado) - chamar tratamento
        tratarEventoSeInativo(idEvento, inscricaoService);
    }

    // Obtém a lista de todos os eventos
    public List<Event> obterListaEventos(InscricaoService inscricaoService) {
        LocalDate hoje = LocalDate.now();
        
        // Identificar eventos expirados para processar
        List<Integer> eventosExpirados = eventos.stream()
            .filter(e -> e.getActive() && hoje.isAfter(e.getDataFimEvento()))
            .map(Event::getIdEvento)
            .toList();
        
        // Tratar cada evento expirado
        for (int idEvento : eventosExpirados) {
            tratarEventoSeInativo(idEvento, inscricaoService);
        }
        
        // Retornar lista atualizada de eventos
        return List.copyOf(eventos);
    }

    // Retorna eventos ativos, com inscrições ainda abertas e com vagas disponíveis
    public List<Event> consultarEventosDisponiveis(InscricaoService inscricaoService) {
        LocalDate hoje = LocalDate.now();
        return eventos.stream()
            .filter(e -> e.getActive() && !hoje.isAfter(e.getDataFimInscricoes()))
            .filter(e -> {
                int inscricoesEvento = inscricaoService.obterListaParticipantes(e.getIdEvento()).size();
                return inscricoesEvento < e.getMaxParticipantes();
            })
            .toList();
    }

    // Imprime todos os detalhes de um evento
    public void detalhesEvento(int idEvento) {
        Event evento = findEventoByIdSimples(idEvento);
        if (evento == null) {
            throw new IllegalArgumentException("Evento com id " + idEvento + " não encontrado.");
        }

        System.out.println("========== DETALHES DO EVENTO ==========");
        System.out.println("ID: " + evento.getIdEvento());
        System.out.println("Nome: " + evento.getNome());
        System.out.println("Descrição: " + evento.getDescricao());
        System.out.println("Local: " + evento.getLocal());
        System.out.println("Data do Evento: " + evento.getDataInicioEvento() + " a " + evento.getDataFimEvento());
        System.out.println("Horário: " + evento.getHoraInicioEvento() + " - " + evento.getHoraFimEvento());
        System.out.println("Máximo de Participantes: " + evento.getMaxParticipantes());
        System.out.println("Período de Inscrições: " + evento.getDataInicioInscricoes() + " a " + evento.getDataFimInscricoes());
        System.out.println("Estado: " + (evento.getActive() ? "Ativo" : "Inativo"));
        
        System.out.println("\n--- Fases de Inscrição ---");
        for (FaseInscricao fase : evento.getFases()) {
            System.out.println("  " + fase.getTipoFase() + ": " + fase.getDataInicio() + " a " + fase.getDataFim());
            System.out.println("    Preço Estudante: " + fase.obterPreco(TipoInscricao.ESTUDANTE) + "€");
            System.out.println("    Preço Não Estudante: " + fase.obterPreco(TipoInscricao.NAO_ESTUDANTE) + "€");
        }
        
        System.out.println("\n--- Opções Adicionais ---");
        if (evento.getOpcoes().isEmpty()) {
            System.out.println("  Nenhuma opção adicional disponível");
        } else {
            for (OpcaoAdicional opcao : evento.getOpcoes()) {
                System.out.println("  " + opcao.getNome() + " - " + opcao.getPreco() + "€" + 
                    (opcao.isObrigatoria() ? " (Obrigatória)" : " (Opcional)"));
                System.out.println("    " + opcao.getDescricao());
            }
        }
        System.out.println("========================================");
    }

    // Valida os dados do evento
    private void validateEventoData(String nome, String descricao, String local,
                                    LocalDate dataInicioEvento, LocalDate dataFimEvento,
                                    LocalTime horaInicioEvento, LocalTime horaFimEvento,
                                    int maxParticipantes, LocalDate dataInicioInscricoes,
                                    LocalDate dataFimInscricoes) {
        if (maxParticipantes <= 0) {
            throw new IllegalArgumentException("maxParticipantes deve ser positivo");
        }
        requireNonBlank(nome, "nome");
        requireNonBlank(descricao, "descricao");
        requireNonBlank(local, "local");

        if (dataInicioEvento == null || dataFimEvento == null || dataInicioInscricoes == null || dataFimInscricoes == null) {
            throw new IllegalArgumentException("datas nao podem ser nulas");
        }
        if (horaInicioEvento == null || horaFimEvento == null) {
            throw new IllegalArgumentException("horas nao podem ser nulas");
        }

        if (dataInicioEvento.isAfter(dataFimEvento)) {
            throw new IllegalArgumentException("dataInicioEvento deve ser <= dataFimEvento");
        }
        if (dataInicioInscricoes.isAfter(dataFimInscricoes)) {
            throw new IllegalArgumentException("dataInicioInscricoes deve ser <= dataFimInscricoes");
        }
        if (dataInicioEvento.equals(dataFimEvento) && !horaInicioEvento.isBefore(horaFimEvento)) {
            throw new IllegalArgumentException("horaInicioEvento deve ser antes de horaFimEvento para eventos de um dia");
        }
    }

    // Valida as fases de inscrição
    private void validateFases(List<FaseInscricao> fases) {
        if (fases == null || fases.isEmpty()) {
            throw new IllegalArgumentException("Fases inválidas");
        }
        if (fases.size() > 3) {
            throw new IllegalArgumentException("Máximo de 3 fases de inscrição");
        }

        // Verificar tipos duplicados
        long distinctTypes = fases.stream()
                .map(FaseInscricao::getTipoFase)
                .distinct()
                .count();
        if (distinctTypes != fases.size()) {
            throw new IllegalArgumentException("Tipos de fase não podem estar duplicados");
        }

        // Verificar ordem cronológica e não-sobreposição
        for (int i = 0; i < fases.size() - 1; i++) {
            FaseInscricao atual = fases.get(i);
            FaseInscricao proxima = fases.get(i + 1);
            
            if (!atual.getDataFim().isBefore(proxima.getDataInicio())) {
                throw new IllegalArgumentException("Fases devem estar ordenadas cronologicamente sem sobreposição");
            }
        }
    }

    // Valida as opções adicionais
    private void validateOpcoes(List<OpcaoAdicional> opcoes) {
        if (opcoes == null) {
            throw new IllegalArgumentException("opcoes nao pode ser nulo");
        }
        // Opcoes pode ser vazio (não é obrigatório ter opções adicionais)
        // Opções duplicadas são permitidas (ex: nomes iguais com preços diferentes)
    }

    /*
     * Procura o evento pelo ID e verifica se expirou para não retornar eventos inválidos.
     * Exposto para permitir consultas externas (em InscricaoService).
     */ 
    public Event findEventoById(int idEvento, InscricaoService inscricaoService) {
        for (Event evento : eventos) {
            if (evento.getIdEvento() == idEvento) {
                // Verificar se o evento expirou
                LocalDate hoje = LocalDate.now();
                if (evento.getActive() && hoje.isAfter(evento.getDataFimEvento())) {
                    tratarEventoSeInativo(idEvento, inscricaoService); // Tratar evento expirado
                    return null;
                }
                return evento;
            }
        }
        return null;
    }

    // Versão sem verificação de expiração
    public Event findEventoByIdSimples(int idEvento) {
        for (Event evento : eventos) {
            if (evento.getIdEvento() == idEvento) {
                return evento;
            }
        }
        return null;
    }

    // Verifica e trata eventos inativos ou expirados
    private boolean tratarEventoSeInativo(int idEvento, InscricaoService inscricaoService) {
        Event evento = findEventoByIdSimples(idEvento);
        
        LocalDate hoje = LocalDate.now();
        boolean expirado = hoje.isAfter(evento.getDataFimEvento());
        boolean ativo = evento.getActive();

        // Caso ativo e ainda no prazo: retorna true (tudo ok)
        if (ativo && !expirado) {
            return true;
        }

        // Caso ativo mas expirado: inativar e limpar tudo
        if (ativo && expirado) {
            evento.setActive(false);
            deleteEvento(idEvento, inscricaoService);
            return false;
        }

        // Caso já inativo (inativação manual pelo admin antes do evento iniciar)
        // 1) Obter todas as inscrições do evento
        List<Inscricao> inscricoesDoEvento = inscricaoService.obterListaParticipantes(idEvento);
        
        // 2) Mostrar pagamentos a reembolsar
        System.out.println("========== REEMBOLSOS A EFETUAR ==========");
        for (Inscricao inscricao : inscricoesDoEvento) {
            Pagamento pagamento = inscricao.getPagamento();
            System.out.println("Inscrição ID: " + inscricao.getId());
            System.out.println("  Participante: " + inscricao.getNome());
            System.out.println("  Email: " + inscricao.getEmail());
            System.out.println("  Valor a Reembolsar: " + pagamento.getValorTransferido() + "€");
            System.out.println("  Estado Pagamento: " + pagamento.getEstado());
            System.out.println("---");
        }
        
        // 3) Notificar inscritos (Simulação)
        System.out.println("Notificando " + inscricoesDoEvento.size() + " participantes sobre cancelamento...");
        for (Inscricao inscricao : inscricoesDoEvento) {
            System.out.println("  [NOTIFICAÇÃO] Email enviado para: " + inscricao.getEmail());
        }
        System.out.println("Notificações enviadas.\n");
        
        // 4) Limpar tudo relacionado ao evento
        deleteEvento(idEvento, inscricaoService);
        
        return false;
    }

    // Apaga evento e todos os dados relacionados
    private void deleteEvento(int idEvento, InscricaoService inscricaoService) {
        // 1. Remover todas as inscrições do evento
        inscricaoService.removerInscricoesDoEvento(idEvento, this);
        
        // 2. Remover o evento da lista
        eventos.removeIf(e -> e.getIdEvento() == idEvento);
        
        System.out.println("Evento " + idEvento + " e todos os dados relacionados foram removidos da memória.");
    }

    // Método auxiliar para validar strings não vazias
    private static void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " não pode ser vazio");
        }
    }
}
