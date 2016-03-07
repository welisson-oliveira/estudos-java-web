package financeiro.web.util;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import financeiro.util.AutenticaUsuario;
import financeiro.util.UtilException;

public class EmailUtil {
	private Properties p;
	
	public void enviarEmail(String de, String para, String assunto, String mensagem) 
		throws UtilException {
		
		try {
			Context initialContext = new InitialContext();
			Context envContext = (Context) initialContext.lookup("java:comp/env");
			Session session = (Session) envContext.lookup("mail/Session"); //1
			
			Transport envio = null;
			MimeMessage email = new MimeMessage(session);
			p = session.getProperties();//3
			
			AutenticaUsuario autenticaUsuario = new AutenticaUsuario(p.getProperty("mail.smtp.user"), p.getProperty("mail.smtp.password")); //1
			session = Session.getDefaultInstance(this.configuracaoEmail(), autenticaUsuario);
			
			email.setRecipient(Message.RecipientType.TO, new InternetAddress(para));
			if (de != null) {
				email.setFrom(new InternetAddress(de));
			} else {
				
				email.setFrom(new InternetAddress(p.getProperty("mail.smtp.user")));
			}
			email.setSubject(assunto);
			email.setContent(mensagem, "text/plain");
			email.setSentDate(new Date());
			envio = session.getTransport("smtp");
			envio.connect(p.getProperty("mail.smtp.host"),p.getProperty("mail.smtp.user"),p.getProperty("mail.smtp.password"));
			email.saveChanges();
			envio.sendMessage(email, email.getAllRecipients());
			envio.close();
			
		} catch (NamingException | MessagingException e) {
			throw new UtilException(e);
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
		config.put("mail.smtp.host", p.getProperty("mail.smtp.host")); //servidor SMTP do GMAIL
		config.put("mail.smtp.auth", "true"); //ativa autenticacao
		config.put("mail.smtp.user", p.getProperty("mail.smtp.user")); // conta que esta enviando o email
		config.put("mail.debug", "true");
		config.put("mail.smtp.port", p.getProperty("mail.smtp.port")); //porta
		config.put("mail.smtp.socketFactory.port", p.getProperty("mail.smtp.port")); //mesma porta para o socket
		config.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		config.put("mail.smtp.socketFactory.fallback", "false");
		return config;
	}
}
