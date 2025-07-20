package br.com.applogin.applogin.repository;

import br.com.applogin.applogin.model.Mensagem;
import org.springframework.data.repository.CrudRepository;

public interface MensagemRepository extends CrudRepository<Mensagem, Long> {
}