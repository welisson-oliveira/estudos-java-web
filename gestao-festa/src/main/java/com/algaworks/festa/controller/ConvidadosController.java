package com.algaworks.festa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.algaworks.festa.model.Convidado;
import com.algaworks.festa.repository.ConvidadosRepository;




@Controller
@RequestMapping("/convidados")
public class ConvidadosController {
	
	@Autowired //Injeta o repositorio no controller
	private ConvidadosRepository convidadosRepository;
	
	@RequestMapping
	public ModelAndView listar(){
		ModelAndView mv = new ModelAndView("ListaConvidados");
		mv.addObject("convidados", convidadosRepository.todos());
		mv.addObject(new Convidado());// chamado de command object, é o objeto que modela o formulario.
		
		return mv;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String salvar(Convidado convidado){
		this.convidadosRepository.adicionar(convidado);
		return "redirect:/convidados";
	}
}
