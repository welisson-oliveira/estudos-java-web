/**
 * Já a classe ServletInitializer será usada se nossa aplicação for executada em um servidor externo, como um Apache Tomcat em produção, por exemplo.
 * 
 */

package com.algaworks.festa;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

//SpringBootServletInitializer é um contener web que suporta a partir da versão 3.0 da especificação de Servlet, faz com que a aplicação seja iniciada sem a necessidade do web.xml
public class ServletInitializer extends SpringBootServletInitializer { 

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(GestaoFestaApplication.class);
	}

}
