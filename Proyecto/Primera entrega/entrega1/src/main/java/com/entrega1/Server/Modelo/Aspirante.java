package com.entrega1.Server.Modelo;

import java.io.Serializable;
import java.util.ArrayList;

public class Aspirante implements Serializable {
    // Atributos
    int idAspirante;
    String nombre;
    int idSector;
    int experiencia;
    String estudios;
    String habilidades;

    // borrar
    ArrayList<Boolean> ofertasAceptadas;

    public Aspirante() {

    }

    public Aspirante(int idAspirante, String nombre, int idSector, int experiencia, String estudios, String habilidades,
            ArrayList<Boolean> ofertasAceptadas) {
        this.idAspirante = idAspirante;
        this.nombre = nombre;
        this.idSector = idSector;
        this.experiencia = experiencia;
        this.estudios = estudios;
        this.habilidades = habilidades;
        this.ofertasAceptadas = ofertasAceptadas;
    }

    public String getEstudios() {
        return estudios;
    }

    public String getHabilidades() {
        return habilidades;
    }

    public int getIdAspirante() {
        return idAspirante;
    }

    public void setIdAspirante(int idAspirante) {
        this.idAspirante = idAspirante;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getIdSector() {
        return idSector;
    }

    public void setIdSector(int idSector) {
        this.idSector = idSector;
    }

    public int getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(int experiencia) {
        this.experiencia = experiencia;
    }

    public String isEstudios() {
        return estudios;
    }

    public void setEstudios(String estudios) {
        this.estudios = estudios;
    }

    public String isHabilidades() {
        return habilidades;
    }

    public void setHabilidades(String habilidades) {
        this.habilidades = habilidades;
    }

    public ArrayList<Boolean> getOfertasAceptadas() {
        return ofertasAceptadas;
    }

    public void setOfertasAceptadas(ArrayList<Boolean> ofertasAceptadas) {
        this.ofertasAceptadas = ofertasAceptadas;
    }

}
