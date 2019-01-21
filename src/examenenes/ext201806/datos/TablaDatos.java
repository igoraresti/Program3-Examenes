package examenenes.ext201806.datos;

import java.io.*;
import java.util.*;

import javax.swing.JOptionPane;

/** Clase para gestionar objeto de tablas de datos para el miniexcel
 * @author andoni.eguiluz @ ingenieria @ deusto.es
 */
public class TablaDatos implements Serializable {
	private static final long serialVersionUID = -4896211263225307821L;
	
	private ValorCelda datos[][];      // Tabla de datos
	public int MAX_NUM_FILAS = 1000;   // Tama�o m�ximo de datos (filas)
	public int MAX_NUM_COLUMNAS = 100; // Tama�o m�ximo de datos (columnas)
	private HashSet<RefCelda> celdasSels; // Conjunto de celdas seleccionadas
	
	/** Construye una nueva tabla de datos miniexcel
	 * @param filas	N�mero de filas de la tabla (entre 1 y MAX_NUM_FILAS)
	 * @param columnas N�mero de columnas de la tabla (entre 1 y MAX_NUM_FILAS)
	 * @throws IndexOutOfBoundsException	Si se inicializan incorrectamente las filas o columnas
	 */
	public TablaDatos( int filas, int columnas ) throws IndexOutOfBoundsException {
		if (filas<1 || columnas<1 || filas>MAX_NUM_FILAS || columnas>MAX_NUM_COLUMNAS) {
			throw new IndexOutOfBoundsException( "TablaExcel creada de forma incorrecta con " + filas + " filas y " + columnas + " columnas." );
		}
		datos = new ValorCelda[filas][columnas];
		celdasSels = new HashSet<>(); // Sin celdas seleccionadas al principio
	}
	
	/** Devuelve el valor de la celda indicada por su fila-columna
	 * @param fila	Fila, de 0 a numFilas() - 1
	 * @param columna	Columna, de 0 a numColumnas() - 1 
	 * @return	Valor de la celda indicada, null si no est� inicializada
	 * @throws IndexOutOfBoundsException	Si los valores de fila,columna son incorrectos
	 */
	public ValorCelda get( int fila, int columna ) {
		return datos[fila][columna];
	}
	
	/** Cambia el valor de la celda indicada por su fila-columna
	 * @param fila	Fila, de 0 a numFilas() - 1
	 * @param columna	Columna, de 0 a numColumnas() - 1 
	 * @param val	Valor a introducir en esa celda 
	 * @throws IndexOutOfBoundsException	Si los valores de fila,columna son incorrectos
	 */
	public void set( int fila, int columna, ValorCelda val ) {
		datos[fila][columna] = val;
	}
	
	/** Vac�a todos los datos de la tabla, respetando su tama�o
	 */
	public void clear() {
		for (int fila=0; fila<datos.length; fila++)
			for (int col=0; col<datos[0].length; col++)
				datos[fila][col] = null;
	}
	
	/** Devuelve el n�mero de columnas
	 * @return	N�mero de columnas
	 */
	public int getColumnas() {
		return datos[0].length;
	}
	
	/** Devuelve el n�mero de filas
	 * @return	N�mero de filas
	 */
	public int getFilas() {
		return datos.length;
	}
	
	/** Devuelve el nombre de la columna indicada, "" si hay cualquier error
	 * @param col	N�mero de la columna a recuperar
	 * @return	Nombre de la columna: "A" para la 0, "B" para la 1 ... "Z", "AA", "AB", ... "AZ", "BA"...
	 */
	public String getNomColumna( int col ) {
		return RefCelda.getNomColumna(col);
	}
	
	/** Selecciona o deselecciona la celda indicada 
	 * @param refCelda	Referencia de celda
	 * @param seleccionar	true si se quieren seleccionar, false si se quieren deseleccionar
	 */
	public void selCelda( RefCelda refCelda, boolean seleccionar ) {
		if (seleccionar) {
			celdasSels.add( refCelda );
		} else {
			celdasSels.remove( refCelda );
		}
		System.out.println( celdasSels );
	}
	
	/** Devuelve la informaci�n de si la celda est� seleccionada
	 * @param refCelda	Referencia de la celda
	 * @return	true si la celda est� seleccionada, false en caso contrario
	 */
	public boolean isCeldaSel( RefCelda refCelda ) {
		return celdasSels.contains( refCelda );
	}
	
	/** Quita todas las celdas seleccionadas
	 */
	public void clearSel() {
		celdasSels.clear();
	}
	
    /** Escribe a fichero de texto el contenido de los datos del miniexcel
     * @param fSalida	Fichero en el que escribir
     */
    public void escribeAFichero( File fSalida ) {
	    try {
	    	PrintStream ps = new PrintStream( new FileOutputStream( fSalida ) );
	    	for (int f=0; f<getFilas(); f++)
	    		for (int c=0; c<getColumnas(); c++)
	    			if (get(f,c)!=null) {
	    				ps.println( f + "\t" + c + "\t" + get(f,c).getTextoEdicion() );
	    			}
	    	ps.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog( null, "Error en escritura: " + e.getClass().getName(), "Error", JOptionPane.ERROR_MESSAGE );
			e.printStackTrace();
		}
    }
    
    /** Lee de fichero de texto el contenido de los datos del miniexcel. Si hay cualquier error, lo ignora
     * @param fEntrada	Fichero del que leer
     */
    public void leeDeFichero( File fEntrada ) {
	    try {
	    	Scanner scanner = new Scanner( fEntrada );
	    	clear();  // Vac�a la tabla
	    	while (scanner.hasNextLine()) {
	    		String linea = scanner.nextLine();
	    		// La l�nea tiene que tener fila - tab - columna - tab - valor
	    		String[] val = linea.split( "\t" );
	    		if (val!=null && val.length==3) {
	    			try {
	    				int fila = Integer.parseInt( val[0] );
	    				int col = Integer.parseInt( val[1] );
	    				ValorCelda valorCelda = ValorCelda.getValorFromEdicion( val[2], this );
	    				if (valorCelda!=null) set( fila, col, valorCelda );
	    			} catch (Exception e) { e.printStackTrace(); }
	    		}
	    	}
			scanner.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(  null, "Error en lectura: " + e.getClass().getName(), "Error", JOptionPane.ERROR_MESSAGE );
			e.printStackTrace();
		}
    }
    

	
}

