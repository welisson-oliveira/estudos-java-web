package financeiro.bolsa.acao;

import java.util.List;

import financeiro.usuario.Usuario;

public interface AcaoDAO {
	public void salvar(Acao acao);
	public void excluir(Acao acao);
	public Acao carregar(String codigo);
	public List<Acao> listar(Usuario usuario);
}
