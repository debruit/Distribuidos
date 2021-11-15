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
public class RegistradorEmpleador {

    public static void main(String args[]) {

        int filtro,i=1;

        try (ZContext context = new ZContext()) {
            ZMQ.Socket empleador = context.createSocket(SocketType.PUB);
            empleador.bind("tcp://*:1099");
            empleador.bind("ipc://registrador");

            try {
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

                    String oferta = String.format("%d-Oferta-%d-%d-%s-%s-%d", filtro, consulta.getId(),
                        consulta.getIdEmpleador(), consulta.getDescripcion(), consulta.getCargo(),
                        consulta.getSueldo());

                    empleador.send(oferta, 0);

                    if(myReader.nextLine().equals("/")){
                        consulta = new Oferta();
                        i++;
                    }

                    Thread.sleep(2000);
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
