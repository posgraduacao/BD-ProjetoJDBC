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

public class PessoaDAO implements IGenericDAO<Pessoa, Integer> {

	private ConnectionFactory connectionFactory = new ConnectionFactory();

	public List<Pessoa> listar() throws Exception {
		List<Pessoa> pessoas = new ArrayList<Pessoa>();

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			connection = connectionFactory.getConnection();

			String sql = "SELECT pessoa_id, cpf, nome, nascimento FROM PESSOA";
			statement = connection.prepareStatement(sql);
			rs = statement.executeQuery();

			while (rs.next()) {
				Pessoa p = new Pessoa();
				p.setId(rs.getInt("pessoa_id"));
				p.setCpf(rs.getLong("cpf"));
				p.setNome(rs.getString("nome"));
				p.setNascimento(rs.getDate("nascimento"));

				pessoas.add(p);
			}
			return pessoas;
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

	public Pessoa buscarEager(Integer id) throws Exception {
		Pessoa retorno = null;

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			connection = connectionFactory.getConnection();

			String sql = "SELECT p.cpf, p.nome, p.nascimento,"
					+ " a.animal_id, a.nome nomeAnimal, a.nascimento nascAnimal" + " FROM PESSOA p"
					+ " LEFT JOIN ANIMAL a ON (p.pessoa_id = a.pessoa_id)" + " WHERE p.pessoa_id = ?";
			statement = connection.prepareStatement(sql);
			statement.setInt(1, id);
			rs = statement.executeQuery();

			if (rs.next()) {
				retorno = new Pessoa();
				retorno.setId(id);
				retorno.setCpf(rs.getLong("cpf"));
				retorno.setNome(rs.getString("nome"));
				retorno.setNascimento(rs.getDate("nascimento"));

				List<Animal> animais = new ArrayList<>();
				if (!rs.wasNull()) {
					do {
						Animal animal = new Animal();
						animal.setId(rs.getInt("animal_id"));
						animal.setNome(rs.getString("nomeAnimal"));
						animal.setTipoAnimal(null);
						animal.setNascimento(rs.getDate("nascAnimal"));
						animal.setDono(retorno);
						animais.add(animal);
					} while (rs.next());
				}
				retorno.setAnimais(animais);
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

	public Pessoa buscar(Integer id) throws Exception {
		Pessoa retorno = null;

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			connection = connectionFactory.getConnection();

			String sql = "SELECT cpf, nome, nascimento FROM PESSOA" + " WHERE pessoa_id = ?";
			statement = connection.prepareStatement(sql);
			statement.setInt(1, id);
			rs = statement.executeQuery();

			if (rs.next()) {
				retorno = new Pessoa();
				retorno.setId(id);
				retorno.setCpf(rs.getLong("cpf"));
				retorno.setNome(rs.getString("nome"));
				retorno.setNascimento(rs.getDate("nascimento"));
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

	public void inserir(Pessoa pessoa) throws Exception {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet generateKeys = null;
		
		try {
			connection = connectionFactory.getConnection();
			connection.setAutoCommit(false);

			String sql = "INSERT INTO pessoa (cpf,nome,nascimento) VALUES (?,?,?)";
			statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			statement.setLong(1, pessoa.getCpf());
			statement.setString(2, pessoa.getNome());
			if (pessoa.getNascimento() != null) {
				statement.setDate(3, new java.sql.Date(pessoa.getNascimento().getTime()));
			} else {
				statement.setDate(3, null);
			}
			
			statement.execute();
			generateKeys = statement.getGeneratedKeys();
			if (generateKeys.next()) {
				pessoa.setId(generateKeys.getInt(1));
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

	public void atualizar(Pessoa pessoa) throws Exception {
		String sql = "update pessoa set nome = ? ,cpf = ?,nascimento = ?" + " where pessoa_id=?";
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = connectionFactory.getConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1, pessoa.getNome());
			statement.setLong(2, pessoa.getCpf());
			if (pessoa.getNascimento() != null)
				statement.setDate(3, new java.sql.Date(pessoa.getNascimento().getTime()));
			else
				statement.setDate(3, null);
			statement.setLong(4, pessoa.getId());
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

	public void remover(Pessoa pessoa) throws Exception {
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = connectionFactory.getConnection();
			statement = connection.prepareStatement("delete from pessoa where pessoa_id=?");
			statement.setLong(1, pessoa.getId());
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
	
	public void removerComRelacionamento(Pessoa pessoa) throws Exception {
		Connection connection = null;
		PreparedStatement statement = null;
		PreparedStatement statementPessoa = null;
		PreparedStatement statementAnimal = null;
		ResultSet rs = null;
		
		try {
			connection = connectionFactory.getConnection();
			connection.setAutoCommit(false);
			
			String sql = "SELECT p.pessoa_id,  a.animal_id "
					+ "FROM pessoa p "
					+ "LEFT JOIN animal a ON (p.pessoa_id = a.pessoa_id) "
					+ "WHERE p.pessoa_id=?";
			statement = connection.prepareStatement(sql);
			statement.setInt(1, pessoa.getId());
			rs = statement.executeQuery();
			
			while (rs.next()){
				Animal animal = new Animal();
				animal.setId(rs.getInt("animal_id"));
				
				sql = "DELETE FROM animal WHERE animal_id=?";
				
				statementAnimal = connection.prepareStatement(sql);
				statementAnimal.setInt(1, animal.getId());
				statementAnimal.execute();
			}
			
			sql = "DELETE FROM pessoa WHERE pessoa_id=?";
			
			statementPessoa = connection.prepareStatement(sql);
			statementPessoa.setInt(1, pessoa.getId());			
			statementPessoa.execute();
			
			connection.commit();
			
		} catch (SQLException e) {
			connection.rollback();
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

}
