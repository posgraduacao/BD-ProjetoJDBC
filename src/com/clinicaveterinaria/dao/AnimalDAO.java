package com.clinicaveterinaria.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.clinicaveterinaria.jdbc.ConnectionFactory;
import com.clinicaveterinaria.model.Animal;
import com.clinicaveterinaria.model.Pessoa;
import com.clinicaveterinaria.model.TipoAnimal;

public class AnimalDAO implements IGenericDAO<Animal, Integer> {

	private ConnectionFactory connectionFactory = new ConnectionFactory();

	@Override
	public void inserir(Animal animal) throws Exception {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet generateKeys = null;
		
		try {
			connection = connectionFactory.getConnection();
			
			String sql = "INSERT INTO animal (tipo_id,pessoa_id,nome,nascimento) VALUES (?,?,?,?)";
			statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			statement.setInt(1, animal.getTipoAnimal().getId());
			statement.setInt(2, animal.getDono().getId());
			statement.setString(3, animal.getNome());
			if (animal.getNascimento() == null) {
				statement.setDate(4, null);
			} else {
				statement.setDate(4, new java.sql.Date(animal.getNascimento().getTime()));
			}
			statement.executeQuery();

			generateKeys = statement.getGeneratedKeys();
			if (generateKeys.next()) {
				animal.setId(generateKeys.getInt(1));
			} else {
				throw new Exception("Erro ao gravar entidade.");
			}
			generateKeys.close();
			
		} catch (SQLException sqle) {
			throw new RuntimeException(sqle);
		} finally {
			try {
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
	public List<Animal> listar() throws Exception {
		List<Animal> animais = new ArrayList<Animal>();

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			connection = connectionFactory.getConnection();

			String sql = "SELECT animal_id, nome, nascimento FROM animal";
			statement = connection.prepareStatement(sql);
			rs = statement.executeQuery();

			while (rs.next()) {
				Animal a = new Animal();
				a.setId(rs.getInt("animal_id"));
				a.setNome(rs.getString("nome"));
				a.setNascimento(rs.getDate("nascimento"));
				animais.add(a);
			}
			return animais;
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
	public Animal buscar(Integer id) throws Exception {
		Animal retorno = null;

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			connection = connectionFactory.getConnection();

			String sql = "SELECT tipo_id, pessoa_id, nome, nascimento FROM ANIMAL" + " WHERE animal_id = ?";
			statement = connection.prepareStatement(sql);
			statement.setInt(1, id);
			rs = statement.executeQuery();

			if (rs.next()) {
				retorno = new Animal();
				retorno.setId(id);
				retorno.setNome(rs.getString("nome"));
				retorno.setNascimento(rs.getDate("nascimento"));

				Pessoa dono = new Pessoa();
				dono.setId(rs.getInt("pessoa_id"));
				retorno.setDono(dono);

				TipoAnimal tipoAnimal = new TipoAnimal();
				tipoAnimal.setId(rs.getInt("tipo_id"));
				retorno.setTipoAnimal(tipoAnimal);

				if (rs.next()) {
					throw new Exception("H� um problema com o banco.");
				}
			}
			return retorno;
		} catch (Exception e) {
			throw new Exception("Ocorreu um erro ao executar a consulta", e);
		} finally {
			try {
				if (connection != null)
					connection.close();
				if (rs != null)
					rs.close();
				if (statement != null)
					statement.close();
				// tratar melhor, pois se connection lançar uma exceção, rs
				// e statement não fecham
			} catch (Exception e) {
				throw new Exception("Ocorreu um erro ao executar a consulta", e);
			}
		}
	}

	@Override
	public void atualizar(Animal animal) throws Exception {
		String sql = "UPDATE animal SET nome = ?, nascimento = ?" + " WHERE animal_id=?";
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = connectionFactory.getConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1, animal.getNome());
			if (animal.getNascimento() != null)
				statement.setDate(2, new java.sql.Date(animal.getNascimento().getTime()));
			else
				statement.setDate(2, null);
			statement.setLong(3, animal.getId());
			statement.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			try {
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
	public void remover(Animal animal) throws Exception {
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = connectionFactory.getConnection();
			statement = connection.prepareStatement("DELETE FROM animal WHERE animal_id=?");
			statement.setLong(1, animal.getId());
			statement.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (statement != null)
					statement.close();
				if (connection != null)
					connection.close();
				// tratar melhor, pois se connection lançar uma exceção
				// statement não fecha
			} catch (Exception e) {
				throw new Exception("Ocorreu um erro ao executar a consulta", e);
			}
		}
	}

	public void inserirComRelacionamentos(Animal animal) throws Exception {

		Connection connection = null;
		PreparedStatement statementTipo = null;
		PreparedStatement statementPessoa = null;
		PreparedStatement statementAnimal = null;
		ResultSet generateKeys = null;

		try {
			connection = connectionFactory.getConnection();
			connection.setAutoCommit(false);

			String sql = "insert into tipoanimal (nomeraca,descricao) values (?,?)";
			statementTipo = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			statementTipo.setString(1, animal.getTipoAnimal().getNomeRaca());
			statementTipo.setString(2, animal.getTipoAnimal().getDescricao());
			statementTipo.execute();
			generateKeys = statementTipo.getGeneratedKeys();
			if (generateKeys.next()) {
				animal.getTipoAnimal().setId(generateKeys.getInt(1));
			} else {
				throw new Exception("Erro ao gravar entidade.");
			}
			generateKeys.close();

			sql = "insert into pessoa (cpf,nome,nascimento) values (?,?,?)";
			statementPessoa = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			statementPessoa.setLong(1, animal.getDono().getCpf());
			statementPessoa.setString(2, animal.getDono().getNome());
			if (animal.getDono().getNascimento() != null)
				statementPessoa.setDate(3, new java.sql.Date(animal.getDono().getNascimento().getTime()));
			else
				statementPessoa.setDate(3, null);
			statementPessoa.execute();
			generateKeys = statementPessoa.getGeneratedKeys();
			if (generateKeys.next()) {
				animal.getDono().setId(generateKeys.getInt(1));
			} else {
				throw new Exception("Erro ao gravar entidade.");
			}
			generateKeys.close();

			sql = "insert into animal (tipo_id,pessoa_id,nome,nascimento) values (?,?,?,?)";
			statementAnimal = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			statementAnimal.setInt(1, animal.getTipoAnimal().getId());
			statementAnimal.setInt(2, animal.getDono().getId());
			statementAnimal.setString(3, animal.getNome());
			if (animal.getNascimento() == null) {
				statementAnimal.setDate(4, null);
			} else {
				statementAnimal.setDate(4, new java.sql.Date(animal.getNascimento().getTime()));
			}
			statementAnimal.execute();
			generateKeys = statementAnimal.getGeneratedKeys();
			if (generateKeys.next()) {
				animal.setId(generateKeys.getInt(1));
			} else {
				throw new Exception("Erro ao gravar entidade.");
			}
			generateKeys.close();
			
			connection.commit();
			
		} catch (SQLException sqle) {
			connection.rollback();
			throw new RuntimeException(sqle);
		} finally {
			try {
				if (statementPessoa != null)
					statementPessoa.close();
				if (statementTipo != null)
					statementTipo.close();
				if (connection != null)
					connection.close();
			} catch (Exception e) {
				throw new Exception("Ocorreu um erro ao executar a consulta", e);
			}
		}
	}
}
