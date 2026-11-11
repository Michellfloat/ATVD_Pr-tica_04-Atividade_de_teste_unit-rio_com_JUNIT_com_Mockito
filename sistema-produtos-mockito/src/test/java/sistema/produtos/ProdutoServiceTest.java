package sistema.produtos;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
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
import sistema.produtos.exception.ProdutoNaoEncontradoException;
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

    @Test 
    @DisplayName("CT09/RF03 - Buscar produto por ID inexistente")
    void produtoIdInexistente(){
        
        // Arrange (preparar)
        Mockito.when(repository.buscarPorId(99L)).thenReturn(null);

        // Act (executar)
        // e
        // Assert (verificar)
        Assertions.assertThrows(ProdutoNaoEncontradoException.class,() -> service.buscarPorId(99L));

        //Verify(verificar)
        Mockito.verify(repository).buscarPorId(99L);
    }

    @Test 
    @DisplayName("CT10/RF04 - Falha de conexão ao salvar")
    void falhaConexaoAoSalvar(){
        // Arrange (preparar)
        Produto produto = new Produto("Playstation 5", 9999.99, 25);
        doThrow(new RuntimeException("Falha na conexão")).when(repository).salvar(produto);

        // Act (executar)
        // e 
        //Assert (verificar)
        Assertions.assertThrows(
        ErroAoSalvarProdutoException.class, 
        () -> service.salvar(produto)
        );

        //Verify(veirificar)
        Mockito.verify(repository).salvar(produto);
    }

    @Test 
    @DisplayName("CT11/RF04 - Falha de conexão ao salvar")
    void precoIgualAZero(){
        // Arrange (preparar)
        Produto produto = new Produto("pia", 0.0, 7);


        // Act & Assert (executar e analisar)
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.salvar(produto) );

        // Assert (verificar)
        Mockito.verify(repository, Mockito.never()).salvar(Mockito.any());
    }

    @Test 
    @DisplayName("CT12 - Produto com preço no limite mínimo válido (0.01)")
    void produtoPrecoLimite(){

        // Arrange: 
        Produto produto = new Produto("Lápis", 0.01, 50);
        Mockito.when(repository.salvar(produto)).thenReturn(produto);

        // Act:
        Produto resultado = service.salvar(produto);

        // Assert:
        Assertions.assertEquals(0.01, resultado.getPreco(), 0.0001);

        // Verify:
        verify(repository).salvar(produto);
    }

    @Test 
    @DisplayName("CT13 - Nome do produto em branco(Não confundir com vazio)")
    void nomeProdutoBranco(){

        // Arrange
        Produto produto = new Produto(" ", 20.0, 2);

        // Act
        // e
        // assert
        Assertions.assertThrows(
        IllegalArgumentException.class, 
        () -> service.salvar(produto)
        );

        // verify:
        verify(repository, never()).salvar(any());
    }

    @Test
    @DisplayName("CT14 - Buscar por id e produto não ser encontrado")
    void buscarProdutoNaoEncontrado() {
        // Arrange
        when(repository.buscarPorId(99L)).thenReturn(null);

        // Act e Assert
        Assertions.assertThrows(
            ProdutoNaoEncontradoException.class,
            () -> service.buscarPorId(99L)
        );

        // Verify
        verify(repository).buscarPorId(99L);
    }

    @Test
    @DisplayName("CT15 - Banco de dados fora do ar ao salvar produto")
    void bancoDeDadosForaDoAr() {
        // Arrange
        Produto produto = new Produto("Sofá", 150.0, 2);
        doThrow(new RuntimeException("Conexão com o banco recusada"))
            .when(repository).salvar(any(Produto.class));

        // Act e Assert
        Assertions.assertThrows(
            ErroAoSalvarProdutoException.class,
            () -> service.salvar(produto)
        );

        // Verify
        verify(repository).salvar(produto);
    }

    @Test
    @DisplayName("CT16 - Teste de Comparação: Objeto específico x Matcher genérico any()")
    void demonstrarDiferencaEntreObjetoEspecificoEAny() {
        // Arrange
        Produto produto = new Produto("Caderno", 15.0, 10);
        
        // O any(Produto.class) instrui o Mockito a aceitar QUALQUER instância da classe Produto
        when(repository.salvar(any(Produto.class))).thenReturn(produto);

        // Act
        Produto resultado = service.salvar(produto);

        // Assert
        Assertions.assertNotNull(resultado);

        // Verify:
        // 1. O any() verifica se a chamada ocorreu com qualquer instância de Produto
        verify(repository).salvar(any(Produto.class));
        
        // 2. A referência direta verifica se a chamada ocorreu EXATAMENTE com este objeto na memória
        verify(repository).salvar(produto);
    }
    

}
