package br.com.codiub.enderecos.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.codiub.enderecos.entity.DadosLogradouros;
import br.com.codiub.enderecos.entity.Distritos;
import br.com.codiub.enderecos.entity.Logradouros;
import br.com.codiub.enderecos.entity.TiposLogradouros;
import br.com.codiub.enderecos.exceptionhandler.PersonalExceptionHandler.Erro;
import br.com.codiub.enderecos.input.DadosLogradourosInput;
import br.com.codiub.enderecos.input.LogradourosInput;
import br.com.codiub.enderecos.repository.DadosLogradourosRepository;
import br.com.codiub.enderecos.repository.DistritosRepository;
import br.com.codiub.enderecos.repository.LogradourosRepository;
import br.com.codiub.enderecos.repository.TiposLogradourosRepository;

@Service
public class LogradourosService {

  @Autowired private LogradourosRepository logradourosRepository;
  //Tonhas 14032021
  @Autowired private DadosLogradourosRepository dadosLogradourosRepository;
  @Autowired private DistritosRepository distritosRepository;
  @Autowired private TiposLogradourosRepository tiposLogradourosRepository;

  public Logradouros atualizar(Long codigo, Logradouros logradouros) {
    Logradouros logradourosSalva = buscarPeloCodigo(codigo);

    BeanUtils.copyProperties(logradouros, logradourosSalva, "id");
    return logradourosRepository.save(logradourosSalva);
  }

  public Logradouros buscarPeloCodigo(Long codigo) {
    Optional<Logradouros> logradourosSalva = logradourosRepository.findById(codigo);
    if (logradourosSalva == null) {
      throw new EmptyResultDataAccessException(1);
    }
    return logradourosSalva.get();
  }
  
  	//Tonhas 14042021
	@Transactional
	public ResponseEntity<Object> save(LogradourosInput logradourosInput) {
		Logradouros logradouros = saveLogradouros(logradourosInput);
		
		saveDadosLogradouros(logradouros, logradourosInput.getDadosLogradouros());
		return ResponseEntity.status(HttpStatus.CREATED).body(logradouros);
	}
	
	//Tonhas 14042021
	private Logradouros saveLogradouros(LogradourosInput logradourosInput) {
		//LOGRADOURO
		Logradouros logradourosSalva = new Logradouros();
		BeanUtils.copyProperties(logradourosInput, logradourosSalva, "id");
		
		Distritos distritos = distritosRepository.findById(logradourosInput.getDistritos().getId()).get();
		TiposLogradouros tiposLogradouros = tiposLogradourosRepository.findById(logradourosInput.getTiposLogradouros().getId()).get();
		logradourosSalva.setDistritos(distritos);
		logradourosSalva.setTiposLogradouros(tiposLogradouros);
		
		logradourosSalva.setDataAlteracao(new Date());
		return logradourosRepository.save(logradourosSalva);
	}
	
	//Tonhas 14042021
	private DadosLogradouros saveDadosLogradouros(Logradouros logradouros, DadosLogradourosInput dadosLogradourosInput) {
		// Dados Logradouros
		DadosLogradouros dadosLogradourosSalva = new DadosLogradouros();
		
		if(dadosLogradourosInput != null) {
			
			BeanUtils.copyProperties(dadosLogradourosInput, dadosLogradourosSalva);
		} 
		
		dadosLogradourosSalva.setDataAlteracao(new Date());
		dadosLogradourosSalva.setLogradouros(logradouros);
		
		return dadosLogradourosRepository.save(dadosLogradourosSalva);
	}
	
	//Tonhas 16042021 - upd
	@Transactional
	public ResponseEntity<Object> atualizar(Long codigo,LogradourosInput logradourosInput) {
		try {
			List<DadosLogradouros> dadosLogradourosList = dadosLogradourosRepository.findByLogradourosId(codigo);
			for (DadosLogradouros dadosLogradouros : dadosLogradourosList) {
				BeanUtils.copyProperties(logradourosInput.getDadosLogradouros(), dadosLogradouros , "id");	
				dadosLogradouros.setDataAlteracao(new Date());
				dadosLogradourosRepository.save(dadosLogradouros);
			}
			
			Logradouros logradourosSalva = buscarPeloCodigo(codigo);
			BeanUtils.copyProperties(logradourosInput, logradourosSalva, "id");
			logradourosSalva.setDataAlteracao(new Date());
			
			Logradouros save = logradourosRepository.save(logradourosSalva);
			return ResponseEntity.ok(save);
		} catch (Exception e) {			
			String mensagemUsuario = "Não foi possivel Atualizar estas Informações";
			String mensagemDesenvolvedor = " "+e.getMessage();
			List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
			return ResponseEntity.badRequest().body(erros);
		}
		
	}
}



