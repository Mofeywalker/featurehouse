package de.ovgu.cide.fstgen.ast;

import java.util.LinkedList;
import java.util.List;

import com.google.gson.*;

import de.ovgu.cide.fstgen.ast.FSTTerminal.FSTTerminalSerializer;

import java.lang.reflect.Type;

public class FSTNonTerminal extends FSTNode {

	private List<FSTNode> children;

	public FSTNonTerminal(String type, String name) {
		super(type, name);
		this.children = new LinkedList<FSTNode>();
	}

	public FSTNonTerminal(String type, String name, List<FSTNode> children) {
		super(type, name);
		this.children = children;
		for (FSTNode child : children)
			child.setParent(this);
	}

	@Override
	public FSTNode getShallowClone() {
		return new FSTNonTerminal(getType(), getName(),
				new LinkedList<FSTNode>());
	}

	@Override
	public FSTNode getDeepClone() {
		LinkedList<FSTNode> cloneChildren = new LinkedList<FSTNode>();
		for (FSTNode child : getChildren())
			cloneChildren.add(child.getDeepClone());
		return new FSTNonTerminal(getType(), getName(), cloneChildren);
	}

	public List<FSTNode> getChildren() {
		return children;
	}

	/** replaces all children of this node <p>needed for FST processing
	 * @param newChildren - new children to set */
	public void setChildren(List<FSTNode> newChildren) {
		this.children = newChildren;
	}

	public void addChild(FSTNode child) {
		child.setParent(this);
		children.add(child);
	}

	public void addChild(FSTNode child, int index) {
		child.setParent(this);
		children.add(index, child);
	}

	public FSTNode getCompatibleChild(FSTNode node) {
		for (FSTNode child : getChildren()) {
			if (child.compatibleWith(node))
				return child;
		}
		return null;
	}

	public String toStringShort() {
		//return "[NT -> " + getName() + " : " + getType() + "]";
		//////////////
		Gson gsonNonTerminal = new GsonBuilder().registerTypeAdapter(FSTNonTerminal.class, new FSTNonTerminalSerializer()).create();
		return gsonNonTerminal.toJson(this);
		//////////////
	}

	@Override
	public String toString() {
		if (getType().equals("Feature") && children.isEmpty())
			//////////////////
			//return "";
			return "FeatureName:" + getFeatureName();
			/////////////////
		String shortS = toStringShort();
		return shortS + printChildrenList();
	}
	
	///////////////////////////
	public String export() {
		if (getType().equals("Feature") && children.isEmpty())
			//////////////////
			//return "";
			return "FeatureName:" + getFeatureName();
			/////////////////
		String shortS = toStringShort();
		return shortS + exportChildrenList();
	}
	
	public String exportChildrenList() {
		level++;
		Gson gsonTerminal = new GsonBuilder().registerTypeAdapter(FSTTerminal.class, new FSTTerminalSerializer()).create();
		Gson gsonNonTerminal = new GsonBuilder().registerTypeAdapter(FSTNonTerminal.class, new FSTNonTerminalSerializer()).create();
		
		String result = "";
		for (int idx = 0; idx < children.size(); idx++) {
			// if (idx != 0)
			result += "\n";
			for (int i = 0; i < level; i++)
				result += "\t";
			
			if (children.get(idx) instanceof FSTTerminal) {
				result += gsonTerminal.toJson((FSTTerminal)children.get(idx));
			} else {
				result += gsonNonTerminal.toJson((FSTNonTerminal)children.get(idx));
				result += children.get(idx).export();
			}
			
		}
		level--;
		return result;
	}
	
	
	public static class FSTNonTerminalSerializer implements JsonSerializer<FSTNonTerminal> {
        public JsonElement serialize(final FSTNonTerminal node, final Type type, final JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            result.add("name", new JsonPrimitive(node.getName()));
            result.add("type", new JsonPrimitive(node.getType()));
            result.add("feature", new JsonPrimitive(node.getFeatureName()));

            return result;
        }
	}
	
	//////////////////////////

	private static int level = 0;

	private String printChildrenList() {
		level++;

		String result = "";
		for (int idx = 0; idx < children.size(); idx++) {
			// if (idx != 0)
			result += "\n";
			for (int i = 0; i < level; i++)
				result += "\t";
			result += children.get(idx).toString();
		}
		level--;
		return result;
	}

	public String printFST(int indent) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < indent; i++)
			buffer.append("    ");
		buffer.append(this.toStringShort());
		buffer.append("\n");
		for (FSTNode n : children) {
			buffer.append(n.printFST(indent + 1));
		}
		return buffer.toString();
	}

	@Override
	public void accept(FSTVisitor visitor) {
		boolean visitInner = visitor.visit(this);
		if (visitInner)
			for (FSTNode child : children)
				child.accept(visitor);
		visitor.postVisit(this);
	}

	public void removeChild(FSTNode node) {
		children.remove(node);
	}

}
