package com.entrega1.Filtro;

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
public class FiltroImpl extends UnicastRemoteObject implements Filtro {
    ArrayList<Oferta> totalOfertas = new ArrayList<Oferta>();
    ArrayList<Solicitud> totalSolicitudes = new ArrayList<Solicitud>();

    ArrayList<Oferta> ofertas = new ArrayList<Oferta>();
    ArrayList<Solicitud> solicitudes = new ArrayList<Solicitud>();

    public FiltroImpl(String name) throws RemoteException {
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

    public void recibirSolicitud(Solicitud solicitud){
        solicitudes.add(solicitud);
        //
        if (solicitudes.size() >= 10) {
            //enviar al servidor
        }
        else{
            //guardar en backup
            grabarBackup(solicitud);
        }
    }

    public boolean consultarOferta(Solicitud solicitud) throws RemoteException {
        //Buscar si hay ofertas que correspondan a las solicitudes
        //recorrer servidores buscando la oferta que corresponden 
        return false;
    }

    public void registrarOferta(Oferta oferta) {
        //clasificar
        ofertas.add(oferta);
        
        if (ofertas.size() >= 10) {
            //enviar al servidor
        }
        else{
            //guardar en backup
            grabarBackup(oferta);
        }
        
    }

    public boolean grabarBackup(Oferta oferta){
        FileOutputStream fos = null;
        ObjectOutputStream salida = null;
        try {
            // Se crea el fichero
            fos = new FileOutputStream("backup", true);
            salida = new ObjectOutputStream(fos);
            // Se escribe el objeto en el fichero
            salida.writeObject(oferta);
        } catch (FileNotFoundException e) {
            System.out.println("1: " + e.getMessage());
            return false;
        } catch (IOException e) {
            System.out.println("2: " + e.getMessage());
            return false;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (salida != null) {
                    salida.close();
                }

            } catch (IOException e) {
                System.out.println("3: " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    public boolean grabarBackup(Solicitud solicitud){
        FileOutputStream fos = null;
        ObjectOutputStream salida = null;
        try {
            // Se crea el fichero
            fos = new FileOutputStream("backup", true);
            salida = new ObjectOutputStream(fos);
            // Se escribe el objeto en el fichero
            salida.writeObject(solicitud);
        } catch (FileNotFoundException e) {
            System.out.println("1: " + e.getMessage());
            return false;
        } catch (IOException e) {
            System.out.println("2: " + e.getMessage());
            return false;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (salida != null) {
                    salida.close();
                }

            } catch (IOException e) {
                System.out.println("3: " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    //si se calleron los servers => leer backup
    public void leerBackup(){

    }

}
