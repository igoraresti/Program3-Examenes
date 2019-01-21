package examenenes.parc201805.iu;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import examenenes.parc201805.datos.*;

/** Ventana para el IU principal de la gesti�n de ligas deportivas
 */
public class VentanaLiga extends JFrame {
	// Atributos gr�ficos
	JComboBox<Liga> cbLiga;
	JComboBox<Equipo> cbEquipo;
	JComboBox<String> cbJornada;
	JTextArea taDatos;
	// Datos para la gesti�n de la ventana
	private ArrayList<Liga> ligas; // lista de ligas gestionadas
	private Liga ligaSel; // liga actualmente seleccionada
	
	public VentanaLiga( ArrayList<Liga> ligas ) {
		// Configuraci�n de ventana
		setTitle( "Examen mayo 2018" );
		setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		setSize( 800, 400 );
		setLocationRelativeTo( null );  // La hace relativa al escritorio (se centrar� en pantalla al visualizarse)
		// Creaci�n de componentes y contenedores
		cbLiga = new JComboBox<>();
		cbEquipo = new JComboBox<>();
		cbJornada = new JComboBox<>();
		taDatos = new JTextArea( 5, 30 );
		JPanel pSuperior = new JPanel();
		pSuperior.setLayout( new BoxLayout( pSuperior, BoxLayout.Y_AXIS ) );
		JPanel pSup1 = new JPanel();
		JPanel pSup2 = new JPanel();
		JPanel pSup3 = new JPanel();
		JPanel pBotonera = new JPanel();
		JButton bClasif = new JButton( "Clasificaci�n" );
		JButton bTarea1 = new JButton( "T1" );
		JButton bTarea2 = new JButton( "T2" );
		JButton bTarea3 = new JButton( "T3" );
		JButton bTarea4 = new JButton( "T4" );
		// Asignaci�n de componentes a contenedores
		pSup1.add( new JLabel( "Selecciona liga:" ) );
		pSup1.add( cbLiga );
		pSup2.add( new JLabel( "Selecciona equipo:" ) );
		pSup2.add( cbEquipo );
		pSup3.add( new JLabel( "Selecciona jornada:" ) );
		pSup3.add( cbJornada );
		pSuperior.add( pSup1 );
		pSuperior.add( pSup2 );
		pSuperior.add( pSup3 );
		pBotonera.add( bClasif );
		pBotonera.add( bTarea1 );
		pBotonera.add( bTarea2 );
		pBotonera.add( bTarea3 );
		pBotonera.add( bTarea4 );
		getContentPane().add( pSuperior, BorderLayout.NORTH );
		getContentPane().add( new JScrollPane(taDatos), BorderLayout.WEST );
		getContentPane().add( pBotonera, BorderLayout.SOUTH );
		// Eventos de componentes
		cbLiga.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				eventoSeleccionDeLiga();
			}
		});
		cbEquipo.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				eventoSeleccionDeEquipo();
			}
		});
		cbJornada.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				eventoSeleccionDeJornada();
			}
		});
		bClasif.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (ligaSel!=null) {
					ArrayList<Equipo> clasif = ligaSel.calculaClasificacion();
					taDatos.setText( "Clasificaci�n:" );
					for (Equipo equipo : clasif )
						taDatos.append( "\n" + equipo.getNombre() + " Pt=" + equipo.getPuntos() + " " + equipo.getDeporte() );
				}
			}
		});
		// Inicializaci�n de datos de ligas
		setLigas( ligas );
	}
	
	//
	// M�todos de gesti�n de eventos
	//
	
	// Selecci�n de liga. Actualiza los datos de equipos y de jornadas de los otros combos
	private void eventoSeleccionDeLiga() {
		DefaultComboBoxModel<Equipo> datosEquipos = new DefaultComboBoxModel<>();
		DefaultComboBoxModel<String> datosJornadas = new DefaultComboBoxModel<>();
		ligaSel = (Liga) cbLiga.getSelectedItem(); // Actualiza la liga seleccionada
		if (ligaSel!=null) {
			// Carga lista de equipos para la ventana
			for (String codEquipo : ligaSel.getEquipos()) {
				Equipo equipo = ligaSel.buscaEquipo( codEquipo );
				datosEquipos.addElement( equipo );
			}
			// Carga lista de jornadas para la ventana
			int numJornadas = ligaSel.getJornadas();
			for (int jornada=1; jornada<=numJornadas; jornada++) {
				datosJornadas.addElement( "Jornada " + jornada );
			}
		}
		cbEquipo.setModel( datosEquipos );
		cbJornada.setModel( datosJornadas );
		cbEquipo.setSelectedIndex( -1 ); // Desactiva selecci�n de equipo en el combo (tiene que ser expl�cita por el usuario)
		cbJornada.setSelectedIndex( -1 ); // Desactiva selecci�n de jornada en el combo (tiene que ser expl�cita por el usuario)
	}
	
	// Selecci�n de equipo en el combo.
	private void eventoSeleccionDeEquipo() {
		taDatos.setText( "Equipo seleccionado: " + cbEquipo.getSelectedItem() );  // Visualiza el equipo seleccionado
	}
	
	// Selecci�n de jornada en el combo.
	private void eventoSeleccionDeJornada() {
		String jornadaSel = (String) cbJornada.getSelectedItem();
		if (jornadaSel!=null && ligaSel!=null) {
			int posEspacio = jornadaSel.indexOf( " " ); // Busca el espacio - en el string "Jornada x"
			int numJornada = Integer.parseInt( jornadaSel.substring( posEspacio+1, jornadaSel.length() ) );
			taDatos.setText( "Jornada " + numJornada + ":" );
			ArrayList<Partido> partidos = ligaSel.getPartidos( numJornada );
			for (Partido partido : partidos) {
				taDatos.append( "\n" + partido );
			}
		}
	}

	//
	// M�todos p�blicos de la ventana
	// 
	
	/** Asigna la lista de ligas que va a gestionar la ventana. La ventana se reinicia de acuerdo a los datos nuevos
	 * @param ligas	Lista de ligas a gestionar
	 */
	public void setLigas(ArrayList<Liga> ligas) {
		this.ligas = ligas;
		cbLiga.setModel( new DefaultComboBoxModel<Liga>( (Liga[]) this.ligas.toArray( new Liga[0] ) ));
		if (ligas.size()>0) {
			cbLiga.setSelectedIndex( 0 );  // Selecciona la primera liga si hay alguna
		}
	}
	
	
}
