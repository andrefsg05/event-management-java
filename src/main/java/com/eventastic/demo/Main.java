package com.eventastic.demo;

import com.eventastic.api.EventasticAPI;
import com.eventastic.enums.TipoFase;
import com.eventastic.enums.TipoInscricao;
import com.eventastic.model.ConfiguracaoPreco;
import com.eventastic.model.Event;
import com.eventastic.model.FaseInscricao;
import com.eventastic.model.Inscricao;
import com.eventastic.model.OpcaoAdicional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws IOException {
        EventasticAPI api = new EventasticAPI();

        System.out.println("\n================ DEMO EVENTASTIC =================\n");

        // 1) Criar eventos (futuro, a decorrer, expirado - para testes)
        System.out.println("1) Criar eventos");
        Event techConf = criarEventoTech(api);
        Event musicFest = criarEventoMusic(api);
        Event oldHack = criarEventoExpirado(api);
        System.out.println("3 eventos criados!");

        // 1.b) Demonstrar inativação de evento expirado (evento 3)
        System.out.println("\n1.b) Inativar o terceiro evento que está expirado (Old Hackathon)");
        api.inativarEvento(oldHack.getIdEvento());
        System.out.println("Evento expirado inativado com sucesso.");

        // 2) Listar eventos (devem aparecer os 2 primeiros, o 3º foi removido)
        System.out.println("\n2) Listar eventos ativos na memória (esperado: 2 eventos)");
        api.obterListaEventos().forEach(e -> System.out.println("- " + e));

        // 3) Consultar eventos disponíveis
        System.out.println("\n3) Eventos disponíveis para inscrição");
        api.consultarEventosDisponiveis().forEach(e -> System.out.println("- " + e));

        // 4) Detalhes de um evento
        System.out.println("\n4) Detalhes dos eventos criados");
        api.detalhesEvento(techConf.getIdEvento());
        api.detalhesEvento(musicFest.getIdEvento());

        // 5) Procurar evento por ID
        System.out.println("\n5) Procurar evento techConf (esperado ser encontrado)");
        System.out.println(api.procurarEvento(techConf.getIdEvento()));
        System.out.println("Procurar evento inativo OldHack (esperado não ser encontrado)");
        Event resultado = api.procurarEvento(oldHack.getIdEvento());
        if (resultado == null) {
            System.out.println("[OK] Evento inativo não encontrado (como esperado).");
        }

        // 6) Fazer inscrições (incluindo testes de validação)
        System.out.println("\n6) Realizar 2 inscrições no evento Tech Conf");
        Inscricao insc1 = api.inscrever(techConf, "André Gonçalves", "andre@exemplo.com", 123456789,
                TipoInscricao.ESTUDANTE, 58010, List.of(nfcBadge(), almoco()));
        Inscricao insc2 = api.inscrever(techConf, "Miguel Costa", "miguel@exemplo.com", 987654321,
                TipoInscricao.NAO_ESTUDANTE, null, List.of(almoco()));
        System.out.println("2 inscrições realizadas com sucesso:");
        System.out.println("- Inscrição " + insc1.getId() + " de " + insc1.getEmail());
        System.out.println("- Inscrição " + insc2.getId() + " de " + insc2.getEmail());
        System.out.println("\nOBS: Lotação do evento Tech Conf é 2, o mesmo já não deve aparecer disponível.");
        System.out.println("Eventos disponíveis agora:");
        api.consultarEventosDisponiveis().forEach(e -> System.out.println("- " + e));

        // Testar duplicado de email (espera exception)
        System.out.println("\n6.b) Testar inscrição com email duplicado (deve falhar)");
        try {
            api.inscrever(techConf, "André Clone", "andre@exemplo.com", 555555555,
                    TipoInscricao.NAO_ESTUDANTE, null, List.of());
        } catch (Exception ex) {
            System.out.println("[OK] Bloqueou email duplicado: " + ex.getMessage());
        }

        // Testar lotação (evento com max 2 vagas - tentar 3ª inscrição)
        System.out.println("\n6.c) Testar inscrição além da lotação (deve falhar)");
        try {
            api.inscrever(techConf, "Carla Rocha", "carla@exemplo.com", 222333444,
                    TipoInscricao.ESTUDANTE, 58022, List.of());
        } catch (Exception ex) {
            System.out.println("[OK] Bloqueou por lotação: " + ex.getMessage());
        }

        // Testar inscrição sem incluir opção obrigatória
        System.out.println("\n6.d) Testar inscrição sem opção obrigatória escolhida (vip) no Music Fest (deve falhar)");
        try {
            api.inscrever(musicFest, "Ruben Gingeira", "ruben@exemplo.com", 333444555,
                    TipoInscricao.NAO_ESTUDANTE, null, List.of(camisola()));
        } catch (Exception ex) {
            System.out.println("[OK] Bloqueou falta de opção obrigatória: " + ex.getMessage());
        }

        // Testar inscrição de estudante sem número de aluno
        System.out.println("\n6.e) Testar inscrição de estudante sem número de aluno (deve falhar)");
        try {
            api.inscrever(musicFest, "André Zhan", "andrezhan@exemplo.com", 111222555,
                    TipoInscricao.ESTUDANTE, null, List.of(vip()));
        } catch (Exception ex) {
            System.out.println("[OK] Bloqueou falta de número de aluno: " + ex.getMessage());
        }

        // Testar inscrição de estudante com número de aluno inválido
        System.out.println("\n6.e) Testar inscrição de estudante com número de aluno inválido (deve falhar)");
        try {
            api.inscrever(musicFest, "Filipe", "flp@exemplo.com", 111222666,
                    TipoInscricao.ESTUDANTE, 57405, List.of(vip()));
        } catch (Exception ex) {
            System.out.println("[OK] Bloqueou número de aluno inválido: " + ex.getMessage());
        }

        // Testar inscrição em evento inativo
        System.out.println("\n6.f) Testar inscrição em evento inativo (Old Hackathon) (deve falhar)");
        try {
            api.inscrever(oldHack, "Ana Maria", "ana@exemplo.com", 444555666,
                    TipoInscricao.NAO_ESTUDANTE, null, List.of());
        } catch (Exception ex) {
            System.out.println("[OK] Bloqueou inscrição em evento inativo: " + ex.getMessage());
        }

        // 7) Listar inscrições
        System.out.println("\n7) Listar inscrições");
        api.listarInscricoes().forEach(i -> System.out.println("- Inscrição " + i.getId() + " de " + i.getNome()));

        // 8) Consultar inscrição (dados corretos)
        System.out.println("\n8) Consultar inscrição de André (e-mail correto)");
        api.consultarInscricao(insc1.getId(), "andre@exemplo.com");
        System.out.println("Consultar inscrição de André (e-mail errado - deve falhar)");
        try {
            api.consultarInscricao(insc1.getId(), "andre_errado@exemplo.com");
        } catch (Exception ex) {
            System.out.println("[OK] Bloqueou consulta com e-mail errado: " + ex.getMessage());
        }

        // 9) Listar participantes do evento
        System.out.println("\n9) Participantes do evento Tech Conf");
        api.obterListaParticipantes(techConf.getIdEvento())
            .forEach(i -> System.out.println("- " + i.getNome()));

        // 10) Procurar participante por critérios
        System.out.println("\n10) Procurar participante por e-mail (miguel@exemplo.com usado)");
        api.procurarParticipante(techConf, null, "miguel@exemplo.com", null)
            .forEach(i -> System.out.println("Encontrado: " + i.getNome()));

        // Procurar participante com critérios errados
        System.out.println("\n10.b) Procurar participante com critérios errados (nome 'Yuri' usado)");
        List<Inscricao> resultados = api.procurarParticipante(techConf, "Yuri", null, null);
        if (resultados.isEmpty()) {
            System.out.println("Nenhum participante encontrado, conforme esperado.");
        } else {
            resultados.forEach(i -> System.out.println("Encontrado: " + i.getNome()));
        }

        // 11) Exportar CSV
        System.out.println("\n11) Exportar participantes para CSV (participantes_techconf.csv)");
        api.exportarParticipantesParaCSV(techConf.getIdEvento(), "participantes_techconf.csv");
        System.out.println("CSV gerado.");

        // 12) Pagamento: consultar e registar 
        System.out.println("\n12) Pagamento (consultar e registar)");
        api.consultarPagamento(insc1.getId());
        System.out.println("A registar pagamento de 30.00€ para André Gonçalves...");
        api.registarPagamento(insc1.getId(), 30.00f, LocalDateTime.now(), "Transferência recebida");
        api.consultarPagamento(insc1.getId());
        System.out.println("A registar pagamento de -25.00€ para Miguel Costa (valor negativo, deve falhar)...");
        try {
            api.registarPagamento(insc2.getId(), -25.00f, LocalDateTime.now(), "Transferência recebida");
        } catch (Exception ex) {
            System.out.println("[OK] Bloqueou valor negativo: " + ex.getMessage());
        }

        // 13) Editar evento futuro (sucesso pois não começou)
        System.out.println("\n13) Editar evento futuro (Tech Conf)");
        api.editarEvento(techConf.getIdEvento(), "Tech Conf 2026", "Conferência de tecnologia",
                "Faro", techConf.getDataInicioEvento(), techConf.getDataFimEvento(),
                techConf.getHoraInicioEvento(), techConf.getHoraFimEvento(),
                techConf.getMaxParticipantes(), techConf.getFases(), techConf.getOpcoes());
        System.out.println("Evento editado com sucesso.\n");
        api.detalhesEvento(techConf.getIdEvento());

        // 14) Tentar editar evento a decorrer (deve falhar)
        System.out.println("14) Editar evento a decorrer (Music Fest) - deve falhar");
        try {
            api.editarEvento(musicFest.getIdEvento(), "Music Fest 2", "Live", "Beja",
                    musicFest.getDataInicioEvento(), musicFest.getDataFimEvento(),
                    musicFest.getHoraInicioEvento(), musicFest.getHoraFimEvento(),
                    musicFest.getMaxParticipantes(), musicFest.getFases(), musicFest.getOpcoes());
        } catch (Exception ex) {
            System.out.println("[OK] Bloqueou edição de evento a decorrer: " + ex.getMessage());
        }

        // 15) Inativar evento futuro (gera reembolsos e limpa)
        System.out.println("\n15) Inativar evento futuro (Tech Conf)");
        api.inativarEvento(techConf.getIdEvento());

        // 16) Inativar evento a decorrer (deve falhar)
        System.out.println("\n16) Tentativa de inativar evento a decorrer (Music Fest) - deve falhar");
        try {
            api.inativarEvento(musicFest.getIdEvento());
        } catch (Exception ex) {
            System.out.println("[OK] Bloqueou inativação durante o evento: " + ex.getMessage());
        }

        // 17) Listar eventos restantes
        System.out.println("\n17) Eventos restantes após inativações/expirações");
        api.obterListaEventos().forEach(e -> System.out.println("- " + e));

        // 18) Eventos disponíveis para inscrição (depois das limpezas)
        System.out.println("\n18) Eventos disponíveis para inscrição após limpezas");
        api.consultarEventosDisponiveis().forEach(e -> System.out.println("- " + e));

        // 19) Criar evento com fases de inscrição inválidas (deve falhar)
        System.out.println("\n19) Criar evento com fases de inscrição sobrepostas (deve falhar)");
        try {
            List<FaseInscricao> fasesInvalidas = List.of(
                    fase(TipoFase.EARLY, LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 10), 10f, 20f),
                    fase(TipoFase.LATE, LocalDate.of(2026, 3, 5), LocalDate.of(2026, 3, 15), 20f, 40f)
            );
            api.criarEvento(
                    "Evento Inválido",
                    "Fases sobrepostas",
                    "Lisboa",
                    LocalDate.of(2026, 3, 20), LocalDate.of(2026, 3, 22),
                    LocalTime.of(10, 0), LocalTime.of(18, 0),
                    100,
                    fasesInvalidas,
                    List.of()
            );
        } catch (Exception ex) {
            System.out.println("[OK] Bloqueou criação de evento com fases sobrepostas: " + ex.getMessage());
        }

        System.out.println("\n19.b) Criar evento com fases de inscrição duplicadas (deve falhar)");
        try {
            List<FaseInscricao> fasesInvalidas2 = List.of(
                    fase(TipoFase.EARLY, LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 10), 10f, 20f),
                    fase(TipoFase.EARLY, LocalDate.of(2026, 4, 11), LocalDate.of(2026, 4, 20), 15f, 30f)
            );
            api.criarEvento(
                    "Evento Inválido 2",
                    "Fases duplicadas",
                    "Porto",
                    LocalDate.of(2026, 4, 25), LocalDate.of(2026, 4, 27),
                    LocalTime.of(9, 0), LocalTime.of(17, 0),
                    100,
                    fasesInvalidas2,
                    List.of()
            );
        } catch (Exception ex) {
            System.out.println("[OK] Bloqueou criação de evento com fases duplicadas: " + ex.getMessage());
        }

        System.out.println("\n================ FIM DEMO =================\n");
    }

    // ---------- Métodos auxiliares para criar dados ----------

    // Criar evento futuro de tecnologia
    private static Event criarEventoTech(EventasticAPI api) {
        List<FaseInscricao> fases = List.of(
                fase(TipoFase.EARLY, LocalDate.of(2026, 1, 10), LocalDate.of(2026, 1, 27), 15f, 30f),
                fase(TipoFase.LATE, LocalDate.of(2026, 1, 28), LocalDate.of(2026, 2, 9), 25f, 50f)
        );
        List<OpcaoAdicional> opcoes = List.of(nfcBadge(), almoco());
        return api.criarEvento(
                "Tech Conference",
                "Conferência de Tecnologia 2026",
                "Évora",
                LocalDate.of(2026, 2, 10), LocalDate.of(2026, 2, 12),
                LocalTime.of(9, 0), LocalTime.of(18, 0),
                2, // lotação pequena para testar bloqueio
                fases,
                opcoes
        );
    }

    // Criar evento a decorrer de música
    private static Event criarEventoMusic(EventasticAPI api) {
        List<FaseInscricao> fases = List.of(
                fase(TipoFase.EARLY, LocalDate.of(2025, 12, 15), LocalDate.of(2026, 1, 19), 20f, 40f),
                fase(TipoFase.DURING, LocalDate.of(2026, 1, 20), LocalDate.of(2026, 1, 27), 30f, 60f)
        );
        List<OpcaoAdicional> opcoes = List.of(camisola(), vip());
        return api.criarEvento(
                "Music Fest",
                "Festival de Música",
                "Évora",
                LocalDate.of(2026, 1, 20), LocalDate.of(2026, 1, 27),
                LocalTime.of(16, 0), LocalTime.of(23, 59),
                5,
                fases,
                opcoes
        );
    }

    // Criar evento expirado para exemplo
    private static Event criarEventoExpirado(EventasticAPI api) {
        List<FaseInscricao> fases = List.of(
                fase(TipoFase.EARLY, LocalDate.of(2025, 10, 1), LocalDate.of(2025, 10, 15), 10f, 20f)
        );
        List<OpcaoAdicional> opcoes = List.of();
        return api.criarEvento(
                "Old Hackathon",
                "Festival de programação",
                "Évora",
                LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 2),
                LocalTime.of(8, 0), LocalTime.of(20, 0),
                10,
                fases,
                opcoes
        );
    }

    // Métodos auxiliares para criar fases e opções adicionais
    private static FaseInscricao fase(TipoFase tipo, LocalDate inicio, LocalDate fim, float precoEstudante, float precoNaoEstudante) {
        Map<TipoInscricao, ConfiguracaoPreco> precos = new HashMap<>();
        precos.put(TipoInscricao.ESTUDANTE, new ConfiguracaoPreco(TipoInscricao.ESTUDANTE, precoEstudante));
        precos.put(TipoInscricao.NAO_ESTUDANTE, new ConfiguracaoPreco(TipoInscricao.NAO_ESTUDANTE, precoNaoEstudante));
        return new FaseInscricao(tipo, inicio, fim, precos);
    }

    private static OpcaoAdicional nfcBadge() {
        return new OpcaoAdicional("Badge NFC", "Badge com chip para acesso rápido", 5f, false);
    }

    private static OpcaoAdicional almoco() {
        return new OpcaoAdicional("Almoço", "Almoço incluído", 10f, false);
    }

    private static OpcaoAdicional camisola() {
        return new OpcaoAdicional("Camisola", "Camisola oficial do festival", 15f, false);
    }

    private static OpcaoAdicional vip() {
        return new OpcaoAdicional("VIP", "Acesso a zona VIP", 40f, true);
    }
}
