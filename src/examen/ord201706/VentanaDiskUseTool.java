package examen.ord201706;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

@SuppressWarnings("serial")
public class VentanaDiskUseTool extends JFrame {
  private JTreeExpansible tree;
  private NodoConTamanyo raiz;
  private DefaultTreeModel modeloArbol;
  private JTable table;
  private DefaultTableModel modeloTabla;
  private JTextArea taMensajes;

  public VentanaDiskUseTool(String tituloRaiz, String textoRaiz) {
    if (textoRaiz == null || textoRaiz.isEmpty())
      textoRaiz = "Inicio";
    setTitle("Espacio ocupado en disco desde " + tituloRaiz);
    setSize(800, 600);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLocationRelativeTo(null);
    // Paneles desplazables
    JSplitPane pVertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    getContentPane().add(pVertical, BorderLayout.CENTER);
    JSplitPane pCentral = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    pVertical.setTopComponent(pCentral);
    // Texto inferior
    taMensajes = new JTextArea(10, 10);
    taMensajes.setEditable(false);
    JScrollPane spMensajes = new JScrollPane(taMensajes);
    pVertical.setBottomComponent(spMensajes);
    pVertical.setDividerLocation(450);
    // Arbol izquierdo
    tree = new JTreeExpansible();
    JScrollPane spArbol = new JScrollPane(tree);
    pCentral.setLeftComponent(spArbol);
    raiz = new NodoConTamanyo(textoRaiz, new ConteoDir());
    modeloArbol = new DefaultTreeModel(raiz);
    tree.setModel(modeloArbol);
    tree.setCellRenderer(new TreeNodeTamanyoRenderer());
    // Tabla central
    modeloTabla = new DefaultTableModel(new Object[] {"Nombre", "Tama�o", "Fecha"}, 0);
    table = new JTable(modeloTabla);
    table.getColumn("Nombre").setPreferredWidth(100);
    table.getColumn("Tama�o").setPreferredWidth(50);
    table.getColumn("Fecha").setPreferredWidth(50);

    JScrollPane spTabla = new JScrollPane(table);
    pCentral.setRightComponent(spTabla);
    pCentral.setDividerLocation(450);
    // TAREA Renderer
    table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
      @Override
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
          boolean hasFocus, int row, int column) {
        Component rend =
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (modeloTabla.getValueAt(row, 1).equals("") && column == 0) {
          rend.setBackground(Color.YELLOW);
        } else {
          rend.setBackground(Color.WHITE);
        }
        return rend;
      }
    });
    // Eventos
    tree.addTreeSelectionListener(new TreeSelectionListener() {
      @Override
      public void valueChanged(TreeSelectionEvent e) {
        ConteoDir cd = ((NodoConTamanyo) (e.getPath().getLastPathComponent())).datos;
        while (modeloTabla.getRowCount() > 0)
          modeloTabla.removeRow(0); // Vaciar el modelo
        if (cd.getDirectorio() != null && cd.getDirectorio().listFiles() != null) {
          for (File f : cd.getDirectorio().listFiles()) {
            String espacio = (f.isDirectory()) ? "" : Utils.getEspacioDesc(f.length());
            modeloTabla
                .addRow(new Object[] {f.getName(), espacio, Utils.sdf.format(f.lastModified())});
          }
        }
      }
    });
    // TAREA Hilo - parada de recorrido
  }

  /**
   * A�ade un mensaje al final de la zona de texto inferior (si se quiere salto de l�nea, hay que
   * incluirlo en el mensaje
   * 
   * @param mensaje Mensaje a a�adir
   */
  public void addMensaje(String mensaje) {
    taMensajes.append(mensaje);
    taMensajes.setSelectionStart(taMensajes.getText().length());
    taMensajes.setSelectionEnd(taMensajes.getText().length());
  }

  /**
   * Borra la zona inferior de mensajes
   */
  public void clearMensajes() {
    taMensajes.setText("");
    taMensajes.setSelectionStart(0);
    taMensajes.setSelectionEnd(0);
  }

  /**
   * Crea un nuevo nodo hijo en el �rbol
   * 
   * @param texto Texto del nodo
   * @param datos Datos del nodo
   * @param padre Nodo padre del que crear el nodo hijo. Si es null, se crea como hijo de la ra�z
   *        principal del �rbol
   * @return Nodo hijo reci�n creado
   */
  public NodoConTamanyo anyadeNodoHijo(String texto, ConteoDir datos, final NodoConTamanyo padre) {
    final NodoConTamanyo nuevo = new NodoConTamanyo(texto, datos);
    Runnable r = new Runnable() {
      NodoConTamanyo miNuevo = nuevo;
      NodoConTamanyo miPadre = padre;

      @Override
      public void run() {
        if (padre == null) {
          raiz.add(miNuevo);
          modeloArbol.nodesWereInserted(raiz, new int[] {raiz.getChildCount() - 1});
        } else {
          miPadre.add(nuevo);
          modeloArbol.nodesWereInserted(padre, new int[] {padre.getChildCount() - 1});
        }
        tree.setES(new TreePath(miNuevo.getPath()), true);
      }
    };
    try {
      SwingUtilities.invokeAndWait(r);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return nuevo;
  }


  /**
   * Devuelve el JTree asociado a la ventana
   * 
   * @return jtree de la ventana
   */
  public JTreeExpansible getTree() {
    return tree;
  }

  /**
   * Devuelve la ra�z del modelo de datos asociado al JTree de la ventana
   * 
   * @return ra�z del modelo
   */
  public NodoConTamanyo getRaiz() {
    return raiz;
  }

  /**
   * Devuelve el modelo de datos asociado al JTree de la ventana
   * 
   * @return modelo de datos de tree
   */
  public DefaultTreeModel getModeloArbol() {
    return modeloArbol;
  }


  // -------------------------------------
  // Clases de utilidad para JTree
  // -------------------------------------


  public static class NodoConTamanyo extends DefaultMutableTreeNode {
    protected ConteoDir datos;

    public NodoConTamanyo(String nombre, ConteoDir datos) {
      super(nombre);
      this.datos = datos;
    }

    public ConteoDir getDatos() {
      return datos;
    }
  }


  public static class TreeNodeTamanyoRenderer extends DefaultTreeCellRenderer {
    private JPanel panel;
    private JLabel lTexto;
    // TAREA JProgressBar
    private JProgressBar pbTam;

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
        boolean expanded, boolean leaf, int row, boolean hasFocus) {
      if (panel == null) {
        panel = new JPanel(new BorderLayout());
        // panel.setPreferredSize( new Dimension( 200, 22 ) );
        lTexto = new JLabel();
        lTexto.setOpaque(true);
        // TAREA JProgressBar
        pbTam = new JProgressBar(0, 20);
        pbTam.setOpaque(false);
        panel.add(pbTam, BorderLayout.WEST);
        panel.add(lTexto, BorderLayout.CENTER);
        panel.setOpaque(true);
        panel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        lTexto.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 1));
      }
      JLabel normal = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
          row, hasFocus);
      lTexto.setFont(normal.getFont());
      NodoConTamanyo nodo = (NodoConTamanyo) value;
      // Representar el tama�o de los ficheros
      double subidaLogaritmica = nodo.datos.getTamFicheros() / 1024.0;
      if (subidaLogaritmica < 1)
        subidaLogaritmica = 0;
      else
        subidaLogaritmica = Math.log(subidaLogaritmica);
      // Escala logaritmica: 0 si es < 1024 bytes, de 0.0 a ln(bytes) si es >=.
      pbTam.setValue((int) Math.round(subidaLogaritmica));
      int val = (int) (200 * nodo.datos.getTamFicheros() / 50000000000L); // 230 = 50 Gb aprox
      if (val > 230)
        val = 230;
      val = 230 - val; // Se invierte el color (cuanto más lleno más rojo)
      pbTam.setForeground(new Color(255, val, val));
      // TAREA JProgressBar - Representar en la progress bar tambi�n el n�mero de ficheros
      // Texto en el nodo
      lTexto.setText(normal.getText() + " (" + Utils.getEspacioDesc(nodo.datos.getTamFicheros())
          + String.format(" - %1$,1d fs)", nodo.datos.getNumFicheros()));
      if (sel) {
        lTexto.setBackground(Color.LIGHT_GRAY);
        panel.setBackground(Color.LIGHT_GRAY);
      } else {
        lTexto.setBackground(Color.WHITE);
        panel.setBackground(Color.WHITE);
      }
      return panel;
    }

  }

}
