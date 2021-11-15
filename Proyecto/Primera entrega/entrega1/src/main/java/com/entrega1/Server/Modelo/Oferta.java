package com.entrega1.Server.Modelo;

import java.io.Serializable;

public class Oferta implements Serializable {
    int id;
    String idSector;
    int idEmpleador;
    String descripcion;
    String cargo;
    int sueldo;

    int experiencia;
    String estudios;
    String habilidades;

    public Oferta(int id, String idSector, int idEmpleador, String descripcion, String cargo, int sueldo,
            int experiencia, String estudios, String habilidades) {
        this.id = id;
        this.idSector = idSector;
        this.idEmpleador = idEmpleador;
        this.descripcion = descripcion;
        this.cargo = cargo;
        this.sueldo = sueldo;
        this.experiencia = experiencia;
        this.estudios = estudios;
        this.habilidades = habilidades;
    }

    public Oferta() {

    }

    @Override
    public String toString() {
        return "id:" + id + "-idSector:" + idSector + "-idEmpleador:" + idEmpleador + "-descripcion:" + descripcion
                + "-cargo:" + cargo + "-sueldo:" + sueldo + "-experiencia:" + experiencia + "-estudios:" + estudios
                + "-habilidades:" + habilidades + "\n";
    }

    public String getEstudios() {
        return estudios;
    }

    public String getHabilidades() {
        return habilidades;
    }

    public int getId() {
        return id;
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

    public void setId(int id) {
        this.id = id;
    }

    public String getIdSector() {
        return idSector;
    }

    public void setIdSector(String idSector) {
        this.idSector = idSector;
    }

    public int getIdEmpleador() {
        return idEmpleador;
    }

    public void setIdEmpleador(int idEmpleador) {
        this.idEmpleador = idEmpleador;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public int getSueldo() {
        return sueldo;
    }

    public void setSueldo(int sueldo) {
        this.sueldo = sueldo;
    }

}
