package financeiro.web.util;

import java.util.Date;
import java.util.Properties;

import javax.mail.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import financeiro.util.UtilException;

public class EmailUtil {
	public void enviarEmail(String de, String para, String assunto, String mensagem) 
		throws UtilException {
		try {
			Context initialContext = new InitialContext();
			Context envContext = (Context) initialContext.lookup("java:comp/env");
			Session session = (Session) envContext.lookup("mail/Session"); //1
			SimpleEmail email = new SimpleEmail();
			email.setMailSession(session); //2
			if (de != null) {
				email.setFrom(de);
			} else {
				Properties p = session.getProperties();//3 
				email.setFrom(p.getProperty("mail.smtp.user"));
			}
			email.addTo(para);
			email.setSubject(assunto);
			email.setMsg(mensagem);
			email.setSentDate(new Date());
			email.send();
		} catch (EmailException | NamingException e) {
			throw new UtilException(e);
		}
	}
}
