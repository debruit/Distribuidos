package com.entrega1;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 

/**
 *
 * @author usuario
 */
public class Server1 {

    public static void main(String args[]) throws Exception {

            try(ZContext context = new ZContext()){

                System.out.println("Corriendo servidor...");
                ZMQ.Socket server = context.createSocket(SocketType.REP);
                server.bind("tcp://*:1098");

                while(!Thread.currentThread().isInterrupted()){
                    byte[] reply = server.recv(0);

                    String response = "Llego al servidor";

                    server.send(response.getBytes(ZMQ.CHARSET),0);

                    Thread.sleep(1000);
                }
                
            } catch (Exception e) {
                System.err.println(" System exception: "+ e);
            }
    }
}
