package com.entrega1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

/**
 *
 * @author usuario
 */
public class RegistradorAspirante {

    public static void main(String args[]) {

        int filtro, i = 0;
        ArrayList<Aspirante> aspirantes = new ArrayList<Aspirante>();

        try (ZContext context = new ZContext()) {
            ZMQ.Socket empleador = context.createSocket(SocketType.PUB);
            empleador.bind("tcp://*:1099");
            empleador.bind("ipc://registrador");

            try {
                File file = new File("solicitudes.txt");
                // File file = new File("solicitudes.txt");
                Scanner myReader = new Scanner(file);
                Aspirante solicitud = new Aspirante();
                filtro = 0;
                while (myReader.hasNextLine()) {
                    solicitud.setIdAspirante(i);
                    solicitud.setNombre(myReader.nextLine());
                    solicitud.setIdSector(Integer.parseInt(myReader.nextLine()));
                    solicitud.setEstudios(myReader.nextLine());
                    solicitud.setExperiencia(Integer.parseInt(myReader.nextLine()));
                    solicitud.setHabilidades(myReader.nextLine());

                    String solicitudNueva = String.format("%d-Aspirante-%d-%s-%s-%s-%d-%d", filtro,
                            solicitud.getIdAspirante(), solicitud.getNombre(), solicitud.getHabilidades(),
                            solicitud.getEstudios(), solicitud.getExperiencia(), solicitud.getIdSector());

                    empleador.send(solicitudNueva, 0);

                    if (myReader.nextLine().equals("/")) {
                        solicitud = new Aspirante();
                        i++;
                    }

                    aspirantes.add(solicitud);

                    Thread.sleep(2000);
                }
                myReader.close();
                empleador.close();

            } catch (FileNotFoundException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.err.println(" System exception: " + e);
        }

        try (ZContext context = new ZContext()) {
            ZMQ.Socket subscriber = context.createSocket(SocketType.SUB);
            subscriber.connect("tcp://127.0.0.1:2099");
            System.out.println("Esperando respuesta vacante...");

            String respuesta = "0";

            subscriber.subscribe(respuesta.getBytes(ZMQ.CHARSET));

            String mensaje = subscriber.recvStr(0).trim();

            StringTokenizer token = new StringTokenizer(mensaje, "-");

            Integer.valueOf(token.nextToken());

            // for de todos los aspirantes

            for (Aspirante aspirante : aspirantes) {
                if (aspirante.getIdSector() == Integer.valueOf(token.nextToken())) {
                    System.out.println("Se encontró la oferta: " + token.nextToken());
                    // Evalua la vacante y responde
                    ZMQ.Socket respuestaSocket = context.createSocket(SocketType.PUB);
                    respuestaSocket.bind("tcp://*:3099");
                    respuestaSocket.bind("ipc://respuesta");

                    System.out.println("Acepta la vacante? (y/n)");

                    String acepta = System.console().readLine();

                    int id = 0;
                    Thread.sleep(2000);
                    
                    String respuestaAspirante = String.format("%d-%s", id, acepta);
                    respuestaSocket.send(respuestaAspirante, 0);

                    Thread.sleep(2000);
                    break;
                }
            }

        } catch (Exception e) {
            System.err.println(" System exception: " + e);
        }
    }
}
