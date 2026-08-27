package br.com.projeto.exception;

public class VinculoJaExisteException extends RuntimeException {
    public VinculoJaExisteException(String message) {
        super(message);
    }
}
