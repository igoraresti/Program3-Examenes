package examen.parc201805.datos;

import java.util.*;

/** Clase para crear ligas deportivas
 */
public class Liga {

	private Deporte deporte;  // Deporte de la liga
	private String nombre;    // Nombre de la liga
	private HashMap<String,Equipo> equipos;          // Equipos de la liga
	private ArrayList<ArrayList<Partido>> partidos;  // Partidos de la liga (organizados por jornadas)
	
	/** Crea una liga vacía (sin equipos, sin partidos) de un deporte dado
	 * @param nombre	Nombre de la liga
	 * @param deporte	Deporte de la liga
	 */
	public Liga( String nombre, Deporte deporte ) {
		this.nombre = nombre;
		this.deporte = deporte;
		equipos = new HashMap<>();
		partidos = new ArrayList<>();
	}
	
	/** Devuelve el conjunto de códigos de equipo de la liga
	 * @return	Conjunto de códigos de equipo
	 */
	public Set<String> getEquipos() {
		return equipos.keySet();
	}
	
	/** Devuelve el deporte correspondiente a la liga
	 * @return	
	 */
	public Deporte getDeporte() {
		return deporte;
	}

	/** Devuelve el nombre de la liga
	 * @return	nombre asignado a la liga
	 */
	public String getNombre() {
		return nombre;
	}

	/** Modifica el nombre de la liga
	 * @param nombre	nuevo nombre asignado a la liga
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/** Añade un equipo a la liga (si existe, no lo añade)
	 * @param equipo	Nuevo equipo a añadir
	 */
	public void anyadeEquipo( Equipo equipo ) {
		equipos.put( equipo.getCodigo(), equipo );
	}
	
	/** Busca un equipo en la liga
	 * @param codEquipo	Código de equipo a buscar
	 * @return	Equipo con ese código, null si no existe
	 */
	public Equipo buscaEquipo( String codEquipo ) {
		return equipos.get( codEquipo );
	}
	
	/** Añade un partido a la liga
	 * @param partido	Nuevo partido a añadir
	 */
	public void anyadePartido( Partido partido ) {
		while (partidos.size()<partido.getJornada())  // Añade jornadas si no existen
			partidos.add( new ArrayList<Partido>() );
		partidos.get(partido.jornada-1).add( partido );  // Añade el partido de la jornada j (1 a N) en la posición j-1 (0 a N-1) 
	}
	
	/** Inicializa una liga de ejemplo de fútbol
	 * @return	liga de ejemplo
	 */
	public static Liga initEjemplo() {
		Liga liga = new Liga( "Liga BBVA 2015-16", new Futbol() );
		
		liga.anyadeEquipo( new Equipo( "Athletic", "Athletic Bilbao", new Futbol() ) );
		liga.anyadeEquipo( new Equipo( "Atlético", "Atlético de Madrid", new Futbol() ) );
		liga.anyadeEquipo( new Equipo( "Barcelona", "FC Barcelona", new Futbol() ) );
		liga.anyadeEquipo( new Equipo( "Betis", "RCD Betis", new Futbol() ) );
		liga.anyadeEquipo( new Equipo( "Celta", "RCD Celta", new Futbol() ) );
		liga.anyadeEquipo( new Equipo( "Deportivo", "Deportivo de la Coruña", new Futbol() ) );
		liga.anyadeEquipo( new Equipo( "Eibar", "SD Eibar", new Futbol() ) );
		liga.anyadeEquipo( new Equipo( "Espanyol", "RCD Espanyol", new Futbol() ) );
		liga.anyadeEquipo( new Equipo( "Getafe", "RCD Getafe", new Futbol() ) );
		liga.anyadeEquipo( new Equipo( "Granada", "FC Granada", new Futbol() ) );
		liga.anyadeEquipo( new Equipo( "LasPalmas", "UD Las Palmas", new Futbol() ) );
		liga.anyadeEquipo( new Equipo( "Levante", "UD Levante", new Futbol() ) );
		liga.anyadeEquipo( new Equipo( "Málaga", "CF Málaga", new Futbol() ) );
		liga.anyadeEquipo( new Equipo( "R.Madrid", "Real Madrid", new Futbol() ) );
		liga.anyadeEquipo( new Equipo( "R.Sociedad", "Real Sociedad", new Futbol() ) );
		liga.anyadeEquipo( new Equipo( "Rayo", "Rayo Vallecano", new Futbol() ) );
		liga.anyadeEquipo( new Equipo( "Sevilla", "Sevilla FC", new Futbol() ) );
		liga.anyadeEquipo( new Equipo( "Sporting", "Sporting de Gijón", new Futbol() ) );
		liga.anyadeEquipo( new Equipo( "Valencia", "Valencia FC", new Futbol() ) );
		liga.anyadeEquipo( new Equipo( "Villarreal", "Villarreal FC", new Futbol() ) );
		
		liga.anyadePartido( new Partido( 1, liga.buscaEquipo("Granada"), liga.buscaEquipo("Eibar"), 1, 3 ) );
		liga.anyadePartido( new Partido( 1, liga.buscaEquipo("Betis"), liga.buscaEquipo("Villarreal"), 1, 1 ) );
		liga.anyadePartido( new Partido( 1, liga.buscaEquipo("Levante"), liga.buscaEquipo("Celta"), 1, 2 ) );
		liga.anyadePartido( new Partido( 1, liga.buscaEquipo("Sporting"), liga.buscaEquipo("R.Madrid"), 0, 0 ) );
		liga.anyadePartido( new Partido( 1, liga.buscaEquipo("Athletic"), liga.buscaEquipo("Barcelona"), 0, 1 ) );
		liga.anyadePartido( new Partido( 1, liga.buscaEquipo("Rayo"), liga.buscaEquipo("Valencia"), 0, 0 ) );
		liga.anyadePartido( new Partido( 1, liga.buscaEquipo("Atlético"), liga.buscaEquipo("LasPalmas"), 1, 0 ) );
		liga.anyadePartido( new Partido( 1, liga.buscaEquipo("Espanyol"), liga.buscaEquipo("Getafe"), 1, 0 ) );
		liga.anyadePartido( new Partido( 1, liga.buscaEquipo("Deportivo"), liga.buscaEquipo("R.Sociedad"), 0, 0 ) );
		liga.anyadePartido( new Partido( 1, liga.buscaEquipo("Málaga"), liga.buscaEquipo("Sevilla"), 0, 0 ) );

		liga.anyadePartido( new Partido( 2, liga.buscaEquipo("Valencia"), liga.buscaEquipo("Deportivo"), 1, 1 ) );
		liga.anyadePartido( new Partido( 2, liga.buscaEquipo("Sevilla"), liga.buscaEquipo("Atlético"), 0, 3 ) );
		liga.anyadePartido( new Partido( 2, liga.buscaEquipo("Getafe"), liga.buscaEquipo("Granada"), 1, 2 ) );
		liga.anyadePartido( new Partido( 2, liga.buscaEquipo("LasPalmas"), liga.buscaEquipo("Levante"), 0, 0 ) );
		liga.anyadePartido( new Partido( 2, liga.buscaEquipo("Villarreal"), liga.buscaEquipo("Espanyol"), 3, 1 ) );
		liga.anyadePartido( new Partido( 2, liga.buscaEquipo("R.Sociedad"), liga.buscaEquipo("Sporting"), 0, 0 ) );
		liga.anyadePartido( new Partido( 2, liga.buscaEquipo("Barcelona"), liga.buscaEquipo("Málaga"), 1, 0 ) );
		liga.anyadePartido( new Partido( 2, liga.buscaEquipo("Celta"), liga.buscaEquipo("Rayo"), 3, 0 ) );
		liga.anyadePartido( new Partido( 2, liga.buscaEquipo("R.Madrid"), liga.buscaEquipo("Betis"), 5, 0 ) );
		liga.anyadePartido( new Partido( 2, liga.buscaEquipo("Eibar"), liga.buscaEquipo("Athletic"), 2, 0 ) );

		liga.anyadePartido( new Partido( 3, liga.buscaEquipo("Rayo"), liga.buscaEquipo("Deportivo"), 1, 3 ) );
		liga.anyadePartido( new Partido( 3, liga.buscaEquipo("Málaga"), liga.buscaEquipo("Eibar"), 0, 0 ) );
		liga.anyadePartido( new Partido( 3, liga.buscaEquipo("Celta"), liga.buscaEquipo("LasPalmas"), 3, 3 ) );
		liga.anyadePartido( new Partido( 3, liga.buscaEquipo("Athletic"), liga.buscaEquipo("Getafe"), 3, 1 ) );
		liga.anyadePartido( new Partido( 3, liga.buscaEquipo("Granada"), liga.buscaEquipo("Villarreal"), 1, 3 ) );
		liga.anyadePartido( new Partido( 3, liga.buscaEquipo("Betis"), liga.buscaEquipo("R.Sociedad"), 1, 0 ) );
		liga.anyadePartido( new Partido( 3, liga.buscaEquipo("Atlético"), liga.buscaEquipo("Barcelona"), 1, 2 ) );
		liga.anyadePartido( new Partido( 3, liga.buscaEquipo("Sporting"), liga.buscaEquipo("Valencia"), 0, 1 ) );
		liga.anyadePartido( new Partido( 3, liga.buscaEquipo("Espanyol"), liga.buscaEquipo("R.Madrid"), 0, 6 ) );
		liga.anyadePartido( new Partido( 3, liga.buscaEquipo("Levante"), liga.buscaEquipo("Sevilla"), 1, 1 ) );

		liga.anyadePartido( new Partido( 4, liga.buscaEquipo("Getafe"), liga.buscaEquipo("Málaga"), 1, 0 ) );
		liga.anyadePartido( new Partido( 4, liga.buscaEquipo("R.Madrid"), liga.buscaEquipo("Granada"), 1, 0 ) );
		liga.anyadePartido( new Partido( 4, liga.buscaEquipo("Valencia"), liga.buscaEquipo("Betis"), 0, 0 ) );
		liga.anyadePartido( new Partido( 4, liga.buscaEquipo("Eibar"), liga.buscaEquipo("Atlético"), 0, 2 ) );
		liga.anyadePartido( new Partido( 4, liga.buscaEquipo("R.Sociedad"), liga.buscaEquipo("Espanyol"), 2, 3 ) );
		liga.anyadePartido( new Partido( 4, liga.buscaEquipo("Sevilla"), liga.buscaEquipo("Celta"), 1, 2 ) );
		liga.anyadePartido( new Partido( 4, liga.buscaEquipo("Deportivo"), liga.buscaEquipo("Sporting"), 2, 3 ) );
		liga.anyadePartido( new Partido( 4, liga.buscaEquipo("Villarreal"), liga.buscaEquipo("Athletic"), 3, 1 ) );
		liga.anyadePartido( new Partido( 4, liga.buscaEquipo("Barcelona"), liga.buscaEquipo("Levante"), 4, 1 ) );
		liga.anyadePartido( new Partido( 4, liga.buscaEquipo("LasPalmas"), liga.buscaEquipo("Rayo"), 0, 1 ) );

		liga.anyadePartido( new Partido( 5, liga.buscaEquipo("Betis"), liga.buscaEquipo("Deportivo"), 1, 2 ) );
		liga.anyadePartido( new Partido( 5, liga.buscaEquipo("Málaga"), liga.buscaEquipo("Villarreal"), 0, 1 ) );
		liga.anyadePartido( new Partido( 5, liga.buscaEquipo("LasPalmas"), liga.buscaEquipo("Sevilla"), 2, 0 ) );
		liga.anyadePartido( new Partido( 5, liga.buscaEquipo("Athletic"), liga.buscaEquipo("R.Madrid"), 1, 2 ) );
		liga.anyadePartido( new Partido( 5, liga.buscaEquipo("Rayo"), liga.buscaEquipo("Sporting"), 2, 1 ) );
		liga.anyadePartido( new Partido( 5, liga.buscaEquipo("Levante"), liga.buscaEquipo("Eibar"), 2, 2 ) );
		liga.anyadePartido( new Partido( 5, liga.buscaEquipo("Celta"), liga.buscaEquipo("Barcelona"), 4, 1 ) );
		liga.anyadePartido( new Partido( 5, liga.buscaEquipo("Granada"), liga.buscaEquipo("R.Sociedad"), 0, 3 ) );
		liga.anyadePartido( new Partido( 5, liga.buscaEquipo("Espanyol"), liga.buscaEquipo("Valencia"), 1, 0 ) );
		liga.anyadePartido( new Partido( 5, liga.buscaEquipo("Atlético"), liga.buscaEquipo("Getafe"), 2, 0 ) );

		liga.anyadePartido( new Partido( 6, liga.buscaEquipo("Sporting"), liga.buscaEquipo("Betis"), 1, 2 ) );
		liga.anyadePartido( new Partido( 6, liga.buscaEquipo("Deportivo"), liga.buscaEquipo("Espanyol"), 3, 0 ) );
		liga.anyadePartido( new Partido( 6, liga.buscaEquipo("Getafe"), liga.buscaEquipo("Levante"), 3, 0 ) );
		liga.anyadePartido( new Partido( 6, liga.buscaEquipo("R.Sociedad"), liga.buscaEquipo("Athletic"), 0, 0 ) );
		liga.anyadePartido( new Partido( 6, liga.buscaEquipo("Valencia"), liga.buscaEquipo("Granada"), 1, 0 ) );
		liga.anyadePartido( new Partido( 6, liga.buscaEquipo("Barcelona"), liga.buscaEquipo("LasPalmas"), 2, 1 ) );
		liga.anyadePartido( new Partido( 6, liga.buscaEquipo("R.Madrid"), liga.buscaEquipo("Málaga"), 0, 0 ) );
		liga.anyadePartido( new Partido( 6, liga.buscaEquipo("Villarreal"), liga.buscaEquipo("Atlético"), 1, 0 ) );
		liga.anyadePartido( new Partido( 6, liga.buscaEquipo("Sevilla"), liga.buscaEquipo("Rayo"), 3, 2 ) );
		liga.anyadePartido( new Partido( 6, liga.buscaEquipo("Eibar"), liga.buscaEquipo("Celta"), 1, 1 ) );

		liga.anyadePartido( new Partido( 7, liga.buscaEquipo("Celta"), liga.buscaEquipo("Getafe"), 0, 0 ) );
		liga.anyadePartido( new Partido( 7, liga.buscaEquipo("Sevilla"), liga.buscaEquipo("Barcelona"), 2, 1 ) );
		liga.anyadePartido( new Partido( 7, liga.buscaEquipo("Granada"), liga.buscaEquipo("Deportivo"), 1, 1 ) );
		liga.anyadePartido( new Partido( 7, liga.buscaEquipo("Espanyol"), liga.buscaEquipo("Sporting"), 1, 2 ) );
		liga.anyadePartido( new Partido( 7, liga.buscaEquipo("LasPalmas"), liga.buscaEquipo("Eibar"), 0, 2 ) );
		liga.anyadePartido( new Partido( 7, liga.buscaEquipo("Málaga"), liga.buscaEquipo("R.Sociedad"), 3, 1 ) );
		liga.anyadePartido( new Partido( 7, liga.buscaEquipo("Rayo"), liga.buscaEquipo("Betis"), 0, 2 ) );
		liga.anyadePartido( new Partido( 7, liga.buscaEquipo("Athletic"), liga.buscaEquipo("Valencia"), 3, 1 ) );
		liga.anyadePartido( new Partido( 7, liga.buscaEquipo("Levante"), liga.buscaEquipo("Villarreal"), 1, 0 ) );
		liga.anyadePartido( new Partido( 7, liga.buscaEquipo("Atlético"), liga.buscaEquipo("R.Madrid"), 1, 1 ) );

		liga.anyadePartido( new Partido( 8, liga.buscaEquipo("R.Madrid"), liga.buscaEquipo("Levante"), 3, 0 ) );
		liga.anyadePartido( new Partido( 8, liga.buscaEquipo("Eibar"), liga.buscaEquipo("Sevilla"), 1, 1 ) );
		liga.anyadePartido( new Partido( 8, liga.buscaEquipo("Barcelona"), liga.buscaEquipo("Rayo"), 5, 2 ) );
		liga.anyadePartido( new Partido( 8, liga.buscaEquipo("Valencia"), liga.buscaEquipo("Málaga"), 3, 0 ) );
		liga.anyadePartido( new Partido( 8, liga.buscaEquipo("Betis"), liga.buscaEquipo("Espanyol"), 1, 3 ) );
		liga.anyadePartido( new Partido( 8, liga.buscaEquipo("Villarreal"), liga.buscaEquipo("Celta"), 1, 2 ) );
		liga.anyadePartido( new Partido( 8, liga.buscaEquipo("R.Sociedad"), liga.buscaEquipo("Atlético"), 0, 2 ) );
		liga.anyadePartido( new Partido( 8, liga.buscaEquipo("Getafe"), liga.buscaEquipo("LasPalmas"), 4, 0 ) );
		liga.anyadePartido( new Partido( 8, liga.buscaEquipo("Deportivo"), liga.buscaEquipo("Athletic"), 2, 2 ) );
		liga.anyadePartido( new Partido( 8, liga.buscaEquipo("Sporting"), liga.buscaEquipo("Granada"), 3, 3 ) );

		liga.anyadePartido( new Partido( 9, liga.buscaEquipo("Athletic"), liga.buscaEquipo("Sporting"), 3, 0 ) );
		liga.anyadePartido( new Partido( 9, liga.buscaEquipo("Atlético"), liga.buscaEquipo("Valencia"), 2, 1 ) );
		liga.anyadePartido( new Partido( 9, liga.buscaEquipo("Barcelona"), liga.buscaEquipo("Eibar"), 3, 1 ) );
		liga.anyadePartido( new Partido( 9, liga.buscaEquipo("LasPalmas"), liga.buscaEquipo("Villarreal"), 0, 0 ) );
		liga.anyadePartido( new Partido( 9, liga.buscaEquipo("Levante"), liga.buscaEquipo("R.Sociedad"), 0, 4 ) );
		liga.anyadePartido( new Partido( 9, liga.buscaEquipo("Málaga"), liga.buscaEquipo("Deportivo"), 2, 0 ) );
		liga.anyadePartido( new Partido( 9, liga.buscaEquipo("Sevilla"), liga.buscaEquipo("Getafe"), 5, 0 ) );
		liga.anyadePartido( new Partido( 9, liga.buscaEquipo("Granada"), liga.buscaEquipo("Betis"), 1, 1 ) );
		liga.anyadePartido( new Partido( 9, liga.buscaEquipo("Celta"), liga.buscaEquipo("R.Madrid"), 1, 3 ) );
		liga.anyadePartido( new Partido( 9, liga.buscaEquipo("Rayo"), liga.buscaEquipo("Espanyol"), 3, 0 ) );

		liga.anyadePartido( new Partido( 10, liga.buscaEquipo("Eibar"), liga.buscaEquipo("Rayo"), 1, 0 ) );
		liga.anyadePartido( new Partido( 10, liga.buscaEquipo("Espanyol"), liga.buscaEquipo("Granada"), 1, 1 ) );
		liga.anyadePartido( new Partido( 10, liga.buscaEquipo("Sporting"), liga.buscaEquipo("Málaga"), 1, 0 ) );
		liga.anyadePartido( new Partido( 10, liga.buscaEquipo("Betis"), liga.buscaEquipo("Athletic"), 1, 3 ) );
		liga.anyadePartido( new Partido( 10, liga.buscaEquipo("R.Sociedad"), liga.buscaEquipo("Celta"), 2, 3 ) );
		liga.anyadePartido( new Partido( 10, liga.buscaEquipo("Getafe"), liga.buscaEquipo("Barcelona"), 0, 2 ) );
		liga.anyadePartido( new Partido( 10, liga.buscaEquipo("Valencia"), liga.buscaEquipo("Levante"), 3, 0 ) );
		liga.anyadePartido( new Partido( 10, liga.buscaEquipo("Villarreal"), liga.buscaEquipo("Sevilla"), 2, 1 ) );
		liga.anyadePartido( new Partido( 10, liga.buscaEquipo("R.Madrid"), liga.buscaEquipo("LasPalmas"), 3, 1 ) );
		liga.anyadePartido( new Partido( 10, liga.buscaEquipo("Deportivo"), liga.buscaEquipo("Atlético"), 1, 1 ) );

		liga.anyadePartido( new Partido( 11, liga.buscaEquipo("LasPalmas"), liga.buscaEquipo("R.Sociedad"), 2, 0 ) );
		liga.anyadePartido( new Partido( 11, liga.buscaEquipo("Celta"), liga.buscaEquipo("Valencia"), 1, 5 ) );
		liga.anyadePartido( new Partido( 11, liga.buscaEquipo("Levante"), liga.buscaEquipo("Deportivo"), 1, 1 ) );
		liga.anyadePartido( new Partido( 11, liga.buscaEquipo("Rayo"), liga.buscaEquipo("Granada"), 2, 1 ) );
		liga.anyadePartido( new Partido( 11, liga.buscaEquipo("Eibar"), liga.buscaEquipo("Getafe"), 3, 1 ) );
		liga.anyadePartido( new Partido( 11, liga.buscaEquipo("Málaga"), liga.buscaEquipo("Betis"), 0, 1 ) );
		liga.anyadePartido( new Partido( 11, liga.buscaEquipo("Athletic"), liga.buscaEquipo("Espanyol"), 2, 1 ) );
		liga.anyadePartido( new Partido( 11, liga.buscaEquipo("Barcelona"), liga.buscaEquipo("Villarreal"), 3, 0 ) );
		liga.anyadePartido( new Partido( 11, liga.buscaEquipo("Atlético"), liga.buscaEquipo("Sporting"), 1, 0 ) );
		liga.anyadePartido( new Partido( 11, liga.buscaEquipo("Sevilla"), liga.buscaEquipo("R.Madrid"), 3, 2 ) );

		liga.anyadePartido( new Partido( 12, liga.buscaEquipo("Getafe"), liga.buscaEquipo("Rayo"), 1, 1 ) );
		liga.anyadePartido( new Partido( 12, liga.buscaEquipo("R.Sociedad"), liga.buscaEquipo("Sevilla"), 2, 0 ) );
		liga.anyadePartido( new Partido( 12, liga.buscaEquipo("R.Madrid"), liga.buscaEquipo("Barcelona"), 0, 4 ) );
		liga.anyadePartido( new Partido( 12, liga.buscaEquipo("Espanyol"), liga.buscaEquipo("Málaga"), 2, 0 ) );
		liga.anyadePartido( new Partido( 12, liga.buscaEquipo("Valencia"), liga.buscaEquipo("LasPalmas"), 1, 1 ) );
		liga.anyadePartido( new Partido( 12, liga.buscaEquipo("Deportivo"), liga.buscaEquipo("Celta"), 2, 0 ) );
		liga.anyadePartido( new Partido( 12, liga.buscaEquipo("Sporting"), liga.buscaEquipo("Levante"), 0, 3 ) );
		liga.anyadePartido( new Partido( 12, liga.buscaEquipo("Villarreal"), liga.buscaEquipo("Eibar"), 1, 1 ) );
		liga.anyadePartido( new Partido( 12, liga.buscaEquipo("Granada"), liga.buscaEquipo("Athletic"), 2, 0 ) );
		liga.anyadePartido( new Partido( 12, liga.buscaEquipo("Betis"), liga.buscaEquipo("Atlético"), 0, 1 ) );

		liga.anyadePartido( new Partido( 13, liga.buscaEquipo("Levante"), liga.buscaEquipo("Betis"), 0, 1 ) );
		liga.anyadePartido( new Partido( 13, liga.buscaEquipo("Barcelona"), liga.buscaEquipo("R.Sociedad"), 4, 0 ) );
		liga.anyadePartido( new Partido( 13, liga.buscaEquipo("Atlético"), liga.buscaEquipo("Espanyol"), 1, 0 ) );
		liga.anyadePartido( new Partido( 13, liga.buscaEquipo("Málaga"), liga.buscaEquipo("Granada"), 2, 2 ) );
		liga.anyadePartido( new Partido( 13, liga.buscaEquipo("Celta"), liga.buscaEquipo("Sporting"), 2, 1 ) );
		liga.anyadePartido( new Partido( 13, liga.buscaEquipo("LasPalmas"), liga.buscaEquipo("Deportivo"), 0, 2 ) );
		liga.anyadePartido( new Partido( 13, liga.buscaEquipo("Getafe"), liga.buscaEquipo("Villarreal"), 2, 0 ) );
		liga.anyadePartido( new Partido( 13, liga.buscaEquipo("Eibar"), liga.buscaEquipo("R.Madrid"), 0, 2 ) );
		liga.anyadePartido( new Partido( 13, liga.buscaEquipo("Rayo"), liga.buscaEquipo("Athletic"), 0, 3 ) );
		liga.anyadePartido( new Partido( 13, liga.buscaEquipo("Sevilla"), liga.buscaEquipo("Valencia"), 1, 0 ) );

		liga.anyadePartido( new Partido( 14, liga.buscaEquipo("R.Madrid"), liga.buscaEquipo("Getafe"), 4, 1 ) );
		liga.anyadePartido( new Partido( 14, liga.buscaEquipo("Granada"), liga.buscaEquipo("Atlético"), 0, 2 ) );
		liga.anyadePartido( new Partido( 14, liga.buscaEquipo("Valencia"), liga.buscaEquipo("Barcelona"), 1, 1 ) );
		liga.anyadePartido( new Partido( 14, liga.buscaEquipo("Deportivo"), liga.buscaEquipo("Sevilla"), 1, 1 ) );
		liga.anyadePartido( new Partido( 14, liga.buscaEquipo("Betis"), liga.buscaEquipo("Celta"), 1, 1 ) );
		liga.anyadePartido( new Partido( 14, liga.buscaEquipo("R.Sociedad"), liga.buscaEquipo("Eibar"), 2, 1 ) );
		liga.anyadePartido( new Partido( 14, liga.buscaEquipo("Villarreal"), liga.buscaEquipo("Rayo"), 2, 1 ) );
		liga.anyadePartido( new Partido( 14, liga.buscaEquipo("Sporting"), liga.buscaEquipo("LasPalmas"), 3, 1 ) );
		liga.anyadePartido( new Partido( 14, liga.buscaEquipo("Athletic"), liga.buscaEquipo("Málaga"), 0, 0 ) );
		liga.anyadePartido( new Partido( 14, liga.buscaEquipo("Espanyol"), liga.buscaEquipo("Levante"), 1, 1 ) );

		liga.anyadePartido( new Partido( 15, liga.buscaEquipo("Atlético"), liga.buscaEquipo("Athletic"), 2, 1 ) );
		liga.anyadePartido( new Partido( 15, liga.buscaEquipo("Villarreal"), liga.buscaEquipo("R.Madrid"), 1, 0 ) );
		liga.anyadePartido( new Partido( 15, liga.buscaEquipo("Eibar"), liga.buscaEquipo("Valencia"), 1, 1 ) );
		liga.anyadePartido( new Partido( 15, liga.buscaEquipo("Rayo"), liga.buscaEquipo("Málaga"), 1, 2 ) );
		liga.anyadePartido( new Partido( 15, liga.buscaEquipo("LasPalmas"), liga.buscaEquipo("Betis"), 1, 0 ) );
		liga.anyadePartido( new Partido( 15, liga.buscaEquipo("Sevilla"), liga.buscaEquipo("Sporting"), 2, 0 ) );
		liga.anyadePartido( new Partido( 15, liga.buscaEquipo("Celta"), liga.buscaEquipo("Espanyol"), 1, 0 ) );
		liga.anyadePartido( new Partido( 15, liga.buscaEquipo("Levante"), liga.buscaEquipo("Granada"), 1, 2 ) );
		liga.anyadePartido( new Partido( 15, liga.buscaEquipo("Barcelona"), liga.buscaEquipo("Deportivo"), 2, 2 ) );
		liga.anyadePartido( new Partido( 15, liga.buscaEquipo("Getafe"), liga.buscaEquipo("R.Sociedad"), 1, 1 ) );

		liga.anyadePartido( new Partido( 16, liga.buscaEquipo("Valencia"), liga.buscaEquipo("Getafe"), 2, 2 ) );
		liga.anyadePartido( new Partido( 16, liga.buscaEquipo("Espanyol"), liga.buscaEquipo("LasPalmas"), 1, 0 ) );
		liga.anyadePartido( new Partido( 16, liga.buscaEquipo("Betis"), liga.buscaEquipo("Sevilla"), 0, 0 ) );
		liga.anyadePartido( new Partido( 16, liga.buscaEquipo("Deportivo"), liga.buscaEquipo("Eibar"), 2, 0 ) );
		liga.anyadePartido( new Partido( 16, liga.buscaEquipo("R.Madrid"), liga.buscaEquipo("Rayo"), 10, 2 ) );
		liga.anyadePartido( new Partido( 16, liga.buscaEquipo("R.Sociedad"), liga.buscaEquipo("Villarreal"), 0, 2 ) );
		liga.anyadePartido( new Partido( 16, liga.buscaEquipo("Athletic"), liga.buscaEquipo("Levante"), 2, 0 ) );
		liga.anyadePartido( new Partido( 16, liga.buscaEquipo("Granada"), liga.buscaEquipo("Celta"), 0, 2 ) );
		liga.anyadePartido( new Partido( 16, liga.buscaEquipo("Málaga"), liga.buscaEquipo("Atlético"), 1, 0 ) );
		liga.anyadePartido( new Partido( 16, liga.buscaEquipo("Sporting"), liga.buscaEquipo("Barcelona"), 1, 3 ) );

		liga.anyadePartido( new Partido( 17, liga.buscaEquipo("R.Madrid"), liga.buscaEquipo("R.Sociedad"), 3, 1 ) );
		liga.anyadePartido( new Partido( 17, liga.buscaEquipo("Levante"), liga.buscaEquipo("Málaga"), 0, 1 ) );
		liga.anyadePartido( new Partido( 17, liga.buscaEquipo("Rayo"), liga.buscaEquipo("Atlético"), 0, 2 ) );
		liga.anyadePartido( new Partido( 17, liga.buscaEquipo("Sevilla"), liga.buscaEquipo("Espanyol"), 2, 0 ) );
		liga.anyadePartido( new Partido( 17, liga.buscaEquipo("Eibar"), liga.buscaEquipo("Sporting"), 2, 0 ) );
		liga.anyadePartido( new Partido( 17, liga.buscaEquipo("Barcelona"), liga.buscaEquipo("Betis"), 4, 0 ) );
		liga.anyadePartido( new Partido( 17, liga.buscaEquipo("Getafe"), liga.buscaEquipo("Deportivo"), 0, 0 ) );
		liga.anyadePartido( new Partido( 17, liga.buscaEquipo("Celta"), liga.buscaEquipo("Athletic"), 0, 1 ) );
		liga.anyadePartido( new Partido( 17, liga.buscaEquipo("LasPalmas"), liga.buscaEquipo("Granada"), 4, 1 ) );
		liga.anyadePartido( new Partido( 17, liga.buscaEquipo("Villarreal"), liga.buscaEquipo("Valencia"), 1, 0 ) );

		liga.anyadePartido( new Partido( 18, liga.buscaEquipo("Sporting"), liga.buscaEquipo("Getafe"), 1, 2 ) );
		liga.anyadePartido( new Partido( 18, liga.buscaEquipo("Valencia"), liga.buscaEquipo("R.Madrid"), 2, 2 ) );
		liga.anyadePartido( new Partido( 18, liga.buscaEquipo("Deportivo"), liga.buscaEquipo("Villarreal"), 1, 2 ) );
		liga.anyadePartido( new Partido( 18, liga.buscaEquipo("Athletic"), liga.buscaEquipo("LasPalmas"), 2, 2 ) );
		liga.anyadePartido( new Partido( 18, liga.buscaEquipo("Granada"), liga.buscaEquipo("Sevilla"), 2, 1 ) );
		liga.anyadePartido( new Partido( 18, liga.buscaEquipo("Betis"), liga.buscaEquipo("Eibar"), 0, 4 ) );
		liga.anyadePartido( new Partido( 18, liga.buscaEquipo("Rayo"), liga.buscaEquipo("R.Sociedad"), 2, 2 ) );
		liga.anyadePartido( new Partido( 18, liga.buscaEquipo("Málaga"), liga.buscaEquipo("Celta"), 2, 0 ) );
		liga.anyadePartido( new Partido( 18, liga.buscaEquipo("Atlético"), liga.buscaEquipo("Levante"), 1, 0 ) );
		liga.anyadePartido( new Partido( 18, liga.buscaEquipo("Espanyol"), liga.buscaEquipo("Barcelona"), 0, 0 ) );

		liga.anyadePartido( new Partido( 19, liga.buscaEquipo("Barcelona"), liga.buscaEquipo("Granada"), 4, 0 ) );
		liga.anyadePartido( new Partido( 19, liga.buscaEquipo("Sevilla"), liga.buscaEquipo("Athletic"), 2, 0 ) );
		liga.anyadePartido( new Partido( 19, liga.buscaEquipo("Getafe"), liga.buscaEquipo("Betis"), 1, 0 ) );
		liga.anyadePartido( new Partido( 19, liga.buscaEquipo("R.Madrid"), liga.buscaEquipo("Deportivo"), 5, 0 ) );
		liga.anyadePartido( new Partido( 19, liga.buscaEquipo("Levante"), liga.buscaEquipo("Rayo"), 2, 1 ) );
		liga.anyadePartido( new Partido( 19, liga.buscaEquipo("Villarreal"), liga.buscaEquipo("Sporting"), 2, 0 ) );
		liga.anyadePartido( new Partido( 19, liga.buscaEquipo("R.Sociedad"), liga.buscaEquipo("Valencia"), 2, 0 ) );
		liga.anyadePartido( new Partido( 19, liga.buscaEquipo("Eibar"), liga.buscaEquipo("Espanyol"), 2, 1 ) );
		liga.anyadePartido( new Partido( 19, liga.buscaEquipo("LasPalmas"), liga.buscaEquipo("Málaga"), 1, 1 ) );
		liga.anyadePartido( new Partido( 19, liga.buscaEquipo("Celta"), liga.buscaEquipo("Atlético"), 0, 2 ) );

		liga.anyadePartido( new Partido( 20, liga.buscaEquipo("Eibar"), liga.buscaEquipo("Granada"), 5, 1 ) );
		liga.anyadePartido( new Partido( 20, liga.buscaEquipo("Barcelona"), liga.buscaEquipo("Athletic"), 6, 0 ) );
		liga.anyadePartido( new Partido( 20, liga.buscaEquipo("Getafe"), liga.buscaEquipo("Espanyol"), 3, 1 ) );
		liga.anyadePartido( new Partido( 20, liga.buscaEquipo("LasPalmas"), liga.buscaEquipo("Atlético"), 0, 3 ) );
		liga.anyadePartido( new Partido( 20, liga.buscaEquipo("R.Madrid"), liga.buscaEquipo("Sporting"), 5, 1 ) );
		liga.anyadePartido( new Partido( 20, liga.buscaEquipo("Valencia"), liga.buscaEquipo("Rayo"), 2, 2 ) );
		liga.anyadePartido( new Partido( 20, liga.buscaEquipo("R.Sociedad"), liga.buscaEquipo("Deportivo"), 1, 1 ) );
		liga.anyadePartido( new Partido( 20, liga.buscaEquipo("Villarreal"), liga.buscaEquipo("Betis"), 0, 0 ) );
		liga.anyadePartido( new Partido( 20, liga.buscaEquipo("Celta"), liga.buscaEquipo("Levante"), 4, 3 ) );
		liga.anyadePartido( new Partido( 20, liga.buscaEquipo("Sevilla"), liga.buscaEquipo("Málaga"), 2, 1 ) );

		liga.anyadePartido( new Partido( 21, liga.buscaEquipo("Levante"), liga.buscaEquipo("LasPalmas"), 3, 2 ) );
		liga.anyadePartido( new Partido( 21, liga.buscaEquipo("Betis"), liga.buscaEquipo("R.Madrid"), 1, 1 ) );
		liga.anyadePartido( new Partido( 21, liga.buscaEquipo("Deportivo"), liga.buscaEquipo("Valencia"), 1, 1 ) );
		liga.anyadePartido( new Partido( 21, liga.buscaEquipo("Sporting"), liga.buscaEquipo("R.Sociedad"), 5, 1 ) );
		liga.anyadePartido( new Partido( 21, liga.buscaEquipo("Málaga"), liga.buscaEquipo("Barcelona"), 1, 2 ) );
		liga.anyadePartido( new Partido( 21, liga.buscaEquipo("Espanyol"), liga.buscaEquipo("Villarreal"), 2, 2 ) );
		liga.anyadePartido( new Partido( 21, liga.buscaEquipo("Granada"), liga.buscaEquipo("Getafe"), 3, 2 ) );
		liga.anyadePartido( new Partido( 21, liga.buscaEquipo("Rayo"), liga.buscaEquipo("Celta"), 3, 0 ) );
		liga.anyadePartido( new Partido( 21, liga.buscaEquipo("Athletic"), liga.buscaEquipo("Eibar"), 5, 2 ) );
		liga.anyadePartido( new Partido( 21, liga.buscaEquipo("Atlético"), liga.buscaEquipo("Sevilla"), 0, 0 ) );

		liga.anyadePartido( new Partido( 22, liga.buscaEquipo("Deportivo"), liga.buscaEquipo("Rayo"), 2, 2 ) );
		liga.anyadePartido( new Partido( 22, liga.buscaEquipo("R.Madrid"), liga.buscaEquipo("Espanyol"), 6, 0 ) );
		liga.anyadePartido( new Partido( 22, liga.buscaEquipo("LasPalmas"), liga.buscaEquipo("Celta"), 2, 1 ) );
		liga.anyadePartido( new Partido( 22, liga.buscaEquipo("Valencia"), liga.buscaEquipo("Sporting"), 0, 1 ) );
		liga.anyadePartido( new Partido( 22, liga.buscaEquipo("Sevilla"), liga.buscaEquipo("Levante"), 3, 1 ) );
		liga.anyadePartido( new Partido( 22, liga.buscaEquipo("R.Sociedad"), liga.buscaEquipo("Betis"), 2, 1 ) );
		liga.anyadePartido( new Partido( 22, liga.buscaEquipo("Villarreal"), liga.buscaEquipo("Granada"), 1, 0 ) );
		liga.anyadePartido( new Partido( 22, liga.buscaEquipo("Eibar"), liga.buscaEquipo("Málaga"), 1, 2 ) );
		liga.anyadePartido( new Partido( 22, liga.buscaEquipo("Getafe"), liga.buscaEquipo("Athletic"), 0, 1 ) );
		liga.anyadePartido( new Partido( 22, liga.buscaEquipo("Barcelona"), liga.buscaEquipo("Atlético"), 2, 1 ) );

		liga.anyadePartido( new Partido( 23, liga.buscaEquipo("Espanyol"), liga.buscaEquipo("R.Sociedad"), 0, 5 ) );
		liga.anyadePartido( new Partido( 23, liga.buscaEquipo("Granada"), liga.buscaEquipo("R.Madrid"), 1, 2 ) );
		liga.anyadePartido( new Partido( 23, liga.buscaEquipo("Celta"), liga.buscaEquipo("Sevilla"), 1, 1 ) );
		liga.anyadePartido( new Partido( 23, liga.buscaEquipo("Betis"), liga.buscaEquipo("Valencia"), 1, 0 ) );
		liga.anyadePartido( new Partido( 23, liga.buscaEquipo("Levante"), liga.buscaEquipo("Barcelona"), 0, 2 ) );
		liga.anyadePartido( new Partido( 23, liga.buscaEquipo("Atlético"), liga.buscaEquipo("Eibar"), 3, 1 ) );
		liga.anyadePartido( new Partido( 23, liga.buscaEquipo("Rayo"), liga.buscaEquipo("LasPalmas"), 2, 0 ) );
		liga.anyadePartido( new Partido( 23, liga.buscaEquipo("Málaga"), liga.buscaEquipo("Getafe"), 3, 0 ) );
		liga.anyadePartido( new Partido( 23, liga.buscaEquipo("Athletic"), liga.buscaEquipo("Villarreal"), 0, 0 ) );
		liga.anyadePartido( new Partido( 23, liga.buscaEquipo("Sporting"), liga.buscaEquipo("Deportivo"), 1, 1 ) );

		liga.anyadePartido( new Partido( 24, liga.buscaEquipo("R.Sociedad"), liga.buscaEquipo("Granada"), 3, 0 ) );
		liga.anyadePartido( new Partido( 24, liga.buscaEquipo("Sevilla"), liga.buscaEquipo("LasPalmas"), 2, 0 ) );
		liga.anyadePartido( new Partido( 24, liga.buscaEquipo("Getafe"), liga.buscaEquipo("Atlético"), 0, 1 ) );
		liga.anyadePartido( new Partido( 24, liga.buscaEquipo("Eibar"), liga.buscaEquipo("Levante"), 2, 0 ) );
		liga.anyadePartido( new Partido( 24, liga.buscaEquipo("Barcelona"), liga.buscaEquipo("Celta"), 6, 1 ) );
		liga.anyadePartido( new Partido( 24, liga.buscaEquipo("Deportivo"), liga.buscaEquipo("Betis"), 2, 2 ) );
		liga.anyadePartido( new Partido( 24, liga.buscaEquipo("Valencia"), liga.buscaEquipo("Espanyol"), 2, 1 ) );
		liga.anyadePartido( new Partido( 24, liga.buscaEquipo("Villarreal"), liga.buscaEquipo("Málaga"), 1, 0 ) );
		liga.anyadePartido( new Partido( 24, liga.buscaEquipo("R.Madrid"), liga.buscaEquipo("Athletic"), 4, 2 ) );
		liga.anyadePartido( new Partido( 24, liga.buscaEquipo("Sporting"), liga.buscaEquipo("Rayo"), 2, 2 ) );

		liga.anyadePartido( new Partido( 25, liga.buscaEquipo("Atlético"), liga.buscaEquipo("Villarreal"), 0, 0 ) );
		liga.anyadePartido( new Partido( 25, liga.buscaEquipo("Athletic"), liga.buscaEquipo("R.Sociedad"), 0, 1 ) );
		liga.anyadePartido( new Partido( 25, liga.buscaEquipo("Granada"), liga.buscaEquipo("Valencia"), 1, 2 ) );
		liga.anyadePartido( new Partido( 25, liga.buscaEquipo("Málaga"), liga.buscaEquipo("R.Madrid"), 1, 1 ) );
		liga.anyadePartido( new Partido( 25, liga.buscaEquipo("Rayo"), liga.buscaEquipo("Sevilla"), 2, 2 ) );
		liga.anyadePartido( new Partido( 25, liga.buscaEquipo("Celta"), liga.buscaEquipo("Eibar"), 3, 2 ) );
		liga.anyadePartido( new Partido( 25, liga.buscaEquipo("Betis"), liga.buscaEquipo("Sporting"), 1, 1 ) );
		liga.anyadePartido( new Partido( 25, liga.buscaEquipo("Espanyol"), liga.buscaEquipo("Deportivo"), 1, 0 ) );
		liga.anyadePartido( new Partido( 25, liga.buscaEquipo("LasPalmas"), liga.buscaEquipo("Barcelona"), 1, 2 ) );
		liga.anyadePartido( new Partido( 25, liga.buscaEquipo("Levante"), liga.buscaEquipo("Getafe"), 3, 0 ) );
		
		return liga;
	}
	
	/** Devuelve el la lista de partidos de la jornada indicada
	 * @param numJornada	Número de la jornada (de 1 a N)
	 * @return	Lista de partidos de esa jornada, lista vacía si se ha disputado esa jornada pero no hay partidos, null si no se ha disputado
	 */
	public ArrayList<Partido> getPartidos( int numJornada ) {
		if (partidos.size()<numJornada) return null;
		return partidos.get( numJornada-1 );
	}
	
	/** Devuelve el número de jornadas disputadas
	 * @return	Número de jornadas disputadas, 0 si no hay jornadas disputadas
	 */
	public int getJornadas() {
		return partidos.size();
	}
	
	@Override
	public String toString() {
		return nombre;
	}
	
	public ArrayList<Equipo> calculaClasificacion() {
		ArrayList<Equipo> clasif = new ArrayList<Equipo>( );
		for (Equipo equipo : equipos.values()) {
			equipo.resetPuntos();
			equipo.getDeporte().reset();
			clasif.add( equipo );
		}
		for (ArrayList<Partido> jornada : partidos)
			for (Partido partido : jornada) {
				partido.getEquipo1().incPuntos( deporte.getPuntos1( partido ) );
				partido.getEquipo2().incPuntos( deporte.getPuntos2( partido ) );
				partido.getEquipo1().deporte.actualiza( partido, partido.getEquipo1() );
				partido.getEquipo2().deporte.actualiza( partido, partido.getEquipo2() );
			}
		clasif.sort( new Comparator<Equipo>() {
			@Override
			public int compare(Equipo o1, Equipo o2) {
				return deporte.comparaEquipos( o1, o2 );
			}
		});
		return clasif;
	}
	
	/** Devuelve la lista de equipos con los que el indicado ha conseguido ya victorias
	 * @param equipo	Equipo a chequear
	 * @return	Lista de equipos a los que equipo ha ganado (vacía si ninguno, puede aparecer dos veces el mismo equipo si se le ha ganado dos veces)
	 */
	public ArrayList<Equipo> getVictorias( Equipo equipo ) {
		ArrayList<Equipo> ret = new ArrayList<>();
		for (ArrayList<Partido> jornada : partidos) {
			for (Partido partido : jornada) {
				if (partido.getEquipo1().equals( equipo ) && partido.getMarcador1() > partido.getMarcador2()) {
					ret.add( partido.getEquipo2() );
				} else if (partido.getEquipo2().equals( equipo ) && partido.getMarcador2() > partido.getMarcador1()) {
					ret.add( partido.getEquipo1() );
				}
			}
		}
		return ret;
	}
	
	/** Devuelve la lista de equipos con los que el indicado ha conseguido ya derrotas
	 * @param equipo	Equipo a chequear
	 * @return	Lista de equipos con los que equipo ha perdido (vacía si ninguno, puede aparecer dos veces el mismo equipo si se ha perdido dos veces)
	 */
	public ArrayList<Equipo> getDerrotas( Equipo equipo ) {
		ArrayList<Equipo> ret = new ArrayList<>();
		for (ArrayList<Partido> jornada : partidos) {
			for (Partido partido : jornada) {
				if (partido.getEquipo1().equals( equipo ) && partido.getMarcador1() < partido.getMarcador2()) {
					ret.add( partido.getEquipo2() );
				} else if (partido.getEquipo2().equals( equipo ) && partido.getMarcador2() < partido.getMarcador1()) {
					ret.add( partido.getEquipo1() );
				}
			}
		}
		return ret;
	}
	
	/** Test de carga de liga de ejemplo (de fútbol)
	 * @param args	No utilizado
	 */
	public static void main(String[] args) {
		Liga ligaEjemplo = initEjemplo();
		// Ejemplo de un partido
		int jornada = 1; int partido = 0;
		System.out.println( "Ejemplo de jornada " + jornada + " - partido " + partido );
		System.out.println( "  " + ligaEjemplo.getPartidos( jornada ).get(partido) );
		// Volcado de toda la liga
		for (int jor = 1; jor <= ligaEjemplo.getJornadas(); jor++) {
			System.out.println( "Jornada " + jor );
			for (int par = 0; par < ligaEjemplo.getPartidos(jor).size(); par++) {
				System.out.println( "  " + ligaEjemplo.getPartidos(jor).get(par) );
			}
			System.out.println();
		}
	}

}
