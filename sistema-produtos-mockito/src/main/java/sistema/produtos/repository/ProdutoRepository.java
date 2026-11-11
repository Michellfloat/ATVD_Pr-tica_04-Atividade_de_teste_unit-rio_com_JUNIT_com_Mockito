package sistema.produtos.repository;

import sistema.produtos.Produto;

public interface ProdutoRepository {
    Produto salvar(Produto produto);
    Produto buscarPorId(Long id);
}