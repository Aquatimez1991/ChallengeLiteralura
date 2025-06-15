package com.literalura.ChallengeLiteralura.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "libros")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    private Double numeroDeDescargas;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "idiomas_libros", joinColumns = @JoinColumn(name = "libro_id"))
    @Column(name = "idioma")
    private List<String> idiomas = new ArrayList<>();


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "libro_autor",
            joinColumns = @JoinColumn(name = "libro_id"),
            inverseJoinColumns = @JoinColumn(name = "autor_id"))
    private List<Autor> autores = new ArrayList<>();



    public Libro() {}

    public Libro(DatosLibro datos) {
        this.titulo = datos.titulo();
        this.numeroDeDescargas = datos.numeroDeDescargas();
        if (datos.idiomas() == null || datos.idiomas().isEmpty()) {
            this.idiomas = List.of("N/A");
        } else {
            this.idiomas = datos.idiomas();
        }
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public Double getNumeroDeDescargas() {
        return numeroDeDescargas;
    }

    public List<String> getIdiomas() {
        return idiomas;
    }

    public List<Autor> getAutores() {
        return autores;
    }

    public void setAutores(List<Autor> autores) {
        this.autores = autores;
    }

    public void setIdiomas(List<String> idiomas) {
        this.idiomas = idiomas;
    }



    @Override
    public String toString() {
        return "Libro{" +
                "titulo='" + titulo + '\'' +
                ", idiomas=" + idiomas +
                ", numeroDeDescargas=" + numeroDeDescargas +
                ", autores=" + autores +
                '}';
    }
}
