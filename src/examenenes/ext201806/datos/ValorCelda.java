package examenenes.ext201806.datos;

import java.awt.Color;
import java.io.Serializable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/** Clase abstracta para el valor de cada celda de miniexcel
 * @author andoni.eguiluz @ ingenieria @ deusto.es
 */
public abstract class ValorCelda implements Serializable {
	private static final long serialVersionUID = 4086719000073707028L;
	
	private static DefaultTableCellRenderer rendererPorDefecto = new DefaultTableCellRenderer();
	/** Devuelve el renderer (visualizador) de la celda
	 * @return	renderer
	 */
	public TableCellRenderer getRenderer() {
		rendererPorDefecto.setBackground( Color.white );
		return rendererPorDefecto;
	}
	
	/** Devuelve el texto de edici�n de la celda (no siempre igual al texto visualizado de la celda)
	 * (por ejemplo en f�rmulas se visualiza el valor pero se edita la f�rmula)
	 * @return	texto de edici�n de la celda
	 */
	public abstract String getTextoEdicion();
	
	/** Actualiza el valor de una celda dado un nuevo texto de edici�n
	 * @param nuevoValor	Texto de nuevo valor de celda 
	 * @return	true si la actualizaci�n es correcta (y se recalcula el valor), false si hay cualquier error
	 */
	public abstract boolean actualizaTexto( String nuevoValor );

	/** Devuelve el valor de un texto editado
	 * @param texto	Texto editado
	 * @param tabla	tabla de datos a la que corresponde el texto editado
	 * @return	valor correspondiente a ese texto de la clase apropiada, null si no es reconocido o es vac�o
	 */
	public static ValorCelda getValorFromEdicion( String texto, TablaDatos tabla ) {
		if (texto==null || texto.isEmpty()) return null;
		ValorCelda ret = Numero.getValorFromEdicion( texto, tabla );
		if (ret!=null) return ret;  // Si es convertible en n�mero
		ret = FormulaSuma.getValorFromEdicion( texto, tabla );
		if (ret!=null) return ret;  // Si es interpretable como f�rmula de suma
		ret = Texto.getValorFromEdicion( texto, tabla );  // Si no, seguro que es texto
		return ret;
	}
}
