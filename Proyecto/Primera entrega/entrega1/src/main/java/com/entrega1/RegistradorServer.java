package com.entrega1;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 

/**
 *
 * @author usuario
 */
public class RegistradorServer {

    public static void main(String args[]) {
        System.setProperty("java.rmi.server.hostname","25.12.51.131");
        try {
            java.rmi.registry.LocateRegistry.createRegistry(1099);
            new RegistradorImpl("rmi://25.12.51.131:1099" + "/MiRegistrador");
        } catch (Exception e) {
            System.err.println("System exception" + e);
        }
    }
}
