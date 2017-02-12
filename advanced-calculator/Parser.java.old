

import java.lang.Exception;



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
		System.out.println(" " + exp);
		} catch (Exception e) {
		System.err.println(" Error: " + e.getMessage());
		}
		
		Expect(0);
	}

	ASTExp  CExp() {
		ASTExp  v;
		v = null; 
		if (la.kind == 3) {
			Def();
		} else if (StartOf(1)) {
			Exp();
		} else SynErr(25);
		return v;
	}

	void Def() {
		Expect(3);
		Expect(2);
		if (la.kind == 4) {
			Get();
			ParamNames();
			Expect(5);
		} else if (la.kind == 6) {
		} else SynErr(26);
		Expect(6);
		Exp();
	}

	void Exp() {
		T();
		while (la.kind == 8 || la.kind == 9) {
			if (la.kind == 8) {
				Get();
				T();
			} else {
				Get();
				T();
			}
		}
	}

	void ParamNames() {
		Expect(2);
		while (la.kind == 7) {
			Get();
			Expect(2);
		}
	}

	void T() {
		U();
		while (la.kind == 10 || la.kind == 11 || la.kind == 12) {
			if (la.kind == 10) {
				Get();
				U();
			} else if (la.kind == 11) {
				Get();
				U();
			} else {
				Get();
				U();
			}
		}
	}

	void U() {
		if (la.kind == 9) {
			Get();
			F();
		} else if (StartOf(2)) {
			F();
		} else SynErr(27);
	}

	void F() {
		if (la.kind == 1) {
			Get();
		} else if (la.kind == 2) {
			VarOrFunc();
		} else if (la.kind == 16) {
			IFExp();
		} else if (la.kind == 4) {
			Get();
			Exp();
			Expect(5);
		} else if (la.kind == 13) {
			Get();
			SeqExp();
			Expect(14);
		} else SynErr(28);
	}

	void VarOrFunc() {
		Expect(2);
		if (la.kind == 4) {
			Get();
			Params();
			Expect(5);
		} else if (StartOf(3)) {
		} else SynErr(29);
	}

	void IFExp() {
		Expect(16);
		ExpL();
		Expect(17);
		Exp();
		Expect(18);
		Exp();
	}

	void SeqExp() {
		Exp();
		while (la.kind == 15) {
			Get();
			Exp();
		}
	}

	void Params() {
		Exp();
		while (la.kind == 7) {
			Get();
			Exp();
		}
	}

	void ExpL() {
		Exp();
		OpRel();
		Exp();
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
			case 14: s = "\"}\" expected"; break;
			case 15: s = "\";\" expected"; break;
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
