package examenenes.ord201706;

import java.util.*;
import static examenenes.ord201706.VentanaDiskUseTool.*;
import java.io.File;
import javax.swing.*;
import javax.swing.tree.*;

/** Clase que visualiza el espacio ocupado en disco en sus carpetas,
 * utilizando el componente visual JTree modificado (JTreeExpansible) para mostrar
 * una barra de progreso adem�s de una etiqueta (nombre y espacio de la carpeta)
 * @author andoni.eguiluz @ ingenieria.deusto.es
 */
public class DiskUseTool {

	private static DiskUseTool tool;
	public static void main(String[] args) {
		try {
			Object o = JOptionPane.showInputDialog( null, "Elige directorio a revisar", "Espacio ocupado en disco", JOptionPane.INFORMATION_MESSAGE, 
					null, new String[] { "Test 1", "Test 2", "C:\\", "Elige otro..." }, "Test 1" );
	        String dirInicial = "dataTest";
			if ("Test 1".equals(o)) {
				DiskUseTest.crearArbolDirs( 1 );
			} else if ("Test 2".equals(o)) {
				DiskUseTest.crearArbolDirs( 2 );
			} else if ("C:\\".equals(o)) {
				dirInicial = "C:\\";
			} else {
				JFileChooser fc = new JFileChooser( dirInicial );
				String mens = "Elige carpeta a revisar";
				fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
				int valor = fc.showDialog( null, mens );
				if (valor == JFileChooser.APPROVE_OPTION) {
					dirInicial = fc.getSelectedFile().getPath();
				} else {
					return; // Acabar sin mostrar
				}
			}
			tool = new DiskUseTool( dirInicial );
			// TAREA Observer
			tool.visualizarEspacioEnDiscoConJTree( dirInicial );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Atributo de uso de disco
	private VentanaDiskUseTool vent = null;  // Ventana de UI
	
	/** Constructor de la herramienta de uso de disco
	 * @param tituloOrigen
	 */
	public DiskUseTool( String tituloOrigen ) {
		vent = new VentanaDiskUseTool( tituloOrigen, tituloOrigen );
		vent.setVisible( true );
	}
	
	/** Devuelve la ventana de la herramienta de uso de disco
	 * @return	ventana de la herramienta
	 */
	public VentanaDiskUseTool getVentana() {
		return vent;
	}

	// TAREA Observer
	
	// ---------------------------------
	// Recorrido de ficheros en disco
	// ---------------------------------
	
	
	/** Visualiza el espacio en disco ocupado por cada carpeta del directorio indicado
	 * @param dirIni	Directorios a explorar. Si no es una carpeta correcta el m�todo no hace nada.
	 */
	public void visualizarEspacioEnDiscoConJTree( String... dirIni ) {
		for (String dir : dirIni) {
			File ini = new File( dir );
			if (!ini.exists() || !ini.isDirectory()) return;
			inicioRecorrido();
			ConteoDir parcial = recorridoUnidad( ini, 0, vent.getRaiz() );
			finalRecorrido();
			vent.getRaiz().datos.sumaConteo( parcial );
			vent.getModeloArbol().nodeChanged( vent.getRaiz() );  // Lanza evento de modificación en el modelo
		}
	}

	// TAREA Hilo - atributo y m�todo p�blico
	
	private ConteoDir recorridoUnidad( File ficODir, int nivel, NodoConTamanyo padre ) {
		// TAREA Hilo - parada de recorrido
		if (ficODir==null) return new ConteoDir();  // No hay ficheros, no hay carpetas, tama�o cero bytes
		if (!ficODir.isDirectory()) { // Si es fichero se devuelve tal cual
			procesaFichero( ficODir );
			return new ConteoDir( null, 1, 0, ficODir.length() );  // 1 fichero, no hay carpetas, tama�o bytes del fichero
		}
		// Si es directorio, proceso recursivo
		File[] files = ficODir.listFiles();
		String nom = ficODir.getName();
		if (nom.isEmpty()) nom = ficODir.getAbsolutePath();  // Caso especial para ra�z del disco
		final NodoConTamanyo nodoNuevo = vent.anyadeNodoHijo( nom, new ConteoDir(ficODir, 0, 1, 0L), padre );  // No hay ficheros, una carpeta -a�n sin contenido-, tama�o cero bytes
		if (files==null || files.length==0) return nodoNuevo.datos;  // No hay ficheros, una carpeta -sin contenido-, tama�o cero bytes
		ArrayList<File> al = new ArrayList<>( Arrays.asList( files ) );
		al.sort( new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				String n1 = o1.getName();
				String n2 = o2.getName();
				return n1.compareToIgnoreCase( n2 );
			}
		});
		for (File file : al) {
			ConteoDir parcial = recorridoUnidad( file, nivel+1, nodoNuevo );
			nodoNuevo.datos.sumaConteo( parcial );
			SwingUtilities.invokeLater( new Runnable() {
				@Override
				public void run() {
					vent.getModeloArbol().nodeChanged( nodoNuevo );  // Lanza evento de modificaci�n en el modelo
				}
			});
		}
		nodoNuevo.datos.setNumCarpetas( nodoNuevo.datos.getNumCarpetas() + 1 );  // Actualizar el n�mero de carpetas para retornar (las que haya dentro + la actual)
		SwingUtilities.invokeLater( new Runnable() { // Colapsar la carpeta y as� el �rbol va teniendo un tama�o aceptable en la ventana
			@Override
			public void run() {
				vent.getTree().collapsePath( new TreePath( nodoNuevo.getPath() ) );
			}
		});
		return nodoNuevo.datos;
	}
	
	// ---------------------------------
	// M�todos de secuencia de recorrido de ficheros
	// ---------------------------------
	
	
	// TAREA JC - Atributos necesarios
	// TAREA BD - atributos
	
	// Se ejecuta antes de empezar el recorrido
	private void inicioRecorrido() {
		// TAREA BD
	}
	
	// Se ejecuta con cada fichero del recorrido (no con los directorios)
	private void procesaFichero( File file ) {
		// TAREA Observer
		// TAREA BD
		// TAREA JC
	}
	
	// Se ejecuta despu�s de acabar el recorrido
	private void finalRecorrido() {
		// TAREA BD
		// TAREA JC
	}

	
}
