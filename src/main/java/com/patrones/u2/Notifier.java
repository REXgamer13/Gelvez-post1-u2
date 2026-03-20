package com.patrones.u2;

/**
 * Product (interfaz): contrato de todo notificador.
 * El codigo cliente solo conoce esta interfaz, nunca las
 * implementaciones concretas.
 */
public interface Notifier {
    /**
     * Envía una notificación al destinatario.
     * @param recipient identificador del destinatario (email, numero,
    token)
     * @param message   contenido del mensaje
     */
    void send(String recipient, String message);

    /**
     * Retorna el identificador del canal de este notificador.
     */
    String channel();

}