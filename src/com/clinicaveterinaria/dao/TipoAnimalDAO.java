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

public class TipoAnimalDAO implements IGenericDAO<TipoAnimal, Integer> {

	private ConnectionFactory connectionFactory = new ConnectionFactory();

	@Override
	public void inserir(TipoAnimal tipoAnimal) throws Exception {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet generateKeys = null;
		
		try {
			connection = connectionFactory.getConnection();

			String sql = "INSERT INTO tipopessoa (nomeraca,descricao) VALUES (?,?)";
			statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			statement.setString(1, tipoAnimal.getNomeRaca());
			statement.setString(2, tipoAnimal.getDescricao());

			statement.execute();
			
			generateKeys = statement.getGeneratedKeys();
			if (generateKeys.next()) {
				tipoAnimal.setId(generateKeys.getInt(1));
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
	public List<TipoAnimal> listar() throws Exception {
		List<TipoAnimal> tipos = new ArrayList<TipoAnimal>();

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			connection = connectionFactory.getConnection();

			String sql = "SELECT tipoanimal_id,nomeraca,descricao FROM tipoanimal";
			statement = connection.prepareStatement(sql);
			rs = statement.executeQuery();

			while (rs.next()) {
				TipoAnimal tipoAnimal = new TipoAnimal();
				tipoAnimal.setId(rs.getInt("tipoanimal_id"));
				tipoAnimal.setNomeRaca(rs.getString("nomeraca"));
				tipoAnimal.setDescricao(rs.getString("descricao"));
				tipos.add(tipoAnimal);
			}
			return tipos;
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
	public TipoAnimal buscar(Integer id) throws Exception {
		TipoAnimal retorno = null;

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			connection = connectionFactory.getConnection();

			String sql = "SELECT nomeraca, descricao FROM tipoanimal WHERE tipoanimal_id = ?";
			statement = connection.prepareStatement(sql);
			statement.setInt(1, id);
			rs = statement.executeQuery();

			if (rs.next()) {
				retorno = new TipoAnimal();
				retorno.setId(id);
				retorno.setNomeRaca(rs.getString("nomeraca"));
				retorno.setDescricao(rs.getString("descricao"));
				if (rs.next()) {
					throw new Exception("H� um problema com o banco.");
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
	
	public TipoAnimal buscarEager(Integer id) throws Exception {
		TipoAnimal retorno = null;

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			connection = connectionFactory.getConnection();
			
			String sql = "SELECT t.nomeraca, t.descricao, a.animal_id, a.nome nomeAnimal, a.nascimento nascAnimal, p.pessoa_id, p.nome, p.cpf "
					+ "FROM tipoanimal t "
					+ "LEFT JOIN animal a ON (t.tipoanimal_id = a.tipo_id) "
					+ "LEFT JOIN pessoa p ON (a.pessoa_id = p.pessoa_id) "
					+ "WHERE t.tipoanimal_id=?";
			statement = connection.prepareStatement(sql);
			statement.setInt(1, id);
			rs = statement.executeQuery();

			if (rs.next()) {
				retorno = new TipoAnimal();
				retorno.setId(id);
				retorno.setNomeRaca(rs.getString("nomeraca"));
				retorno.setDescricao(rs.getString("descricao"));
				
				List<Animal> animais = new ArrayList<>();
				if (!rs.wasNull()) {
					do {
						Animal animal = new Animal();
						animal.setId(rs.getInt("animal_id"));
						animal.setNome(rs.getString("nomeAnimal"));
						animal.setTipoAnimal(retorno);
						animal.setNascimento(rs.getDate("nascAnimal"));
						Pessoa pessoa = new Pessoa();
						pessoa.setId(rs.getInt("pessoa_id"));
						pessoa.setNome(rs.getString("nome"));
						pessoa.setCpf(rs.getLong("cpf"));
						animal.setDono(pessoa);
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

	@Override
	public void atualizar(TipoAnimal tipoAnimal) throws Exception {
		String sql = "UPDATE tipoanimal SET nomeraca=? ,descricao=? WHERE tipoanimal_id=?";
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = connectionFactory.getConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1, tipoAnimal.getNomeRaca());
			statement.setString(2, tipoAnimal.getDescricao());
			statement.setLong(3, tipoAnimal.getId());
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
	public void remover(TipoAnimal tipoAnimal) throws Exception {
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = connectionFactory.getConnection();
			statement = connection.prepareStatement("delete from tipoanimal where tipoanimal_id=?");
			statement.setLong(1, tipoAnimal.getId());
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

}
