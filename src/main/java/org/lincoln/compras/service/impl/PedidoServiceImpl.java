package org.lincoln.compras.service.impl;

import lombok.RequiredArgsConstructor;
import org.lincoln.compras.domain.entity.ItemPedido;
import org.lincoln.compras.domain.enums.StatusPedido;
import org.lincoln.compras.domain.repository.Clientes;
import org.lincoln.compras.domain.repository.ItensPedidos;
import org.lincoln.compras.domain.repository.Pedidos;
import org.lincoln.compras.domain.repository.Produtos;
import org.lincoln.compras.exception.PedidoNotFoundException;
import org.lincoln.compras.service.PedidoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.lincoln.compras.domain.entity.Cliente;
import org.lincoln.compras.domain.entity.Pedido;
import org.lincoln.compras.domain.entity.Produto;
import org.lincoln.compras.exception.RegraNegocioException;
import org.lincoln.compras.rest.dto.ItemPedidoDTO;
import org.lincoln.compras.rest.dto.PedidoDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final Pedidos pedidosRepository;
    private final Clientes clientesRepository;
    private final Produtos produtosRepository;
    private final ItensPedidos itensPedidosRepository;

    @Override
    @Transactional
    public Pedido salvar(PedidoDTO dto) {
        Integer idCliente = dto.getCliente();
        Cliente cliente = clientesRepository
                .findById(idCliente)
                .orElseThrow( () -> new RegraNegocioException("Código de cliente inválido: " + idCliente ));

        Pedido pedido = new Pedido();
        pedido.setTotal(dto.getTotal());
        pedido.setDataPedido(LocalDate.now());
        pedido.setCliente(cliente);
        pedido.setStatus(StatusPedido.REALIZADO);

        List<ItemPedido> itensPedidos = converterItens(pedido, dto.getItens());
        pedidosRepository.save(pedido);
        itensPedidosRepository.saveAll(itensPedidos);
        pedido.setItens(itensPedidos);

        return pedido;
    }

    @Override
    public Optional<Pedido> obterPedidoCompleto(Integer id) {
        return pedidosRepository.findByIdFetchItens(id);
    }

    @Override
    @Transactional
    public void atualizaStatus(Integer id, StatusPedido statusPedido) {
        pedidosRepository
                .findById(id)
                .map( pedido -> {
                    pedido.setStatus(statusPedido);
                    return pedidosRepository.save(pedido);
                }).orElseThrow( () -> new PedidoNotFoundException());
    }

    private List<ItemPedido> converterItens(Pedido pedido, List<ItemPedidoDTO> itens){
        if(itens.isEmpty()){
            throw new RegraNegocioException("Não é possível realizar um pedido sem itens.");
        }

        return itens
                .stream()
                .map(dto -> {
                    Integer idProduto = dto.getProduto();
                    Produto produto = produtosRepository
                            .findById(idProduto)
                            .orElseThrow( () -> new RegraNegocioException("Código de produto inválido: " + idProduto));;

                    ItemPedido itemPedido = new ItemPedido();
                    itemPedido.setQuantidade(dto.getQuantidade());
                    itemPedido.setPedido(pedido);
                    itemPedido.setProduto(produto);
                    return itemPedido;
                }).collect(Collectors.toList());

    }
}
