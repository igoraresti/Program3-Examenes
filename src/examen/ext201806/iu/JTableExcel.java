package examen.ext201806.iu;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import examen.ext201806.datos.*;

/** JTable especializada para implementar un miniexcel, con render y edición propia
 * @author andoni
 */
public class JTableExcel extends JTable {
	// Modelo de datos propio
	private static TableExcelModel datos;
	private static final long serialVersionUID = -812979698599600077L;

		private static DefaultTableCellRenderer rendererCentrado = new DefaultTableCellRenderer();
		static {
			rendererCentrado.setHorizontalAlignment( JLabel.CENTER );
			rendererCentrado.setBackground( Color.lightGray );
		}
		
	/** Construye una nueva JTableExcel partiendo de una tabla de datos excel
	 * @param tabla
	 */
	public JTableExcel( TablaDatos tabla ) {
		super( new TableExcelModel(tabla) );
		datos = (TableExcelModel) getModel();
		// Pone el renderer de la columna 0 (cabeceras de fila)
		getColumn("_").setCellRenderer( rendererCentrado );
		// Pone las anchuras mínimas de las columnas y los renderers
		getColumn("_").setMinWidth( 30 );
		for (int i = 1; i<datos.getColumnCount(); i++)
			getColumn( datos.getColumnName(i) ).setMinWidth( 60 );
		// Pone el editor especial de las celdas
		setDefaultEditor( Object.class, miEditor );
		// Quita la selección de filas y columnas
		setRowSelectionAllowed(false);
		setColumnSelectionAllowed(false);
	}

	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		TableCellRenderer ren = null;
		if (datos.getValueAt(row, column)!=null && datos.getValueAt(row, column) instanceof ValorCelda) {
			ren = ((ValorCelda) datos.getValueAt(row, column)).getRenderer();
		} else {
			ren = super.getCellRenderer(row, column);
			((DefaultTableCellRenderer) ren).setBackground( Color.white );
		}
		if (datos.datos.isCeldaSel( new RefCelda( row, column ) ) ) {
			((DefaultTableCellRenderer) ren).setBackground( Color.yellow );  // Color de selección = amarillo
		}
		return ren;
	}

		private TableCellEditor miEditor = new MiCellEditor();
			class MiCellEditor extends DefaultCellEditor {
				private static final long serialVersionUID = -174118777882643028L;
				MiCellEditor() {
					super( new JTextField() );
				}

				@Override
				public Component getTableCellEditorComponent(JTable table, Object value,
						boolean isSelected, int row, int column) {
					Component comp = super.getTableCellEditorComponent( table, value, isSelected, row, column );
					if (value != null && value instanceof ValorCelda) {
						if (comp instanceof JTextField)
							((JTextField)comp).setText( ((ValorCelda)value).getTextoEdicion() );
					}
					return comp;
				}
			};
		
	@Override
	public void setModel(TableModel t) {
		if (t instanceof TableExcelModel)
			super.setModel( t );
		else
			throw new IncompatibleClassChangeError( "No se puede asignar cualquier modelo de datos a una JTableExcel" );
	}

	/** Devuelve el modelo de datos de la tabla
	 * @return
	 */
	public TableExcelModel getJTableExcelModel() {
		return (TableExcelModel) getModel();
	}
	
	/** Cambia el objeto de datos de la tabla
	 * @return
	 */
	public void setTableExcelModel( TablaDatos te ) {
		setModel( new TableExcelModel(te) );
		datos = (TableExcelModel) getModel();
	}


	/** Clase privada que implementa el modelo de datos para usar en JTableExcel
	 * partiendo de un objeto de tipo TablaExcel
	 */
	private static class TableExcelModel extends AbstractTableModel {
	    // Datos del modelo
		private TablaDatos datos;
		private static final long serialVersionUID = 7026825532332562011L;

	    /** Constructor de modelo de tabla
	     * @param tabla	Instancia de tabla sobre la que se define el modelo	
	     */
	    public TableExcelModel( TablaDatos tabla ) {
	    	datos = tabla;
	    }
	    
	    public int getColumnCount() {
	        return datos.getColumnas()+1;  // Las columnas + la de nombre de fila
	    }

	    public int getRowCount() {
	        return datos.getFilas();  // Las filas
	    }

	    // Devuelve el nombre de la columna: _ para la de nombre de fila, "A" .. "Z" .. etc. para el resto
	    public String getColumnName(int col) {
	    	if (col==0) return "_";
	    	else return datos.getNomColumna(col-1);
	    }

	    public Object getValueAt(int row, int col) {
	    	if (col==0) return row+1;
	    	else return datos.get( row, col-1 );
	    }

	    /* Identifica qué celdas pueden editarse
	     */
	    public boolean isCellEditable(int row, int col) {
	    	if (col==0) return false;
	    	else return true;
	    }

	    /* Método para cambiar (editar) valores de celdas
	     * si el objeto no era del tipo ValorCelda,
	     * se crea el objeto al que pueda convertirse 
	     */
	    public void setValueAt(Object value, int row, int col) {
	    	if (col==0) return;
	    	if (datos.get(row, col-1) != null && datos.get(row,col-1) instanceof ValorCelda) {
	    		// Cambio de valor de celda con dato
	    		boolean cambio = ((ValorCelda)datos.get(row,col-1)).actualizaTexto( value.toString() );
	    		if (cambio) {
	    			hayCambioEnDato( row, col-1 );
	    			fireTableCellUpdated(row, col);  // Notifica a escuchadores de cambio de celda
	    		}
	    	} else if (value instanceof String) {
	    		// Nuevo valor en celda anteriormente vacía
	    		ValorCelda vc = null;
	    		try {
	    			double d = Double.parseDouble( (String)value );
	    			vc = new Numero( d );
	    		} catch (NumberFormatException e) {
	    			boolean esFormula = false;
	    			if (((String)value).startsWith("=")) {  // Posible fórmula
	    				vc = new FormulaSuma( new RefCelda(0,0), new RefCelda(0,0), datos );
	    				esFormula = vc.actualizaTexto((String)value);
	    			}
	    			if (!esFormula) {
	    				vc = new Texto( (String)value );
	    			}
	    		}
	    		datos.set( row, col-1, vc );
				hayCambioEnDato( row, col-1 );
	            fireTableCellUpdated(row, col);  // Notifica a escuchadores de cambio de celda
	    	}
	    }
	    
	    /** Método privado que se lanza cuando hay algún cambio en el dato indicado de la TablaExcel
	     * @param fila	Fila del dato que cambia
	     * @param columna	Columna del dato que cambia
	     */
	    private void hayCambioEnDato( int fila, int columna ) {
	    	for (int i=0; i<datos.getFilas(); i++) {
	    		for (int j=0; j<datos.getColumnas(); j++) {
	    			if (datos.get(i,j) instanceof FormulaSuma) {
	    				((FormulaSuma)datos.get(i,j)).recalculaValor();
	    				fireTableCellUpdated( i, j );
	    			}
	    		}
	    	}
	    }

	}
	
}



