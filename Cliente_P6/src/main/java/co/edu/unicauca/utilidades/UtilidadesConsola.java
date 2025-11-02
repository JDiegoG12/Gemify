package co.edu.unicauca.utilidades;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Clase de utilidades para la lectura de datos desde la consola.
 * Proporciona métodos seguros para leer enteros y cadenas con manejo de errores.
 * 
 */
public class UtilidadesConsola {

    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    /**
     * Lee un número entero desde la consola.
     * Muestra un mensaje predeterminado y repite hasta que se ingrese un valor válido.
     * 
     * @return el número entero ingresado por el usuario
     */
    public static int leerEntero() {
        return leerEntero("Ingrese la opción: ");
    }

    /**
     * Lee un número entero desde la consola mostrando un mensaje personalizado.
     * 
     * @param mensaje el mensaje a mostrar al usuario
     * @return el número entero ingresado
     */
    public static int leerEntero(String mensaje) {
        int valor = 0;
        boolean valido;
        do {
            try {
                System.out.print(mensaje);
                String linea = reader.readLine();
                valor = Integer.parseInt(linea);
                valido = true;
            } catch (Exception e) {
                System.out.println("Entrada inválida. Por favor, ingrese un número entero.");
                valido = false;
            }
        } while (!valido);
        return valor;
    }

    /**
     * Lee una cadena de texto desde la consola.
     * Muestra un mensaje predeterminado.
     * 
     * @return la cadena ingresada por el usuario
     */
    public static String leerCadena() {
        return leerCadena("Ingrese un valor: ");
    }

    /**
     * Lee una cadena de texto desde la consola mostrando un mensaje personalizado.
     * 
     * @param mensaje el mensaje a mostrar al usuario
     * @return la cadena ingresada
     */
    public static String leerCadena(String mensaje) {
        String valor = "";
        boolean valido;
        do {
            try {
                System.out.print(mensaje);
                valor = reader.readLine();
                if (valor != null && !valor.trim().isEmpty()) {
                    valido = true;
                } else {
                    System.out.println("El valor no puede estar vacío. Intente nuevamente.");
                    valido = false;
                }
            } catch (Exception e) {
                System.out.println("Error al leer la entrada. Intente nuevamente.");
                valido = false;
            }
        } while (!valido);
        return valor.trim();
    }
}