package com.entrega1;
import java.util.ArrayList;

public interface Registrador extends java.rmi.Remote {

    public String prueba()throws java.rmi.RemoteException;

   public boolean registrar(Oferta consulta) throws java.rmi.RemoteException;

   public ArrayList<Oferta> consultar() throws java.rmi.RemoteException;
}
