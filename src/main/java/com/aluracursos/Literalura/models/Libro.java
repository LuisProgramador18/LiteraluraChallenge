package com.aluracursos.Literalura.models;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "Libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLibro;
    private String titulo;
    private Integer numeroDescargas;
    private String idioma;
    @ManyToOne
    @JoinColumn(name = "author_id")
    private Autor autor;

    public Libro(){}

    public Libro(String titulo, Integer numeroDescargas, String idioma, Autor autor) {
        this.titulo = titulo;
        this.numeroDescargas = numeroDescargas;
        this.idioma = idioma;
        this.autor = autor;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public Integer getNumeroDescargas() {
        return numeroDescargas;
    }

    public void setNumeroDescargas(Integer numeroDescargas) {
        this.numeroDescargas = numeroDescargas;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    @Override
    public String toString() {
        return "Libro{" +
                " titulo='" + titulo + '\'' +
                ", numeroDescargas=" + numeroDescargas +
                ", idioma='" + idioma + '\'' +
                ", autor=" + autor +
                '}';
    }
}
