package com.clinicaveterinaria.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import com.clinicaveterinaria.jdbc.ConnectionFactory;
import com.clinicaveterinaria.model.VacinaAnimal;
import com.clinicaveterinaria.model.VacinaAnimalID;

public class VacinaAnimalDAO implements IGenericDAO<VacinaAnimal, VacinaAnimalID>{

	private ConnectionFactory connectionFactory = new ConnectionFactory();
	
	@Override
	public void inserir(VacinaAnimal objeto) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<VacinaAnimal> listar() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VacinaAnimal buscar(VacinaAnimalID id) throws Exception {
		VacinaAnimal retorno = null;

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			connection = connectionFactory.getConnection();

			String sql = "SELECT descricaoveterinario FROM vacinaanimal "
					+ "WHERE vacina_id = ? AND animal_id = ? AND datavacinacao = ?";
			statement = connection.prepareStatement(sql);
			statement.setInt(1, id.getVacina().getId());
			statement.setInt(2, id.getAnimal().getId());
			statement.setDate(3, new Date(id.getDataVacinacao().getTime()));
			
			rs = statement.executeQuery();

			if (rs.next()) {
				retorno = new VacinaAnimal();
				retorno.setId(id);
				retorno.setDescricaoVeterinario(rs.getString("descricaoveterinario"));
				if (rs.next()) {
					throw new Exception("Há um problema com o banco.");
				}
			}
			return retorno;
		} catch (Exception e) {
			throw new Exception("Ocorreu um erro ao executar a consulta", e);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (statement != null)
					statement.close();
				if (connection != null)
					connection.close();
				// tratar melhor, pois se connection lançar uma exceção, rs
				// e statement não fecham
			} catch (Exception e) {
				throw new Exception("Ocorreu um erro ao executar a consulta", e);
			}
		}
	}

	@Override
	public void atualizar(VacinaAnimal objeto) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remover(VacinaAnimal objeto) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
