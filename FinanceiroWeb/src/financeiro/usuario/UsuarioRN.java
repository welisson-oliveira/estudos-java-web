package financeiro.usuario;

import java.util.List;

import financeiro.categoria.CategoriaRN;
import financeiro.util.DAOFactory;

public class UsuarioRN {
	private UsuarioDAO usuarioDAO;
	
	public UsuarioRN(){
		this.usuarioDAO = DAOFactory.criarUsuarioDAO();
	}
	
	public Usuario carregar(Integer codigo){
		return this.usuarioDAO.carregar(codigo);
	}
	
	public Usuario buscarPorLogin(String login){
		return this.usuarioDAO.buscarPorLogin(login);
	}
	
	public void salvar(Usuario usuario){
		Integer codigo = usuario.getCodigo();
		if(codigo == null || codigo == 0){
			usuario.getPermissao().add("ROLE_USUARIO");
			this.letrasMaiusculas(usuario);//Salva o usuario com o nome maiusculo
			this.usuarioDAO.salvar(usuario);
			
			CategoriaRN categoriaRN = new CategoriaRN();
			categoriaRN.salvaEstruturaPadrao(usuario);
		}else{
			this.usuarioDAO.atualizar(usuario);
		}
	}
	
	 public void excluir(Usuario usuario){
		 CategoriaRN categoriaRN = new CategoriaRN();
		 categoriaRN.excluir(usuario);
		 
		 this.usuarioDAO.excluir(usuario);
	 }
	 
	 public List<Usuario> listar(){
		 return this.usuarioDAO.listar();
	 }
	 
	 private void letrasMaiusculas(Usuario usu){
			String retorno = "";
			if(usu != null && usu.getNome() != null){
//				System.out.println(usu.getNome());
				String[] nome = usu.getNome().split(" ");
				
				for(String n : nome){
//					System.out.println(n);
					retorno += String.valueOf(n.charAt(0)).toUpperCase();
					retorno += n.substring(1, n.length());
					retorno += " ";
					
				}
				
				usu.setNome(retorno);
				
			}
		}
}
