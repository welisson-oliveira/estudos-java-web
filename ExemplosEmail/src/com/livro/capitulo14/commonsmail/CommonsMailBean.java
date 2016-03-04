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
package com.livro.capitulo14.commonsmail;

import java.util.*;
import javax.faces.application.FacesMessage;
import javax.faces.bean.*;
import javax.faces.context.FacesContext;
import javax.mail.internet.*;
import org.apache.commons.mail.*;

@ManagedBean(name = "commonsMailBean")
@RequestScoped
public class CommonsMailBean {

	public static final String 	SERVIDOR_SMTP = "localhost";
	public static final int 		PORTA_SERVIDOR_SMTP = 25;
	private static final String 	CONTA_PADRAO = "livrojava@localhost";
	private static final String 	SENHA_CONTA_PADRAO = "123";

	private String 				de;
	private String 				para;
	private String 				destinatariosNormais;
	private String 				destinatariosOcultos;
	private String 				assunto;
	private String 				mensagem;
	private String 				anexo;

	public void enviarAutenticado() {
		FacesContext context = FacesContext.getCurrentInstance();
		try {
			// Cria o e-mail preparado para os anexos
			MultiPartEmail email = new MultiPartEmail(); //1

			// informações do servidor
			email.setHostName(CommonsMailBean.SERVIDOR_SMTP);
			email.setSmtpPort(CommonsMailBean.PORTA_SERVIDOR_SMTP);
			// Autenticando no servidor
			email.setAuthentication(CommonsMailBean.CONTA_PADRAO, CommonsMailBean.SENHA_CONTA_PADRAO);

			// montando o e-mail
			email.setFrom(this.de, "Livro Java Para Web - Editora Novatec");
			email.addTo(this.para, this.para);
			email.setSubject(this.assunto);
			email.setMsg(this.mensagem);

			List<InternetAddress> normais = this.montaDestinatarios(this.destinatariosNormais);
			if (normais != null) {
				email.setCc(normais);
			}
			
			List<InternetAddress> ocultos = this.montaDestinatarios(this.destinatariosNormais);
			if (ocultos != null) {
				email.setBcc(ocultos);
			}

			// Cria o anexo
			if (this.anexo != null && this.anexo.trim().length() > 0) {
				EmailAttachment anexo = new EmailAttachment(); //2
				anexo.setPath(this.anexo);
				anexo.setDisposition(EmailAttachment.ATTACHMENT);
				// 	Adiciona o anexo ao e-mail
				email.attach(anexo);
			}
			email.send(); //3

			context.addMessage(null, new FacesMessage("E-mail enviado com sucesso"));

		} catch (EmailException e) {
			FacesMessage msg = new FacesMessage("Erro ao enviar e-mail! Erro: " + e.getMessage());
			context.addMessage(null, msg);
			return;
		} catch (AddressException e) {
			FacesMessage msg = new FacesMessage("Erro ao enviar e-mail! Erro: " + e.getMessage());
			context.addMessage(null, msg);
			return;
		}
	}

	public void enviarSimples() { //4
		FacesContext context = FacesContext.getCurrentInstance();
		try {
			SimpleEmail email = new SimpleEmail();
			// informações do servidor
			email.setHostName(CommonsMailBean.SERVIDOR_SMTP);
			email.setSmtpPort(CommonsMailBean.PORTA_SERVIDOR_SMTP);
			// montando o e-mail
			email.setFrom(this.de, "Livro Java Para Web - Editora Novatec");
			email.addTo(this.para, this.para);
			email.setSubject(this.assunto);
			email.setMsg(this.mensagem);

			List<InternetAddress> normais = this.montaDestinatarios(this.destinatariosNormais);
			if (normais != null) {
				email.setCc(normais);
			}
			
			List<InternetAddress> ocultos = this.montaDestinatarios(this.destinatariosOcultos);
			if (ocultos != null) {
				email.setBcc(ocultos);
			}

			email.send();

			context.addMessage(null, new FacesMessage("E-mail enviado com sucesso"));

		} catch (EmailException e) {
			FacesMessage msg = new FacesMessage("Erro ao enviar e-mail! Erro: " + e.getMessage());
			context.addMessage(null, msg);
			return;
		} catch (AddressException e) {
			FacesMessage msg = new FacesMessage("Erro ao enviar e-mail! Erro: " + e.getMessage());
			context.addMessage(null, msg);
			return;
		}
	}

	private List<InternetAddress> montaDestinatarios(String destinatarios) throws AddressException {
		List<InternetAddress> emails = null;
		if (destinatarios != null && destinatarios.trim().length() > 0) {
			String[] lista = destinatarios.split(";");
			emails = new ArrayList<InternetAddress>();
			for (int i = 0; i < lista.length; i++) {
				emails.add(new InternetAddress(lista[i]));
			}
		}
		return emails;
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

	public String getDestinatariosNormais() {
		return destinatariosNormais;
	}

	public void setDestinatariosNormais(String destinatariosNormais) {
		this.destinatariosNormais = destinatariosNormais;
	}

	public String getDestinatariosOcultos() {
		return destinatariosOcultos;
	}

	public void setDestinatariosOcultos(String destinatariosOcultos) {
		this.destinatariosOcultos = destinatariosOcultos;
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

	public String getAnexo() {
		return anexo;
	}

	public void setAnexo(String anexo) {
		this.anexo = anexo;
	}
}
