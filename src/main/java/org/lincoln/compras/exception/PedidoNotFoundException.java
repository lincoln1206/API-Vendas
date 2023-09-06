package org.lincoln.compras.exception;

public class PedidoNotFoundException extends RuntimeException {

    public PedidoNotFoundException(){
        super("Pedido não econtrado.");
    }
}
