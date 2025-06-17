```markdown
# 📚 Challenge Literalura

Aplicación de consola desarrollada en Java con Spring Boot que permite buscar libros a través de la API
pública de Gutendex, registrar información en una base de datos y realizar consultas sobre libros y autores.

---

## 🚀 Funcionalidades

- 🔍 Buscar libros por título desde la API de Gutendex.
- 💾 Registrar automáticamente libros y sus autores en la base de datos.
- 📚 Listar todos los libros y autores registrados.
- 🗣️ Filtrar libros por idioma.
- 📅 Buscar autores vivos en un año específico.
- 📈 Mostrar estadísticas de descargas.
- 🏆 Ver el Top 10 de libros más descargados.
- 👤 Buscar autores por nombre o por año de nacimiento/fallecimiento.

---

## 🛠️ Tecnologías Utilizadas

- **Java 17**
- **Spring Boot**
- **Spring Data JPA**
- **Hibernate**
- **API REST Gutendex**
- **Base de Datos H2 (en memoria)**
- **Maven**

---


```


## 🧪 ¿Cómo Ejecutarlo?

### 1️⃣ Clonar el repositorio

```bash
git clone https://github.com/tuusuario/challenge-literalura.git
cd challenge-literalura
```

### 2️⃣ Ejecutar con Maven

```bash
./mvnw spring-boot:run
```

### 3️⃣ Acceder a la base de datos H2

- URL: `http://localhost:8080/h2-console`
- JDBC: `jdbc:h2:mem:testdb`
- Usuario: `sa`
- Contraseña: *(vacío por defecto)*

---

## ✨ Ejemplo de uso

```
*** Aplicación Challenge Literalura ***
1 - Buscar libro por título (API)
...
Ingrese el título del libro a buscar: Don Quijote

Libro guardado correctamente:
==================================================
 Título: Don Quijote
 Idiomas: es
 Descargas: 16507
 Autor(es): Cervantes Saavedra, Miguel de
==================================================
```

---

## 👤 Autor

**Elías Jeshua Salgado Coripuna**  
📍 Perú / Chile  
🛠️ Soporte técnico | Programador Java | Desarrollador Angular  
📧 esalgadoc@outlook.com

---

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Consulta el archivo [LICENSE](LICENSE) para más información.
```
