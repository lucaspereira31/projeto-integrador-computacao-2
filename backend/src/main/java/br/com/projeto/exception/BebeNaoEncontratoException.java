package br.com.projeto.exception;

public class BebeNaoEncontratoException extends RuntimeException {
    public BebeNaoEncontratoException(String message) {
        super(message);
    }
}
