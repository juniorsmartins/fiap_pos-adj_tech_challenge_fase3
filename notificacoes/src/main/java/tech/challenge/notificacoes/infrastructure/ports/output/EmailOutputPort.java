package tech.challenge.notificacoes.infrastructure.ports.output;

public interface EmailOutputPort {

    String enviarEmail(String emailDestinatario, String assunto, String mensagem);
}
