package br.com.applogin.applogin.repository;

import br.com.applogin.applogin.model.Chamado;
import br.com.applogin.applogin.model.StatusChamado;
import br.com.applogin.applogin.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface ChamadoRepository extends JpaRepository<Chamado, Long> {

    List<Chamado> findByCliente(Usuario cliente);
    long countByStatus(StatusChamado status);
    List<Chamado> findByStatusNotIn(List<StatusChamado> statuses);

}