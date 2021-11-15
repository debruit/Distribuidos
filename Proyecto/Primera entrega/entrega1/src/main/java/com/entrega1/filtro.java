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

        ArrayList<Aspirante> solicitudes = new ArrayList<Aspirante>();
        String solicitudServer = "", sector = "";

        ArrayList<Oferta> ofertas = new ArrayList<Oferta>();
        String ofertaServer = "";

        try (ZContext context = new ZContext()) {
            System.out.println("Corriendo filtro...");
            ZMQ.Socket subscriber = context.createSocket(SocketType.SUB);
            // subscriber.connect("tcp://25.12.51.131:1099");
            subscriber.connect("tcp://127.0.0.1:1099");

            String solicitud = "0";

            subscriber.subscribe(solicitud.getBytes(ZMQ.CHARSET));

            while (true) {

                String mensaje = subscriber.recvStr(0).trim();

                StringTokenizer token = new StringTokenizer(mensaje, "-");

                Integer.valueOf(token.nextToken());

                if (token.nextToken().equals("Aspirante")) {
                    agregarSolicitud(token, solicitudes, context, sector, solicitudServer);
                } else {
                    agregarOferta(token, ofertas, context, sector, ofertaServer);
                }

            }

        } catch (Exception e) {
            System.err.println(" System exception: " + e);
        }
    }

    public static void agregarSolicitud(StringTokenizer token, ArrayList<Aspirante> solicitudes, ZContext context,
            String sector, String solicitudServer) {
        Aspirante temp = new Aspirante();

        temp.setIdAspirante(Integer.valueOf(token.nextToken()));
        temp.setNombre(token.nextToken());
        temp.setHabilidades(token.nextToken());
        temp.setEstudios(token.nextToken());
        temp.setExperiencia(Integer.valueOf(token.nextToken()));
        temp.setIdSector(Integer.valueOf(token.nextToken()));
        solicitudes.add(temp);

        if (solicitudes.size() > 1) {

            ZMQ.Socket server = context.createSocket(SocketType.REQ);
            // server.connect("tcp://25.12.51.131:1098");
            server.connect("tcp://127.0.0.1:1098");
            for (int i = 0; i < solicitudes.size(); i++) {

                sector = solicitudes.get(i).getHabilidades();

                if (sector.contains("Director") || sector.contains("Gerente") || sector.contains("Administrador")
                        || sector.contains("Supervisor") || sector.contains("Jefe")) {
                    System.out.println("Sector: DIRECTORES Y GERENTES");

                    solicitudServer = String.format("Aspirante-%d-%s-%s-%s-%d-%d", solicitudes.get(i).getIdAspirante(),
                            solicitudes.get(i).getNombre(), solicitudes.get(i).getHabilidades(),
                            solicitudes.get(i).getEstudios(), solicitudes.get(i).getExperiencia(),
                            solicitudes.get(i).getIdSector());

                    server.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
                } else if (sector.contains("Cientifico") || sector.contains("Fisico")) {
                    System.out.println("Sector: PROFESIONALES CIENTIFICOS E INTELECTUALES");

                    solicitudServer = String.format("Aspirante-%d-%s-%s-%s-%d-%d", solicitudes.get(i).getIdAspirante(),
                            solicitudes.get(i).getNombre(), solicitudes.get(i).getHabilidades(),
                            solicitudes.get(i).getEstudios(), solicitudes.get(i).getExperiencia(),
                            solicitudes.get(i).getIdSector());

                    server.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
                } else if (sector.contains("Ingeniero") || sector.contains("Tecnico") || sector.contains("Operario")
                        || sector.contains("Asistente")) {
                    System.out.println("Sector: TECNICOS Y PROFESIONALES");

                    solicitudServer = String.format("Aspirante-%d-%s-%s-%s-%d-%d", solicitudes.get(i).getIdAspirante(),
                            solicitudes.get(i).getNombre(), solicitudes.get(i).getHabilidades(),
                            solicitudes.get(i).getEstudios(), solicitudes.get(i).getExperiencia(),
                            solicitudes.get(i).getIdSector());

                    server.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
                } else if (sector.contains("Administrativo") || sector.contains("Secretaria")
                        || sector.contains("Recepcionista") || sector.contains("Auxiliar")) {
                    System.out.println("Sector: PERSONAL DE APOYO ADMNISTRATIVO");

                    solicitudServer = String.format("Aspirante-%d-%s-%s-%s-%d-%d", solicitudes.get(i).getIdAspirante(),
                            solicitudes.get(i).getNombre(), solicitudes.get(i).getHabilidades(),
                            solicitudes.get(i).getEstudios(), solicitudes.get(i).getExperiencia(),
                            solicitudes.get(i).getIdSector());

                    server.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
                } else if (sector.contains("Agricultor") || sector.contains("Pesquero")) {
                    System.out.println("Sector: AGRICULTORES FORESTALES Y PESQUEROS");

                    solicitudServer = String.format("Aspirante-%d-%s-%s-%s-%d-%d", solicitudes.get(i).getIdAspirante(),
                            solicitudes.get(i).getNombre(), solicitudes.get(i).getHabilidades(),
                            solicitudes.get(i).getEstudios(), solicitudes.get(i).getExperiencia(),
                            solicitudes.get(i).getIdSector());

                    server.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
                }
                byte[] reply = server.recv(0);

                System.out.println(new String(reply, ZMQ.CHARSET));
            }
        }
    }

    public static void agregarOferta(StringTokenizer token, ArrayList<Oferta> ofertas, ZContext context, String sector,
            String ofertaServer) {
        Oferta temp2 = new Oferta();

        temp2.setId(Integer.valueOf(token.nextToken()));
        temp2.setIdEmpleador(Integer.valueOf(token.nextToken()));
        temp2.setDescripcion(token.nextToken());
        temp2.setCargo(token.nextToken());
        temp2.setSueldo(Integer.valueOf(token.nextToken()));
        ofertas.add(temp2);

        if (ofertas.size() > 0) {
            // ZMQ.Socket server2 = context.createSocket(SocketType.REQ);
            ZMQ.Socket server21 = context.createSocket(SocketType.REQ);
            ZMQ.Socket server22 = context.createSocket(SocketType.REQ);
            ZMQ.Socket server23 = context.createSocket(SocketType.REQ);
            // server2.connect("tcp://25.12.51.131:1098");
            // server2.connect("tcp://127.0.0.1:1098");
            server21.connect("tcp://127.0.0.1:1101");
            server22.connect("tcp://127.0.0.1:1102");
            server23.connect("tcp://127.0.0.1:1103");
            for (int i = 0; i < ofertas.size(); i++) {

                sector = ofertas.get(i).getCargo();

                if (sector.contains("Director") || sector.contains("Gerente") || sector.contains("Administrador")
                        || sector.contains("Supervisor") || sector.contains("Jefe")) {
                    System.out.println("Sector: DIRECTORES Y GERENTES");

                    ofertas.get(i).setSector("DIRECTORES Y GERENTES");

                    ofertaServer = String.format("Oferta-%d-%s-%d-%s-%s-%d", ofertas.get(i).getId(),
                            ofertas.get(i).getSector(), ofertas.get(i).getIdEmpleador(),
                            ofertas.get(i).getDescripcion(), ofertas.get(i).getCargo(), ofertas.get(i).getSueldo());

                    // server2.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                    server21.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                    server22.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                    server23.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                } else if (sector.contains("Cientifico") || sector.contains("Fisico")) {
                    System.out.println("Sector: PROFESIONALES CIENTIFICOS E INTELECTUALES");

                    ofertas.get(i).setSector("PROFESIONALES CIENTIFICOS E INTELECTUALES");

                    ofertaServer = String.format("Oferta-%d-%s-%d-%s-%s-%d", ofertas.get(i).getId(),
                            ofertas.get(i).getSector(), ofertas.get(i).getIdEmpleador(),
                            ofertas.get(i).getDescripcion(), ofertas.get(i).getCargo(), ofertas.get(i).getSueldo());

                    // server2.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                    server21.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                    server22.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                    server23.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                } else if (sector.contains("Ingeniero") || sector.contains("Tecnico") || sector.contains("Operario")
                        || sector.contains("Asistente")) {
                    System.out.println("Sector: TECNICOS Y PROFESIONALES");

                    ofertas.get(i).setSector("TECNICOS Y PROFESIONALES");

                    ofertaServer = String.format("Oferta-%d-%s-%d-%s-%s-%d", ofertas.get(i).getId(),
                            ofertas.get(i).getSector(), ofertas.get(i).getIdEmpleador(),
                            ofertas.get(i).getDescripcion(), ofertas.get(i).getCargo(), ofertas.get(i).getSueldo());

                    // server2.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                    server21.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                    server22.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                    server23.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                } else if (sector.contains("Administrativo") || sector.contains("Secretaria")
                        || sector.contains("Recepcionista") || sector.contains("Auxiliar")) {
                    System.out.println("Sector: PERSONAL DE APOYO ADMNISTRATIVO");

                    ofertas.get(i).setSector("PERSONAL DE APOYO ADMNISTRATIVO");

                    ofertaServer = String.format("Oferta-%d-%s-%d-%s-%s-%d", ofertas.get(i).getId(),
                            ofertas.get(i).getSector(), ofertas.get(i).getIdEmpleador(),
                            ofertas.get(i).getDescripcion(), ofertas.get(i).getCargo(), ofertas.get(i).getSueldo());

                    // server2.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                    server21.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                    server22.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                    server23.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                } else if (sector.contains("Agricultor") || sector.contains("Pesquero")) {
                    System.out.println("Sector: AGRICULTORES FORESTALES Y PESQUEROS");

                    ofertas.get(i).setSector("AGRICULTORES FORESTALES Y PESQUEROS");

                    ofertaServer = String.format("Oferta-%d-%s-%d-%s-%s-%d", ofertas.get(i).getId(),
                            ofertas.get(i).getSector(), ofertas.get(i).getIdEmpleador(),
                            ofertas.get(i).getDescripcion(), ofertas.get(i).getCargo(), ofertas.get(i).getSueldo());

                    // server2.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                    server21.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                    server22.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                    server23.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                }
                // byte[] reply = server2.recv(0);
                byte[] reply1 = server21.recv(0);
                byte[] reply2 = server22.recv(0);
                byte[] reply3 = server23.recv(0);

                // System.out.println(new String(reply, ZMQ.CHARSET));
                System.out.println(new String(reply1, ZMQ.CHARSET));
                System.out.println(new String(reply2, ZMQ.CHARSET));
                System.out.println(new String(reply3, ZMQ.CHARSET));
                System.out.println();

            }
            ofertas.clear();
        }

    }
}
