package com.entrega1;

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
public class RegistradorServer {

    public static void main(String args[]) {

        try(ZContext context = new ZContext()){
            System.out.println("Corriendo servidor...");
            ZMQ.Socket subscriber = context.createSocket(SocketType.SUB);
            subscriber.connect("tcp://localhost:1099");

            String filter = "0";

            subscriber.subscribe(filter.getBytes(ZMQ.CHARSET));

            String mensaje = subscriber.recvStr(0).trim();
            StringTokenizer token = new StringTokenizer(mensaje, " ");
            int idOferta = Integer.valueOf(token.nextToken());
            int idSector = Integer.valueOf(token.nextToken());
            int idEmpleador= Integer.valueOf(token.nextToken());
            String descripcion = token.nextToken();
            String cargo = token.nextToken();
            int sueldo= Integer.valueOf(token.nextToken());

            System.out.println("idSector: "+idSector);
            
        } catch (Exception e) {
            System.err.println(" System exception: "+ e);
        }
        // System.setProperty("java.rmi.server.hostname","25.12.51.131");
        // try {
        //     java.rmi.registry.LocateRegistry.createRegistry(1099);
        //     new RegistradorImpl("rmi://25.12.51.131:1099" + "/MiRegistrador");
        // } catch (Exception e) {
        //     System.err.println("System exception" + e);
        // }
    }
}
