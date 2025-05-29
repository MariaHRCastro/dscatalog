package com.maria.dscatalog.services;



import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.maria.dscatalog.dto.CategoryDTO;
import com.maria.dscatalog.dto.ProductDTO;
import com.maria.dscatalog.entities.Category;
import com.maria.dscatalog.entities.Product;
import com.maria.dscatalog.repositories.CategoryRepository;
import com.maria.dscatalog.repositories.ProductRepository;
import com.maria.dscatalog.services.exceptions.DatabaseException;
import com.maria.dscatalog.services.exceptions.EntityNotFoundException;

@Service
public class ProductService {

	@Autowired
	private ProductRepository repository;
	
	@Autowired
	private CategoryRepository categoryRepository;

	@Transactional(readOnly = true)
	public Page<ProductDTO> findAllPaged(Pageable pageable) {
		Page<Product> list = repository.findAll(pageable);
		Page<ProductDTO> listDto = list.map(x -> new ProductDTO(x));

		return listDto;
	}

	@Transactional
	public ProductDTO findById(Long id) {
		Optional<Product> obj = repository.findById(id);
		Product prod = obj.orElseThrow(() -> new EntityNotFoundException("O produto "+id+" não existe!"));
		return new ProductDTO(prod, prod.getCategories());
	}
	
	@Transactional
	public ProductDTO insert(ProductDTO dto) {
		Product entity = new Product();
		copyDtoToEntity(dto, entity);
		entity = repository.save(entity);
		return new ProductDTO(entity);
	}

	@Transactional
	public ProductDTO update(Long id, ProductDTO dto) {
		try {
		Product entity = repository.getReferenceById(id);
		copyDtoToEntity(dto, entity);
		entity = repository.save(entity);
		return new ProductDTO(entity);
		}catch(jakarta.persistence.EntityNotFoundException e) {
			throw new EntityNotFoundException("O id: "+ id + " não existe!");
		}
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	public void delete(Long id) {

		if(!repository.existsById(id)) {
			throw new EntityNotFoundException("Produto não encontrada!");
		}
		try {
			repository.deleteById(id);
		}
		catch(DataIntegrityViolationException e) {
			throw new DatabaseException("Falha de integridade referencial");
		}
		
	}
	
	private void copyDtoToEntity(ProductDTO dto, Product entity) {
		entity.setName(dto.getName());
		entity.setDate(dto.getDate());
		entity.setDescription(dto.getDescription());
		entity.setPrice(dto.getPrice());
		entity.setImgUrl(dto.getImgUrl());
		
		entity.getCategories().clear();
		for(CategoryDTO catDto: dto.getCategories()) {
			Category category = categoryRepository.getReferenceById(catDto.getId());
			entity.getCategories().add(category);
		}
	}
	

}
