package com.entrega1;

import java.util.ArrayList;
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

        ArrayList<Oferta> ofertas = new ArrayList<Oferta>();

            try(ZContext context = new ZContext()){
                System.out.println("Corriendo filtro...");
                ZMQ.Socket subscriber = context.createSocket(SocketType.SUB);
                subscriber.connect("tcp://25.12.51.131:1099");

                String filter = "0";

                subscriber.subscribe(filter.getBytes(ZMQ.CHARSET));

                while(true){

                    Oferta temp = new Oferta();

                    String mensaje = subscriber.recvStr(0).trim();

                    StringTokenizer token = new StringTokenizer(mensaje, " ");
                    int pos = Integer.valueOf(token.nextToken());
                    temp.setId(Integer.valueOf(token.nextToken()));
                    temp.setIdSector(Integer.valueOf(token.nextToken()));
                    temp.setIdEmpleador(Integer.valueOf(token.nextToken()));
                    temp.setDescripcion(token.nextToken());
                    temp.setCargo(token.nextToken());
                    temp.setSueldo(Integer.valueOf(token.nextToken()));
                    ofertas.add(temp);

                    if(ofertas.size()==2){
                        
                        ZMQ.Socket server = context.createSocket(SocketType.REQ);
                        server.connect("tcp://25.12.51.131:1098");
                        for (int i = 0; i <ofertas.size();i++){

                            String oferta = String.format("%d %d %d %s %s %d", ofertas.get(i).getId(), ofertas.get(i).getIdSector(),
                            ofertas.get(i).getIdEmpleador(), ofertas.get(i).getDescripcion(), ofertas.get(i).getCargo(),
                            ofertas.get(i).getSueldo());

                            server.send(oferta.getBytes(ZMQ.CHARSET), 0);

                            System.out.println("Oferta enviada");

                            byte[] reply = server.recv(0);

                            System.out.println(new String(reply,ZMQ.CHARSET));
                        }
                    }

                    System.out.println("Oferta: "+temp.getId());
                }
                
            } catch (Exception e) {
                System.err.println(" System exception: "+ e);
            }
    }
}
