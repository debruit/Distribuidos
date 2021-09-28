package com.entrega1;

import java.util.Scanner;

// import java.rmi.*;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

/**
 *
 * @author usuario
 */
public class RegistradorEmpleador {

    public static void main(String args[]) {

        // Random rand = new Random();
        int id, sector, sueldo, filtro,idoferta;
        String descripcion, cargo;

        try (ZContext context = new ZContext()) {
            ZMQ.Socket empleador = context.createSocket(SocketType.PUB);
            empleador.bind("tcp://*:1099");
            empleador.bind("ipc://registrador");

            Scanner entrada = new Scanner(System.in);
            System.out.println("Cuantas ofertas desea ingresar?");
            int cantOfertas = entrada.nextInt();
            for (int i = 0; i < cantOfertas; i++) {
                Oferta consulta = new Oferta();
                filtro = 0;
                System.out.println("ID oferta");
                idoferta = entrada.nextInt();
                consulta.setId(idoferta);
                System.out.println("Indique su ID");
                id = entrada.nextInt();
                consulta.setIdEmpleador(id);
                System.out.println(
                        "Indique el sector:\n1. DIRECTORES Y GERENTES\n2. PROFESIONALES CIENTIFICOS E INTELECTUALES\n3. TECNICOS Y PROFESIONALES\n4. PERSONAL DE APOYO ADMNISTRATIVO\n5. AGRICULTORES FORESTALES Y PESQUEROS");
                sector = entrada.nextInt();
                consulta.setIdSector(sector);
                System.out.println("Escriba la descripción del cargo");
                descripcion = entrada.next();
                consulta.setDescripcion(descripcion);
                System.out.println("Escriba el nombre del cargo");
                cargo = entrada.next();
                consulta.setCargo(cargo);
                System.out.println("Indique el sueldo del cargo");
                sueldo = entrada.nextInt();
                consulta.setSueldo(sueldo);

                String oferta = String.format("%d %d %d %d %s %s %d", filtro, consulta.getId(), consulta.getIdSector(),
                        consulta.getIdEmpleador(), consulta.getDescripcion(), consulta.getCargo(),
                        consulta.getSueldo());

                empleador.send(oferta, 0);
            }
            entrada.close();

        } catch (Exception e) {
            System.err.println(" System exception: " + e);
        }
    }
}
