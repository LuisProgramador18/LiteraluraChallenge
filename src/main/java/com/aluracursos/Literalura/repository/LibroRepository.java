package com.aluracursos.Literalura.repository;

import com.aluracursos.Literalura.models.DatosLibro;
import com.aluracursos.Literalura.models.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LibroRepository extends JpaRepository<Libro, Long> {

//    @Query("SELECT l FROM Libro l WHERE LOWER(REPLACE(l.titulo, ' ', '')) = :titulo")
    boolean existsByTitulo(String titulo);

    List<Libro> findAll();

    List<Libro> findByIdioma(String idioma);
}
