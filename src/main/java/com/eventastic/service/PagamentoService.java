package com.eventastic.service;

import com.eventastic.model.Inscricao;
import com.eventastic.model.Pagamento;

import java.time.LocalDateTime;

// Serviço responsável pela gestão de pagamentos
public class PagamentoService {

    private final InscricaoService inscricaoService;
    
    // Construtor
    public PagamentoService(InscricaoService inscricaoService) {
        this.inscricaoService = inscricaoService;
    }

    // Consulta o estado do pagamento de uma inscrição
    public void consultarPagamento(int idInscricao) {
        Inscricao inscricao = localizarInscricao(idInscricao);
        Pagamento pagamento = inscricao.getPagamento();

        System.out.println("========== PAGAMENTO ==========");
        System.out.println("ID Inscrição: " + inscricao.getId());
        System.out.println("Participante: " + inscricao.getNome());
        System.out.println("E-mail: " + inscricao.getEmail());
        System.out.println("Valor Total a Pagar: " + inscricao.getValorTotal() + "€");
        System.out.println("\n--- Estado do Pagamento ---");
        System.out.println("Valor Transferido: " + pagamento.getValorTransferido() + "€");
        System.out.println("Data da Transferência: " +
            (pagamento.getDataTransferencia() != null ? pagamento.getDataTransferencia() : "Não registada"));
        System.out.println("Estado: " + pagamento.getEstado());
        System.out.println("Notas Internas: " +
            (pagamento.getNotasInternas() != null ? pagamento.getNotasInternas() : "Sem notas"));
        System.out.println("================================\n");
    }

    // Regista ou atualiza o pagamento de uma inscrição
    public void registarPagamento(int idInscricao, float valorTransferido, LocalDateTime dataTransferencia, String notasInternas) {
        if (dataTransferencia == null) {
            throw new IllegalArgumentException("dataTransferencia não pode ser nula");
        }

        Inscricao inscricao = localizarInscricao(idInscricao);
        Pagamento pagamento = inscricao.getPagamento();

        pagamento.setValorTransferido(valorTransferido);
        pagamento.setDataTransferencia(dataTransferencia);
        pagamento.setNotasInternas(notasInternas);

        System.out.println("\n✓ Pagamento registado/atualizado com sucesso!");
        System.out.println("Estado: " + pagamento.getEstado());
        System.out.println("Valor: " + pagamento.getValorTransferido() + "€");
    }

    // Método auxiliar para localizar uma inscrição pelo seu ID
    private Inscricao localizarInscricao(int idInscricao) {
        return inscricaoService.listarInscricoes().stream()
            .filter(i -> i.getId() == idInscricao)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Inscrição não encontrada"));
    }
}
