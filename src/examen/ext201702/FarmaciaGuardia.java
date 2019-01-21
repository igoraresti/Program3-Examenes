package examen.ext201702;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** Clase para representar una farmacia de guardia
 * @author andoni.eguiluz @ ingenieria.deusto.es
 */
public class FarmaciaGuardia implements Serializable, Comparable<FarmaciaGuardia> {
	private static final long serialVersionUID = 1L;

	private String localidad = "";
	private long horaDesde = -1;
	private long horaHasta = -1;
	private String zona = "";
	private String direccion = "";
	private String telefono = "";
	
	/** Constructor de farmacia de guardia usando cada atributo
	 * @param localidad	Localidad de la farmacia
	 * @param horaDesde	Hora de apertura de la farmacia (milisegundos desde las 00:00 AM)
	 * @param horaHasta	Hora de cierre de la farmacia (milisegundos desde las 00:00 AM)
	 * @param zona	Zona/barrio de la farmacia
	 * @param direccion	Dirección de la farmacia
	 * @param telefono	Teléfono de la farmacia (en formato string)
	 */
	public FarmaciaGuardia(String localidad, long horaDesde, long horaHasta, String zona, String direccion, String telefono) {
		super();
		this.localidad = localidad;
		this.horaDesde = horaDesde;
		this.horaHasta = horaHasta;
		this.zona = zona;
		this.direccion = direccion;
		this.telefono = telefono;
	}

	// Atributos utilitarios
	public static SimpleDateFormat sdfHM = new SimpleDateFormat( "HH:mm" );  // para formateo de hora y minutos
	public static long MSGS_DIA = 24L * 3600000L;  // para calcular milisegundos al día (independientes de la fecha)
	
	
	/** Constructor de farmacia de guardia con atributos integrados
	 * @param localidad	Localidad de la farmacia
	 * @param horario	Horario en formato "hh:mm - hh:mm" (primero la apertura y después el cierre)
	 * @param zonaDirTelefono	Datos en formato "(zona) dirección | teléfono"
	 */
	public FarmaciaGuardia(String localidad, String horario, String zonaDirTelefono) {
		super();
		this.localidad = localidad;
		int guion = horario.indexOf( "-" );
		if (guion > 0) {
			try {
				Date hora = sdfHM.parse( horario.substring(0, guion).trim() );
				setHoraDesde( hora.getTime() );
				hora = sdfHM.parse( horario.substring(guion+1).trim() );
				setHoraHasta( hora.getTime() );
			} catch (ParseException e) { }
		}
		int parentesis = zonaDirTelefono.indexOf( "(" );
		int parentesis2 = zonaDirTelefono.indexOf( ")" );
		if (parentesis >= 0 && parentesis2 > 0) {
			zona = zonaDirTelefono.substring( parentesis+1, parentesis2 ).trim();
			zonaDirTelefono = zonaDirTelefono.substring( parentesis2+1 );
		}
		int barra = zonaDirTelefono.indexOf( "|" );
		if (barra > 0) {
			telefono = zonaDirTelefono.substring( barra + 1 ).trim();
			if (telefono.toUpperCase().startsWith( "TFNO: " )) telefono = telefono.substring( 6 ).trim();
			zonaDirTelefono = zonaDirTelefono.substring( 0, barra ).trim();
		}
		direccion = zonaDirTelefono;
	}
	
	/** Chequea si los datos de la farmacia son completos
	 * @return	true si la farmacia tiene datos de hora de apertura y cierre, localidad y dirección. false en caso contrario
	 */
	public boolean estaCompleta() {
		return horaDesde >= 0 && horaHasta >= 0 && !localidad.isEmpty() && !direccion.isEmpty();
	}
	
	public String getLocalidad() {
		return localidad;
	}
	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}
	
	/** Consulta la hora de apertura de la farmacia
	 * @return	Hora de apertura en msgs desde las 00:00 AM
	 */
	public long getHoraDesde() {
		return horaDesde;
	}
	/** Consulta la hora de apertura de la farmacia
	 * @return	Hora de apertura en formato string "hh:mm"
	 */
	public String getHoraDesdeSt() {
		return sdfHM.format( new Date(horaDesde) );
	}
	/** Modifica la hora de apertura de la farmacia
	 * @param horaDesde	Msgs transcurridos desde las 00:00 AM (se ignoran los días-fecha)
	 */
	public void setHoraDesde(long horaDesde) {
		this.horaDesde = horaDesde % MSGS_DIA;
	}
	
	/** Consulta la hora de cierre de la farmacia
	 * @return	Hora de cierre en msgs desde las 00:00 AM
	 */
	public long getHoraHasta() {
		return horaHasta;
	}
	/** Consulta la hora de cierre de la farmacia
	 * @return	Hora de cierre en formato string "hh:mm"
	 */
	public String getHoraHastaSt() {
		return sdfHM.format( new Date(horaHasta) );
	}
	/** Modifica la hora de cierre de la farmacia
	 * @param horaDesde	Msgs transcurridos desde las 00:00 AM (se ignoran los días-fecha)
	 */
	public void setHoraHasta(long horaHasta) {
		this.horaHasta = horaHasta;
	}
	
	public String getZona() {
		return zona;
	}
	public void setZona(String zona) {
		this.zona = zona;
	}
	
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	/** Indica si la farmacia está actualmente abierta o va a estar abierta en los siguientes minutos
	 * @param proxMinutos	Número de minutos a considerar (positivo o cero)
	 * @return true si está abierta ahora, o estará abierta en los minutos indicados. false en caso contrario
	 */
	public boolean estaAbiertaAhora( int proxMinutos ) {
		long horaAhora = System.currentTimeMillis() % MSGS_DIA;
		// Test de horas distintas a la actual desde la aplicación
		horaAhora = (horaAhora + VentanaFarmacias.horaTest*3600000L + VentanaFarmacias.minsTest*60000L) % MSGS_DIA;  // Modificación de hora actual para testeo de la aplicación ( normalmente hraTest y minsTest debería ser 0)
		//
		long horaInic = horaDesde % MSGS_DIA;
		long horaFin = horaHasta % MSGS_DIA;
		long minutosMargen = proxMinutos * 60000L;
		boolean ret = (horaAhora >= horaInic && horaAhora <= horaFin);
		if (!ret) {
			if (horaInic < horaFin) {   // Horario que no cambia de día (ej: 9 a 22)
				ret = (horaAhora >= horaInic-minutosMargen && horaAhora <= horaFin);
			} else {   // Horario que cambia de día  (ej: 22 a 9)
				ret = (horaAhora >= horaInic-minutosMargen || horaAhora <= horaFin);
			}
		}
		return ret;
	}
	
	@Override
	public String toString() {
		return localidad + "  " + sdfHM.format( new Date(horaDesde) ) + "-" + sdfHM.format( new Date(horaHasta)) +
				"  (" + zona + ")  " + direccion + "  " + telefono;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FarmaciaGuardia) {
			FarmaciaGuardia f2 = (FarmaciaGuardia) obj;
			return localidad!=null && localidad.equals(f2.localidad) && horaDesde==f2.horaDesde && horaHasta==f2.horaHasta && direccion!=null && direccion.equals(f2.direccion);
		}
		return false;
	}

	@Override
	public int compareTo(FarmaciaGuardia o) {
		if (horaDesde!=o.horaDesde) return (int) (horaDesde-o.horaDesde);
		if (horaHasta!=o.horaHasta) return (int) (horaHasta-o.horaHasta);
		return (zona+direccion).compareTo( o.zona+o.direccion );
	}
	
	/** Devuelve la propiedad del teléfono de la farmacia de ser capicúa. 
	 * Para ello se comparan los dígitos inicial y final y se devuelve su diferencia, sumada a la 
	 * diferencia de los dígitos siguiente y anterior, y así sucesivamente. Cuando un número es capicúa,
	 * su diferencia total será cero. Si es cercano al capicúa, será un número pequeño. Por ejemplo:
	 * telefono = "94 4139000" --> calcCapicua() grande
	 * telefono = "94 413 14 49" --> calcCapicua() = 0
	 * telefono = "944 132 439" --> calcCapicua() = 2
	 * @return	Cálculo de propiedad de capicua del teléfono de la farmacia (0 si es perfectamente capicúa)
	 */
	public int calcCapicua() {
		String telef = quitaSimbolos( telefono, 0 );
		return calcCapicua( telef, 0, telef.length()-1 );
	}
		private String quitaSimbolos( String stringOriginal, int carIni ) {
			if (carIni < stringOriginal.length()) {
				char caracter = stringOriginal.charAt(carIni);
				if (caracter >= '0' && caracter <= '9')
					return caracter + quitaSimbolos( stringOriginal, carIni+1 );
				else 
					return quitaSimbolos( stringOriginal, carIni+1 );
			} else {  // Caso base
				return "";  
			}
		}
		private int calcCapicua( String telefono, int carIni, int carFin ) {
			if (carIni>=carFin) return 0;  // Caso base
			int valIni = (int) (telefono.charAt(carIni) - '0');
			int valFin = (int) (telefono.charAt(carFin) - '0');
			return Math.abs( valIni - valFin ) + calcCapicua( telefono, carIni+1, carFin-1 );
		}
	
}

