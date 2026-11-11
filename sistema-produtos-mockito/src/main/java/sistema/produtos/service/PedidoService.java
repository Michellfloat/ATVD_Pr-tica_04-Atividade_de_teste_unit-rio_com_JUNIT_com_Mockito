package sistema.produtos.service;

import sistema.produtos.Pedido;
import sistema.produtos.Produto;
import sistema.produtos.exception.EstoqueInsuficienteException;
import sistema.produtos.exception.ProdutoNaoEncontradoException;
import sistema.produtos.repository.PedidoRepository;
import sistema.produtos.repository.ProdutoRepository;

public class PedidoService {
private final ProdutoRepository produtoRepository;
private final PedidoRepository pedidoRepository;
public PedidoService(ProdutoRepository produtoRepository,
PedidoRepository pedidoRepository) {
this.produtoRepository = produtoRepository;
this.pedidoRepository = pedidoRepository;
}
public Pedido criarPedido(Long produtoId, int quantidade) {
if (quantidade <= 0) {
throw new IllegalArgumentException(
"Quantidade deve ser maior que zero");
}
Produto produto = produtoRepository.buscarPorId(produtoId);
if (produto == null) {
throw new ProdutoNaoEncontradoException(
"Produto não encontrado: " + produtoId);
}
if (quantidade > produto.getQuantidade()) {
throw new EstoqueInsuficienteException("Estoque insuficiente");
}
double total = produto.getPreco() * quantidade;
Pedido pedido = new Pedido(produto, quantidade, total);
pedidoRepository.salvar(pedido);
return pedido;
}
}
