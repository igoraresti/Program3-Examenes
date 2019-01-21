package examen.ord201901.sesionesCentros;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;

/** Clase de ventana para muestra de datos de centros escolares y feedback de mentoras
 * @author andoni.eguiluz @ ingenieria.deusto.es
 */
@SuppressWarnings("serial")
public class VentanaDatos extends JFrame {
	
	private JTable tDatos;  // JTable de datos de la ventana
	private JLabel lMensaje;  // Label de mensaje
	
	/** Crea una nueva ventana
	 */
	public VentanaDatos() {
		// Configuración general
		setTitle( "Ventana de datos" );
		setSize( 800, 600 ); // Tamaño por defecto
		setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		// Creación de componentes y contenedores
		JPanel pBotonera = new JPanel();
		tDatos = new JTable();
		JButton bCargaFeedback = new JButton( "Carga feedback" );
		JButton bGuardaBD = new JButton( "Guardar en BD" );
		JButton bBuscaMentora = new JButton( "Buscar mentora" );
		JButton bT2 = new JButton( "T2" );
		JButton bT3 = new JButton( "T3" );
		JLabel lMensaje = new JLabel( " " );
		// Asignación de componentes
		pBotonera.add( bCargaFeedback );
		pBotonera.add( bGuardaBD );
		pBotonera.add( bBuscaMentora );
		pBotonera.add( bT2 );
		pBotonera.add( bT3 );
		getContentPane().add( new JScrollPane( tDatos ), BorderLayout.CENTER );
		getContentPane().add( pBotonera, BorderLayout.SOUTH );
		getContentPane().add( lMensaje, BorderLayout.NORTH );
		// Eventos
		bCargaFeedback.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clickCargaFeedback();
			}
		});
		bGuardaBD.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clickGuardaBD();
			}
		});
		bBuscaMentora.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clickBuscarMentora();
			}
		});
		bT2.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clickT2();
			}
		});
		bT3.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clickT3();
			}
		});
		// Cierre
		setLocationRelativeTo( null );  // Centra la ventana en el escritorio
	}
	
	/** Asigna una tabla de datos a la JTable principal de la ventana
	 * @param tabla	Tabla de datos a visualizar
	 */
	public void setTabla( Tabla tabla ) {
		tDatos.setModel( tabla.getTableModel() );
	}
	
	// ========================================
	// Eventos
	
	// Click botón de guardado en Base de Datos
	private void clickGuardaBD() {
		Connection conn = BD.initBD( "centrosEd.bd" );
		Statement stat = BD.usarCrearTablasBD( conn );
		if (stat==null) return; // Error
		for (CentroEd centro : Datos.centros.values()) {
			if (centro.getContSesiones().get()>0) { // Solo se actualizan los centros con sesiones
				// Tabla centros
				boolean existe = BD.centroSelect( stat, centro.getCodigo() );
				if (existe) {
					BD.centroUpdate( stat, centro );
				} else {
					BD.centroInsert( stat, centro );
				}
				// Tabla sesiones
				for (int numSes=0; numSes<6; numSes++) {
					int numEsts = BD.sesionSelect( stat, centro.getCodigo(), numSes+1 );
					if (numEsts==-1) {
						BD.sesionInsert( stat, centro.getCodigo(), numSes+1, centro.getEstudPorSesion()[numSes] );
					} else {
						BD.sesionUpdate( stat, centro.getCodigo(), numSes+1, centro.getEstudPorSesion()[numSes] );
					}
				}
			}
		}
	}
	
	// T2
	private void clickT2() {
	}
	
	// T3
	private void clickT3() {
	}
	
	// Click botón de búsqueda de mentora
	private void clickBuscarMentora() {
		String aBuscar = JOptionPane.showInputDialog( this, "Introduce email", "Búsqueda de mentora", JOptionPane.QUESTION_MESSAGE );
		if (aBuscar==null || aBuscar.isEmpty()) return;
		ArrayList<MentoraCentro> listaMentorasCentros = new ArrayList<>();
		try {
			Tabla mentoras = Tabla.processCSV( VentanaDatos.class.getResource( "Mentoring2018.csv" ).toURI().toURL() );
			int columnaCentro = mentoras.getColumnWithHeader( "COD", true );
			int columnaEmail = mentoras.getColumnWithHeader( "email", false );
			for (int fila=0; fila<mentoras.size(); fila++) {
				try {
					String codCentro = mentoras.get( fila, columnaCentro );
					String email = mentoras.get( fila,  columnaEmail );
					if (!email.isEmpty() && !codCentro.isEmpty()) {
						MentoraCentro mc = new MentoraCentro( email, codCentro );
						int yaEsta = listaMentorasCentros.indexOf( mc );
						if (yaEsta==-1)
							listaMentorasCentros.add( mc );  // Nueva mentora-centro: meterla en la lista
						else
							listaMentorasCentros.get( yaEsta ).getContSesiones().inc();  // Ya estaba: incrementar contador de sesiones
					}
				} catch (Exception e) {}
			}
			listaMentorasCentros.sort( new Comparator<MentoraCentro>() {  // Ordena la lista por emails y luego por centros
				@Override
				public int compare(MentoraCentro o1, MentoraCentro o2) {
					return (o1.email+o1.codCentro).compareTo( o2.email+o2.codCentro );
				}
			});
			for (MentoraCentro mc : listaMentorasCentros) System.out.println( mc );  // Visualiza las mentoras en consola (a efectos de entender la estructura en el examen)
			contLlamadas = 0;
			int posi = buscarMentoraRec( listaMentorasCentros, aBuscar, 0, listaMentorasCentros.size()-1 );
			String mens = "";
			while (posi>=0 && posi<listaMentorasCentros.size() && listaMentorasCentros.get(posi).getEmail().equals( aBuscar )) {
				mens = mens + listaMentorasCentros.get(posi).getCodCentro() + " - " + listaMentorasCentros.get(posi).getContSesiones() + "\n";
				posi++;
			}
			if (mens.isEmpty()) mens = "No encontrada";
			JOptionPane.showMessageDialog( this, mens, "Sesiones de mentora " + aBuscar, JOptionPane.INFORMATION_MESSAGE );
			System.out.println( "Número de llamadas recursivas: " + contLlamadas + " (tamaño de la lista = " + listaMentorasCentros.size() + ")" );
		} catch (Exception e) { e.printStackTrace(); }
	}
	
		private static int contLlamadas = 0;
		private int buscarMentoraRec( ArrayList<MentoraCentro> listaMC, String emailABuscar, int desde, int hasta ) {
			contLlamadas++;
			if (desde>=hasta) {  // Caso base: encontrado elemento o no
				if (desde==hasta && listaMC.get(desde).getEmail().equals(emailABuscar)) return desde;  // Búsqueda exitosa
				return -1;  // Búsqueda no exitosa
			} else {
				int medio = (desde + hasta) / 2;
				MentoraCentro mc = listaMC.get( medio );
				if (mc.getEmail().compareTo(emailABuscar)>=0) {  // Email del medio >= email a buscar
					return buscarMentoraRec( listaMC, emailABuscar, desde, medio );
				} else {  // Email del medio < email a buscar
					return buscarMentoraRec( listaMC, emailABuscar, medio+1, hasta );
				}
			}
		}
	
		/** Clase interna de gestión de mentora-centro con contador de sesiones de esa mentora en ese centro */
		public static class MentoraCentro {
			public String email;
			public String codCentro;
			public Contador contSesiones;
			/** Crea un nuevo objeto mentora-centro con contador de sesiones a uno
			 * @param email	Email de la mentora
			 * @param codCentro	Código del centro
			 */
			public MentoraCentro(String email, String codCentro) {
				super();
				this.email = email;
				this.codCentro = codCentro;
				this.contSesiones = new Contador(1);
			}
			public Contador getContSesiones() { return contSesiones; }
			public String getEmail() { return email; }
			public String getCodCentro() { return codCentro; }
			@Override
			public boolean equals(Object obj) {
				if (!(obj instanceof MentoraCentro)) return false;
				MentoraCentro mc = (MentoraCentro) obj;
				return mc.codCentro.equals(codCentro) && mc.email.equals(email);
			}
			@Override
			public String toString() {
				return "{" + email + "-" + codCentro + " : " + contSesiones + "}";
			}
		}

	// Click botón de carga de feedback
	private void clickCargaFeedback() {
		// Cálculo de datos en función del seguimiento del mentoring
		seguimientoSesiones();
	}
	
	private static void seguimientoSesiones() {
		try {
			Tabla mentoras = Tabla.processCSV( VentanaDatos.class.getResource( "Mentoring2018.csv" ).toURI().toURL() );
			int columnaSesion = mentoras.getColumnWithHeader( "Número de sesión", false );
			int columnaCentro = mentoras.getColumnWithHeader( "COD", true );
			int columnaNumEsts = mentoras.getColumnWithHeader( "Nº de chicas/os", false );
			int columnaSatMent = mentoras.getColumnWithHeader( "Tu nivel de satisf", false );
			int columnaSatEst = mentoras.getColumnWithHeader( "satisfacción de los chicas/os", false );
			for (int fila=0; fila<mentoras.size(); fila++) {
				try {
					int numSesion = Integer.parseInt( mentoras.get( fila, columnaSesion ) );
					String codCentro = mentoras.get( fila, columnaCentro );
					int numEstuds = Integer.parseInt( mentoras.get( fila, columnaNumEsts ) );
					int satMentora = Integer.parseInt( mentoras.get( fila, columnaSatMent ) );
					int satEstuds = Integer.parseInt( mentoras.get( fila, columnaSatEst ) );
					CentroEd centro = Datos.centros.get( codCentro );
					if (centro!=null) {
						centro.getContSesiones().inc();    // Incrementa el contador de sesiones
						centro.addValMentor( satMentora ); // Añade satisfacción de mentora
						centro.addValEstud( satEstuds );   // Añade satisfacción de estudiantes
						centro.getEstudPorSesion()[ numSesion-1 ] += numEstuds;  // Añade número de estudiantes en la sesión correspondiente
					} else {
						// System.err.println( "Código de centro incorrecto en línea de seguimiento: " + mentoras.getFila( fila ) );
						procesaErrorLinea( (l) -> System.out.println( "Código de centro incorrecto en línea de seguimiento: " + l ), 
							mentoras.getFila(fila) );
					}
				} catch (Exception e) {
					// System.err.println( "Error en línea de seguimiento: " + mentoras.getFila( fila ) );
					procesaErrorLinea( (l) -> logger.log( Level.WARNING, "Error en línea de seguimiento: " + l ), 
						mentoras.getFila(fila) );
				}
			}
			Tabla c = Tabla.createTablaFromColl( Datos.centros.values() );
			Datos.v.setTabla( c );
		} catch (Exception e) { e.printStackTrace(); }
	}
	
		private static Logger logger = Logger.getLogger( "Seguimiento sesiones" );
		static {
			try {
				logger.addHandler( new FileHandler( "errores-sesiones.xml", true ) );
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private static void procesaErrorLinea( Consumer<ArrayList<String>> proceso, ArrayList<String> linea ) {
			proceso.accept( linea );
		}
	
}
