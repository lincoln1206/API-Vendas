package org.lincoln.compras.service.impl;

import lombok.RequiredArgsConstructor;
import org.lincoln.compras.domain.entity.ItemPedido;
import org.lincoln.compras.domain.enums.StatusPedido;
import org.lincoln.compras.domain.repository.ClienteRepository;
import org.lincoln.compras.domain.repository.ItemPedidoRepository;
import org.lincoln.compras.domain.repository.PedidoRepository;
import org.lincoln.compras.domain.repository.ProdutoRepository;
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

    private final PedidoRepository pedidoRepositoryRepository;
    private final ClienteRepository clienteRepositoryRepository;
    private final ProdutoRepository produtoRepositoryRepository;
    private final ItemPedidoRepository itemPedidoRepositoryRepository;

    @Override
    @Transactional
    public Pedido salvar(PedidoDTO dto) {
        Integer idCliente = dto.getCliente();
        Cliente cliente = clienteRepositoryRepository
                .findById(idCliente)
                .orElseThrow( () -> new RegraNegocioException("Código de cliente inválido: " + idCliente ));

        Pedido pedido = new Pedido();
        pedido.setTotal(dto.getTotal());
        pedido.setDataPedido(LocalDate.now());
        pedido.setCliente(cliente);
        pedido.setStatus(StatusPedido.REALIZADO);

        List<ItemPedido> itensPedidos = converterItens(pedido, dto.getItens());
        pedidoRepositoryRepository.save(pedido);
        itemPedidoRepositoryRepository.saveAll(itensPedidos);
        pedido.setItens(itensPedidos);

        return pedido;
    }

    @Override
    public Optional<Pedido> obterPedidoCompleto(Integer id) {
        return pedidoRepositoryRepository.findByIdFetchItens(id);
    }

    @Override
    @Transactional
    public void atualizaStatus(Integer id, StatusPedido statusPedido) {
        pedidoRepositoryRepository
                .findById(id)
                .map( pedido -> {
                    pedido.setStatus(statusPedido);
                    return pedidoRepositoryRepository.save(pedido);
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
                    Produto produto = produtoRepositoryRepository
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
