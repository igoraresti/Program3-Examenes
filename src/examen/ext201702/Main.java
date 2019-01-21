package examen.ext201702;

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
	private static long MAX_SIZE_FICHERO_LOG = 50L * 1024L * 1024L;  // 50 Mb tamaño máximo fichero log para reiniciarlo
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
			// Creación de logger asociado a fichero de logger
			logger = Logger.getLogger( Main.class.getName() );  // Nombre del logger - el de la clase
			logger.setLevel( Level.ALL );  // Loguea todos los niveles
			try {
				// logger.addHandler( new FileHandler( "editoraverias-" + System.currentTimeMillis() + ".log.xml" ) );  // Y saca el log a fichero xml
				logger.addHandler( new FileHandler( NOMBRE_FICHERO_LOG + EXT_FICHERO_LOG, true ) );  // Y saca el log a fichero xml (añadiendo al log previo)
			} catch (Exception e) {
				JOptionPane.showMessageDialog( null, "¡Atención! No se podrá crear fichero de log.", 
						"Error en creación de fichero", JOptionPane.ERROR_MESSAGE );
			}
		}
		return logger;
	}

	public static void main(String[] args) {
		logger.log( Level.INFO, "Inicio de ejecución de Main" );
		try {
			SwingUtilities.invokeAndWait( new Runnable() { @Override public void run() {
				miVentana = new VentanaFarmacias();
				miVentana.setVisible( true );
			}});
		} catch (Exception e) {
			e.printStackTrace();
			Main.logger.log( Level.INFO, "Error en creación de ventana", e );
		}
		cargaFarmacias();
		miVentana.actualizarReloj();
		hiloCambioPantalla = new HiloCambioPantalla();
		hiloCambioPantalla.start();
	}

	/** Carga farmacias en memoria, en el mapa de localidad - farmacias
	 */
	public static void cargaFarmacias() {
		MapaFarmacias mapaAnterior = null;
		try {
			mapaAnterior = new MapaFarmacias( FICHERO_FARMACIAS );   // Intenta cargar del fichero
		} catch (NullPointerException e) { logger.log( Level.INFO, "No existe fichero de mapa previo", e ); }
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
		miVentana.setTitle( "Farmacias de guardia del día " + mapaActual.getDia() + "/" + mapaActual.getMes() );
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

	public static HiloCambioPantalla hiloCambioPantalla;
	public static class HiloCambioPantalla extends Thread {
		private boolean enMarcha = true;
		public void parar() {
			enMarcha = false;
			interrupt();
		}
		@Override
		public void run() {
			int conteoDecimas = 0;
			while (enMarcha) {
				conteoDecimas++;
				if (conteoDecimas>=600) {  // 60 sgs
					conteoDecimas = 0;
					cargaFarmacias();  // Carga farmacias de nuevo de internet
					actualizaFarmaciasEnPantalla(); // Actualiza listado de farmacias
				}
				if (conteoDecimas % 10 == 0) {  // 1 sg
					miVentana.actualizarReloj();
				}
				miVentana.mueveFarmaciasAhora();
				try { Thread.sleep( 100L ); } catch (Exception e) { logger.log( Level.INFO, "Hilo de cambio de pantalla interrumpido" ); }  // 1 d�cima
			}
			logger.log( Level.INFO, "Final de hilo de actualizaci�n de pantalla" );
		}
	}
	
}
