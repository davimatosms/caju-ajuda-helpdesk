package br.com.applogin.applogin.repository;

import br.com.applogin.applogin.model.PrioridadeChamado;
import br.com.applogin.applogin.model.SlaRegra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SlaRegraRepository extends JpaRepository<SlaRegra, Long> {

    Optional<SlaRegra> findByPrioridade(PrioridadeChamado prioridade);
}