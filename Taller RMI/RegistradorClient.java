import java.rmi.*;
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

            if(args[0].equals("registrar") && args.length == 4){
                Oferta consulta = new Oferta();
                consulta.setEmpresa(args[1]);
                consulta.setCargo(args[2]);
                consulta.setSueldo(args[3]);
                if(miregistrador.registrar(consulta))
                    System.out.println("Satisfactoria");
                else{
                    System.out.println("No satisfactoria");
                }
            }
            else if(args[0].equals("consultar")){
                miregistrador.consultar().forEach(oferta -> {
                    System.out.println("Empresa: "+oferta.getEmpresa());
                    System.out.println("Cargo: "+oferta.getCargo());
                    System.out.println("Sueldo: "+oferta.getSueldo());
                });
            }
            else if(args[0].equals("prueba")){
                miregistrador.prueba();
            }
        } catch (Exception e) {
            System.err.println(" System exception: "+ e);
        }
        System.exit(0);
    }
}
