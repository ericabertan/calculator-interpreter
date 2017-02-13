import java.lang.Exception;
import java.util.ArrayList;

class ASTExp {

	public enum tipos {
		NENHUM,
		INTEIRO_LITERAL,
		INVERTE_SINAL
	}

	public String tipo;
	public double valor;
	public ASTExp operando1;
	public ASTExp operando2;
	public String operacao;
	public ArrayList<ASTExp> sequenciaExpressoes;

	public ASTExp condicao;
	public ASTExp expressaoThen;
	public ASTExp expressaoElse;
	/*public ASTExp() {
		self.tipo = "NENHUM";
		self.valor = 0;
	}*/



}
// Esta classe se refere a um item da Tabela de Símbolos
class Objeto {
	public ASTExp expressao;
	public String tipo;
	public Escopo escopo;
	public Objeto proximoObjeto;
}

// Esta classe indica o escopo atual das variáveis
class Escopo {
	public String identificador;
	public Escopo escopoSuperior;
	public Objeto objetoSuperior;
}

class Interpreter {

	//TabelaSymb ts;

	public Escopo escopoAtual;


	public ASTExp eval(ASTExp exp) throws Exception {

		System.out.println(exp.tipo);
		if(exp.tipo == "LITERAL_DOUBLE")
			return exp;
		/*if (exp.tipo == "SOMA") {
			return new ASTExp(eval(exp.op1) + eval(exp.op2));
		} else (exp.tipo == VARIAVEL) {
			double k = exp.var.valor;
			return new ASTExp(k);
		} else (exp.tipo == INTEIRO_LITERAL) {
			return new ASTExp(exp.valor);
		} else (exp.tipo*/

		return null;
	}
}
