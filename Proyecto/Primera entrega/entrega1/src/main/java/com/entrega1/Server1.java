package com.entrega1;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 

/**
 *
 * @author usuario
 */
public class Server1 {

    public static void main(String args[]) throws Exception {

        String key;
        Oferta value;

            try(ZContext context = new ZContext()){

                System.out.println("Corriendo servidor...");
                ZMQ.Socket server = context.createSocket(SocketType.REP);
                server.bind("tcp://*:1098");

                while(!Thread.currentThread().isInterrupted()){
                    String solicitud = server.recvStr(0).trim();

                    StringTokenizer token = new StringTokenizer(solicitud, "-");
                    if(token.nextToken().equals("Aspirante")){
                        Aspirante temp = new Aspirante();

                        temp.setIdAspirante(Integer.valueOf(token.nextToken()));
                        temp.setNombre(token.nextToken());
                        temp.setHabilidades(token.nextToken());
                        temp.setEstudios(token.nextToken());
                        temp.setExperiencia(Integer.valueOf(token.nextToken()));
                        temp.setIdSector(Integer.valueOf(token.nextToken()));

                        System.out.println("Llego solicitud: "+ temp.getIdAspirante());

                        String response = "Solicitud "+temp.getIdAspirante()+" OK en el servidor";

                        server.send(response.getBytes(ZMQ.CHARSET),0);

                        Thread.sleep(1000);
                    }else{
                        Oferta temp = new Oferta();

                        temp.setId(Integer.valueOf(token.nextToken()));
                        temp.setSector(token.nextToken());
                        temp.setIdEmpleador(Integer.valueOf(token.nextToken()));
                        temp.setDescripcion(token.nextToken());
                        temp.setCargo(token.nextToken());
                        temp.setSueldo(Integer.valueOf(token.nextToken()));

                        System.out.println("Llego oferta: "+ temp.getId());

                        String response = "Oferta "+temp.getId()+" OK en el servidor";

                        server.send(response.getBytes(ZMQ.CHARSET),0);

                        Thread.sleep(1000);
                    }

                    // String oferta = server.recvStr(0).trim();

                    // Oferta temp = new Oferta();

                    // StringTokenizer token = new StringTokenizer(oferta, "-");
                    // temp.setId(Integer.valueOf(token.nextToken()));
                    // temp.setSector(token.nextToken());
                    // temp.setIdEmpleador(Integer.valueOf(token.nextToken()));
                    // temp.setDescripcion(token.nextToken());
                    // temp.setCargo(token.nextToken());
                    // temp.setSueldo(Integer.valueOf(token.nextToken()));

                    // key = Integer.toString(temp.getId()) +"_"+Integer.toString(temp.getIdEmpleador());
                    // value = temp;


                    // dht(key,value);

                    // System.out.println("Llego oferta: "+ temp.getId());

                    // String response = "Oferta "+temp.getId()+" OK en el servidor";

                    // server.send(response.getBytes(ZMQ.CHARSET),0);

                    // Thread.sleep(1000);
                }
                
            } catch (Exception e) {
                System.err.println(" System exception: "+ e);
            }
    }

    public static boolean dht(String key, Oferta value) {
        FileOutputStream fos = null;
        ObjectOutputStream salida = null;
           try {
            //Se crea el fichero
               fos = new FileOutputStream("Proyecto/Primera entrega/entrega1/ofertas",true);                                                 
               salida = new ObjectOutputStream(fos);
               //Se escribe el objeto en el fichero
               Hashtable<String, Oferta> ht = new Hashtable<String, Oferta>();
               ht.put(key, value);
               salida.writeObject(ht);
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
