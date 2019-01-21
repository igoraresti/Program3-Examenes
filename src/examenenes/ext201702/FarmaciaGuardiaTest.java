package examenenes.ext201702;

import static org.junit.Assert.*;
import org.junit.Test;

/** Clase de test unitario de la clase FarmaciaGuardia
 * @author andoni.eguiluz @ ingenieria.deusto.es
 */
public class FarmaciaGuardiaTest {

	@Test
	public void testConstructor3Strings() {
		FarmaciaGuardia f = new FarmaciaGuardia( "Bilbao", "00:00-22:00", "(Deusto) Avda. Universidades 24 | 944139000" );
		FarmaciaGuardia f1 = new FarmaciaGuardia( "Bilbao", "00:01-22:00", "Test" );
		FarmaciaGuardia f2 = new FarmaciaGuardia( "Bilbao", "23:59-22:00", "Test" );
		assertTrue( f.getHoraDesde() < f1.getHoraDesde() );
		assertTrue( f.getHoraDesde() < f2.getHoraDesde() );
		assertEquals( f.getLocalidad(), "Bilbao" );
		assertEquals( f.getZona(), "Deusto" );
		assertEquals( f.getDireccion(), "Avda. Universidades 24" );
		assertEquals( f.getTelefono(), "944139000" );
		FarmaciaGuardia f3 = new FarmaciaGuardia( "Bilbao", "00:00-22:00", "Deusto Avda. Universidades 24 944139000" );
		assertEquals( f3.getZona(), "" );
		assertEquals( f3.getTelefono(), "" );
	}
	
	@Test
	public void testCapicua() {
		FarmaciaGuardia f1 = new FarmaciaGuardia( "Bilbao", "09:00-22:00", "(Deusto) Avda. Universidades 24 | 94 4139000" );
		FarmaciaGuardia f2 = new FarmaciaGuardia( "Bilbao", "09:00-22:00", "(Deusto) Avda. Universidades 24 | 94 413 14 49" );
		FarmaciaGuardia f3 = new FarmaciaGuardia( "Bilbao", "09:00-22:00", "(Deusto) Avda. Universidades 24 | 944 132 439" );
		assertTrue( f1.calcCapicua() > 5 );
		assertTrue( f2.calcCapicua() == 0 );
		assertTrue( f3.calcCapicua() == 2 );
	}

}
