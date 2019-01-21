package examenenes.ext201806.datos;

import java.io.Serializable;

public class RefCelda implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int fila;    // Fila de la celda referenciada
	private int columna; // Columna de la celda referenciada
	
	/** Construye la referencia a una celda dada
	 * @param f	N�mero de fila de la celda (0 a n-1)
	 * @param c	N�mero de columna de la celda (0 a n-1)
	 */
	public RefCelda( int f, int c ) {
		fila = f;
		columna = c;
	}
	
		private static int codigoDeA = (int) 'A';
		private static int numLetras = (int) 'Z' - codigoDeA + 1;
	/** Construye la referencia a una celda dada en formato texto. Si la referencia es inv�lida
	 * lanza una excepci�n
	 * @param refTexto	Referencia en formato letra + n�mero (por ejemplo "A1", "C3", "AF95"...)
	 * 	La letra hace referencia a la columna (A=0, B=1, etc.) y el n�mero a la columna (1=0, 2=1...) 
	 * @throws NumberFormatException	Si la referencia es inv�lida
	 */
	public RefCelda( String refTexto ) throws NumberFormatException {
		int posNumero = 0;
		while (posNumero < refTexto.length() && (refTexto.charAt(posNumero)<'0' || refTexto.charAt(posNumero)>'9')) {
			posNumero++;
		}
		try {
			String refCol = refTexto.substring( 0, posNumero );
			String refFil = refTexto.substring( posNumero );
			columna = -1;
			do {
				char letra = refCol.charAt(0);
				if (letra>='a' && letra<='z') {
					columna = ((columna+1) * numLetras) + (((int)letra) - ((int)'a'));
				} else if (letra>='A' && letra<='Z') {
					columna = ((columna+1) * numLetras) + (((int)letra) - ((int)'A'));
				} else {
					throw new Exception();  // Error en construcci�n
				}
				refCol = refCol.substring(1);
			} while (refCol.length()>0);
			fila = Integer.parseInt(refFil) - 1;
		} catch (Exception e) {
			throw new NumberFormatException( "Referencia de celda inv�lida: " + refTexto );
		}
	}
	
	/** Devuelve la fila de la referencia
	 * @return	fila (de 0 a n-1)
	 */
	public int getFila() {
		return fila;
	}

	/** Devuelve la columna de la referencia
	 * @return	columna (de 0 a n-1)
	 */
	public int getColumna() {
		return columna;
	}

	/** Devuelve el nombre en letra de la columna indicada en n�mero, "" si hay cualquier error
	 * @param col	N�mero de la columna
	 * @return	Nombre de la columna: "A" para la 0, "B" para la 1 ... "Z", "AA", "AB", ... "AZ", "BA"...
	 */
	static public String getNomColumna( int col ) {
		if (col<0) return "";
		if (col>=(numLetras+1)*numLetras) return "";
		int letra1 = col % numLetras;
		int letra2 = col / numLetras;
		char cLetra1 = (char) (letra1 + codigoDeA);
		if (letra2==0)
			return "" + cLetra1;
		else
			return "" + ((char)(letra2+codigoDeA-1)) + cLetra1;
	}
	
	@Override
	public String toString() {
		return getNomColumna(columna) + (fila+1);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RefCelda) {
			RefCelda r2 = (RefCelda) obj;
			return r2.columna==columna && r2.fila==fila;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return fila*17+columna;  // Devuelve un hash para poder utilizar RefCelda en tablas hash
	}
	
}
