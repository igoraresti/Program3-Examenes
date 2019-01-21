package examenenes.ext201702.gui;

/** Clase b�sica para realizar pruebas de eficiencia de memoria y tiempo de ejecuci�n de c�digo.
 * @author Andoni Egu�luz Mor�n
 * Facultad de Ingenier�a - Universidad de Deusto
 */
public class BancoDePruebas {
	private static long ultimoTiempo;
	private static Object ultimoResultado;
	
	/** Realiza un test del banco de pruebas, inicializ�ndolo previamente y devolviendo el tiempo que tarda<p>
	 * @param proc	Proceso a probar
	 * @param tamanyoPrueba	Tama�o a pasar a ese proceso (t�picamente, tama�o de la estructura que ese proceso maneja)
	 * @return	Tiempo que tarda el proceso (en nanosegundos). 
	 */
	public static long realizaTest( ProcesoProbable proc, int tamanyoPrueba ) {
		proc.init( tamanyoPrueba );
		ultimoTiempo = System.nanoTime();
		ultimoResultado = proc.test();
		return System.nanoTime() - ultimoTiempo;
	}	
	
	/** Realiza un test del banco de pruebas, inicializ�ndolo previamente y devolviendo el tiempo que tarda<p>
	 * Atenci�n: la granularidad de la medici�n de tiempo de System.currentTimeMillis() hace que medir tiempos por debajo
	 * de 10-15 milisegundos no sea nada preciso (especialmente en SO Windows). Usar System.nanoTime() en lugar de este m�todo para ese caso.
	 * @param proc	Proceso a probar
	 * @param tamanyoPrueba	Tama�o a pasar a ese proceso (t�picamente, tama�o de la estructura que ese proceso maneja)
	 * @return	Tiempo que tarda el proceso (en milisegundos). 
	 */
	public static long realizaTestMillis( ProcesoProbable proc, int tamanyoPrueba ) {
		proc.init( tamanyoPrueba );
		ultimoTiempo = System.currentTimeMillis();
		ultimoResultado = proc.test();
		return System.currentTimeMillis() - ultimoTiempo;
	}	
	
	/** Devuelve el tama�o del objeto creado por el �ltimo test realizado del banco de pruebas.<p>
	 * Previamente debe llamarse a realizaTest para que el proceso se realice y retorne ese objeto resultado.
	 * @return	Tama�o del objeto resultado del �ltimo test, en bytes
	 */
	public static int getTamanyoTest() {
		if (ultimoResultado==null) return 0;
		return ExploradorObjetos.getTamanyoObjeto( ultimoResultado );
	}
	
	/** Devuelve el objeto devuelto por el �ltimo test realizado del banco de pruebas.<p>
	 * Previamente debe llamarse a realizaTest para que el proceso se realice y retorne ese objeto resultado.
	 * @return	Objeto resultado del �ltimo test
	 */
	public static Object getTestResult() {
		return ultimoResultado;
	}
	
		// Clase de prueba del banco de pruebas
		// Prueba a recorrer un array completo de enteros en un sentido y en otro y calcula y visualiza su suma  (ver m�todo test)
		private static class RecorridoArray implements ProcesoProbable {
			int[] arrayPrueba;
			@Override
			public void init(int tamanyoTest) {
				arrayPrueba = new int[tamanyoTest];
			}
			@Override
			public Object test() {
				int suma = 0;
				if (arrayPrueba.length<arrayPrueba.length) throw new NullPointerException( "Error en test no inicializado" );  // Proceso no puede realizarse
				// Recorrido arriba
				for (int i=0; i<arrayPrueba.length; i++) {
					suma += arrayPrueba[i];
				}
				// Recorrido abajo
				for (int i=arrayPrueba.length-1; i>=0; i--) {
					suma += arrayPrueba[i];
				}
				System.out.println( "Proceso de prueba <RecorridoArray>. Suma " + suma );
				return arrayPrueba;
			}
		}
		
	public static void main(String[] args) {
		ProcesoProbable proc = new RecorridoArray();
		// Realiza la prueba para 10, 100, 1000... hasta 1000000.
		int tamanyo = 10;
		while (tamanyo <= 1000000) {
			long tiempo = realizaTest( proc, tamanyo );
			int espacio = getTamanyoTest();
			System.out.println( "Prueba array de " + tamanyo + " -- tiempo: " + tiempo + " msgs. / espacio = " + espacio + " bytes.");
			tamanyo *= 10;
		}
	}

}
