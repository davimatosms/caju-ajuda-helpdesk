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
        logger.info("--- INICIANDO PROCESSO DE ENVIO DE E-MAIL DE VERIFICAÇÃO PARA {} ---", para);
        try {
            Context context = new Context();
            context.setVariable("username", nomeUsuario);
            context.setVariable("verificationUrl", urlDeVerificacao);

            logger.info("Passo 1: Processando template HTML 'verificacao-email'...");
            String corpoEmailHtml = templateEngine.process("verificacao-email", context);
            logger.info("Passo 2: Template processado com sucesso. Preparando para enviar.");

            enviarEmail(para, "Caju Ajuda - Confirme sua conta", corpoEmailHtml);

            logger.info("--- E-MAIL DE VERIFICAÇÃO ENVIADO COM SUCESSO PARA {} ---", para);
        } catch (Exception e) {
            logger.error("!!! FALHA CRÍTICA AO ENVIAR E-MAIL DE VERIFICAÇÃO para {}: {}", para, e.getMessage());
            e.printStackTrace(); // Imprime o erro completo no console para depuração
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
        }
    }

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
        logger.info("Passo 3: Criando a mensagem MimeMessage...");
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
        helper.setFrom(remetente);
        helper.setTo(para);
        helper.setSubject(assunto);
        helper.setText(corpoHtml, true);

        logger.info("Passo 4: Tudo pronto. Executando mailSender.send()...");
        mailSender.send(mimeMessage);
        logger.info("Passo 5: Comando mailSender.send() executado sem exceções.");
    }
}