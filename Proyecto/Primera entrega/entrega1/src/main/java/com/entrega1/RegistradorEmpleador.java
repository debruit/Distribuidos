package com.entrega1;

// import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;
/**
 *
 * @author usuario
 */
public class RegistradorEmpleador {

    public static void main(String args[]) {

        Random rand = new Random();

        try {
            String serverIP1 = "25.12.51.131";
            String serverIP2 = "25.83.21.137";
            String serverIP3 = "25.78.26.72";
            int serverPort = 1099;
            Registry registry = LocateRegistry.getRegistry(serverIP1, serverPort);
            Registrador miregistrador = (Registrador) registry.lookup("MiRegistrador");
            System.out.println("Buscando Objeto ");
            if(args[0].equals("registrar") && args.length == 7){
                int cont = rand.nextInt(3);
                if(cont==0){
                    registry = LocateRegistry.getRegistry(serverIP1, serverPort);
                    miregistrador = (Registrador) registry.lookup("MiRegistrador");
                    cont++;
                }
                else if(cont==1){
                    registry = LocateRegistry.getRegistry(serverIP2, serverPort);
                    miregistrador = (Registrador) registry.lookup("MiRegistrador");
                    cont++;
                }
                else if(cont==2){
                    registry = LocateRegistry.getRegistry(serverIP3, serverPort);
                    miregistrador = (Registrador) registry.lookup("MiRegistrador");
                    cont = 0;
                }
                Oferta consulta = new Oferta();
                consulta.setId(Integer.parseInt(args[1]));
                consulta.setIdSector(Integer.parseInt(args[2]));
                consulta.setIdEmpleador(Integer.parseInt(args[3]));
                consulta.setDescripcion(args[4]);
                consulta.setCargo(args[5]);
                consulta.setSueldo(Integer.parseInt(args[6]));
                if(miregistrador.registrar(consulta))
                    System.out.println("Satisfactoria");
                else{
                    System.out.println("No satisfactoria");
                }
            }
        } catch (Exception e) {
            System.err.println(" System exception: "+ e);
        }
        System.exit(0);
    }
}
