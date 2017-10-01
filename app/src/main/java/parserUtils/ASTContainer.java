package parserUtils;

import java.util.LinkedList;

public class ASTContainer {
	int id;	
	LinkedList<Integer> father;
	
	String content;
	String tipo;
	
	public ASTContainer(int id, LinkedList<Integer> father, String content, String tipo) {
		this.id = id;
		this.father = father;
		this.content = content;
		this.tipo = tipo;
	}
	
	public int getId() {
		return id;
	}
	
	public LinkedList<Integer> getFather() {
		return father;
	}
	
	public String getContent() {
		return content;
	}
	
	public String getTipo() {
		return tipo;
	}
	
}
