package com.aluracursos.Literalura.principal;

import com.aluracursos.Literalura.models.*;
import com.aluracursos.Literalura.repository.AutorRepository;
import com.aluracursos.Literalura.repository.LibroRepository;
import com.aluracursos.Literalura.service.ConsumoAPI;
import com.aluracursos.Literalura.service.ConvierteDatos;

import java.sql.Array;
import java.util.*;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private String BaseURL = "https://gutendex.com/books/";
    private ConvierteDatos conversor = new ConvierteDatos();
    private ConsumoAPI consumidorApi = new ConsumoAPI();
    private LibroRepository libroRepository;
    private AutorRepository autorRepository;

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public void mostrarMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Buscar libro por título 
                    2 - Listar libros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos en un determinado periodo de tiempo
                    5 - Listar libros por idioma
                    
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarLibroPorTitulo();
                    break;
                case 2:
                    listarLibrosRegistrados();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4:
                    listarAutoresPorFecha();
                    break;
                case 5:
                    listarLibrosPorIdioma();
                    break;

                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }

        }

    }

    private GutendexRes getDatos(String title) {
        var jsonRecibido = consumidorApi.obtenerDatos(BaseURL + "?search=" + title.toLowerCase().replace(" ", "+"));
        return conversor.obtenerDatos(jsonRecibido, GutendexRes.class);
    }


    private void buscarLibroPorTitulo() {
        System.out.println("Escriba el titulo del libro:");
        var title = teclado.nextLine().trim();

        var datosLibros = getDatos(title);
        if (!datosLibros.results().isEmpty()) {
            var libroDeCatalogoEncontrado = datosLibros.results().get(0);
            var autorLibro = libroDeCatalogoEncontrado.autores().stream()
                    .findFirst();

            boolean libroEnBD = libroRepository.existsByTitulo(libroDeCatalogoEncontrado.titulo());
            if (!libroEnBD) {
                System.out.printf("""
                        ----------------DATOS DEL LIBRO---------------
                        Título: %s
                        Autor: %s
                        Idioma: %s
                        Número de descargas: %s
                        ----------------------------------------------
                        """, libroDeCatalogoEncontrado.titulo(), autorLibro.get().authorName(), libroDeCatalogoEncontrado.idiomasLibro().get(0), libroDeCatalogoEncontrado.numDescargas().toString());

                Optional<Autor> autorEncontrado = autorRepository.findByNombreAutor(autorLibro.get().authorName());

                if (autorEncontrado.isPresent()) {
                    Libro nuevoLibro = new Libro(libroDeCatalogoEncontrado.titulo(), libroDeCatalogoEncontrado.numDescargas(), libroDeCatalogoEncontrado.idiomasLibro().get(0), autorEncontrado.get());
                    autorEncontrado.get().getLibros().add(nuevoLibro);
                    autorRepository.save(autorEncontrado.get());
                } else {
                    Autor nuevoAutor = new Autor(autorLibro.get().birthYear(), autorLibro.get().deathYear(), autorLibro.get().authorName());
                    Libro nuevoLibro = new Libro(libroDeCatalogoEncontrado.titulo(), libroDeCatalogoEncontrado.numDescargas(), libroDeCatalogoEncontrado.idiomasLibro().get(0), nuevoAutor);
                    nuevoAutor.getLibros().add(nuevoLibro);
                    autorRepository.save(nuevoAutor);

                }

                System.out.println("El libro (" + title + ") fue registrado con éxito en la base de datos!\n");
            } else {
                System.out.println("No se puede registrar el mismo libro 2 veces\n");
            }


        } else {
            System.out.println("El libro (" + title + ") no disponible en el catálogo\n");
        }


    }

    private void listarLibrosRegistrados() {
        List<Libro> librosRegistrados = libroRepository.findAll();
        if (!librosRegistrados.isEmpty()) {
            for (var cadaLibro : librosRegistrados) {
                System.out.printf("""
                        ----------------DATOS DEL LIBRO---------------
                        Título: %s
                        Autor: %s
                        Idioma: %s
                        Número de descargas: %s
                        ----------------------------------------------
                        
                        """, cadaLibro.getTitulo(), cadaLibro.getAutor().getNombreAutor(), cadaLibro.getIdioma(), cadaLibro.getNumeroDescargas().toString());

            }
        } else {
            System.out.println("Aún no hay libros registrados");
        }

    }

    private void listarAutoresRegistrados() {
        List<Autor> autoresRegistrados = autorRepository.findAll();
        if(autoresRegistrados.isEmpty()){
            System.out.println("Al parecer todavía no hay autores registrados");
        }else{
            for (var cadaAutor : autoresRegistrados){

                List<String> librosAutor = cadaAutor.getLibros().stream()
                                .map(Libro::getTitulo)
                                        .toList();
                System.out.printf("""
                        --------------------------
                        Autor: %s
                        Fecha de nacimiento: %s
                        Fecha de fallecimiento: %s
                        Libros que escribió: %s
                        --------------------------
                        
                        """, cadaAutor.getNombreAutor(), cadaAutor.getFechaNacimiento(), cadaAutor.getFechaFallecimiento(),librosAutor);
            }
        }


    }

    private void listarAutoresPorFecha() {
        System.out.println("Ingrese el año de inicio de la consulta: ");
        var fechaInicio = teclado.nextInt();
        System.out.println("\nExcelente! Ahora, por favor, ingrese el año fin de la consulta: ");
        var fechaFin = teclado.nextInt();
        List<Autor> autoresVivosPorFecha = autorRepository.getAuthorsAliveInCertainPeriodOfTime(fechaInicio, fechaFin);

        if(autoresVivosPorFecha.isEmpty()){
            System.out.println("No hay autores registrados en la BD que hayan vivido en ese periodo de tiempo");
        }else{
            for (var cadaAutor : autoresVivosPorFecha){

                List<String> librosAutor = cadaAutor.getLibros().stream()
                        .map(Libro::getTitulo)
                        .toList();
                System.out.printf("""
                        --------------------------
                        Autor: %s
                        Fecha de nacimiento: %s
                        Fecha de fallecimiento: %s
                        Libros que escribió: %s
                        --------------------------
                        
                        """, cadaAutor.getNombreAutor(), cadaAutor.getFechaNacimiento(), cadaAutor.getFechaFallecimiento(),librosAutor);
            }
        }

    }

    private void listarLibrosPorIdioma() {
        System.out.println("""
                Ingrese el código del idioma por el que le gustaría filtrar:
                en - Inglés
                es - Español
                pt - Portugués
                fr - Francés
               ----------------
                """);
        var idiomaIngresado = teclado.nextLine();
        List<Libro> librosConIdiomaEsp = libroRepository.findByIdioma(idiomaIngresado);
        if (librosConIdiomaEsp.isEmpty()){
            System.out.println("Parece ser que no hay libros con el código del idioma ingresado en la base de datos");
        }else{
            for (var cadaLibro : librosConIdiomaEsp) {
                System.out.printf("""
                        ----------------DATOS DEL LIBRO---------------
                        Título: %s
                        Autor: %s
                        Idioma: %s
                        Número de descargas: %s
                        ----------------------------------------------
                        
                        """, cadaLibro.getTitulo(), cadaLibro.getAutor().getNombreAutor(), cadaLibro.getIdioma(), cadaLibro.getNumeroDescargas().toString());

            }
        }

    }


}



