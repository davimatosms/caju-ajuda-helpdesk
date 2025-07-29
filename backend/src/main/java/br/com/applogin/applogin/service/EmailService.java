package br.com.applogin.applogin.service;

import br.com.applogin.applogin.model.Chamado;
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
        // ... (seu método existente) ...
    }


    @Async
    public void enviarNotificacaoDeNovaMensagem(Mensagem mensagem) {
        // ... (seu método existente) ...
    }

    // --- NOVO MÉTODO ADICIONADO ---
    @Async
    public void enviarNotificacaoDeChamadoFechado(Chamado chamado) {
        String para = chamado.getCliente().getEmail();
        String nomeCliente = chamado.getCliente().getNome().split(" ")[0];

        logger.info("Enviando e-mail de NOTIFICAÇÃO de fechamento para {} sobre o chamado #{}", para, chamado.getId());
        try {
            Context context = new Context();
            context.setVariable("nomeCliente", nomeCliente);
            context.setVariable("chamadoId", chamado.getId());
            context.setVariable("chamadoTitulo", chamado.getTitulo());

            String corpoEmailHtml = templateEngine.process("notificacao-fechamento", context);
            enviarEmail(para, "Seu chamado #" + chamado.getId() + " foi resolvido!", corpoEmailHtml);
        } catch (Exception e) {
            logger.error("Falha ao enviar e-mail de notificação de fechamento para {}: {}", para, e.getMessage());
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