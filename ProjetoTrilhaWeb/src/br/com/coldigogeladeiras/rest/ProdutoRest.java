package br.com.coldigogeladeiras.rest;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import br.com.coldigogeladeiras.bd.Conexao;
import br.com.coldigogeladeiras.jdbc.JDBCProdutoDAO;
import br.com.coldigogeladeiras.modelo.Produto;

@Path("produto")
public class ProdutoRest extends UtilRest{

	@POST
	@Path("/inserir")
	@Consumes("application/*")
	public Response inserir(String produtoParam){


		try{
			Produto produto = new Gson().fromJson(produtoParam, Produto.class);
			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();

			JDBCProdutoDAO jdbcProduto = new JDBCProdutoDAO(conexao);
		
			String msg = "";
			boolean retorno = false;

			List<JsonObject> listaProdutos= new ArrayList<JsonObject>();
			
			listaProdutos = jdbcProduto.buscarPorNome(produto.getModelo());
			//retorno = jdbcProduto.verificaProduto(produto.getMarcaId());
			
			if(listaProdutos.size() == 0) {
				
				//List<JsonObject> listaMarcas = new ArrayList<JsonObject>();
				retorno = jdbcProduto.verificaProduto(produto.getMarcaId());
				
				if (retorno) {
					retorno = jdbcProduto.inserir(produto);

					if (retorno) {
						msg = "Produto cadastrado com sucesso";

					} else {
						msg = "Erro ao cadastrar produto";
					}
					
				} else {
					msg = "A marca selecionada não existe mais, favor atualize a pagina";
				}
					
				
			} else {
				msg = "Ja existe um produto com esse modelo";
			}

			conec.fecharConexao();

			if (retorno) {
				return this.buildResponse(msg);
			} else {
				return this.buildErrorResponse(msg);
			}

		}catch(Exception e){
			e.printStackTrace();
			return this.buildErrorResponse(e.getMessage());
		}

	}
	
	@GET
	@Path("/buscar")
	@Consumes("application/*")
	@Produces(MediaType.APPLICATION_JSON)
	public Response buscarPorNome(@QueryParam("valorBusca") String nome) {
		
		try {
			
			List<JsonObject> listaProdutos = new ArrayList<JsonObject> ();
			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
			JDBCProdutoDAO jdbcProduto = new JDBCProdutoDAO(conexao);
			listaProdutos = jdbcProduto.buscarPorNome(nome);
			conec.fecharConexao();
			
			String json = new Gson().toJson(listaProdutos);
			return this.buildResponse(json);
			
		}catch(Exception e) {
			e.printStackTrace();
			return this.buildErrorResponse(e.getMessage());
		}
	}

	@DELETE
	@Path("/excluir/{id}")
	@Consumes("application/*")
	public Response excluir(@PathParam("id") int id) {
		
		try {
			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
			JDBCProdutoDAO jdbcProduto = new JDBCProdutoDAO(conexao);
			
			boolean retorno = jdbcProduto.deletar(id);
			
			String msg = "";
			if(retorno) {
				msg = "Produto excluido com sucesso!";
			}else {
				msg = "Erro ao excluir produto.";
				return this.buildErrorResponse(msg);
			}
			
			conec.fecharConexao();
			
			return this.buildResponse(msg);
			
		}catch(Exception e) {
			e.printStackTrace();
			return this.buildErrorResponse(e.getMessage());
		}
	}
	
	@GET
	@Path("/buscarPorId")
	@Consumes("application/*")
	@Produces(MediaType.APPLICATION_JSON)
	public Response buscarPorId(@QueryParam("id") int id) {
		
		try {
			Produto produto = new Produto();
			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
			JDBCProdutoDAO jdbcProduto = new JDBCProdutoDAO(conexao);
			
			produto = jdbcProduto.buscarPorId(id);
			
			conec.fecharConexao();
			
			return this.buildResponse(produto);
			
		}catch(Exception e) {
			e.printStackTrace();
			return this.buildErrorResponse(e.getMessage());
		}
		
	}
	
	
	@PUT
	@Path("/alterar")
	@Consumes("application/*")
	public Response alterar(String produtoParam) {
		
		try {
			Produto produto = new Gson().fromJson(produtoParam, Produto.class);
			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
			JDBCProdutoDAO jdbcProduto = new JDBCProdutoDAO(conexao);

			String msg = "";
			boolean retorno = false;
			retorno = jdbcProduto.verificaProduto(produto.getMarcaId());
			
			//verifica se o boolean eh false
			if(retorno) {
				retorno = jdbcProduto.alterar(produto);
				
				if (retorno) {
					msg = "Produto alterado com sucesso!";
				}else {
					msg = "Erro ao alterar produto.";
				}

			}else {
				msg = "Está marca foi alterada recentemente, por favor atualize a página.";
			}
			
			conec.fecharConexao();
			
			if (retorno) {
				return this.buildResponse(msg);
			}else {
				return this.buildErrorResponse(msg);
			}
			
			
		}catch(Exception e) {
			e.printStackTrace();
			return this.buildErrorResponse(e.getMessage());
		}
		
	}


}



/*
@Path("produto")
public class ProdutoRest extends Rest{
	
	@POST
	@Path("/inserir")
	@Consumes("application/*")
	public Response inserir(String produtoParam){
		
		System.out.println(produtoParam);
		try{
			System.out.println(produtoParam);
			//Produto produto = new ObjectMapper().readValue(produtoParam, Produto.class);
			Produto produto = new Gson().fromJson(produtoParam, Produto.class);
			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();

			JDBCProdutoDAO jdbcProduto= new JDBCProdutoDAO(conexao);
			
			boolean retorno = jdbcProduto.inserir(produto);
			String msg = "";
		
			if(retorno){
				msg = "Produto cadastrado com sucesso!";
			}else{
				msg = "Erro ao cadastrar produto.";
			}

			conec.fecharConexao();

			return this.buildResponse(msg);
			
		}catch(Exception e){
			e.printStackTrace();
			return this.buildErrorResponse(e.getMessage());
		}
		
	}
	
	@GET
	@Path("/buscar")
	@Consumes("application/*")
	@Produces(MediaType.APPLICATION_JSON)
	public Response buscarPorNome(@QueryParam("valorBusca") String nome){
		
		try{
			List<JsonObject> listaProdutos = new ArrayList<JsonObject>();

			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
			JDBCProdutoDAO jdbcProduto = new JDBCProdutoDAO(conexao);
			listaProdutos = jdbcProduto.buscarPorNome(nome);
			conec.fecharConexao();	
			String json = new Gson().toJson(listaProdutos);
			
			return this.buildResponse(json);
		}catch(Exception e){
			e.printStackTrace();
			return this.buildErrorResponse(e.getMessage());
		}
	}
	
	@DELETE
	@Path("/excluir/{id}")
	@Consumes("application/*")
	public Response excluir(@PathParam("id") int id){
		
		try{
			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
			JDBCProdutoDAO jdbcProduto = new JDBCProdutoDAO(conexao);
			
			String msg = "";
			
			boolean retorno = jdbcProduto.deletar(id);
				
			if(retorno){
				msg = "Produto excluído com sucesso!";
			}else{
				msg = "Erro ao excluir produto.";
			}
		
			conec.fecharConexao();

			return this.buildResponse(msg);
			
		}catch(Exception e){
			e.printStackTrace();
			return this.buildErrorResponse(e.getMessage());
		}

	}
	
	@GET
	@Path("/buscarPorId")
	@Consumes("application/*")
	@Produces(MediaType.APPLICATION_JSON)
	public Response buscarPorId(@QueryParam("id") int id){
		
		try{
			Produto produto = new Produto();

			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
			JDBCProdutoDAO jdbcProduto = new JDBCProdutoDAO(conexao);
			produto = jdbcProduto.buscarPorId(id);
			conec.fecharConexao();	
			String json = new Gson().toJson(produto);
			
			return this.buildResponse(json);
		}catch(Exception e){
			e.printStackTrace();
			return this.buildErrorResponse(e.getMessage());
		}
	}
	
	@PUT
	@Path("/alterar")
	@Consumes("application/*")
	public Response alterar(String produtoParam){
		try{
			Produto produto = new ObjectMapper().readValue(produtoParam, Produto.class);
			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
			JDBCProdutoDAO jdbcProduto = new JDBCProdutoDAO(conexao);
			boolean retorno = jdbcProduto.alterar(produto);
			String msg = "";
			if (retorno){
				msg = "Produto alterado com sucesso!";
			}else{
				msg = "Erro ao alterar produto.";
			}

			conec.fecharConexao();	

			return this.buildResponse(msg);
		}catch(Exception e){
			e.printStackTrace();
			return this.buildErrorResponse(e.getMessage());
		}
	}
	


}
*/