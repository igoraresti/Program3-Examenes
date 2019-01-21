package examen.ord201706;

import java.io.File;
import java.sql.*;
import java.util.*; 
import java.util.logging.*;
import javax.swing.JOptionPane;

//Documentación particular de foreign keys en sqlite en https://www.sqlite.org/foreignkeys.html

/** Clase de gestión de base de datos del sistema de ficheros e historial de cambios
 * @author andoni.eguiluz @ ingenieria.deusto.es
 */
public class BD {
	
	private static boolean ACTIVAR_LOG_A_CONSOLA_DE_ERROR = false;  // false desactiva log en System.err, true lo activa
	
	public static void main(String[] args) {
		Object resp = null;
		do {
			resp = JOptionPane.showInputDialog( null, "Selecciona operación de base de datos a realizar", "Gestión básica de BD de ficheros",
					JOptionPane.WARNING_MESSAGE, null, 
					new String[] { "Sacar BD a consola", "Borrar BD"} , "Sacar BD a consola" );
			if ("Sacar BD a consola".equals(resp)) {
				System.out.println( "Volcado de base de datos:" );
				int numFics = 0;
				int numCambs = 0;
				Connection con = initBD( "ficheros.bd" );
				Statement st = usarCrearTablasBD( con );
				ArrayList<Fich> lFichs = ficheroSelect( st, null );
				if (lFichs!=null) {
					numFics += lFichs.size();
					for (Fich fic : lFichs) {
						System.out.println( fic );
						ArrayList<Fich> lCambios = historiaSelect( st, fic.getPathCompleto() );
						if (lCambios!=null) {
							numCambs += lCambios.size();
							for (Fich cambio : lCambios) {
								System.out.println( "   Cambio: " + cambio.getNombre() + " - " + Utils.getEspacioDesc(cambio.getTamanyo()) + 
									" - " + Utils.sdf.format( cambio.getFecha() ) + " - " + Utils.sdf.format( cambio.getFechaActualizacion() ));
							}
						}
					}
				}
				System.out.println( "Total de ficheros: " + numFics + " - Total de cambios: " + numCambs );
			} else if ("Borrar BD".equals(resp)) {
				int resp2 = JOptionPane.showConfirmDialog( null, "¿Confirmas que quieres borrar la base de datos?  Se perderá toda su información", "Confirmación borrado", JOptionPane.YES_NO_OPTION );
				if (resp2==JOptionPane.YES_OPTION) {
					Connection con = initBD( "ficheros.bd" );
					Statement st = reiniciarBD( con );
					cerrarBD( con, st );
				}
			}
		} while (resp!=null);
	}

	private static Exception lastError = null;  // Información de último error SQL ocurrido
	
	/** Inicializa una BD SQLITE y devuelve una conexión con ella
	 * @param nombreBD	Nombre de fichero de la base de datos
	 * @return	Conexión con la base de datos indicada. Si hay algún error, se devuelve null
	 */
	public static Connection initBD( String nombreBD ) {
		try {
		    Class.forName("org.sqlite.JDBC");
		    Connection con = DriverManager.getConnection("jdbc:sqlite:" + nombreBD );
		    Statement st = con.createStatement();      // (1) Solo para foreign keys
		    st.execute( "PRAGMA foreign_keys = ON" );  // (1)
		    st.close();                                // (1)
			log( Level.INFO, "Conectada base de datos " + nombreBD, null );
		    return con;
		} catch (ClassNotFoundException | SQLException e) {
			lastError = e;
			log( Level.SEVERE, "Error en conexión de base de datos " + nombreBD, e );
			e.printStackTrace();
			return null;
		}
	}
	
	/** Devuelve statement para usar la base de datos
	 * @param con	Conexión ya creada y abierta a la base de datos
	 * @return	sentencia de trabajo si se crea correctamente, null si hay cualquier error
	 */
	public static Statement usarBD( Connection con ) {
		try {
			Statement statement = con.createStatement();
			statement.setQueryTimeout(30);  // poner timeout 30 msg
			return statement;
		} catch (SQLException e) {
			lastError = e;
			log( Level.SEVERE, "Error en uso de base de datos", e );
			e.printStackTrace();
			return null;
		}
	}
	
	/** Crea las tablas de la base de datos. Si ya existen, las deja tal cual
	 * @param con	Conexión ya creada y abierta a la base de datos
	 * @return	sentencia de trabajo si se crea correctamente, null si hay cualquier error
	 */
	public static Statement usarCrearTablasBD( Connection con ) {
		try {
			Statement statement = con.createStatement();
			statement.setQueryTimeout(30);  // poner timeout 30 msg
			try {
				statement.executeUpdate("create table fichero " +
					"(pathAbs string PRIMARY KEY" // Path absoluto - (1) Solo para foreign keys
					+ ", nombre string"           // Nombre de fichero
					+ ", tamanyo bigint"          // Tamaño del fichero en bytes
					+ ", fecha bigint"            // Fecha de última modificación del fichero (msgs. desde 1/1/1970)
					+ ", ultRevision bigint"      // Fecha de última revisión del fichero (msgs. desde 1/1/1970)
					+ ")" );
			} catch (SQLException e) {} // Tabla ya existe. Nada que hacer
			try {
				statement.executeUpdate("create table historiaCambios " +
					"(fichero_pathAbs string REFERENCES fichero(pathAbs) ON DELETE CASCADE" // Path absoluto - (1) Solo para foreign keys
					+ ", tamanyo bigint"          // Tamaño anterior del fichero en bytes
					+ ", fecha bigint"            // Fecha anterior de última modificación del fichero (msgs. desde 1/1/1970)
					+ ", ultRevision bigint"      // Fecha de última revisión del fichero (msgs. desde 1/1/1970)
					+ ")" );
			} catch (SQLException e) {} // Tabla ya existe. Nada que hacer
			log( Level.INFO, "Creada base de datos", null );
			return statement;
		} catch (SQLException e) {
			lastError = e;
			log( Level.SEVERE, "Error en creación de base de datos", e );
			e.printStackTrace();
			return null;
		}
	}
	
	/** Reinicia en blanco las tablas de la base de datos. 
	 * UTILIZAR ESTE MËTODO CON PRECAUCIÓN. Borra todos los datos que hubiera ya en las tablas
	 * @param con	Conexión ya creada y abierta a la base de datos
	 * @return	sentencia de trabajo si se borra correctamente, null si hay cualquier error
	 */
	public static Statement reiniciarBD( Connection con ) {
		try {
			Statement statement = con.createStatement();
			statement.setQueryTimeout(30);  // poner timeout 30 msg
			statement.executeUpdate("drop table if exists fichero");
			statement.executeUpdate("drop table if exists historiaCambios");
			log( Level.INFO, "Reiniciada base de datos", null );
			return usarCrearTablasBD( con );
		} catch (SQLException e) {
			log( Level.SEVERE, "Error en reinicio de base de datos", e );
			lastError = e;
			e.printStackTrace();
			return null;
		}
	}
	
	/** Cierra la base de datos abierta
	 * @param con	Conexión abierta de la BD
	 * @param st	Sentencia abierta de la BD
	 */
	public static void cerrarBD( Connection con, Statement st ) {
		try {
			if (st!=null) st.close();
			if (con!=null) con.close();
			log( Level.INFO, "Cierre de base de datos", null );
		} catch (SQLException e) {
			lastError = e;
			log( Level.SEVERE, "Error en cierre de base de datos", e );
			e.printStackTrace();
		}
	}
	
	/** Devuelve la información de excepción del último error producido por cualquiera 
	 * de los métodos de gestión de base de datos
	 */
	public static Exception getLastError() {
		return lastError;
	}
	
	/////////////////////////////////////////////////////////////////////
	//                      Operaciones de fichero                     //
	/////////////////////////////////////////////////////////////////////
	
	/** Añade un fichero a la tabla abierta de BD, usando la sentencia INSERT de SQL
	 * @param st	Sentencia ya abierta de Base de Datos (con la estructura de tabla correspondiente al fichero)
	 * @param file	Fichero a añadir en la base de datos
	 * @return	true si la inserción es correcta, false en caso contrario
	 */
	public static boolean ficheroInsert( Statement st, File file ) {
		String sentSQL = "";
		try {
			sentSQL = "insert into fichero values(" +
					"'" + secu(file.getAbsolutePath()) + "', " +
					"'" + secu(file.getName()) + "', " +
					"" + file.length() + ", " +
					"" + file.lastModified() + ", " +
					"" + System.currentTimeMillis() + "" +
					")";
			int val = st.executeUpdate( sentSQL );
			log( Level.INFO, "BD añadida " + val + " fila\t" + sentSQL, null );
			if (val!=1) {  // Se tiene que añadir 1 - error si no
				log( Level.SEVERE, "Error en insert de BD\t" + sentSQL, null );
				return false;  
			}
			return true;
		} catch (SQLException e) {
			log( Level.SEVERE, "Error en BD\t" + sentSQL, e );
			lastError = e;
			e.printStackTrace();
			return false;
		}
	}

	/** Realiza una consulta a la tabla abierta de ficheros de la BD, usando la sentencia SELECT de SQL
	 * @param st	Sentencia ya abierta de Base de Datos (con la estructura de tabla correspondiente al fichero)
	 * @param codigoSelect	Sentencia correcta de WHERE (sin incluir la partícula where) para filtrar la búsqueda (vacía si no se usa)
	 * @return	lista de ficheros cargados desde la base de datos, null si hay cualquier error
	 */
	public static ArrayList<Fich> ficheroSelect( Statement st, String codigoSelect ) {
		String sentSQL = "";
		ArrayList<Fich> ret = new ArrayList<>();
		try {
			sentSQL = "select * from fichero";
			if (codigoSelect!=null && !codigoSelect.equals(""))
				sentSQL = sentSQL + " where " + codigoSelect;
			ResultSet rs = st.executeQuery( sentSQL );
			while (rs.next()) {
				String path = rs.getString( "pathAbs" );
				String nombre = rs.getString( "nombre" );
				long tamanyo = rs.getLong( "tamanyo" );
				long fecha = rs.getLong( "fecha" );
				long ultRevision = rs.getLong( "ultRevision" );
				Fich file = new Fich( path, nombre, new java.util.Date(fecha), tamanyo );
				file.setFechaActualizacion( ultRevision );
				ret.add( file );
			}
			rs.close();
			log( Level.INFO, "BD\t" + sentSQL, null );
			return ret;
		} catch (Exception e) {
			log( Level.SEVERE, "Error en select BD\t" + sentSQL, e );
			lastError = e;
			e.printStackTrace();
			return null;
		}
	}

	/** Modifica un fichero en la tabla abierta de BD, usando la sentencia UPDATE de SQL
	 * @param st	Sentencia ya abierta de Base de Datos (con la estructura de tabla correspondiente al fichero)
	 * @param fic	Fichero a modificar en la base de datos. Se toma su path absoluto como clave
	 * @return	true si la inserción es correcta, false en caso contrario
	 */
	public static boolean ficheroUpdate( Statement st, File fic ) {
		String sentSQL = "";
		try {
			sentSQL = "update fichero set" +
					" nombre='" + secu(fic.getName()) + "', " +
					" tamanyo=" + fic.length() + ", " +
					" fecha=" + fic.lastModified() + ", " +
					" ultRevision=" + System.currentTimeMillis() + "" +
				" where pathAbs='" + secu(fic.getAbsolutePath()) + "'";
			int val = st.executeUpdate( sentSQL );
			log( Level.INFO, "BD modificada " + val + " fila\t" + sentSQL, null );
			if (val!=1) {  // Se tiene que modificar 1 - error si no
				log( Level.SEVERE, "Error en update de BD\t" + sentSQL, null );
				return false;  
			}
			return true;
		} catch (SQLException e) {
			log( Level.SEVERE, "Error en BD\t" + sentSQL, e );
			lastError = e;
			e.printStackTrace();
			return false;
		}
	}

	/** Borrar un fichero de la tabla abierta de BD, usando la sentencia DELETE de SQL
	 * @param st	Sentencia ya abierta de Base de Datos (con la estructura de tabla correspondiente al fichero)
	 * @param path	Path absoluto del fichero a borrar de la base de datos (clave)
	 * @return	true si el borrado es correcto, false en caso contrario
	 */
	public static boolean ficheroDelete( Statement st, String path ) {
		String sentSQL = "";
		try {
			sentSQL = "delete from fichero where pathAbs='" + secu(path) + "'";
			int val = st.executeUpdate( sentSQL );
			log( Level.INFO, "BD borrada " + val + " fila\t" + sentSQL, null );
			return (val==1);
		} catch (SQLException e) {
			log( Level.SEVERE, "Error en BD\t" + sentSQL, e );
			lastError = e;
			e.printStackTrace();
			return false;
		}
	}

	/////////////////////////////////////////////////////////////////////
	//                 Operaciones de historiaCambios                  //
	/////////////////////////////////////////////////////////////////////
	
	/** Añade un historial a la tabla abierta de BD, usando la sentencia INSERT de SQL
	 * @param st	Sentencia ya abierta de Base de Datos (con la estructura de tabla correspondiente a la historia de cambios)
	 * @param pathAbs	Path absoluto del fichero que ha cambiado
	 * @param tamanyo	Tamaño anterior en bytes del fichero que ha cambiado
	 * @param fecha	Fecha anterior de fichero (msgs. desde 1/1/1970)
	 * @param ultRevision	Fecha de última revisión de ese cambio (msgs. desde 1/1/1970)
	 * @return	true si la inserción es correcta, false en caso contrario
	 */
	public static boolean historiaInsert( Statement st, String pathAbs, long tamanyo, long fecha, long ultRevision ) {
		String sentSQL = "";
		try {
			sentSQL = "insert into historiaCambios values(" +
					"'" + pathAbs + "', " +
					"" + tamanyo + ", " +
					"" + fecha + ", " +
					"" + ultRevision + ")";
			int val = st.executeUpdate( sentSQL );
			log( Level.INFO, "BD añadida " + val + " fila\t" + sentSQL, null );
			if (val!=1) return false;  // Se tiene que añadir 1 - error si no
			return true;
		} catch (SQLException e) {
			log( Level.SEVERE, "Error en BD\t" + sentSQL, e );
			lastError = e;
			e.printStackTrace();
			return false;
		}
	}
	
	/** Realiza una consulta a la tabla abierta de historial de cambios de la BD, usando la sentencia SELECT de SQL
	 * @param st	Sentencia ya abierta de Base de Datos (con la estructura de tabla correspondiente a la historia de cambios)
	 * @param path	Path absoluto del cual se quieren cargar los cambios
	 * @return	lista de cambios cargados desde la base de datos, null si hay cualquier error SQL
	 */
	public static ArrayList<Fich> historiaSelect( Statement st, String path ) {
		String sentSQL = "";
		ArrayList<Fich> ret = new ArrayList<>();
		if (path==null || path.isEmpty()) return ret;  // Si no hay path se devuelve vacío
		try {
			sentSQL = "select * from historiaCambios where fichero_pathAbs='" + secu(path) + "'";
			ResultSet rs = st.executeQuery( sentSQL );
			String nombre = (new File(path)).getName();
			while (rs.next()) {
				long tamanyo = rs.getLong( "tamanyo" );
				long fecha = rs.getLong( "fecha" );
				long ultRev = rs.getLong( "ultRevision" );
				Fich f = new Fich( path, nombre, new java.util.Date(fecha), tamanyo );
				f.setFechaActualizacion( ultRev );
				ret.add( f );
			}
			rs.close();
			log( Level.INFO, "BD\t" + sentSQL, null );
			return ret;
		} catch (SQLException e) {
			log( Level.SEVERE, "Error en BD\t" + sentSQL, e );
			lastError = e;
			e.printStackTrace();
			return null;
		}
	}

	/////////////////////////////////////////////////////////////////////
	//                      Métodos privados                           //
	/////////////////////////////////////////////////////////////////////

	/** Devuelve el string "securizado" para SQL
	 * @param string	String a asegurar para SQL
	 * @return	String asegurado, sin caracteres extraños, sin saltos de línea ni tabuladores, con las comillas convertidas en dobles comillas
	 */
	public static String secu( String string ) {
		StringBuffer ret = new StringBuffer();
		for (char c : string.toCharArray()) {
			if ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZñÑáéíóúüÁÉÍÓÚÚ.,:;-_(){}[]-+*=<>'\"¿?¡!&%$@#/\\0123456789".indexOf(c)>=0) ret.append(c);
		}
		return ret.toString().replaceAll("'", "''");
	}
	

	/////////////////////////////////////////////////////////////////////
	//                      Logging                                    //
	/////////////////////////////////////////////////////////////////////
	
	private static Logger logger = null;
	// Método público para asignar un logger externo
	/** Asigna un logger ya creado para que se haga log de las operaciones de base de datos
	 * @param logger	Logger ya creado
	 */
	public static void setLogger( Logger logger ) {
		BD.logger = logger;
	}
	/** Devuelve el logger de BD
	 * @return	logger
	 */
	public static Logger getLogger() {
		initLogger();  // Crea el logger si no está iniciado
		return logger;
	}
		// Método local - crea el logger si no está iniciado
		private static void initLogger() {
			if (logger==null) {  // Logger por defecto local:
				logger = Logger.getLogger( BD.class.getName() );  // Nombre del logger - el de la clase
				logger.setLevel( Level.ALL );  // Loguea todos los niveles
				logger.setUseParentHandlers( ACTIVAR_LOG_A_CONSOLA_DE_ERROR ); // Conecta o desconecta la salida estándar a System.err
				try {
					logger.addHandler( new FileHandler( "bd.log.xml", true ) );  // Y saca el log a fichero xml
				} catch (Exception e) {
					logger.log( Level.SEVERE, "No se pudo crear fichero de log", e );
				}
			}
		}
		// Método local para loggear (si no se asigna un logger externo, se asigna uno local)
		private static void log( Level level, String msg, Throwable excepcion ) {
			initLogger();
			if (excepcion==null)
				logger.log( level, msg );
			else
				logger.log( level, msg, excepcion );
		}
	
	
	
}
