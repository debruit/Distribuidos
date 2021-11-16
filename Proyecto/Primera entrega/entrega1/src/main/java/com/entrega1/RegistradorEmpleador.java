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
public class RegistradorEmpleador {

    public static void main(String args[]) {

        int filtro, i = 0;
        ArrayList<Oferta> ofertas = new ArrayList<Oferta>();

        try (ZContext context = new ZContext()) {
            ZMQ.Socket empleador = context.createSocket(SocketType.PUB);
            empleador.bind("tcp://*:1099");
            empleador.bind("ipc://registrador");

            try {
                // File file = new File("empleador.txt");
                File file = new File("empleador.txt");
                Scanner myReader = new Scanner(file);
                Oferta consulta = new Oferta();
                filtro = 0;
                while (myReader.hasNextLine()) {
                    consulta.setId(i);
                    consulta.setIdEmpleador(Integer.parseInt(myReader.nextLine()));
                    consulta.setDescripcion(myReader.nextLine());
                    consulta.setCargo(myReader.nextLine());
                    consulta.setSueldo(Integer.parseInt(myReader.nextLine()));
                    consulta.setExperiencia(Integer.parseInt(myReader.nextLine()));
                    consulta.setHabilidades(myReader.nextLine());
                    consulta.setEstudios(myReader.nextLine());

                    String oferta = String.format("%d-Oferta-%d-%d-%s-%s-%d-%d-%s-%s", filtro, consulta.getId(),
                            consulta.getIdEmpleador(), consulta.getDescripcion(), consulta.getCargo(),
                            consulta.getSueldo(), consulta.getExperiencia(), consulta.getHabilidades(),
                            consulta.getEstudios());

                    empleador.send(oferta, 0);

                    if (myReader.nextLine().equals("/")) {
                        consulta = new Oferta();
                        i++;
                    }

                    ofertas.add(consulta);

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
            subscriber.connect("tcp://127.0.0.1:4099");
            System.out.println("Esperando respuesta aspirante...");

            // Id del sector
            String respuesta = "0";

            subscriber.subscribe(respuesta.getBytes(ZMQ.CHARSET));

            while (true) {
                String mensaje = subscriber.recvStr(0).trim();
                StringTokenizer token = new StringTokenizer(mensaje, "-");

                Integer.valueOf(token.nextToken());
                int idEmpleador = Integer.valueOf(token.nextToken());

                for (Oferta oferta : ofertas) {

                    if (oferta.getIdEmpleador() == idEmpleador) {
                        System.out.println(String.format("%s%s%s%d", token.nextToken(), token.nextToken(),
                                token.nextToken(), Integer.valueOf(token.nextToken())));
                                break;
                    }
                }
            }

        } catch (Exception e) {
            System.err.println(" System exception: " + e);
        }
    }
}
