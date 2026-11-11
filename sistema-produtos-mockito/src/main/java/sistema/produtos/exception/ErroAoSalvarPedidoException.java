package sistema.produtos.exception;

public class ErroAoSalvarPedidoException extends RuntimeException {
    public ErroAoSalvarPedidoException(String message, Throwable cause) {
        super(message, cause);
    }
}