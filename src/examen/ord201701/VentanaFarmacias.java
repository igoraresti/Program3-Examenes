package examen.ord201701;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.*;

/** Ventana principal de aplicación de farmacias de guardia
 * @author andoni.eguiluz @ ingenieria.deusto.es
 */
@SuppressWarnings("serial")
public class VentanaFarmacias extends JFrame {
	private JTextArea taMensajes = new JTextArea( 10, 10 );
	private JTextArea taFarmaciasAbiertas = new JTextArea( 5, 40 );
	private JScrollPane spMensajes = new JScrollPane( taMensajes );
	private JScrollPane spFarmaciasAbiertas = new JScrollPane( taFarmaciasAbiertas );
	private JScrollBar sbVerticalAbiertas = spFarmaciasAbiertas.getVerticalScrollBar();
	private JLabel lReloj = new JLabel( "   :        " );
	private JTable jtableFarmacias = new JTable();
	private DefaultTableModel modeloDatosFarmacias = null;   // Modelo de datos para la tabla
	
	public VentanaFarmacias() {
		setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		JLabel lTexto1 = new JLabel( "Todas las farmacias", JLabel.CENTER  );
		JLabel lTexto2 = new JLabel( "Farmacias abiertas en Bilbao ahora", JLabel.CENTER );
		JPanel pMain = new JPanel( new BorderLayout() );
		JPanel pIzquierdo = new JPanel( new BorderLayout() );
		JPanel pFarmGuardia = new JPanel( new BorderLayout() );
		JPanel pSup = new JPanel( new BorderLayout() );
		JPanel pBotonera  = new JPanel( new FlowLayout() );
		pIzquierdo.add( pBotonera, BorderLayout.NORTH );
		pIzquierdo.add( spMensajes, BorderLayout.CENTER );
		pFarmGuardia.add( lTexto2, BorderLayout.NORTH );
		pFarmGuardia.add( spFarmaciasAbiertas, BorderLayout.CENTER );
		pSup.add( pIzquierdo, BorderLayout.CENTER );
		pBotonera.add( lReloj );
		pSup.add( lTexto1, BorderLayout.SOUTH );
		pMain.add( pSup, BorderLayout.NORTH );
		pMain.add( new JScrollPane(jtableFarmacias), BorderLayout.CENTER );
		add( pMain, BorderLayout.CENTER );
		add( pFarmGuardia, BorderLayout.EAST );
		
		taMensajes.setEditable( false );
		taFarmaciasAbiertas.setFocusable( false );
		setSize( 1024, 768 );
		Font fontTitulo = new Font( "Arial", Font.BOLD, 24 );
		Font fontTexto = new Font( "Arial", Font.PLAIN, 14 );
		Font fontReloj = new Font( "Courier", Font.BOLD, 20 );
		lTexto1.setFont( fontTitulo );
		lTexto2.setFont( fontTitulo );
		taMensajes.setFont( fontTexto );
		taFarmaciasAbiertas.setFont( fontTexto );
		lReloj.setFont( fontReloj );

		initFormatoTabla();
		addWindowListener( new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				cerrar();
			}
		});
		
		// Botones
		JButton jButton = new JButton( "Listado" );
		pBotonera.add( jButton );
		jButton.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clickListado();
			}
		});
		jButton = new JButton( "Capicúas" );
		pBotonera.add( jButton );
		jButton.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clickCapicuas();
			}
		});
		
		// Escuchador general de teclado  (de esta forma se escucha el teclado independientemente del componente que tenga el foco)
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher( new KeyEventDispatcher() {
			boolean ctrlPulsado = false;
			@Override
		    public boolean dispatchKeyEvent(KeyEvent e) {
		        if (e.getID() == KeyEvent.KEY_PRESSED) {
		        	if (e.getKeyCode() == KeyEvent.VK_CONTROL) ctrlPulsado = true;
		        	else if (ctrlPulsado) {
		        		if (e.getKeyCode() == KeyEvent.VK_F4) {   // Ctrl+F4 acaba
		        			VentanaFarmacias.this.dispose();
		        			cerrar();
		        		} else if (e.getKeyCode() == KeyEvent.VK_UP) {  // Ctrl+arriba (para test de hora +)
		        			horaTest++;
		        			actualizarVentana(); // Actualiza la ventana tras el cambio de hora
		        		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {  // Ctrl+abajo (para test de hora -)
		        			horaTest--;
		        			actualizarVentana(); // Actualiza la ventana tras el cambio de hora
		        		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {  // Ctrl+arriba (para test de minutos +)
		        			minsTest += 10;
		        			actualizarVentana(); // Actualiza la ventana tras el cambio de hora
		        		} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {  // Ctrl+arriba (para test de minutos -)
		        			minsTest -= 10;
		        			actualizarVentana(); // Actualiza la ventana tras el cambio de hora
		        		}
		        	}
		        } else if (e.getID() == KeyEvent.KEY_RELEASED) {
		        	if (e.getKeyCode() == KeyEvent.VK_CONTROL) ctrlPulsado = false;
		        }
		        return false;   // Con false el evento de teclado se reenvía (al componente que tiene el foco)
		    }
		});
		
		// TAREA 5 - Escuchador de click en reloj para manipulación directa
		// TODO
	}

		// Actualiza la ventana tras un cambio de hora
		private void actualizarVentana() {
			Main.actualizaFarmaciasEnPantalla();
			cargaFarmaciasEnTabla( null );
			actualizarReloj();
		}
	
	private void cerrar() {
		// TAREA 3
		// TODO
	}

	/** Click en el botón de "Listado"
	 */
	public void clickListado() {
		// TAREA 6
		// TODO
	}
	
	/** Click en el botón de "Capicúas"
	 */
	public void clickCapicuas() {
		ArrayList<FarmaciaGuardia> lCapicuas = new ArrayList<FarmaciaGuardia>();
		for (ArrayList<FarmaciaGuardia> l : mapaTodasFarmacias.getMapaFarmacias().values()) {
			for (FarmaciaGuardia f : l) {
				if (f.calcCapicua() < 5) {
					lCapicuas.add( f );
				}
			}
		}
		taMensajes.setText( "Teléfonos capicúas o casi:" );
		for (FarmaciaGuardia f : lCapicuas) {
			taMensajes.append( "\n" + f.calcCapicua() + "  -  " + f );
		}
	}
	
	/** Carga las farmacias actualmente abiertas en el área de texto de la derecha
	 * @param listado	String multilínea a meter en el área
	 */
	public void cargaFarmaciasAhora( String listado ) {
		listadoTemp = listado;
		lanzaEnEDT( cargaFarmaciasAhora2 );
	}
		private String listadoTemp;
		Runnable cargaFarmaciasAhora2 = new Runnable() { @Override public void run() {
			taFarmaciasAbiertas.setText( listadoTemp );
		}};

	/** Mueve el scroll del área de texto derecha de farmacias de guardia al principio
	 */
	public void inicioFarmaciasAhora() {
		lanzaEnEDT( inicioFA );
	}
		Runnable inicioFA = new Runnable() { @Override public void run() {
			sbVerticalAbiertas.setValue( 0 );
		}};
	
	/** Mueve el scroll del área de texto derecha de farmacias de guardia 2 pixels hacia abajo
	 * hasta que llega al final, en cuyo caso vuelve al principio
	 */
	public void mueveFarmaciasAhora() {
		lanzaEnEDT( mueveFA2 );
	}
		Runnable mueveFA2 = new Runnable() { @Override public void run() {
			int ant = sbVerticalAbiertas.getValue();
			sbVerticalAbiertas.setValue( ant+2 );
			if (sbVerticalAbiertas.getValue()==ant) {
				sbVerticalAbiertas.setValue( 0 );
			}
		}};
		
	/** Inicializa el formato de la jtable para que las filas de población / horario se vean en color cyan y negrita
	 */
	public void initFormatoTabla() {
		// A este método ya se le llama, una sola vez, en el constructor de la ventana
		// TAREA 4 - Inicializa el formato de jtableFarmacias para el color de las filas de cabecera
		// TODO
	}
		
	/** Carga las farmacias del mapa en la JTable de la ventana
	 * y hace que solo se puedan editar las celdas de dirección, y sus modificaciones se reflejen en el mapa
	 * @param mapa	Mapa de farmacias a visualizar. Si es null se utiliza (si existe) el último utilizado). Si no lo hay, no hace nada.
	 */
	public void cargaFarmaciasEnTabla( MapaFarmacias mapa ) {
		if (mapa==null) mapa = mapaTodasFarmacias;  // Memoriza y usa el mapa que se pasó la última vez 
		mapaTodasFarmacias = mapa;
		if (mapaTodasFarmacias==null) return; // Nada que hacer
		lanzaEnEDT( cargaFarmaciasTodas2 );
	}
		private MapaFarmacias mapaTodasFarmacias;
		Runnable cargaFarmaciasTodas2 = new Runnable() { 
			@Override public void run() {
				// A este método ya se le llama, cada vez que se carga un mapa de farmacias, desde el hilo de Swing
				// TAREA 4 - Crear modelo de datos de farmacias (modeloDatosFarmacias)
				// con tres columnas, y cabeceras "Lugar", "Hora-Dirección", "Teléfono".
				// Que la columna 1 sea la única editable (cuando la celda sea una dirección)
				// TAREA 4 - Volcar todos los datos de farmacias del mapa (mapaTodasFarmacias) 
				// en el modelo de datos de la tabla, modeloDatosFarmacias
				// TODO
		}};

	public void inicioFarmaciasTodas() {
		lanzaEnEDT( inicioFT );
	}
		Runnable inicioFT = new Runnable() { @Override public void run() {
			sbVerticalAbiertas.setValue( 0 );
		}};
	
		// Variables auxiliares para manipular la hora con el teclado
		public static int horaTest = 0;
		public static int minsTest = 0;
	public void actualizarReloj() {
		String sReloj = " " + FarmaciaGuardia.sdfHM.format( new Date( System.currentTimeMillis() + horaTest*3600000L + minsTest*60000L ) ) + " ";
		if ((System.currentTimeMillis() / 1000) % 2 == 0) sReloj = sReloj.replace( ":", " " );  // Cambia a segundos pares e impares los : por espacio para dar el feedback visual de que hay cambio en el reloj
		lReloj.setText( sReloj + "     " );
	}
	
	// Lanza un código (Runnable), asegurándose que se ejecuta desde el hilo de Swing
	private void lanzaEnEDT( Runnable r ) {
		if (SwingUtilities.isEventDispatchThread()) {
			r.run();
		} else {
			SwingUtilities.invokeLater( r );
		}
	}
	
}
