package com.entrega1.Server;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.function.BooleanSupplier;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;

/**
 *
 * @author usuario
 */
public class ServerImpl extends UnicastRemoteObject implements Server {
    ArrayList<Oferta> totalOfertas = new ArrayList<Oferta>();
    ArrayList<Solicitud> totalSolicitudes = new ArrayList<Solicitud>();

    ArrayList<Oferta> ofertas = new ArrayList<Oferta>();
    ArrayList<Solicitud> solicitudes = new ArrayList<Solicitud>();

    public ServerImpl(String name) throws RemoteException {
        super();
        try {
            System.out.println("Rebind Object " + name);
            Naming.rebind(name, this);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public String prueba() throws RemoteException {
        try {
            return ("holiaaaaaa");

        } catch (Exception e) {
            e.printStackTrace();
            return ("error");
        }
    }

    public void almacenarOfertas(ArrayList<Oferta> ofertas){

    }

    public int getNumeroOfertasDHT(){
        //
        return 0;
    }

    public void buscarVacante(){
        //
    }

    public void notificarEmpleador(){

    }

    public void notificarAspirante(){

    }
}
