package br.com.applogin.applogin.controller;

import br.com.applogin.applogin.dto.MetricasDto;
import br.com.applogin.applogin.model.StatusChamado;
import br.com.applogin.applogin.repository.ChamadoRepository;
import br.com.applogin.applogin.repository.MensagemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin") // <-- MUDANÇA AQUI (removido o /api)
public class AdminApiController {

    @Autowired
    private ChamadoRepository chamadoRepository;

    @Autowired
    private MensagemRepository mensagemRepository;

    @GetMapping("/metricas")
    public ResponseEntity<MetricasDto> getMetricasDashboard() {
        MetricasDto metricas = new MetricasDto();

        metricas.setChamadosAbertos(chamadoRepository.countByStatus(StatusChamado.ABERTO));
        metricas.setChamadosEmAndamento(chamadoRepository.countByStatus(StatusChamado.EM_ANDAMENTO));
        metricas.setChamadosFechados(chamadoRepository.countByStatus(StatusChamado.FECHADO));
        metricas.setPerformanceTecnicos(mensagemRepository.countChamadosFechadosPorTecnico());

        return ResponseEntity.ok(metricas);
    }
}