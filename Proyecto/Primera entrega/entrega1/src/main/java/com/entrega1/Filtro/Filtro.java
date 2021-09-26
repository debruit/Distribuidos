package com.entrega1.Filtro;
import java.util.ArrayList;

public interface Filtro extends java.rmi.Remote {

    public String prueba()throws java.rmi.RemoteException;

    public void recibirSolicitud(Solicitud solicitud);

    public boolean consultarOferta(Solicitud solicitud) throws java.rmi.RemoteException;

    public void registrarOferta(Oferta consulta) throws java.rmi.RemoteException;

    public boolean grabarBackup(Oferta oferta);

    public boolean grabarBackup(Solicitud solicitud);

    public void leerBackup();
}
