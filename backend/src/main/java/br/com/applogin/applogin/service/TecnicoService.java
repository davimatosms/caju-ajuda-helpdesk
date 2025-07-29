package br.com.applogin.applogin.service;

import br.com.applogin.applogin.model.Chamado;
import br.com.applogin.applogin.model.Mensagem;
import br.com.applogin.applogin.model.StatusChamado;
import br.com.applogin.applogin.model.Usuario;
import br.com.applogin.applogin.repository.ChamadoRepository;
import br.com.applogin.applogin.repository.MensagemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TecnicoService {

    @Autowired
    private ChamadoRepository chamadoRepository;

    @Autowired
    private MensagemRepository mensagemRepository;

    @Autowired
    private EmailService emailService;

    @Transactional
    public void atualizarStatusChamado(Long chamadoId, StatusChamado novoStatus, Usuario tecnico) {
        Chamado chamado = chamadoRepository.findById(chamadoId)
                .orElseThrow(() -> new RuntimeException("Chamado não encontrado"));

        if (novoStatus == StatusChamado.FECHADO) {
            fecharChamado(chamado, tecnico);
        } else {
            chamado.setStatus(novoStatus);
            chamadoRepository.save(chamado);
        }
    }

    private void fecharChamado(Chamado chamado, Usuario tecnico) {
        chamado.setStatus(StatusChamado.FECHADO);
        chamado.setDataFechamento(LocalDateTime.now());
        chamadoRepository.save(chamado);

        Mensagem mensagemSistema = new Mensagem();
        mensagemSistema.setAutor(tecnico);
        mensagemSistema.setChamado(chamado);
        mensagemSistema.setTexto("O chamado foi marcado como FECHADO.");
        mensagemSistema.setDataEnvio(LocalDateTime.now());
        mensagemRepository.save(mensagemSistema);

        emailService.enviarNotificacaoDeChamadoFechado(chamado);
    }
}