package co.edu.unicauca.utilidades;

import co.edu.unicauca.distribuidos.streaming.AudioServiceGrpc;
import co.edu.unicauca.distribuidos.streaming.StreamingProto.FragmentoCancion;
import co.edu.unicauca.distribuidos.streaming.StreamingProto.PeticionDTO;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;

import javax.sound.sampled.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Cliente gRPC para la reproducción de canciones desde el Servidor de Streaming.
 * <p>
 * Esta clase establece una conexión gRPC con el servidor de streaming y recibe fragmentos
 * de audio MP3 mediante un flujo unidireccional del servidor al cliente. Los fragmentos
 * se almacenan temporalmente en disco y se reproducen utilizando la librería JLayer
 * en combinación con la Java Sound API.
 * <p>
 * Implementa un mecanismo de control de reproducción basado en una bandera volátil,
 * permitiendo detener la reproducción de forma segura desde otro hilo (por ejemplo, al salir del menú).
 * 
 */
public class StreamingAudioClient {

    /**
     * Canal gRPC para la comunicación con el servidor de streaming.
     */
    private final ManagedChannel channel;

    /**
     * Stub bloqueante para invocar métodos del servicio gRPC {@code AudioService}.
     */
    private final AudioServiceGrpc.AudioServiceBlockingStub blockingStub;

    /**
     * Pool de hilos utilizado para gestionar las tareas concurrentes de red y audio.
     * Se inicializa bajo demanda al iniciar la reproducción.
     */
    private ExecutorService executor;

    /**
     * Bandera volátil que indica si la reproducción está activa.
     * <p>
     * Se utiliza para coordinar la terminación segura de los hilos de red y audio.
     * El modificador {@code volatile} garantiza visibilidad entre hilos.
     */
    private volatile boolean isPlaying;

    /**
     * Línea de audio activa utilizada por la Java Sound API para la reproducción.
     * <p>
     * Se mantiene como referencia de clase para permitir su cierre desde el método
     * {@link #detenerReproduccion()}.
     */
    private SourceDataLine audioLine;

    /**
     * Referencia al hilo encargado de la recepción y reproducción del audio.
     * <p>
     * Permite interrumpir el hilo de forma controlada durante la detención.
     */
    private Thread audioThread;

    /**
     * Constructor que inicializa la conexión gRPC con el servidor de streaming.
     * 
     * @param host dirección IP o nombre de host del servidor de streaming
     * @param port puerto en el que escucha el servidor de streaming
     */
    public StreamingAudioClient(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        this.blockingStub = AudioServiceGrpc.newBlockingStub(channel);
    }

    /**
     * Inicia la reproducción de una canción de forma no bloqueante.
     * <p>
     * Este método configura los hilos necesarios para:
     * <ul>
     *   <li>Recibir fragmentos de audio del servidor gRPC</li>
     *   <li>Almacenarlos temporalmente y reproducirlos</li>
     * </ul>
     * Retorna inmediatamente después de iniciar los hilos, permitiendo al cliente
     * continuar con otras operaciones (como mostrar un menú).
     * 
     * @param nombreCancion nombre del archivo de audio MP3 a reproducir (ej: "Afuera.mp3")
     * 
     * @implNote Si ya hay una reproducción en curso, se detiene automáticamente antes
     *           de iniciar la nueva.
     */
    public void reproducirCancion(String nombreCancion) {
        detenerReproduccion();
        this.isPlaying = true;
        this.executor = Executors.newFixedThreadPool(2);

        System.out.println(">> Solicitando stream para la cancion: " + nombreCancion);

        try {
            PipedOutputStream pipeOut = new PipedOutputStream();
            PipedInputStream pipeIn = new PipedInputStream(pipeOut, 128 * 1024);

            executor.submit(() -> recibirFragmentos(nombreCancion, pipeOut));
            this.audioThread = new Thread(() -> recibirYReproducirComoArchivo(pipeIn));
            this.audioThread.start();

        } catch (IOException e) {
            System.err.println("ERROR: Fallo el pipeline de streaming: " + e.getMessage());
        }
    }

    /**
     * Hilo encargado de recibir fragmentos de audio del servidor gRPC.
     * <p>
     * Lee los fragmentos del stream gRPC y los escribe en un {@link PipedOutputStream}
     * para que sean consumidos por el hilo de audio.
     * 
     * @param nombreCancion nombre de la canción solicitada al servidor
     * @param pipeOut flujo de salida conectado al pipe de audio
     */
    private void recibirFragmentos(String nombreCancion, PipedOutputStream pipeOut) {
        System.out.println("[Hilo de Red] Iniciado.");
        try {
            PeticionDTO request = PeticionDTO.newBuilder().setNombreCancion(nombreCancion).build();
            Iterator<FragmentoCancion> fragmentos = blockingStub.streamAudio(request);

            while (fragmentos.hasNext() && isPlaying) {
                FragmentoCancion fragmento = fragmentos.next();
                pipeOut.write(fragmento.getData().toByteArray());
            }
        } catch (StatusRuntimeException e) {
            if (isPlaying)
                System.err.println("[Hilo de Red] ERROR de gRPC: " + e.getStatus().getDescription());
        } catch (Exception e) {
            if (isPlaying)
                System.err.println("[Hilo de Red] ERROR: " + e.getMessage());
        } finally {
            System.out.println("[Hilo de Red] Finalizado.");
            try {
                pipeOut.close();
            } catch (Exception e) {
                /* Ignorado */ }
        }
    }

    /**
     * Hilo encargado de recibir los fragmentos de audio y reproducirlos.
     * <p>
     * Almacena los fragmentos recibidos en un archivo temporal MP3 y luego lo reproduce
     * utilizando JLayer y la Java Sound API.
     * 
     * @param pipeIn flujo de entrada conectado al pipe de red
     */
    private void recibirYReproducirComoArchivo(PipedInputStream pipeIn) {
        System.out.println("[Hilo de Audio] Recibiendo fragmentos...");
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("spotifake_", ".mp3");
            try (OutputStream fos = Files.newOutputStream(tempFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while (isPlaying && (bytesRead = pipeIn.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }
            if (isPlaying) {
                System.out.println("[Hilo de Audio] Archivo temporal guardado: " + tempFile);
                reproducirMp3ConJLayer(tempFile.toFile());
            }
        } catch (Exception e) {
            if (isPlaying)
                System.err.println("[Hilo de Audio] Error: " + e.getMessage());
        } finally {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException e) {
                    /* Ignorado */ }
            }
            try {
                pipeIn.close();
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Reproduce un archivo MP3 utilizando la librería JLayer y la Java Sound API.
     * <p>
     * Decodifica el archivo MP3 frame por frame y envía los datos PCM resultantes
     * a una línea de audio para su reproducción en tiempo real.
     * 
     * @param mp3File archivo MP3 a reproducir
     * @throws Exception si ocurre un error durante la decodificación o reproducción
     */
    private void reproducirMp3ConJLayer(File mp3File) throws Exception {
        try (FileInputStream fis = new FileInputStream(mp3File)) {
            Bitstream bitstream = new Bitstream(fis);
            Decoder decoder = new Decoder();

            this.audioLine = null;
            try {
                while (isPlaying) {
                    Header frameHeader = bitstream.readFrame();
                    if (frameHeader == null)
                        break;

                    if (this.audioLine == null) {
                        int channels = (frameHeader.mode() == Header.SINGLE_CHANNEL) ? 1 : 2;
                        AudioFormat format = new AudioFormat((float) frameHeader.frequency(), 16, channels, true,
                                false);
                        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
                        this.audioLine = (SourceDataLine) AudioSystem.getLine(info);
                        this.audioLine.open(format);
                        this.audioLine.start();
                    }

                    SampleBuffer output = (SampleBuffer) decoder.decodeFrame(frameHeader, bitstream);
                    short[] pcm = output.getBuffer();
                    byte[] pcmBytes = toByteArray(pcm, output.getBufferLength());
                    this.audioLine.write(pcmBytes, 0, pcmBytes.length);

                    bitstream.closeFrame();
                }
            } finally {
                if (this.audioLine != null) {
                    this.audioLine.drain();
                    this.audioLine.close();
                }
                bitstream.close();
            }
        }
    }

    /**
     * Convierte un arreglo de muestras de audio en formato {@code short[]} a {@code byte[]}.
     * <p>
     * Este método es necesario porque la Java Sound API espera datos en formato de bytes,
     * mientras que JLayer entrega las muestras en formato de enteros cortos.
     * 
     * @param shorts arreglo de muestras de audio en formato short
     * @param len número de muestras válidas en el arreglo
     * @return arreglo de bytes listo para ser reproducido
     */
    private byte[] toByteArray(short[] shorts, int len) {
        byte[] bytes = new byte[len * 2];
        for (int i = 0; i < len; i++) {
            bytes[i * 2] = (byte) (shorts[i] & 0xFF);
            bytes[i * 2 + 1] = (byte) ((shorts[i] >> 8) & 0xFF);
        }
        return bytes;
    }

    /**
     * Detiene de forma segura la reproducción de audio en curso.
     * <p>
     * Este método:
     * <ul>
     *   <li>Cambia la bandera {@link #isPlaying} a {@code false}</li>
     *   <li>Detiene y cierra la línea de audio</li>
     *   <li>Interrumpe el hilo de audio</li>
     *   <li>Apaga el pool de hilos</li>
     * </ul>
     * Es seguro llamarlo desde cualquier hilo, incluido el hilo principal del menú.
     */
    public void detenerReproduccion() {
        if (this.isPlaying) {
            System.out.println("<< Deteniendo la reproduccion...");
            this.isPlaying = false;

            if (this.audioLine != null) {
                this.audioLine.stop();
                this.audioLine.close();
            }
            if (this.audioThread != null) {
                this.audioThread.interrupt();
            }
            if (this.executor != null && !this.executor.isShutdown()) {
                this.executor.shutdownNow();
            }
        }
    }

    /**
     * Cierra todos los recursos utilizados por el cliente gRPC.
     * <p>
     * Invoca {@link #detenerReproduccion()} para garantizar que no queden hilos activos
     * antes de cerrar el canal gRPC.
     * 
     * @throws InterruptedException si el hilo actual es interrumpido mientras espera
     *                              el cierre del canal
     */
    public void shutdown() throws InterruptedException {
        detenerReproduccion();
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}