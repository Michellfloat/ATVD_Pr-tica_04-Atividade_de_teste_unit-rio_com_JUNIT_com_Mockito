package sistema.produtos;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import sistema.produtos.repository.ProdutoRepository;
import sistema.produtos.service.ProdutoService;

@ExtendWith(MockitoExtension.class)
public class ProdutoServiceTest {

    @Mock
    private ProdutoRepository repository;

    @InjectMocks
    private ProdutoService service;

    @Test
    @DisplayName("CT01/RF01 - Salvar produto com nome, preço e qauntidade em estoque salvando no Repository.")
    void deveSalvarProdutoComPrecoValido() {
        // Arrange (preparar)
        Produto produto = new Produto("Mouse", 49.90, 10);
        Mockito.when(repository.salvar(produto)).thenReturn(produto);

        // Act (executar)
        Produto resultado = service.salvar(produto);

        // Assert (verificar)
        Assertions.assertEquals("Mouse", resultado.getNome());
        Mockito.verify(repository).salvar(produto);
    }

    @Test
    @DisplayName("CT02/RF02 - Produto com preço negativo")
    void produtoPrecoNegativo(){
        // Arrange (preparar)
        Produto produto = new Produto("Mouse", -49.90, 10);


        // Act & Assert (executar e analisar)
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.salvar(produto) );

        // Assert (verificar)
        Mockito.verify(repository, Mockito.never()).salvar(Mockito.any());


    }

    @Test
    @DisplayName("CT03/RF02 - Produto com preço igual a '0'")
    void produtoPreco0(){
        // Arrange (preparar)
        Produto produto = new Produto("Mouse", 0.0, 10);


        // Act & Assert (executar e analisar)
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.salvar(produto) );

        // Assert (verificar)
        Mockito.verify(repository, Mockito.never()).salvar(Mockito.any());
    }

    @Test
    @DisplayName("CT04/RF02 - nome do produto 'vazio'")
    void produtoNomeVazio(){
        // Arrange (preparar)
        Produto produto = new Produto("", 45.50, 10);


        // Act & Assert (executar e analisar)
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.salvar(produto) );

        // Assert (verificar)
        Mockito.verify(repository, Mockito.never()).salvar(Mockito.any());
    }

    @Test
    @DisplayName("CT05/RF02 - Nome do produto 'nulo'")
    void nomeProdutoNulo(){
        // Arrange (preparar)
        Produto produto = new Produto(null, 45.50, 10);


        // Act & Assert (executar e analisar)
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.salvar(produto) );

        // Assert (verificar)
        Mockito.verify(repository, Mockito.never()).salvar(Mockito.any());
    }

    @Test
    @DisplayName("CT06/RF02 - Quantidade do produto negativa")
    void quantidadeProdutoNegativo(){
        // Arrange (preparar)
        Produto produto = new Produto("Teclado", 45.50, -18);


        // Act & Assert (executar e analisar)
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.salvar(produto) );

        // Assert (verificar)
        Mockito.verify(repository, Mockito.never()).salvar(Mockito.any());
    }

    @Test
    @DisplayName("CT07/RF02 - Quantidade igual a '0'")
    void quantidadeProdutoZero(){
        // Arrange (preparar)
        Produto produto = new Produto("Monitor", 25.45, 0);
        Mockito.when(repository.salvar(produto)).thenReturn(produto);

        // Act (executar)
        Produto resultado = service.salvar(produto);

        // Assert (verificar)
        Assertions.assertEquals("Monitor", resultado.getNome());
        Mockito.verify(repository).salvar(produto);
    }

    @Test
    @DisplayName("CT08/RF03 - Buscar por um produto existente")
    void buscarProdutoPorId(){
        // Arrange (preparar)
        Produto produto = new Produto("Espada", 95.99, 0);
        Mockito.when(repository.buscarPorId(1L)).thenReturn(produto);

        // Act (executar)
        Produto resultado = service.buscarPorId(1L);

        // Assert (verificar)
        Assertions.assertSame(produto, resultado);
        Mockito.verify(repository).buscarPorId(1L);
    }
}
