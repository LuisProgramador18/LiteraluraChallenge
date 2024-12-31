package com.aluracursos.Literalura.repository;

import com.aluracursos.Literalura.models.Autor;
import com.aluracursos.Literalura.models.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {

    Optional<Autor> findByNombreAutor(String nombreAutor);

    List<Autor> findAll();

    @Query("SELECT a FROM Autor a WHERE a.fechaNacimiento >= :fechaNacimiento AND (a.fechaFallecimiento IS NULL OR a.fechaFallecimiento <= :fechaFallecimiento)")
    List<Autor> getAuthorsAliveInCertainPeriodOfTime(@Param("fechaNacimiento") Integer fechaNacimiento,  @Param("fechaFallecimiento") Integer fechaFallecimiento);

}
