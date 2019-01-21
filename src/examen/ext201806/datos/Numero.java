package examen.ext201806.datos;

import java.awt.Color;

import javax.swing.*;
import javax.swing.table.*;

public class Numero extends ValorCelda implements ValorNumerico {
	private static final long serialVersionUID = 1L;

	/** Devuelve el valor de un texto editado, si corresponde a esta clase
	 * @param texto	Texto editado
	 * @param tabla	tabla de datos a la que corresponde el texto editado
	 * @return	valor correspondiente a ese texto, null si no es posible como instancia de esta clase
	 */
	public static ValorCelda getValorFromEdicion( String texto, TablaDatos tabla ) {
		Numero ret = new Numero( 0.0 );
		boolean ok = ret.actualizaTexto( texto );
		if (!ok) ret = null; 
		return ret;
	}
	
	private double valor;
	
		private static DefaultTableCellRenderer rendererDerecha = new DefaultTableCellRenderer();
		static {
			rendererDerecha.setHorizontalAlignment( JLabel.RIGHT );
		}
	@Override
	public TableCellRenderer getRenderer() {
		rendererDerecha.setBackground( Color.white );
		return rendererDerecha;
	}

	/** Construye un nuevo número con el valor indicado
	 * @param d	Valor del número
	 */
	public Numero( double d ) {
		valor = d; 		
	}

	@Override
	public boolean actualizaTexto( String nuevoValor ) {
		try {
			valor = Double.parseDouble( nuevoValor );
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@Override
	public String getTextoEdicion() {
		return toString();   // Coincide el texto mostrado con el editado
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
