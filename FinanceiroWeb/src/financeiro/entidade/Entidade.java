package financeiro.entidade;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import financeiro.lancamento.Lancamento;

@Entity
@Table(name = "entidade")
public class Entidade implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1066674780752712046L;

	@Id
	@GeneratedValue		
	@Column(name = "codigo")
	private Long id;
	
	@Column(name = "fav_for")
	private String nome;
	
//	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
//	@JoinColumn(name = "codigo", updatable = false)
//	@org.hibernate.annotations.OrderBy(clause = "nome asc")
//	private List<Lancamento> lancamentos;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

//	public List<Lancamento> getLancamentos() {
//		return lancamentos;
//	}
//
//	public void setLancamentos(List<Lancamento> lancamentos) {
//		this.lancamentos = lancamentos;
//	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Entidade other = (Entidade) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		return true;
	}

//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((id == null) ? 0 : id.hashCode());
//		result = prime * result
//				+ ((lancamentos == null) ? 0 : lancamentos.hashCode());
//		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
//		return result;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		Entidade other = (Entidade) obj;
//		if (id == null) {
//			if (other.id != null)
//				return false;
//		} else if (!id.equals(other.id))
//			return false;
//		if (lancamentos == null) {
//			if (other.lancamentos != null)
//				return false;
//		} else if (!lancamentos.equals(other.lancamentos))
//			return false;
//		if (nome == null) {
//			if (other.nome != null)
//				return false;
//		} else if (!nome.equals(other.nome))
//			return false;
//		return true;
//	}

	
	
	
}
