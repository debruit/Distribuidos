package com.entrega1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

/**
 *
 * @author usuario
 */
public class RegistradorAspirante {

    public static void main(String args[]) {

        int filtro,i=1;

        try (ZContext context = new ZContext()) {
            ZMQ.Socket empleador = context.createSocket(SocketType.PUB);
            empleador.bind("tcp://*:1099");
            empleador.bind("ipc://registrador");

            try {
                File file = new File("solicitudes.txt");
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

                    String solicitudNueva = String.format("%d-Aspirante-%d-%s-%s-%s-%d-%d", filtro, solicitud.getIdAspirante(),
                        solicitud.getNombre(), solicitud.getHabilidades(), solicitud.getEstudios(),
                        solicitud.getExperiencia(), solicitud.getIdSector());

                    empleador.send(solicitudNueva, 0);

                    if(myReader.nextLine().equals("/")){
                        solicitud = new Aspirante();
                        i++;
                    }

                    Thread.sleep(5000);
                }
                myReader.close();
            } catch (FileNotFoundException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.err.println(" System exception: " + e);
        }
    }
}
