package com.clinicaveterinaria.model;

import java.util.Date;

public class VacinaAnimal {
	private VacinaAnimalID id;
	private String descricaoVeterinario;
	
	public VacinaAnimal() {
		id = new VacinaAnimalID();
	}
	
	public Animal getAnimal() {
		return id.getAnimal();
	}
	public void setAnimal(Animal animal) {
		id.setAnimal(animal);
	}
	public Vacina getVacina() {
		return id.getVacina();
	}
	public void setVacina(Vacina vacina) {
		id.setVacina(vacina);
	}
	public Date getDataVacinacao() {
		return id.getDataVacinacao();
	}
	public void setDataVacinacao(Date dataVacinacao) {
		id.setDataVacinacao(dataVacinacao);
	}
	
	public VacinaAnimalID getId() {
		return id;
	}
	public void setId(VacinaAnimalID id) {
		this.id = id;
	}
	public String getDescricaoVeterinario() {
		return descricaoVeterinario;
	}
	public void setDescricaoVeterinario(String descricaoVeterinario) {
		this.descricaoVeterinario = descricaoVeterinario;
	}
	
}
