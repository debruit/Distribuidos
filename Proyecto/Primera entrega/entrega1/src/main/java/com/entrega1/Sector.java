package com.entrega1;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author usuario
 */
public enum Sector {
    // atributos
    SECTOR_1("DIRECTORES_Y_GERENTES",1),
    SECTOR_2("PROFESIONALES_CIENTIFICOS_E_INTELECTUALES",2),
    SECTOR_3("TECNICOS_Y_PROFESIONALES",3),
    SECTOR_4("PERSONAL_APOYO_ADMNISTRATIVO",4),
    SECTOR_5("AGRICULTORES_FORESTALES_Y_PESQUEROS",6);

    private String nombreSector;
    private int id;

    private Sector(String nombreSector, int id) {
        this.nombreSector = nombreSector;
        this.id = id;
    }

    public String getNombreSector() {
        return nombreSector;
    }

    public void setNombreSector(String nombreSector) {
        this.nombreSector = nombreSector;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    

    
}
