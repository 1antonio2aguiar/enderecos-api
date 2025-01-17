package br.com.codiub.enderecos.repository.logradouros;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.util.StringUtils;

import br.com.codiub.enderecos.entity.Bairros;
import br.com.codiub.enderecos.entity.Cidades_;
import br.com.codiub.enderecos.entity.Distritos_;
import br.com.codiub.enderecos.entity.Estados_;
import br.com.codiub.enderecos.entity.Logradouros;
import br.com.codiub.enderecos.entity.Logradouros_;
import br.com.codiub.enderecos.entity.Paises_;
import br.com.codiub.enderecos.entity.TiposLogradouros_;
import br.com.codiub.enderecos.filter.BairrosFilter;
import br.com.codiub.enderecos.filter.LogradourosFilter;

public class LogradourosRepositoryImpl implements LogradourosRepositoryQuery {

	  @PersistenceContext private EntityManager manager;

	  @Override
	  public Page<Logradouros> filtrar(LogradourosFilter logradourosFilter, Pageable pageable) {
	    CriteriaBuilder builder = manager.getCriteriaBuilder();
	    CriteriaQuery<Logradouros> criteria = builder.createQuery(Logradouros.class);
	    Root<Logradouros> root = criteria.from(Logradouros.class);

	    List<Order> orders = QueryUtils.toOrders(pageable.getSort(), root, builder);

	    Predicate[] predicates = criarRestricoes(logradourosFilter, builder, root);
	    criteria.where(predicates).orderBy(orders);

	    TypedQuery<Logradouros> query = manager.createQuery(criteria);
	    adicionarRestricoesDePaginacao(query, pageable);

	    return new PageImpl<>(query.getResultList(), pageable, total(logradourosFilter));
	  }
	  
	  //AA - Referente a lista
	  @Override
	  public List<Logradouros> filtrar(LogradourosFilter logradourosFilter){
		  
		  CriteriaBuilder builder = manager.getCriteriaBuilder();
		  CriteriaQuery<Logradouros> criteria = builder.createQuery(Logradouros.class);
		  Root<Logradouros> root = criteria.from(Logradouros.class);
		  
		  Predicate[] predicates = criarRestricoes(logradourosFilter, builder, root);
		  criteria.where(predicates);
		  
		  TypedQuery<Logradouros> query = manager.createQuery(criteria);
		  return query.getResultList();
	  }
	  // Até aqui.

	  private Predicate[] criarRestricoes(
	      LogradourosFilter logradourosFilter, CriteriaBuilder builder, Root<Logradouros> root) {
	    List<Predicate> predicates = new ArrayList<>();

	    // DISTRITO
	    if (logradourosFilter.getDistritosFilter() != null) {
	      // CIDADE
	      if (logradourosFilter.getDistritosFilter().getCidadesFilter() != null) {
	        
	        // ID
	        if (logradourosFilter.getDistritosFilter().getCidadesFilter().getId() != null) {
	          predicates.add(
	              builder.equal(
	                  root.get(Logradouros_.DISTRITOS).get(Distritos_.CIDADES).get(Cidades_.ID),
	                  logradourosFilter.getDistritosFilter().getCidadesFilter().getId()));
	        }
	        // NOME
	        if (StringUtils.hasLength(
	            logradourosFilter.getDistritosFilter().getCidadesFilter().getNome())) {
	          predicates.add(
	              builder.like(
	                  builder.lower(
	                      root.get(Logradouros_.DISTRITOS).get(Distritos_.CIDADES).get(Cidades_.NOME)),
	                  "%"
	                      + logradourosFilter
	                          .getDistritosFilter()
	                          .getCidadesFilter()
	                          .getNome()
	                          .toLowerCase()
	                      + "%"));
	        }

	      }

	      // ID
	      if (logradourosFilter.getDistritosFilter().getId() != null) {
	        predicates.add(
	            builder.equal(
	                root.get(Logradouros_.DISTRITOS).get(Distritos_.ID),
	                logradourosFilter.getDistritosFilter().getId()));
	      }
	      // NOME
	      if (StringUtils.hasLength(logradourosFilter.getDistritosFilter().getNome())) {
	        predicates.add(
	            builder.like(
	                builder.lower(root.get(Logradouros_.DISTRITOS).get(Distritos_.NOME)),
	                "%" + logradourosFilter.getDistritosFilter().getNome().toLowerCase() + "%"));
	      }
	    }

	    // ID
	    if (logradourosFilter.getId() != null) {
	      predicates.add(builder.equal(root.get(Logradouros_.ID), logradourosFilter.getId()));
	    }
	    // NOME
	    if (StringUtils.hasLength(logradourosFilter.getNome())) {
	      predicates.add(
	          builder.like(
	              builder.lower(root.get(Logradouros_.NOME)),
	              "%" + logradourosFilter.getNome().toLowerCase() + "%"));
	    }

	    // NOME_REDUZIDO
	    if (StringUtils.hasLength(logradourosFilter.getNomeReduzido())) {
	      predicates.add(
	          builder.like(
	              builder.lower(root.get(Logradouros_.NOME_REDUZIDO)),
	              "%" + logradourosFilter.getNomeReduzido().toLowerCase() + "%"));
	    }

	    // NOME_SIMPLIFICADO
	    if (StringUtils.hasLength(logradourosFilter.getNomeSimplificado())) {
	      predicates.add(
	          builder.like(
	              builder.lower(root.get(Logradouros_.NOME_SIMPLIFICADO)),
	              "%" + logradourosFilter.getNomeSimplificado().toLowerCase() + "%"));
	    }

	    // TIPO_LOGRADOURO
	    if (logradourosFilter.getTiposLogradourosFilter() != null) {
	      // DESCRICAO
	      if (StringUtils.hasLength(logradourosFilter.getTiposLogradourosFilter().getDescricao())) {
	        predicates.add(
	            builder.like(
	                builder.lower(
	                    root.get(Logradouros_.TIPOS_LOGRADOUROS).get(TiposLogradouros_.DESCRICAO)),
	                "%"
	                    + logradourosFilter.getTiposLogradourosFilter().getDescricao().toLowerCase()
	                    + "%"));
	      }

	      // ID
	      if (logradourosFilter.getTiposLogradourosFilter().getId() != null) {
	        predicates.add(
	            builder.equal(
	                root.get(Logradouros_.TIPOS_LOGRADOUROS).get(TiposLogradouros_.ID),
	                logradourosFilter.getTiposLogradourosFilter().getId()));
	      }
	      // SIGLA
	      if (StringUtils.hasLength(logradourosFilter.getTiposLogradourosFilter().getSigla())) {
	        predicates.add(
	            builder.like(
	                builder.lower(root.get(Logradouros_.TIPOS_LOGRADOUROS).get(TiposLogradouros_.SIGLA)),
	                "%"
	                    + logradourosFilter.getTiposLogradourosFilter().getSigla().toLowerCase()
	                    + "%"));
	      }
	    }

	    return predicates.toArray(new Predicate[predicates.size()]);
	  }

	  private void adicionarRestricoesDePaginacao(TypedQuery<?> query, Pageable pageable) {
	    int paginaAtual = pageable.getPageNumber();
	    int totalRegistrosPorPagina = pageable.getPageSize();
	    int primeiroRegistroDaPagina = paginaAtual * totalRegistrosPorPagina;

	    query.setFirstResult(primeiroRegistroDaPagina);
	    query.setMaxResults(totalRegistrosPorPagina);
	  }

	  private Long total(LogradourosFilter filter) {
	    CriteriaBuilder builder = manager.getCriteriaBuilder();
	    CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
	    Root<Logradouros> root = criteria.from(Logradouros.class);

	    Predicate[] predicates = criarRestricoes(filter, builder, root);
	    criteria.where(predicates);

	    criteria.select(builder.count(root));
	    return manager.createQuery(criteria).getSingleResult();
	  }
	}
