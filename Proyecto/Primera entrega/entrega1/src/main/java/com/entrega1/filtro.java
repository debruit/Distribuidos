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

        int contador = 1;
        ArrayList<Oferta> ofertas = new ArrayList<Oferta>();
        String ofertaServer;

        try (ZContext context = new ZContext()) {
            System.out.println("Corriendo filtro...");
            ZMQ.Socket subscriber = context.createSocket(SocketType.SUB);
            // subscriber.connect("tcp://25.12.51.131:1099");
            subscriber.connect("tcp://127.0.0.1:1099");

            String filter = "0";

            subscriber.subscribe(filter.getBytes(ZMQ.CHARSET));

            while (true) {

                Oferta temp = new Oferta();

                String mensaje = subscriber.recvStr(0).trim();

                StringTokenizer token = new StringTokenizer(mensaje, "-");
                Integer.valueOf(token.nextToken());
                temp.setId(Integer.valueOf(token.nextToken()));
                temp.setIdSector(Integer.valueOf(token.nextToken()));
                temp.setIdEmpleador(Integer.valueOf(token.nextToken()));
                temp.setDescripcion(token.nextToken());
                temp.setCargo(token.nextToken());
                temp.setSueldo(Integer.valueOf(token.nextToken()));
                ofertas.add(temp);

                if (ofertas.size() >= 1) {

                    ZMQ.Socket server1 = context.createSocket(SocketType.REQ);
                    ZMQ.Socket server2 = context.createSocket(SocketType.REQ);
                    ZMQ.Socket server3 = context.createSocket(SocketType.REQ);
                    // server.connect("tcp://25.12.51.131:1098");
                    server1.connect("tcp://127.0.0.1:1101");
                    server2.connect("tcp://127.0.0.1:1102");
                    server3.connect("tcp://127.0.0.1:1103");
                    for (int i = 0; i < ofertas.size(); i++) {

                        switch (ofertas.get(i).getIdSector()) {
                        case 1:
                            System.out.println("Sector: " + ofertas.get(i).getIdSector());

                            ofertaServer = String.format("Oferta-%d-%d-%d-%s-%s-%d", contador,
                                    ofertas.get(i).getIdSector(), ofertas.get(i).getIdEmpleador(),
                                    ofertas.get(i).getDescripcion(), ofertas.get(i).getCargo(),
                                    ofertas.get(i).getSueldo());

                            server1.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                            server2.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                            server3.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                            break;
                        case 2:
                            System.out.println("Sector: " + ofertas.get(i).getIdSector());

                            ofertaServer = String.format("%d-%d-%d-%s-%s-%d", contador, ofertas.get(i).getIdSector(),
                                    ofertas.get(i).getIdEmpleador(), ofertas.get(i).getDescripcion(),
                                    ofertas.get(i).getCargo(), ofertas.get(i).getSueldo());

                            server1.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                            server2.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                            server3.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                            break;
                        case 3:
                            System.out.println("Sector: " + ofertas.get(i).getIdSector());

                            ofertaServer = String.format("%d-%d-%d-%s-%s-%d", contador, ofertas.get(i).getIdSector(),
                                    ofertas.get(i).getIdEmpleador(), ofertas.get(i).getDescripcion(),
                                    ofertas.get(i).getCargo(), ofertas.get(i).getSueldo());

                            server1.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                            server2.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                            server3.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                            break;
                        case 4:
                            System.out.println("Sector: " + ofertas.get(i).getIdSector());

                            ofertaServer = String.format("%d-%d-%d-%s-%s-%d", contador, ofertas.get(i).getIdSector(),
                                    ofertas.get(i).getIdEmpleador(), ofertas.get(i).getDescripcion(),
                                    ofertas.get(i).getCargo(), ofertas.get(i).getSueldo());

                            server1.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                            server2.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                            server3.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                            break;
                        case 5:
                            System.out.println("Sector: " + ofertas.get(i).getIdSector());

                            ofertaServer = String.format("%d-%d-%d-%s-%s-%d", contador, ofertas.get(i).getIdSector(),
                                    ofertas.get(i).getIdEmpleador(), ofertas.get(i).getDescripcion(),
                                    ofertas.get(i).getCargo(), ofertas.get(i).getSueldo());

                            server1.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                            server2.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                            server3.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                            break;
                        }
                        contador++;

                        byte[] reply = server1.recv(0);
                        byte[] reply2 = server2.recv(0);
                        byte[] reply3 = server3.recv(0);

                        System.out.println(new String(reply, ZMQ.CHARSET));
                        System.out.println(new String(reply2, ZMQ.CHARSET));
                        System.out.println(new String(reply3, ZMQ.CHARSET));
                        ofertas.remove(i);
                        i--;
                    }
                }
            }

        } catch (Exception e) {
            System.err.println(" System exception: " + e);
        }
    }
}
