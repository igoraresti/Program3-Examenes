/**
 * 
 */
package recursividad.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import recursividad.main.EjerciciosRecursividad;

/**
 * @author igoraresti
 *
 */
public class EjerciciosRecursividadTest {


  @Before
  public void before() {

  }

  @Test
  public void factorial() {
    assertEquals(120, EjerciciosRecursividad.factorial(5));
  }

  @Test
  public void fibonacci() {
    assertEquals(5, EjerciciosRecursividad.fibonnaci(5));
    assertEquals(21, EjerciciosRecursividad.fibonnaci(8));
  }

  @Test
  public void count7() {
    assertEquals(2, EjerciciosRecursividad.count7(23477));
    assertEquals(1, EjerciciosRecursividad.count7(178));
  }

  @Test
  public void countX() {
    assertEquals(2, EjerciciosRecursividad.countX("axrxw"));
    assertEquals(1, EjerciciosRecursividad.countX("aaaax"));
  }

  @Test
  public void strCount() {
    assertEquals(2, EjerciciosRecursividad.strCount("catcowcat", "cat"));
    assertEquals(1, EjerciciosRecursividad.strCount("catcowcat", "cow"));
    assertEquals(0, EjerciciosRecursividad.strCount("catcowcat", "dog"));
  }

  @Test
  public void noX() {
    assertEquals("ab", EjerciciosRecursividad.noX("xaxb"));
    assertEquals("abc", EjerciciosRecursividad.noX("abc"));
    assertEquals("", EjerciciosRecursividad.noX("xx"));
  }

  @Test
  public void powerN() {
    assertEquals(3, EjerciciosRecursividad.powerN(3, 1));
    assertEquals(9, EjerciciosRecursividad.powerN(3, 2));
    assertEquals(27, EjerciciosRecursividad.powerN(3, 3));
  }

  @Test
  public void strCopies() {
    assertTrue(EjerciciosRecursividad.strCopies("catcowcat", "cat", 2));
    assertFalse(EjerciciosRecursividad.strCopies("catcowcat", "cow", 2));
    assertTrue(EjerciciosRecursividad.strCopies("catcowcat", "cow", 1));
  }

  @Test
  public void changeXY() {
    assertEquals("codex", EjerciciosRecursividad.changeXY("codey"));
    assertEquals("xxhixx", EjerciciosRecursividad.changeXY("yyhiyy"));
    assertEquals("xhixhix", EjerciciosRecursividad.changeXY("yhiyhiy"));
  }

  @Test
  public void allStar() {
    assertEquals("h*o*l*a", EjerciciosRecursividad.allStar("hola"));
    assertNotEquals("a*g*u*r*", EjerciciosRecursividad.allStar("agur"));
  }

  @Test
  public void countPairs() {
    assertEquals(1, EjerciciosRecursividad.countPairs("axa"));
    assertEquals(2, EjerciciosRecursividad.countPairs("axax"));
    assertEquals(2, EjerciciosRecursividad.countPairs("axxbcx"));
  }
}
