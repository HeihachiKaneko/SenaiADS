package br.com.coldigogeladeiras.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

import br.com.coldigogeladeiras.jdbcinterface.MarcaDAO;
import br.com.coldigogeladeiras.modelo.Marca;



public class JDBCMarcaDAO implements MarcaDAO {

	private Connection conexao;

	public JDBCMarcaDAO(Connection conexao) {
		this.conexao = conexao;
	}

	public List<Marca> buscar() {
		
		//Criação da instrução SQL para busca de todas as marcas 
		String comando = "SELECT * FROM marcas WHERE status=1";
		
		//Criação de uma lista para armazenar cada marca encontrada
		List<Marca> listMarcas = new ArrayList<Marca>();
		
		//Criação do objeto marca com valor null (ou seja, sem instanciá-lo)
		Marca marca = null;
		
		//Abertura do try-catch
		try {
			
			//Uso da conexão do banco para prepará-lo para uma instrução SQL
			Statement stmt = conexao.createStatement(); 
			
			//Execução da instrução criada previamente 
			//e armazenamento do resultado no objeto rs 
			ResultSet rs = stmt.executeQuery(comando);
			
			//Enquanto houver uma próxima linha no resultado
			while (rs.next()) {
				
				//Criação de instância da classe Marca   
				marca = new Marca();
				
				//Recebimento dos 2 dados retornados do BD para cada linha encontrada
				int id = rs.getInt("id");
				String nome = rs.getString("nome");
				int status = rs.getInt("status");
				
				//Setando no objeto marca os valores encontrados
		        marca.setId(id);
		        marca.setNome(nome);
		        marca.setStatus(status);
		        				
		        //Adição da instância contida no objeto Marca na lista de marcas
				listMarcas.add(marca);
			}
			
		//Caso alguma Exception seja gerada no try, recebe-a no objeto "ex"	
		} catch (Exception ex) {
			//Exibe a exceção na console
			ex.printStackTrace();
		}		
		
		//Retorna para quem chamou o método a lista criada
		return listMarcas;
		
	} 
	
	public boolean inserir(Marca marca) {
		
		String comando = "INSERT INTO marcas "
				+ "(id, nome) "
				+ "VALUES (?,?)";
		
		PreparedStatement p;
		
		try {
			//Prepara o comando para execucao no banco em que nos conectamos
			p = this.conexao.prepareStatement(comando);
			
			//Subistitui no comando os ? pelos valores do produto
			p.setInt(1, marca.getId());
			p.setString(2, marca.getNome());
			
			//Executa o comando no banco
			p.execute();
			
		}catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public List<JsonObject> buscarPorNome(String nome){
		
		//Inicia criacao do comando SQL de busca
		String comando = "SELECT * FROM marcas ";
		
		//Se o nome nao estiver vazio...
		if (!nome.equals("")) {
			//concatena no comando o WHERE buscando no nome do produto
			//o texto da variavel nome
			comando += "WHERE nome LIKE '%" + nome + "%' ";
		}
		//Finaliza o comando ordenando alfabeticamente por
		//categoria, marca e depois modelo.
		comando += "ORDER BY nome ASC";
		
		//System.out.println(comando);
		
		List<JsonObject> listaMarcas = new ArrayList<JsonObject>();
		JsonObject marca = null;
		
		try {
			Statement stmt = conexao.createStatement();
			ResultSet rs = stmt.executeQuery(comando);
			
			// .next() serve para passar a linha no banco de dados para poder
			//pegar linha por linha das informacoes do banco
			while (rs.next()) {
				
				int id = rs.getInt("id");
				int status = rs.getInt("status");
				String marcaNome = rs.getString("nome");

				
				marca = new JsonObject();
				marca.addProperty("status", status);
				marca.addProperty("id", id);
				marca.addProperty("nome", marcaNome);
				
				listaMarcas.add(marca);
				
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return listaMarcas;
	}
	
	public boolean deletar(int id) {
		String comando = "DELETE FROM marcas WHERE id = ?";
		//PreparedStatement faz uma pre-execucao dos 3 primeiros pontos dos 4 que o Statement utiliza
		//Assim, como ele ja possui a pre-execucao possibilita menos peso no banco de dados por nao ter que sempre estar gerando as 
		//mesmas informacoes
		PreparedStatement p;
		try {
			p = this.conexao.prepareStatement(comando);
			p.setInt(1,  id);
			p.execute();
		}catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public Marca buscarPorId(int id) {
		String comando = "SELECT * FROM marcas WHERE marcas.id = ?";
		Marca marca = new Marca();
		try {
			PreparedStatement p = this.conexao.prepareStatement(comando);
			p.setInt(1,  id);
			ResultSet rs = p.executeQuery();
			while (rs.next()) {
				
				String nome = rs.getString("nome");
				
				marca.setId(id);
				marca.setNome(nome);
				 
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return marca;
	}
	
	public boolean alterar(Marca marca) {
		
		String comando = "UPDATE marcas "
				+ "SET nome=? "
				+ "WHERE id=?";
		PreparedStatement p;
		try {
			p = this.conexao.prepareStatement(comando);
			p.setString(1, marca.getNome());
			p.setInt(2, marca.getId());
			p.executeUpdate();
		}catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	
	//FAZER UM METODO BOOLEAN E NAO LIST
	public boolean verificaMarca(int id) {
		String comando = "SELECT produtos.*, marcas.nome, marcas.id FROM produtos "
				+ "INNER JOIN marcas ON produtos.marcas_id = marcas.id WHERE marcas.id =" + id;
		

		try {
			
			Statement stmt = conexao.createStatement();
			ResultSet rs = stmt.executeQuery(comando);
			//rs.next();

			//int marcaId = rs.getInt("marca.id");
			
//			if(rs.wasNull()) {
//				return true;
//			}else {
//				return false;
//			}
			return rs.next();
			
			
		} catch (Exception e) {
			e.printStackTrace();
			
			return false;
		}

	}

	
	public boolean alterarStatus(int id, boolean status) {
		String comando = "UPDATE marcas SET status=? WHERE id=?";
		int statusInt = status ? 1 : 0; 
		try {
			PreparedStatement p = this.conexao.prepareStatement(comando);
			p.setInt(1, statusInt);
			p.setInt(2, id);
			p.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}

/*
public List<Marca> buscar() {
		//Criação da instrução SQL para busca de todas as marcas 
		String comando = "SELECT * FROM marcas";
		//Criação de uma lista para armazenar cada marca encontrada
		List<Marca> listMarcas = new ArrayList<Marca>();
		//Criação do objeto marca com valor null (ou seja, sem instanciá-lo)
		Marca marca = null;
		//Abertura do try-catch
		try {
			//Uso da conexão do banco para prepará-lo para uma instrução SQL
			Statement stmt = conexao.createStatement(); 
			//Execução da instrução criada previamente 
			//e armazenamento do resultado no objeto rs 
			ResultSet rs = stmt.executeQuery(comando);
			//Enquanto houver uma próxima linha no resultado
			while (rs.next()) {
				//Criação de instância da classe Marca   
				marca = new Marca();
				
				//Recebimento dos 2 dados retornados do BD para cada linha encontrada
				int id = rs.getInt("id");
				String nome = rs.getString("nome");
				
				//Setando no objeto marca os valores encontrados
		        marca.setId(id);
		        marca.setNome(nome);
		        				
		        //Adição da instância contida no objeto Marca na lista de marcas
				listMarcas.add(marca);
			}
		//Caso alguma Exception seja gerada no try, recebe-a no objeto "ex"	
		} catch (Exception ex) {
			//Exibe a exceção na console
			ex.printStackTrace();
		}
		//Retorna para quem chamou o método a lista criada
		return listMarcas;
	}
 
*/