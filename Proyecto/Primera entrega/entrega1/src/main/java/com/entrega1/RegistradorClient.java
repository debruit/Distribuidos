package com.entrega1;

// import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
/**
 *
 * @author usuario
 */
public class RegistradorClient {

    public static void main(String args[]) {

        try {
            String serverIP = "25.12.51.131"; // or localhost if client and server on same machine.
            int serverPort = 1099;
            Registry registry = LocateRegistry.getRegistry(serverIP, serverPort);

            Registrador miregistrador = (Registrador) registry.lookup("MiRegistrador");
            System.out.println("Buscando Objeto ");
            // Registrador miregistrador = (Registrador) Naming.lookup("rmi://" + "25.12.51.131:1099" + "/" + "MiRegistrador");

            // System.out.println("Pudo");
            miregistrador.prueba();
        } catch (Exception e) {
            System.err.println(" System exception: "+ e);
        }
        System.exit(0);
    }
}
