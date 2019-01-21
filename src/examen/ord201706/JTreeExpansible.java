package examen.ord201706;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

/** Clase de utilidad que expone de forma pública el método #setES
 * (equivalente al #setExpandedState, que es protegido en JTree)
 * @author andoni.eguiluz @ ingenieria.deusto.es
 */
@SuppressWarnings("serial")
public class JTreeExpansible extends JTree {
	@Override
	protected void setExpandedState(TreePath path, boolean state) {
		super.setExpandedState(path, state);
	}
	
	public void setES(TreePath path, boolean state) {
		setExpandedState(path, state);
	}
}
	
