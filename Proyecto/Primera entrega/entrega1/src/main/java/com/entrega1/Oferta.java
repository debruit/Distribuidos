package com.entrega1;
import java.io.Serializable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author usuario
 */
public class Oferta implements Serializable {
    // atributos
    int id;
    int idSector;
    int idEmpleador;
    String descripcion;
    String cargo;
    int sueldo;

    public Oferta(int id, int idSector, int idEmpleador, String descripcion, String cargo, int sueldo) {
        this.id = id;
        this.idSector = idSector;
        this.idEmpleador = idEmpleador;
        this.descripcion = descripcion;
        this.cargo = cargo;
        this.sueldo = sueldo;
    }

    public Oferta(){
        
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdSector() {
        return idSector;
    }

    public void setIdSector(int idSector) {
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
