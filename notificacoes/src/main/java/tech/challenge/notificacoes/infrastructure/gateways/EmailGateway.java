package tech.challenge.notificacoes.infrastructure.gateways;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import tech.challenge.notificacoes.infrastructure.ports.output.EmailOutputPort;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailGateway implements EmailOutputPort {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String emailRemetente;

    @Override
    public String enviarEmail(String emailDestinatario, String assunto, String mensagem) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(emailRemetente);
            simpleMailMessage.setTo(emailDestinatario);
            simpleMailMessage.setSubject(assunto);
            simpleMailMessage.setText(mensagem);
            javaMailSender.send(simpleMailMessage);
            return "Email enviado.";

        } catch (Exception e) {
            log.error("\n\n Erro ao enviar email: {} \n\n", e.getMessage());
            return "Falha ao enviar email.";
        }
    }
}
