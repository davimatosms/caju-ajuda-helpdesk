package br.com.applogin.applogin.repository;

import br.com.applogin.applogin.model.Chamado;
import br.com.applogin.applogin.model.StatusChamado;
import br.com.applogin.applogin.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChamadoRepository extends JpaRepository<Chamado, Long> {
    List<Chamado> findByCliente(Usuario cliente);

    // --- MÉTODOS NOVOS ADICIONADOS ---
    // Busca chamados de um cliente que NÃO ESTÃO em um dos status fornecidos
    List<Chamado> findByClienteAndStatusNotIn(Usuario cliente, List<StatusChamado> statuses);

    // Busca chamados de um cliente que ESTÃO em um dos status fornecidos
    List<Chamado> findByClienteAndStatusIn(Usuario cliente, List<StatusChamado> statuses);

    long countByStatus(StatusChamado status);

    List<Chamado> findByStatusNotIn(List<StatusChamado> statuses);
}