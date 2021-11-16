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
    int idSector;
    // ArrayList<Integer> idSectores;
    int experiencia;
    String estudios;
    String habilidades;
    ArrayList<Boolean> ofertasAceptadas;

    public Aspirante(){

    }
    
    public Aspirante(int idAspirante, String nombre, int idSector, int experiencia, String estudios,
    String habilidades, ArrayList<Boolean> ofertasAceptadas) {
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

    // public void addSector(int sector) {
    //     this.idSector.add(sector);
    // }

    public String getEstudios() {
        return estudios;
    }

    public String getHabilidades() {
        return habilidades;
    }

    public void addOfertaAceptada(boolean oferta) {
        this.ofertasAceptadas.add(oferta);
    }

    

   

    
}
