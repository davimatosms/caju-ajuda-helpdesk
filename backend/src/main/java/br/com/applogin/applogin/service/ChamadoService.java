package br.com.applogin.applogin.service;

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
        novoChamado.setPrioridade(novoChamadoDto.getPrioridade());
        novoChamado.setStatus(StatusChamado.ABERTO);
        novoChamado.setDataCriacao(LocalDateTime.now());
        novoChamado.setStatusSla(StatusSla.NO_PRAZO);

        Mensagem primeiraMensagem = new Mensagem();
        primeiraMensagem.setTexto(novoChamadoDto.getDescricao());
        primeiraMensagem.setAutor(cliente);
        primeiraMensagem.setDataEnvio(LocalDateTime.now());
        primeiraMensagem.setChamado(novoChamado);
        novoChamado.getMensagens().add(primeiraMensagem);

        Optional<SlaRegra> regraOpt = slaRegraRepository.findByPrioridade(novoChamado.getPrioridade());
        if (regraOpt.isPresent()) {
            SlaRegra regra = regraOpt.get();
            novoChamado.setDataLimitePrimeiraResposta(novoChamado.getDataCriacao().plusHours(regra.getTempoRespostaHoras()));
            novoChamado.setDataLimiteResolucao(novoChamado.getDataCriacao().plusHours(regra.getTempoResolucaoHoras()));
        }

        return chamadoRepository.save(novoChamado);
    }
}