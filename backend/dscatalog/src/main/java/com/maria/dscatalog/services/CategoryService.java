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
import com.maria.dscatalog.entities.Category;
import com.maria.dscatalog.repositories.CategoryRepository;
import com.maria.dscatalog.services.exceptions.DatabaseException;
import com.maria.dscatalog.services.exceptions.EntityNotFoundException;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository repository;

	@Transactional(readOnly = true)
	public Page<CategoryDTO> findAllPaged(Pageable pageable) {
		Page<Category> list = repository.findAll(pageable);
		Page<CategoryDTO> listDto = list.map(x -> new CategoryDTO(x));

		return listDto;
	}

	@Transactional
	public CategoryDTO findById(Long id) {
		Optional<Category> obj = repository.findById(id);
		Category cat = obj.orElseThrow(() -> new EntityNotFoundException("A categoria "+id+" não existe!"));
		return new CategoryDTO(cat);
	}
	
	@Transactional
	public CategoryDTO insert(CategoryDTO dto) {
		Category entity = new Category();
		entity.setName(dto.getName());
		entity = repository.save(entity);
		return new CategoryDTO(entity);
	}

	@Transactional
	public CategoryDTO update(Long id, CategoryDTO dto) {
		try {
		Category entity = repository.getReferenceById(id);
		entity.setName(dto.getName());
		entity = repository.save(entity);
		return new CategoryDTO(entity);
		}catch(jakarta.persistence.EntityNotFoundException e) {
			throw new EntityNotFoundException("O id: "+ id + " não existe!");
		}
	}
	@Transactional(propagation = Propagation.SUPPORTS)
	public void delete(Long id) {

		if(!repository.existsById(id)) {
			throw new EntityNotFoundException("Categoria não encontrada!");
		}
		try {
			repository.deleteById(id);
		}
		catch(DataIntegrityViolationException e) {
			throw new DatabaseException("Falha de integridade referencial");
		}
		
	}
	

}
