package examen.ord201901.sesionesCentros;

import java.util.*;

/** Clase para gestionar centros educativos
 * @author andoni.eguiluz @ ingenieria.deusto.es
 */
public class CentroEd implements ConvertibleEnTabla {
	private String codigo;               // Código de centro
	private ArrayList<String> lAbrevs;   // Lista de abreviaturas que nos pueden valer para identificar al centro
	private Contador contSesiones;       // Contador de sesiones de mentoring realizadas en el centro (inicializado a 0)
	private int[] estudPorSesion;        // Array de estudiantes por sesión [0-5]
	private int satisfMentorasSuma;      // Suma de satisfacción general de mentoras
	private int numSatisfMentoras;       // Número de valoraciones de satisfacción de mentoras
	private int satisfEstudsSuma;        // Suma de satisfacción general de estudiantes
	private int numSatisfEstuds;         // Número de valoraciones de satisfacción de estudiantes
	
	/** Crea un nuevo centro educativo
	 * @param codigo	Código único del centro
	 */
	public CentroEd( String codigo ) {
		this.codigo = codigo;
		lAbrevs = new ArrayList<>();
		this.contSesiones = new Contador();
		estudPorSesion = new int[6];
	}
	
	/** Devuelve el código
	 * @return	Código único del centro
	 */
	public String getCodigo() {
		return codigo;
	}

	/** Añade abreviaturas
	 * @param lAbrevs
	 */
	public void addAbrevs( List<String> lAbrevs ) {
		this.lAbrevs.addAll( lAbrevs );
	}
	
	/** Devuelve la lista de abreviaturas
	 * @return	Lista de abreviaturas del centro
	 */
	public ArrayList<String> getlAbrevs() {
		return lAbrevs;
	}

	/** Devuelve el contador de sesiones
	 * @return	Contador de sesiones realizadas
	 */
	public Contador getContSesiones() {
		return contSesiones;
	}

	/** Devuelve el array de estudiantes por sesión
	 * @return	Array de 6 posiciones (0 a 5) para el número de estudiantes por cada sesión (0=sesión 1, 1=sesión 2, ... 5=sesión 6)
	 */
	public int[] getEstudPorSesion() {
		return estudPorSesion;
	}

	/** Devuelve la satisfacción media de las mentoras
	 * @return	Satisfacción media, Double.NaN si no se ha añadido aún ninguna valoración
	 */
	public double getMediaSatisfMentoras() {
		return satisfMentorasSuma * 1.0 / numSatisfMentoras;
	}

	/** Devuelve el número de valoraciones de satisfacción de las mentoras
	 * @return	Número de valoraciones introducidas
	 */
	public int getValsMentoras() {
		return numSatisfMentoras;
	}

	/** Añade una valoración de mentora
	 * @param satisf	Valor de satisfacción a añadir (de 0 a 5)
	 */
	public void addValMentor( int satisf ) {
		satisfMentorasSuma += satisf;
		numSatisfMentoras++;
	}

	/** Devuelve la satisfacción media de los estudiantes
	 * @return	Satisfacción media, Double.NaN si no se ha añadido aún ninguna valoración
	 */
	public double getMediaSatisfEstuds() {
		return satisfEstudsSuma * 1.0 / numSatisfEstuds;
	}

	/** Devuelve el número de valoraciones de satisfacción de los estudiantes
	 * @return	Número de valoraciones introducidas	
	 */
	public int getValsEstuds() {
		return numSatisfEstuds;
	}

	/** Añade una valoración de estudiantes
	 * @param satisf	Valor de satisfacción a añadir (de 0 a 5)
	 */
	public void addValEstud( int satisf ) {
		satisfEstudsSuma += satisf;
		numSatisfEstuds++;
	}
	
	@Override
	public String toString() {
		return codigo;
	}


	// =========================================================================
	// Métodos de interfaz ConvertibleEnTabla
	
	@Override
	public int getNumColumnas() {
		return 10;
	}
	@Override
	public String getValorColumna( int col ) {
		switch (col) {
			case 0: {
				return codigo;
			}
			case 1: {
				return "" + contSesiones;
			}
			case 2: {
				return Double.isNaN(getMediaSatisfMentoras()) ? "" : "" + getMediaSatisfMentoras();
			}
			case 3: {
				return Double.isNaN(getMediaSatisfEstuds()) ? "" : "" + getMediaSatisfEstuds();
			}
			default: {
				if (col<=9)
					return "" + estudPorSesion[ col-4 ];
				return "";
			}
		}
	}
	@Override
	public String getNombreColumna( int col ) {
		switch (col) {
			case 0: {
				return "Cód.centro";
			}
			case 1: {
				return "Nº Sesiones";
			}
			case 2: {
				return "Satisf.Mentoras";
			}
			case 3: {
				return "Satisf.Estudiantes";
			}
			default: {
				if (col<=9)
					return "NºEsts/Ses." + (col-3);
				return "";
			}
		}
	}
	
	
	
	// =========================================================================
	// Métodos estáticos
	
	public static HashMap<String,CentroEd> cargaCentros() {
		HashMap<String,CentroEd> mapa = new HashMap<>();
		Scanner scanner = new Scanner( CentroEd.class.getResourceAsStream( "centros-escolares.txt"), "UTF-8" );
		while (scanner.hasNext()) {
			String lin = scanner.nextLine();
			StringTokenizer st = new StringTokenizer( lin, "\t" );
			if (st.hasMoreTokens()) {
				String cole = st.nextToken();
				CentroEd centro = new CentroEd( cole );
				ArrayList<String> cods = new ArrayList<>();
				while (st.hasMoreTokens()) {
					cods.add( st.nextToken().toLowerCase() );
				}
				centro.addAbrevs( cods );
				mapa.put( cole, centro );
			}
		}
		scanner.close();
		return mapa;
	}

}
