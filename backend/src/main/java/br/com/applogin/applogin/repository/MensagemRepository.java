package br.com.applogin.applogin.repository;

import br.com.applogin.applogin.dto.MetricaPorTecnicoDto;
import br.com.applogin.applogin.model.Mensagem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List; // <-- A IMPORTAÇÃO QUE FALTAVA

public interface MensagemRepository extends CrudRepository<Mensagem, Long> {

    @Query("SELECT new br.com.applogin.applogin.dto.MetricaPorTecnicoDto(m.autor.nome, COUNT(DISTINCT m.chamado.id)) " +
            "FROM Mensagem m " +
            "WHERE m.chamado.status = 'FECHADO' AND m.autor.role = 'TECNICO' " +
            "GROUP BY m.autor.nome " +
            "ORDER BY COUNT(DISTINCT m.chamado.id) DESC")
    List<MetricaPorTecnicoDto> countChamadosFechadosPorTecnico();
}