package financeiro.cheque;

import java.util.List;

import financeiro.conta.Conta;

public interface ChequeDAO {
	public void salvar(Cheque cheque);
	public void excluir(Cheque cheque);
	public Cheque carregar(ChequeId chequeId);
	public List<Cheque> listar(Conta conta);
}
