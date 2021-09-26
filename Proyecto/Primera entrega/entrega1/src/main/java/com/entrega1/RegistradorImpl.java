package com.entrega1;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
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
public class RegistradorImpl extends UnicastRemoteObject implements Registrador {

    ArrayList<Oferta> ofertas = new ArrayList<Oferta>();

    public RegistradorImpl(String name) throws RemoteException {
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
            return("error");
        }
    }

   public ArrayList<Oferta> consultar() throws RemoteException {
       ArrayList<Oferta> ofertas = new ArrayList<Oferta>();
     File archivo = new File("ofertas");
      try {
         // Apertura del fichero y creacion de BufferedReader para poder
          // hacer una lectura comoda (disponer del metodo readLine()).
          // Lectura del fichero
          FileInputStream fis = new FileInputStream(archivo);
          ObjectInputStream ois;

          while (fis.available()>0){
              ois = new ObjectInputStream(fis);
              Oferta temp = (Oferta) ois.readObject();
              ofertas.add(temp);
          }

          fis.close();

          
      }
      catch(Exception e){
         e.printStackTrace();
         return null;
      }
      return ofertas;
   }

   public boolean registrar(Oferta oferta){

    ofertas.add(oferta);
    FileOutputStream fos = null;
    ObjectOutputStream salida = null;

    if(ofertas.size()==10){

        

    }


        try {
         //Se crea el fichero
            fos = new FileOutputStream("backup",true);                                                 
            salida = new ObjectOutputStream(fos);
            //Se escribe el objeto en el fichero
            salida.writeObject(oferta);
        } catch (FileNotFoundException e) {
                 System.out.println("1: "+e.getMessage());
                 return false;                                                          
        } catch (IOException e) {
                 System.out.println("2: "+e.getMessage());
                 return false;  
        } finally {
            try {
                if(fos!=null){
                   fos.close();
                }
                if(salida!=null){
                   salida.close();
                }
                
            } catch (IOException e) {
                     System.out.println("3: "+e.getMessage());
                     return false;  
            }
        }
        return true;
   }
}
