package decaf.translate;

import java.util.Stack;
import java.util.Iterator;

import decaf.tree.Tree;
import decaf.backend.OffsetCounter;
import decaf.machdesc.Intrinsic;
import decaf.symbol.Variable;
import decaf.symbol.Class;
import decaf.symbol.Symbol;
import decaf.tac.Label;
import decaf.tac.Temp;
import decaf.type.BaseType;
import decaf.type.ClassType;
import decaf.scope.ClassScope;

public class TransPass2 extends Tree.Visitor {

	private Translater tr;

	private Temp currentThis;

	private Stack<Label> loopExits;

	public TransPass2(Translater tr) {
		this.tr = tr;
		loopExits = new Stack<Label>();
	}

	@Override
	public void visitClassDef(Tree.ClassDef classDef) {
		for (Tree f : classDef.fields) {
			f.accept(this);
		}
	}

	@Override
	public void visitMethodDef(Tree.MethodDef funcDefn) {
		if (!funcDefn.statik) 
		{
			currentThis = ((Variable) funcDefn.symbol.getAssociatedScope()
					.lookup("this")).getTemp();
		}
		tr.beginFunc(funcDefn.symbol);
		funcDefn.body.accept(this);
		tr.endFunc();
		currentThis = null;
	}

	@Override
	public void visitTopLevel(Tree.TopLevel program) {
		for (Tree.ClassDef cd : program.classes) {
			cd.accept(this);
		}
	}

	@Override
	public void visitVarDef(Tree.VarDef varDef) {
		if (varDef.symbol.isLocalVar()) {
			Temp t = Temp.createTempI4();
			t.sym = varDef.symbol;
			varDef.symbol.setTemp(t);
		}
	}

	@Override
	public void visitBinary(Tree.Binary expr) {
		expr.left.accept(this);
		expr.right.accept(this);
		if(expr.left.type.equal(BaseType.COMPLEX) || expr.right.type.equal(BaseType.COMPLEX))
		{
			Temp re = null;//= Temp.createTempI4();
			Temp im = null;//= Temp.createTempI4();

			Temp re1 = expr.left.type.equal(BaseType.COMPLEX)? tr.genLoad(expr.left.val, 0) : expr.left.val;
			Temp im1 = expr.left.type.equal(BaseType.COMPLEX)? tr.genLoad(expr.left.val, 4) : tr.genLoadImm4(0);
			Temp re2 = expr.right.type.equal(BaseType.COMPLEX)? tr.genLoad(expr.right.val, 0) : expr.right.val;
			Temp im2 = expr.right.type.equal(BaseType.COMPLEX)? tr.genLoad(expr.right.val, 4) : tr.genLoadImm4(0);

			switch (expr.tag)
			{
				case Tree.PLUS:
					re = tr.genAdd(re1, re2);
					im = tr.genAdd(im1, im2);
					break;
				case Tree.MUL:
					re = tr.genSub(tr.genMul(re1, re2), tr.genMul(im1, im2));
					im = tr.genAdd(tr.genMul(re1, im2), tr.genMul(re2, im1));
					break;
			}

			expr.val = tr.genComplex(re, im);
		}
		else

		switch (expr.tag) {
		case Tree.PLUS:
			expr.val = tr.genAdd(expr.left.val, expr.right.val);
			break;
		case Tree.MINUS:
			expr.val = tr.genSub(expr.left.val, expr.right.val);
			break;
		case Tree.MUL:
			expr.val = tr.genMul(expr.left.val, expr.right.val);
			break;
		case Tree.DIV:
			tr.genCheckDivByZero(expr.right.val);
			expr.val = tr.genDiv(expr.left.val, expr.right.val);
			break;
		case Tree.MOD:
			tr.genCheckDivByZero(expr.right.val);
			expr.val = tr.genMod(expr.left.val, expr.right.val);
			break;
		case Tree.AND:
			expr.val = tr.genLAnd(expr.left.val, expr.right.val);
			break;
		case Tree.OR:
			expr.val = tr.genLOr(expr.left.val, expr.right.val);
			break;
		case Tree.LT:
			expr.val = tr.genLes(expr.left.val, expr.right.val);
			break;
		case Tree.LE:
			expr.val = tr.genLeq(expr.left.val, expr.right.val);
			break;
		case Tree.GT:
			expr.val = tr.genGtr(expr.left.val, expr.right.val);
			break;
		case Tree.GE:
			expr.val = tr.genGeq(expr.left.val, expr.right.val);
			break;
		case Tree.EQ:
		case Tree.NE:
			genEquNeq(expr);
			break;
		}
	}

	private void genEquNeq(Tree.Binary expr) {
		if (expr.left.type.equal(BaseType.STRING)
				|| expr.right.type.equal(BaseType.STRING)) {
			tr.genParm(expr.left.val);
			tr.genParm(expr.right.val);
			expr.val = tr.genDirectCall(Intrinsic.STRING_EQUAL.label,
					BaseType.BOOL);
			if(expr.tag == Tree.NE){
				expr.val = tr.genLNot(expr.val);
			}
		} else {
			if(expr.tag == Tree.EQ)
				expr.val = tr.genEqu(expr.left.val, expr.right.val);
			else
				expr.val = tr.genNeq(expr.left.val, expr.right.val);
		}
	}

	@Override
	public void visitAssign(Tree.Assign assign) {
		assign.left.accept(this);
		assign.expr.accept(this);

		Temp ae = assign.expr.val;

		//if (assign.expr.type.equal(BaseType.COMPLEX))
		//	ae = tr.genComplex(tr.genLoad(ae, 0), tr.genLoad(ae, 4));

		switch (assign.left.lvKind) {
		case ARRAY_ELEMENT:
			Tree.Indexed arrayRef = (Tree.Indexed) assign.left;
			Temp esz = tr.genLoadImm4(OffsetCounter.WORD_SIZE);
			Temp t = tr.genMul(arrayRef.index.val, esz);
			Temp base = tr.genAdd(arrayRef.array.val, t);
			tr.genStore(ae, base, 0);
			break;
		case MEMBER_VAR:
			Tree.Ident varRef = (Tree.Ident) assign.left;
			tr.genStore(ae, varRef.owner.val, varRef.symbol.getOffset());
			break;
		case PARAM_VAR:
		case LOCAL_VAR:
			tr.genAssign(((Tree.Ident) assign.left).symbol.getTemp(),ae);
			break;
		}
	}

	@Override
	public void visitLiteral(Tree.Literal literal) {
		switch (literal.typeTag) {
		case Tree.INT:
			literal.val = tr.genLoadImm4(((Integer)literal.value).intValue());
			break;
		case Tree.BOOL:
			literal.val = tr.genLoadImm4((Boolean)(literal.value) ? 1 : 0);
			break;
		case Tree.COMPLEX:
			literal.val = tr.genComplex(tr.genLoadImm4(0), 
					tr.genLoadImm4(((Integer)literal.value).intValue())
					);
			break;
		default:
			literal.val = tr.genLoadStrConst((String)literal.value);
		}
	}

	@Override
	public void visitExec(Tree.Exec exec) {
		exec.expr.accept(this);
	}

	@Override
	public void visitUnary(Tree.Unary expr) {
		expr.expr.accept(this);
		switch (expr.tag){
		case Tree.NEG:
			expr.val = tr.genNeg(expr.expr.val);
			break;
		case Tree.NOT:
			expr.val = tr.genLNot(expr.expr.val);
			break;
		case Tree.RE:
			expr.val = tr.genLoad(expr.expr.val, 0);
			break;
		case Tree.IM:
			expr.val = tr.genLoad(expr.expr.val, 4);
			break;
		case Tree.COMPCAST:
			Temp comp = tr.genComplex(expr.expr.val, tr.genLoadImm4(0));
			expr.val = comp;	
			break;
		default:
			System.out.println("????");
			assert(false);
		}
	}

	@Override
	public void visitNull(Tree.Null nullExpr) {
		nullExpr.val = tr.genLoadImm4(0);
	}

	@Override
	public void visitBlock(Tree.Block block) {
		for (Tree s : block.block) {
			s.accept(this);
		}
	}

	@Override
	public void visitThisExpr(Tree.ThisExpr thisExpr) {
		thisExpr.val = currentThis;
	}

	@Override
	public void visitSuperExpr(Tree.SuperExpr superExpr) {
		superExpr.val = currentThis;
	}


	@Override
	public void visitReadIntExpr(Tree.ReadIntExpr readIntExpr) {
		readIntExpr.val = tr.genIntrinsicCall(Intrinsic.READ_INT);
	}

	@Override
	public void visitReadLineExpr(Tree.ReadLineExpr readStringExpr) {
		readStringExpr.val = tr.genIntrinsicCall(Intrinsic.READ_LINE);
	}

	@Override
	public void visitReturn(Tree.Return returnStmt) {
		if (returnStmt.expr != null) {
			returnStmt.expr.accept(this);
			tr.genReturn(returnStmt.expr.val);
		} else {
			tr.genReturn(null);
		}

	}

	@Override
	public void visitPrint(Tree.Print printStmt) {
		for (Tree.Expr r : printStmt.exprs) {
			r.accept(this);
			tr.genParm(r.val);
			if (r.type.equal(BaseType.BOOL)) {
				tr.genIntrinsicCall(Intrinsic.PRINT_BOOL);
			} else if (r.type.equal(BaseType.INT)) {
				tr.genIntrinsicCall(Intrinsic.PRINT_INT);
			} else if (r.type.equal(BaseType.STRING)) {
				tr.genIntrinsicCall(Intrinsic.PRINT_STRING);
			}
		}
	}

	@Override
	public void visitPrintComp(Tree.PrintComp printCompStmt)
	{
		for (Tree.Expr r : printCompStmt.exprs) 
		{
			r.accept(this);
			tr.genParm(tr.genLoad(r.val, 0));
			tr.genIntrinsicCall(Intrinsic.PRINT_INT);
			
			tr.genParm(tr.genLoadStrConst("+"));
			tr.genIntrinsicCall(Intrinsic.PRINT_STRING);
			
			tr.genParm(tr.genLoad(r.val, 4));
			tr.genIntrinsicCall(Intrinsic.PRINT_INT);
			
			tr.genParm(tr.genLoadStrConst("j"));
			tr.genIntrinsicCall(Intrinsic.PRINT_STRING);
		}
	}

	@Override
	public void visitIndexed(Tree.Indexed indexed) {
		indexed.array.accept(this);
		indexed.index.accept(this);
		tr.genCheckArrayIndex(indexed.array.val, indexed.index.val);
		
		Temp esz = tr.genLoadImm4(OffsetCounter.WORD_SIZE);
		Temp t = tr.genMul(indexed.index.val, esz);
		Temp base = tr.genAdd(indexed.array.val, t);
		indexed.val = tr.genLoad(base, 0);
	}

	@Override
	public void visitIdent(Tree.Ident ident) {
		if(ident.lvKind == Tree.LValue.Kind.MEMBER_VAR){
			ident.owner.accept(this);
		}
		
		switch (ident.lvKind) {
		case MEMBER_VAR:
			ident.val = tr.genLoad(ident.owner.val, ident.symbol.getOffset());
			break;
		default:
			ident.val = ident.symbol.getTemp();
			break;
		}
	}
	
	@Override
	public void visitBreak(Tree.Break breakStmt) {
		tr.genBranch(loopExits.peek());
	}

	@Override
	public void visitCallExpr(Tree.CallExpr callExpr) {
		if (callExpr.isArrayLength) {
			callExpr.receiver.accept(this);
			callExpr.val = tr.genLoad(callExpr.receiver.val,
					-OffsetCounter.WORD_SIZE);
		} else {
			if (callExpr.receiver != null) {
				callExpr.receiver.accept(this);
			}
			for (Tree.Expr expr : callExpr.actuals) {
				expr.accept(this);
			}
			if (callExpr.receiver != null) {
				tr.genParm(callExpr.receiver.val);
			}
			for (Tree.Expr expr : callExpr.actuals) {
				tr.genParm(expr.val);
			}
			if (callExpr.receiver == null) {
				callExpr.val = tr.genDirectCall(
						callExpr.symbol.getFuncty().label, callExpr.symbol
								.getReturnType());
			} else {
				Temp vt = tr.genLoad(callExpr.receiver.val, 0);
				
				if(callExpr.receiver.tag == Tree.THISEXPR 
						|| callExpr.receiver.tag == Tree.SUPEREXPR)
				{
					ClassType ct = (ClassType)callExpr.receiver.type;
					if(callExpr.receiver.tag == Tree.SUPEREXPR)
						ct = ct.getParentType();
					vt = tr.genLoadVTable(ct.getSymbol().getVtable());
				}
				
				Temp func = tr.genLoad(vt, callExpr.symbol.getOffset());
				callExpr.val = tr.genIndirectCall(func, callExpr.symbol
						.getReturnType());
			}
		}

	}
// new_do
	@Override
	public void visitDoStmt(Tree.DoStmt dos)
	{
		Label startDo = Label.createLabel();
		Label exit = Label.createLabel();
		loopExits.push(exit);
		tr.genMark(startDo);

		for(Tree.DoBranch db:dos.doBranchList)
		{
			db.condition.accept(this);
			Label nextBranch = Label.createLabel();
			tr.genBeqz(db.condition.val, nextBranch);
			
			db.stmt.accept(this);
			
			tr.genBranch(startDo);
			tr.genMark(nextBranch);
		}

		tr.genMark(exit);
		loopExits.pop();
	}


// new_case
	@Override
	public void visitCase(Tree.Case c)
	{
		Temp caseResult = Temp.createTempI4();
		Temp caseCondition = Temp.createTempI4();
		c.condition.accept(this);
		tr.genAssign(caseCondition, c.condition.val);

		Label exit = Label.createLabel(); 
		for(Tree.ACaseExpr ac:c.caseList)
		{
			ac.caseCont.accept(this);
			ac.expr.accept(this);

			tr.genAssign(caseResult, ac.expr.val);
			tr.genBeqz(tr.genNeq(caseCondition, ac.caseCont.val), exit);

		}
		
		c.defaultExpr.accept(this);
		tr.genAssign(caseResult, c.defaultExpr.val);
		tr.genMark(exit);
		c.val = caseResult;
	}

	@Override
	public void visitForLoop(Tree.ForLoop forLoop) {
		if (forLoop.init != null) {
			forLoop.init.accept(this);
		}
		Label cond = Label.createLabel();
		Label loop = Label.createLabel();
		tr.genBranch(cond);
		tr.genMark(loop);
		if (forLoop.update != null) {
			forLoop.update.accept(this);
		}
		tr.genMark(cond);
		forLoop.condition.accept(this);
		Label exit = Label.createLabel();
		tr.genBeqz(forLoop.condition.val, exit);
		loopExits.push(exit);
		if (forLoop.loopBody != null) {
			forLoop.loopBody.accept(this);
		}
		tr.genBranch(loop);
		loopExits.pop();
		tr.genMark(exit);
	}

	@Override
	public void visitIf(Tree.If ifStmt) {
		ifStmt.condition.accept(this);
		if (ifStmt.falseBranch != null) {
			Label falseLabel = Label.createLabel();
			tr.genBeqz(ifStmt.condition.val, falseLabel);
			ifStmt.trueBranch.accept(this);
			Label exit = Label.createLabel();
			tr.genBranch(exit);
			tr.genMark(falseLabel);
			ifStmt.falseBranch.accept(this);
			tr.genMark(exit);
		} else if (ifStmt.trueBranch != null) {
			Label exit = Label.createLabel();
			tr.genBeqz(ifStmt.condition.val, exit);
			if (ifStmt.trueBranch != null) {
				ifStmt.trueBranch.accept(this);
			}
			tr.genMark(exit);
		}
	}

	@Override
	public void visitNewArray(Tree.NewArray newArray) {
		newArray.length.accept(this);
		newArray.val = tr.genNewArray(newArray.length.val);
	}

	@Override
	public void visitNewClass(Tree.NewClass newClass) {
		newClass.val = tr.genDirectCall(newClass.symbol.getNewFuncLabel(),
				BaseType.INT);
	}
	
	@Override
	public void visitSCopy(Tree.SCopy sc)
	{
		sc.expr.accept(this);
		sc.val = tr.genDirectCall(
				((ClassType)sc.expr.type)
				.getSymbol().getNewFuncLabel(),
				BaseType.INT);
		ClassScope cs = ((ClassType)sc.expr.type)
				.getSymbol().getAssociatedScope();

		Iterator<Symbol> iter = cs.iterator();
		Temp tmp = Temp.createTempI4();
		while(iter.hasNext())
		{
			Symbol sym = iter.next();
		//	System.out.println(sym);
			if(sym.isVariable())
			{
				int offset =((Variable)sym).getOffset();
				tr.genAssign(tmp, tr.genLoad(sc.expr.val, offset));
				tr.genStore(tmp, sc.val, offset);
			}
		}
	}

	@Override
	public void visitDCopy(Tree.DCopy dc)
	{
		dc.expr.accept(this);
		dc.val = deepCopy((ClassType)dc.expr.type, dc.expr.val);
	}

	private Temp deepCopy(ClassType srcType, Temp srcVal )
	{
		Temp dst;
		dst = tr.genDirectCall(
				srcType.getSymbol().getNewFuncLabel(),
				BaseType.INT);
		
		ClassScope cs = ((ClassType)srcType)
				.getSymbol().getAssociatedScope();


		Iterator<Symbol> iter = cs.iterator();
		Temp tmp = Temp.createTempI4();
		while(iter.hasNext())
		{
			Symbol sym = iter.next();
//			System.out.println(sym);
			if(sym.isVariable())
			{
				int offset =((Variable)sym).getOffset();
				if(sym.getType().isClassType())
					tr.genAssign(tmp, deepCopy((ClassType)sym.getType(), srcVal));
				else
					tr.genAssign(tmp, tr.genLoad(srcVal, offset));
				tr.genStore(tmp, dst, offset);
			}
		}
		return dst;
	}

	@Override
	public void visitWhileLoop(Tree.WhileLoop whileLoop) {
		Label loop = Label.createLabel();
		tr.genMark(loop);
		whileLoop.condition.accept(this);
		Label exit = Label.createLabel();
		tr.genBeqz(whileLoop.condition.val, exit);
		loopExits.push(exit);
		if (whileLoop.loopBody != null) {
			whileLoop.loopBody.accept(this);
		}
		tr.genBranch(loop);
		loopExits.pop();
		tr.genMark(exit);
	}

	@Override
	public void visitTypeTest(Tree.TypeTest typeTest) {
		typeTest.instance.accept(this);
		typeTest.val = tr.genInstanceof(typeTest.instance.val,
				typeTest.symbol);
	}

	@Override
	public void visitTypeCast(Tree.TypeCast typeCast) {
		typeCast.expr.accept(this);
		if (!typeCast.expr.type.compatible(typeCast.symbol.getType())) {
			tr.genClassCast(typeCast.expr.val, typeCast.symbol);
		}
		typeCast.val = typeCast.expr.val;
	}
}
