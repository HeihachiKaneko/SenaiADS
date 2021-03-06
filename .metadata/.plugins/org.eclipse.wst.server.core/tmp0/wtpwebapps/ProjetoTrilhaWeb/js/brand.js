COLDIGO.marca = new Object();

$(document).ready(function() {

	//Cadastra no banco a Marca informada
	COLDIGO.marca.cadastrar = function() {
		
		var marca = new Object();
		
		marca.nome = document.frmAddMarca.nome.value;
		
		if (marca.nome == ""){
				COLDIGO.exibirAviso("Cadastre uma marca!");
		}else{
			$.ajax({
				type: "POST",
				url: COLDIGO.PATH + "marca/inserir",
				data:JSON.stringify(marca),
				success: function (msg) {
					COLDIGO.exibirAviso(msg);
					COLDIGO.marca.buscar();
					$("#addMarca").trigger("reset");
				},
				error: function (info) {
					COLDIGO.exibirAviso(info.responseText + " " + info.status + " - " + info.statusText);
				}
			});
			
		}
	}
	
	//Busca no banco e exibe na pagina as marcas que atendam a solicitao do usuario
	COLDIGO.marca.buscar = function(){
		
		var valorBusca = $("#campoBuscaMarca").val();
		
		$.ajax({
			type: "GET",
			url: COLDIGO.PATH + "marca/buscarPorNome",
			data: "valorBusca=" + valorBusca,
			success: function(dados){
				
				dados = JSON.parse(dados);
				//console.log(status);
				//console.log(dados);
				
				$("#listaMarcas").html(COLDIGO.marca.exibir(dados));
			
			},
			error: function(info){
				COLDIGO.exibirAviso(info.responseText + " " + info.status + " - " + info.statusText);
			}
		});
	};
	
	//Transforma os dados do produto recebido do servidor em uma tabela HTML
	COLDIGO.marca.exibir = function(listaDeMarcas){
		
		var tabela = "<table>" +
		"<tr>" +
		"<th>Nome</th>" +
		"<th class='acoes'>Ações</th>" +
		"<th class='status'>Status</th>" +
		"</tr>";
		
		if (listaDeMarcas != undefined && listaDeMarcas.length > 0) {
			
			for (var i=0; i<listaDeMarcas.length; i++) {
				
				//var checked = listaDeMarcas[i].status === 1 ? 'checked' : ''
				//listaDeMarcas[i].status === 1 (condicao do if)
				//"?" serve como parametro para um else
								
				var checked = ''
				if(listaDeMarcas[i].status === 1){
					checked = 'checked';
				}
					
				console.log(listaDeMarcas[i].status)
				
				tabela += "<tr>" +
					"<td>" + listaDeMarcas[i].nome+"</td>" +
					"<td>" + 
						"<a onclick=\"COLDIGO.marca.exibirEdicao('"+listaDeMarcas[i].id+"')\"><img src='../../imgs/edit.png' alt='Editar registro'></a> " +
						"<a onclick=\"COLDIGO.marca.exibirExclusao('"+listaDeMarcas[i].id+"')\"><img src='../../imgs/delete.png' alt='Excluir registro'></a> " +
					"</td>" +
					"<td class='center'>" +
						"<label class='switch'>" +
							"<input onclick=\"COLDIGO.marca.alteraStatus('"+listaDeMarcas[i].id+"', this)\" type='checkbox' " + checked + ">" +
							"<span class='slider round'>" +
						"</label>" +
					"</td>" +
					"</tr>";
			}
		
			
		} else if (listaDeMarcas == "") {
			tabela += "<tr><td colspan='6'>Nenhum registro encontrado</td></tr>";
		}
		tabela += "</table>";
		
		return tabela;
	};
	
	//Executa a funcao de buscar ao carregar a pagina
	COLDIGO.marca.buscar();
	
	
	
	COLDIGO.marca.exibirExclusao = function(id){
		
		var modalExcluiMarca = {
				title: "Excluir Marca",
				height: 400,
				width: 550,
				modal: true,
				buttons: {
					"Sim": function(){
						COLDIGO.marca.excluir(id);
						
					},
					"Cancelar": function(){
						$(this).dialog("close");
					}
				},
				close: function(){
					//caso o usuario simplesmente feche a caixa de edicao
					//nao deve acontecer nada
				}
			};
			
			$("#modalExcluiMarca").dialog(modalExcluiMarca);
	}
	
	//Exclui o produto selecionado
	COLDIGO.marca.excluir = function(id){
		$.ajax({
			type: "DELETE",
			url: COLDIGO.PATH + "marca/excluir/" + id,
			success: function(msg){
				COLDIGO.exibirAviso(msg);
				COLDIGO.marca.buscar();
				$("#modalExcluiMarca").dialog("close");
			},
			error: function(info){
				COLDIGO.exibirAviso(info.responseText + " " + info.status + " - " + info.statusText);
			}
			
			
		});
		
		$("#modalExcluiMarca").dialog(modalExcluiMarca);
	};
	
	
	//Carrega no BD os dados do produto selecionado para alteracao e coloca-os no formulario de alteracao
	COLDIGO.marca.exibirEdicao = function(id){
		$.ajax({
			type:"GET",
			url: COLDIGO.PATH + "marca/buscarPorId",
			data: "id="+id,
			success: function(marca){
				
				//console.log(marca);
				
				document.frmEditaMarca.idMarca.value = marca.id;
				document.frmEditaMarca.nome.value = marca.nome;
				
				var modalEditaMarca = {
					title: "Editar Marca",
					height: 400,
					width: 550,
					modal: true,
					buttons: {
						"Salvar": function(){
							COLDIGO.marca.editar();
							
						},
						"Cancelar": function(){
							$(this).dialog("close");
						}
					},
					close: function(){
						//caso o usuario simplesmente feche a caixa de edicao
						//nao deve acontecer nada
					}
				};
				
				$("#modalEditaMarca").dialog(modalEditaMarca);
				
			},
			error: function(info){
				COLDIGO.exibirAviso(info.responseText + " " + info.status + " - " + info.statusText);
			}
		});
	}
	
	//Realiza a edicao dos dados no BD
	COLDIGO.marca.editar = function(){
		
		var marca = new Object();
		marca.id = document.frmEditaMarca.idMarca.value;
		marca.nome = document.frmEditaMarca.nome.value;
	
		//console.log(marca);
		
		$.ajax({
			type:"PUT",
			url: COLDIGO.PATH + "marca/alterar",
			data: JSON.stringify(marca),
			success: function(msg){
				COLDIGO.exibirAviso(msg);
				COLDIGO.marca.buscar();
				$("#modalEditaMarca").dialog("close");
				
			},
			error: function(info){
				COLDIGO.exibirAviso(info.responseText + " " + info.status + " - " + info.statusText);
				
			}
		});
	};
	
	COLDIGO.marca.alteraStatus = function(id, checkbox){
		console.log(checkbox);
		var status = checkbox.checked;
		
		$.ajax({
			type: "PUT",
			url: COLDIGO.PATH + "marca/alterar/" + id + "/" + status,
			success: function(msg){
				COLDIGO.exibirAviso(msg);
				
			},
			error:  function(info){
				//!status serve para manter o checkbox atualizado sem ter que recarregar a pagina
				//pois se der erro no servidor eh necessario voltar para a etapa inicial
				checkbox.checked = !status;
				console.log(info);
				COLDIGO.exibirAviso(info.responseText + " " + info.status + " - " + info.statusText);
				//COLDIGO.exibirAviso("qualquer coisa");
			},
		});
	}
	
});