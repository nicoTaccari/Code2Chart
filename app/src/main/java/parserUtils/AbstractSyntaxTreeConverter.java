package parserUtils;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AbstractSyntaxTreeConverter {
	/**
     * The payload will either be the name of the parser rule, or the token
     * of a leaf in the tree.
     */
    private final Object payload;
    private int id = 0;
    private LinkedList<Integer> previous = new LinkedList<Integer>();
    private String type;
    private String content;
    
    /**
     * All child nodes of this AST.
     */
    private final List<AbstractSyntaxTreeConverter> children;
    
//PARA AGREGAR UN NODO INICIO Y UN NODO FIN
    public AbstractSyntaxTreeConverter(String token) {
        this.payload = token;
        this.children = new LinkedList<>();
    }
    
    public void addChildren(AbstractSyntaxTreeConverter ast) {
    	this.children.add(ast);
    }
//END    
    
    public AbstractSyntaxTreeConverter(ParseTree tree) {
        this(null, tree);
    }

    private AbstractSyntaxTreeConverter(AbstractSyntaxTreeConverter ast, ParseTree tree) {
        this(ast, tree, new ArrayList<AbstractSyntaxTreeConverter>());
    }

    private AbstractSyntaxTreeConverter(AbstractSyntaxTreeConverter parent,
    		ParseTree tree, List<AbstractSyntaxTreeConverter> children) {

        this.payload = getPayload(tree);
        this.children = children;

        if (parent == null) {
            // We're at the root of the AST, traverse down the parse tree to fill
            // this AST with nodes.
            walk(tree, this);
        }
        else {
            parent.children.add(this);
        }
    }

    public Object getPayload() {
        return payload;
    }

    public List<AbstractSyntaxTreeConverter> getChildren() {
        return new ArrayList<>(children);
    }

    // Determines the payload of this AST: a string in case it's an inner node (which
    // is the name of the parser rule), or a Token in case it is a leaf node.
    private Object getPayload(ParseTree tree) {
        if (tree.getChildCount() == 0) {
            // A leaf node: return the tree's payload, which is a Token.
            return tree.getPayload();
        }
        else {
            // The name for parser rule `foo` will be `FooContext`. Strip `Context` and
            // lower case the first character.
            String ruleName = tree.getClass().getSimpleName().replace("Context", "");
            return Character.toLowerCase(ruleName.charAt(0)) + ruleName.substring(1);
        }
    }

    // Fills this AST based on the parse tree.
    private static void walk(ParseTree tree, AbstractSyntaxTreeConverter ast) {

        if (tree.getChildCount() == 0) {
            // We've reached a leaf. We must create a new instance of an AST because
            // the constructor will make sure this new instance is added to its parent's
            // child nodes.
            new AbstractSyntaxTreeConverter(ast, tree);
        }
        /*else if (tree.getChildCount() == 1) {
            // We've reached an inner node with a single child: we don't include this in
            // our AST.
            walk(tree.getChild(0), ast);
        }*/
        else if (tree.getChildCount() > 0) {

            for (int i = 0; i < tree.getChildCount(); i++) {

            	AbstractSyntaxTreeConverter temp = new AbstractSyntaxTreeConverter(ast, tree.getChild(i));

                if (!(temp.payload instanceof Token)) {
                    // Only traverse down if the payload is not a Token.
                    walk(tree.getChild(i), temp);
                }
            }
        }
    }
    
    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();

        AbstractSyntaxTreeConverter ast = this;
        List<AbstractSyntaxTreeConverter> firstStack = new ArrayList<>();
        firstStack.add(ast);

        List<List<AbstractSyntaxTreeConverter>> childListStack = new ArrayList<>();
        childListStack.add(firstStack);

        while (!childListStack.isEmpty()) {

            List<AbstractSyntaxTreeConverter> childStack = childListStack.get(childListStack.size() - 1);

            if (childStack.isEmpty()) {
                childListStack.remove(childListStack.size() - 1);
            }
            else {
                ast = childStack.remove(0);
                String caption;

                if (ast.payload instanceof Token) {
                    Token token = (Token) ast.payload;
                    caption = String.format("TOKEN[type: %s, text: %s]",
                            token.getType(), token.getText().replace("\n", "\\n"));
                }
                else {
                    caption = String.valueOf(ast.payload);
                }

                String indent = "";

                for (int i = 0; i < childListStack.size() - 1; i++) {
                    indent += (childListStack.get(i).size() > 0) ? "|  " : "   ";
                }
                
               // if(0 != ast.previous){
                builder.append(indent)
                        .append(childStack.isEmpty() ? "'- " : "|- ")
                        .append(caption)
                        .append(" ID = " + ast.id)
                        .append(" PREVIOUS = " + ast.previous)
                        .append("\n");
                //}
                if (ast.children.size() > 0) {
                    List<AbstractSyntaxTreeConverter> children = new ArrayList<>();
                    for (int i = 0; i < ast.children.size(); i++) {
                        children.add(ast.children.get(i));
                    }
                    childListStack.add(children);
                }
            }
        }

        return builder.toString();
    }

    public String getChildrenContent() {
    	return getFullContent(this.getChildren());
    }
    
	private String getTokenContent() {
		String s;
		
		if(this.getPayload() instanceof Token){
			
			Token token = (Token) this.payload;
			s = token.getText();
		} else {
			s = getFullContent(this.children);
		}
		return s;
	}
	
	public String getFullContent(List<AbstractSyntaxTreeConverter> children){
        String s = new String();

        for (AbstractSyntaxTreeConverter child : children) {
            s = s.concat(child.getTokenContent());
        }
        return s;
    }
	
	public int findChildren(String key, int repeticiones) {
		int i, tries = 0;
		for(i=0;i<this.getChildren().size();i++) {
			if(this.getChildren().get(i).getPayload().toString().equals(key)) {
				if(repeticiones == tries) {
					return i;
				} else {
					tries++;
				}
			}
		}
		
		return 0;
	}

	public int setID(int myID) {
		this.id = myID;
		
		for(int i = 0;i<this.getChildren().size();i++){
			myID++;
			myID = this.getChildren().get(i).setID(myID);
		}
		return myID;
	}

	public int getId() {
		return id;
	}
	
	public LinkedList<Integer> getIdAsList(){
		
		LinkedList<Integer> list = new LinkedList<Integer>();
		list.add(this.id);
		return list;
	}

	public LinkedList<Integer> getPrevious() {
		return previous;
	}

	public void setPrevious(LinkedList<Integer> prev) {
		this.previous.addAll(prev);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
