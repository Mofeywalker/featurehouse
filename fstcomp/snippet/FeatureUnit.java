package snippet;

import java.util.List;

//import com.google.gson.Gson;

import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTNonTerminal;

/**
 * This class represents a CompilationUnit with the annotation to which feature this CU belongs.
 * 
 * @author mfey
 *
 */
public class FeatureUnit {
	
	/**
	 * Name of the Feature to which this FeatureUnit belongs.
	 */
	private String featureName;
	
	/**
	 * Root Node of the CompilationUnit.
	 */
	private FSTNonTerminal compilationUnitRoot;
	
	/**
	 * List of children.
	 */
	private List<FSTNode> children;
	
	public FeatureUnit(String _featureName, FSTNonTerminal _compilationUnitRoot, List<FSTNode> _children) {
		this.featureName = _featureName;
		this.compilationUnitRoot = _compilationUnitRoot;
		this.children = _children;
	}
	
	public String export() {
		return compilationUnitRoot.export();
	}
}
