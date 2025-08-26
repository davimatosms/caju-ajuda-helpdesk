package br.com.applogin.applogin.service;

import br.com.applogin.applogin.dto.AnaliseChamadoDto;
import br.com.applogin.applogin.dto.NovoChamadoDto;
import br.com.applogin.applogin.model.*;
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

    @Autowired
    private GeminiService geminiService;

    @Transactional
    public Chamado criarNovoChamado(Chamado chamado) {
        Optional<SlaRegra> regraOpt = slaRegraRepository.findByPrioridade(chamado.getPrioridade());
        if (regraOpt.isPresent()) {
            SlaRegra regra = regraOpt.get();
            LocalDateTime agora = LocalDateTime.now();
            LocalDateTime prazoResposta = agora.plusHours(regra.getTempoRespostaHoras());
            LocalDateTime prazoResolucao = agora.plusHours(regra.getTempoResolucaoHoras());
            chamado.setDataLimitePrimeiraResposta(prazoResposta);
            chamado.setDataLimiteResolucao(prazoResolucao);
        }
        return chamadoRepository.save(chamado);
    }

    @Transactional
    public Chamado criarNovoChamadoPeloCliente(NovoChamadoDto novoChamadoDto, Usuario cliente) {
        Chamado novoChamado = new Chamado();
        novoChamado.setCliente(cliente);
        novoChamado.setTitulo(novoChamadoDto.getTitulo());
        novoChamado.setDescricao(novoChamadoDto.getDescricao());
        // A prioridade e categoria não são mais definidas pelo cliente
        novoChamado.setStatus(StatusChamado.ABERTO);
        novoChamado.setDataCriacao(LocalDateTime.now());
        novoChamado.setStatusSla(StatusSla.NO_PRAZO);

        Mensagem primeiraMensagem = new Mensagem();
        primeiraMensagem.setTexto(novoChamadoDto.getDescricao());
        primeiraMensagem.setAutor(cliente);
        primeiraMensagem.setDataEnvio(LocalDateTime.now());
        primeiraMensagem.setChamado(novoChamado);
        novoChamado.getMensagens().add(primeiraMensagem);

        // 1. Primeiro salvamos o chamado para que ele tenha um ID e a mensagem esteja associada
        Chamado chamadoSalvo = chamadoRepository.save(novoChamado);

        // 2. Com o chamado salvo, chamamos a IA para analisar e retornar categoria e prioridade
        AnaliseChamadoDto analiseIA = geminiService.analisarChamado(chamadoSalvo);

        // 3. Atualizamos o chamado com os dados retornados pela IA
        chamadoSalvo.setCategoria(analiseIA.getCategoria());
        chamadoSalvo.setPrioridade(analiseIA.getPrioridade());

        // 4. Com a prioridade definida pela IA, calculamos o SLA
        Optional<SlaRegra> regraOpt = slaRegraRepository.findByPrioridade(chamadoSalvo.getPrioridade());
        if (regraOpt.isPresent()) {
            SlaRegra regra = regraOpt.get();
            chamadoSalvo.setDataLimitePrimeiraResposta(chamadoSalvo.getDataCriacao().plusHours(regra.getTempoRespostaHoras()));
            chamadoSalvo.setDataLimiteResolucao(chamadoSalvo.getDataCriacao().plusHours(regra.getTempoResolucaoHoras()));
        }

        // 5. Salvamos o chamado final com todos os dados e retornamos
        return chamadoRepository.save(chamadoSalvo);
    }
}