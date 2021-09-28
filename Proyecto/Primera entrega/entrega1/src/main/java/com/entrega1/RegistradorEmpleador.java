package com.entrega1;

import java.io.Console;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

/**
 *
 * @author usuario
 */
public class RegistradorEmpleador {

    public static void main(String args[]) {

        int id, sector, sueldo, filtro,i=1;
        String descripcion, cargo,resp;
        boolean otra;

        try (ZContext context = new ZContext()) {
            ZMQ.Socket empleador = context.createSocket(SocketType.PUB);
            empleador.bind("tcp://*:1099");
            empleador.bind("ipc://registrador");

            Console entrada = System.console();
            
            do{
                otra = false;
                Oferta consulta = new Oferta();
                filtro = 0;
                consulta.setId(i);
                System.out.println("Indique su ID");
                id = Integer.parseInt(entrada.readLine());
                consulta.setIdEmpleador(id);
                System.out.println(
                        "Indique el sector:\n1. DIRECTORES Y GERENTES\n2. PROFESIONALES CIENTIFICOS E INTELECTUALES\n3. TECNICOS Y PROFESIONALES\n4. PERSONAL DE APOYO ADMNISTRATIVO\n5. AGRICULTORES FORESTALES Y PESQUEROS");
                sector = Integer.parseInt(entrada.readLine());
                consulta.setIdSector(sector);
                System.out.println("Escriba la descripción del cargo");
                descripcion = entrada.readLine();
                consulta.setDescripcion(descripcion);
                System.out.println("Escriba el nombre del cargo");
                cargo = entrada.readLine();
                consulta.setCargo(cargo);
                System.out.println("Indique el sueldo del cargo");
                sueldo = Integer.parseInt(entrada.readLine());
                consulta.setSueldo(sueldo);

                String oferta = String.format("%d-%d-%d-%d-%s-%s-%d", filtro, consulta.getId(), consulta.getIdSector(),
                        consulta.getIdEmpleador(), consulta.getDescripcion(), consulta.getCargo(),
                        consulta.getSueldo());

                empleador.send(oferta, 0);

                System.out.println("Desea ingresar otra oferta? (y/n)");
                resp = entrada.readLine();
                if(resp.equals("y")){
                    otra = true;
                    i++;
                }
            }while(otra == true);

        } catch (Exception e) {
            System.err.println(" System exception: " + e);
        }
    }
}
