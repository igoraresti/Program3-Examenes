package examen.ext201702;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

public class MapaFarmaciasOrdenadas {
	private TreeMap<String,TreeSet<FarmaciaGuardia>> mapaOrd;
	/** Crea un mapa de farmacias ordenadas por localidad, horario, zona y dirección partiendo de un mapa desordenado
	 * @param mapa	Mapa de farmacias por población, desordenado
	 */
	public MapaFarmaciasOrdenadas( MapaFarmacias mapa ) {
		mapaOrd = new TreeMap<>();
		for (String loc : mapa.getMapaFarmacias().keySet()) {
			ArrayList<FarmaciaGuardia> l = mapa.getMapaFarmacias().get( loc );
			TreeSet<FarmaciaGuardia> tree = new TreeSet<FarmaciaGuardia>();
			for (FarmaciaGuardia f : l) {
				tree.add( f );
			}
			mapaOrd.put( loc, tree );
		}
	}
	
	public TreeMap<String, TreeSet<FarmaciaGuardia>> getMapaOrd() {
		return mapaOrd;
	}
	
}
