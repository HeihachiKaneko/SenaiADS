package br.com.coldigogeladeiras.jdbcinterface;

import java.util.List;

import com.google.gson.JsonObject;

import br.com.coldigogeladeiras.modelo.Marca;

public interface MarcaDAO {
	
	public boolean inserir(Marca marca);
	public List<Marca> buscar();
	public List<JsonObject> buscarPorNome(String nome);
	public boolean deletar(int id);
	public Marca buscarPorId(int id);
	public boolean alterarStatus(int id, boolean status);
	public boolean alterar(Marca marca);
	public boolean verificaMarca(int id);
}

