package com.literalura.ChallengeLiteralura.principal;

import com.literalura.ChallengeLiteralura.model.*;
import com.literalura.ChallengeLiteralura.repository.AutorRepository;
import com.literalura.ChallengeLiteralura.repository.LibroRepository;
import com.literalura.ChallengeLiteralura.service.ConsumoAPI;
import com.literalura.ChallengeLiteralura.service.ConvierteDatos;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class Principal {

    private final String URL_BASE = "https://gutendex.com/books/?search=";
    private final ConsumoAPI consumoAPI = new ConsumoAPI();
    private final ConvierteDatos conversor = new ConvierteDatos();
    private final Scanner teclado = new Scanner(System.in);

    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public void muestraElMenu() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("""
                \n*** Aplicación Challenge Literalura ***
                1 - Buscar libro por título (API)
                2 - Listar libros registrados
                3 - Listar autores registrados
                4 - Listar autores vivos en un año
                5 - Listar libros por idioma
                6 - Ver estadísticas de descargas
                7 - Top 10 libros más descargados
                8 - Buscar autor por nombre
                9 - Buscar autores por año de nacimiento o fallecimiento
                0 - Salir
            """);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1 -> buscarLibroApi();
                case 2 -> listarLibrosRegistrados();
                case 3 -> listarAutoresRegistrados();
                case 4 -> listarAutoresVivosEnAnio();
                case 5 -> listarLibrosPorIdioma();
                case 6 -> mostrarEstadisticas();
                case 7 -> mostrarTop10Descargados();
                case 8 -> buscarAutorPorNombre();
                case 9 -> buscarAutoresPorAño();
                case 0 -> {
                    System.out.println("Saliendo de la aplicación...");
                    System.exit(0);
                }
                default -> System.out.println("Opción inválida");
            }
        }
    }

    private void buscarLibroApi() {
        System.out.println("Ingrese el nombre del libro:");
        var titulo = teclado.nextLine();
        var json = consumoAPI.obtenerDatos(URL_BASE + titulo.replace(" ", "+"));
        var datos = conversor.obtenerDatos(json, Datos.class);

        Optional<DatosLibro> libroOptional = datos.resultados().stream()
                .filter(l -> l.titulo().equalsIgnoreCase(titulo))
                .findFirst();

        if (libroOptional.isPresent()) {
            DatosLibro datosLibro = libroOptional.get();

            // Verificar si ya existe en la base de datos
            boolean existe = libroRepository.findByTituloContainingIgnoreCase(datosLibro.titulo()).stream()
                    .anyMatch(l -> l.getTitulo().equalsIgnoreCase(datosLibro.titulo()));

            if (existe) {
                System.out.println("El libro ya está registrado.");
                return;
            }

            Libro libro = new Libro(datosLibro);

            // Agregar autores
            List<Autor> autores = new ArrayList<>();
            for (DatosAutor datosAutor : datosLibro.autor()) {
                Autor autor = autorRepository.findByNombreContainsIgnoreCase(datosAutor.nombre()).orElseGet(() -> {
                    Autor nuevo = new Autor(datosAutor);
                    nuevo.setFechaDeNacimiento(datosAutor.fechaNacimiento());
                    nuevo.setFechaDeMuerte(datosAutor.fechaFallecimiento());
                    return autorRepository.save(nuevo);
                });
                autores.add(autor);
            }

            if (!autores.isEmpty()) {
                libro.setAutorPrincipal(autores.get(0)); // Set autor_id para tabla libros
            }

            libro.setAutores(autores);
            libro.setAutorPrincipal(autores.get(0)); // <--- nuevo

            if (datosLibro.idiomas() == null || datosLibro.idiomas().isEmpty()) {
                libro.setIdiomas(List.of("N/A"));
            } else {
                libro.setIdiomas(datosLibro.idiomas());
            }

            libroRepository.save(libro);

            System.out.println("Libro guardado correctamente:\n" + libro);
        } else {
            System.out.println("Libro no encontrado.");
        }
    }


    private void listarLibrosRegistrados() {
        var libros = libroRepository.findAll();
        libros.forEach(System.out::println);
    }

    private void listarAutoresRegistrados() {
        var autores = autorRepository.findAll();
        autores.forEach(System.out::println);
    }

    private void listarAutoresVivosEnAnio() {
        System.out.println("Ingrese el año:");
        int anio = teclado.nextInt();
        teclado.nextLine();
        var autores = autorRepository.findByFechaDeNacimientoLessThanEqualAndFechaDeMuerteGreaterThanEqual(anio, anio);
        if (autores.isEmpty()) {
            System.out.println("No se encontraron autores vivos en ese año.");
        } else {
            autores.forEach(System.out::println);
        }
    }

    private void listarLibrosPorIdioma() {
        System.out.println("Ingrese el código del idioma (ej. 'en', 'es'):");
        var idioma = teclado.nextLine();
        var libros = libroRepository.findByIdiomasContaining(idioma);
        if (libros.isEmpty()) {
            System.out.println("No se encontraron libros en ese idioma.");
        } else {
            libros.forEach(System.out::println);
        }
    }

    private void mostrarEstadisticas() {
        var libros = libroRepository.findAll();
        var stats = libros.stream()
                .collect(Collectors.summarizingDouble(Libro::getNumeroDeDescargas));

        System.out.println("Cantidad promedio de descargas: " + stats.getAverage());
        System.out.println("Descarga máxima: " + stats.getMax());
        System.out.println("Descarga mínima: " + stats.getMin());
        System.out.println("Total de libros evaluados: " + stats.getCount());
    }

    private void mostrarTop10Descargados() {
        var libros = libroRepository.findTop10ByOrderByNumeroDeDescargasDesc();
        System.out.println("*** Top 10 Libros Más Descargados ***");
        libros.forEach(l -> System.out.println(l.getTitulo().toUpperCase() + " - Descargas: " + l.getNumeroDeDescargas()));
    }

    private void buscarAutorPorNombre() {
        System.out.println("Ingrese el nombre del autor:");
        String nombre = teclado.nextLine();
        Optional<Autor> autor = autorRepository.findByNombreContainsIgnoreCase(nombre);
        autor.ifPresentOrElse(
                System.out::println,
                () -> System.out.println("Autor no encontrado.")
        );
    }

    private void buscarAutoresPorAño() {
        System.out.println("Ingrese un año (nacimiento o muerte):");
        int anio = teclado.nextInt();
        teclado.nextLine();
        var autores = autorRepository.findByFechaDeNacimientoOrFechaDeMuerte(anio, anio);
        if (autores.isEmpty()) {
            System.out.println("No se encontraron autores.");
        } else {
            autores.forEach(System.out::println);
        }
    }
}
