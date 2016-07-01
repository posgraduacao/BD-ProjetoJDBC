package com.clinicaveterinaria.dao;

import java.util.List;

public interface IGenericDAO<T,U> {
	public void inserir(T objeto) throws Exception;
	public List<T> listar() throws Exception;
	public T buscar(U id) throws Exception;
	public void atualizar(T objeto) throws Exception;
	public void remover(T objeto) throws Exception;
}
