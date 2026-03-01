package com.ucenm.tl01e0027;

public class Contactos {
    private Integer id;
    private String pais;
    private String nombre;
    private String telefono;
    private String nota;
    private byte[] imagen;

    // Constructor vacío
    public Contactos() {}

    // Constructor lleno para facilitar la creación del objeto
    public Contactos(Integer id, String pais, String nombre, String telefono, String nota, byte[] imagen) {
        this.id = id;
        this.pais = pais;
        this.nombre = nombre;
        this.telefono = telefono;
        this.nota = nota;
        this.imagen = imagen;
    }

    // Métodos Getter y Setter (Necesarios para la rúbrica)
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getNota() { return nota; }
    public void setNota(String nota) { this.nota = nota; }

    public byte[] getImagen() { return imagen; }
    public void setImagen(byte[] imagen) { this.imagen = imagen; }
}
