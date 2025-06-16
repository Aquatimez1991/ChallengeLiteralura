package com.literalura.ChallengeLiteralura.repository;

import com.literalura.ChallengeLiteralura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface LibroRepository extends JpaRepository<Libro, Long> {

    List<Libro> findByIdiomasContaining(String idioma);

    List<Libro> findTop10ByOrderByNumeroDeDescargasDesc();

    List<Libro> findByTituloContainingIgnoreCase(String titulo);

}
