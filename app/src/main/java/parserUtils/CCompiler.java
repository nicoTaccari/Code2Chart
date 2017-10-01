package parserUtils;

import org.antlr.v4.runtime.*;

import cUtils.CLexer;
import cUtils.CParser;
import exceptions.UnableToParseFileException;


public class CCompiler {
	
	public AbstractSyntaxTreeConverter compile(String file){
		AbstractSyntaxTreeConverter ast = null;
		
		try {
			ANTLRInputStream input = new ANTLRInputStream(file);
			CLexer lexer = new CLexer(input);
			CParser parser = new CParser(new CommonTokenStream(lexer));
			
			CParser.CompilationUnitContext tree = parser.compilationUnit();
			
			ast = new AbstractSyntaxTreeConverter("FullAst");
			AbstractSyntaxTreeConverter beginAst = new AbstractSyntaxTreeConverter("inicio");
			AbstractSyntaxTreeConverter codeAst = new AbstractSyntaxTreeConverter(tree);
			AbstractSyntaxTreeConverter endAst = new AbstractSyntaxTreeConverter("fin");
			
			ast.addChildren(beginAst);
			ast.addChildren(codeAst);
			ast.addChildren(endAst);
			
			ast.setID(0);

		} catch (Exception e) {
			new UnableToParseFileException();
		}
		
		return ast;
	}
}


	