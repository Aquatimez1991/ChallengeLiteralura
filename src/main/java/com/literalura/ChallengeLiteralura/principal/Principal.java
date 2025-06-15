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
        System.out.println(" Ingrese el título del libro a buscar:");
        String titulo = teclado.nextLine().trim();
        String url = URL_BASE + titulo.replace(" ", "+");

        var json = consumoAPI.obtenerDatos(url);
        var datos = conversor.obtenerDatos(json, Datos.class);

        List<DatosLibro> resultados = datos.resultados();

        if (resultados.isEmpty()) {
            System.out.println(" No se encontraron libros con ese título.");
            return;
        }

        System.out.println("\n Resultados encontrados:");
        for (int i = 0; i < resultados.size(); i++) {
            var libro = resultados.get(i);
            String autores = libro.autor().isEmpty()
                    ? "Autor desconocido"
                    : libro.autor().stream()
                    .map(DatosAutor::nombre)
                    .collect(Collectors.joining(", "));

            System.out.printf("%d. %s (%s) - Autores: %s -  %.0f descargas%n",
                    i + 1,
                    libro.titulo(),
                    String.join(", ", libro.idiomas()),
                    autores,
                    libro.numeroDeDescargas());
        }

        System.out.print("\nSeleccione el número del libro a registrar (0 para cancelar): ");
        int opcion = teclado.nextInt();
        teclado.nextLine();

        if (opcion < 1 || opcion > resultados.size()) {
            System.out.println(" Operación cancelada.");
            return;
        }

        DatosLibro datosLibro = resultados.get(opcion - 1);


        boolean existe = libroRepository.findByTituloContainingIgnoreCase(datosLibro.titulo())
                .stream()
                .anyMatch(l -> l.getTitulo().equalsIgnoreCase(datosLibro.titulo()));

        if (existe) {
            System.out.println(" El libro ya está registrado.");
            return;
        }

        Libro libro = new Libro(datosLibro);

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

        libro.setAutores(autores);
        libro.setIdiomas(datosLibro.idiomas().isEmpty() ? List.of("N/A") : datosLibro.idiomas());

        libroRepository.save(libro);

        System.out.println("\n Libro guardado correctamente:");
        System.out.println("=".repeat(50));
        System.out.printf(" Título: %s%n", libro.getTitulo());
        System.out.printf(" Idiomas: %s%n", String.join(", ", libro.getIdiomas()));
        System.out.printf(" Descargas: %.0f%n", libro.getNumeroDeDescargas());
        System.out.printf(" Autor(es): %s%n",
                libro.getAutores().stream().map(Autor::getNombre).collect(Collectors.joining(", ")));
        System.out.println("=".repeat(50));
    }


    private void listarLibrosRegistrados() {
        var libros = libroRepository.findAll();
        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados.");
            return;
        }

        System.out.println("\n LISTA DE LIBROS REGISTRADOS");
        System.out.println("=".repeat(40));

        int i = 1;
        for (Libro libro : libros) {
            System.out.printf("""
            %d. Título: %s
               Idiomas: %s
               Nº de Descargas: %.0f
               Autores: %s
            %s
            """,
                    i++, libro.getTitulo(),
                    String.join(", ", libro.getIdiomas()),
                    libro.getNumeroDeDescargas(),
                    libro.getAutores().isEmpty() ? "Sin autores registrados" :
                            libro.getAutores().stream().map(Autor::getNombre).collect(Collectors.joining(", ")),
                    "-".repeat(40));
        }
    }


    private void listarAutoresRegistrados() {
        var autores = autorRepository.findAll();
        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados.");
            return;
        }

        System.out.println("\n LISTA DE AUTORES REGISTRADOS");
        System.out.println("=".repeat(40));

        int i = 1;
        for (Autor autor : autores) {
            System.out.printf("""
            %d. Nombre: %s
               Fecha de Nacimiento: %s
               Fecha de Muerte: %s
               Libros: %s
            %s
            """,
                    i++,
                    autor.getNombre(),
                    autor.getFechaDeNacimiento() != null ? autor.getFechaDeNacimiento() : "Desconocida",
                    autor.getFechaDeMuerte() != null ? autor.getFechaDeMuerte() : "Desconocida",
                    autor.getLibros().isEmpty() ? "Sin libros registrados" :
                            autor.getLibros().stream().map(Libro::getTitulo).collect(Collectors.joining(", ")),
                    "-".repeat(40));
        }
    }


    private void listarAutoresVivosEnAnio() {
        System.out.print("\n Ingrese el año para buscar autores vivos en ese periodo: ");
        int anio;

        try {
            anio = teclado.nextInt();
            teclado.nextLine();
        } catch (InputMismatchException e) {
            System.out.println(" Entrada inválida. Debe ingresar un número entero.");
            teclado.nextLine(); // limpiar buffer
            return;
        }

        if (anio < 1000 || anio > Calendar.getInstance().get(Calendar.YEAR)) {
            System.out.println(" Por favor ingrese un año válido entre 1000 y el año actual.");
            return;
        }

        var autores = autorRepository.findByFechaDeNacimientoLessThanEqualAndFechaDeMuerteGreaterThanEqual(anio, anio);

        if (autores.isEmpty()) {
            System.out.printf(" No se encontraron autores que estuvieran vivos en el año %d.%n", anio);
        } else {
            System.out.printf(" Autores que estaban vivos en el año %d:%n", anio);
            System.out.println("=".repeat(50));
            for (Autor autor : autores) {
                System.out.printf("""
                 Nombre: %s
                 Nacimiento: %s
                 Fallecimiento: %s
                %s
                """,
                        autor.getNombre(),
                        autor.getFechaDeNacimiento() != null ? autor.getFechaDeNacimiento() : "Desconocido",
                        autor.getFechaDeMuerte() != null ? autor.getFechaDeMuerte() : "Desconocido",
                        "-".repeat(50)
                );
            }
        }
    }


    private void listarLibrosPorIdioma() {
        System.out.print("\n Ingrese el código del idioma (por ejemplo: 'en', 'es'): ");
        var idioma = teclado.nextLine().trim().toLowerCase();

        if (idioma.isEmpty() || idioma.length() > 5) {
            System.out.println(" Código de idioma inválido. Intente nuevamente con un código como 'en' o 'es'.");
            return;
        }

        var libros = libroRepository.findByIdiomasContaining(idioma);

        if (libros.isEmpty()) {
            System.out.printf(" No se encontraron libros registrados en el idioma '%s'.%n", idioma);
        } else {
            System.out.printf(" Libros encontrados en idioma '%s':%n", idioma);
            System.out.println("=".repeat(50));
            libros.forEach(libro -> {
                System.out.printf("""
                 Título: %s
                 Idiomas: %s
                 Descargas: %.0f
                 Autor(es): %s
                %s
                """,
                        libro.getTitulo(),
                        String.join(", ", libro.getIdiomas()),
                        libro.getNumeroDeDescargas(),
                        libro.getAutores().isEmpty()
                                ? "Sin autores"
                                : libro.getAutores().stream().map(Autor::getNombre).collect(Collectors.joining(", ")),
                        "-".repeat(50)
                );
            });
        }
    }


    private void mostrarEstadisticas() {
        var libros = libroRepository.findAll();

        if (libros.isEmpty()) {
            System.out.println(" No hay libros registrados para generar estadísticas.");
            return;
        }

        var stats = libros.stream()
                .collect(Collectors.summarizingDouble(Libro::getNumeroDeDescargas));

        System.out.println("\n Estadísticas de Descargas de Libros");
        System.out.println("=".repeat(50));
        System.out.printf(" Total de libros evaluados: %d%n", (long) stats.getCount());
        System.out.printf(" Descargas promedio: %.2f%n", stats.getAverage());
        System.out.printf(" Descarga máxima: %.0f%n", stats.getMax());
        System.out.printf(" Descarga mínima: %.0f%n", stats.getMin());
        System.out.println("=".repeat(50));
    }


    private void mostrarTop10Descargados() {
        var libros = libroRepository.findTop10ByOrderByNumeroDeDescargasDesc();

        if (libros.isEmpty()) {
            System.out.println(" No hay libros registrados para mostrar el Top 10.");
            return;
        }

        System.out.println("\n TOP 10 LIBROS MÁS DESCARGADOS");
        System.out.println("=".repeat(60));

        int posicion = 1;
        for (Libro libro : libros) {
            String autores = libro.getAutores().isEmpty()
                    ? "Autor desconocido"
                    : libro.getAutores().stream().map(Autor::getNombre).collect(Collectors.joining(", "));

            System.out.printf("""
            #%d Título: %s
                Autor(es): %s
                Idiomas: %s
                Descargas: %.0f
            %s
            """,
                    posicion++,
                    libro.getTitulo(),
                    autores,
                    String.join(", ", libro.getIdiomas()),
                    libro.getNumeroDeDescargas(),
                    "-".repeat(60)
            );
        }
    }


    private void buscarAutorPorNombre() {
        System.out.println(" Ingrese el nombre del autor a buscar:");
        String nombre = teclado.nextLine().trim();

        Optional<Autor> autor = autorRepository.findByNombreContainsIgnoreCase(nombre);

        if (autor.isPresent()) {
            Autor a = autor.get();
            System.out.println("\n Autor encontrado:");
            System.out.println("=".repeat(50));
            System.out.printf(" Nombre: %s%n", a.getNombre());
            System.out.printf(" Año de nacimiento: %s%n", a.getFechaDeNacimiento() != null ? a.getFechaDeNacimiento() : "No registrado");
            System.out.printf(" Año de fallecimiento: %s%n", a.getFechaDeMuerte() != null ? a.getFechaDeMuerte() : "No registrado");
            System.out.println(" Libros registrados:");

            if (a.getLibros().isEmpty()) {
                System.out.println("   - No tiene libros asociados.");
            } else {
                a.getLibros().forEach(libro ->
                        System.out.printf("   - %s (%s)  %.0f descargas%n",
                                libro.getTitulo(),
                                String.join(", ", libro.getIdiomas()),
                                libro.getNumeroDeDescargas()));
            }

            System.out.println("=".repeat(50));
        } else {
            System.out.printf(" No se encontró ningún autor que coincida con \"%s\".%n", nombre);
        }
    }


    private void buscarAutoresPorAño() {
        System.out.println("¿Desea buscar por año de nacimiento o de fallecimiento?");
        System.out.println("Escriba 'nacimiento' o 'muerte':");
        String tipo = teclado.nextLine().trim().toLowerCase();

        while (!tipo.equals("nacimiento") && !tipo.equals("muerte")) {
            System.out.println("Opción inválida. Escriba 'nacimiento' o 'muerte':");
            tipo = teclado.nextLine().trim().toLowerCase();
        }

        System.out.println("Ingrese el año:");
        int anio = teclado.nextInt();
        teclado.nextLine();

        List<Autor> autores;

        if (tipo.equals("nacimiento")) {
            autores = autorRepository.findByFechaDeNacimiento(anio);
        } else {
            autores = autorRepository.findByFechaDeMuerte(anio);
        }

        if (autores.isEmpty()) {
            System.out.printf("No se encontraron autores con %s en el año %d.%n", tipo, anio);
        } else {
            System.out.printf("Autores con %s en el año %d:%n", tipo, anio);
            for (Autor autor : autores) {
                System.out.printf("- %s%n", autor.getNombre());
            }
        }
    }

}
