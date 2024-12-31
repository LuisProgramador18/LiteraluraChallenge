package com.aluracursos.Literalura.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosAutor(@JsonAlias("name") String authorName,
                         @JsonAlias("birth_year") Integer birthYear,
                         @JsonAlias("death_year") Integer deathYear) {
}
