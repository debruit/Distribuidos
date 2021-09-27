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
public class filtro {

    public static void main(String args[]) {

        // while(true){
            try(ZContext context = new ZContext()){
                System.out.println("Corriendo filtro...");
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

                System.out.println("Sector: "+ idSector);

                ZMQ.Socket server = context.createSocket(SocketType.REQ);
                server.connect("tcp://localhost:1098");
                for (int i = 0; i <10;i++){
                    server.send(descripcion.getBytes(ZMQ.CHARSET), 0);

                    byte[] reply = server.recv(0);

                    System.out.println("Received: "+ new String(reply,ZMQ.CHARSET)+" "+i);
                }
                
            } catch (Exception e) {
                System.err.println(" System exception: "+ e);
            }
    }
}
