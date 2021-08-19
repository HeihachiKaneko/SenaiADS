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
import br.com.coldigogeladeiras.jdbc.JDBCMarcaDAO;

import br.com.coldigogeladeiras.modelo.Marca;



@Path("marca")
public class MarcaRest extends UtilRest{
	
	@GET
	@Path("/buscar")
	@Produces(MediaType.APPLICATION_JSON)
	public Response buscar(){
		
		try{	
			List<Marca> listaMarcas = new ArrayList<Marca>();
			
			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
			JDBCMarcaDAO jdbcMarca = new JDBCMarcaDAO(conexao);
			listaMarcas = jdbcMarca.buscar();
			conec.fecharConexao();	
			return this.buildResponse(listaMarcas);
		}catch(Exception e){
			e.printStackTrace();
			return this.buildErrorResponse(e.getMessage());
		}
		
	}
	
	@POST
	@Path("/inserir")
	@Consumes("application/*")
	public Response inserir(String marcaParam) {
		
		try {
			Marca marca = new Gson().fromJson(marcaParam, Marca.class);
			List<JsonObject> listaMarcas = new ArrayList<JsonObject>();

			
			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
			
			JDBCMarcaDAO jdbcMarca = new JDBCMarcaDAO(conexao);
			listaMarcas = jdbcMarca.buscarPorNome(marca.getNome());

			boolean retorno = false;
			String msg = "";
			
			if (listaMarcas.size() == 0) {
				retorno = jdbcMarca.inserir(marca);

				if (retorno) {
					msg = "Marca cadastrada com sucesso";
				} else {
					msg = "Erro ao cadastrar marca";
				}
			} else {
				msg = "Ja existe uma marca com esse nome";
			}

			conec.fecharConexao();
			
			if (retorno) {
				return this.buildResponse(msg);
			} else {
				return this.buildErrorResponse(msg);
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			return this.buildErrorResponse(e.getMessage());
		}
	}

	@GET
	@Path("/buscarPorNome")
	@Consumes("application/*")
	@Produces(MediaType.APPLICATION_JSON)
	public Response buscarPorNome(@QueryParam("valorBusca") String nome) {
		
		try {
			
			List<JsonObject> listaMarcas = new ArrayList<JsonObject> ();
			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
			JDBCMarcaDAO jdbcMarca = new JDBCMarcaDAO(conexao);
			listaMarcas = jdbcMarca.buscarPorNome(nome);
			conec.fecharConexao();
			
			String json = new Gson().toJson(listaMarcas);
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
			JDBCMarcaDAO jdbcMarca = new JDBCMarcaDAO(conexao);
			
			boolean retorno = false;
			retorno = jdbcMarca.verificaMarca(id);
			String msg = "";
		
			//boolean verificar se eh false
			
			if (!retorno) {
				retorno = jdbcMarca.deletar(id);

				if (retorno) {
					msg = "Marca excluída com sucesso";
				} else {
					msg = "Erro ao excluir a marca";	
				}
			
			} else {
				msg = "A marca que você está tentando excluir está sendo usada em um produto";
			}

			conec.fecharConexao();

			if (retorno) {
				return this.buildResponse(msg);
			} else {
				return this.buildErrorResponse(msg);
			}
		
			
			
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
			Marca marca= new Marca();
			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
			JDBCMarcaDAO jdbcMarca = new JDBCMarcaDAO(conexao);
			
			marca = jdbcMarca.buscarPorId(id);
			
			conec.fecharConexao();
			
			return this.buildResponse(marca);
			
		}catch(Exception e) {
			e.printStackTrace();
			return this.buildErrorResponse(e.getMessage());
		}
		
	}
	
	@PUT
	@Path("/alterar")
	@Consumes("application/*")
	public Response alterar(String marcaParam) {
		
		try {
			Marca marca = new Gson().fromJson(marcaParam, Marca.class);
			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
			JDBCMarcaDAO jdbcMarca = new JDBCMarcaDAO(conexao);
		
			boolean retorno = jdbcMarca.alterar(marca);
			
			String msg = "";
			if (retorno) {
				msg = "Marca alterado com sucesso!";
			}else {
				msg = "Erro ao alterar marca.";
				return this.buildErrorResponse((msg));
			}
			
			conec.fecharConexao();
			
		
			return this.buildResponse(msg);
		
		}catch(Exception e) {
			e.printStackTrace();
			return this.buildErrorResponse(e.getMessage());
		}
		
	}
	
	@PUT
	@Path("/alterar/{id}/{status}")
	@Consumes("application/*")
	public Response alterarStatus(@PathParam("id") int id, @PathParam("status") boolean status) {
		try {
			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
			JDBCMarcaDAO jdbcMarca = new JDBCMarcaDAO(conexao);

			List<JsonObject> listaMarca = new ArrayList<JsonObject>();

			//listaMarca = jdbcMarca.verificaMarca(id);
			boolean retorno = false;
			String msg = "";
			if (listaMarca.size() == 0) {
				retorno = jdbcMarca.alterarStatus(id, status);

				if (retorno) {

					msg = "Status alterado com sucesso";

				} else {
					msg = "Erro ao alterar o status da marca";
					
				}
			} else {
				msg = "A marca que você está tentando alterar está sendo usada em um produto";
			}

			conec.fecharConexao();
			if (retorno) {
				return this.buildResponse(msg);
			} else {
				return this.buildErrorResponse(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return this.buildErrorResponse(e.getMessage());
		}
	}

}

/*
	public Response buscar(){
		try{
			List<Marca> listaMarcas = new ArrayList<Marca>();

			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
			JDBCMarcaDAO jdbcMarca = new JDBCMarcaDAO(conexao);
			listaMarcas = jdbcMarca.buscar();
			conec.fecharConexao();	
			String json = new Gson().toJson(listaMarcas);
			return this.buildResponse(json);
		}catch(Exception e){
			e.printStackTrace();
			return this.buildErrorResponse(e.getMessage());
		}
		//return this.buildResponse("oi!");
	}
*/