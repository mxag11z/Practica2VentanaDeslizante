import java.io.*;
import java.net.*;

public class servidor {
    private static final int puerto = 12345;
    private static final int bufferSize = 1024;

    public static void main(String[] args) {
        try {
            DatagramSocket socket = new DatagramSocket(puerto);
            byte[] buffer = new byte[bufferSize];
            int expectedSequenceNumber = 0; // Número de secuencia esperado inicial

            while (true) {
                DatagramPacket paqueteRecibido = new DatagramPacket(buffer, buffer.length);
                socket.receive(paqueteRecibido);

                // Procesar el paquete recibido
                String mensaje = new String(paqueteRecibido.getData(), 0, paqueteRecibido.getLength());
                String[] partesMensaje = mensaje.split(":", 2); // Dividir el mensaje en número de secuencia y datos
                int sequenceNumber = Integer.parseInt(partesMensaje[0]);
                String datos = partesMensaje[1];

                if (sequenceNumber == expectedSequenceNumber) {
                    // Procesar los datos recibidos
                    System.out.println("Paquete recibido con número de secuencia: " + sequenceNumber);
                   // System.out.println("Datos recibidos: " + datos);

                    // Enviar ACK al cliente
                    InetAddress direccionCliente = paqueteRecibido.getAddress();
                    int puertoCliente = paqueteRecibido.getPort();
                    String ack = String.valueOf(sequenceNumber); // ACK = número de secuencia
                    byte[] bufferACK = ack.getBytes();
                    DatagramPacket paqueteACK = new DatagramPacket(bufferACK, bufferACK.length, direccionCliente,
                            puertoCliente);
                    socket.send(paqueteACK);

                    // Actualizar el número de secuencia esperado
                    expectedSequenceNumber++;
                } else {
                    // Descartar el paquete duplicado o fuera de secuencia
                    System.out.println("Paquete descartado: número de secuencia incorrecto");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
