package examenenes.ord201706;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.text.ParseException;

/** Clase para crear ficheros de prueba en el disco
 * todos los ficheros y carpetas tienen que contener la palabra "test" al inicio o "Test" al final
 * @author andoni.eguiluz @ ingenieria.deusto.es
 */
public class DiskUseTest {

	protected static Dir arbolDirs1;
	protected static Dir arbolDirs2;
	static {
		try {
			arbolDirs1 = new Dir( "dataTest", Utils.sdf.parse("01/05/2017"),
				new Fich( "testfic1.txt", Utils.sdf.parse("01/05/2017"), 1044 ),
				new Dir( "testdir1", Utils.sdf.parse("01/05/2017"),
						new Fich( "testa.dat", Utils.sdf.parse("01/05/2017"), 5453010 ),
						new Fich( "testb.dat", Utils.sdf.parse("01/05/2017"), 2048 ) ),
				new Dir( "testdir2", Utils.sdf.parse("01/05/2017"),
						new Fich( "testa.txt", Utils.sdf.parse("01/05/2017"), 527 ),
						new Fich( "testb.txt", Utils.sdf.parse("01/05/2017"), 527 ),
						new Fich( "testc.txt", Utils.sdf.parse("01/05/2017"), 2048 ) )
				);
			arbolDirs2 = new Dir( "dataTest", Utils.sdf.parse("01/05/2017"),
				new Fich( "testfic1.txt", Utils.sdf.parse("01/05/2017"), 1044 ),
				new Dir( "testdir1", Utils.sdf.parse("01/05/2017"),
						new Fich( "testa.dat", Utils.sdf.parse("01/05/2017"), 5453010 ),
						new Fich( "testa.txt", Utils.sdf.parse("01/05/2017"), 527 ), // Fichero que es igual a otros de los directorios dir2 y dir3
						new Fich( "testb.dat", Utils.sdf.parse("01/05/2017"), 2048 ) ),
				new Dir( "testdir2", Utils.sdf.parse("01/05/2017"),
						new Fich( "testa.txt", Utils.sdf.parse("01/05/2017"), 527 ), // Fichero nuevo que es igual a otros de los directorios dir3 y dir1
						new Fich( "testb.txt", Utils.sdf.parse("02/05/2017"), 824 ),  // Fichero modificado
						new Fich( "testc.txt", Utils.sdf.parse("01/05/2017"), 2048 ),
						new Fich( "testa.dat", Utils.sdf.parse("01/05/2017"), 5453010 ), // Fichero nuevo que es igual a otro del directorio dir1
						new Fich( "testd.txt", Utils.sdf.parse("02/05/2017"), 1024 ) ),  // Fichero nuevo
				new Dir( "testdir3", Utils.sdf.parse("01/05/2017"),  // Directorio nuevo
						new Fich( "testa.txt", Utils.sdf.parse("01/05/2017"), 527 ), // Fichero nuevo que es igual a otros de los directorios dir2 y dir1
						new Fich( "testa.new", Utils.sdf.parse("01/05/2017"), 8000000 ) )  // Fichero nuevo
				);
		} catch (ParseException e) { e.printStackTrace(); }
	};
	
	/** Crea una estructura de carpetas y ficheros de prueba en el directorio actual
	 * @param numTest	1 para el test de ficheros 1, 2 para el test 2
	 */
	public static void crearArbolDirs( int numTest ) {
		Dir aCrear;
		if (numTest==1) aCrear = arbolDirs1; else aCrear = arbolDirs2;
		File dirPrincipal = new File( aCrear.nombre );
		borrarDirOFic( dirPrincipal ); // Primero borramos lo que hubiera
		crearDir( aCrear, null ); // Y luego creamos la estructura
	}
		// Borra directorio completo (solo si todos los archivos/ficheros tienen prefijo "test" o sufijo "Test"
		private static void borrarDirOFic(File file) {
			if (!file.getName().startsWith("test") && !file.getName().endsWith("Test")) return;  // Si no empieza o acaba por test, finaliza el proceso 
			if (file.exists() && file.isDirectory()) {
				File[] contents = file.listFiles();
		        for (File f : contents) {
		            borrarDirOFic(f);
		        }
		    }
			file.delete();
		}
		private static void crearDir( Dir dir, File raiz ) {
			if (raiz == null) {
				raiz = new File( dir.nombre );
			} else {
				raiz = new File( raiz, dir.nombre );
			}
			try {
				Files.createDirectory( raiz.toPath() );
				for (Fich fic : dir.fichs) {
					if (fic instanceof Dir) { // Recursivamente crea el directorio
						crearDir( (Dir)fic, raiz );
					} else { // Crea el fichero con bytes 0
						File file = new File( raiz, fic.nombre );
						// file.createNewFile();
						FileOutputStream fos = new FileOutputStream( file );
						byte[] bytes = new byte[ (int) (fic.tamanyo) ];
						fos.write( bytes );
						fos.close();
						file.setLastModified( fic.fecha.getTime() );
					}
				}
				raiz.setLastModified( dir.fecha.getTime() );  // Hay que hacerlo despu�s de meter los datos dentro de la carpeta, porque si no el SO lo modifica
				// Con nio se pueden cambiar el resto de atributos:
				Files.setAttribute( raiz.toPath(), "creationTime", FileTime.fromMillis(dir.fecha.getTime()));
				Files.setAttribute( raiz.toPath(), "lastAccessTime", FileTime.fromMillis(dir.fecha.getTime()));
				// Files.setAttribute( raiz.toPath(), "lastModifiedTime", FileTime.fromMillis(dir.fecha.getTime())); // Esto es igual que el setLastModified de File
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	
}
