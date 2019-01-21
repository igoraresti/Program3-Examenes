package examenenes.parc201711.utils;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;

/** Clase ventana sencilla para dibujado
 */
public class VentanaGrafica {
	private JFrame ventana;       // Ventana que se visualiza
	private boolean cerrada;      // L�gica de cierre (false al inicio)
	private JPanel panel;         // Panel principal
	private JLabel lMens;         // Etiqueta de texto de mensajes en la parte inferior
	private BufferedImage buffer; // Buffer gr�fico de la ventana
	private Graphics2D graphics;  // Objeto gr�fico sobre el que dibujar (del buffer)
	private Point pointPressed;   // Coordenada pulsada de rat�n (si existe)
	private Point pointMoved;     // Coordenada pasada de rat�n (si existe)
	private Point pointMovedPrev; // Coordenada pasada anterior de rat�n (si existe)
	private boolean dibujadoInmediato = true; // Refresco de dibujado en cada orden de dibujado

		private Object lock = new Object();  // Tema de sincronizaci�n de hilos para el acceso como si no los hubiera
	
	/** Construye una nueva ventana gr�fica con fondo blanco y la visualiza en el centro de la pantalla
	 * @param anchura	Anchura en p�xels (valor positivo)
	 * @param altura	Altura en p�xels (valor positivo)
	 * @param titulo	T�tulo de la ventana
	 */
	@SuppressWarnings("serial")
	public VentanaGrafica( int anchura, int altura, String titulo ) {
		cerrada = false;
		ventana = new JFrame( titulo );
		ventana.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		ventana.setSize( anchura, altura );
		ventana.setLocationRelativeTo( null );
		buffer = new BufferedImage( 2000, 1500, BufferedImage.TYPE_INT_ARGB );
		graphics = buffer.createGraphics();
		graphics.setPaint ( Color.white );
		graphics.fillRect ( 0, 0, 2000, 1500 );
		panel = new JPanel() {
			{
				setLayout( new BorderLayout() );
			}
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				((Graphics2D)g).drawImage( buffer, null, 0, 0 );
			}
		};
		lMens = new JLabel( " " );
		ventana.getContentPane().add( panel, BorderLayout.CENTER );
		ventana.getContentPane().add( lMens, BorderLayout.SOUTH );
		ventana.addWindowListener( new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				cerrada = true;
			}
		});
		panel.addMouseListener( new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				synchronized (lock) {
					pointPressed = null;
				}
			}
			@Override
			public void mousePressed(MouseEvent e) {
				synchronized (lock) {
					pointPressed = e.getPoint();
				}
			}
		});
		panel.addMouseMotionListener( new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
				synchronized (lock) {
					pointMoved = e.getPoint();
				}
			}
			@Override
			public void mouseDragged(MouseEvent e) {
				synchronized (lock) {
					pointPressed = e.getPoint();
				}
			}
		});
		try {
			SwingUtilities.invokeAndWait( new Runnable() {
				@Override
				public void run() {
					ventana.setVisible( true );
				}
			});
		} catch (InvocationTargetException | InterruptedException e1) {
			e1.printStackTrace();
		}
	}
	/** Espera un tiempo y sigue
	 * @param milis	Milisegundos a esperar
	 */
	public void espera( long milis ) {
		try {
			Thread.sleep( milis );
		} catch (InterruptedException e) {
		}
	}
	/** Espera hasta que ocurra un click completo de rat�n
	 */
	public void esperaAClick() {
		while (getRatonPulsado()==null && !estaCerrada()) {}  // Espera a pulsaci�n...
		while (getRatonPulsado()!=null && !estaCerrada()) {}  // ...y espera a suelta
	}
	/** Cierra la ventana (tambi�n ocurre cuando se pulsa el icono de cierre)
	 */
	public void acaba() {
		ventana.dispose();
		cerrada = true;
	}
	
	/** Consultor de estado de visibilidad de la ventana
	 * @return	false si sigue activa, true si ya se ha cerrado
	 */
	public boolean estaCerrada() {
		return cerrada;
	}
	
	/** Devuelve el punto donde est� el rat�n pulsado en este momento
	 * @return	Punto relativo a la ventana, null si el rat�n no est� siendo pulsado
	 */
	public Point getRatonPulsado() {
		synchronized (lock) {
			return pointPressed;
		}
	}
	
	/** Devuelve el punto donde est� el rat�n en este momento
	 * @return	Punto relativo a la ventana, null si el rat�n no se ha movido nunca
	 */
	public Point getRatonMovido() {
		synchronized (lock) {
			return pointMoved;
		}
	}
	
	/** Devuelve el �ltimo vector de movimiento del rat�n (desde la �ltima vez que se llam� a este mismo m�todo)
	 * @return	Punto relativo a la ventana, null si el rat�n no se ha movido nunca
	 */
	public Point getVectorRatonMovido() {
		synchronized (lock) {
			Point ret = new Point( 0, 0 );
			if (pointMovedPrev!=null) {
				ret.setLocation( pointMoved.getX()-pointMovedPrev.getX(), pointMoved.getY()- pointMovedPrev.getY() );
			}
			pointMovedPrev = pointMoved;
			return ret;
		}
	}	
	
	/** Cambia el mensaje de la ventana (l�nea inferior de mensajes)
	 * @param mensaje	Texto de mensaje
	 */
	public void setMensaje( String mensaje ) {
		if (mensaje==null || mensaje.isEmpty())
			lMens.setText( " " );
		else
			lMens.setText( mensaje );
	}
	
	/** Devuelve la altura del panel de dibujo de la ventana
	 * @return	Altura del panel principal (�ltima coordenada y) en p�xels
	 */
	public int getAltura() {
		return panel.getHeight()-1;
	}
	
	/** Devuelve la anchura del panel de dibujo de la ventana
	 * @return	Anchura del panel principal (�ltima coordenada x) en p�xels
	 */
	public int getAnchura() {
		return panel.getWidth()-1;
	}
	
	/** Borra toda la ventana (pinta de color blanco)
	 */
	public void borra() {
		graphics.setColor( Color.white );
		graphics.fillRect( 0, 0, panel.getWidth()+2, panel.getHeight()+2 );
		if (dibujadoInmediato) panel.repaint();
	}
	
	/** Dibuja un rect�ngulo en la ventana
	 * @param rectangulo	Rect�ngulo a dibujar
	 * @param grosor	Grueso de la l�nea del rect�ngulo (en p�xels)
	 * @param color  	Color del rect�ngulo
	 */
	public void dibujaRect( Rectangle rectangulo, float grosor, Color color ) {
		dibujaRect( rectangulo.getX(), rectangulo.getY(), rectangulo.getX()+rectangulo.getWidth(), rectangulo.getY()+rectangulo.getHeight(), grosor, color );
	}
	
	/** Dibuja un rect�ngulo en la ventana
	 * @param x	Coordenada x de la esquina superior izquierda del rect�ngulo
	 * @param y	Coordenada y de la esquina superior izquierda del rect�ngulo
	 * @param anchura	Anchura del rect�ngulo (en p�xels) 
	 * @param altura	Altura del rect�ngulo (en p�xels)
	 * @param grosor	Grueso del rect�ngulo (en p�xels)
	 * @param color  	Color del rect�ngulo
	 */
	public void dibujaRect( double x, double y, double anchura, double altura, float grosor, Color color ) {
		graphics.setColor( color );
		graphics.setStroke( new BasicStroke( grosor ));
		graphics.drawRect( (int)Math.round(x), (int)Math.round(y), (int)Math.round(anchura), (int)Math.round(altura) );
		if (dibujadoInmediato) panel.repaint();
	}
	
	/** Dibuja un rect�ngulo relleno en la ventana
	 * @param x	Coordenada x de la esquina superior izquierda del rect�ngulo
	 * @param y	Coordenada y de la esquina superior izquierda del rect�ngulo
	 * @param anchura	Anchura del rect�ngulo (en p�xels) 
	 * @param altura	Altura del rect�ngulo (en p�xels)
	 * @param grosor	Grueso del rect�ngulo (en p�xels)
	 * @param color  	Color de la l�nea del rect�ngulo
	 * @param colorRell	Color del relleno del rect�ngulo
	 */
	public void dibujaRect( double x, double y, double anchura, double altura, float grosor, Color color, Color colorRell ) {
		graphics.setColor( colorRell );
		graphics.setStroke( new BasicStroke( grosor ));
		graphics.fillRect( (int)Math.round(x), (int)Math.round(y), (int)Math.round(anchura), (int)Math.round(altura) );
		graphics.setColor( color );
		graphics.drawRect( (int)Math.round(x), (int)Math.round(y), (int)Math.round(anchura), (int)Math.round(altura) );
		if (dibujadoInmediato) panel.repaint();
	}
	
	/** Dibuja un rect�ngulo azul en la ventana
	 * @param x	Coordenada x de la esquina superior izquierda del rect�ngulo
	 * @param y	Coordenada y de la esquina superior izquierda del rect�ngulo
	 * @param anchura	Anchura del rect�ngulo (en p�xels) 
	 * @param altura	Altura del rect�ngulo (en p�xels)
	 * @param grosor	Grueso del rect�ngulo (en p�xels)
	 */
	public void dibujaRect( double x, double y, double anchura, double altura, float grosor ) {
		dibujaRect( x, y, anchura, altura, grosor, Color.blue );
	}
	
	/** Borra un rect�ngulo en la ventana
	 * @param x	Coordenada x de la esquina superior izquierda del rect�ngulo
	 * @param y	Coordenada y de la esquina superior izquierda del rect�ngulo
	 * @param anchura	Anchura del rect�ngulo (en p�xels) 
	 * @param altura	Altura del rect�ngulo (en p�xels)
	 * @param grosor	Grueso del rect�ngulo (en p�xels)
	 */
	public void borraRect( double x, double y, double anchura, double altura, float grosor ) {
		dibujaRect( x, y, anchura, altura, grosor, Color.white );
	}
	
	/** Dibuja un c�rculo en la ventana
	 * @param x	Coordenada x del centro del c�rculo
	 * @param y	Coordenada y del centro del c�rculo
	 * @param radio	Radio del c�rculo (en p�xels) 
	 * @param grosor	Grueso del c�rculo (en p�xels)
	 * @param color  	Color del c�rculo
	 */
	public void dibujaCirculo( double x, double y, double radio, float grosor, Color color ) {
		graphics.setColor( color );
		graphics.setStroke( new BasicStroke( grosor ));
		graphics.drawOval( (int)Math.round(x-radio), (int)Math.round(y-radio), (int)Math.round(radio*2), (int)Math.round(radio*2) );
		if (dibujadoInmediato) panel.repaint();
	}
	
	/** Dibuja un c�rculo azul en la ventana
	 * @param x	Coordenada x del centro del c�rculo
	 * @param y	Coordenada y del centro del c�rculo
	 * @param radio	Radio del c�rculo (en p�xels) 
	 * @param grosor	Grueso del c�rculo (en p�xels)
	 */
	public void dibujaCirculo( double x, double y, double radio, float grosor ) {
		dibujaCirculo( x, y, radio, grosor, Color.blue );
	}
	
	/** Borra un c�rculo en la ventana
	 * @param x	Coordenada x del centro del c�rculo
	 * @param y	Coordenada y del centro del c�rculo
	 * @param radio	Radio del c�rculo (en p�xels) 
	 * @param grosor	Grueso del c�rculo (en p�xels)
	 */
	public void borraCirculo( double x, double y, double radio, float grosor ) {
		dibujaCirculo( x, y, radio, grosor, Color.white );
	}
	
	/** Dibuja una l�nea en la ventana
	 * @param linea	a dibujar
	 * @param grosor	Grueso de la l�nea (en p�xels)
	 * @param color  	Color de la l�nea
	 */
	public void dibujaLinea( Line2D linea, float grosor, Color color ) {
		dibujaLinea( linea.getX1(), linea.getY1(), linea.getX2(), linea.getY2(), grosor, color );
	}
	
	/** Dibuja una l�nea en la ventana
	 * @param x	Coordenada x de un punto de la l�nea 
	 * @param y	Coordenada y de un punto de la l�nea
	 * @param x2	Coordenada x del segundo punto de la l�nea 
	 * @param y2	Coordenada y del segundo punto de la l�nea
	 * @param grosor	Grueso de la l�nea (en p�xels)
	 * @param color  	Color de la l�nea
	 */
	public void dibujaLinea( double x, double y, double x2, double y2, float grosor, Color color ) {
		graphics.setColor( color );
		graphics.setStroke( new BasicStroke( grosor ));
		graphics.drawLine( (int)Math.round(x), (int)Math.round(y), (int)Math.round(x2), (int)Math.round(y2) );
		if (dibujadoInmediato) panel.repaint();
	}
	
	/** Dibuja una l�nea azul en la ventana
	 * @param x	Coordenada x de un punto de la l�nea 
	 * @param y	Coordenada y de un punto de la l�nea
	 * @param x2	Coordenada x del segundo punto de la l�nea 
	 * @param y2	Coordenada y del segundo punto de la l�nea
	 * @param grosor	Grueso de la l�nea (en p�xels)
	 */
	public void dibujaLinea( double x, double y, double x2, double y2, float grosor ) {
		dibujaLinea( x, y, x2, y2, grosor, Color.blue );
	}
	
	/** Borra una l�nea en la ventana
	 * @param x	Coordenada x de un punto de la l�nea 
	 * @param y	Coordenada y de un punto de la l�nea
	 * @param x2	Coordenada x del segundo punto de la l�nea 
	 * @param y2	Coordenada y del segundo punto de la l�nea
	 * @param grosor	Grueso de la l�nea (en p�xels)
	 */
	public void borraLinea( double x, double y, double x2, double y2, float grosor ) {
		dibujaLinea( x, y, x2, y2, grosor, Color.white );
	}

	/** Dibuja una flecha en la ventana
	 * @param linea	a dibujar (el segundo punto es la punta de la flecha)
	 * @param grosor	Grueso de la l�nea (en p�xels)
	 * @param color  	Color de la l�nea
	 */
	public void dibujaFlecha( Line2D linea, float grosor, Color color ) {
		dibujaFlecha( linea.getX1(), linea.getY1(), linea.getX2(), linea.getY2(), grosor, color );
	}
	
	/** Dibuja una flecha en la ventana
	 * @param x	Coordenada x de un punto de la l�nea 
	 * @param y	Coordenada y de un punto de la l�nea
	 * @param x2	Coordenada x del segundo punto de la l�nea (el de la flecha)
	 * @param y2	Coordenada y del segundo punto de la l�nea (el de la flecha)
	 * @param grosor	Grueso de la l�nea (en p�xels)
	 * @param color  	Color de la l�nea
	 */
	public void dibujaFlecha( double x, double y, double x2, double y2, float grosor, Color color ) {
		dibujaFlecha( x, y, x2, y2, grosor, color, 10 );
	}
	
	/** Dibuja una flecha en la ventana
	 * @param x	Coordenada x de un punto de la l�nea 
	 * @param y	Coordenada y de un punto de la l�nea
	 * @param x2	Coordenada x del segundo punto de la l�nea (el de la flecha)
	 * @param y2	Coordenada y del segundo punto de la l�nea (el de la flecha)
	 * @param grosor	Grueso de la l�nea (en p�xels)
	 * @param color  	Color de la l�nea
	 * @param largoFl	Pixels de largo de la flecha
	 */
	public void dibujaFlecha( double x, double y, double x2, double y2, float grosor, Color color, int largoFl ) {
		graphics.setColor( color );
		graphics.setStroke( new BasicStroke( grosor ));
		graphics.drawLine( (int)Math.round(x), (int)Math.round(y), (int)Math.round(x2), (int)Math.round(y2) );
		double angulo = Math.atan2( y2-y, x2-x ) + Math.PI;
		double angulo1 = angulo - Math.PI / 10;  // La flecha se forma rotando 1/10 de Pi hacia los dos lados
		double angulo2 = angulo + Math.PI / 10;
		graphics.drawLine( (int)Math.round(x2), (int)Math.round(y2), 
				(int)Math.round(x2+largoFl*Math.cos(angulo1)), (int)Math.round(y2+largoFl*Math.sin(angulo1)) );
		graphics.drawLine( (int)Math.round(x2), (int)Math.round(y2), 
				(int)Math.round(x2+largoFl*Math.cos(angulo2)), (int)Math.round(y2+largoFl*Math.sin(angulo2)) );
		if (dibujadoInmediato) panel.repaint();
	}
	
	/** Dibuja una l�nea azul en la ventana
	 * @param x	Coordenada x de un punto de la l�nea 
	 * @param y	Coordenada y de un punto de la l�nea
	 * @param x2	Coordenada x del segundo punto de la l�nea (el de la flecha)
	 * @param y2	Coordenada y del segundo punto de la l�nea (el de la flecha)
	 * @param grosor	Grueso de la l�nea (en p�xels)
	 */
	public void dibujaFlecha( double x, double y, double x2, double y2, float grosor ) {
		dibujaFlecha( x, y, x2, y2, grosor, Color.blue );
	}
	
	/** Borra una l�nea en la ventana
	 * @param x	Coordenada x de un punto de la l�nea 
	 * @param y	Coordenada y de un punto de la l�nea
	 * @param x2	Coordenada x del segundo punto de la l�nea (el de la flecha)
	 * @param y2	Coordenada y del segundo punto de la l�nea (el de la flecha)
	 * @param grosor	Grueso de la l�nea (en p�xels)
	 */
	public void borraFlecha( double x, double y, double x2, double y2, float grosor ) {
		dibujaFlecha( x, y, x2, y2, grosor, Color.white );
	}
	
	/** Dibuja un pol�gono en la ventana
	 * @param grosor	Grueso de la l�nea (en p�xels)
	 * @param color  	Color de la l�nea
	 * @param cerrado	true si el pol�gono se cierra (�ltimo punto con el primero), false en caso contrario
	 * @param punto		Puntos a dibujar (cada punto se enlaza con el siguiente)
	 */
	public void dibujaPoligono( float grosor, Color color, boolean cerrado, Point2D... punto ) {
		graphics.setColor( color );
		graphics.setStroke( new BasicStroke( grosor ));
		if (punto.length<2) return;
		Point2D puntoIni = punto[0];
		Point2D puntoAnt = punto[0];
		Point2D pto = null;
		int numPto = 1;
		do {
			pto = punto[numPto];
			graphics.drawLine( (int)Math.round(puntoAnt.getX()), (int)Math.round(puntoAnt.getY()), (int)Math.round(pto.getX()), (int)Math.round(pto.getY()) );
			puntoAnt = pto;
			numPto++;
		} while (numPto<punto.length);
		if (cerrado) {
			graphics.drawLine( (int)Math.round(pto.getX()), (int)Math.round(pto.getY()), (int)Math.round(puntoIni.getX()), (int)Math.round(puntoIni.getY()) );
		}
		if (dibujadoInmediato) panel.repaint();
	}
	
	/** Borra un pol�gono en la ventana
	 * @param grosor	Grueso de la l�nea (en p�xels)
	 * @param cerrado	true si el pol�gono se cierra (�ltimo punto con el primero), false en caso contrario
	 * @param punto		Puntos a borrar (cada punto se enlaza con el siguiente)
	 */
	public void borraPoligono( float grosor, boolean cerrado, Point2D... punto ) {
		dibujaPoligono( grosor, Color.white, cerrado, punto );
	}

	/** Dibuja un texto en la ventana
	 * @param x	Coordenada x de la esquina superior izquierda del rect�ngulo
	 * @param y	Coordenada y de la esquina superior izquierda del rect�ngulo
	 * @param texto	Texto a dibujar 
	 * @param font	Tipo de letra con el que dibujar el texto
	 * @param color	Color del texto
	 */
	public void dibujaTexto( double x, double y, String texto, Font font, Color color ) {
		graphics.setColor( color );
		graphics.setFont( font );
		graphics.drawString( texto, (int)Math.round(x), (int)Math.round(y) );
		if (dibujadoInmediato) panel.repaint();
	}
	
	
	
	
	
	/** Devuelve el objeto de gr�fico sobre el que pintar, correspondiente al 
	 * panel principal de la ventana. Despu�s de actualizar graphics hay que llamar a {@link #repaint()}
	 * si se quiere que se visualice en pantalla
	 * @return	Objeto gr�fico principal de la ventana
	 */
	public Graphics2D getGraphics() {
		return graphics;
	}
	
	/** Repinta la ventana. En caso de que el dibujado inmediato est� desactivado,
	 * es imprescindible llamar a este m�todo para que la ventana gr�fica se refresque.
	 */
	public void repaint() {
		panel.paintImmediately( 0, 0, panel.getWidth(), panel.getHeight() );
	}
	

	// M�todos est�ticos
		private static int codTeclaTecleada = 0;
		private static int codTeclaActualmentePulsada = 0;
		private static HashSet<Integer> teclasPulsadas = new HashSet<Integer>();
		private static boolean controlActivo = false;
	// Inicializa el control de teclado
	private static void init() {
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher( new KeyEventDispatcher() {
			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				if (e.getID() == KeyEvent.KEY_PRESSED) {
					teclasPulsadas.add( e.getKeyCode() );
					codTeclaActualmentePulsada = e.getKeyCode();
					if (e.getKeyCode() == KeyEvent.VK_CONTROL) controlActivo = true; 
				} else if (e.getID() == KeyEvent.KEY_RELEASED) {
					teclasPulsadas.remove( e.getKeyCode() );
					if (e.getKeyCode() == KeyEvent.VK_CONTROL) controlActivo = false; 
					codTeclaTecleada = e.getKeyCode();
					codTeclaActualmentePulsada = 0;
				} else if (e.getID() == KeyEvent.KEY_TYPED) {
				}
				return false;   // false = enviar el evento al comp
			} } );
	}
	static {  // Inicializar en la carta de la clase
		init();
	}

	/** Indica si la tecla Ctrl est� siendo pulsada en este momento
	 * @return	true si est� pulsada, false en caso contrario
	 */
	public boolean isControlPulsado() {
		return controlActivo;
	}
	
	/** Devuelve el c�digo de la tecla pulsada actualmente
	 * @return	c�digo de tecla pulsada, 0 si no hay ninguna. Si hay varias, se devuelve la �ltima que se puls�.<br>
	 * 			Si se pulsan varias a la vez, tras soltar la primera este m�todo devuelve 0.
	 */
	public int getCodTeclaQueEstaPulsada() {
		return codTeclaActualmentePulsada;
	}
	
	/** Devuelve el c�digo de la �ltima tecla tecleada (pulsada y soltada). Tras eso, borra la tecla (solo se devuelve una vez)
	 * @return	C�digo de la �ltima tecla tecleada. Si no ha sido tecleada ninguna o ya se ha consultado, se devuelve 0.
	 */
	public int getCodUltimaTeclaTecleada() {
		int ret = codTeclaTecleada;
		codTeclaTecleada = 0;
		return ret;
	}

	/** Devuelve la informaci�n de si una tecla est� o no pulsada actualmente
	 * @param codTecla	C�digo de tecla a chequear
	 * @return	true si la tecla est� pulsada, false en caso contrario.
	 */
	public boolean isTeclaPulsada( int codTecla ) {
		return teclasPulsadas.contains(codTecla);
	}
	
	/** Pone modo de dibujado (por defecto el modo es de dibujado inmediato = true)
	 * @param dibujadoInmediato	true si cada orden de dibujado inmediatamente pinta la ventana. false si se
	 * van acumulando las �rdenes y se pinta la ventana solo al hacer un {@link #repaint()}.
	 */
	public void setDibujadoInmediato( boolean dibujadoInmediato ) {
		this.dibujadoInmediato = dibujadoInmediato;
	}

		// Variable local para guardar las im�genes y no recargarlas cada vez
		private static volatile HashMap<String,ImageIcon> recursosGraficos = new HashMap<>();
		
	/** Carga una imagen de un fichero gr�fico y la dibuja en la ventana. Si la imagen no puede cargarse, no se dibuja nada.
	 * El recurso gr�fico se busca en el paquete de esta clase o en la clase llamadora.
	 * El recurso gr�fico se carga en memoria, de modo que al volver a dibujar la misma imagen, no se vuelve a cargar ya de fichero
	 * @param recursoGrafico	Nombre del fichero (path absoluto desde la carpeta ra�z de clases del proyecto)  (p. ej. "/img/prueba.png")
	 * @param centroX	Coordenada x de la ventana donde colocar el centro de la imagen 
	 * @param centroY	Coordenada y de la ventana donde colocar el centro de la imagen
	 * @param anchuraDibujo	Pixels de anchura con los que dibujar la imagen (se escala de acuerdo a su tama�o)
	 * @param alturaDibujo	Pixels de altura con los que dibujar la imagen (se escala de acuerdo a su tama�o)
	 * @param zoom	Zoom a aplicar (mayor que 1 aumenta la imagen, menor que 1 y mayor que 0 la disminuye)
	 * @param radsRotacion	Rotaci�n en radianes
	 * @param opacity	Opacidad (0.0f = totalmente transparente, 1.0f = totalmente opaca)
	 */
	public void dibujaImagen( String recursoGrafico, double centroX, double centroY, 
			int anchuraDibujo, int alturaDibujo, double zoom, double radsRotacion, float opacity ) {
		ImageIcon ii = getRecursoGrafico(recursoGrafico); if (ii==null) return;
		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR); // Configuraci�n para mejor calidad del gr�fico escalado
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);	
		graphics.translate( centroX-anchuraDibujo/2, centroY-alturaDibujo/2 );
		graphics.rotate( radsRotacion, anchuraDibujo/2, alturaDibujo/2 );  // Incorporar al gr�fico la rotaci�n definida
		graphics.setComposite(AlphaComposite.getInstance( AlphaComposite.SRC_OVER, opacity ) ); // Incorporar la transparencia definida
        int anchoDibujado = (int)Math.round(anchuraDibujo*zoom);  // Calcular las coordenadas de dibujado con el zoom, siempre centrado en el label
        int altoDibujado = (int)Math.round(alturaDibujo*zoom);
        int difAncho = (anchuraDibujo - anchoDibujado) / 2;  // Offset x para centrar
        int difAlto = (alturaDibujo - altoDibujado) / 2;     // Offset y para centrar
        graphics.drawImage( ii.getImage(), difAncho, difAlto, anchoDibujado, altoDibujado, null);  // Dibujar la imagen con el tama�o calculado tras aplicar el zoom
		graphics.setTransform( new AffineTransform() );  // Restaurar graphics  (sin rotaci�n ni traslaci�n)
		graphics.setComposite(AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1f ));  // Restaurar graphics (pintado sin transparencia)
		if (dibujadoInmediato) panel.repaint();
	}

	/** Carga una imagen de un fichero gr�fico y la dibuja en la ventana. Si la imagen no puede cargarse, no se dibuja nada.
	 * El recurso gr�fico se busca en el paquete de esta clase o en la clase llamadora.
	 * El recurso gr�fico se carga en memoria, de modo que al volver a dibujar la misma imagen, no se vuelve a cargar ya de fichero
	 * @param recursoGrafico	Nombre del fichero (path absoluto desde la carpeta ra�z de clases del proyecto)  (p. ej. "/img/prueba.png")
	 * @param centroX	Coordenada x de la ventana donde colocar el centro de la imagen 
	 * @param centroY	Coordenada y de la ventana donde colocar el centro de la imagen
	 * @param zoom	Zoom a aplicar (mayor que 1 aumenta la imagen, menor que 1 y mayor que 0 la disminuye)
	 * @param radsRotacion	Rotaci�n en radianes
	 * @param opacity	Opacidad (0.0f = totalmente transparente, 1.0f = totalmente opaca)
	 */
	public void dibujaImagen( String recursoGrafico, double centroX, double centroY, 
			double zoom, double radsRotacion, float opacity ) {
		ImageIcon ii = getRecursoGrafico(recursoGrafico); if (ii==null) return;
		dibujaImagen( recursoGrafico, centroX, centroY, ii.getIconWidth(), ii.getIconHeight(), zoom, radsRotacion, opacity);
	}
		// Intenta cargar el recurso gr�fico del mapa interno, de la clase actual, o de la clase llamadora. Devuelve null si no se ha podido hacer
		private ImageIcon getRecursoGrafico( String recursoGrafico ) {
			ImageIcon ii = recursosGraficos.get( recursoGrafico );
			if (ii==null) {
				try {
					ii = new ImageIcon( VentanaGrafica.class.getResource( recursoGrafico ));
					recursosGraficos.put( recursoGrafico, ii );
				} catch (NullPointerException e) {  // Mirar si est� en la clase llamadora en lugar de en la ventana gr�fica
					StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
					for (int i=1; i<stElements.length; i++) {
						StackTraceElement ste = stElements[i];
						if ( !ste.getClassName().endsWith("VentanaGrafica") ) {  // Busca la clase llamadora a VentanaGrafica y busca ah� el recurso
							try {
								Class<?> c = Class.forName( ste.getClassName() );
								URL url = c.getResource( recursoGrafico );
								if (url==null) return null;
								ii = new ImageIcon( url );
								recursosGraficos.put( recursoGrafico, ii );
								return ii;
							} catch (ClassNotFoundException e1) {
								return null;
							}
						}
					}
					return null;
				}			
			}
			return ii;
		}


	
	private transient JPanel pBotonera = null;
/** A�ade un bot�n de acci�n a la botonera superior
 * @param texto	Texto del bot�n
 * @param evento	Evento a lanzar en la pulsaci�n del bot�n
 */
public void anyadeBoton( String texto, ActionListener evento ) {
	JButton b = new JButton( texto );
	if (pBotonera==null) {
		pBotonera = new JPanel();
		pBotonera.add( b );
		ventana.getContentPane().add( pBotonera, BorderLayout.NORTH );
		ventana.revalidate();
	} else {
		pBotonera.add( b );
		pBotonera.revalidate();
	}
	b.addActionListener( evento );
}

	
	/** M�todo main de prueba de la clase
	 * @param args	No utilizado
	 */
	public static void main(String[] args) {
		VentanaGrafica v = new VentanaGrafica( 600, 480, "Test Ventana Gr�fica" );
		v.anyadeBoton( "Pon dibujado inmediato", new ActionListener() {  // Para ver c�mo se ve con flickering si se dibujan cosas una a una
			@Override
			public void actionPerformed(ActionEvent e) {
				v.setDibujadoInmediato( true );
			}
		});
		v.setDibujadoInmediato( false );
		for (int i=0; i<=200; i++) {
			v.borra();
			v.dibujaImagen( "/utils/ventanas/ventanaJuego/img/" + "UD-green.png",
				100, 100, 0.5+i/200.0, Math.PI / 100 * i, 0.9f );
			v.dibujaImagen( "/utils/ventanas/ventanaJuego/img/" + "UD-magenta.png",
					500, 100, 100, 50, 1.2, Math.PI / 100 * i, 0.1f );
			v.dibujaRect( 20, 20, 160, 160, 0.5f, Color.red );
			v.dibujaRect( 0, 0, 100, 100, 1.5f, Color.blue );
			v.dibujaCirculo( 500, 100, 50, 1.5f, Color.orange );
			v.dibujaPoligono( 2.3f, Color.magenta, true, 
				new Point(200,250), new Point(300,320), new Point(400,220) );
			v.repaint();
			v.espera( 10 );
		}
		v.espera( 5000 );
		v.acaba();
	}
	
	/** A�ade un escuchador al cambio de tama�o del panel de dibujado de la ventana
	 * @param l	Escuchador de cambio de tama�o a a�adir
	 */
	public void addComponentListener( ComponentListener l ) {
		panel.addComponentListener( l );
	}
	
	/** Elimina un escuchador de cambio de tama�o del panel de dibujado de la ventana
	 * @param l	Escuchador de cambio de tama�o a eliminar
	 */
	public void removeComponentListener( ComponentListener l ) {
		panel.removeComponentListener( l );
	}
	
	/** A�ade un escuchador de ventana a la ventana
	 * @param l	Escuchador a a�adir
	 */
	public void addWindowListener( WindowListener l ) {
		ventana.addWindowListener( l );
	}
	
	/** Elimina un escuchador de ventana de la ventana
	 * @param l	Escuchador a eliminar
	 */
	public void removeWindowListener( WindowListener l ) {
		ventana.removeWindowListener( l );
	}
		
}
