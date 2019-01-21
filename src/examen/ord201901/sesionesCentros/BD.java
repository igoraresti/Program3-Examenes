package examen.ord201901.sesionesCentros;

import java.sql.*;
import java.util.logging.*;

/** Clase de gestión de base de datos del examen 201811
 * @author andoni.eguiluz @ ingenieria.deusto.es
 */
public class BD {

	/** Inicializa una BD SQLITE y devuelve una conexión con ella
	 * @param nombreBD	Nombre de fichero de la base de datos
	 * @return	Conexión con la base de datos indicada. Si hay algún error, se devuelve null
	 */
	public static Connection initBD( String nombreBD ) {
		try {
		    Class.forName("org.sqlite.JDBC");
		    Connection con = DriverManager.getConnection("jdbc:sqlite:" + nombreBD );
			log( Level.INFO, "Conectada base de datos " + nombreBD, null );
		    return con;
		} catch (ClassNotFoundException | SQLException e) {
			log( Level.SEVERE, "Error en conexión de base de datos " + nombreBD, e );
			return null;
		}
	}
	
	/** Crea las tablas de la base de datos. Si ya existen, las deja tal cual. Devuelve un statement para trabajar con esa base de datos
	 * @param con	Conexión ya creada y abierta a la base de datos
	 * @return	sentencia de trabajo si se crea correctamente, null si hay cualquier error
	 */
	public static Statement usarCrearTablasBD( Connection con ) {
		try {
			Statement statement = con.createStatement();
			statement.setQueryTimeout(30);  // poner timeout 30 msg
			try {
				statement.executeUpdate("create table centroEd " +
					"(cod string" +                     // Código de centro
					", numSesiones integer" +           // Número de sesiones en el centro
					", satMen double, satEst double" +  // Satisfacción media de mentoras y estudiantes
					")");
			} catch (SQLException e) {} // Tabla ya existe. Nada que hacer
			try {
				statement.executeUpdate("create table centroSesion " +
					"(cod string" +         // Código de centro
					", numSes integer " +   // Número de sesión (de 1 a 6)
					", numEst integer" +    // Número de estudiantes de ese centro en esa sesión
					")");
			} catch (SQLException e) {} // Tabla ya existe. Nada que hacer
			log( Level.INFO, "Creada base de datos", null );
			return statement;
		} catch (SQLException e) {
			log( Level.SEVERE, "Error en creación de base de datos", e );
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
			statement.executeUpdate("drop table if exists centroEd");
			statement.executeUpdate("drop table if exists centroSesion");
			log( Level.INFO, "Reiniciada base de datos", null );
			return usarCrearTablasBD( con );
		} catch (SQLException e) {
			log( Level.SEVERE, "Error en reinicio de base de datos", e );
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
			log( Level.SEVERE, "Error en cierre de base de datos", e );
		}
	}
	
	/////////////////////////////////////////////////////////////////////
	//                      Operaciones sobre tablas                   //
	/////////////////////////////////////////////////////////////////////
	
	/** Añade un centro educativo a la tabla abierta de BD, usando la sentencia INSERT de SQL
	 * @param st	Sentencia ya abierta de Base de Datos (con la estructura de tabla correspondiente al centro)
	 * @param centro	Centro a añadir en la base de datos
	 * @return	true si la inserción es correcta, false en caso contrario
	 */
	public static boolean centroInsert( Statement st, CentroEd centro ) {
		String sentSQL = "";
		try {
			sentSQL = "insert into centroEd (cod, numSesiones, satMen, satEst) values(" +
					"'" + secu(centro.getCodigo()) + "', " +
					centro.getContSesiones().get() + ", " +
					centro.getMediaSatisfMentoras() + ", " +
					centro.getMediaSatisfEstuds() +
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
			return false;
		}
	}

	/** Realiza una consulta a la tabla abierta de centros de la BD, usando la sentencia SELECT de SQL
	 * @param st	Sentencia ya abierta de Base de Datos (con la estructura de tabla correspondiente al centro)
	 * @param codCentro	Código de centro a buscar
	 * @return	true si el centro ya existe, false en caso contrario
	 */
	public static boolean centroSelect( Statement st, String codCentro ) {
		String sentSQL = "";
		try {
			sentSQL = "select * from centroEd where cod='" + secu(codCentro) +"'";
			ResultSet rs = st.executeQuery( sentSQL );
			boolean existe = rs.next();  // Si existe el resultset no es vacío
			rs.close();
			log( Level.INFO, "BD\t" + sentSQL, null );
			return existe;
		} catch (SQLException e) {
			log( Level.SEVERE, "Error en BD\t" + sentSQL, e );
			e.printStackTrace();
			return false;
		}
	}

	/** Modifica un centro en la tabla abierta de BD, usando la sentencia UPDATE de SQL
	 * @param st	Sentencia ya abierta de Base de Datos (con la estructura de tabla correspondiente al centro)
	 * @param centro	Centro a modificar en la base de datos. Se toma su código como clave
	 * @return	true si la modificación es correcta, false en caso contrario
	 */
	public static boolean centroUpdate( Statement st, CentroEd centro ) {
		String sentSQL = "";
		try {
			sentSQL = "update centroEd set" +
					" numSesiones=" + centro.getContSesiones().get() + ", " +
					" satMen=" + centro.getMediaSatisfMentoras() + ", " +
					" satEst=" + centro.getMediaSatisfEstuds() +
					" where cod='" + secu(centro.getCodigo()) + "'";
			int val = st.executeUpdate( sentSQL );
			log( Level.INFO, "BD modificada " + val + " fila\t" + sentSQL, null );
			if (val!=1) {  // Se tiene que modificar 1 - error si no
				log( Level.SEVERE, "Error en update de BD\t" + sentSQL, null );
				return false;  
			}
			return true;
		} catch (SQLException e) {
			log( Level.SEVERE, "Error en BD\t" + sentSQL, e );
			e.printStackTrace();
			return false;
		}
	}

	/** Borrar un centro de la tabla abierta de BD, usando la sentencia DELETE de SQL
	 * @param st	Sentencia ya abierta de Base de Datos (con la estructura de tabla correspondiente al centro)
	 * @param centro	Centro a borrar de la base de datos  (se toma su código para el borrado)
	 * @return	true si el borrado es correcto, false en caso contrario
	 */
	public static boolean centroDelete( Statement st, CentroEd centro ) {
		String sentSQL = "";
		try {
			sentSQL = "delete from centroEd where nick='" + secu(centro.getCodigo()) + "'";
			int val = st.executeUpdate( sentSQL );
			log( Level.INFO, "BD borrada " + val + " fila\t" + sentSQL, null );
			return (val==1);
		} catch (SQLException e) {
			log( Level.SEVERE, "Error en BD\t" + sentSQL, e );
			e.printStackTrace();
			return false;
		}
	}

	/////////////////////////////////////////////////////////////////////
	//                      Operaciones de sesiones                    //
	/////////////////////////////////////////////////////////////////////
	
	/** Añade una sesión a la tabla abierta de BD, usando la sentencia INSERT de SQL
	 * @param st	Sentencia ya abierta de Base de Datos (con la estructura de tabla correspondiente a la sesión)
	 * @param codCentro	Código de centro de sesión a añadir en la base de datos
	 * @param numSesion	Número de sesión
	 * @param numEsts	Número de estudiantes
	 * @return	true si la inserción es correcta, false en caso contrario
	 */
	public static boolean sesionInsert( Statement st, String codCentro, int numSesion, int numEsts ) {
		String sentSQL = "";
		try {
			sentSQL = "insert into centroSesion (cod, numSes, numEst) values(" +
					"'" + secu(codCentro) + "', " +
					"" + numSesion + ", " +
					"" + numEsts + ")";
			int val = st.executeUpdate( sentSQL );
			log( Level.INFO, "BD añadida " + val + " fila\t" + sentSQL, null );
			if (val!=1) return false;  // Se tiene que añadir 1 - error si no
			return true;
		} catch (SQLException e) {
			log( Level.SEVERE, "Error en BD\t" + sentSQL, e );
			return false;
		}
	}

	/** Realiza una consulta a la tabla abierta de sesiones de la BD, usando la sentencia SELECT de SQL
	 * @param st	Sentencia ya abierta de Base de Datos (con la estructura de tabla correspondiente a la sesión)
	 * @param codCentro	Código de centro del cual se quieren cargar los estudiantes
	 * @param numSesion	Número de sesión que se quiere buscar (de 1 a 6)
	 * @return	Número de estudiantes de esa sesión, -1 si no existe el registro en la BD o hay cualquier error
	 */
	public static int sesionSelect( Statement st, String codCentro, int numSesion ) {
		String sentSQL = "";
		try {
			sentSQL = "select * from centroSesion where cod='" + secu(codCentro) + "' and numSes=" + numSesion;
			ResultSet rs = st.executeQuery( sentSQL );
			int ret = -1;
			if (rs.next()) {
				ret = rs.getInt( "numEst" );
			}
			rs.close();
			log( Level.INFO, "BD\t" + sentSQL, null );
			return ret;
		} catch (SQLException e) {
			log( Level.SEVERE, "Error en BD\t" + sentSQL, e );
			e.printStackTrace();
			return -1;
		}
	}
	
	/** Modifica una sesión en la tabla abierta de BD, usando la sentencia UPDATE de SQL
	 * @param st	Sentencia ya abierta de Base de Datos (con la estructura de tabla correspondiente a la sesión)
	 * @param codCentro	Código de centro a modificar en la base de datos
	 * @param numSesion	Número de sesión a modificar en la base de datos (de 1 a 6)
	 * @param numEsts Número de estudiantes que actualizar en la base de datos para ese centro y sesión
	 * @return	true si la modificación es correcta, false en caso contrario
	 */
	public static boolean sesionUpdate( Statement st, String codCentro, int numSesion, int numEsts ) {
		String sentSQL = "";
		try {
			sentSQL = "update centroSesion set" +
					" numEst=" + numEsts +
					" where cod='" + secu(codCentro) + "' and numSes=" + numSesion;
			int val = st.executeUpdate( sentSQL );
			log( Level.INFO, "BD modificada " + val + " fila\t" + sentSQL, null );
			if (val!=1) {  // Se tiene que modificar 1 - error si no
				log( Level.SEVERE, "Error en update de BD\t" + sentSQL, null );
				return false;  
			}
			return true;
		} catch (SQLException e) {
			log( Level.SEVERE, "Error en BD\t" + sentSQL, e );
			return false;
		}
	}


	/////////////////////////////////////////////////////////////////////
	//                      Métodos privados                           //
	/////////////////////////////////////////////////////////////////////

	// Devuelve el string "securizado" para volcarlo en SQL
	private static String secu( String string ) {
		return string.replaceAll( "'",  "''" );
	}
	

	/////////////////////////////////////////////////////////////////////
	//                      Logging                                    //
	/////////////////////////////////////////////////////////////////////
	
	private static Logger logger = null;
	
	// Método local para loggear
	private static void log( Level level, String msg, Throwable excepcion ) {
		if (logger==null) {  // Logger por defecto local:
			logger = Logger.getLogger( BD.class.getName() );  // Nombre del logger - el de la clase
			logger.setLevel( Level.ALL );  // Loguea todos los niveles
		}
		if (excepcion==null)
			logger.log( level, msg );
		else
			logger.log( level, msg, excepcion );
	}
	
}
