package financeiro.entidade;
import java.util.List;

import financeiro.util.DAOFactory;
public class EntidadeRN {
	private EntidadeDAO	entidadeDAO;
	public EntidadeRN() {
		this.entidadeDAO = DAOFactory.criarEntidadeDAO();
	}
	public void salvar(Entidade entidade) {
		this.entidadeDAO.salvar(entidade);
	}
	public void excluir(Entidade entidade) {
		this.entidadeDAO.excluir(entidade);
	}
	public Entidade carregar(Integer entidade) {
		return this.entidadeDAO.carregar(entidade);
	}
	public List<Entidade> listar() { 
		return this.entidadeDAO.listar();
	}
}
