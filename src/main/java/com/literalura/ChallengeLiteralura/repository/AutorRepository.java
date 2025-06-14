package com.literalura.ChallengeLiteralura.repository;

import com.literalura.ChallengeLiteralura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {

    Optional<Autor> findByNombreContainsIgnoreCase(String nombre);

    List<Autor> findByFechaDeNacimientoLessThanEqualAndFechaDeMuerteGreaterThanEqual(Integer nacimiento, Integer muerte);

    List<Autor> findByFechaDeNacimientoOrFechaDeMuerte(Integer nacimiento, Integer muerte);

    @Query("SELECT DISTINCT a FROM Autor a JOIN a.libros l WHERE :idioma MEMBER OF l.idiomas")
    List<Autor> autoresPorIdioma(String idioma);

    @Query("SELECT a FROM Autor a WHERE SIZE(a.libros) >= :cantidad")
    List<Autor> autoresConMinimoLibros(int cantidad);
}
