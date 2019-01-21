package examenenes.ord201801.item;

import java.awt.Color;
import examenenes.ord201801.EdicionZonasGPS;
import examenenes.ord201801.PuntoGPS;

public class Arbol extends ItemEnMapa {
	int edadAproximadaEnAnyos;

	/** Crea un �rbol representable en un mapa GPS
	 * @param punto	Punto GPS del �rbol
	 * @param nombre	Especie del �rbol
	 * @param edadAproximada	Edad aproximada del �rbol en a�os
	 */
	public Arbol(PuntoGPS punto, String nombre, int edadAproximada ) {
		super(punto, nombre);
		this.edadAproximadaEnAnyos = edadAproximada;
	}

	/** Devuelve la edad del �rbol
	 * @return La edad aproximada del �rbol en a�os
	 */
	public int getEdadAproximada() {
		return edadAproximadaEnAnyos;
	}

	/**	Cambia la edad del �rbol
	 * @param edadAproximada La edad del �rbol aproximada en a�os
	 */
	public void setEdadAproximada(int edadAproximada ) {
		this.edadAproximadaEnAnyos = edadAproximada;
	}
	
	/** Dibuja el �tem en un mapa gr�fico de la ventana
	 * @param ventana	Ventana en la que dibujar
	 * @param pintaEnVentana	true si se quiere pintar inmediatamente en el mapa, false si se pinta en el objeto gr�fico pero no se muestra a�n en pantalla
	 */
	@Override
	public void dibuja(EdicionZonasGPS ventana, boolean pintaEnVentana) {
		ventana.dibujaCirculo( punto.getLongitud(), punto.getLatitud(), 4, Color.GREEN, EdicionZonasGPS.stroke2m, true );
	}
	
}
