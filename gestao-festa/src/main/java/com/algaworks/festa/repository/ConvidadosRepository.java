package com.algaworks.festa.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.algaworks.festa.model.Convidado;

@Repository
public class ConvidadosRepository {
	private static final List<Convidado> LISTA_CONVIDADOS = new ArrayList<>();
	
	static{
		LISTA_CONVIDADOS.add(new Convidado("Pedro",2));
		LISTA_CONVIDADOS.add(new Convidado("Maria",3));
		LISTA_CONVIDADOS.add(new Convidado("Ricardo",1));
	}
	
	public List<Convidado> todos(){
		return ConvidadosRepository.LISTA_CONVIDADOS;
	}
	
	public void adicionar(Convidado convidado){
		ConvidadosRepository.LISTA_CONVIDADOS.add(convidado);
	}
}
