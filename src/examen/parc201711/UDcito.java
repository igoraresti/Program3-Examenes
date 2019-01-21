package examen.parc201711;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import examen.parc201711.utils.*;

/** Clase que permite crear y gestionar objetos gr�ficos que se mueven y saltan
 * con animaciones gr�ficas bitmap. Implementado con un escudo de UDeusto
 */
public class UDcito extends ObjetoMovil {
	private double radio;    // Radio de choque
	private double energia;  // Energ�a = vida de udcito
	
	// Atributos de animaci�n
	private HashMap<String,ArrayList<String>> animaciones;  // Gr�ficos de animaci�n por estado
	private ArrayList<String> estados;  // Estados de animaci�n  (se inicializa como 0 = parado, 1 = andando, 2 = saltando)
	private ArrayList<Integer> cicloAnimsEstadoMs;  // Ciclos de animaci�n de cada estado (milisegundos entre cambio de gr�fico)
	private int estadoActual; // N�mero de estado en el que se encuentra el objeto ahora
	private int animActual; // N�mero de animaci�n en la que se encuentra el objeto ahora, dentro de su estado
	private long msgsAnimacion;  // Informaci�n de milisegundos de animaci�n para ir actualizando el estado (msg de la �ltima animaci�n calculada)
	
	/** Constructor de udcito por defecto: nombre vac�o, centro (0,0), radio 40, color de c�rculo 40
	 */
	public UDcito() {
		this( 0, 0, 40, Color.blue );
	}
	
	/** Constructor de pelota con datos
	 * @param x	Coordenada x del centro de la pelota
	 * @param y	Coordenada y del centro de la pelota
	 * @param radio	Radio de la pelota
	 * @param color	Color del c�rculo de choque
	 */
	public UDcito(double x, double y, double radio, Color color) {
		super( x, y, color );
		this.radio = radio;
		this.energia = 100;
		animaciones = new HashMap<>();
		ArrayList<String> anim = new ArrayList<>(); anim.add( "img/UD-normal.png" );
		animaciones.put( "parado", anim );
		anim = new ArrayList<>( Arrays.asList( new String[] { "img/UD-normal.png", "img/UD-andando1.png", "img/UD-andando2.png", "img/UD-andando3.png", "img/UD-andando2.png", "img/UD-andando1.png", "img/UD-normal.png", "img/UD-andando3.png", "img/UD-andando4.png", "img/UD-andando3.png" } ) );
		animaciones.put( "andando", anim );
		anim = new ArrayList<>( Arrays.asList( new String[] { "img/UD-normal.png", "img/UD-saltando1.png", "img/UD-saltando2.png", "img/UD-saltando3.png", "img/UD-saltando2.png", "img/UD-saltando1.png" } ) );
		animaciones.put( "saltando", anim );
		estados = new ArrayList<>( Arrays.asList( new String[] { "parado", "andando", "saltando" } ) );
		cicloAnimsEstadoMs = new ArrayList<>( Arrays.asList( new Integer[] { 0, 100, 100 } ) );
		estadoActual = 0; // parado
		animActual = 0;
	}
	
	public double getRadio() {
		return radio;
	}

	public void setRadio(double radio) {
		this.radio = radio;
	}
	
	/** Incrementa o decrementa la energ�a del objeto
	 * @param incremento	Cantidad de energ�a a incrementar (positivo) o decrementar (negativo)
	 */
	public void cambiaEnergia( double incremento ) {
		energia += incremento;
	}
	
	public double getEnergia() {
		return energia;
	}

	//
	// M�todos redefinidos
	//

	/** Calcula el volumen de la pelota partiendo de su informaci�n de radio
	 * @return	Volumen de la pelota suponiendo una esfera perfecta
	 */
	@Override
	public double getVolumen() {
		return 4.0/3*Math.PI*radio*radio*radio;
	}
	
	/** Calcula el �rea de la pelota partiendo de su informaci�n de radio
	 * @return	Area de la pelota suponiendo una circunferencia perfecta
	 */
	@Override
	public double getArea() {
		return Math.PI*radio*radio;
	}
	
	/** Dibuja la pelota en una ventana, en el color correspondiente de la pelota (por defecto, negro)
	 * @param v	Ventana en la que dibujar la pelota
	 */
	@Override
	public void dibuja( VentanaGrafica v ) {
			String imagenActual = animaciones.get( estados.get( estadoActual ) ).get( animActual );
		v.dibujaImagen( imagenActual, x+2, y+1, 0.51, 0, 1.0f );  // zoom y x,y ajustados para que el escudo coincida con el c�rculo de choque
		v.dibujaCirculo( x, y, radio, 1.5f, color );
		super.dibuja( v );  // Para dibujar la velocidad si procede
	}
	
	/** Borra la pelota en una ventana
	 * @param v	Ventana en la que borrar la pelota
	 */
	@Override
	public void borra( VentanaGrafica v ) {
		v.borraCirculo( x, y, radio, 1.5f );
	}

	/** Comprueba si la pelota incluye a un punto dado
	 * @param punto	Punto a chequear
	 * @return	true si el punto est� dentro de la pelota, false en caso contrario
	 */
	@Override
	public boolean contieneA( Point2D punto ) {
		double dist = punto.distance( x, y );
		return dist <= radio;
	}

	@Override
	public boolean isBota() {
		return false;
	}
	
	@Override
	public boolean isFijo() {
		return false;
	}
	
	@Override
	public String toString() {
		return String.format( "UDcito %1s (%2$7.2f,%3$7.2f) R=%4$5.1f Vel.=(%5$6.3f,%6$6.3f)", nombre, x, y, radio, velocidadX, velocidadY );
	}

	//
	// M�todos de choque
	//
	
	/** Ajusta la pelota al suelo, si se ha "pasado" del suelo. Ajusta la velocidad a la que ten�a cuando lo toc�.
	 * @param v	Ventana de la que ajustar al suelo
	 * @param dibujar	true si se quiere dibujar la pelota, false en caso contrario
	 */
	public void corrigeChoqueInferior( VentanaGrafica v, boolean dibujar ) {
		double dondeToca = v.getAltura() - radio;  // Coordenada y en la que se toca el suelo, a la que hay que ajustar
		if (dondeToca >= y) return;  // Si no est� pasando el suelo, no se hace nada 
		setY( dondeToca );
		// Si estaba cayendo con una velocidad positiva (hacia abajo), entonces la velocidad real que ten�a cuando
		// "toc�" el suelo era algo menor. Corregimos posici�n y velocidad (aproximadamente)
		if (velYInicial>0 && velocidadY>0) {
			double tiempoChoque = Fisica.calcTiempoHastaEspacio( velYInicial, antY, Fisica.getGravedad(), dondeToca);
			if (tiempoChoque > 0) {  // Si hay un error en el c�lculo no se aplica correcci�n
				if (dibujar) borra( v );
				long tiempoMsgs = Math.round(tiempoChoque*1000.0);
				setX( Fisica.calcEspacio( antX, tiempoMsgs, velXInicial ) );  // Corrige la posici�n
				setVelocidadY( Fisica.calcVelocidad( velYInicial, tiempoMsgs, Fisica.getGravedad() )); // Corrige la velocidad
				if (dibujar) dibuja( v );
			}
			// System.out.println( "Datos: " + tiempoChoque + " " + velYInicial + " " + antY + " " + radio + " " + (velYInicial*velYInicial - 2.0*Fisica.GRAVEDAD*(antY-(v.getAltura()-radio))) );
		}
	}
	
	@Override
	public void corrigeChoqueLateral(VentanaGrafica v, boolean dibujar) {
		if (dibujar) borra( v );
		if (x<radio) {
			setX( radio );
		} else if (x>v.getAnchura()-radio){
			setX( v.getAnchura() - radio );
		}
		if (dibujar) dibuja( v );
	}
	
	/** Ajusta la pelota al lateral
	 * @param v	Ventana de la que ajustar al lateral
	 * @param izquierda	true para izquierda, false para derecha
	 * @param dibujar	true si se quiere dibujar la pelota, false en caso contrario
	 */
	public void corrigeChoqueLateral( VentanaGrafica v, boolean izquierda, boolean dibujar ) {
		// Corregimos al lateral sin tocar la velocidad
		if (dibujar) borra( v );
		if (izquierda) {
			setX( radio );
		} else {
			setX( v.getAnchura() - radio );
		}
		if (dibujar) dibuja( v );
	}
	
	@Override
	public void corrigeChoqueVertical(VentanaGrafica v, boolean dibujar) {
		if (dibujar) borra( v );
		if (y > v.getAltura()-radio) {
			setY( v.getAltura()-radio );
		}
		if (dibujar) dibuja( v );
	}

	/** Detecta el choque de la pelota con los bordes de la ventana
	 * @param v	Ventana con la que probar el choque
	 * @return	Devuelve un n�mero formado por la suma de: 0 si no choca, 1 si choca con la izquierda, 2 con la derecha, 4 arriba, 8 abajo.
	 */
	@Override
	public int chocaConBorde( VentanaGrafica v ) {
		int ret = 0;
		if (x-radio<=0) ret += 1;
		if (x+radio>=v.getAnchura()) ret += 2;
		if (y-radio<=0) ret += 4;
		if (y+radio>=v.getAltura()) ret += 8;
		return ret;
	}
	
	/** Detecta el choque de la pelota con otro objeto
	 * @param pelota2	Pelota con la que probar el choque
	 * @return	Devuelve null si no chocan, un vector con forma de punto indicando el �ngulo y amplitud del choque sobre la pelota en curso
	 */
	@Override
	public Point2D chocaConObjeto( ObjetoMovil objeto2 ) {
		if (objeto2 instanceof Pelota) {
			Pelota pelota2 = (Pelota) objeto2;
			Point2D p = new Point2D.Double( pelota2.x - x, pelota2.y - y );
			double dist = p.distance(0,0);
			double moduloChoque = radio + pelota2.getRadio() - dist;
			if (moduloChoque < 0) return null;
			p.setLocation( p.getX() * moduloChoque / dist, p.getY() * moduloChoque / dist );
			return p;
		} else if (objeto2 instanceof Bloque) {
			Bloque b = (Bloque) objeto2;
			if (b.getX()<=x+radio/2 && b.getX()+b.getAnchura()>=x-radio/2) {
				if (b.getY()<=y+radio && b.getY()+b.getAltura()/2>y) {  // UDcito est� chocando encima del bloque
					Point2D p = new Point2D.Double( 0.0, y+radio-b.getY() );
					return p;
				} else if (b.getY()+b.getAltura()>=y-radio && b.getY()+b.getAltura()/2<y) {  // UDcito est� chocando debajo del bloque
					Point2D p = new Point2D.Double( 0.0, y-radio-b.getY()-b.getAltura() );
					return p;
				}
			}
			return null;
		} else {
			return null;
		}
	}
	
	//
	// M�todos de animaci�n
	//
	
	/** Actualiza la informaci�n interna de animaci�n del objeto, de acuerdo al tiempo de animaci�n.
	 * En la siguiente llamada al dibujado, se dibujar� el gr�fico que corresponda al estado de animaci�n.
	 * @param msgs	Valor de milisegundos absoluto de la animaci�n. En funci�n de la diferencia con la �ltima actualizaci�n, se actualiza el ciclo de animaci�n.
	 */
	public void refrescaAnimacion( long msgs ) {
		long dif = msgs - msgsAnimacion;
		if (dif >= cicloAnimsEstadoMs.get(estadoActual)) {  // Si han pasado m�s milisegundos que los de animaci�n en el estado actual
			ArrayList<String> anim = animaciones.get( estados.get(estadoActual) );
			animActual++; if (animActual>=anim.size()) animActual = 0;
			msgsAnimacion = msgs;
		}
	}
	
	/** Devuelve el estado de animaci�n actual del objeto
	 * @return	Nombre del estado actual
	 */
	public String getEstadoAnimacion() {
		return estados.get(estadoActual);
	}
	
	/** Devuelve los posibles estados de animaci�n del objeto
	 * @return	Lista de nombres v�lidos de estados de animaci�n
	 */
	public ArrayList<String> getEstadosAnimacion() {
		return estados;
	}
	
	/** Cambia el estado de animaci�n del objeto
	 * @param estado	Nombre del nuevo estado a activar. Debe ser un nombre v�lido, si no lo es el estado de animaci�n no cambia.
	 */
	public void setEstadoAnimacion( String estado ) {
		int nuevoEstado = estados.indexOf( estado );
		if (nuevoEstado>-1 && estadoActual!=nuevoEstado) {
			estadoActual = nuevoEstado;
			animActual = 0;
		}
	}
	
}

