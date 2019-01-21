package examenenes.ext201702.gui;

/** Interfaz Java para un proceso que puede probarse (en tiempo y espacio de memoria) en el banco de pruebas.
 * @author Andoni Egu�luz Mor�n
 * Facultad de Ingenier�a - Universidad de Deusto
 */
public interface ProcesoProbable {
	
	/** M�todo de inicializaci�n de test (si es necesario)
	 * @param tamanyoTest	Tama�o del test a realizar (t�picamente, tama�o de la estructura de datos a manejar)
	 */
	public void init( int tamanyoTest );
	
	/** Realizaci�n de la prueba. Debe llamarse antes al m�todo init (cuando la inicializaci�n sea necesaria).
	 * @param objetoProducido	Objeto que se devuelve como resultado del test, relacionado con el uso de memoria que se quiere medir.
	 */
	public Object test();

}
