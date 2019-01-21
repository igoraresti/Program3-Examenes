package examen.ord201701;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main {
	
	private static String FICHERO_FARMACIAS = "farmaciasguardia.dat";
	
	public static MapaFarmacias mapaActual = null;
	private static long MAX_SIZE_FICHERO_LOG = 50L * 1024L * 1024L;  // 50 Mb tama帽o m谩ximo fichero log para reiniciarlo
	public static Logger logger = initLogger();
	public static VentanaFarmacias miVentana = null;

	private static final String NOMBRE_FICHERO_LOG = "farmaciasGuardia";
	private static final String EXT_FICHERO_LOG = ".log.xml";
	private static Logger initLogger() {
		if (logger==null) {  // Logger por defecto local:
			// Reinicio de fichero de logger si ya muy grande
			File fLoggerAnt = new File( NOMBRE_FICHERO_LOG + EXT_FICHERO_LOG );
			if (fLoggerAnt.exists() && fLoggerAnt.length() > MAX_SIZE_FICHERO_LOG ) {
				String newFicLog = NOMBRE_FICHERO_LOG + "-" + fLoggerAnt.lastModified() + EXT_FICHERO_LOG;
				try {
					Files.move( fLoggerAnt.toPath(), Paths.get(newFicLog) );  // Renombra el fichero para empezar de nuevo
				} catch (Exception e) {}
			}
			// Creaci贸n de logger asociado a fichero de logger
			logger = Logger.getLogger( Main.class.getName() );  // Nombre del logger - el de la clase
			logger.setLevel( Level.ALL );  // Loguea todos los niveles
			try {
				// logger.addHandler( new FileHandler( "editoraverias-" + System.currentTimeMillis() + ".log.xml" ) );  // Y saca el log a fichero xml
				logger.addHandler( new FileHandler( NOMBRE_FICHERO_LOG + EXT_FICHERO_LOG, true ) );  // Y saca el log a fichero xml (a帽adiendo al log previo)
			} catch (Exception e) {
				JOptionPane.showMessageDialog( null, "隆Atenci贸n! No se podr谩 crear fichero de log.", 
						"Error en creaci贸n de fichero", JOptionPane.ERROR_MESSAGE );
			}
		}
		return logger;
	}

	public static void main(String[] args) {
		logger.log( Level.INFO, "Inicio de ejecuci贸n de Main" );
		try {
			SwingUtilities.invokeAndWait( new Runnable() { @Override public void run() {
				miVentana = new VentanaFarmacias();
				miVentana.setVisible( true );
			}});
		} catch (Exception e) {
			e.printStackTrace();
			Main.logger.log( Level.INFO, "Error en creaci贸n de ventana", e );
		}
		cargaFarmacias();
		miVentana.actualizarReloj();
		// TAREA 3 - Lanzar hilo
		// TODO
	}

	/** Carga farmacias en memoria, en el mapa de localidad - farmacias
	 */
	public static void cargaFarmacias() {
		MapaFarmacias mapaAnterior = null;
		try {
			mapaAnterior = new MapaFarmacias( FICHERO_FARMACIAS );   // Intenta cargar del fichero
		} catch (NullPointerException e) { logger.log( Level.INFO, "(Tarea 2) No existe fichero de mapa previo", e ); }
		do {
			try {
				mapaActual = MapaFarmacias.cargaMapaFarmaciasGuardia(0);   // Intenta cargar de la web
				mapaActual.saveToFile( FICHERO_FARMACIAS );
			} catch (IOException e) { logger.log( Level.INFO, "Error en guardado de fichero", e );
			} catch (NullPointerException e) { logger.log( Level.INFO, "Error en carga de internet", e ); }
			if (mapaAnterior==null && mapaActual==null) {  // Pausa para reintentar carga en un minuto
				try { Thread.sleep( 60000L ); } catch (Exception e) { logger.log( Level.INFO, "Hilo interrumpido" ); }  // 1 min
			}
		} while (mapaActual==null && mapaAnterior==null);
		if (mapaActual==null) mapaActual = mapaAnterior;
		actualizaFarmaciasEnPantalla();
		miVentana.cargaFarmaciasEnTabla( mapaActual );
		miVentana.setTitle( "Farmacias de guardia del d韆 " + mapaActual.getDia() + "/" + mapaActual.getMes() );
	}
	
	/** Actualiza las farmacias que se ven en pantalla partiendo del mapa de farmacias que hay cargado en memoria
	 */
	public static void actualizaFarmaciasEnPantalla() {
		String farmAhora = "";
		for (String loc : mapaActual.getMapaFarmacias().keySet()) {
			if (loc.equals( "Bilbao")) {
				for (FarmaciaGuardia f : mapaActual.getMapaFarmacias().get(loc)) {
					if (f.estaAbiertaAhora( 15 )) {
						if (!farmAhora.isEmpty())
							farmAhora = farmAhora + "\n";
						farmAhora = farmAhora + f.getHoraDesdeSt() + "-" + f.getHoraHastaSt() + " - " + f.getDireccion();
					}
				}
			}
		}
		miVentana.actualizarReloj();
		miVentana.cargaFarmaciasAhora( farmAhora );   // Carga farmacias abiertas ahora
	}

	// TAREA 3 - Clase de hilo
	// TODO
	// public static HiloCambioPantalla hiloCambioPantalla;  // Descomentar esta l韓ea
	// class HiloCambioPantalla  // Crear esta clase
	
}
