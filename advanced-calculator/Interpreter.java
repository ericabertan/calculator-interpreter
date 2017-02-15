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

	public Objeto variavel;
	public Objeto funcao;

	public ASTExp() {

	}

	public ASTExp(double valor) {
		this.tipo = "LITERAL_DOUBLE";
		this.valor = valor;
	}

}
// Esta classe se refere a um item da Tabela de Símbolos
class Objeto {
	public ASTExp expressao;
	public String tipo;
	public double valor;
	public String identificador;
	public Escopo escopo;
	public Objeto proximoObjeto;
	public ArrayList<String> paramNames;

	public static Objeto addVariavel(String identificador) {
		Objeto novaVariavel = new Objeto();
		novaVariavel.tipo = "VARIAVEL";
		novaVariavel.valor = 0;
		novaVariavel.identificador = identificador;
		//System.out.println(novaVariavel.identificador + " = " + novaVariavel.valor);
		return novaVariavel;
	}

	public static Objeto addFuncao(String identificador) {
		Objeto novaFuncao = new Objeto();
		novaFuncao.tipo = "CLOSURE";
		novaFuncao.escopo = new Escopo("fun:" + identificador);
		return novaFuncao;
	}
}

// Esta classe indica o escopo atual das variáveis
class Escopo {
	public String identificador;
	public Escopo escopoSuperior;
	public Objeto objetoSuperior;
	public ArrayList<Objeto> listaObjetos;
	public String nome;

	public Escopo(String identificador) {
		listaObjetos = new ArrayList<Objeto>();
		nome = identificador;
	}

	public Objeto inserir(Objeto objeto) {
		listaObjetos.add(objeto);
		return objeto;
	}

	public Objeto buscar(String identificador) {
		for (Objeto objeto: listaObjetos) {
			//System.out.println(" " + objeto.identificador + " - " + objeto.tipo + " - " + objeto.valor);
			if(objeto.tipo != "CLOSURE" &&
				 objeto.identificador.equals(identificador)) {
					 return objeto;
				 }
			if(escopoSuperior != null) {
				return escopoSuperior.buscar(identificador);
			}
		}
		return null;
	}

}

class Interpreter {
	public Escopo tabelaSimbolos;
	public Escopo escopoAtual;

	public Interpreter() {
		tabelaSimbolos = new Escopo("global");
		escopoAtual = tabelaSimbolos;
	}

	public ASTExp eval(ASTExp exp) throws Exception {

		System.out.println(exp.tipo);
		if(exp.tipo == "LITERAL_DOUBLE") {
			return new ASTExp(exp.valor);
		} else if(exp.tipo == "VARIAVEL") {
			if(exp.variavel == null)
				return new ASTExp(0);
			else return new ASTExp(exp.variavel.valor);
		} else if (exp.tipo == "DEFINICAO_VARIAVEL") {
			exp.variavel.valor = eval(exp.variavel.expressao).valor;
			return new ASTExp(exp.variavel.valor);
		}


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
