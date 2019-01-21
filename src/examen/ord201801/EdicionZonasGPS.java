package examen.ord201801;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import examen.ord201801.item.Arbol;

/** Clase principal ejecutable con ventana de visualización de zonas GPS. 
 * Inicializada para representar zonas ajardinadas de Erandio (más o menos realistas)
 * y una serie de árboles de Erandio (ficticios)
 * @author andoni.eguiluz @ ingenieria.deusto.es
 */
@SuppressWarnings("serial")
public class EdicionZonasGPS extends JFrame {
	// Pinceles generales con los que dibujar
	/** Pincel de 4 píxels de grosor */
	public static final Stroke stroke4 = new BasicStroke( 4.0f );
	/** Pincel de 2 píxels y medio de grosor */
	public static final Stroke stroke2m = new BasicStroke( 2.5f );
	/** Pincel de 1 píxel y medio de grosor */
	public static final Stroke stroke1m = new BasicStroke( 1.5f );

	// Ventana principal única creada por la ejecución de esta clase como principal
	private static EdicionZonasGPS ventana;
	
	/** Método principal de la clase. Crea y muestra una ventana de zonas de Erandio
	 * @param args	No utilizado
	 */
	public static void main(String[] args) {
		ventana = new EdicionZonasGPS();
		ventana.setVisible( true );
	}
	
	// Constantes de la ventana
	private static final int ANCH_MAX = 3000;   // Máxima anchura de dibujado del objeto gráfico
	private static final int ALT_MAX = 2000;    // Máxima altura de dibujado del objeto gráfico
	// Componentes gráficos de la ventana
	private JLabel lMensaje = new JLabel( " " );
	private JLabel lMensaje2 = new JLabel( " " );
	private ImageIcon mapa;
	private BufferedImage biImagen = new BufferedImage( ANCH_MAX, ALT_MAX, BufferedImage.TYPE_INT_RGB );
	private Graphics2D graphics;
	private JToggleButton tbMover = new JToggleButton( "Mover" );
	private JToggleButton tbZoom = new JToggleButton( "Zoom" );
	private JToggleButton tbGeoposicionar = new JToggleButton( "Geoposicionar" );
	private JButton bCentrar = new JButton( "Centrar" );
	private DefaultListModel<Zona> mZonas = new DefaultListModel<>();
	private JList<Zona> lZonas = new JList<Zona>( mZonas );
	private JPanel pDibujo;
	// Valores para el dibujado: escalado, desplazado, geolocalización
	private double zoomX = 1.0;         // A cuántos pixels de pantalla corresponden los pixels del gráfico en horizontal. 2.0 = aumentado. 0.5 = disminuido
	private double zoomY = 1.0;         // En vertical
	private double offsetX = 0.0;       // Qué píxel x de pantalla marca el origen del gráfico
	private double offsetY = 0.0;       // Qué píxel y de pantalla marca el origen del gráfico
	private double xImagen1, yImagen1;  // Punto 1 en la imagen asociado a una coordenada GPS
	private double longGPS1, latiGPS1;        // Coordenada GPS asociada al punto 1  (x = longitud, y = latitud)
	private double xImagen2, yImagen2;  // Punto 1 en la imagen asociado a una coordenada GPS
	private double longGPS2, latiGPS2;        // Coordenada GPS asociada al punto 1  (x = longitud, y = latitud)
	double relGPSaGrafX;                // Relación horizontal entre longitud (GPS) y x del gráfico
	double relGPSaGrafY;                // Relación vertical entre latitud (GPS) y x del gráfico
	double origenXGPSEnGraf;            // Pixel x virtual del gráfico en el que está el origen de longitud GPS
	double origenYGPSEnGraf;            // Pixel y virtual del gráfico en el que está el origen de latitud GPS
	boolean dibujadoArboles;            // Si es true se dibujan los árboles, si no no se dibujan
	
	/** Construye una ventana de dibujado y edición de zonas GPS
	 */
	public EdicionZonasGPS() {
		// Acciones generales de ventana
		setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		setSize( 1600, 1000 );
		setTitle( "Edición de zonas geoposicionadas (Erandio)" );
		// Creación de objetos gráficos
		mapa = new ImageIcon( this.getClass().getResource( "img/mapa-erandio.jpg" ) );
		graphics = (Graphics2D) biImagen.getGraphics();
		// Componentes y contenedores visuales y asignación de componentes a contenedores
		JPanel pSup = new JPanel();
		pSup.setLayout( new BorderLayout() );
		pSup.add( lMensaje, BorderLayout.WEST );
		pSup.add( lMensaje2, BorderLayout.EAST );
		add( pSup, BorderLayout.NORTH );
		JPanel p = new JPanel();
		tbZoom.setSelected( true );
		JCheckBox cbArboles = new JCheckBox( "Dibujar árboles" ); cbArboles.setSelected( false );
		p.add( cbArboles ); p.add( tbZoom ); p.add( tbMover ); p.add( tbGeoposicionar ); p.add( bCentrar );
		add( p, BorderLayout.SOUTH );
		pDibujo = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				g.drawImage( biImagen, 0, 0, null );
			}
		};
		add( pDibujo, BorderLayout.CENTER );
		// Lista de zonas de la derecha de la ventana
		Iterator<Zona> itZona = GrupoZonas.jardinesErandio.getIteradorZonas();
		while (itZona.hasNext()) {
			Zona zona = itZona.next();
			mZonas.addElement( zona );
		}
		add( new JScrollPane( lZonas ), BorderLayout.EAST );
		// Eventos de los distintos componentes
		lZonas.addListSelectionListener( new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				ArrayList<String> zonasErandio = GrupoZonas.jardinesErandio.getCodZonas();
				if (!e.getValueIsAdjusting() && lZonas.getSelectedIndex()>=0) {
					int val = lZonas.getSelectedIndex();
					Zona zona = GrupoZonas.jardinesErandio.getZona( zonasErandio.get(val) );
					calcMoverACentroZona( zona );
					calculaMapa();
					dibujaZona( val+1, zona, Color.CYAN );
					lMensaje.setText( "Zona " + (val+1) + " tiene " + zona.getNumSubzonas() + " subzonas y " + zona.getNumPuntos() + " puntos diferentes." );
				}
			}
		});
		lZonas.addMouseListener( new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (lZonas.getSelectedIndex()>=0) {
					int val = lZonas.getSelectedIndex();
					ArrayList<String> zonasErandio = GrupoZonas.jardinesErandio.getCodZonas();
					Zona zona = GrupoZonas.jardinesErandio.getZona( zonasErandio.get(val) );
					calcMoverACentroZona( zona );
					calculaMapa();
					dibujaZona( val+1, zona, Color.CYAN );
					lMensaje.setText( "Zona " + (val+1) + " tiene " + zona.getNumSubzonas() + " subzonas y " + zona.getNumPuntos() + " puntos diferentes." );
				}
			}
		});
		pDibujo.addMouseListener( new MouseAdapter() {
			Point pressed;
			@Override
			public void mouseReleased(MouseEvent e) {
				if (pressed.equals(e.getPoint())) {
					clickEn( e.getPoint(), e.getButton()==MouseEvent.BUTTON1 ); 
				} else {
					dragEn( pressed, e.getPoint(), e.getButton()==MouseEvent.BUTTON1 );
				}
			}
			@Override
			public void mousePressed(MouseEvent e) {
				pressed = e.getPoint();
			}
		});
		pDibujo.addMouseMotionListener( new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
				lMensaje2.setText( String.format( "Coordenadas GPS ratón: %1$.4f , %2$.4f", yPantallaAlatiGPS( e.getY() ), xPantallaAlongGPS( e.getX() ) ) );
			}
			@Override
			public void mouseDragged(MouseEvent e) {
			}
		});
		tbZoom.addActionListener( (e) -> { tbMover.setSelected( false ); tbGeoposicionar.setSelected( false ); } );
		tbMover.addActionListener( (e) -> { tbGeoposicionar.setSelected( false ); tbZoom.setSelected( false ); } );
		tbGeoposicionar.addActionListener( (e) -> { tbMover.setSelected( false ); tbZoom.setSelected( false ); } );
		bCentrar.addActionListener( (e) -> { calcMoverACentroImagen(); calculaMapa(); } );
		cbArboles.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dibujadoArboles = cbArboles.isSelected();
				calculaMapa();
			}
		});
		addWindowListener( new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				guardaProperties();
			}
		});
		// Carga final de propiedades de configuración (zoom y posición del mapa, referencias GPS, primer dibujado del mapa en ventana
		cargaProperties();
	}
	
		// Gestión de eventos de click de ratón, dependiendo del botón que esté seleccionado
		private boolean inicioGeopos = false;
		private void clickEn( Point p, boolean boton1 ) {
			if (tbZoom.isSelected()) {  // Click con zoom: si es botón izquierdo acerca, derecho aleja. Además entendemos que al hacer click se hace una traslación, por eso se duplica el código de traslación
				double zoom = 1 / 1.5; if (boton1) zoom = 1.5;  // Botón izquierdo aumenta, derecho u otro disminuye
				zoomX *= zoom; zoomY *= zoom;
				calcMoverCentroAlPunto( p.x, p.y );
				calcZoomManteniendoCentro( zoom );
				calculaMapa();
				lMensaje.setText( "Zoom actual: " + String.format( "%.2f", zoomX ) + "%" );
			} else if (tbMover.isSelected()) {
				calcMoverCentroAlPunto( p.x, p.y );
				lMensaje.setText( "Movido el mapa al centro indicado." );
				calculaMapa();
			} else if (tbGeoposicionar.isSelected()) {  // Primer click de un geoposicionamiento
				lMensaje.setText( "Introducida coordenada de posicionamiento. Introduce GPS correspondiente..." );
				String dato = JOptionPane.showInputDialog( ventana, "Introduce coordenada GPS (latitud,longitud):", "Geolocalización", JOptionPane.QUESTION_MESSAGE );
				if (!inicioGeopos) {
					inicioGeopos = true;
					calcGeoposicion( p.x, p.y, dato, 1 );
				} else {
					inicioGeopos = false;
					calcGeoposicion( p.x, p.y, dato, 2 );
				}
			}
		}
		
		// Gestión de eventos de drag de ratón, dependiendo del botón que esté seleccionado
		private void dragEn( Point p1, Point p2, boolean boton1  ) {
			if (tbZoom.isSelected()) {  // Zoom sobre drag tiene dos partes: mover al centro y escalar al rectángulo del drag (si no es demasiado pequeño)
				double anchoDrag = Math.abs( p1.getX() - p2.getX() );
				double altoDrag = Math.abs( p1.getY() - p2.getY() );
				if (altoDrag < 10 || anchoDrag < 10) return;  // Si alguna dimensión del drag es menor de 10 píxels no se hace nada (para evitar zooms demasiado grandes o pequeños)
				double relAnchura = pDibujo.getWidth() / anchoDrag;
				double relAltura = pDibujo.getHeight() / altoDrag;
				double relZoom = Math.min( relAnchura, relAltura );  // No queremos cambiar la rel de aspecto con lo cual nos limitamos a la menos cambiada de las dos dimensiones
				if (!boton1) relZoom = 1 / relZoom;  // Si el botón no es el izquierdo se reduce el zoom en lugar de ampliar
				int medioX = (p1.x + p2.x) / 2;
				int medioY = (p1.y + p2.y) / 2;
				zoomX *= relZoom; zoomY *= relZoom;
				calcMoverCentroAlPunto( medioX, medioY );
				calcZoomManteniendoCentro( relZoom );
				calculaMapa();
				lMensaje.setText( "Zoom actual: " + zoomX );
			} else if (tbMover.isSelected()) {
				calcMoverRelativo( p2.x - p1.x, p2.y - p1.y );
				calculaMapa();
				lMensaje.setText( "Movido el mapa según el vector indicado." );
			} else if (tbGeoposicionar.isSelected()) {
				// Nada
			}
		}
		
		// Rutinas de recálculo de zoom
		private void calcZoomManteniendoCentro( double relZoom ) {
			offsetX = pDibujo.getWidth()/2 - relZoom * (pDibujo.getWidth()/2 - offsetX);
			offsetY = pDibujo.getHeight()/2 - relZoom * (pDibujo.getHeight()/2 - offsetY);
			if (zoomX > 20.001) { double relZ2 = 20.0/zoomX; zoomX = 20.0; zoomY = 20.0; calcZoomManteniendoCentro( relZ2 ); }
			else if (zoomX < 0.099) { double relZ2 = 0.10/zoomX; zoomX = 0.10; zoomY = 0.10; calcZoomManteniendoCentro( relZ2 ); }
		}
		// Rutinas de recálculo de movimiento
		private void calcMoverRelativo( int difX, int difY ) {
			offsetX += difX;
			offsetY += difY;
		}
		private void calcMoverCentroAlPunto( int x, int y ) {
			offsetX += (pDibujo.getWidth()/2 - x);  // Mover al centro
			offsetY += (pDibujo.getHeight()/2 - y);
		}
		private void calcMoverACentroImagen() {
			offsetX = pDibujo.getWidth()/2 - mapa.getIconWidth() * 0.5 * zoomX;
			offsetY = pDibujo.getHeight()/2 - mapa.getIconHeight() * 0.5 * zoomY;
		}
		// Mueve el centro de la pantalla a la media aritmética de los puntos de la zona
		private void calcMoverACentroZona( Zona zona ) {
			double sumaLong = 0; double sumaLati = 0;
			for (ArrayList<PuntoGPS> lp : zona.getPuntosGPS())
				for (int i=0; i<lp.size()-1; i++) {  // No se coge el último que repite siempre al primero (cierra la subzona)
					PuntoGPS p = lp.get(i);
					sumaLong += p.getLongitud();
					sumaLati += p.getLatitud();
				}
			sumaLati /= zona.getNumPuntos();  sumaLong /= zona.getNumPuntos();  // Medias aritméticas de los puntos de la zona
			int medioX = xGraficoAPantalla( longGPSaXGrafico( sumaLong ) );
			int medioY = yGraficoAPantalla( latiGPSaYGrafico( sumaLati ) );
			calcMoverCentroAlPunto( medioX, medioY );
		}
	
	// Calcula y redibuja todo el mapa visual en la ventana
	private void calculaMapa() {
		graphics.setColor( Color.BLACK );
		graphics.fillRect( 0, 0, ANCH_MAX, ALT_MAX );
		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR); // Configuración para mejor calidad del gráfico escalado
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);	
		graphics.drawImage( mapa.getImage(), 
				(int) Math.round(offsetX), (int) Math.round(offsetY), 
				(int) Math.round(mapa.getIconWidth()*zoomX), (int) Math.round(mapa.getIconHeight()*zoomY), null );
		dibujaZonas();
		if (pDibujo!=null) pDibujo.repaint();
	}
	
	// Relaciona un punto de pantalla (x,y) a una coordenada GPS (gps) latitud,longitud. Si hay dos puntos correctos, recalcula la relación del gráfico con el geoposicionamiento y redibuja
	private void calcGeoposicion( int x, int y, String gps, int numPunto ) {
		if (gps==null || gps.isEmpty()) { lMensaje.setText( "Introducción GPS incorrecta." ); inicioGeopos = false; return; }
		double grafX = xPantallaAGrafico( x );
		double grafY = yPantallaAGrafico( y );
		try {
			int coma = gps.indexOf( ',' );
			double lati = Double.parseDouble( gps.substring( 0, coma ) );
			double longi = Double.parseDouble( gps.substring( coma+1 ) );
			if (numPunto==1) {
				xImagen1 = grafX;
				yImagen1 = grafY;
				longGPS1 = longi;
				latiGPS1 = lati;
			} else {  // punto 2
				xImagen2 = grafX;
				yImagen2 = grafY;
				longGPS2 = longi;
				latiGPS2 = lati;
			}
			calcGPSBase();
			calculaMapa();
		} catch (Exception e) {
		}
	}

	// Dibuja las zonas de Erandio y los árboles (si el checkbox lo indica) en el objeto gráfico de pantalla
	private void dibujaZonas() {
		if (longGPS1!=0.0) {  // Marca de asociación GPS de punto 1
			int x = xGraficoAPantalla( xImagen1 );
			int y = yGraficoAPantalla( yImagen1 );
			graphics.setColor( Color.BLACK );
			graphics.setStroke( stroke4 );
			graphics.drawOval( x-6, y-6, 12, 12 );
			if (longGPS2!=0.0) {  // Marca de asociación GPS de punto 2 - si no está no se puede correlacionar GPS con el gráfico y por tanto no se dibujan zonas ni puntos
				int x2 = xGraficoAPantalla( xImagen2 );
				int y2 = yGraficoAPantalla( yImagen2 );
				graphics.setColor( Color.RED );
				graphics.setStroke( stroke4 );
				graphics.drawOval( x2-6, y2-6, 12, 12 );
				int nZ = 1;
				graphics.setStroke( stroke2m );
				Iterator<Zona> itZona = GrupoZonas.jardinesErandio.getIteradorZonas();
				while (itZona.hasNext()) {
					Zona zona = itZona.next();
					dibujaZona( nZ, zona );
					nZ++;
				}
				if (dibujadoArboles) {
					for (Arbol arbol : GrupoZonas.arbolesErandio) {
						arbol.dibuja( this, false );
					}
				}
			}
		}
	}
	
	// Dibuja las zona indicada con líneas y puntos, en el color indicado (si no se indica, se alterna magenta y azul), en el objeto gráfico de pantalla
	private void dibujaZona( int numZona, Zona zona, Color... colorOpcional ) {
		if (zona.getNumSubzonas()>0) {
			if (numZona%2==0) graphics.setColor( Color.BLUE ); else graphics.setColor( Color.MAGENTA );
			if (colorOpcional.length>0) { graphics.setColor( colorOpcional[0] ); graphics.setStroke( stroke4 ); }
			Color color = graphics.getColor();
			Stroke stroke = graphics.getStroke();
			for (ArrayList<PuntoGPS> subzona : zona.getPuntosGPS()) {
				PuntoGPS p1 = subzona.get(0);
				for (int i=1; i<subzona.size(); i++) {
					PuntoGPS p2 = subzona.get(i);
					dibujaLinea( p1.getLongitud(), p1.getLatitud(), p2.getLongitud(), p2.getLatitud(), null, null, false );
					dibujaCirculo( p1.getLongitud(), p1.getLatitud(), 3, Color.YELLOW, stroke1m, false );  // Punto amarillo en cada vértice de la zona
					graphics.setColor( color );
					graphics.setStroke( stroke );
					p1 = p2;
				}
				dibujaCirculo( p1.getLongitud(), p1.getLatitud(), 4, null, null, false );  // Punto gordo para el inicial de la lista de puntos
			}
		}
	}
	
	/** Dibuja un círculo en el mapa gráfico de la ventana
	 * @param longitud	Coordenada longitud (horizontal) donde dibujar el centro del círculo
	 * @param latitud  Coordenada latitud (vertical) donde dibujar el centro del círculo
	 * @param radio	Píxels de pantalla de radio del círculo a dibujar
	 * @param color	Color del círculo (si es null utiliza el color actual de dibujado)
	 * @param stroke Pincel de dibujado del círculo (grosor) (si es null utiliza el stroke actual de dibujado)
	 * @param pintaEnVentana	true si se quiere pintar inmediatamente en el mapa, false si se pinta en el objeto gráfico pero no se muestra aún en pantalla
	 */
	public void dibujaCirculo( double longitud, double latitud, double radio, Color color, Stroke stroke, boolean pintaEnVentana ) {
		if (color!=null) graphics.setColor( color );
		if (stroke!=null) graphics.setStroke( stroke );
		graphics.drawOval( (int) Math.round( longGPSaXPantalla( longitud ) - radio), (int) Math.round( latiGPSaYPantalla( latitud ) - radio),
				(int) Math.round(radio*2), (int) Math.round(radio*2) );
		if (pintaEnVentana && pDibujo!=null) pDibujo.repaint();
	}

	/** Dibuja una línea en el mapa gráfico de la ventana
	 * @param longitud1	Coordenada longitud (horizontal) del punto inicial
	 * @param latitud1  Coordenada latitud (vertical) del punto inicial
	 * @param longitud2	Coordenada longitud (horizontal) del punto inicial
	 * @param latitud2  Coordenada latitud (vertical) del punto inicial
	 * @param color	Color de la línea (si es null utiliza el color actual de dibujado)
	 * @param stroke Pincel de dibujado de la línea (grosor) (si es null utiliza el stroke actual de dibujado)
	 * @param pintaEnVentana	true si se quiere pintar inmediatamente en el mapa, false si se pinta en el objeto gráfico pero no se muestra aún en pantalla
	 */
	public void dibujaLinea( double longitud1, double latitud1, double longitud2, double latitud2, Color color, Stroke stroke, boolean pintaEnVentana ) {
		if (color!=null) graphics.setColor( color );
		if (stroke!=null) graphics.setStroke( stroke );
		graphics.drawLine( (int) Math.round(longGPSaXPantalla( longitud1 )), (int) Math.round(latiGPSaYPantalla(latitud1)),
				(int) Math.round(longGPSaXPantalla( longitud2 )), (int) Math.round(latiGPSaYPantalla(latitud2)) );
		if (pintaEnVentana && pDibujo!=null) pDibujo.repaint();
	}
	
	
	// Métodos de conversión entre x,y de pantalla,  x,y de píxels del gráfico de fondo (mapa),  long,lati de la coordenada GPS equivalente
	private double xPantallaAGrafico( double x ) { return (x - offsetX) / zoomX; }
	private double yPantallaAGrafico( double y ) { return (y - offsetY) / zoomY; }
	private double xPantallaAlongGPS( double x ) { return xGraficoAlongGPS( xPantallaAGrafico( x ) ); }
	private double yPantallaAlatiGPS( double y ) { return yGraficoAlatiGPS( yPantallaAGrafico( y ) ); }
	private int xGraficoAPantalla( double x ) { return (int) Math.round( zoomX * x + offsetX ); }
	private int yGraficoAPantalla( double y ) { return (int) Math.round( zoomY * y + offsetY ); }
	private double longGPSaXGrafico( double longi ) {
		return origenXGPSEnGraf + longi*relGPSaGrafX;
	}
	private int longGPSaXPantalla( double longitud ) { return xGraficoAPantalla( longGPSaXGrafico( longitud ) ); }
	private double latiGPSaYGrafico( double lati ) {
		return origenYGPSEnGraf + lati*relGPSaGrafY;
	}
	private int latiGPSaYPantalla( double latitud ) { return yGraficoAPantalla( latiGPSaYGrafico( latitud ) ); }
	private double xGraficoAlongGPS( double x ) {
		return (x - origenXGPSEnGraf) / relGPSaGrafX;
	}
	private double yGraficoAlatiGPS( double y ) {
		return (y - origenYGPSEnGraf) / relGPSaGrafY;
	}
	

	// Método de cálculo de los coeficientes de referencia para convertir coordenadas de pantalla y mapa en GPS georeferenciadas
	// (dependiendo de dos puntos suministrados de forma explícita para interpolar partiendo de ellos)
	private void calcGPSBase() {
		if (longGPS1!=0.0 && longGPS2!=0.0) {  // Marca de asociación realizada en los dos puntos GPS
			double difX = xImagen2 - xImagen1;
			double difY = yImagen2 - yImagen1;
			double difLong = longGPS2 - longGPS1;
			double difLati = latiGPS2 - latiGPS1;
			relGPSaGrafX = difX / difLong;
			relGPSaGrafY = difY / difLati;
			origenXGPSEnGraf = xImagen1 - longGPS1 * relGPSaGrafX;
			origenYGPSEnGraf = yImagen1 - latiGPS1 * relGPSaGrafY;
			// System.out.println( "Origen GPS en el gráfico: " + origenXGPSEnGraf + " , " + origenYGPSEnGraf );
			// System.out.println( "Escala de imagen en graf: " + relGPSaGrafX  + " , " + relGPSaGrafY );
		}
	}

	// Atributo de propiedades de configuración
	private Properties properties;
	/**	Carga los valores de configuración: zoom y posición del mapa, referencias GPS
	 * Si el fichero no existe, los inicializa con valores por defecto
	 * Hace el cálculo de cooordenadas GPS y realiza el primer dibujado del mapa en ventana
	 */
	private void cargaProperties() {
		properties = new Properties();
		try {
			properties.loadFromXML( new FileInputStream( "utmgps.ini" ) );
			xImagen1 = Double.parseDouble( properties.getProperty( "xImagen1" ) );
			yImagen1 = Double.parseDouble( properties.getProperty( "yImagen1" ) );
			xImagen2 = Double.parseDouble( properties.getProperty( "xImagen2" ) );
			yImagen2 = Double.parseDouble( properties.getProperty( "yImagen2" ) );
			longGPS1 = Double.parseDouble( properties.getProperty( "longGPS1" ) );
			latiGPS1 = Double.parseDouble( properties.getProperty( "latiGPS1" ) );
			longGPS2 = Double.parseDouble( properties.getProperty( "longGPS2" ) );
			latiGPS2 = Double.parseDouble( properties.getProperty( "latiGPS2" ) );
			offsetX = Double.parseDouble( properties.getProperty( "offsetX" ) );
			offsetY = Double.parseDouble( properties.getProperty( "offsetY" ) );
			zoomX = Double.parseDouble( properties.getProperty( "zoomX" ) );
			zoomY = Double.parseDouble( properties.getProperty( "zoomY" ) );
		} catch (Exception e) {  // Fichero no existe o error en algún dato
			// longGPS2 = 0.0;  // Marcaría que no hay asociación GPS
			// TODO - Esto habría que cambiarlo si el mapa fuera otro. Está configurado para el mapa y los valores de ejemplo de Erandio (ver GrupoZonas)
			longGPS1 = -2.98926;
			longGPS2 = -2.953024;
			latiGPS1 = 43.313283;
			latiGPS2 = 43.294753;
			xImagen1 = 130.16049382715826;
			xImagen2 = 1264.0216049382686;
			yImagen1 = 1601.4228395061725;
			yImagen2 = 2394.3672839506157;
		}
		calcGPSBase();
		calculaMapa();
		System.out.println( "Coordenada GPS correspondiente a la esquina superior izquierda del gráfico: " + yGraficoAlatiGPS(0) + " , " + xGraficoAlongGPS(0) );
		System.out.println( "Coordenada GPS correspondiente a la esquina inferior derecha del gráfico: " + yGraficoAlatiGPS(mapa.getIconHeight()) + " , " + xGraficoAlongGPS(mapa.getIconWidth()) );
	}
	
	/**	Guarda en fichero los valores de configuración
	 */
	private void guardaProperties() {
		properties.setProperty( "xImagen1", ""+xImagen1 );
		properties.setProperty( "yImagen1", ""+yImagen1 );
		properties.setProperty( "xImagen2", ""+xImagen2 );
		properties.setProperty( "yImagen2", ""+yImagen2 );
		properties.setProperty( "longGPS1", ""+longGPS1 );
		properties.setProperty( "latiGPS1", ""+latiGPS1 );
		properties.setProperty( "longGPS2", ""+longGPS2 );
		properties.setProperty( "latiGPS2", ""+latiGPS2 );
		properties.setProperty( "offsetX", ""+offsetX );
		properties.setProperty( "offsetY", ""+offsetY );
		properties.setProperty( "zoomX", ""+zoomX );
		properties.setProperty( "zoomY", ""+zoomY );
		try {
			properties.storeToXML( new FileOutputStream( "utmgps.ini" ), "datos de RevisionZonasUTMGPS" );
		} catch (Exception e) {
		}
	}
	
}
