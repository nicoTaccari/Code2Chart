package parserUtils;

import java.util.LinkedList;

public class ParserToXmlAdapter {
	LinkedList<ASTContainer> list;

	public ParserToXmlAdapter() {
		list = new LinkedList<>();
	}

	public LinkedList<ASTContainer> getConvertedList(AbstractSyntaxTreeConverter ast) {
		/*
		LinkedList<Integer> astCont = new LinkedList<Integer>();
		
		ASTContainer cont = new ASTContainer(0, null, "inicio", "Inicio");
		list.add(cont);
		*/
		convert(ast);
		/*
		astCont.add(list.getLast().getId());
		cont = new ASTContainer(list.getLast().getId()+1, astCont, "fin", "Fin");
		list.add(cont);
		*/
		return list;
	}

	private void convert(AbstractSyntaxTreeConverter ast) {
		if (ast.getType() != null) {
			ASTContainer container = new ASTContainer(ast.getId(), ast.getPrevious(), ast.getContent(), ast.getType());
			list.add(container);
		}
		for (AbstractSyntaxTreeConverter c : ast.getChildren()) {
			convert(c);
		}
	}
}
