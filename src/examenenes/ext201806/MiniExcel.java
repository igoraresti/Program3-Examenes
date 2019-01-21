package examenenes.ext201806;

import examenenes.ext201806.datos.*;
import examenenes.ext201806.iu.VentanaMiniExcel;

/** Clase de lanzamiento del miniexcel con datos de prueba
 * @author andoni.eguiluz @ ingenieria @ deusto.es
 */
public class MiniExcel {

    public static void main(String[] args) {
    	// Crear datos de prueba
    	TablaDatos datos = new TablaDatos( 50, 10 );
    	datos.set(1, 1, new Numero(3.0));
    	datos.set(1, 2, new Numero(7.5));
    	datos.set(1, 3, new Numero(2.2));
    	datos.set(3, 1, new Texto("Suma:"));
    	try {
	    	datos.set(3, 2, new FormulaSuma( new RefCelda("B2"), new RefCelda("D2"), datos ));
    	} catch (NumberFormatException e) {}
    	// Crear ventana con esos datos
        VentanaMiniExcel v = new VentanaMiniExcel( datos );
        v.setVisible( true );
    }

}
