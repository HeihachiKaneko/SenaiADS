package br.com.coldigogeladeiras.jdbc;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.google.gson.JsonObject;

import br.com.coldigogeladeiras.jdbcinterface.ProdutoDAO;
import br.com.coldigogeladeiras.modelo.Produto;

public class JDBCProdutoDAO implements ProdutoDAO {

	private Connection conexao;
	
	public JDBCProdutoDAO(Connection conexao) {
		this.conexao = conexao;
	}
	
	public boolean inserir(Produto produto) {
		
		String comando = "INSERT INTO produtos "
				+ "(id, categoria, modelo, capacidade, valor, marcas_id) "
				+ "VALUES (?,?,?,?,?,?)";
		PreparedStatement p;
		
		try {
			
			//Prepara o comando para execução no BD em que nos conectamos
			p = this.conexao.prepareStatement(comando);
			
			//Substitui no comando os "?" pelos valores do produto
			p.setInt(1, produto.getId());
			p.setString(2, produto.getCategoria());
			p.setString(3, produto.getModelo());
			p.setInt(4, produto.getCapacidade());
			p.setFloat(5, produto.getValor());
			p.setInt(6, produto.getMarcaId());
			
			//Executa o comando no BD
			p.execute();
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;		
		
	}
	
	public List<JsonObject> buscarPorNome(String nome) {
		
		//Inicia criacao do comando SQL de busca
		//INNER JOIN serve para trazer os dados relacionados de uma outra tabela
		//No caso as marcasID
		String comando = "SELECT produtos.*, marcas.nome as marca FROM produtos " 
		+ "INNER JOIN marcas ON produtos.marcas_id = marcas.id ";
		
		//Se o nome nao estiver vazio...
		if (!nome.equals("")) {
			//concatena no comando o WHERE buscando no nome do produto
			//o texto da variavel nome
			comando += "WHERE modelo LIKE '%" + nome + "%' ";
		}
		//Finaliza o comando ordenando alfabeticamente por
		//categoria, marca e depois modelo.
		comando += "ORDER BY categoria ASC, marcas.nome ASC, modelo ASC";
		
		List<JsonObject> listaProdutos = new ArrayList<JsonObject>();
		JsonObject produto = null;
		
		try {
			//Statement serve 
			Statement stmt = conexao.createStatement();
			ResultSet rs = stmt.executeQuery(comando);
			
			
			// .next() serve para passar a linha no banco de dados para poder pegar linha por linha das informacoes do banco
			while (rs.next()) {
				
				int id = rs.getInt("id");
				String categoria = rs.getString("categoria");
				String modelo = rs.getString("modelo");
				int capacidade = rs.getInt("capacidade");
				float valor = rs.getFloat("valor");
				String marcaNome = rs.getString("marca");
				
				if (categoria.contentEquals("1")) {
					categoria = "Geladeira";
				}else if (categoria.contentEquals("2")) {
					categoria = "Freezer";
				}
				
				produto = new JsonObject();
				produto.addProperty("id", id);
				produto.addProperty("categoria", categoria);
				produto.addProperty("modelo", modelo);
				produto.addProperty("capacidade", capacidade);
				produto.addProperty("valor", valor);
				produto.addProperty("marcaNome", marcaNome);
				
				listaProdutos.add(produto);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return listaProdutos;
	}
	
	public boolean deletar(int id) {
		String comando = "DELETE FROM produtos WHERE id = ?";
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
	
	public Produto buscarPorId(int id) {
		String comando = "SELECT * FROM produtos WHERE produtos.id = ?";
		Produto produto = new Produto();
		try {
			PreparedStatement p = this.conexao.prepareStatement(comando);
			p.setInt(1,  id);
			ResultSet rs = p.executeQuery();
			while (rs.next()) {
				
				String categoria = rs.getString("categoria");
				String modelo = rs.getString("modelo");
				int capacidade = rs.getInt("capacidade");
				float valor = rs.getFloat("valor");
				int marcaId = rs.getInt("marcas_id");
				
				produto.setId(id);
				produto.setCategoria(categoria);
				produto.setMarcaId(marcaId);
				produto.setModelo(modelo);
				produto.setCapacidade(capacidade);
				produto.setValor(valor);
				
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return produto;
	}
	
	public boolean alterar(Produto produto) {
		
		String comando = "UPDATE produtos "
				+ "SET categoria=?, modelo=?, capacidade=?, valor=?, marcas_id=? "
				+ "WHERE id=?";
		PreparedStatement p;
		try {
			p = this.conexao.prepareStatement(comando);
			p.setString(1,  produto.getCategoria());
			p.setString(2, produto.getModelo());
			p.setInt(3, produto.getCapacidade());
			p.setFloat(4, produto.getValor());
			p.setInt(5, produto.getMarcaId());
			p.setInt(6, produto.getId());
			p.executeUpdate();
		}catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean verificaProduto(int marcaId) {
		String comando = "SELECT marcas.nome as marca FROM marcas WHERE status=1 AND marcas.id=" + marcaId;
	//	String comando = "SELECT marcas.nome as marca FROM marcas WHERE marcas.id=" + marcaId;

		//juntar comando sql
		//if (marcaId != 0) {
		//	comando += "WHERE marcas.id=" + marcaId + " ";
		//}

		//List<JsonObject> listaMarcas = new ArrayList<JsonObject>();
		//JsonObject marca = null;
		try {
			Statement stmt = conexao.createStatement();
			ResultSet rs = stmt.executeQuery(comando);
			//while (rs.next()) {
			//	String marcaNome = rs.getString("marca");

			//	marca = new JsonObject();
			//	marca.addProperty("marcaNome", marcaNome);

			//	listaMarcas.add(marca);
			return rs.next();
			//}

		} catch (Exception e) {
			e.printStackTrace();
		}

		//return listaMarcas;
		return false;
	}

}


/*


public boolean inserir(Produto produto) {
	
	String comando = "INSERT INTO produtos (id, categoria, modelo, capacidade, valor, marcas_id) VALUES (?,?,?,?,?,?)";
	PreparedStatement p;
	try {
		p = this.conexao.prepareStatement(comando);
		p.setInt(1, produto.getId());
		p.setString(2, produto.getCategoria());
		p.setString(3, produto.getModelo());
		p.setInt(4, produto.getCapacidade());
		p.setFloat(5, produto.getValor());
		p.setInt(6, produto.getMarcaId());
		p.execute();
	} catch (SQLException e) {
		e.printStackTrace();
		return false;
	}
	return true;
}

public List<JsonObject> buscarPorNome(String nome) {
	String comando = "SELECT produtos.*, marcas.nome as marca FROM produtos "
			+ "INNER JOIN marcas ON produtos.marcas_id = marcas.id ";
	if (!nome.equals("")) {
		comando += "WHERE modelo LIKE '%" + nome + "%' ";
	}
	comando += "ORDER BY categoria ASC, marcas.nome ASC, modelo ASC";  
	
	List<JsonObject> listaProdutos = new ArrayList<JsonObject>();
	JsonObject produto = null;
	
	
	try {
		Statement stmt = conexao.createStatement();
		ResultSet rs = stmt.executeQuery(comando);
		while (rs.next()) {
			
			int id = rs.getInt("id");
			String categoria = rs.getString("categoria");
			String modelo = rs.getString("modelo");
			int capacidade = rs.getInt("capacidade");
			float valor = rs.getFloat("valor");
			String marcaNome = rs.getString("marca");

			if (categoria.equals("1")) {
				categoria = "Geladeira";
			}else if (categoria.equals("2")) {
				categoria = "Freezer";
			}

			produto = new JsonObject();
			produto.addProperty("id", id);
			produto.addProperty("categoria", categoria);
			produto.addProperty("modelo", modelo);
			produto.addProperty("capacidade", capacidade);
			produto.addProperty("valor", valor);
			produto.addProperty("marcaNome", marcaNome);
	        
			listaProdutos.add(produto);
	        
		}
	} catch (Exception e) {
		e.printStackTrace();
	}

	return listaProdutos;
}

public boolean deletar(int id) {
	String comando = "DELETE FROM produtos WHERE id = '" + id +"'";
	Statement p;
	try {
		p = this.conexao.createStatement();
		p.execute(comando);
	} catch (SQLException e) {
		e.printStackTrace();
		return false;
	}
	return true;
}

public Produto buscarPorId(int id) {
	String comando = "SELECT * FROM produtos WHERE produtos.id = '" + id + "'";
	Produto produto = new Produto();
	try {
		Statement stmt = conexao.createStatement();
		ResultSet rs = stmt.executeQuery(comando);
		while (rs.next()) {
			
			String categoria = rs.getString("categoria");
			String modelo = rs.getString("modelo");
			int capacidade = rs.getInt("capacidade");
			float valor = rs.getFloat("valor");
			int marcaId = rs.getInt("marcas_id");
			
			produto.setId(id);
			produto.setCategoria(categoria);
			produto.setMarcaId(marcaId);
			produto.setModelo(modelo);
	        produto.setCapacidade(capacidade);
	        produto.setValor(valor);
	        
		}
	} catch (Exception e) {
		e.printStackTrace();
	}

	return produto;
}


public boolean alterar(Produto produto) {
	
	String comando = "UPDATE produtos "
			+ "SET categoria=?, modelo=?, capacidade=?, valor=?, marcas_id=?"
			+ " WHERE id=?";
	PreparedStatement p;
	try {
		p = this.conexao.prepareStatement(comando);
		p.setString(1, produto.getCategoria());
		p.setString(2, produto.getModelo());
		p.setInt(3, produto.getCapacidade());
		p.setFloat(4, produto.getValor());
		p.setInt(5, produto.getMarcaId());
		p.setInt(6, produto.getId());
		p.executeUpdate();
	} catch (SQLException e) {
		e.printStackTrace();
		return false;
	}
	return true;
}
*/