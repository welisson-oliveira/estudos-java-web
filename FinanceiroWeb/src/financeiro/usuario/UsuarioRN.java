package financeiro.usuario;

import java.util.List;
import java.util.Locale;

import financeiro.categoria.CategoriaRN;
import financeiro.util.DAOFactory;
import financeiro.util.RNException;
import financeiro.util.UtilException;
import financeiro.web.util.EmailUtil;
import financeiro.web.util.MensagemUtil;

public class UsuarioRN {
	private UsuarioDAO usuarioDAO;

	public UsuarioRN() {
		this.usuarioDAO = DAOFactory.criarUsuarioDAO();
	}

	public Usuario carregar(Integer codigo) {
		return this.usuarioDAO.carregar(codigo);
	}

	public Usuario buscarPorLogin(String login) {
		return this.usuarioDAO.buscarPorLogin(login);
	}

	public void salvar(Usuario usuario) {
		Integer codigo = usuario.getCodigo();
		if (codigo == null || codigo == 0) {
			usuario.getPermissao().add("ROLE_USUARIO");
			this.letrasMaiusculas(usuario);// Salva o usuario com o nome
											// maiusculo
			this.usuarioDAO.salvar(usuario);

			CategoriaRN categoriaRN = new CategoriaRN();
			categoriaRN.salvaEstruturaPadrao(usuario);
		} else {
			this.usuarioDAO.atualizar(usuario);
		}
	}

	public void enviarEmailPosCadastramento(Usuario usuario, String senha) throws RNException {
		// Enviando um e-mail conforme o idioma do usuário
		String[] info = usuario.getIdioma().split("_");
		Locale locale = new Locale(info[0], info[1]);
		String titulo = MensagemUtil.getMensagem(locale, "email_titulo");
		String mensagem = MensagemUtil.getMensagem(locale, "email_mensagem",
				usuario.getNome(), usuario.getLogin(), senha);
		try {
			EmailUtil emailUtil = new EmailUtil();
			emailUtil.enviarEmail(null, usuario.getEmail(), titulo, mensagem);
		} catch (UtilException e) {
			throw new RNException(e);
		}
	}

	public void excluir(Usuario usuario) {
		CategoriaRN categoriaRN = new CategoriaRN();
		categoriaRN.excluir(usuario);

		this.usuarioDAO.excluir(usuario);
	}

	public List<Usuario> listar() {
		return this.usuarioDAO.listar();
	}

	private void letrasMaiusculas(Usuario usu) {
		String retorno = "";
		if (usu != null && usu.getNome() != null) {
			// System.out.println(usu.getNome());
			String[] nome = usu.getNome().split(" ");

			for (String n : nome) {
				// System.out.println(n);
				retorno += String.valueOf(n.charAt(0)).toUpperCase();
				retorno += n.substring(1, n.length());
				retorno += " ";

			}

			usu.setNome(retorno);

		}
	}
}
