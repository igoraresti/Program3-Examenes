package examen.ext201806.datos;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

public class FormulaSuma extends ValorCelda implements ValorNumerico {
	private static final long serialVersionUID = 1L;
	
	/** Devuelve el valor de un texto editado, si corresponde a esta clase
	 * @param texto	Texto editado
	 * @param tabla	tabla de datos a la que corresponde el texto editado
	 * @return	valor correspondiente a ese texto, null si no es posible como instancia de esta clase
	 */
	public static ValorCelda getValorFromEdicion( String texto, TablaDatos tabla ) {
		FormulaSuma ret = new FormulaSuma( null, null, tabla );
		boolean ok = ret.actualizaTexto( texto );
		if (!ok) ret = null; 
		return ret;
	}
	
	private RefCelda refCeldaInicio;
	private RefCelda refCeldaFin;
	private double valor;
	private TablaDatos tablaDatos;
	
		private static DefaultTableCellRenderer rendererDerecha = new DefaultTableCellRenderer();
		static {
			rendererDerecha.setHorizontalAlignment( JLabel.RIGHT );
			rendererDerecha.setForeground( Color.BLUE );
		}
	@Override
	public TableCellRenderer getRenderer() {
		return rendererDerecha;
	}

	/** Construye una nueva fórmula de suma con el rango indicado
	 * @param celdaIni	Referencia a celda de inicio
	 * @param celdaFin	Referencia a celda de fin
	 * @param datos	Tabla de datos
	 */
	public FormulaSuma( RefCelda celdaIni, RefCelda celdaFin, TablaDatos datos ) {
		refCeldaInicio = celdaIni;
		refCeldaFin = celdaFin;
		tablaDatos = datos;
		recalculaValor();
	}
	
	/** Recalcula el valor de la fórmula
	 */
	public void recalculaValor() {
		if (refCeldaInicio==null || refCeldaFin==null) return;
		int fila1 = refCeldaInicio.getFila();
		int fila2 = refCeldaFin.getFila();
		int col1 = refCeldaInicio.getColumna();
		int col2 = refCeldaFin.getColumna();
		double suma = 0;
		for (int fila=fila1; fila<=fila2; fila++) {
			for (int col=col1; col<=col2; col++) {
				if (tablaDatos.get( fila, col ) instanceof ValorNumerico) {
					suma += ((ValorNumerico) tablaDatos.get(fila,col)).getValor();
				}
			}
		}
		valor = suma;
	}

	/** Devuelve el texto representativo de la fórmula de suma
	 * Formato "=SUMA(refCeldaInicio:refCeldaFin)"
	 * @return	texto representativo
	 */
	@Override
	public String getTextoEdicion() {
		return "=SUMA(" + refCeldaInicio + ":" + refCeldaFin + ")";
	}
	
	
	/** Actualiza la fórmula de suma partiendo de un texto de fórmula dado
	 * El texto debe tener el formato "=SUMA(refCeldaInicio:refCeldaFin)"
	 * @param nuevaFormula	Texto de nueva fórmula SUMA 
	 * @return	true si la actualización es correcta (y se recalcula el valor), false si hay cualquier error
	 */
	public boolean actualizaTexto( String nuevaFormula ) {
		if (nuevaFormula.length()<7 || !"=SUMA(".equalsIgnoreCase(nuevaFormula.substring(0,6))) return false;
		if (!nuevaFormula.endsWith(")")) return false;
		nuevaFormula = nuevaFormula.substring(6, nuevaFormula.length()-1);
		int posDosPuntos = nuevaFormula.indexOf(':');
		if (posDosPuntos == -1) return false;
		String inicio = nuevaFormula.substring(0, posDosPuntos);
		String fin = nuevaFormula.substring(posDosPuntos+1);
		try {
			RefCelda rc1 = new RefCelda( inicio );
			RefCelda rc2 = new RefCelda( fin );
			refCeldaInicio = rc1;
			refCeldaFin = rc2;
			recalculaValor();
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@Override
	public String toString() {
		return ""+valor;
	}

	@Override
	public double getValor() {
		return valor;
	}
	
}
