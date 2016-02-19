package financeiro.web;

import java.io.Serializable;
import java.util.*;

import javax.faces.bean.*;

import org.primefaces.event.RateEvent;

import financeiro.categoria.Categoria;
import financeiro.conta.Conta;
import financeiro.entidade.Entidade;
import financeiro.entidade.EntidadeRN;
import financeiro.lancamento.*;
import financeiro.util.ContextoUtil;

@ManagedBean(name = "lancamentoBean")
@ViewScoped
public class LancamentoBean implements Serializable {
	private static final long serialVersionUID = -3050807461213326560L;
	private List<Lancamento> lista;
	private Conta conta;
	private List<Double> saldos = new ArrayList<Double>();
	private float saldoGeral;
	private Lancamento editado = new Lancamento();
	private Lancamento selecionado = new Lancamento();
	
	List<Entidade> results = new ArrayList<Entidade>();
	private String entidade;

	@ManagedProperty(value = "#{contextoBean}")
	private ContextoBean contextoBean;
	private List<Lancamento> listaAteHoje;
	private List<Lancamento> listaFuturos;

	public LancamentoBean() {
		this.novo();
	}

	public String novo() {
		this.editado = new Lancamento();
		this.entidade = "";
		this.editado.setData(new Date());
		return null;
	}

	public void editar() {
	}

	public void salvar() {
		
		if(!addEntidade()){
			return;
		}
		
		
		this.editado.setUsuario(this.contextoBean.getUsuarioLogado());
		this.editado.setConta(this.contextoBean.getContaAtiva());

		LancamentoRN lancamentoRN = new LancamentoRN();
		lancamentoRN.salvar(this.editado);

		this.novo();
		this.lista = null;
	}
	
	private boolean addEntidade(){
		System.out.println("--------entidade: "+entidade);
		if(!entidade.equals(null) || !entidade.trim().equals("")){
			for(Entidade e : results){
				if(entidade.equals(e.getNome())){
					editado.setEntidade(e);
					return true;
				}
			}
			
			Entidade ent = new Entidade();
			ent.setNome(entidade);
			new EntidadeRN().salvar(ent);
			editado.setEntidade(ent);
			
			return true;
		}
		return false;
	}

	public void excluir() {
		LancamentoRN lancamentoRN = new LancamentoRN();
		lancamentoRN.excluir(this.editado);
		this.lista = null;
	}

	public List<Lancamento> getLista() {
		if (this.lista == null
				|| this.conta != this.contextoBean.getContaAtiva()) {
			this.conta = this.contextoBean.getContaAtiva();

			Calendar dataSaldo = new GregorianCalendar();
			dataSaldo.add(Calendar.MONTH, -1);
			dataSaldo.add(Calendar.DAY_OF_MONTH, -1);

			Calendar inicio = new GregorianCalendar();
			inicio.add(Calendar.MONTH, -1);

			LancamentoRN lancamentoRN = new LancamentoRN();
			this.saldoGeral = lancamentoRN.saldo(this.conta,
					dataSaldo.getTime());
			this.lista = lancamentoRN
					.listar(this.conta, inicio.getTime(), null);

			Categoria categoria = null;
			double saldo = this.saldoGeral;
			this.saldos = new ArrayList<Double>();
			for (Lancamento lancamento : this.lista) {
				categoria = lancamento.getCategoria();
				saldo = saldo
						+ (lancamento.getValor().floatValue() * categoria
								.getFator());
				this.saldos.add(saldo);
			}
		}
		return this.lista;
	}

	public List<Lancamento> getListaAteHoje() {
		if (this.listaFuturos == null) {
			ContextoBean contextoBean = ContextoUtil.getContextoBean();
			Conta conta = contextoBean.getContaAtiva();

			Calendar hoje = new GregorianCalendar();

			LancamentoRN lancamentoRN = new LancamentoRN();
			this.listaAteHoje = lancamentoRN
					.listar(conta, null, hoje.getTime());
		}
		return this.listaAteHoje;
	}

	public List<Lancamento> getListaFuturos() {
		if (this.listaFuturos == null) {
			ContextoBean contextoBean = ContextoUtil.getContextoBean();
			Conta conta = contextoBean.getContaAtiva();

			Calendar amanha = new GregorianCalendar();
			amanha.add(Calendar.DAY_OF_MONTH, 1);

			LancamentoRN lancamentoRN = new LancamentoRN();
			this.listaFuturos = lancamentoRN.listar(conta, amanha.getTime(),
					null);
		}
		return this.listaFuturos;
	}

	public List<String> completeText(String query) {
		List<Entidade> entities = getEntidades();
		List<String> resultNomes = new ArrayList<String>();
		results.clear();
		for (Entidade e : entities) {
			if (e.getNome().equals(query)) {
				results.add(e);
				resultNomes.add(e.getNome());
			}
		}

		return resultNomes;
	}
	
	public void onrate(RateEvent rateEvent) {
		System.out.println("---------------Selecionado: "+selecionado.getValor());
		this.selecionado.setRating(( Integer.parseInt(rateEvent.getRating().toString())));
        LancamentoRN lancamentoRN = new LancamentoRN();
		lancamentoRN.atualizarAvaliacao(this.selecionado);
    }
     
    public void oncancel() {
    	selecionado.setRating(0);
    	LancamentoRN lancamentoRN = new LancamentoRN();
		lancamentoRN.atualizarAvaliacao(this.selecionado);
    }

	private List<Entidade> getEntidades() {
		return new EntidadeRN().listar();
	}

	public Conta getConta() {
		return conta;
	}

	public void setConta(Conta conta) {
		this.conta = conta;
	}

	public List<Double> getSaldos() {
		return saldos;
	}

	public void setSaldos(List<Double> saldos) {
		this.saldos = saldos;
	}

	public float getSaldoGeral() {
		return saldoGeral;
	}

	public void setSaldoGeral(float saldoGeral) {
		this.saldoGeral = saldoGeral;
	}

	public Lancamento getEditado() {
		return editado;
	}

	public void setEditado(Lancamento editado) {
		this.editado = editado;
	}

	public ContextoBean getContextoBean() {
		return contextoBean;
	}

	public void setContextoBean(ContextoBean contextoBean) {
		this.contextoBean = contextoBean;
	}

	public void setLista(List<Lancamento> lista) {
		this.lista = lista;
	}

	public String getEntidade() {
		return entidade;
	}

	public void setEntidade(String entidade) {
		this.entidade = entidade;
	}

	public Lancamento getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Lancamento selecionado) {
		this.selecionado = selecionado;
	}
	
}
