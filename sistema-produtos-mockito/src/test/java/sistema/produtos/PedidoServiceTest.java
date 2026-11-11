package sistema.produtos;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import sistema.produtos.exception.ErroAoSalvarProdutoException;
import sistema.produtos.exception.EstoqueInsuficienteException;
import sistema.produtos.exception.ProdutoNaoEncontradoException;
import sistema.produtos.repository.PedidoRepository;
import sistema.produtos.repository.ProdutoRepository;
import sistema.produtos.service.PedidoService;

@ExtendWith(MockitoExtension.class)
public class PedidoServiceTest {

    @Mock 
    private ProdutoRepository produtoRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks 
    private PedidoService pedidoService;

    @Test 
    @DisplayName("PED01 - Pedido válido para o sistema.")
    void ped01_pedidoValidoCalculaValorTotalESalva() {
        // Arrange
        Produto produto = new Produto("Mouse", 49.90, 10);
        when(produtoRepository.buscarPorId(1L)).thenReturn(produto);

        // Act
        Pedido pedido = pedidoService.criarPedido(1L, 2);

        // Assert
        Assertions.assertNotNull(pedido);
        Assertions.assertEquals(99.80, pedido.getValorTotal(), 0.0001);
        verify(pedidoRepository).salvar(pedido);
    }

    @Test
    @DisplayName("PED02 - Pedido válido: verificar chamadas dos dois repositórios com times(1)")
    void ped02_pedidoValidoVerificaTimesNosRepositorios() {
        // Arrange
        Produto produto = new Produto("Mouse", 49.90, 10);
        when(produtoRepository.buscarPorId(1L)).thenReturn(produto);

        // Act
        Pedido pedido = pedidoService.criarPedido(1L, 2);

        // Assert & Verify
        verify(produtoRepository, times(1)).buscarPorId(1L);
        verify(pedidoRepository, times(1)).salvar(pedido);
    }

    @Test
    @DisplayName("PED03 - Quantidade igual a zero")
    void ped03_quantidadeIgualAZeroDeveLancarExcecao() {
        // Act & Assert
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> pedidoService.criarPedido(1L, 0)
        );

        // PED08 - Garantir que o pedido não é salvo em cenários de erro
        verify(pedidoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("PED04 - Quantidade negativa (-3)")
    void ped04_quantidadeNegativaDeveLancarExcecao() {
        // Act & Assert
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> pedidoService.criarPedido(1L, -3)
        );

        // PED08 - Garantir que o pedido não é salvo em cenários de erro
        verify(pedidoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("PED05 - Produto inexistente (ID 99)")
    void ped05_produtoInexistenteDeveLancarExcecao() {
        // Arrange
        when(produtoRepository.buscarPorId(99L)).thenReturn(null);

        // Act & Assert
        Assertions.assertThrows(
            ProdutoNaoEncontradoException.class,
            () -> pedidoService.criarPedido(99L, 2)
        );

        // PED08 - Garantir que o pedido não é salvo em cenários de erro
        verify(pedidoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("PED06 - Quantidade maior que o estoque (11 com estoque 10)")
    void ped06_quantidadeMaiorQueEstoqueDeveLancarExcecao() {
        // Arrange
        Produto produto = new Produto("Mouse", 49.90, 10);
        when(produtoRepository.buscarPorId(1L)).thenReturn(produto);

        // Act & Assert
        Assertions.assertThrows(
            EstoqueInsuficienteException.class,
            () -> pedidoService.criarPedido(1L, 11)
        );

        // PED08 - Garantir que o pedido não é salvo em cenários de erro
        verify(pedidoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("PED07 - Quantidade igual ao estoque (10 com estoque 10)")
    void ped07_quantidadeIgualAoEstoqueDeveAceitar() {
        // Arrange
        Produto produto = new Produto("Mouse", 49.90, 10);
        when(produtoRepository.buscarPorId(1L)).thenReturn(produto);

        // Act
        Pedido pedido = pedidoService.criarPedido(1L, 10);

        // Assert
        Assertions.assertNotNull(pedido);
        Assertions.assertEquals(499.00, pedido.getValorTotal(), 0.0001);
        verify(pedidoRepository).salvar(pedido);
    }

    @Test
@DisplayName("Cenário A - Banco fora do ar ao salvar o pedido")
void falhaConexaoBancoAoSalvarPedido() {
        // Arrange
        Produto produto = new Produto("Mouse", 49.90, 10);
        when(produtoRepository.buscarPorId(1L)).thenReturn(produto);
        
        doThrow(new RuntimeException("Falha na conexão"))
        .when(pedidoRepository).salvar(any(Pedido.class));

        // Act e Assert
        Assertions.assertThrows(
        RuntimeException.class, 
        () -> pedidoService.criarPedido(1L, 2)
        );

        // Verify
        verify(pedidoRepository).salvar(any(Pedido.class));
    }

    @Test
@DisplayName("Cenário C - Falha no ProdutoRepository interrompe fluxo")
void falhaProdutoRepositoryNaoDeveChamarSalvar() {
    // Arrange: Simula erro ao buscar o produto
    when(produtoRepository.buscarPorId(1L))
        .thenThrow(new RuntimeException("Erro de conexão no banco ao buscar produto"));

    // Act & Assert
    Assertions.assertThrows(
        RuntimeException.class,
        () -> pedidoService.criarPedido(1L, 2)
    );

    // Verify: Garante que o pedidoRepository NUNCA foi acionado
    verify(pedidoRepository, never()).salvar(any());
    }

    
}
