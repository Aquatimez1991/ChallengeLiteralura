package com.literalura.ChallengeLiteralura.repository;

import com.literalura.ChallengeLiteralura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {

    Optional<Autor> findByNombreContainsIgnoreCase(String nombre);

    List<Autor> findByFechaDeNacimientoLessThanEqualAndFechaDeMuerteGreaterThanEqual(Integer nacimiento, Integer muerte);

    List<Autor> findByFechaDeNacimiento(Integer fechaNacimiento);

    List<Autor> findByFechaDeMuerte(Integer fechaMuerte);


}
