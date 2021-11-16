package com.entrega1.Server;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;

import com.entrega1.Server.Modelo.*;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class ServerImpl {
    static Hashtable<String, Oferta> ht = new Hashtable<String, Oferta>();
    // static String[] info = { "85", "170", "255", "tcp://127.0.0.1:1102",
    // "tcp://127.0.0.1:1103", "tcp://*:1101" };
    static String[] info = { "170", "255", "85", "tcp://127.0.0.1:1103", "tcp://127.0.0.1:1101", "tcp://*:1102" };
    // static String[] info = { "255", "85", "170", "tcp://127.0.0.1:1101",
    // "tcp://127.0.0.1:1102", "tcp://*:1103" };

    static int serverId = Integer.valueOf(info[0]);
    static int sucesor = Integer.valueOf(info[1]);
    static int predecesor = Integer.valueOf(info[2]);

    static ZMQ.Socket server;

    static ZMQ.Socket sucesorServer;
    static String sucesorIP = info[3];

    static ZMQ.Socket antecesorServer;
    static String antecesorIP = info[4];

    public static void main(String args[]) throws Exception {
        inicializarDHT();

        try (ZContext context = new ZContext()) {
            System.out.println("Corriendo servidor " + serverId + " ...");

            // Socket con filtro
            server = context.createSocket(SocketType.REP);
            server.bind(info[5]);

            // Socket con sucesor
            sucesorServer = context.createSocket(SocketType.REQ);
            sucesorServer.connect(sucesorIP);

            // Socket con antecesor
            antecesorServer = context.createSocket(SocketType.REP);
            antecesorServer.connect(sucesorIP);

            while (!Thread.currentThread().isInterrupted()) {
                String msjRecibido = server.recvStr(0).trim();
                StringTokenizer tokenMsj = new StringTokenizer(msjRecibido, "-");
                String tipoMsj = tokenMsj.nextToken();
                if (tipoMsj.equals("Oferta")) {
                    String ofertaStr = msjRecibido.substring(msjRecibido.indexOf("-") + 1);
                    Oferta ofertaRecibida = new Oferta();
                    StringTokenizer tokenOferta = new StringTokenizer(ofertaStr, "-");
                    ofertaRecibida.setId(Integer.valueOf(tokenOferta.nextToken()));
                    ofertaRecibida.setIdSector(Integer.valueOf(tokenOferta.nextToken()));
                    // ofertaRecibida.setIdSector(tokenOferta.nextToken());
                    ofertaRecibida.setIdEmpleador(Integer.valueOf(tokenOferta.nextToken()));
                    ofertaRecibida.setDescripcion(tokenOferta.nextToken());
                    ofertaRecibida.setCargo(tokenOferta.nextToken());
                    ofertaRecibida.setSueldo(Integer.valueOf(tokenOferta.nextToken()));
                    ofertaRecibida.setExperiencia(Integer.valueOf(tokenOferta.nextToken()));
                    ofertaRecibida.setHabilidades(tokenOferta.nextToken());
                    ofertaRecibida.setEstudios(tokenOferta.nextToken());

                    String response = grabarOferta(ofertaRecibida, ofertaStr);
                    server.send(response.getBytes(ZMQ.CHARSET), 0);
                    System.out.println("--------------------------");
                    System.out.println("Oferta Respuesta: " + response + " ; Enviada al Filtro");
                    System.out.println("--------------------------");
                } else if (tipoMsj.equals("Aspirante")) {
                    String solicitudStr = msjRecibido.substring(msjRecibido.indexOf("-") + 1);
                    StringTokenizer tokenSolicitud = new StringTokenizer(solicitudStr, "-");
                    Aspirante solicitudRecibida = new Aspirante();
                    solicitudRecibida.setIdAspirante(Integer.valueOf(tokenSolicitud.nextToken()));
                    solicitudRecibida.setNombre(tokenSolicitud.nextToken());
                    solicitudRecibida.setHabilidades(tokenSolicitud.nextToken());
                    solicitudRecibida.setEstudios(tokenSolicitud.nextToken());
                    solicitudRecibida.setExperiencia(Integer.valueOf(tokenSolicitud.nextToken()));
                    solicitudRecibida.setIdSector(Integer.valueOf(tokenSolicitud.nextToken()));

                    String response = verificarVacantes(solicitudRecibida);
                    server.send(response.getBytes(ZMQ.CHARSET), 0);
                    System.out.println("--------------------------");
                    System.out.println("Aspirante Respuesta: " + response + " ; Enviada al Filtro");
                    System.out.println("--------------------------");
                } else if (tipoMsj.equals("BorrarOferta")) {
                    String ofertaStr = msjRecibido.substring(msjRecibido.indexOf("-") + 1);
                    Oferta ofertaRecibida = new Oferta();
                    StringTokenizer tokenOferta = new StringTokenizer(ofertaStr, "-");
                    ofertaRecibida.setId(Integer.valueOf(tokenOferta.nextToken()));
                    ofertaRecibida.setIdSector(Integer.valueOf(tokenOferta.nextToken()));
                    // ofertaRecibida.setIdSector(tokenOferta.nextToken());
                    ofertaRecibida.setIdEmpleador(Integer.valueOf(tokenOferta.nextToken()));
                    ofertaRecibida.setDescripcion(tokenOferta.nextToken());
                    ofertaRecibida.setCargo(tokenOferta.nextToken());
                    ofertaRecibida.setSueldo(Integer.valueOf(tokenOferta.nextToken()));

                    String response = borrarOferta(ofertaRecibida, ofertaStr);
                    server.send(response.getBytes(ZMQ.CHARSET), 0);
                    System.out.println("--------------------------");
                    System.out.println("Oferta Respuesta: " + response + " ; Enviada al Filtro");
                    System.out.println("--------------------------");
                }
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            System.err.println("Server System exception: " + e);
        }
    }

    public static String borrarOferta(Oferta oferta, String ofertaStr) {
        int key = Math.abs(ofertaStr.hashCode());
        key = key % 256;
        String response;
        int lowRange = predecesor > serverId ? 0 : predecesor;
        if (lowRange < key && key <= serverId) {
            ht.remove(Integer.toString(key));
            response = "Llave: " + key + "; Oferta: " + ofertaStr + " Se eliminó del servidor (" + serverId + ")";
        } else {
            response = "Oferta " + ofertaStr + " NO esta en el servidor (" + serverId + ")";
        }
        return response;
    }

    public static String grabarOferta(Oferta oferta, String ofertaStr) {
        int key = Math.abs(ofertaStr.hashCode());
        key = key % 256;
        String response;
        int lowRange = predecesor > serverId ? 0 : predecesor;
        if (lowRange < key && key <= serverId) {
            guardarEnDHT(Integer.toString(key), oferta);
            response = "Llave: " + key + "; Oferta: " + ofertaStr + " OK en el servidor (" + serverId + ")";
        } else {
            response = "Oferta " + ofertaStr + " NO va en el servidor (" + serverId + ")";
        }
        return response;
    }

    public static String verificarVacantes(Aspirante solicitud) {
        ArrayList<Oferta> ofertasDht = new ArrayList<>(ht.values());
        String res = "No se encontraron matches";
        for (int j = 0; j < ofertasDht.size(); j++) {
            if (ofertasDht.get(j).getIdSector() == solicitud.getIdSector()) {
                if (validarCriterios(solicitud, ofertasDht.get(j))) {
                    res = String.format("V-%d-%d-%s-%d-%s",ofertasDht.get(j).getIdSector(),ofertasDht.get(j).getIdEmpleador(),ofertasDht.get(j).getDescripcion(), 
                    ofertasDht.get(j).getId(), solicitud.getNombre());
                    return res;
                }
            }
        }
        System.out.println(res);
        return res;
    }

    public static boolean validarCriterios(Aspirante solicitud, Oferta vacante) {
        if (vacante.getExperiencia() > solicitud.getExperiencia())
            return false;
        if (!vacante.getEstudios().equals(solicitud.getEstudios()))
            return false;
        if (!vacante.getHabilidades().equals(solicitud.getHabilidades()))
            return false;
        return true;
    }

    public static boolean guardarEnDHT(String key, Oferta value) {
        ht.put(key, value);
        System.out.println("Nueva HT:");
        System.out.println(ht);
        // FileOutputStream fos = null;
        // ObjectOutputStream salida = null;
        // try {
        // PrintWriter pw = new PrintWriter(new File("Proyecto/Primera
        // entrega/entrega1/ofertas"));
        // pw.write("");
        // pw.close();
        // fos = new FileOutputStream("Proyecto/Primera entrega/entrega1/ofertas",
        // true);
        // salida = new ObjectOutputStream(fos);
        // salida.writeObject(ht);
        // salida.close();
        // fos.close();
        // } catch (Exception e) {
        // System.out.println("Error guardarEnDHT: " + e.getMessage());
        // return false;
        // }
        return true;
    }

    public static void inicializarDHT() {
        // try {
        // FileInputStream fi = new FileInputStream(new File("Proyecto/Primera
        // entrega/entrega1/ofertas"));
        // ObjectInputStream oi = new ObjectInputStream(fi);
        // ht = (Hashtable<String, Oferta>) oi.readObject();
        // System.out.println(ht);
        // } catch (Exception e) {
        // System.out.println("Error inicializarDHT: " + e.getMessage());
        // }
    }
}
