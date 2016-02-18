package financeiro.web;

import java.util.List;

import javax.faces.bean.*;
import javax.faces.context.*;
import javax.faces.event.ValueChangeEvent;

import financeiro.conta.*;
import financeiro.usuario.*;

@ManagedBean(name = "contextoBean")
@SessionScoped
public class ContextoBean{
		
	private Usuario usuarioLogado = null;
	private Conta contaAtiva = null;

	public Usuario getUsuarioLogado() {
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext external = context.getExternalContext();
		String login = external.getRemoteUser();
		

		if (this.usuarioLogado == null || !login.equals(this.usuarioLogado.getLogin())) {
			
			if (login != null) {
				
				UsuarioRN usuarioRN = new UsuarioRN();
				
				Usuario usu = usuarioRN.buscarPorLogin(login);
				
				this.usuarioLogado = usu;
								
				this.contaAtiva = null;
			}
		}
		return this.usuarioLogado;
	}
	

	public void setUsuarioLogado(Usuario usuario) {
		System.out.println("Passou por aqui!");
		this.usuarioLogado = usuario;
	}

	public Conta getContaAtiva() {
		if (this.contaAtiva == null) {
			Usuario usuario = this.getUsuarioLogado();
			ContaRN contaRN = new ContaRN();
			this.contaAtiva = contaRN.buscarFavorita(usuario);
			if (this.contaAtiva == null) {
				List<Conta> contas = contaRN.listar(usuario);
				if (contas != null) {
					for (Conta conta : contas) {
						this.contaAtiva = conta;
						break;
					}
				}
			}
		}
		return this.contaAtiva;
	}

	public void setContaAtiva(ValueChangeEvent event) {
		Integer conta = (Integer) event.getNewValue();
		ContaRN contaRN = new ContaRN();
		this.contaAtiva = contaRN.carregar(conta);
	}

}
