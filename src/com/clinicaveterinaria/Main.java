package com.clinicaveterinaria;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.clinicaveterinaria.dao.VacinaAnimalDAO;
import com.clinicaveterinaria.model.Animal;
import com.clinicaveterinaria.model.Vacina;
import com.clinicaveterinaria.model.VacinaAnimal;
import com.clinicaveterinaria.model.VacinaAnimalID;

public class Main {

	public static void main(String[] args) {
		try {
			
//			Pessoa p = new Pessoa();
//			p.setNome("João");
//			p.setCpf(12345678900L);
//			p.setNascimento(new Date());
//			
//			PessoaDAO dao = new PessoaDAO();
//			dao.inserir(p);
			
			
//			TipoAnimal t = new TipoAnimal();
//			t.setNomeRaca("Peixe");
//			t.setDescricao("Tipo de Peixe");
//
//			Pessoa p = new Pessoa();
//			p.setCpf(555555L);
//			p.setNome("Carlos");
//			p.setNascimento(new Date());
//
//			Animal a = new Animal();
//			a.setDono(p);
//			a.setNascimento(new Date());
//			a.setNome("Peixinho");
//			a.setTipoAnimal(t);
//			
//			AnimalDAO animalDAO = new AnimalDAO();
//			animalDAO.inserirComRelacionamentos(a);
			
//			TipoAnimalDAO dao = new TipoAnimalDAO();
//			TipoAnimal tipoAnimal = dao.buscarEager(1);
//			
//			System.out.println(tipoAnimal.getNomeRaca());
			
//			Pessoa p = new Pessoa();
//			p.setId(0);
//			
//			PessoaDAO dao = new PessoaDAO();
//			dao.removerComRelacionamento(p);
			
			VacinaAnimalDAO dao = new VacinaAnimalDAO();
			VacinaAnimalID vacinaAnimalID = new VacinaAnimalID();
			Animal animal = new Animal();
			animal.setId(12);
			Vacina vacina = new Vacina();
			vacina.setId(8);
			
			vacinaAnimalID.setAnimal(animal);
			vacinaAnimalID.setVacina(vacina);
			
			SimpleDateFormat df = new SimpleDateFormat("yyyy-dd-MM");
			
			Date date = df.parse("2015-01-01");
			vacinaAnimalID.setDataVacinacao(date);
			
			VacinaAnimal va = dao.buscar(vacinaAnimalID);
			System.out.println(va.getDescricaoVeterinario());
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
