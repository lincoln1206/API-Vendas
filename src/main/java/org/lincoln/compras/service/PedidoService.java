package org.lincoln.compras.service;

import org.lincoln.compras.domain.enums.StatusPedido;
import org.lincoln.compras.domain.entity.Pedido;
import org.lincoln.compras.rest.dto.PedidoDTO;

import java.util.Optional;

public interface PedidoService {

    Pedido salvar(PedidoDTO dto);
    Optional<Pedido> obterPedidoCompleto(Integer id);
    void atualizaStatus(Integer id, StatusPedido statusPedido);

}
