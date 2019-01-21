package examen.ext201702;

import java.util.ArrayList;

import examen.ext201702.gui.*;

// TAREA 6

/** Prueba combinada de distintas estructuras de datos y distintos tamaños
 * @author Andoni Eguíluz Morán
 * Facultad de Ingeniería - Universidad de Deusto
 */
public class PruebaEficienciaMapaFarmacias {

	public static void main(String[] args) {
		String[] pruebas = { "Mapa" };
		ArrayList<ProcesoProbable> procs = new ArrayList<ProcesoProbable>();
		procs.add( new AccesoAFarmaciasMapa() );
		VentanaBancoDePruebas vent = new VentanaBancoDePruebas();
		vent.setProcesos( pruebas, procs );
		vent.setVisible( true );
	}
	
	private static class AccesoAFarmaciasMapa implements ProcesoProbable {

		private MapaFarmacias mapa;
		private int numDeVeces;
		private FarmaciaGuardia farmaciaPrueba1;
		
		// Inicialización:
		// Inicializa las farmacias con la dirección "Prueba " + n en Bilbao y en Barakaldo  (tantas como tamaño tenga el test)
		@Override
		public void init(int tamanyoTest) {
			mapa = new MapaFarmacias();
			ArrayList<FarmaciaGuardia> listaBilbao = new ArrayList<FarmaciaGuardia>();
			ArrayList<FarmaciaGuardia> listaBaraka = new ArrayList<FarmaciaGuardia>();
			mapa.getMapaFarmacias().put( "Bilbao", listaBilbao );
			mapa.getMapaFarmacias().put( "Barakaldo", listaBaraka );
			numDeVeces = tamanyoTest;
			for (int i=0; i<numDeVeces; i++) {
				listaBilbao.add( new FarmaciaGuardia( "Bilbao", "09:00-22:00", "(Albia)  Prueba " + i + "  |  94 4444444" ));
				listaBaraka.add( new FarmaciaGuardia( "Barakaldo", "09:00-22:00", "(San Vicente-Zuazo)  Prueba " + i + "  |  94 4444444" ));
			}
			farmaciaPrueba1 = new FarmaciaGuardia( "Bilbao", "09:00-22:00", "(Albia)  Prueba 1  |  94 4444444" );  // Sí está
		}

		// Test a realizar:
		// busca la farmacia de prueba, y busca el número indicado de farmacias con dirección falsa
		// (construido como "Falsa 1", "Falsa 2", etc.)
		@Override
		public Object test() {  
			ArrayList<FarmaciaGuardia> lista = mapa.getMapaFarmacias().get( "Bilbao" );
			boolean esta1 = lista.contains( farmaciaPrueba1 );
			boolean esta2 = false;
			FarmaciaGuardia farmaciaPrueba2 = new FarmaciaGuardia( "Bilbao", "09:00-22:00", "(Albia)  Buenos Aires, 13  |  94 4444444" );  // No está
			for (int i=0; i<numDeVeces; i++) {
				farmaciaPrueba2.setDireccion( "Falsa " + i );
				esta2 = esta2 || lista.contains( farmaciaPrueba2 );
			}
			System.out.println( esta1 && esta2 );
			return mapa.getMapaFarmacias();
		}

	}
	
}