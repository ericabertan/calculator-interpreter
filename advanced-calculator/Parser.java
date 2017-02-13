

import java.lang.Exception;
import java.util.ArrayList;



public class Parser {
	public static final int _EOF = 0;
	public static final int _number = 1;
	public static final int _ident = 2;
	public static final int maxT = 24;

	static final boolean T = true;
	static final boolean x = false;
	static final int minErrDist = 2;

	public Token t;    // last recognized token
	public Token la;   // lookahead token
	int errDist = minErrDist;
	
	public Scanner scanner;
	public Errors errors;

	Interpreter aci;

	public void setInterpreter(Interpreter aci) {
		this.aci = aci;
	}



	public Parser(Scanner scanner) {
		this.scanner = scanner;
		errors = new Errors();
	}

	void SynErr (int n) {
		if (errDist >= minErrDist) errors.SynErr(la.line, la.col, n);
		errDist = 0;
	}

	public void SemErr (String msg) {
		if (errDist >= minErrDist) errors.SemErr(t.line, t.col, msg);
		errDist = 0;
	}
	
	void Get () {
		for (;;) {
			t = la;
			la = scanner.Scan();
			if (la.kind <= maxT) {
				++errDist;
				break;
			}

			la = t;
		}
	}
	
	void Expect (int n) {
		if (la.kind==n) Get(); else { SynErr(n); }
	}
	
	boolean StartOf (int s) {
		return set[s][la.kind];
	}
	
	void ExpectWeak (int n, int follow) {
		if (la.kind == n) Get();
		else {
			SynErr(n);
			while (!StartOf(follow)) Get();
		}
	}
	
	boolean WeakSeparator (int n, int syFol, int repFol) {
		int kind = la.kind;
		if (kind == n) { Get(); return true; }
		else if (StartOf(repFol)) return false;
		else {
			SynErr(n);
			while (!(set[syFol][kind] || set[repFol][kind] || set[0][kind])) {
				Get();
				kind = la.kind;
			}
			return StartOf(syFol);
		}
	}
	
	void AdvCalc() {
		ASTExp exp; 
		exp = CExp();
		try {
		ASTExp res = aci.eval(exp);
		System.out.println(" " + res.valor);
		} catch (Exception e) {
		System.err.println(" Error: " + e.getMessage());
		}
		
		Expect(0);
	}

	ASTExp  CExp() {
		ASTExp  v;
		v = null; ASTExp exp; 
		if (la.kind == 3) {
			exp = Def();
			v = exp; 
		} else if (StartOf(1)) {
			exp = Exp();
			v = exp; 
		} else SynErr(25);
		return v;
	}

	ASTExp  Def() {
		ASTExp  def;
		Objeto objeto = null;
		def = new ASTExp();
		ASTExp exp;
		
		Expect(3);
		Expect(2);
		String identificador = t.val;
		ArrayList<ASTExp> listaParametros;
		
		if (la.kind == 4) {
			Get();
			ParamNames();
			Expect(5);
		} else if (la.kind == 6) {
		} else SynErr(26);
		Expect(6);
		exp = Exp();
		objeto.expressao = exp;
		if(objeto.tipo == "FUNCAO"){
		//aci = aci.escopoAtual.escopoSuperior;
		}
		
		return def;
	}

	ASTExp  Exp() {
		ASTExp  exp;
		exp = null;
		ASTExp t;
		ASTExp operando;
		
		t = T();
		exp = t; 
		while (la.kind == 8 || la.kind == 9) {
			operando = exp;
			exp = new ASTExp();
			ASTExp node = new ASTExp();
			node.tipo = "OPERAÃÃO";
			node.operando1 = operando;
			t = node;
			
			if (la.kind == 8) {
				Get();
				t = T();
				exp.operando2 = t;
				exp.operacao = "+";
				
			} else {
				Get();
				t = T();
				exp.operando2 = t;
				exp.operacao = "-";
				
			}
		}
		return exp;
	}

	void ParamNames() {
		Expect(2);
		while (la.kind == 7) {
			Get();
			Expect(2);
		}
	}

	ASTExp  T() {
		ASTExp  t;
		t = null;
		ASTExp u;
		ASTExp operando;
		
		u = U();
		t = u; 
		while (la.kind == 10 || la.kind == 11 || la.kind == 12) {
			operando = t;
			t = new ASTExp();
			ASTExp node = new ASTExp();
			node.tipo = "OPERACAO";
			node.operando1 = operando;
			t = node;
			
			if (la.kind == 10) {
				Get();
				u = U();
				t.operando2 = u;
				t.operacao = "*";
				
			} else if (la.kind == 11) {
				Get();
				u = U();
				t.operando2 = u;
				t.operacao = "/";
				
			} else {
				Get();
				u = U();
				t.operando2 = u;
				t.operacao = "%";
				
			}
		}
		return t;
	}

	ASTExp  U() {
		ASTExp  u;
		u = null;
		ASTExp f;
		
		if (la.kind == 9) {
			Get();
			f = F();
			ASTExp node = new ASTExp();
			u = new ASTExp();
			node.tipo = "SINAL";
			node.operando1 = f;
			u = node;
			
		} else if (StartOf(2)) {
			f = F();
			u = f;
			
		} else SynErr(27);
		return u;
	}

	ASTExp  F() {
		ASTExp  f;
		f = null;
		ASTExp exp;
		
		if (la.kind == 1) {
			Get();
			ASTExp node = new ASTExp();
			f = new ASTExp();
			node.tipo = "LITERAL_DOUBLE";
			node.valor = Double.parseDouble(t.val);
			f = node;
			
		} else if (la.kind == 2) {
			exp = VarOrFunc();
			f = exp; 
		} else if (la.kind == 16) {
			exp = IFExp();
			f = exp; 
		} else if (la.kind == 4) {
			Get();
			exp = Exp();
			Expect(5);
			f = exp; 
		} else if (la.kind == 13) {
			exp = SeqExp();
			f = exp; 
		} else SynErr(28);
		return f;
	}

	ASTExp  VarOrFunc() {
		ASTExp  varFunc;
		varFunc = null;
		ASTExp exp;
		ArrayList<ASTExp> params;
		
		Expect(2);
		String valorVariavel = t.val; 
		if (la.kind == 4) {
			Get();
			params = Params();
			Expect(5);
		} else if (StartOf(3)) {
		} else SynErr(29);
		return varFunc;
	}

	ASTExp  IFExp() {
		ASTExp  ifCond;
		ASTExp exp;
		ASTExp condicao;
		ifCond = new ASTExp();
		ifCond.tipo = "CONDICAO";
		
		Expect(16);
		condicao = ExpL();
		ifCond.condicao = condicao; 
		Expect(17);
		exp = Exp();
		ifCond.expressaoThen = exp; 
		Expect(18);
		exp = Exp();
		ifCond.expressaoElse = exp; 
		return ifCond;
	}

	ASTExp  SeqExp() {
		ASTExp  seq;
		seq = new ASTExp();
		seq.tipo = "SEQUENCIA_EXPRESSOES";
		seq.sequenciaExpressoes = new ArrayList<ASTExp>();
		ASTExp exp;
		Objeto objeto = new Objeto();
		// TO-DO implementar sequencia de expressoes
		//objeto.novaSequenciaExpressoes();
		// TO-DO implementar escopo atual
		
		Expect(13);
		exp = CExp();
		seq.sequenciaExpressoes.add(exp); 
		while (la.kind == 14) {
			Get();
			exp = CExp();
			seq.sequenciaExpressoes.add(exp); 
		}
		Expect(15);
		this.aci.escopoAtual = aci.escopoAtual.escopoSuperior; 
		return seq;
	}

	ArrayList<ASTExp>  Params() {
		ArrayList<ASTExp>  parametros;
		parametros = new ArrayList<ASTExp>();
		ASTExp exp;
		
		exp = Exp();
		parametros.add(exp); 
		while (la.kind == 7) {
			Get();
			exp = Exp();
			parametros.add(exp); 
		}
		return parametros;
	}

	ASTExp  ExpL() {
		ASTExp  expl;
		ASTExp exp;
		String operacao = "";
		expl = new ASTExp();
		expl.tipo = "OPERACAO";
		
		exp = Exp();
		expl.operando1 = exp; 
		OpRel();
		exp = Exp();
		expl.operando2 = exp; 
		return expl;
	}

	void OpRel() {
		switch (la.kind) {
		case 19: {
			Get();
			break;
		}
		case 20: {
			Get();
			break;
		}
		case 21: {
			Get();
			break;
		}
		case 22: {
			Get();
			break;
		}
		case 6: {
			Get();
			break;
		}
		case 23: {
			Get();
			break;
		}
		default: SynErr(30); break;
		}
	}



	public void Parse() {
		la = new Token();
		la.val = "";		
		Get();
		AdvCalc();
		Expect(0);

	}

	private static final boolean[][] set = {
		{T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,T,x, T,x,x,x, x,T,x,x, x,T,x,x, T,x,x,x, x,x,x,x, x,x},
		{x,T,T,x, T,x,x,x, x,x,x,x, x,T,x,x, T,x,x,x, x,x,x,x, x,x},
		{T,x,x,x, x,T,T,T, T,T,T,T, T,x,T,T, x,T,T,T, T,T,T,T, x,x}

	};
} // end Parser


class Errors {
	public int count = 0;                                    // number of errors detected
	public java.io.PrintStream errorStream = System.out;     // error messages go to this stream
	public String errMsgFormat = "-- line {0} col {1}: {2}"; // 0=line, 1=column, 2=text
	
	protected void printMsg(int line, int column, String msg) {
		StringBuffer b = new StringBuffer(errMsgFormat);
		int pos = b.indexOf("{0}");
		if (pos >= 0) { b.delete(pos, pos+3); b.insert(pos, line); }
		pos = b.indexOf("{1}");
		if (pos >= 0) { b.delete(pos, pos+3); b.insert(pos, column); }
		pos = b.indexOf("{2}");
		if (pos >= 0) b.replace(pos, pos+3, msg);
		errorStream.println(b.toString());
	}
	
	public void SynErr (int line, int col, int n) {
		String s;
		switch (n) {
			case 0: s = "EOF expected"; break;
			case 1: s = "number expected"; break;
			case 2: s = "ident expected"; break;
			case 3: s = "\"def\" expected"; break;
			case 4: s = "\"(\" expected"; break;
			case 5: s = "\")\" expected"; break;
			case 6: s = "\"=\" expected"; break;
			case 7: s = "\",\" expected"; break;
			case 8: s = "\"+\" expected"; break;
			case 9: s = "\"-\" expected"; break;
			case 10: s = "\"*\" expected"; break;
			case 11: s = "\"/\" expected"; break;
			case 12: s = "\"%\" expected"; break;
			case 13: s = "\"{\" expected"; break;
			case 14: s = "\";\" expected"; break;
			case 15: s = "\"}\" expected"; break;
			case 16: s = "\"if\" expected"; break;
			case 17: s = "\"then\" expected"; break;
			case 18: s = "\"else\" expected"; break;
			case 19: s = "\">\" expected"; break;
			case 20: s = "\">=\" expected"; break;
			case 21: s = "\"<\" expected"; break;
			case 22: s = "\"&<=\" expected"; break;
			case 23: s = "\"!=\" expected"; break;
			case 24: s = "??? expected"; break;
			case 25: s = "invalid CExp"; break;
			case 26: s = "invalid Def"; break;
			case 27: s = "invalid U"; break;
			case 28: s = "invalid F"; break;
			case 29: s = "invalid VarOrFunc"; break;
			case 30: s = "invalid OpRel"; break;
			default: s = "error " + n; break;
		}
		printMsg(line, col, s);
		count++;
	}

	public void SemErr (int line, int col, String s) {	
		printMsg(line, col, s);
		count++;
	}
	
	public void SemErr (String s) {
		errorStream.println(s);
		count++;
	}
	
	public void Warning (int line, int col, String s) {	
		printMsg(line, col, s);
	}
	
	public void Warning (String s) {
		errorStream.println(s);
	}
} // Errors


class FatalError extends RuntimeException {
	public static final long serialVersionUID = 1L;
	public FatalError(String s) { super(s); }
}
