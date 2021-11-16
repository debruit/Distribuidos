package com.entrega1;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
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

    static String[] serversIps = { "tcp://127.0.0.1:1101", "tcp://127.0.0.1:1102", "tcp://127.0.0.1:1103" };
    // static String[] serversIps = { "tcp://25.83.21.137:1101",
    // "tcp://25.12.51.131:1102", "tcp://127.0.0.1:1103" };
    static String aspiranteIp = "";
    static String empleadorIp = "";
    static ArrayList<String> respuestas = new ArrayList<String>();
    // ArrayList<Aspirante> solicitudes = new ArrayList<Aspirante>();
    // ArrayList<Oferta> ofertas = getBackupOfertas();

    public static void main(String args[]) {

        ArrayList<Aspirante> solicitudes = new ArrayList<Aspirante>();
        String solicitudServer = "", sector = "";

        ArrayList<Oferta> ofertas = new ArrayList<Oferta>();
        // ArrayList<Oferta> ofertas = getBackupOfertas();
        String ofertaServer = "";

        try (ZContext context = new ZContext()) {
            System.out.println("Corriendo filtro...");
            ZMQ.Socket subscriber = context.createSocket(SocketType.SUB);
            subscriber.connect("tcp://127.0.0.1:1099");

            String solicitud = "0";

            subscriber.subscribe(solicitud.getBytes(ZMQ.CHARSET));
            // subscriber2.subscribe(solicitud.getBytes(ZMQ.CHARSET));

            subscriber.setReceiveTimeOut(1000);
            ZMQ.Socket backfiltro = context.createSocket(SocketType.REP);
            backfiltro.bind("tcp://127.0.0.1:2777");
            backfiltro.setReceiveTimeOut(200);
            // backfiltro.setSendTimeOut(1000);
            while (true) {
                String msjRecibido = backfiltro.recvStr(0);
                if (msjRecibido != null) {
                    msjRecibido.trim();
                    System.out.println("BackupFiltro: " + msjRecibido);
                    backfiltro.send(getInfoBackup(solicitudes, ofertas).getBytes(ZMQ.CHARSET), 0);
                }

                String mensaje = subscriber.recvStr(0);
                if (mensaje != null) {
                    mensaje.trim();
                    boolean done = true;

                    StringTokenizer token = new StringTokenizer(mensaje, "-");

                    Integer.valueOf(token.nextToken());

                    if (token.nextToken().equals("Aspirante")) {
                        // done = false;
                        agregarSolicitud(token, solicitudes, context, sector, solicitudServer);
                    } else {
                        done = false;
                        agregarOferta(token, ofertas, context, sector, ofertaServer);
                    }

                    if (done) {
                        Thread.sleep(2000);
                        System.out.println("Enviando notificacion...");
                        notificaciones(context);
                        respuestas.clear();
                    }

                }
            }

        } catch (Exception e) {
            System.err.println(" System exception: " + e);
        }
    }

    public static String getInfoBackup(ArrayList<Aspirante> solicitudes, ArrayList<Oferta> ofertas) {
        String msj = "";
        for (int i = 0; i < ofertas.size(); i++) {
            String ofer = String.format("%d-%d-%d-%s-%s-%d-%d-%s-%s", ofertas.get(i).getId(),
                    ofertas.get(i).getIdSector(), ofertas.get(i).getIdEmpleador(), ofertas.get(i).getDescripcion(),
                    ofertas.get(i).getCargo(), ofertas.get(i).getSueldo(), ofertas.get(i).getExperiencia(),
                    ofertas.get(i).getHabilidades(), ofertas.get(i).getEstudios());
            msj = msj + ofer + "_";
        }
        // msj = msj.substring(0, msj.indexOf("_"))
        msj = msj + "|";
        for (int i = 0; i < solicitudes.size(); i++) {
            String sol = String.format("%d-%s-%s-%s-%d-%d", solicitudes.get(i).getIdAspirante(),
                    solicitudes.get(i).getNombre(), solicitudes.get(i).getHabilidades(),
                    solicitudes.get(i).getEstudios(), solicitudes.get(i).getExperiencia(),
                    solicitudes.get(i).getIdSector());
            msj = msj + "_" + sol;
        }
        // if (msj.equals("")) {
        // msj = "|";
        // }
        return msj;
    }

    public static void notificaciones(ZContext context) throws InterruptedException {

        // Request a todos los servidores por vacantes
        for (String reply : respuestas) {
            if (reply.contains("V")) {
                ZMQ.Socket vacante = context.createSocket(SocketType.PUB);
                vacante.bind("tcp://*:2099");
                vacante.bind("ipc://vacante");

                // Tokenizar la respuesta del servidor
                StringTokenizer token = new StringTokenizer(reply, "-");
                token.nextToken();
                int idSector = Integer.valueOf(token.nextToken());
                int idEmpleador = Integer.valueOf(token.nextToken());
                String oferta = token.nextToken();
                int idOferta = Integer.valueOf(token.nextToken());
                String nombre = token.nextToken();
                String cargo = token.nextToken();
                int sueldo = Integer.valueOf(token.nextToken());
                int experiencia = Integer.valueOf(token.nextToken());
                String habilidades = token.nextToken();
                String estudios = token.nextToken();
                Oferta ofertas= new Oferta();
                ofertas.setId(idOferta);
                ofertas.setDescripcion(oferta);
                ofertas.setIdEmpleador(idEmpleador);
                ofertas.setIdSector(idSector);
                ofertas.setCargo(cargo);
                ofertas.setSueldo(sueldo);
                ofertas.setExperiencia(experiencia);
                ofertas.setHabilidades(habilidades);
                ofertas.setEstudios(estudios);

                int filter = 0;
                // Construir el mensaje de la vacante a enviar al aspirante
                String vacanteSector = String.format("%d-%d-%s", filter, idSector, oferta);
                vacante.send(vacanteSector, 0);

                Thread.sleep(2000);

                // Respuesta del aspirante
                ZMQ.Socket respuestaSocket = context.createSocket(SocketType.SUB);
                respuestaSocket.connect("tcp://127.0.0.1:3099");

                //
                String acepta = "0";

                respuestaSocket.subscribe(acepta.getBytes(ZMQ.CHARSET));

                String response = respuestaSocket.recvStr(0).trim();

                String acepto = "";

                if (response.contains("y")) {
                    acepto = "Aceptó la oferta";

                    ZMQ.Socket server1 = context.createSocket(SocketType.REQ);
                    ZMQ.Socket server2 = context.createSocket(SocketType.REQ);
                    ZMQ.Socket server3 = context.createSocket(SocketType.REQ);
                    // server.connect("tcp://25.12.51.131:1098");
                    // server.connect("tcp://127.0.0.1:1098");
                    server1.connect(serversIps[0]);
                    server2.connect(serversIps[1]);
                    server3.connect(serversIps[2]);

                    String ofertaServer = String.format("BorrarOferta-%d-%d-%d-%s-%s-%d-%d-%s-%s", ofertas.getId(),
                            ofertas.getIdSector(), ofertas.getIdEmpleador(),
                            ofertas.getDescripcion(), ofertas.getCargo(), ofertas.getSueldo(),
                            ofertas.getExperiencia(), ofertas.getHabilidades(),
                            ofertas.getEstudios());

                    server1.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                    server2.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                    server3.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                } else {
                    acepto = "Rechazó la oferta";
                }

                ZMQ.Socket respuestaEmpleador = context.createSocket(SocketType.PUB);
                respuestaEmpleador.bind("tcp://*:4099");
                respuestaEmpleador.bind("ipc://empleador");

                // Construir el mensaje de la notificacion a enviar al empleador
                String responseEmpleador = String.format("%d-%d-El aspirante -%s -%s: -%d", filter, idEmpleador, nombre,
                        acepto, idOferta);
                respuestaEmpleador.send(responseEmpleador, 0);

                Thread.sleep(2000);

                vacante.close();
                respuestaSocket.close();
                respuestaEmpleador.close();

            }
        }
    }

    public static boolean agregarSolicitud(StringTokenizer token, ArrayList<Aspirante> solicitudes, ZContext context,
            String sector, String solicitudServer) throws InterruptedException {
        Aspirante temp = new Aspirante();

        temp.setIdAspirante(Integer.valueOf(token.nextToken()));
        temp.setNombre(token.nextToken());
        temp.setHabilidades(token.nextToken());
        temp.setEstudios(token.nextToken());
        temp.setExperiencia(Integer.valueOf(token.nextToken()));
        temp.setIdSector(Integer.valueOf(token.nextToken()));
        solicitudes.add(temp);
        setBackupAspirantes(solicitudes);

        if (solicitudes.size() > 0) {

            // ZMQ.Socket server = context.createSocket(SocketType.REQ);
            ZMQ.Socket server1 = context.createSocket(SocketType.REQ);
            ZMQ.Socket server2 = context.createSocket(SocketType.REQ);
            ZMQ.Socket server3 = context.createSocket(SocketType.REQ);
            // server.connect("tcp://25.12.51.131:1098");
            // server.connect("tcp://127.0.0.1:1098");
            server1.connect(serversIps[0]);
            server2.connect(serversIps[1]);
            server3.connect(serversIps[2]);
            byte[] reply1 = null, reply2 = null, reply3 = null;
            for (int i = 0; i < solicitudes.size(); i++) {

                sector = solicitudes.get(i).getHabilidades();

                if (sector.contains("Director") || sector.contains("Gerente") || sector.contains("Administrador")
                        || sector.contains("Supervisor") || sector.contains("Jefe")) {
                    System.out.println("Sector: DIRECTORES Y GERENTES");

                    solicitudServer = String.format("Aspirante-%d-%s-%s-%s-%d-%d", solicitudes.get(i).getIdAspirante(),
                            solicitudes.get(i).getNombre(), solicitudes.get(i).getHabilidades(),
                            solicitudes.get(i).getEstudios(), solicitudes.get(i).getExperiencia(),
                            solicitudes.get(i).getIdSector());

                    // server.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
                    // if(i)
                    server1.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
                    server2.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
                    server3.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
                } else if (sector.contains("Cientifico") || sector.contains("Fisico")) {
                    System.out.println("Sector: PROFESIONALES CIENTIFICOS E INTELECTUALES");

                    solicitudServer = String.format("Aspirante-%d-%s-%s-%s-%d-%d", solicitudes.get(i).getIdAspirante(),
                            solicitudes.get(i).getNombre(), solicitudes.get(i).getHabilidades(),
                            solicitudes.get(i).getEstudios(), solicitudes.get(i).getExperiencia(),
                            solicitudes.get(i).getIdSector());

                    // server.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
                    server1.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
                    server2.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
                    server3.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
                } else if (sector.contains("Ingeniero") || sector.contains("Tecnico") || sector.contains("Operario")
                        || sector.contains("Asistente")) {
                    System.out.println("Sector: TECNICOS Y PROFESIONALES");

                    solicitudServer = String.format("Aspirante-%d-%s-%s-%s-%d-%d", solicitudes.get(i).getIdAspirante(),
                            solicitudes.get(i).getNombre(), solicitudes.get(i).getHabilidades(),
                            solicitudes.get(i).getEstudios(), solicitudes.get(i).getExperiencia(),
                            solicitudes.get(i).getIdSector());

                    // server.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
                    server1.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
                    server2.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
                    server3.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
                } else if (sector.contains("Administrativo") || sector.contains("Secretaria")
                        || sector.contains("Recepcionista") || sector.contains("Auxiliar")) {
                    System.out.println("Sector: PERSONAL DE APOYO ADMNISTRATIVO");

                    solicitudServer = String.format("Aspirante-%d-%s-%s-%s-%d-%d", solicitudes.get(i).getIdAspirante(),
                            solicitudes.get(i).getNombre(), solicitudes.get(i).getHabilidades(),
                            solicitudes.get(i).getEstudios(), solicitudes.get(i).getExperiencia(),
                            solicitudes.get(i).getIdSector());

                    // server.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
                    server1.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
                    server2.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
                    server3.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
                } else if (sector.contains("Agricultor") || sector.contains("Pesquero")) {
                    System.out.println("Sector: AGRICULTORES FORESTALES Y PESQUEROS");

                    solicitudServer = String.format("Aspirante-%d-%s-%s-%s-%d-%d", solicitudes.get(i).getIdAspirante(),
                            solicitudes.get(i).getNombre(), solicitudes.get(i).getHabilidades(),
                            solicitudes.get(i).getEstudios(), solicitudes.get(i).getExperiencia(),
                            solicitudes.get(i).getIdSector());

                    // server.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
                    server1.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
                    server2.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
                    server3.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
                }
                // byte[] reply = server.recv(0);
                reply1 = server1.recv(0);
                reply2 = server2.recv(0);
                reply3 = server3.recv(0);

                // System.out.println(new String(reply, ZMQ.CHARSET));

                respuestas.add(new String(reply1, ZMQ.CHARSET));
                respuestas.add(new String(reply2, ZMQ.CHARSET));
                respuestas.add(new String(reply3, ZMQ.CHARSET));
                System.out.println();
            }
            // Thread.sleep(6000);
            // notificaciones(context,reply1,reply2,reply3);
            solicitudes.clear();
            clearBackupAspirantes();
        }
        return true;
    }

    public static boolean agregarOferta(StringTokenizer token, ArrayList<Oferta> ofertas, ZContext context,
            String sector, String ofertaServer) {
        Oferta temp2 = new Oferta();

        temp2.setId(Integer.valueOf(token.nextToken()));
        temp2.setIdEmpleador(Integer.valueOf(token.nextToken()));
        temp2.setDescripcion(token.nextToken());
        temp2.setCargo(token.nextToken());
        temp2.setSueldo(Integer.valueOf(token.nextToken()));
        temp2.setExperiencia(Integer.valueOf(token.nextToken()));
        temp2.setHabilidades(token.nextToken());
        temp2.setEstudios(token.nextToken());
        ofertas.add(temp2);
        setBackupOfertas(ofertas);

        if (ofertas.size() > 0) {
            // ZMQ.Socket server2 = context.createSocket(SocketType.REQ);
            ZMQ.Socket server21 = context.createSocket(SocketType.REQ);
            ZMQ.Socket server22 = context.createSocket(SocketType.REQ);
            ZMQ.Socket server23 = context.createSocket(SocketType.REQ);
            // server2.connect("tcp://25.12.51.131:1098");
            // server2.connect("tcp://127.0.0.1:1098");
            server21.connect(serversIps[0]);
            server22.connect(serversIps[1]);
            server23.connect(serversIps[2]);
            for (int i = 0; i < ofertas.size(); i++) {

                sector = ofertas.get(i).getCargo();

                if (sector.contains("Director") || sector.contains("Gerente") || sector.contains("Administrador")
                        || sector.contains("Supervisor") || sector.contains("Jefe")) {
                    System.out.println("Sector: DIRECTORES Y GERENTES");

                    ofertas.get(i).setSector(1);

                    ofertaServer = String.format("Oferta-%d-%d-%d-%s-%s-%d-%d-%s-%s", ofertas.get(i).getId(),
                            ofertas.get(i).getIdSector(), ofertas.get(i).getIdEmpleador(),
                            ofertas.get(i).getDescripcion(), ofertas.get(i).getCargo(), ofertas.get(i).getSueldo(),
                            ofertas.get(i).getExperiencia(), ofertas.get(i).getHabilidades(),
                            ofertas.get(i).getEstudios());

                    // server2.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                    server21.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                    server22.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                    server23.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                } else if (sector.contains("Cientifico") || sector.contains("Fisico")) {
                    System.out.println("Sector: PROFESIONALES CIENTIFICOS E INTELECTUALES");

                    ofertas.get(i).setSector(2);

                    ofertaServer = String.format("Oferta-%d-%d-%d-%s-%s-%d-%d-%s-%s", ofertas.get(i).getId(),
                            ofertas.get(i).getIdSector(), ofertas.get(i).getIdEmpleador(),
                            ofertas.get(i).getDescripcion(), ofertas.get(i).getCargo(), ofertas.get(i).getSueldo(),
                            ofertas.get(i).getExperiencia(), ofertas.get(i).getHabilidades(),
                            ofertas.get(i).getEstudios());

                    // server2.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                    server21.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                    server22.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                    server23.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                } else if (sector.contains("Ingeniero") || sector.contains("Tecnico") || sector.contains("Operario")
                        || sector.contains("Asistente")) {
                    System.out.println("Sector: TECNICOS Y PROFESIONALES");

                    ofertas.get(i).setSector(3);

                    ofertaServer = String.format("Oferta-%d-%d-%d-%s-%s-%d-%d-%s-%s", ofertas.get(i).getId(),
                            ofertas.get(i).getIdSector(), ofertas.get(i).getIdEmpleador(),
                            ofertas.get(i).getDescripcion(), ofertas.get(i).getCargo(), ofertas.get(i).getSueldo(),
                            ofertas.get(i).getExperiencia(), ofertas.get(i).getHabilidades(),
                            ofertas.get(i).getEstudios());

                    // server2.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                    server21.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                    server22.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                    server23.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                } else if (sector.contains("Administrativo") || sector.contains("Secretaria")
                        || sector.contains("Recepcionista") || sector.contains("Auxiliar")) {
                    System.out.println("Sector: PERSONAL DE APOYO ADMNISTRATIVO");

                    ofertas.get(i).setSector(4);

                    ofertaServer = String.format("Oferta-%d-%d-%d-%s-%s-%d-%d-%s-%s", ofertas.get(i).getId(),
                            ofertas.get(i).getIdSector(), ofertas.get(i).getIdEmpleador(),
                            ofertas.get(i).getDescripcion(), ofertas.get(i).getCargo(), ofertas.get(i).getSueldo(),
                            ofertas.get(i).getExperiencia(), ofertas.get(i).getHabilidades(),
                            ofertas.get(i).getEstudios());

                    // server2.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                    server21.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                    server22.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                    server23.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
                } else if (sector.contains("Agricultor") || sector.contains("Pesquero")) {
                    System.out.println("Sector: AGRICULTORES FORESTALES Y PESQUEROS");

                    ofertas.get(i).setSector(5);

                    ofertaServer = String.format("Oferta-%d-%d-%d-%s-%s-%d-%d-%s-%s", ofertas.get(i).getId(),
                            ofertas.get(i).getIdSector(), ofertas.get(i).getIdEmpleador(),
                            ofertas.get(i).getDescripcion(), ofertas.get(i).getCargo(), ofertas.get(i).getSueldo(),
                            ofertas.get(i).getExperiencia(), ofertas.get(i).getHabilidades(),
                            ofertas.get(i).getEstudios());

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
            clearBackupOfertas();
            ofertas.clear();
        }
        return true;

    }

    public static void setBackupOfertas(ArrayList<Oferta> ofertas) {
        try {
            FileOutputStream fos = new FileOutputStream("OfertasBk.txt");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fos);
            objectOutputStream.writeInt(ofertas.size());
            for (Oferta oferta : ofertas) {
                objectOutputStream.writeObject(oferta);
            }
            objectOutputStream.close();
        } catch (Exception e) {
            System.out.println("Exception guardarEnBackup: " + e.getMessage());
        }
    }

    public static void setBackupAspirantes(ArrayList<Aspirante> solicitudes) {
        try {
            FileOutputStream fos = new FileOutputStream("AspirantesBk.txt");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fos);
            objectOutputStream.writeInt(solicitudes.size());
            for (Aspirante oferta : solicitudes) {
                objectOutputStream.writeObject(oferta);
            }
            objectOutputStream.close();
        } catch (Exception e) {
            System.out.println("Exception guardarEnBackup: " + e.getMessage());
        }
    }

    public static ArrayList<Oferta> getBackupOfertas() {
        ArrayList<Oferta> res = new ArrayList<>();
        try {
            FileInputStream fileInputStream = new FileInputStream("OfertasBk.txt");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            int trainCount = objectInputStream.readInt();
            for (int i = 0; i < trainCount; i++) {
                res.add((Oferta) objectInputStream.readObject());
            }
            System.out.println("Se recuperó de backup: ");
            System.out.println(res);
            fileInputStream.close();
            objectInputStream.close();
        } catch (Exception e) {
            System.out.println("Exception obtenerDeBackup: " + e.getMessage());
        }
        return res;
    }

    public static ArrayList<Aspirante> getBackupAspirantes() {
        ArrayList<Aspirante> res = new ArrayList<>();
        try {
            FileInputStream fileInputStream = new FileInputStream("AspirantesBk.txt");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            int trainCount = objectInputStream.readInt();
            for (int i = 0; i < trainCount; i++) {
                res.add((Aspirante) objectInputStream.readObject());
            }
            System.out.println("Se recuperó de backup: ");
            System.out.println(res);
            fileInputStream.close();
            objectInputStream.close();
        } catch (Exception e) {
            System.out.println("Exception obtenerDeBackup: " + e.getMessage());
        }
        return res;
    }

    public static void clearBackupOfertas() {
        try {
            FileWriter fw = new FileWriter("OfertasBk.txt", false);
            PrintWriter pw = new PrintWriter(fw, false);
            pw.flush();
            pw.close();
        } catch (Exception e) {
            System.out.println("Exception obtenerDeBackup: " + e.getMessage());
        }
    }

    public static void clearBackupAspirantes() {
        try {
            FileWriter fw = new FileWriter("AspirantesBk.txt", false);
            PrintWriter pw = new PrintWriter(fw, false);
            pw.flush();
            pw.close();
        } catch (Exception e) {
            System.out.println("Exception obtenerDeBackup: " + e.getMessage());
        }
    }

}
