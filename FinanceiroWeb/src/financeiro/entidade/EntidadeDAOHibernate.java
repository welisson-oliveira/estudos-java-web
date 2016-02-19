package financeiro.entidade;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

public class EntidadeDAOHibernate implements EntidadeDAO {
	private Session	session;
	public void setSession(Session session) {
		this.session = session;
	}
	
	@Override
	public void salvar(Entidade entidade) {
		this.session.saveOrUpdate(entidade);
	}
	
	@Override
	public void excluir(Entidade entidade) {
		this.session.delete(entidade);
	}
	
	@Override
	public Entidade carregar(Integer entidade) {
		return (Entidade) this.session.get(Entidade.class, entidade);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Entidade> listar() {
		
		String hql = "select e from Entidade e";
		Query query = this.session.createQuery(hql);

		List<Entidade> lista = query.list();

		return lista;
	}
}
