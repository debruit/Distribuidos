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
public class BackupFiltro {

  static String[] serversIps = { "tcp://127.0.0.1:1101", "tcp://127.0.0.1:1102", "tcp://127.0.0.1:1103" };
  // static String[] serversIps = { "tcp://25.83.21.137:1101",
  // "tcp://25.12.51.131:1102", "tcp://127.0.0.1:1103" };
  static String aspiranteIp = "";
  static String empleadorIp = "";

  public static void main(String args[]) {
    ArrayList<Aspirante> solicitudes = new ArrayList<>();
    ArrayList<Oferta> ofertas = new ArrayList<>();
    try (ZContext context = new ZContext()) {
      System.out.println("Corriendo backup filtro...");

      // Socket con filtro
      ZMQ.Socket server = context.createSocket(SocketType.REQ);
      server.connect("tcp://127.0.0.1:2777");
      server.setReceiveTimeOut(5000);

      while (!Thread.currentThread().isInterrupted()) {
        String ping = "PingBackup";
        server.send(ping.getBytes(ZMQ.CHARSET), 0);
        System.out.println("Se envia ping a Filtro");
        byte[] reply = server.recv(0);

        if (null == reply) {
          System.out.println("NO llego respuesta del filtro, se iniciará Backup del filtro");
          retomarFiltro(solicitudes, ofertas);
        } else {
          solicitudes.clear();
          ofertas.clear();
          String msjRecibido = new String(reply, ZMQ.CHARSET);
          String arrayOfertas = msjRecibido.substring(0, msjRecibido.indexOf("|"));
          String arraySolicitudes = msjRecibido.substring(msjRecibido.indexOf("|") + 1);

          StringTokenizer tokenArrOfertas = new StringTokenizer(arrayOfertas, "_");
          while (tokenArrOfertas.hasMoreTokens()) {
            String ofertaStr = tokenArrOfertas.nextToken();
            StringTokenizer tokenOferta = new StringTokenizer(ofertaStr, "-");

            Oferta ofertaRecibida = new Oferta();
            ofertaRecibida.setId(Integer.valueOf(tokenOferta.nextToken()));
            ofertaRecibida.setIdSector(Integer.valueOf(tokenOferta.nextToken()));
            // ofertaRecibida.setIdSector(tokenOferta.nextToken());
            ofertaRecibida.setIdEmpleador(Integer.valueOf(tokenOferta.nextToken()));
            ofertaRecibida.setDescripcion(tokenOferta.nextToken());
            ofertaRecibida.setCargo(tokenOferta.nextToken());
            ofertaRecibida.setSueldo(Integer.valueOf(tokenOferta.nextToken()));
            ofertas.add(ofertaRecibida);
          }
          StringTokenizer tokenArrSolicitudes = new StringTokenizer(arraySolicitudes, "_");
          while (tokenArrSolicitudes.hasMoreTokens()) {
            String solicitudStr = tokenArrSolicitudes.nextToken();
            StringTokenizer tokenSolicitud = new StringTokenizer(solicitudStr, "-");

            Aspirante solicitudRecibida = new Aspirante();
            solicitudRecibida.setIdAspirante(Integer.valueOf(tokenSolicitud.nextToken()));
            solicitudRecibida.setNombre(tokenSolicitud.nextToken());
            solicitudRecibida.setHabilidades(tokenSolicitud.nextToken());
            solicitudRecibida.setEstudios(tokenSolicitud.nextToken());
            solicitudRecibida.setExperiencia(Integer.valueOf(tokenSolicitud.nextToken()));
            solicitudRecibida.setIdSector(Integer.valueOf(tokenSolicitud.nextToken()));
            solicitudes.add(solicitudRecibida);
          }
          System.out.println(new String(reply, ZMQ.CHARSET));
        }

        Thread.sleep(5000);
      }
    } catch (Exception e) {
      System.err.println("Server System exception: " + e);
    }
  }

  public static void enviarInfoBackup(ZContext context) {
    ArrayList<Aspirante> solicitudes = new ArrayList<>();
    ArrayList<Oferta> ofertas = new ArrayList<>();
    // try (ZContext context = new ZContext()) {
    // Socket con backupfiltro
    ZMQ.Socket backfiltro = context.createSocket(SocketType.REP);
    backfiltro.bind("tcp://127.0.0.1:2777");
    // backfiltro.setReceiveTimeOut(6000);

    // while (!Thread.currentThread().isInterrupted()) {
    String msjRecibido = backfiltro.recvStr(0).trim();
    System.out.println("BackupFiltro: " + msjRecibido);
    // if (msjRecibido.equals("PingBackup"))
    backfiltro.send(getInfoBackup(solicitudes, ofertas).getBytes(ZMQ.CHARSET), 0);
    // Thread.sleep(1000);
    // }
    // } catch (Exception e) {
    // System.err.println("Server System exception: " + e);
    // }
  }

  public static String getInfoBackup(ArrayList<Aspirante> solicitudes, ArrayList<Oferta> ofertas) {
    String msj = "";
    for (int i = 0; i < solicitudes.size(); i++) {
      String sol = String.format("Aspirante-%d-%s-%s-%s-%d-%d", solicitudes.get(i).getIdAspirante(),
          solicitudes.get(i).getNombre(), solicitudes.get(i).getHabilidades(), solicitudes.get(i).getEstudios(),
          solicitudes.get(i).getExperiencia(), solicitudes.get(i).getIdSector());
      msj = msj + "_" + sol;
    }
    msj = msj + "|";
    for (int i = 0; i < solicitudes.size(); i++) {
      String ofer = String.format("Oferta-%d-%d-%d-%s-%s-%d-%d-%s-%s", ofertas.get(i).getId(),
          ofertas.get(i).getIdSector(), ofertas.get(i).getIdEmpleador(), ofertas.get(i).getDescripcion(),
          ofertas.get(i).getCargo(), ofertas.get(i).getSueldo(), ofertas.get(i).getExperiencia(),
          ofertas.get(i).getHabilidades(), ofertas.get(i).getEstudios());
      msj = msj + "_" + ofer;
    }
    if (msj.equals("")) {
      msj = "|";
    }
    return msj;
  }

  public static void retomarFiltro(ArrayList<Aspirante> solicitudes, ArrayList<Oferta> ofertas) {
    String solicitudServer = "", sector = "";

    String ofertaServer = "";

    boolean doneOferta = false, doneSolicitud = false;

    try (ZContext context = new ZContext()) {
      System.out.println("Corriendo filtro...");
      ZMQ.Socket subscriber = context.createSocket(SocketType.SUB);
      // ZMQ.Socket subscriber2 = context.createSocket(SocketType.SUB);
      // subscriber.connect("tcp://25.12.51.131:1099");
      subscriber.connect("tcp://127.0.0.1:1099");
      // subscriber2.connect("tcp://127.0.0.1:1234");

      String solicitud = "0";

      subscriber.subscribe(solicitud.getBytes(ZMQ.CHARSET));
      // subscriber2.subscribe(solicitud.getBytes(ZMQ.CHARSET));

      while (true) {

        String mensaje = subscriber.recvStr(0).trim();

        StringTokenizer token = new StringTokenizer(mensaje, "-");

        Integer.valueOf(token.nextToken());

        if (token.nextToken().equals("Aspirante")) {
          doneSolicitud = agregarSolicitud(token, solicitudes, context, sector, solicitudServer);
        } else {
          doneOferta = agregarOferta(token, ofertas, context, sector, ofertaServer);
        }

        // if (doneSolicitud && doneOferta) {
        // notificaciones(context);
        // }

      }

    } catch (Exception e) {
      System.err.println(" System exception: " + e);
    }
  }

  public static void notificaciones(ZContext context) {
    // Request a todos los servidores por vacantes
    // Respuesta de todos los servidores
    byte[] reply1 = null;// server21.recv(0);
    String hayVacante = new String(reply1, ZMQ.CHARSET);
    if (hayVacante.contains("1")) {
      ZMQ.Socket vacante = context.createSocket(SocketType.PUB);
      vacante.bind("tcp://*:2099");
      vacante.bind("ipc://vacante");

      // Tokenizar la respuesta del servidor

      // Id del sector y oferta de la vacante(Sale de la tokenizacion)
      int idSector = 0, idOferta = 0;
      // Construir el mensaje de la vacante a enviar al aspirante
      String vacanteSector = String.format("%d-%s", idSector);
      vacante.send(vacanteSector, 0);

      // Respuesta del aspirante
      ZMQ.Socket respuestaSocket = context.createSocket(SocketType.SUB);
      respuestaSocket.connect("tcp://127.0.0.1:3099");

      //
      String acepta = "0";

      respuestaSocket.subscribe(acepta.getBytes(ZMQ.CHARSET));

      String response = respuestaSocket.recvStr(0).trim();

      String acepto = "";

      ZMQ.Socket respuestaEmpleador = context.createSocket(SocketType.PUB);
      respuestaEmpleador.bind("tcp://*:4099");
      respuestaEmpleador.bind("ipc://empleador");

      if (response.contains("y")) {
        acepto = "Aceptó la oferta";
      } else {
        acepto = "Rechazó la oferta";
      }

      int idEmpleador = 0;
      // Construir el mensaje de la notificacion a enviar al empleador
      String responseEmpleador = String.format("%d-El aspirante: -%s-%d", idEmpleador, acepto, idOferta);
      respuestaEmpleador.send(responseEmpleador, 0);
    }
  }

  public static boolean agregarSolicitud(StringTokenizer token, ArrayList<Aspirante> solicitudes, ZContext context,
      String sector, String solicitudServer) {
    Aspirante temp = new Aspirante();

    temp.setIdAspirante(Integer.valueOf(token.nextToken()));
    temp.setNombre(token.nextToken());
    temp.setHabilidades(token.nextToken());
    temp.setEstudios(token.nextToken());
    temp.setExperiencia(Integer.valueOf(token.nextToken()));
    temp.setIdSector(Integer.valueOf(token.nextToken()));
    solicitudes.add(temp);

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

      server1.setSendTimeOut(2000);
      server1.setReceiveTimeOut(2000);
      server2.setSendTimeOut(2000);
      server2.setReceiveTimeOut(2000);
      server3.setSendTimeOut(2000);
      server3.setReceiveTimeOut(2000);
      for (int i = 0; i < solicitudes.size(); i++) {

        sector = solicitudes.get(i).getHabilidades();

        if (sector.contains("Director") || sector.contains("Gerente") || sector.contains("Administrador")
            || sector.contains("Supervisor") || sector.contains("Jefe")) {
          System.out.println("Sector: DIRECTORES Y GERENTES");

          solicitudServer = String.format("Aspirante-%d-%s-%s-%s-%d-%d", solicitudes.get(i).getIdAspirante(),
              solicitudes.get(i).getNombre(), solicitudes.get(i).getHabilidades(), solicitudes.get(i).getEstudios(),
              solicitudes.get(i).getExperiencia(), solicitudes.get(i).getIdSector());

          // server.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
          server1.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
          server2.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
          server3.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
        } else if (sector.contains("Cientifico") || sector.contains("Fisico")) {
          System.out.println("Sector: PROFESIONALES CIENTIFICOS E INTELECTUALES");

          solicitudServer = String.format("Aspirante-%d-%s-%s-%s-%d-%d", solicitudes.get(i).getIdAspirante(),
              solicitudes.get(i).getNombre(), solicitudes.get(i).getHabilidades(), solicitudes.get(i).getEstudios(),
              solicitudes.get(i).getExperiencia(), solicitudes.get(i).getIdSector());

          // server.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
          server1.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
          server2.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
          server3.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
        } else if (sector.contains("Ingeniero") || sector.contains("Tecnico") || sector.contains("Operario")
            || sector.contains("Asistente")) {
          System.out.println("Sector: TECNICOS Y PROFESIONALES");

          solicitudServer = String.format("Aspirante-%d-%s-%s-%s-%d-%d", solicitudes.get(i).getIdAspirante(),
              solicitudes.get(i).getNombre(), solicitudes.get(i).getHabilidades(), solicitudes.get(i).getEstudios(),
              solicitudes.get(i).getExperiencia(), solicitudes.get(i).getIdSector());

          // server.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
          server1.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
          server2.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
          server3.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
        } else if (sector.contains("Administrativo") || sector.contains("Secretaria")
            || sector.contains("Recepcionista") || sector.contains("Auxiliar")) {
          System.out.println("Sector: PERSONAL DE APOYO ADMNISTRATIVO");

          solicitudServer = String.format("Aspirante-%d-%s-%s-%s-%d-%d", solicitudes.get(i).getIdAspirante(),
              solicitudes.get(i).getNombre(), solicitudes.get(i).getHabilidades(), solicitudes.get(i).getEstudios(),
              solicitudes.get(i).getExperiencia(), solicitudes.get(i).getIdSector());

          // server.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
          server1.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
          server2.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
          server3.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
        } else if (sector.contains("Agricultor") || sector.contains("Pesquero")) {
          System.out.println("Sector: AGRICULTORES FORESTALES Y PESQUEROS");

          solicitudServer = String.format("Aspirante-%d-%s-%s-%s-%d-%d", solicitudes.get(i).getIdAspirante(),
              solicitudes.get(i).getNombre(), solicitudes.get(i).getHabilidades(), solicitudes.get(i).getEstudios(),
              solicitudes.get(i).getExperiencia(), solicitudes.get(i).getIdSector());

          // server.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
          server1.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
          server2.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
          server3.send(solicitudServer.getBytes(ZMQ.CHARSET), 0);
        }
        // byte[] reply = server.recv(0);
        byte[] reply1 = server1.recv(0);
        byte[] reply2 = server2.recv(0);
        byte[] reply3 = server3.recv(0);
        if (null == reply1) {
          System.out.println("NO llego respuesta de server 1");
        } else {
          System.out.println(new String(reply1, ZMQ.CHARSET));
        }
        if (null == reply2) {
          System.out.println("NO llego respuesta de server 2");
        } else {
          System.out.println(new String(reply2, ZMQ.CHARSET));
        }
        if (null == reply3) {
          System.out.println("NO llego respuesta de server 3");
        } else {
          System.out.println(new String(reply3, ZMQ.CHARSET));
        }
        System.out.println();
      }
      solicitudes.clear();
    }
    return true;
  }

  public static boolean agregarOferta(StringTokenizer token, ArrayList<Oferta> ofertas, ZContext context, String sector,
      String ofertaServer) {
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

      server21.setSendTimeOut(2000);
      server21.setReceiveTimeOut(2000);
      server22.setSendTimeOut(2000);
      server22.setReceiveTimeOut(2000);
      server23.setSendTimeOut(2000);
      server23.setReceiveTimeOut(2000);
      for (int i = 0; i < ofertas.size(); i++) {

        sector = ofertas.get(i).getCargo();

        if (sector.contains("Director") || sector.contains("Gerente") || sector.contains("Administrador")
            || sector.contains("Supervisor") || sector.contains("Jefe")) {
          System.out.println("Sector: DIRECTORES Y GERENTES");

          ofertas.get(i).setSector(1);

          ofertaServer = String.format("Oferta-%d-%d-%d-%s-%s-%d-%d-%s-%s", ofertas.get(i).getId(),
              ofertas.get(i).getIdSector(), ofertas.get(i).getIdEmpleador(), ofertas.get(i).getDescripcion(),
              ofertas.get(i).getCargo(), ofertas.get(i).getSueldo(), ofertas.get(i).getExperiencia(),
              ofertas.get(i).getHabilidades(), ofertas.get(i).getEstudios());

          // server2.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
          server21.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
          server22.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
          server23.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
        } else if (sector.contains("Cientifico") || sector.contains("Fisico")) {
          System.out.println("Sector: PROFESIONALES CIENTIFICOS E INTELECTUALES");

          ofertas.get(i).setSector(2);

          ofertaServer = String.format("Oferta-%d-%d-%d-%s-%s-%d-%d-%s-%s", ofertas.get(i).getId(),
              ofertas.get(i).getIdSector(), ofertas.get(i).getIdEmpleador(), ofertas.get(i).getDescripcion(),
              ofertas.get(i).getCargo(), ofertas.get(i).getSueldo(), ofertas.get(i).getExperiencia(),
              ofertas.get(i).getHabilidades(), ofertas.get(i).getEstudios());

          // server2.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
          server21.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
          server22.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
          server23.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
        } else if (sector.contains("Ingeniero") || sector.contains("Tecnico") || sector.contains("Operario")
            || sector.contains("Asistente")) {
          System.out.println("Sector: TECNICOS Y PROFESIONALES");

          ofertas.get(i).setSector(3);

          ofertaServer = String.format("Oferta-%d-%d-%d-%s-%s-%d-%d-%s-%s", ofertas.get(i).getId(),
              ofertas.get(i).getIdSector(), ofertas.get(i).getIdEmpleador(), ofertas.get(i).getDescripcion(),
              ofertas.get(i).getCargo(), ofertas.get(i).getSueldo(), ofertas.get(i).getExperiencia(),
              ofertas.get(i).getHabilidades(), ofertas.get(i).getEstudios());

          // server2.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
          server21.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
          server22.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
          server23.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
        } else if (sector.contains("Administrativo") || sector.contains("Secretaria")
            || sector.contains("Recepcionista") || sector.contains("Auxiliar")) {
          System.out.println("Sector: PERSONAL DE APOYO ADMNISTRATIVO");

          ofertas.get(i).setSector(4);

          ofertaServer = String.format("Oferta-%d-%d-%d-%s-%s-%d-%d-%s-%s", ofertas.get(i).getId(),
              ofertas.get(i).getIdSector(), ofertas.get(i).getIdEmpleador(), ofertas.get(i).getDescripcion(),
              ofertas.get(i).getCargo(), ofertas.get(i).getSueldo(), ofertas.get(i).getExperiencia(),
              ofertas.get(i).getHabilidades(), ofertas.get(i).getEstudios());

          // server2.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
          server21.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
          server22.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
          server23.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
        } else if (sector.contains("Agricultor") || sector.contains("Pesquero")) {
          System.out.println("Sector: AGRICULTORES FORESTALES Y PESQUEROS");

          ofertas.get(i).setSector(5);

          ofertaServer = String.format("Oferta-%d-%d-%d-%s-%s-%d-%d-%s-%s", ofertas.get(i).getId(),
              ofertas.get(i).getIdSector(), ofertas.get(i).getIdEmpleador(), ofertas.get(i).getDescripcion(),
              ofertas.get(i).getCargo(), ofertas.get(i).getSueldo(), ofertas.get(i).getExperiencia(),
              ofertas.get(i).getHabilidades(), ofertas.get(i).getEstudios());

          // server2.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
          server21.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
          server22.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
          server23.send(ofertaServer.getBytes(ZMQ.CHARSET), 0);
        }
        // byte[] reply = server2.recv(0);
        byte[] reply1 = server21.recv(0);
        byte[] reply2 = server22.recv(0);
        byte[] reply3 = server23.recv(0);

        if (null == reply1) {
          System.out.println("NO llego respuesta de server 1");
        } else {
          System.out.println(new String(reply1, ZMQ.CHARSET));
        }
        if (null == reply2) {
          System.out.println("NO llego respuesta de server 2");
        } else {
          System.out.println(new String(reply2, ZMQ.CHARSET));
        }
        if (null == reply3) {
          System.out.println("NO llego respuesta de server 3");
        } else {
          System.out.println(new String(reply3, ZMQ.CHARSET));
        }
        System.out.println();

      }
      ofertas.clear();
    }
    return true;

  }

}