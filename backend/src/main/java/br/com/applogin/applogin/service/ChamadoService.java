package br.com.applogin.applogin.service;

import br.com.applogin.applogin.model.Chamado;
import br.com.applogin.applogin.model.SlaRegra;
import br.com.applogin.applogin.repository.ChamadoRepository;
import br.com.applogin.applogin.repository.SlaRegraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ChamadoService {

    @Autowired
    private ChamadoRepository chamadoRepository;

    @Autowired
    private SlaRegraRepository slaRegraRepository;

    @Transactional
    public Chamado criarNovoChamado(Chamado chamado) {
        // LÓGICA DO SLA
        // 1. Buscar a regra de SLA correspondente à prioridade do chamado
        Optional<SlaRegra> regraOpt = slaRegraRepository.findByPrioridade(chamado.getPrioridade());

        if (regraOpt.isPresent()) {
            SlaRegra regra = regraOpt.get();
            LocalDateTime agora = LocalDateTime.now();

            // 2. Calcular os prazos com base na regra encontrada
            LocalDateTime prazoResposta = agora.plusHours(regra.getTempoRespostaHoras());
            LocalDateTime prazoResolucao = agora.plusHours(regra.getTempoResolucaoHoras());

            // 3. Definir os prazos no objeto Chamado
            chamado.setDataLimitePrimeiraResposta(prazoResposta);
            chamado.setDataLimiteResolucao(prazoResolucao);
        } else {
            // Opcional: Logar um aviso se nenhuma regra for encontrada para a prioridade
            System.out.println("Aviso: Nenhuma regra de SLA encontrada para a prioridade " + chamado.getPrioridade());
        }

        // Salva o chamado já com os prazos de SLA calculados
        return chamadoRepository.save(chamado);
    }
}