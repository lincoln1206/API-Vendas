package org.lincoln.compras.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.lincoln.compras.domain.entity.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Integer> {
}
