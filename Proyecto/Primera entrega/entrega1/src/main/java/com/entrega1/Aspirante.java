package com.entrega1;
import java.io.Serializable;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author usuario
 */
public class Aspirante implements Serializable {
    // atributos
    int idAspirante;
    String nombre;
    ArrayList<Integer> idSector;
    int experiencia;
    boolean estudios;
    boolean habilidades;
    ArrayList<Boolean> ofertasAceptadas;
    
    public Aspirante(int idAspirante, String nombre, ArrayList<Integer> idSector, int experiencia, boolean estudios,
            boolean habilidades, ArrayList<Boolean> ofertasAceptadas) {
        this.idAspirante = idAspirante;
        this.nombre = nombre;
        this.idSector = idSector;
        this.experiencia = experiencia;
        this.estudios = estudios;
        this.habilidades = habilidades;
        this.ofertasAceptadas = ofertasAceptadas;
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

    public ArrayList<Integer> getIdSector() {
        return idSector;
    }

    public void setIdSector(ArrayList<Integer> idSector) {
        this.idSector = idSector;
    }

    public int getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(int experiencia) {
        this.experiencia = experiencia;
    }

    public boolean isEstudios() {
        return estudios;
    }

    public void setEstudios(boolean estudios) {
        this.estudios = estudios;
    }

    public boolean isHabilidades() {
        return habilidades;
    }

    public void setHabilidades(boolean habilidades) {
        this.habilidades = habilidades;
    }

    public ArrayList<Boolean> getOfertasAceptadas() {
        return ofertasAceptadas;
    }

    public void setOfertasAceptadas(ArrayList<Boolean> ofertasAceptadas) {
        this.ofertasAceptadas = ofertasAceptadas;
    }

   

    
}
