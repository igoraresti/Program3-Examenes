package examen.ord201901.sesionesCentros;

import static org.junit.Assert.*;

import org.junit.Test;

public class TablaTest {

	private String[][] datosTest1 = {
			{ "1", "1", "1", "1" },
			{ "2", "2", "2", "2" },
			{ "4", "4", "4", "4" },
			{ "6", "6", "6", "6" },
	};
	@Test
	public void test() {
		try { 
			Tabla tabla = Tabla.processCSV( TablaTest.class.getResource( "testTabla1.csv" ).toURI().toURL() );
			assertTrue( tablaOk( tabla, datosTest1 ) );
			tabla = Tabla.processCSV( TablaTest.class.getResource( "testTabla2.csv" ).toURI().toURL() );
			System.out.println( tabla );
			assertTrue( tablaOk( tabla, datosTest1 ) );
		} catch (Exception e) {
			e.printStackTrace();
			fail( "Excepción no esperada" );
		}
	}
	
		private boolean tablaOk( Tabla tabla, String[][] datos ) {
			assertEquals( tabla.size(), datos.length );
			assertEquals( tabla.getWidth(), datos[0].length );
			try {
				for (int fila=0; fila<tabla.size(); fila++) {
					for (int col=0; col<tabla.getWidth(); col++) {
						if (!tabla.get(fila, col).equals( datos[fila][col])) {
							return false;  // Si hay alguno distinto se corta y devuelve falso
						}
					}
				}
				return true;  // Si todos son iguales se devuelve true
			} catch (IndexOutOfBoundsException e) {
				return false; // Si hay cualquier error por acceso incorrecto, no son iguales: false
			}
		}

}
