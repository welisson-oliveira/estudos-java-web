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
package com.livro.capitulo14.javamail;

import java.util.*;
import javax.activation.*;
import javax.faces.application.FacesMessage;
import javax.faces.bean.*;
import javax.faces.context.FacesContext;
import javax.mail.*;
import javax.mail.internet.*;
import com.livro.capitulo14.autenticacao.AutenticaUsuario;

@ManagedBean(name = "javaMailBean")
@RequestScoped
public class JavaMailBean {

	public static final String 	SERVIDOR_SMTP = "localhost";
	public static final int 		PORTA_SERVIDOR_SMTP = 25;
	private static final String 	CONTA_PADRAO = "adm@localhost";
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

		Properties config = new Properties(); //1
		config.put("mail.smtp.host", JavaMailBean.SERVIDOR_SMTP);
		config.put("mail.smtp.port", JavaMailBean.PORTA_SERVIDOR_SMTP);
		config.put("mail.smtp.auth", "true");

		Session sessao = Session.getInstance(config, new AutenticaUsuario(JavaMailBean.CONTA_PADRAO, JavaMailBean.SENHA_CONTA_PADRAO)); //2

		try {
			MimeMessage email = new MimeMessage(sessao);//3 
			email.setFrom(new InternetAddress(this.de));
			email.setRecipient(Message.RecipientType.TO, new InternetAddress(this.para));

			InternetAddress[] normais = this.montaDestinatarios(this.destinatariosNormais);
			if (normais != null) {
				email.setRecipients(Message.RecipientType.CC, normais);
			}
			
			InternetAddress[] ocultos = this.montaDestinatarios(this.destinatariosOcultos);
			if (ocultos != null) {
				email.setRecipients(Message.RecipientType.BCC, ocultos);
			}

			email.setSubject(this.assunto);
			email.setSentDate(new Date());

			// Preparando o corpo de email
			MimeMultipart partesEmail = new MimeMultipart(); //4
			MimeBodyPart corpoEmail = new MimeBodyPart();
			corpoEmail.setContent(this.mensagem, "text/html");
			partesEmail.addBodyPart(corpoEmail);

			// Anexando arquivo
			if (this.anexo != null && this.anexo.trim().length() > 0) {
				MimeBodyPart anexo = new MimeBodyPart();
				FileDataSource arquivo = new FileDataSource(this.anexo); //5
				anexo.setDataHandler(new DataHandler(arquivo));
				anexo.setFileName(arquivo.getName());
				partesEmail.addBodyPart(anexo);
			} 

			email.setContent(partesEmail);

			Transport.send(email); //6

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

	public void enviarSimples() { //7
		FacesContext context = FacesContext.getCurrentInstance();

		Properties config = new Properties(); //8
		config.put("mail.smtp.host", JavaMailBean.SERVIDOR_SMTP);
		config.put("mail.smtp.port", JavaMailBean.PORTA_SERVIDOR_SMTP);

		Session sessao = Session.getInstance(config);

		try {
			MimeMessage email = new MimeMessage(sessao);
			email.setFrom(new InternetAddress(this.de));
			email.setRecipient(Message.RecipientType.TO, new InternetAddress(this.para));

			InternetAddress[] normais = this.montaDestinatarios(this.destinatariosNormais);
			if (normais != null) {
				email.setRecipients(Message.RecipientType.CC, normais);
			}
			
			InternetAddress[] ocultos = this.montaDestinatarios(this.destinatariosOcultos);
			if (normais != null) {
				email.setRecipients(Message.RecipientType.BCC, ocultos);
			}

			email.setSubject(this.assunto);
			email.setSentDate(new Date());
			email.setText(this.mensagem);
			Transport.send(email);

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

	private InternetAddress[] montaDestinatarios(String destinatarios) throws AddressException { //9
		InternetAddress[] emails = null;
		if (destinatarios != null && destinatarios.trim().length() > 0) {			
			String[] lista = destinatarios.split(";");
			emails = new InternetAddress[lista.length];
			for (int i = 0; i < lista.length; i++) {
				emails[i] = new InternetAddress(lista[i]);
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
