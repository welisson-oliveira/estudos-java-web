package financeiro.entidade;

import java.util.Date;
import java.util.List;

public interface EntidadeDAO {
	public void salvar(Entidade entidade);
	public void excluir(Entidade entidade);
	public Entidade carregar(Integer entidade);
	public List<Entidade> listar();
}
