package br.com.applogin.applogin.service;

import br.com.applogin.applogin.model.Mensagem;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String remetente;


    public void enviarEmailDeVerificacao(String para, String nomeUsuario, String urlDeVerificacao) {
        logger.info("Enviando e-mail de VERIFICAÇÃO para {}", para);
        try {
            Context context = new Context();
            context.setVariable("username", nomeUsuario);
            context.setVariable("verificationUrl", urlDeVerificacao);
            String corpoEmailHtml = templateEngine.process("verificacao-email", context);
            enviarEmail(para, "Caju Ajuda - Confirme sua conta", corpoEmailHtml);
        } catch (Exception e) {
            logger.error("Falha ao enviar e-mail de verificação para {}: {}", para, e.getMessage());
            throw new RuntimeException("Erro ao enviar e-mail de verificação.", e);
        }
    }


    @Async
    public void enviarNotificacaoDeNovaMensagem(Mensagem mensagem) {
        String para = mensagem.getChamado().getCliente().getEmail();
        String nomeCliente = mensagem.getChamado().getCliente().getNome().split(" ")[0];
        Long chamadoId = mensagem.getChamado().getId();

        logger.info("Enviando e-mail de NOTIFICAÇÃO (assíncrono) para {} sobre o chamado #{}", para, chamadoId);
        try {
            Context context = new Context();
            context.setVariable("nomeCliente", nomeCliente);
            context.setVariable("chamadoId", chamadoId);
            context.setVariable("textoMensagem", mensagem.getTexto());

            String corpoEmailHtml = templateEngine.process("notificacao-resposta", context);
            enviarEmail(para, "Você recebeu uma nova resposta no chamado #" + chamadoId, corpoEmailHtml);
        } catch (Exception e) {
            logger.error("Falha ao enviar e-mail de notificação para {}: {}", para, e.getMessage());
            // Não lançamos exceção aqui para não quebrar o fluxo do chat
        }
    }


    private void enviarEmail(String para, String assunto, String corpoHtml) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
        helper.setFrom(remetente);
        helper.setTo(para);
        helper.setSubject(assunto);
        helper.setText(corpoHtml, true);
        mailSender.send(mimeMessage);
    }
}