package financeiro.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.primefaces.event.RateEvent;

import financeiro.categoria.Categoria;
import financeiro.cheque.Cheque;
import financeiro.cheque.ChequeId;
import financeiro.cheque.ChequeRN;
import financeiro.conta.Conta;
import financeiro.entidade.Entidade;
import financeiro.entidade.EntidadeRN;
import financeiro.lancamento.Lancamento;
import financeiro.lancamento.LancamentoRN;
import financeiro.util.ContextoUtil;
import financeiro.util.RNException;

@ManagedBean(name = "lancamentoBean")
@ViewScoped
public class LancamentoBean implements Serializable {
	private static final long serialVersionUID = -3050807461213326560L;
	private List<Lancamento> lista;
	private Conta conta;
	private List<Double> saldos;
	private float saldoGeral;
	private Lancamento editado = new Lancamento();
	private Lancamento selecionado = new Lancamento();
	private Integer	numeroCheque; 
	
	private Integer linhaSelecionada = -1;
	private String listaAtiva = "";
	
	List<Entidade> results = new ArrayList<Entidade>();
	private String entidadeSelecionada;
	private List<Entidade> entidades;
	
	@ManagedProperty(value = "#{contextoBean}")
	private ContextoBean contextoBean;
	
	private List<Lancamento> listaAteHoje;
	private List<Lancamento> listaFuturos;

	public LancamentoBean() {
		this.novo();
	}

	public String novo() {
		this.editado = new Lancamento();
		this.editado.setData(new Date());
		this.numeroCheque = null; 
		this.entidadeSelecionada = null;
		return null;
	}

	public void editar() {
		Cheque cheque = this.editado.getCheque();
		if (cheque != null) {
			this.numeroCheque = cheque.getChequeId().getCheque();
		}

	}

	public void salvar() {
		this.editado.setUsuario(this.contextoBean.getUsuarioLogado());
		this.editado.setConta(this.contextoBean.getContaAtiva());

		ChequeRN chequeRN = new ChequeRN(); 
		ChequeId chequeId = null;
		if (this.numeroCheque != null) {
			chequeId = new ChequeId();
			chequeId.setConta(this.contextoBean.getContaAtiva().getConta());
			chequeId.setCheque(this.numeroCheque);
			Cheque cheque = chequeRN.carregar(chequeId);
			FacesContext context = FacesContext.getCurrentInstance();
			if (cheque == null) {
				context.addMessage(null, new FacesMessage("Cheque não cadastrado"));
				return;
			} else if (cheque.getSituacao() == Cheque.SITUACAO_CHEQUE_CANCELADO) {
				context.addMessage(null, new FacesMessage("Cheque já cancelado"));
				return;				
			} else {
				this.editado.setCheque(cheque);
				chequeRN.baixarCheque(chequeId, this.editado);
			}
		}
		
		boolean exists = false;
		for(Entidade e : this.entidades){
			if(e.getNome().equalsIgnoreCase(this.entidadeSelecionada)){
				this.editado.setEntidade(e);
				exists = true;
				break;
			}
		}
		
		if(!exists){
			Entidade e = new Entidade();
			e.setNome(this.entidadeSelecionada);
			EntidadeRN ern = new EntidadeRN();
			ern.salvar(e);
			this.editado.setEntidade(e);
		}
		
		LancamentoRN lancamentoRN = new LancamentoRN();
		lancamentoRN.salvar(this.editado);

		this.novo();
		this.lista = null;
	}
	

//	private boolean addEntidade(){
//		
//		if(!entidade.equals(null) || !entidade.trim().equals("")){
//			for(Entidade e : results){
//				if(entidade.equals(e.getNome())){
//					editado.setEntidade(e);
//					return true;
//				}
//			}
//		}
//	}

	public void mudouCheque(ValueChangeEvent event) { 
		Integer chequeAnterior = (Integer) event.getOldValue();
		if (chequeAnterior != null) {
			ChequeRN chequeRN = new ChequeRN();
			try {
				chequeRN.desvinculaLancamento(contextoBean.getContaAtiva(), chequeAnterior);
			} catch (RNException e) {
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(null, new FacesMessage(e.getMessage()));
				return;

			}
		}
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
		if (this.listaAteHoje == null) {
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
		this.entidades = getEntidades();
		List<String> resultNomes = new ArrayList<String>();
		results.clear();
		for (Entidade e : this.entidades) {
			if (e.getNome().toLowerCase().startsWith(query.toLowerCase())) {
				results.add(e);
				resultNomes.add(e.getNome());
			}
		}

		return resultNomes;
	}
	
	public void onRate(RateEvent rateEvent) {
		if(linhaSelecionada != -1){
			
			List<Lancamento> lancamentos = selectList();
			System.out.println("onRate--------------------Nota: "+lancamentos.get(this.linhaSelecionada).getRating());
			LancamentoRN lancamentoRN = new LancamentoRN();
			lancamentoRN.salvar(lancamentos.get(this.linhaSelecionada));
			linhaSelecionada = -1;
		}
    }
    
    public void salvarLinha(){
    	Map<String, String> requestParamMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		linhaSelecionada = Integer.parseInt(requestParamMap.get("linha"));
		this.listaAtiva = requestParamMap.get("lista");
    }
    
    private List<Lancamento> selectList(){
		switch(this.listaAtiva){
		case "geral":
			return lista;
		case "ateHoje":
			return listaAteHoje;
		case "futuro":
			return null;
		
		}
		return new ArrayList<Lancamento>();
	}

    private List<Entidade> getEntidades(){
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
	
	public void setNumeroCheque(Integer numeroCheque) {
		this.numeroCheque = numeroCheque;
	}
	
	public Integer getNumeroCheque() {
		return numeroCheque;
	}

	public Lancamento getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Lancamento selecionado) {
		this.selecionado = selecionado;
	}

	public String getEntidadeSelecionada() {
		return entidadeSelecionada;
	}

	public void setEntidadeSelecionada(String entidadeSelecionada) {
		this.entidadeSelecionada = entidadeSelecionada;
	}
	
	
}
