package examen.parc201805.datos;

/** Clase para generar el deporte de fútbol (incluíble en liga)
 */
public class Futbol extends Deporte {
	
	private int golesAFavor = 0;   // Atributo particular del fútbol al calcular cada equipo
	private int golesEnContra = 0; // Atributo particular del fútbol al calcular cada equipo
	
	/** Crea el deporte fútbol, con 0 goles a favor y 0 goles en contra
	 */
	public Futbol() {
		nombre = "Fútbol";
	}

	@Override
	public int getPuntos1(Partido partido) {
		if (partido.getMarcador1()>partido.getMarcador2()) return 3; // 3 puntos por victoria
		else if (partido.getMarcador1()<partido.getMarcador2()) return 0; // 0 puntos por derrota
		else return 1; // 1 punto por empate
	}

	@Override
	public int getPuntos2(Partido partido) {
		if (partido.getMarcador1()>partido.getMarcador2()) return 0; // 0 puntos por derrota
		else if (partido.getMarcador1()<partido.getMarcador2()) return 3; // 3 puntos por victoria
		else return 1; // 1 punto por empate
	}
	
	@Override
	public void actualiza( Partido partido, Equipo equipo ) {
		System.out.println( "Actualizando " + partido + " - " + equipo + " - deporte " + this );
		if (partido.getEquipo1()==equipo) {
			setGolesAFavor( getGolesAFavor() + partido.getMarcador1() );
			setGolesEnContra( getGolesEnContra() + partido.getMarcador2() );
		} else if (partido.getEquipo2()==equipo) {
			setGolesAFavor( getGolesAFavor() + partido.getMarcador2() );
			setGolesEnContra( getGolesEnContra() + partido.getMarcador1() );
		}
		System.out.println( "Deporte actualizado: " + this );
	}

	@Override
	public void reset() {
		golesAFavor = 0; golesEnContra = 0;
	}

	@Override
	public int comparaEquipos( Equipo equipo1, Equipo equipo2 ) {
		int dif = equipo2.getPuntos() - equipo1.getPuntos();   // Primer criterio: el que más puntos tiene
		if (dif==0) {
			int difGoles1 = ((Futbol) equipo1.getDeporte()).getGolesAFavor() - ((Futbol) equipo1.getDeporte()).getGolesEnContra();
			int difGoles2 = ((Futbol) equipo2.getDeporte()).getGolesAFavor() - ((Futbol) equipo2.getDeporte()).getGolesEnContra();
			dif = difGoles2 - difGoles1;  // Segundo criterio (a igualdad de puntos): el que mejor diferencia de goles a favor y en contra tiene
		}
		return dif;
	}

	@Override
	public Deporte duplicaDeporte() {
		return new Futbol();
	}

	/** Devuelve los goles a favor en fútbol de la entidad correspondiente
	 * @return	goles a favor 
	 */
	public int getGolesAFavor() {
		return golesAFavor;
	}

	/** Modifica los goles a favor en fútbol de la entidad correspondiente
	 * @param golesEnContra	goles a favor
	 */
	public void setGolesAFavor(int golesAFavor) {
		this.golesAFavor = golesAFavor;
	}

	/** Devuelve los goles en contra en fútbol de la entidad correspondiente
	 * @return	goles en contra
	 */
	public int getGolesEnContra() {
		return golesEnContra;
	}

	/** Modifica los goles en contra en fútbol de la entidad correspondiente
	 * @param golesEnContra	goles en contra
	 */
	public void setGolesEnContra(int golesEnContra) {
		this.golesEnContra = golesEnContra;
	}
	
	@Override
	public String toString() {
		return "GF=" + golesAFavor + " " + "GC=" + golesEnContra;
	}
	
}
