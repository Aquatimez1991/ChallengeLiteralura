package com.literalura.ChallengeLiteralura.repository;

import com.literalura.ChallengeLiteralura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LibroRepository extends JpaRepository<Libro, Long> {

    List<Libro> findByIdiomasContaining(String idioma);

    List<Libro> findTop10ByOrderByNumeroDeDescargasDesc();

    List<Libro> findByTituloContainingIgnoreCase(String titulo);

    List<Libro> findByNumeroDeDescargasGreaterThan(Integer descargas);

    @Query("SELECT l FROM Libro l JOIN l.autores a WHERE a.fechaDeNacimiento <= :anio AND a.fechaDeMuerte >= :anio")
    List<Libro> librosConAutoresVivosEnAnio(Integer anio);

    @Query("SELECT l FROM Libro l WHERE l.numeroDeDescargas BETWEEN :min AND :max")
    List<Libro> librosPorRangoDeDescargas(Integer min, Integer max);
}
