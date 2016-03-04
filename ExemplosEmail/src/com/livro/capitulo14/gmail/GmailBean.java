/*
 * Código-fonte do livro "Programação Java para a Web"
 * Autores: Décio Heinzelmann Luckow <decioluckow@gmail.com>
 *          Alexandre Altair de Melo <alexandremelo.br@gmail.com>
 *
 * ISBN: 978-85-7522-238-6
 * http://www.novatec.com.br/livros/javaparaweb
 * Editora Novatec, 2010 - todos os direitos reservados
 *
 * LICENÇA: Este arquivo-fonte está sujeito a Atribuição 2.5 Brasil, da licença Creative Commons,
 * que encontra-se disponível no seguinte endereço URI: http://creativecommons.org/licenses/by/2.5/br/
 * Se você não recebeu uma cópia desta licença, e não conseguiu obtê-la pela internet, por favor,
 * envie uma notificação aos seus autores para que eles possam enviá-la para você imediatamente.
 *
 *
 * Source-code of "Programação Java para a Web" book
 * Authors: Décio Heinzelmann Luckow <decioluckow@gmail.com>
 *          Alexandre Altair de Melo <alexandremelo.br@gmail.com>
 *
 * ISBN: 978-85-7522-238-6
 * http://www.novatec.com.br/livros/javaparaweb
 * Editora Novatec, 2010 - all rights reserved
 *
 * LICENSE: This source file is subject to Attribution version 2.5 Brazil of the Creative Commons
 * license that is available through the following URI:  http://creativecommons.org/licenses/by/2.5/br/
 * If you did not receive a copy of this license and are unable to obtain it through the web, please
 * send a note to the authors so they can mail you a copy immediately.
 *
 */
package com.livro.capitulo14.gmail;
import java.util.*;
import javax.faces.application.FacesMessage;
import javax.faces.bean.*;
import javax.faces.context.FacesContext;
import javax.mail.*;
import javax.mail.internet.*;
import com.livro.capitulo14.autenticacao.AutenticaUsuario;

@ManagedBean(name="gmailBean")
@RequestScoped
public class GmailBean {

	public static final String	SERVIDOR_SMTP		= "smtp.gmail.com";
	public static final String	PORTA_SERVIDOR_SMTP	= "465";
	private static final String	CONTA_GMAIL			= "convactaubate@gmail.com";
	private static final String	SENHA_GMAIL			= "convac123";

	private String	de;
	private String	para;
	private String	assunto;
	private String	mensagem;

	public void enviarEmail() {
		FacesContext context = FacesContext.getCurrentInstance();
		AutenticaUsuario autenticaUsuario = new AutenticaUsuario(GmailBean.CONTA_GMAIL, GmailBean.SENHA_GMAIL); //1
		Session session = Session.getDefaultInstance(this.configuracaoEmail(), autenticaUsuario);
		//Habilita o LOG das ações executadas durante o envio do email
		session.setDebug(true); //2

		try {
			Transport envio = null;
			MimeMessage email = new MimeMessage(session);
			email.setRecipient(Message.RecipientType.TO, new InternetAddress(this.para));
			email.setFrom(new InternetAddress(this.de));
			email.setSubject(this.assunto);
			email.setContent(this.mensagem, "text/plain");
			email.setSentDate(new Date());
			envio = session.getTransport("smtp");
			envio.connect(GmailBean.SERVIDOR_SMTP, GmailBean.CONTA_GMAIL, GmailBean.SENHA_GMAIL);
			email.saveChanges(); //3
			envio.sendMessage(email, email.getAllRecipients());
			envio.close();

			context.addMessage(null, new FacesMessage("E-mail enviado com sucesso"));

		} catch (AddressException e) {
			FacesMessage msg = new FacesMessage("Erro ao montar mensagem de e-mail! Erro: " + e.getMessage());
			context.addMessage(null, msg);
			return;
		} catch (MessagingException e) {
			FacesMessage msg = new FacesMessage("Erro ao enviar e-mail! Erro: " + e.getMessage());
			context.addMessage(null, msg);
			return;
		}
	}

	public Properties configuracaoEmail() { //4
		Properties config = new Properties();

		//Configuração adicional para servidor proxy.
		//Descomentar somente se utliza servidor com proxy.
		/*
		props.setProperty("proxySet", "true");
		props.setProperty("socksProxyHost","127.0.0.1"); //IP do Servidor Proxy
		props.setProperty("socksProxyPort","8080");  //Porta do servidor Proxy
		*/
		config.put("mail.transport.protocol", "smtp"); //define protocolo de envio como SMTP
		config.put("mail.smtp.starttls.enable", "true");
		config.put("mail.smtp.host", SERVIDOR_SMTP); //servidor SMTP do GMAIL
		config.put("mail.smtp.auth", "true"); //ativa autenticacao
		config.put("mail.smtp.user", GmailBean.CONTA_GMAIL); // conta que esta enviando o email
		config.put("mail.debug", "true");
		config.put("mail.smtp.port", PORTA_SERVIDOR_SMTP); //porta
		config.put("mail.smtp.socketFactory.port", PORTA_SERVIDOR_SMTP); //mesma porta para o socket
		config.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		config.put("mail.smtp.socketFactory.fallback", "false");
		return config;
	}

	public String getDe() {
		return de;
	}

	public void setDe(String de) {
		this.de = de;
	}

	public String getPara() {
		return para;
	}

	public void setPara(String para) {
		this.para = para;
	}

	public String getAssunto() {
		return assunto;
	}

	public void setAssunto(String assunto) {
		this.assunto = assunto;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
}
