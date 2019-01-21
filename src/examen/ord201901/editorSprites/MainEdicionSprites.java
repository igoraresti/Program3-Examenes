package examen.ord201901.editorSprites;

import javax.swing.UIManager;  // Para usar look and feels distintos al estándar

/** Clase principal de edición de sprites<br/>
 * Enlace a un zip con gráficos para sprites de ejemplo:
 * <a href="https://drive.google.com/file/d/1UhqJT1zh_aYzcCgKa_6eRUdQvnqP8k0v/view?usp=sharing">link a fichero comprimido</a>
 * @author andoni.eguiluz @ ingenieria.deusto.es
 */
public class MainEdicionSprites {

	/** Método principal, crea una ventana de edición y la lanza 
	 * @param args
	 */
	public static void main(String[] args) {
		try { // Cambiamos el look and feel (se tiene que hacer antes de crear la GUI
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) { } // Si Nimbus no está disponible, se usa el l&f por defecto
		VentanaEdicionSprites v = new VentanaEdicionSprites();
		
		// Estas tres líneas inicializan la secuencia con tres gráficos de ejemplos (sustituir los paths por los gráficos que se deseen)
		// Con los ficheros suministrados comprobar que están en la carpeta correspondiente a las imágenes de las carpetas
		v.getController().anyadirSpriteASecuencia( new java.io.File( "src/examen/ord201901/editorSprites/img/Attack__000.png" ) );
		v.getController().anyadirSpriteASecuencia( new java.io.File( "src/examen/ord201901/editorSprites/img/Attack__001.png" ) );
		v.getController().anyadirSpriteASecuencia( new java.io.File( "src/examen/ord201901/editorSprites/img/Attack__002.png" ) );
		v.getController().anyadirSpriteASecuencia( new java.io.File( "src/examen/ord201901/editorSprites/img/Attack__003.png" ) );
		v.getController().anyadirSpriteASecuencia( new java.io.File( "src/examen/ord201901/editorSprites/img/Attack__004.png" ) );
		v.setVisible( true );
		
	}
}