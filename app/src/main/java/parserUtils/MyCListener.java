package parserUtils;

import cUtils.CBaseListener;
import cUtils.CParser.PrimaryExpressionContext;

public class MyCListener extends CBaseListener {
	
	@Override
	public void enterPrimaryExpression(PrimaryExpressionContext ctx){
		System.out.println(ctx.getText());
	}

}
