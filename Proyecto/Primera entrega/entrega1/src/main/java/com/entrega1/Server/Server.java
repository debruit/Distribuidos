package com.entrega1.Server;
import java.util.ArrayList;

public interface Server extends java.rmi.Remote {

    public String prueba()throws java.rmi.RemoteException;

    public void almacenarOfertas(ArrayList<Oferta> ofertas);

    public int getNumeroOfertasDHT();

    public void buscarVacante();

    public void notificarEmpleador();

    public void notificarAspirante();

}
