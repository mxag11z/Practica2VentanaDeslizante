import java.io.*;
import java.net.*;
import javax.swing.*;

public class cliente {
    private static final int puerto = 12345;
    private static final int datagramSize = 1024;
    private static final int windowSize = 5;

    public static void main(String[] args) {
        try {
            DatagramSocket socket = new DatagramSocket();
            InetAddress direccionServidor = InetAddress.getByName("localhost");
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Seleccionar archivo");
            int seleccion = fileChooser.showOpenDialog(null);

            if (seleccion == JFileChooser.APPROVE_OPTION) {
                File archivo = fileChooser.getSelectedFile();
                FileInputStream fis = new FileInputStream(archivo);
                byte[] buffer = new byte[datagramSize];
                int bytesRead;
                int nextSequenceNumber = 0;

                while ((bytesRead = fis.read(buffer)) != -1) {
                    // Agregar el número de secuencia al principio de los datos
                    String datosConNumeroSecuencia = nextSequenceNumber + ":" + new String(buffer, 0, bytesRead);
                    byte[] datosEnviar = datosConNumeroSecuencia.getBytes();

                    // Crear y enviar el paquete
                    DatagramPacket paquete = new DatagramPacket(datosEnviar, datosEnviar.length, direccionServidor,
                            puerto);
                    socket.send(paquete);
                    // Esperar ACK del servidor
                    byte[] bufferACK = new byte[datagramSize];
                    DatagramPacket paqueteACK = new DatagramPacket(bufferACK, bufferACK.length);
                    socket.receive(paqueteACK);

                    // Imprimir que se recibió el ACK
                    String ackRecibido = new String(paqueteACK.getData(), 0, paqueteACK.getLength());
                    System.out.println("ACK recibido del servidor: " + ackRecibido);
                    // Incrementar el número de secuencia para el siguiente paquete
                    nextSequenceNumber++;
                }

                fis.close();
                socket.close();
                System.out.println("Archivo enviado con éxito.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
