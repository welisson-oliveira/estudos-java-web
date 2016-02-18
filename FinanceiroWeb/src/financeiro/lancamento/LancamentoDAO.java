package financeiro.lancamento;

import java.util.Date;
import java.util.List;

import financeiro.conta.Conta;

public interface LancamentoDAO {

	public void salvar(Lancamento lancamento);
	public void excluir(Lancamento lancamento);
	public Lancamento carregar(Integer lancamento);
	public List<Lancamento> listar(Conta conta, Date dataInicio, Date dataFim);
	public float saldo(Conta conta, Date data);
}
