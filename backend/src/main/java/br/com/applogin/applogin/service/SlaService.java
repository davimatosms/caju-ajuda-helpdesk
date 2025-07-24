package br.com.applogin.applogin.service;

import br.com.applogin.applogin.model.Chamado;
import br.com.applogin.applogin.model.StatusChamado;
import br.com.applogin.applogin.model.StatusSla;
import br.com.applogin.applogin.repository.ChamadoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class SlaService {

    private static final Logger logger = LoggerFactory.getLogger(SlaService.class);

    @Autowired
    private ChamadoRepository chamadoRepository;

    // A anotação @Scheduled executa este método automaticamente.
    // fixedRate = 600000 significa que será executado a cada 600.000 milissegundos (10 minutos).
    @Scheduled(fixedRate = 600000)
    @Transactional
    public void verificarSlaChamados() {
        logger.info("Iniciando verificação de SLA para chamados abertos...");

        // Busca todos os chamados que não estão fechados ou cancelados
        List<Chamado> chamadosAbertos = chamadoRepository.findByStatusNotIn(
                List.of(StatusChamado.FECHADO)
        );

        LocalDateTime agora = LocalDateTime.now();
        int chamadosAtualizados = 0;

        for (Chamado chamado : chamadosAbertos) {
            StatusSla statusAntigo = chamado.getStatusSla();
            StatusSla novoStatus = calcularNovoStatusSla(chamado, agora);

            if (statusAntigo != novoStatus) {
                chamado.setStatusSla(novoStatus);
                chamadoRepository.save(chamado);
                chamadosAtualizados++;
            }
        }

        if (chamadosAtualizados > 0) {
            logger.info("Verificação de SLA concluída. {} chamados foram atualizados.", chamadosAtualizados);
        } else {
            logger.info("Verificação de SLA concluída. Nenhum chamado precisou de atualização.");
        }
    }

    private StatusSla calcularNovoStatusSla(Chamado chamado, LocalDateTime agora) {
        // Verifica o prazo de resolução
        if (chamado.getDataLimiteResolucao() != null && agora.isAfter(chamado.getDataLimiteResolucao())) {
            return StatusSla.VIOLADO;
        }

        // Verifica se está próximo de violar (ex: últimos 25% do tempo total)
        if (chamado.getDataLimiteResolucao() != null && chamado.getDataCriacao() != null) {
            Duration tempoTotal = Duration.between(chamado.getDataCriacao(), chamado.getDataLimiteResolucao());
            Duration tempoRestante = Duration.between(agora, chamado.getDataLimiteResolucao());

            // Se o tempo restante for menor que 25% do tempo total, marca como próximo do vencimento
            if (tempoRestante.toMillis() < tempoTotal.toMillis() * 0.25) {
                return StatusSla.PROXIMO_VENCIMENTO;
            }
        }

        return StatusSla.NO_PRAZO;
    }
}