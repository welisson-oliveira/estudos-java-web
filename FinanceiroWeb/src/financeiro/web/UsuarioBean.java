package financeiro.web;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.springframework.util.DigestUtils;

import financeiro.conta.Conta;
import financeiro.conta.ContaRN;
import financeiro.usuario.Usuario;
import financeiro.usuario.UsuarioRN;
import financeiro.util.RNException;

@ManagedBean(name = "usuarioBean")
@RequestScoped
public class UsuarioBean {
	private Usuario usuario = new Usuario();
	private String confirmaSenha;
	private List<Usuario> lista;
	private String destinoSalvar;
	private Conta conta = new Conta();

	// senha
	private String senhaCriptografada;

	public String novo() {
		this.destinoSalvar = "usuarioSucesso";
		this.usuario = new Usuario();
		this.usuario.setAtivo(true);
		return "/publico/usuario";
	}

	public String editar() {
		this.senhaCriptografada = this.usuario.getSenha();
		return "/publico/usuario";
	}

	public String salvar() {
		FacesContext context = FacesContext.getCurrentInstance();

		String senha = this.usuario.getSenha();
		if (senha != null && senha.trim().length() > 0
				&& !senha.equals(this.confirmaSenha)) {
			FacesMessage facesMessage = new FacesMessage(
					"A senha não foi confirmada corretamente!");
			context.addMessage(null, facesMessage);
			return null;
		}

		if (senha != null && senha.trim().length() == 0) {
			this.usuario.setSenha(this.senhaCriptografada);
		} else {
			String senhaCripto = DigestUtils.md5DigestAsHex(senha.getBytes());
			this.usuario.setSenha(senhaCripto);
		}

		UsuarioRN usuarioRN = new UsuarioRN();
		try {
			usuarioRN.salvar(usuario);
			
			if (this.conta.getDescricao() != null) {
				this.conta.setUsuario(this.usuario);
				this.conta.setFavorita(true);

				ContaRN contaRN = new ContaRN();
				contaRN.salvar(this.conta);
			}

			// Envia email após o cadastramento de um usuário novo
			if (this.destinoSalvar.equalsIgnoreCase("usuariosucesso")) {
				try {
					usuarioRN.enviarEmailPosCadastramento(this.usuario,senha);
				} catch (RNException e) {
					context.addMessage(null, new FacesMessage(e.getMessage()));
					return null;
				}
			}
			
		} catch (RNException e1) {
			context.addMessage(null, new FacesMessage(e1.getMessage()));
		}

		return this.destinoSalvar;
	}

	public String excluir() {
		UsuarioRN usuarioRN = new UsuarioRN();
		usuarioRN.excluir(this.usuario);
		this.lista = null;
		return null;
	}

	public String ativar() {
		FacesContext context = FacesContext.getCurrentInstance();
		if (this.usuario.isAtivo()) {
			this.usuario.setAtivo(false);
		} else {
			this.usuario.setAtivo(true);
		}

		UsuarioRN usuarioRN = new UsuarioRN();
		try {
			usuarioRN.salvar(this.usuario);
		} catch (RNException e) {
			context.addMessage(null, new FacesMessage(e.getMessage()));
		}
		return null;
	}

	public String atribuiPermissao(Usuario usuario, String permissao) {

		this.usuario = usuario;
		java.util.Set<String> permissoes = this.usuario.getPermissao();
		if (permissoes.contains(permissao)) {
			permissoes.remove(permissao);
		} else {
			permissoes.add(permissao);
		}

		return null;
	}

	public List<Usuario> getLista() {

		if (this.lista == null) {
			UsuarioRN usuarioRN = new UsuarioRN();
			this.lista = usuarioRN.listar();
		}

		return lista;
	}

	public void setLista(List<Usuario> usuarios) {
		this.lista = usuarios;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getConfirmaSenha() {
		return confirmaSenha;
	}

	public void setConfirmaSenha(String confirmaSenha) {
		this.confirmaSenha = confirmaSenha;
	}

	public String getDestinoSalvar() {
		return destinoSalvar;
	}

	public void setDestinoSalvar(String destinoSalvar) {
		this.destinoSalvar = destinoSalvar;
	}

	public String getSenhaCriptografada() {
		return senhaCriptografada;
	}

	public void setSenhaCriptografada(String senhaCriptografada) {
		this.senhaCriptografada = senhaCriptografada;
	}

	public Conta getConta() {
		return conta;
	}

	public void setConta(Conta conta) {
		this.conta = conta;
	}

}
