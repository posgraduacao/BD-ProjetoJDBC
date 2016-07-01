package com.clinicaveterinaria.model;

import java.util.Date;

public class VacinaAnimalID {
	private Animal animal;
	private Vacina vacina;
	private Date dataVacinacao;
	
	public Animal getAnimal() {
		return animal;
	}
	public void setAnimal(Animal animal) {
		this.animal = animal;
	}
	public Vacina getVacina() {
		return vacina;
	}
	public void setVacina(Vacina vacina) {
		this.vacina = vacina;
	}
	public Date getDataVacinacao() {
		return dataVacinacao;
	}
	public void setDataVacinacao(Date dataVacinacao) {
		this.dataVacinacao = dataVacinacao;
	}

}
