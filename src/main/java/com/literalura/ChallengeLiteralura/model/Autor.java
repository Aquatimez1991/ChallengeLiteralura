package com.literalura.ChallengeLiteralura.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "autores")
public class Autor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private Integer fechaDeNacimiento;
    private Integer fechaDeMuerte;

    @ManyToMany(mappedBy = "autores", fetch = FetchType.EAGER)
    private List<Libro> libros = new ArrayList<>();

    public Autor() {}

    public Autor(DatosAutor datos) {
        this.nombre = datos.nombre();
        this.fechaDeNacimiento = datos.fechaNacimiento() != null && datos.fechaNacimiento() != 0
                ? datos.fechaNacimiento() : null;
        this.fechaDeMuerte = datos.fechaFallecimiento() != null && datos.fechaFallecimiento() != 0
                ? datos.fechaFallecimiento() : null;
    }


    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Integer getFechaDeNacimiento() {
        return fechaDeNacimiento;
    }

    public Integer getFechaDeMuerte() {
        return fechaDeMuerte;
    }

    public List<Libro> getLibros() {
        return libros;
    }

    public void setFechaDeNacimiento(Integer fechaDeNacimiento) {
        this.fechaDeNacimiento = fechaDeNacimiento;
    }

    public void setFechaDeMuerte(Integer fechaDeMuerte) {
        this.fechaDeMuerte = fechaDeMuerte;
    }


    @Override
    public String toString() {
        return nombre;
    }
}
