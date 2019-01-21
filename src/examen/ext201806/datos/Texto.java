package examen.ext201806.datos;

public class Texto extends ValorCelda {
	private static final long serialVersionUID = 1L;
	
	/** Devuelve el valor de un texto editado, si corresponde a esta clase
	 * @param texto	Texto editado
	 * @param tabla	tabla de datos a la que corresponde el texto editado
	 * @return	valor correspondiente a ese texto
	 */
	public static ValorCelda getValorFromEdicion( String texto, TablaDatos tabla ) {
		Texto ret = new Texto( texto );
		return ret;
	}
	
	private String valor;

	/** Construye un nuevo texto con el valor indicado
	 * @param d	Valor del texto
	 */
	public Texto( String s ) {
		valor = s; 		
	}

	@Override
	public boolean actualizaTexto( String nuevoValor ) {
		valor = nuevoValor;
		return true;
	}

	@Override
	public String getTextoEdicion() {
		return toString();   // Coincide el texto mostrado con el editado
	}
	
	@Override
	public String toString() {
		return valor;
	}
	
}
