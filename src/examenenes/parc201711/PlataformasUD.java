package examenenes.parc201711;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.*;
import examenenes.parc201711.utils.*;

public class PlataformasUD {
	// Atributos del mundo de juego
	private ArrayList<ObjetoMovil> objetos;
	private VentanaGrafica ventana;
	
	// Atributos de animaci�n
	private static long MILIS_POR_MOVIMIENTO = 16;
	private static long MILIS_ENTRE_MOVTOS = 16;
	private static boolean PAUSA = false;
	private static boolean VER_CHOQUES = false;
	@SuppressWarnings("unused")
	private static boolean CALC_CHOQUE_EXACTO = false;

	// Atributos de l�gica de juego
	private boolean personajeEnSuelo = false;
	private long tiempoDeJuego = 0;
	
	// TODO TAREA 2 - Atributo de properties
	public PlataformasUD() {
		// TODO TAREA 2 - Cargar properties
		objetos = new ArrayList<ObjetoMovil>();
		ventana = new VentanaGrafica( 1000, 800, "Plataformas UD" );
		// TODO TAREA 2 - Guardar properties al cierre de la ventana (buscar el escuchador adecuado para ese cierre)
	}
	
	public ArrayList<ObjetoMovil> getObjetos() {
		return objetos;
	}
	
	public VentanaGrafica getVentana() {
		return ventana;
	}
	
	public boolean addObjeto( ObjetoMovil objeto ) {
		if (objeto.getNombre()==null || objeto.getNombre().isEmpty()) objeto.setNombre( "" + objetos.size() );
		objetos.add( objeto );
		return true;
	}
	

	public static void main(String[] args) {
		crearYMoverMundo();
	}
	
	private static void crearYMoverMundo() {
		PlataformasUD mundo = new PlataformasUD();
		mundo.init();
		mundo.crearMundoTest( 1 );
		mundo.moverMundo();
	}
	
	private void init() {
		ventana.anyadeBoton( "Reinicio test 1", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ejecutaSeguro( new Runnable() {
					@Override
					public void run() {
						CALC_CHOQUE_EXACTO = false;
						crearMundoTest( 1 );
					}
				} );
			}
		});
		ventana.anyadeBoton( "Reinicio test 2", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ejecutaSeguro( new Runnable() {
					@Override
					public void run() {
						CALC_CHOQUE_EXACTO = true;
						ObjetoMovil.DIBUJAR_VELOCIDAD = true;
						VER_CHOQUES = true;
						ventana.setMensaje( "Parar c�lculo en choques ON" );
						crearMundoTest( 2 );
					}
				} );
			}
		});
		ventana.anyadeBoton( "Test", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ejecutaSeguro( new Runnable() {
					@Override
					public void run() {
						test( objetos );
					}
				} );
			}
		});
		ventana.anyadeBoton( "Cargar", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ejecutaSeguro( new Runnable() {
					@Override
					public void run() {
						cargarDeFichero( objetos );
					}
				} );
			}
		});
		ventana.anyadeBoton( "Guardar", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ejecutaSeguro( new Runnable() {
					@Override
					public void run() {
						guardarAFichero( objetos );
					}
				} );
			}
		});
	}
	
		private static Random random = new Random();
		public static int cont = 0;

		private transient UDcito personaje = null;
		private transient Runnable run = null;
		private transient int tipoInit = 0;
	// Crea objetos de test en el mundo
	private void crearMundoTest( int tipoTest ) {
		tipoInit = tipoTest;
		objetos.clear();
		if (tipoInit==1) {  // Un juego sencillo
			MILIS_ENTRE_MOVTOS = 16;
			personaje = new UDcito( 700, 500, 40, Color.green );
			PlataformasUD.this.addObjeto( personaje );
			PlataformasUD.this.addObjeto( new Nave( 200, 100, 20, 100, 100, Color.red ) );
			PlataformasUD.this.addObjeto( new Pelota( 200, 500, 100, Color.magenta, true ) );
			PlataformasUD.this.addObjeto( new Pelota( 500, 250, 60, Color.cyan, false ) );
			PlataformasUD.this.addObjeto( new Bloque( 500, 100, 500, 15, Color.blue, false ) );
			PlataformasUD.this.addObjeto( new Bloque( 0, 350, 500, 15, Color.blue, false ) );
			PlataformasUD.this.addObjeto( new Bloque( 550, 600, 500, 15, Color.blue, false ) );
		} else if (tipoInit==2) {  // Solo un personaje y una bola que choca contra �l
			MILIS_ENTRE_MOVTOS = 128;
			personaje = new UDcito( 820, 650, 40, Color.green );
			personaje.setVelocidad( -1000, -1800 );
			personaje.setEstadoAnimacion( "saltando" );
			PlataformasUD.this.addObjeto( personaje );
			Pelota pelota = new Pelota( 100, 260, 70, Color.magenta, true );
			pelota.setVelocidad( 1000, 0 );
			PlataformasUD.this.addObjeto( pelota );
		}
	}
	
		// M�todo privado para ejecutar un c�digo sin interferir con el ciclo de ejecuci�n del juego
		private void ejecutaSeguro( Runnable miRun ) {
			if (!running)
				miRun.run();  // Si no est� ejecut�ndose el juego, se inicia
			else
				run = miRun;  // Si ya est� ejecut�ndose el juego se almacena el trabajo y el propio juego lo lanzar� cuando sea seguro
		}
		
		private boolean running = false;
	// Permitiendo interacci�n con el rat�n para crear naves y/o lanzarlas en diagonal
	private void moverMundo() {
		running = true;
		primerClick = null;
		ultimoClick = null;
		objetoClickado = null;
		VentanaGrafica v = this.getVentana();
		PAUSA = true;
		ventana.setMensaje( "Pausa ON. Pulsa P para iniciar");
		ventana.setDibujadoInmediato( false );
		tiempoDeJuego = 0;
		while (!v.estaCerrada()) {  // hasta que se cierre la ventana
			// 0.- Cambiar par�metros con posibles indicaciones de teclado
			procesarTeclado();
			if (!PAUSA) {
				// 1.- Chequear posible interacci�n de rat�n
				procesarRaton(v);
				// 2.- Hacer movimiento de los objetos en el lapso de tiempo ocurrido
				moverObjetos(v);
				// 3.- Calcular y corregir choques en el mundo
				corregirMovimiento(v);
			}
			// 4.- Dibujado expl�cito de todos los objetos
			dibujadoMundo(v);
			// 5.- Ciclo de espera hasta la siguiente iteraci�n
			this.getVentana().espera( MILIS_ENTRE_MOVTOS );
			if (!PAUSA) tiempoDeJuego += MILIS_ENTRE_MOVTOS;
			// 6.- Posible trabajo de reinicializaci�n que est� pendiente
			if (run!=null) { run.run(); run = null; }
		}
		running = false;
	}

	// 0.- Cambiar par�metros con posibles indicaciones de teclado
	private void procesarTeclado() {
		int tecla = ventana.getCodUltimaTeclaTecleada(); 
		if (tecla==KeyEvent.VK_V) {
			ObjetoMovil.DIBUJAR_VELOCIDAD = !ObjetoMovil.DIBUJAR_VELOCIDAD;
			ventana.setMensaje( "Dibujar velocidad " + (ObjetoMovil.DIBUJAR_VELOCIDAD ? "ON" : "OFF") );
		} else if (tecla==KeyEvent.VK_P) {
			PAUSA = !PAUSA;
			ventana.setMensaje( "Pausa " + (PAUSA ? "ON" : "OFF") );
		} else if (tecla==KeyEvent.VK_C) {
			VER_CHOQUES = !VER_CHOQUES;
			ventana.setMensaje( "Dibujar y parar c�lculo en choques " + (VER_CHOQUES ? "ON" : "OFF") );
		} else if (tecla==KeyEvent.VK_PLUS) {
			if (ventana.isControlPulsado()) {  // Con ctrl acelera el tiempo de animaci�n del juego
				if (MILIS_ENTRE_MOVTOS>1) {
					MILIS_ENTRE_MOVTOS = MILIS_ENTRE_MOVTOS / 2;
					if (MILIS_POR_MOVIMIENTO >= MILIS_ENTRE_MOVTOS)
						ventana.setMensaje( "Tiempo visualizaci�n x" + (1.0 * MILIS_POR_MOVIMIENTO / MILIS_ENTRE_MOVTOS) );
					else 
						ventana.setMensaje( "Tiempo visualizaci�n /" + (1.0 * MILIS_ENTRE_MOVTOS / MILIS_POR_MOVIMIENTO) );
				}
			} else {   // Sin ctrl acelera 
				if (MILIS_POR_MOVIMIENTO<132) {
					MILIS_POR_MOVIMIENTO = MILIS_POR_MOVIMIENTO * 2;
					if (MILIS_POR_MOVIMIENTO >= MILIS_ENTRE_MOVTOS)
						ventana.setMensaje( "Tiempo visualizaci�n x" + (1.0 * MILIS_POR_MOVIMIENTO / MILIS_ENTRE_MOVTOS) );
					else 
						ventana.setMensaje( "Tiempo visualizaci�n /" + (1.0 * MILIS_ENTRE_MOVTOS / MILIS_POR_MOVIMIENTO) );
				}
			}
		} else if (tecla==KeyEvent.VK_MINUS) {
			if (ventana.isControlPulsado()) {  // Con ctrl decrementa el tiempo de animaci�n del juego
				if (MILIS_ENTRE_MOVTOS<2148) {
					MILIS_ENTRE_MOVTOS = MILIS_ENTRE_MOVTOS * 2;
					if (MILIS_POR_MOVIMIENTO >= MILIS_ENTRE_MOVTOS)
						ventana.setMensaje( "Tiempo visualizaci�n x" + (1.0 * MILIS_POR_MOVIMIENTO / MILIS_ENTRE_MOVTOS) );
					else 
						ventana.setMensaje( "Tiempo visualizaci�n /" + (1.0 * MILIS_ENTRE_MOVTOS / MILIS_POR_MOVIMIENTO) );
				}
			} else {   // Sin ctrl decelera
				if (MILIS_POR_MOVIMIENTO>1) {
					MILIS_POR_MOVIMIENTO = MILIS_POR_MOVIMIENTO / 2;
					if (MILIS_POR_MOVIMIENTO >= MILIS_ENTRE_MOVTOS)
						ventana.setMensaje( "Tiempo visualizaci�n x" + (1.0 * MILIS_POR_MOVIMIENTO / MILIS_ENTRE_MOVTOS) );
					else 
						ventana.setMensaje( "Tiempo visualizaci�n /" + (1.0 * MILIS_ENTRE_MOVTOS / MILIS_POR_MOVIMIENTO) );
				}
			}
		}
		// Pulsaciones activas
		tecla = ventana.getCodTeclaQueEstaPulsada(); 
		if (ventana.isTeclaPulsada( KeyEvent.VK_LEFT )) {
			if (personajeEnSuelo) {
				personaje.setVelocidadX( -200 );
				personaje.setEstadoAnimacion( "andando" );
			}
		} else if (ventana.isTeclaPulsada( KeyEvent.VK_RIGHT )) {
			if (personajeEnSuelo) {
				personaje.setVelocidadX( +200 );
				personaje.setEstadoAnimacion( "andando" );
			}
		} else {  // No hay lateralidad pulsada
			if (!personaje.getEstadoAnimacion().equals("saltando")) {
				personaje.setVelocidadX( 0 );
				personaje.setEstadoAnimacion( "parado" );
			}
		}
		if (ventana.isTeclaPulsada( KeyEvent.VK_UP )) {  // Salto
			if (personajeEnSuelo) {
				personaje.setVelocidadY( -2300 );
				personaje.setEstadoAnimacion( "saltando" );
			} else {
				if (personajeEnSuelo) personaje.setEstadoAnimacion( "parado" );
			}
		} else  {
			if (personajeEnSuelo && personaje.getEstadoAnimacion().equals("saltando")) personaje.setEstadoAnimacion( "parado" );
		}
		personaje.refrescaAnimacion( tiempoDeJuego );
		if (ventana.isTeclaPulsada( KeyEvent.VK_LEFT )) {
			gestionTeclas( "LEFT", true );
		} else {  // Tecla cursor izquierda no est� pulsada
			gestionTeclas( "LEFT", false );
		}
		if (ventana.isTeclaPulsada( KeyEvent.VK_RIGHT )) {
			gestionTeclas( "RIGHT", true );
		} else {  // Tecla cursor derecha no est� pulsada
			gestionTeclas( "RIGHT", false );
		}
		if (ventana.isTeclaPulsada( KeyEvent.VK_UP )) {
			gestionTeclas( "UP", true );
		} else {  // Tecla cursor arriba no est� pulsada
			gestionTeclas( "UP", false );
		}
	}
	
		// TODO TAREA 3 - Atributos de estructuras de datos para control de estad�sticas
		// TODO TAREA 3 - Detecci�n de teclas para estad�sticas
		// Se llama a este m�todo cada iteraci�n de bucle de juego con las tres teclas de control
		// ("UP", "LEFT", "RIGHT") en cada caso con pulsada=true o false seg�n su estado de pulsaci�n
		private void gestionTeclas( String codTecla, boolean pulsada ) {
		}

		// Atributos de interacci�n con rat�n
		private Point primerClick = null;
		private Point ultimoClick = null;
		private ObjetoMovil objetoClickado = null;
	// 1.- Chequear posible interacci�n de rat�n
	private void procesarRaton(VentanaGrafica v) {
		Point clickRaton = v.getRatonPulsado();
		if (clickRaton==null) {
			if (primerClick!=null && ultimoClick!=null && objetoClickado!=null && !primerClick.equals(ultimoClick)) { // Ha habido un drag sobre un objeto
				// Aplicar fuerza a objeto
				objetoClickado.setVelocidadX( (ultimoClick.x - primerClick.x)*10.0 );
				objetoClickado.setVelocidadY( (ultimoClick.y - primerClick.y)*10.0 );
			} else if (primerClick!=null && ultimoClick!=null && objetoClickado==null && !primerClick.equals(ultimoClick)) {  // No hay drag. Creaci�n de objeto nuevo
				double radio = primerClick.distance( ultimoClick );
				if (radio >= 5) {  // Por debajo de 10 p�xels no se considera
					Color color = Color.blue;
					switch (random.nextInt( 3 )) {
						case 0: {
							color = Color.red;
							break;
						}
						case 1: {
							color = Color.green;
							break;
						}
					}
					Nave nave = new Nave( primerClick.x, primerClick.y, 20, ultimoClick.x, ultimoClick.y, color );
					if (this.addObjeto( nave ))
						nave.dibuja( this.getVentana() );
				}
			}
			primerClick = null;
		} else {
			if (primerClick==null) {
				primerClick = clickRaton;
				ultimoClick = null;
				objetoClickado = null;
				for (ObjetoMovil objeto : this.getObjetos()) {
						if (objeto!=null) {
						if (objeto.contieneA(primerClick)) {
							objetoClickado = objeto;
							break;
						}
					}
				}
			} else {
				ultimoClick = clickRaton;
			}
		}
	}
	
	// 2.- Hacer movimiento de los objetos en el lapso de tiempo ocurrido
	private void moverObjetos(VentanaGrafica v) {
		for (ObjetoMovil objeto : this.getObjetos()) {
			if (objeto != null) {  // Ojo, solo con los objetos que haya!
				// Se mueve el objeto
				objeto.mueveUnPoco( v, MILIS_POR_MOVIMIENTO, false );  // M�todo para movimiento con influencia de gravedad   (sin dibujado)
			}
		}
	}

	// 3.- Calcular y corregir choques en el mundo
	private void corregirMovimiento(VentanaGrafica v) {
//		boolean hayChoques;
		int numIteracion = 0;
		personajeEnSuelo = false;  // Antes del choque con suelo se supone que el personaje no est� en el suelo
//		do { 
			numIteracion++;
//			hayChoques = false;
			// 3a.- Comprobamos choques con los l�mites de la ventana
			for (ObjetoMovil objeto : this.getObjetos()) {
				if (objeto != null) {  // Ojo, solo con los objetos que haya!
					// Choque lateral
					int choque = objeto.chocaConBorde( v );
					// System.out.println( choque );
					if ((choque & 0b0001) != 0 && objeto.getVelocidadX()<0) { // Choque izquierda
						if (objeto.isBota()) objeto.rebotaIzquierda( 1.0 );  // Rebota al 100% -sale hacia la derecha-
						objeto.corrigeChoqueLateral( v, false );
//						hayChoques = true;
						reboteEnBorde( objeto, 0 );
					} else if ((choque & 0b0010) != 0 && objeto.getVelocidadX()>0) {  // Choque derecha
						if (objeto.isBota()) objeto.rebotaDerecha( 1.0 );  // Rebota al 100% -sale hacia la izquierda-
						objeto.corrigeChoqueLateral( v, false );
//						hayChoques = true;
						reboteEnBorde( objeto, 1 );
					}
					// Choque en vertical
					if (choque>=8) {  // Abajo
//						hayChoques = true;
						if (objeto.getVelocidadY()>0) objeto.corrigeChoqueVertical( v, false );
						if (objeto.isBota()) objeto.rebotaAbajo( 1.0 );
						objeto.corrigeChoqueVertical( v, false );
						reboteEnBorde( objeto, 3 );
						if (objeto == personaje) personajeEnSuelo = true;
					} else if (choque>=4) {  // Arriba
//						hayChoques = true;
						if (objeto.getVelocidadY()<0) objeto.corrigeChoqueVertical( v, false );
						if (objeto.isBota()) objeto.rebotaArriba( 1.0 );
						objeto.corrigeChoqueVertical( v, false );
						reboteEnBorde( objeto, 2 );
					}
				}
			}
			// 3b.- Comprobamos choques entre objetos
			// Probamos todas con todas (salen rebotadas en la direcci�n del choque)
			for (int i=0; i<this.getObjetos().size(); i++) {
				ObjetoMovil objeto = this.getObjetos().get(i); 
				if (objeto != null) {
					for (int j=i+1; j<this.getObjetos().size(); j++) {
						ObjetoMovil objeto2 = this.getObjetos().get(j); 
						if (objeto2 != null) {
							Point2D choque = objeto.chocaConObjeto( objeto2 );
							if (choque!=null) {
								procesaChoque( objeto, objeto2, choque, numIteracion );
							}
						}
					}
				}
			}
//		} while (hayChoques && numIteracion<=3);
	}

	// 4.- Dibujado expl�cito de todos los objetos
	private void dibujadoMundo(VentanaGrafica v) {
		// Dibujado de mundo
		this.getVentana().borra();  // Borra todo
		for (ObjetoMovil objeto : this.getObjetos()) {  // Y dibuja de nuevo todos los objetos
			if (objeto != null) {
				objeto.dibuja( this.getVentana() );
			}
		}
		// Feedback visual de interacciones
		if (primerClick!=null && ultimoClick!=null) {
			if (objetoClickado!=null) {  // Se est� queriendo imprimir velocidad a un objeto
				ventana.dibujaFlecha( primerClick.getX(), primerClick.getY(), ultimoClick.getX(), ultimoClick.getY(), 1.0f, Color.orange, 25 );
			} else {  // Se est� queriendo crear un objeto
				ventana.dibujaCirculo( primerClick.getX(), primerClick.getY(), 20, 1.0f, Color.orange );
				ventana.dibujaFlecha( primerClick.getX(), primerClick.getY(), ultimoClick.getX(), ultimoClick.getY(), 1.0f, Color.orange, 25 );
			}
		}
		ventana.repaint();
		if (!PAUSA) trasCadaFotograma( this.getObjetos() );
	}
	
	// M�todos de l�gica de la animaci�n
	
	// Se ejecuta en cada choque y recibe los objetos que chocan
	// Se puede procesar varias veces en cada fotograma para correcci�n de varios choques
	// objeto1 siempre es anterior en orden en la lista de objetos principal a objeto2
	private void procesaChoque( ObjetoMovil objeto1, ObjetoMovil objeto2, Point2D choque, int numIteracion ) {
		double milis = MILIS_POR_MOVIMIENTO;
		// TODO TAREA 4 - Quitar estos comentarios de abajo
		/*
		if (CALC_CHOQUE_EXACTO && objeto1 instanceof UDcito && objeto2 instanceof Pelota) {
			ventana.setDibujadoInmediato( true );  // Para dibujar inmediatamente todo lo que se quiera marcar
			UDcito ud = (UDcito) objeto1;
			Pelota pelota = (Pelota) objeto2;
			ud.dibuja( ventana );                     // Dibuja estado final de pelota y udcito
			pelota.dibuja( ventana );
			ud.deshazUltimoMovimiento( ventana );     // Deshace los movimientos para volver a estado inicial de este movimiento
			pelota.deshazUltimoMovimiento( ventana );
			double milisChoque = calcularChoqueExacto( 0, ventana, ud, pelota, MILIS_POR_MOVIMIENTO );  // C�lculo recursivo de choque
			ventana.borra();
			ud.dibuja( ventana );
			pelota.dibuja( ventana );
			ventana.setDibujadoInmediato( false );  // Volver al modo de dibujado est�ndar
		} */ // TAREA 4
		// Aplica velocidad de choque en funci�n de las masas (el que tiene masa m�s grande se ve menos afectado y viceversa)
		Fisica.calcChoqueEntreObjetos(ventana, objeto1, objeto2, milis, VER_CHOQUES );
		if (VER_CHOQUES) {  // Espera a pulsaci�n de rat�n
			if (ventana.getRatonPulsado()==null) { // Si el rat�n no est� pulsado...
				while (ventana.getRatonPulsado()==null && !ventana.estaCerrada()) {}  // Espera a pulsaci�n...
				long esperaMax = System.currentTimeMillis();
				while (System.currentTimeMillis()-esperaMax < 500 && ventana.getRatonPulsado()!=null && !ventana.estaCerrada()) {}  // ...y suelta o medio segundo
			}
		}
		if (numIteracion==1 && (objeto1==personaje || objeto2==personaje)) {  // Procesa el cambio de energ�a si hay choque (solo en la primera iteraci�n)
			ObjetoMovil chocado = objeto1;
			if (objeto1==personaje) { chocado = objeto2; choque.setLocation( choque.getX(), -choque.getY() ); }
			if (chocado instanceof Pelota) {  // Quita energ�a a UDcito si choca con una pelota - no con un bloque
				personaje.cambiaEnergia( - chocado.getArea()/400 );
				ventana.setMensaje( "Choque: Energ�a = " + personaje.getEnergia() );
				if (personaje.getEnergia()<0) {
					objetos.remove( personaje );
					ventana.setMensaje( "Juego terminado! Has perdido" );
				}
			} else if (chocado instanceof Bloque) {  // Detecta el suelo
				if (choque.getY()<0) {  // Si el choque es hacia abajo es que es suelo, no techo
					personajeEnSuelo = true;
				}
			}
		}
	}

	// Se ejecuta tras cada fotograma y recibe todos los objetos en pantalla
	private void trasCadaFotograma( ArrayList<ObjetoMovil> listaObjetos ) {
		
	}
	
	// Se ejecuta tras rebotar en un borde de la ventana un objeto.
	// C�digo de borde: 0-Izquierda 1-Derecha 2-Arriba 3-Abajo
	private void reboteEnBorde( ObjetoMovil objeto, int codigoBorde ) {
		// Pendiente
	}
	
	// Se ejecuta al pulsar el bot�n de test y recibe todos los objetos en pantalla
	private void test( ArrayList<ObjetoMovil> listaObjetos ) {
		// TODO TAREA 3 - Visualizaci�n de estructuras de datos para control de estad�sticas
	}
	
	public void cargarDeFichero( ArrayList<ObjetoMovil> listaObjetos ) {
		// Pendiente
	}

	public void guardarAFichero( ArrayList<ObjetoMovil> listaObjetos ) {
		// TODO TAREA 3 - Base de datos
	}

	// TODO TAREA 4 - Aproximaciones sucesivas
	// Devuelve los milisegundos en los que se produce el choque
	@SuppressWarnings("unused")
	private double calcularChoqueExacto( int numLlamadas, VentanaGrafica ventana, UDcito ud, Pelota pelota, double milisMovimiento ) {
		// Mover la mitad
		// ...
		// ud.dibuja( ventana );      // Si quieres en cada prueba recursiva para ver d�nde est� ahora udcito
		// pelota.dibuja( ventana );  // y d�nde est� ahora la pelota
		// ventana.esperaAClick();    // Si quieres que en cada paso recursivo se pause el juego hasta que se haga click
		// Proceso recursivo - una mitad u otra seg�n si ahora hay o no choque
		// ...
		return 0; // A quitar cuando se acabe el proceso
	}
	
}
