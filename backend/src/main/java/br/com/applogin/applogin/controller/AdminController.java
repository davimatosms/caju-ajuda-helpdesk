package br.com.applogin.applogin.controller;

import br.com.applogin.applogin.model.StatusChamado; // Import necessário
import br.com.applogin.applogin.repository.ChamadoRepository; // Import necessário
import org.springframework.beans.factory.annotation.Autowired; // Import necessário
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ChamadoRepository chamadoRepository; // Injeta o repositório

    @GetMapping("/dashboard")
    public String mostrarDashboardAdmin(Model model) {
        // Busca as métricas reais do banco de dados
        long chamadosAbertos = chamadoRepository.countByStatus(StatusChamado.ABERTO);
        long chamadosEmAndamento = chamadoRepository.countByStatus(StatusChamado.EM_ANDAMENTO);
        long chamadosFechados = chamadoRepository.countByStatus(StatusChamado.FECHADO);

        // Adiciona as métricas ao modelo para que a página possa usá-las
        model.addAttribute("chamadosAbertos", chamadosAbertos);
        model.addAttribute("chamadosEmAndamento", chamadosEmAndamento);
        model.addAttribute("chamadosFechados", chamadosFechados);

        return "admin-dashboard";
    }
}