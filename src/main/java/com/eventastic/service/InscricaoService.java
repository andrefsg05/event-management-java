package com.eventastic.service;

import com.eventastic.enums.EstadoInscricao;
import com.eventastic.enums.TipoInscricao;
import com.eventastic.model.Event;
import com.eventastic.model.Inscricao;
import com.eventastic.model.OpcaoAdicional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

// Serviço que gerencia inscrições em eventos
public class InscricaoService {

    // Simula uma base de dados em memória
    private final List<Inscricao> inscricoes = new ArrayList<>();
    private int nextInscricaoId = 1; // Simula auto-incremento de IDs

    // Cria uma nova inscrição para um evento
    public Inscricao inscrever(Event evento, String nome, String email, Integer nif,
                               TipoInscricao tipoInscricao, Integer numAluno,
                               List<OpcaoAdicional> opcoesEscolhidas) {
        
        // Validar opções escolhidas
        validateOpcoesEscolhidas(opcoesEscolhidas, evento);
        
        // Validar email + evento (combinação única), não permite inscrição duplicada
        validateEmailEventoUnico(email, evento.getIdEvento());
        
        // Validar se ainda há lugares
        validateLotacao(evento);
        
        // Obter preço da fase atual
        float precoFase = obterPrecoFaseAtual(evento, tipoInscricao);
        
        Inscricao inscricao = new Inscricao(
            nextInscricaoId++,
            evento.getIdEvento(),
            nome,
            email,
            nif,
            tipoInscricao,
            numAluno,
            List.copyOf(opcoesEscolhidas),
            precoFase,
            EstadoInscricao.PENDENTE_PAGAMENTO,
            LocalDateTime.now()
        );
        
        inscricoes.add(inscricao);
        return inscricao;
    }

    // Lista todas as inscrições
    public List<Inscricao> listarInscricoes() {
        return List.copyOf(inscricoes);
    }

    // Consulta e imprime detalhes de uma inscrição (verificando email)
    public void consultarInscricao(int idInscricao, String email, EventService eventService) {
        Inscricao inscricao = inscricoes.stream()
            .filter(i -> i.getId() == idInscricao)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Inscrição com id " + idInscricao + " não encontrada"));

        // Verificar email (segurança)
        if (!inscricao.getEmail().equals(email)) {
            throw new IllegalArgumentException("Email não corresponde à inscrição fornecida");
        }

        Event evento = eventService.findEventoById(inscricao.getIdEvento(), this);
        String nomeEvento = (evento != null) ? evento.getNome() : "(Evento não encontrado)";

        System.out.println("========== DETALHES DA INSCRIÇÃO ==========");
        System.out.println("ID Inscrição: " + inscricao.getId());
        System.out.println("Evento: " + nomeEvento + " (ID=" + inscricao.getIdEvento() + ")");
        System.out.println("Participante: " + inscricao.getNome() + " | Email: " + inscricao.getEmail());
        System.out.println("Tipo de Inscrição: " + inscricao.getTipoInscricao());

        System.out.println("\nOpções adicionais:");
        if (inscricao.getOpcoesEscolhidas().isEmpty()) {
            System.out.println("  Nenhuma");
        } else {
            inscricao.getOpcoesEscolhidas().forEach(op ->
                System.out.println("  - " + op.getNome() + " (" + op.getPreco() + "€)" +
                    (op.isObrigatoria() ? " [Obrigatória]" : ""))
            );
        }

        System.out.println("\nValor Total: " + inscricao.getValorTotal() + "€");
        System.out.println("IBAN: " + inscricao.getIban());
        System.out.println("Descrição da Transferência: " + inscricao.getDescricaoTransferencia());
        System.out.println("Estado: " + inscricao.getEstado());
        System.out.println("Data de Criação: " + inscricao.getDataCriacao());
        System.out.println("===========================================");
    }

    // Obtém todas as inscrições para um evento específico
    public List<Inscricao> obterListaParticipantes(int idEvento) {
        return inscricoes.stream()
            .filter(i -> i.getIdEvento() == idEvento)
            .toList();
    }

    // Procura participantes de um evento por critérios de pesquisa
    public List<Inscricao> procurarParticipante(Event evento, String nome, String email, Integer idInscricao) {
        if (evento == null) {
            throw new IllegalArgumentException("Evento não pode ser nulo");
        }

        return inscricoes.stream()
            .filter(i -> i.getIdEvento() == evento.getIdEvento())
            .filter(i -> nome == null || i.getNome().equalsIgnoreCase(nome))
            .filter(i -> email == null || i.getEmail().equalsIgnoreCase(email))
            .filter(i -> idInscricao == null || i.getId() == idInscricao)
            .toList();
    }

    // Valida as opções adicionais escolhidas para a inscrição
    private void validateOpcoesEscolhidas(List<OpcaoAdicional> opcoesEscolhidas, Event evento) {
        if (opcoesEscolhidas == null) {
            throw new IllegalArgumentException("opcoesEscolhidas não pode ser nulo");
        }
        
        List<OpcaoAdicional> opcoesEvento = evento.getOpcoes();
        
        // Validar que todas as opções escolhidas existem no evento
        for (OpcaoAdicional escolhida : opcoesEscolhidas) {
            boolean existe = opcoesEvento.stream()
                .anyMatch(o -> o.getNome().equals(escolhida.getNome()) && 
                             o.getPreco() == escolhida.getPreco());
            
            if (!existe) {
                throw new IllegalArgumentException("Opção '" + escolhida.getNome() + "' não existe neste evento");
            }
        }
        
        // Validar que todas as opções obrigatórias estão incluídas
        for (OpcaoAdicional opcao : opcoesEvento) {
            if (opcao.isObrigatoria()) {
                boolean incluida = opcoesEscolhidas.stream()
                    .anyMatch(o -> o.getNome().equals(opcao.getNome()) && 
                             o.getPreco() == opcao.getPreco());
                
                if (!incluida) {
                    throw new IllegalArgumentException("Opção obrigatória '" + opcao.getNome() + "' não foi incluída");
                }
            }
        }
    }

    // Valida que o email não tem inscrição duplicada no mesmo evento
    private void validateEmailEventoUnico(String email, int idEvento) {
        boolean existe = inscricoes.stream()
            .anyMatch(i -> i.getIdEvento() == idEvento && 
                          i.getEmail().equals(email));
        
        if (existe) {
            throw new IllegalArgumentException("E-mail '" + email + "' já tem uma inscrição neste evento");
        }
    }

    // Valida se o evento ainda tem lotação disponível
    private void validateLotacao(Event evento) {
        long inscricoesEvento = inscricoes.stream()
            .filter(i -> i.getIdEvento() == evento.getIdEvento())
            .count();
        
        if (inscricoesEvento >= evento.getMaxParticipantes()) {
            throw new IllegalArgumentException("Evento com lotação completa");
        }
    }

    // Obtém o preço da fase de inscrição atual para o tipo de inscrição dado
    private float obterPrecoFaseAtual(Event evento, TipoInscricao tipo) {
        LocalDateTime agora = LocalDateTime.now();
        
        for (int i = evento.getFases().size() - 1; i >= 0; i--) {
            var fase = evento.getFases().get(i);
            LocalDateTime dataInicio = fase.getDataInicio().atStartOfDay();
            LocalDateTime dataFim = fase.getDataFim().plusDays(1).atStartOfDay();
            
            if (agora.isAfter(dataInicio) && agora.isBefore(dataFim)) {
                return fase.obterPreco(tipo);
            }
        }
        
        throw new IllegalArgumentException("Nenhuma fase de inscrição ativa neste momento");
    }

    // Exporta a lista de participantes de um evento para um ficheiro CSV
    public void exportarParticipantesParaCSV(int idEvento, String caminhoFicheiro) throws IOException {
        List<Inscricao> participantes = obterListaParticipantes(idEvento);
        
        if (participantes.isEmpty()) {
            throw new IllegalArgumentException("Nenhum participante encontrado para este evento.");
        }
        
        try (FileWriter fw = new FileWriter(caminhoFicheiro);
             BufferedWriter bw = new BufferedWriter(fw)) {
            
            // Cabeçalho
            bw.write("Nome,Email,NIF,Tipo Inscrição,Preço,Estado,Data Inscrição");
            bw.newLine();
            
            // Dados
            for (Inscricao inscricao : participantes) {
                bw.write(String.format(
                    "%s,%s,%d,%s,%.2f,%s,%s",
                    inscricao.getNome(),
                    inscricao.getEmail(),
                    inscricao.getNif(),
                    inscricao.getTipoInscricao(),
                    inscricao.getValorTotal(),
                    inscricao.getEstado(),
                    inscricao.getDataCriacao()
                ));
                bw.newLine();
            }
        }
    }

    // Remove todas as inscrições associadas a um evento (apenas se o evento estiver inativo)
    public void removerInscricoesDoEvento(int idEvento, EventService eventService) {
        Event evento = eventService.findEventoByIdSimples(idEvento);
        
        if (evento == null) {
            throw new IllegalArgumentException("Evento com id " + idEvento + " não encontrado.");
        }
        
        if (evento.getActive()) {
            throw new IllegalStateException("Não é possível remover inscrições de um evento ativo.");
        }
        
        inscricoes.removeIf(i -> i.getIdEvento() == idEvento);
    }
}
