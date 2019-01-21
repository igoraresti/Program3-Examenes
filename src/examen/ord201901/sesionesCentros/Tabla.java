package examen.ord201901.sesionesCentros;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/** Tabla de datos bidimensional de cualquier tipo para análisis posterior
 * Composición: una serie de cabeceras (columnas) con una serie de filas de datos (objetos) que tienen un dato para cada columna
 * @author andoni.eguiluz @ ingenieria.deusto.es
 */
public class Tabla {
	
	// =================================================
	// Atributos
	
	private ArrayList<String> headers; // Nombres de las cabeceras-columnas
	private ArrayList<ArrayList<String>> data; // Datos de la tabla (en el orden de las columnnas), implementados todos como strings
	private ArrayList<Class<?>> types; // Tipos de cada una de las columnas (inferidos de los datos strings)
	
	private static SimpleDateFormat sdf = new SimpleDateFormat( "dd/MM/yyyy HH:mm:ss" );

	// =================================================
	// Métodos

	/** Crea una tabla de datos vacía (sin cabeceras ni datos)
	 */
	public Tabla() {
		headers = new ArrayList<>();
		data = new ArrayList<>();
	}
	
	/** Crea una tabla de datos vacía con cabeceras
	 * @param headers	Nombres de las cabeceras de datos
	 */
	public Tabla( ArrayList<String> headers ) {
		this.headers = headers;
		data = new ArrayList<>();
	}
	
	/** Cambia las cabeceras
	 * @param headers	Nombres de las cabeceras de datos
	 */
	public void setHeaders( ArrayList<String> headers ) {
		this.headers = headers;
	}
	
	/** Añade una columna al final de las existentes
	 * @param header	Nuevo nombre de cabecera para la columna
	 * @param defaultValue	Valor por defecto para asignar a cada fila existente en esa nueva columna
	 */
	public void addColumn( String header, String defaultValue ) {
		headers.add( header );
		for (ArrayList<String> line : data) {
			line.add( defaultValue );
		}
		if (types!=null) {
			types.add( calcDataType( defaultValue ) );
		}
	}
	
	/** Añade una línea de datos al final de la tabla
	 * @param line	New data line
	 */
	public void addDataLine( ArrayList<String> line ) {
		data.add( line );
	}
	
	/** Chequea si una cabecera particular existe, y devuelve su número de columna
	 * @param header	Nombre de cabecera a encontrar
	 * @param exact	true si el match debe ser exacto, false si vale con que sea parcial
	 * @return	número de la columna de primera cabecera que encaja con el nombre pedido, -1 si no existe ninguna
	 */
	public int getColumnWithHeader( String header, boolean exact ) {
		String headerUp = header.toUpperCase();
		int ret = -1;
		for (int col=0; col<headers.size();col++) {
			if (exact && headers.get(col).equals( header ) || !exact && headers.get(col).toUpperCase().contains( headerUp )) {
				ret = col;
				break;
			}
		}
		return ret;
	}
	
	/** Devuelve tamaño de la tabla (número de filas de datos)
	 * @return	Número de filas de datos, 0 si no hay ninguno
	 */
	public int size() {
		return data.size();
	}
	
	/**  Devuelve el número de columnas de la tabla
	 * @return	Número de columnas
	 */
	public int getWidth() {
		return headers.size();
	}
	
	/** Devuelve un valor de dato de la tabla
	 * @param row	Número de fila
	 * @param col	Número de columna
	 * @return	Dato de ese valor
	 */
	public String get( int row, int col ) {
		return data.get( row ).get( col );
	}
	
	/** Modifica un valor de dato de la tabla
	 * @param row	Número de fila
	 * @param col	Número de columna
	 * @param value	Valor a modificar en esa posición
	 */
	public void set( int row, int col, String value ) {
		data.get( row ).set( col, value );
	}
	
	/** Devuelve una fila completa de la tabla
	 * @param row	Número de fila
	 * @return	Lista de valores de esa fila
	 */
	public ArrayList<String> getFila( int row ) {
		return data.get(row);
	}
	
	/** Procesa tabla de datos con los datos ya existentes. Calcula los tipos de datos (inferidos de los valores)
	 * @return	0 si el proceso es correcto, otro valor si se detecta algún error (número de líneas de datos erróneas - no hay el mismo número de datos que número de cabeceras)
	 */
	public int calcTypes() {
		// 1.- calcs errors
		int ok = 0;
		int lin = 1;
		for (ArrayList<String> line : data) if (line.size()!=headers.size()) {
			ok++;
			if (LOG_CONSOLE_CSV) {
				System.out.println( "Error en línea " + lin + ": " + line.size() + " valores en vez de " + headers.size() );
			}
			lin++;
		}
		// 2.- calcs data types (if not error)
		if (ok>0) return ok;
		types = new ArrayList<>();
		for (int col=0; col<headers.size(); col++) {
			Class<?> type = String.class;
			if (data.size()>0) {
				type = calcDataType( data.get(0).get(col) );
				if (type!=String.class) { // String is the most general - not more info needed
					for (int row=1; row<data.size(); row++) {
						Class<?> nextType = calcDataType( data.get(row).get(col) );
						if (type==null) {
							type = nextType;
						} else {
							if (nextType==String.class) { // String is the most general - not more info needed
								type = String.class;
								break;
							} else {
								if (nextType!=null) {
									if (type!=nextType) {
										if (type==Integer.class && nextType==Double.class) {
											type = Double.class;
										} else if (type==Double.class && nextType==Integer.class) {
											// nothing (type = Double)
										} else { // Dif type: must be consdered as string
											type = String.class;
											break;
										}
									}
								}
							}
						}
					}
				}
			}
			types.add( type );
		}
		return ok;
	}
		private Class<?> calcDataType( String value ) {
			if (value.isEmpty()) return null;
			Class<?> ret = String.class;
			try {
				Integer.parseInt( value );
				ret = Integer.class;
			} catch (NumberFormatException e) {
				try {
					Double.parseDouble( value.replaceAll(",", ".") );
					ret = Double.class;
				} catch (NumberFormatException e2) {
					try {
						sdf.parse( value );
						ret = Date.class;
					} catch (ParseException e3) {
					}
				}
			}
			return ret;
		}

	// =================================================
	// toString
		
	@Override
	public String toString() {
		String ret = "";
		boolean ini = true;
		for (String header : headers) {
			if (!ini) ret += "\t";
			ret += header;
			ini = false;
		}
		ret += "\n";
		for (ArrayList<String> lin : data) {
			ini = true;
			for (String val : lin) {
				if (!ini) ret += "\t";
				ret += val;
				ini = false;
			}
			ret += "\n";
		}
		return ret;
	}
	
	// =================================================
	// Métodos estáticos
	
	/** Crea una tabla partiendo de una colección de valores convertibles
	 * @param vals	Valores a convertir en tabla
	 * @return	Tabla creada partiendo de esos valores
	 */
	public static Tabla createTablaFromColl( Collection<? extends ConvertibleEnTabla> vals  ) {
		Tabla ret = new Tabla();
		if (vals.size()>0) {
			Iterator<? extends ConvertibleEnTabla> it = vals.iterator();
			ConvertibleEnTabla fila = it.next();
			for (int col=0; col<fila.getNumColumnas(); col++) {
				ret.addColumn( fila.getNombreColumna(col), "" );
			}
			do {
				ArrayList<String> linea = new ArrayList<String>();
				for (int col=0; col<fila.getNumColumnas(); col++) {
					linea.add( fila.getValorColumna(col) );
				}
				ret.addDataLine( linea );
				if (it.hasNext()) fila = it.next();
				else fila = null;
			} while (fila!=null);
		}
		return ret;
	}

		private static boolean LOG_CONSOLE_CSV = false;  // Log en consola del csv
		
	// Método de carga de tabla desde CSV
	/** Procesa un fichero csv y lo carga devolviéndolo en una nueva tabla
	 * @param file	Fichero del csv
	 * @return	Nuevo objeto tabla con los contenidos de ese csv
	 * @throws IOException
	 */
	public static Tabla processCSV( File file ) 
	throws IOException // Error de I/O
	{
		Tabla tabla = processCSV( file.toURI().toURL() );
		return tabla;
	}
	
	/** Procesa un fichero csv y lo carga devolviéndolo en una nueva tabla
	 * @param urlCompleta	URL del csv
	 * @return	Nuevo objeto tabla con los contenidos de ese csv
	 * @throws IOException
	 * @throws UnknownHostException
	 * @throws FileNotFoundException
	 * @throws ConnectException
	 */
	public static Tabla processCSV( URL url ) 
	throws MalformedURLException,  // URL incorrecta 
	 IOException, // Error al abrir conexión
	 UnknownHostException, // servidor web no existente
	 FileNotFoundException, // En algunos servidores, acceso a página inexistente
	 ConnectException // Error de timeout
	{
		Tabla table = new Tabla();
		BufferedReader input = null;
		InputStream inStream = null;
		try {
		    URLConnection connection = url.openConnection();
		    connection.setDoInput(true);
		    inStream = connection.getInputStream();
		    input = new BufferedReader(new InputStreamReader( inStream, "UTF-8" ));  // Supone utf-8 en la codificación de texto
		    String line = "";
		    int numLine = 0;
		    while ((line = input.readLine()) != null) {
		    	numLine++;
		    	if (LOG_CONSOLE_CSV) System.out.println( numLine + "\t" + line );
		    	try {
			    	ArrayList<String> l = processCSVLine( input, line, numLine );
			    	if (LOG_CONSOLE_CSV) System.out.println( "\t" + l.size() + "\t" + l );
			    	if (numLine==1) {
			    		table.setHeaders( l );
			    	} else {
			    		if (!l.isEmpty())
			    			table.addDataLine( l );
			    	}
		    	} catch (StringIndexOutOfBoundsException e) {
		    		/* if (LOG_CONSOLE_CSV) */ System.err.println( "\tError: " + e.getMessage() );
		    	}
		    }
		} finally {
			try {
				inStream.close();
				input.close();
			} catch (Exception e2) {
			}
		}
		table.calcTypes();
		for (int fila=table.size()-1; fila>=0; fila--) {  // Iteramos de arriba abajo porque vamos a ir quitando filas
			if (table.getFila(fila).size()!=table.headers.size()) {
				table.data.remove( fila );
			}
		}
	    return table;
	}
	
		/** Procesa una línea de entrada de csv	
		 * @param input	Stream de entrada ya abierto
		 * @param line	La línea YA LEÍDA desde input
		 * @param numLine	Número de línea ya leída
		 * @return	Lista de strings procesados en el csv. Si hay algún string sin acabar en la línea actual, lee más líneas del input hasta que se acaben los strings o el input
		 * @throws StringIndexOutOfBoundsException
		 */
		public static ArrayList<String> processCSVLine( BufferedReader input, String line, int numLine ) throws StringIndexOutOfBoundsException {
			ArrayList<String> ret = new ArrayList<>();
			int posCar = 0;
			boolean inString = false;
			boolean finString = false;
			String stringActual = "";
			char separador = 0;
			while (line!=null && (posCar<line.length() || line.isEmpty() && posCar==0)) {
				if (line.isEmpty() && posCar==0) {
					if (!inString) return ret;  // Línea vacía
				} else {
					char car = line.charAt( posCar );
					if (car=='"') {
						if (inString) {
							if (nextCar(line,posCar)=='"') {  // Doble "" es un "
								posCar++;
								stringActual += "\"";
							} else {  // " de cierre
								inString = false;
								finString = true;
							}
						} else {  // !inString
							if (stringActual.isEmpty()) {  // " de apertura
								inString = true;
							} else {  // " después de valor - error
								throw new StringIndexOutOfBoundsException( "\" after data in char " + posCar + " of line [" + line + "]" );
							}
						}
					} else if (car==',' || car==';') {
						if (inString) {  // separador dentro de string
							stringActual += car;
						} else {  // separador que separa valores
							if (separador==0) { // Si no se había encontrado separador hasta ahora
								separador = car;
								ret.add( stringActual );
								stringActual = "";
								finString = false;
							} else { // Si se había encontrado, solo vale el mismo (, o ;)
								if (separador==car) {  // Es un separador
									ret.add( stringActual );
									stringActual = "";
									finString = false;
								} else {  // Es un carácter normal
									if (finString) throw new StringIndexOutOfBoundsException( "Data after string in char " + posCar + " of line [" + line + "]");  // valor después de string - error
									stringActual += car;
								}
							}
						}
					} else {  // Carácter dentro de valor
						if (finString) throw new StringIndexOutOfBoundsException( "Data after string in char " + posCar + " of line [" + line + "]");  // valor después de string - error
						stringActual += car;
					}
					posCar++;
				}
				if (posCar>=line.length() && inString) {  // Se ha acabado la línea sin acabarse el string. Eso es porque algún string incluye salto de línea. Se sigue con la siguiente línea
					line = null;
				    try {
						line = input.readLine();
				    	if (LOG_CONSOLE_CSV) System.out.println( "  " + numLine + " (add)\t" + line );
						posCar = 0;
						stringActual += "\n";
					} catch (IOException e) {}  // Si la línea es null es que el fichero se ha acabado ya o hay un error de I/O
				}
			}
			if (inString) throw new StringIndexOutOfBoundsException( "String not closed in line " + numLine + ": [" + line + "]");
			ret.add( stringActual );
			return ret;
		}

			// Devuelve el siguiente carácter (car 0 si no existe el siguiente carácter)
			private static char nextCar( String line, int posCar ) {
				if (posCar+1<line.length()) return line.charAt( posCar + 1 );
				else return Character.MIN_VALUE;
			}

	
	// =================================================
	// Métodos relacionados con el modelo de tabla (cuando se quiere utilizar esta tabla en una JTable)
	
	private TablaTableModel miModelo = null;
	/** Devuelve un modelo de tabla de este objeto tabla para poderse utilizar como modelo de datos en una JTable
	 * @return	modelo de datos de la tabla
	 */
	public TablaTableModel getTableModel() {
		if (miModelo==null) {
			miModelo = new TablaTableModel();
		}
		return miModelo;
	}
	
	public class TablaTableModel implements TableModel {
		@Override
		public int getRowCount() {
			return data.size();
		}
		@Override
		public int getColumnCount() {
			return headers.size();
		}
		@Override
		public String getColumnName(int columnIndex) {
			return headers.get(columnIndex);
		}
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return String.class; //  types.get(columnIndex);  si se quisiera tipos específicos
		}
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) { // Estas tablas de datos no son editables por defecto
			return false;
		}
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return data.get(rowIndex).get(columnIndex);
		}
		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			for (TableModelListener l : lListeners) {
				l.tableChanged( new TableModelEvent( this, rowIndex, rowIndex, columnIndex, TableModelEvent.UPDATE ) );
			}
		}
		
		private ArrayList<TableModelListener> lListeners = new ArrayList<>();
		@Override
		public void addTableModelListener(TableModelListener l) {
			lListeners.add( l );
		}
		@Override
		public void removeTableModelListener(TableModelListener l) {
			lListeners.remove( l );
		}
		
	}

}
