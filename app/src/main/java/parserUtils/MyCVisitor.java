package parserUtils;

import org.antlr.v4.runtime.Token;

import java.util.LinkedList;

public class MyCVisitor {
	
	public LinkedList<Integer> visit(AbstractSyntaxTreeConverter ast, LinkedList<Integer> father) {
		int i;
		Token token = null;
		
		if(father == null) {
			father = new LinkedList<Integer>();
			father.add(0);
		}
		
		switch(ast.getPayload().toString()){
			case "inicio":
//				ast.setPrevious(father);
				ast.setType("inicio");
				
				father = new LinkedList<Integer>();
				father.add(ast.getId());
			break;
			
			case "fin":
				ast.setPrevious(father);
				ast.setType("fin");
				
				father = new LinkedList<Integer>();
//				father.add(ast.getId());
			break;
			
			case "selectionStatement":
				token = (Token) ast.getChildren().get(0).getPayload();
				switch(token.getText()){
					case "if":
						/********************IF - ELSE********************/
						System.out.println("IF " + ast.getChildren().get(ast.findChildren("expression",0)).getChildrenContent());
						
						ast.setPrevious(father);
						ast.setType("decisión");
						ast.setContent(ast.getChildren().get(ast.findChildren("expression",0)).getChildrenContent());
						
						i = ast.findChildren("statement",0);
						father = visit(ast.getChildren().get(i), ast.getIdAsList());
						
						i++;
						if(ast.getChildren().size() > i) {
							//HAY UN ELSE
							if(ast.getChildren().get(i).getPayload() instanceof Token){
								
								token = (Token) ast.getChildren().get(i).getPayload();
								
								if(token.getText().equals("else")) {
									
									System.out.println("CONSULTA-ELSE ");
									father.addAll(visit(ast.getChildren().get(ast.findChildren("statement",1)),ast.getIdAsList()));
								}
							}
						} else {
							System.out.println("CONSULTA-SIN-ELSE ");
							father.add(ast.getId());
						}
						System.out.println("IF-FIN ");
						
					break;
					
					case "switch":
						/********************SWITCH********************/
						LinkedList<Integer> parentsFromTrueSide = new LinkedList<>();
						
						System.out.println("SWITCH "+ ast.getChildren().get(ast.findChildren("expression",0)).getChildrenContent());
						
						String expression = ast.getChildren().get(ast.findChildren("expression",0)).getChildrenContent();
						father = caseVisit(ast, father,parentsFromTrueSide, expression);
						
						father.addAll(parentsFromTrueSide);
						
						System.out.println("SWITCH-FIN ");
					break;
				}
			break;
			
			case "iterationStatement":
				token = (Token) ast.getChildren().get(0).getPayload();
				
				AbstractSyntaxTreeConverter finBucle = new AbstractSyntaxTreeConverter("finBucle"+ast.getId());
				ast.addChildren(finBucle);
				
				finBucle.setType("finBucle"+ast.getId());
				finBucle.setID(ast.getId()+1);
				
				switch(token.getText()){
					case "while":
						/********************WHILE********************/
						ast.setPrevious(father);
						ast.setType("bucle");
						ast.setContent("while(" + ast.getChildren().get(ast.findChildren("expression",0)).getChildrenContent() + ")");
						
						System.out.println("WHILE "+ ast.getChildren().get(ast.findChildren("expression",0)).getChildrenContent());
						father = visit(ast.getChildren().get(ast.findChildren("statement",0)),null/*ast.getIdAsList()*/);
						System.out.println("WHILE-FIN ");
						
					break;
					
					case "do":
						/********************DO********************/
						ast.setPrevious(father);
						ast.setType("bucle");
						ast.setContent("do... while(" + ast.getChildren().get(ast.findChildren("expression",0)).getChildrenContent() + ")");
						
						System.out.println("DO "+ ast.getChildren().get(ast.findChildren("expression",0)).getChildrenContent());
						father = visit(ast.getChildren().get(ast.findChildren("statement",0)),null/*ast.getIdAsList()*/);
						System.out.println("DO-FIN ");
						
					break;
					
					case "for":
						/********************FOR********************/
						ast.setPrevious(father);
						ast.setType("bucle");
						ast.setContent("for(" + ast.getChildren().get(ast.findChildren("forCondition",0)).getChildrenContent() + ")");
						
						System.out.println("FOR "+ ast.getChildren().get(ast.findChildren("forCondition",0)).getChildrenContent());
						father = visit(ast.getChildren().get(ast.findChildren("statement",0)),null/*ast.getIdAsList()*/);
						System.out.println("FOR-FIN ");
						
					break;
				}
				
				finBucle.setPrevious(father);
				//father = finBucle.getIdAsList();
				father = ast.getIdAsList();
			break;
			
			//EXPRESIONES, FUNCIONES Y ASIGNACIONES VARIAS
			case "expression": case "initDeclaratorList":
				System.out.println("ASIGNACION/LLAMADA "+ ast.getChildrenContent());
				
				ast.setPrevious(father);
				ast.setType("proceso");
				ast.setContent(ast.getChildrenContent());
				
				father = new LinkedList<Integer>();
				father.add(ast.getId());
			break;
		
			default:
				for (i = 0; i < ast.getChildren().size(); i++) {
		            if (!(ast.getPayload() instanceof Token)) {
		                
		            	//SOLO BAJAR AL HIJO SI NO ES UN TOKEN
		            	if(father.contains(ast.getId())){
		            		father = visit(ast.getChildren().get(i),ast.getIdAsList());
		            	} else {
		            		LinkedList<Integer> aux = new LinkedList<Integer>();
		            		aux = visit(ast.getChildren().get(i),father);
		            		
		            		father = aux;
		            	}
		            }
				}
			break;
			
		}
		return father;
	}
	
	public LinkedList<Integer> caseVisit(AbstractSyntaxTreeConverter ast, LinkedList<Integer> father,LinkedList<Integer> parentsFromTrueSide, String expression) {
		
		int childrenNo = 0;
		AbstractSyntaxTreeConverter caseChildren = null;
		int i;
		
        if (!(ast.getPayload() instanceof Token)) {
        	childrenNo = ast.findChildren("labeledStatement", 0);
    		
    		if(!ast.getChildren().get(childrenNo).getPayload().toString().equals("labeledStatement")) {
    			for (i = 0; i < ast.getChildren().size(); i++) {
	            	//SOLO BAJAR AL HIJO SI NO ES UN TOKEN
	            	if(father.contains(ast.getId())){
	            		father = caseVisit(ast.getChildren().get(i),ast.getIdAsList(),parentsFromTrueSide, expression);
	            	} else {
	            		LinkedList<Integer> aux = new LinkedList<Integer>();
	            		aux = caseVisit(ast.getChildren().get(i),father, parentsFromTrueSide,expression);
	            		
	            		father = aux;
	            	}
    			}
            } else {
    			caseChildren = ast.getChildren().get(childrenNo);
    			
    			caseChildren.setPrevious(father);
    			caseChildren.setType("decisión");
    			String condition = caseChildren.getChildren().get(caseChildren.findChildren("constantExpression",0)).getChildrenContent();
    			if(condition.isEmpty()) {
    				caseChildren.setContent("true");
    			} else {
    				caseChildren.setContent(expression + " = " + caseChildren.getChildren().get(caseChildren.findChildren("constantExpression",0)).getChildrenContent());
    			}
    			
    			
    			parentsFromTrueSide.addAll(visit(caseChildren.getChildren().get(caseChildren.findChildren("statement",0)), caseChildren.getIdAsList()));
    			
    			father = new LinkedList<Integer>();
    			father.add(caseChildren.getId());
    			}
        }
		
		return father;
	}
	
	
}
