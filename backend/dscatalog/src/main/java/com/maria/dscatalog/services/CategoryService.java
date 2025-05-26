package com.maria.dscatalog.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maria.dscatalog.dto.CategoryDTO;
import com.maria.dscatalog.entities.Category;
import com.maria.dscatalog.repositories.CategoryRepository;
import com.maria.dscatalog.services.exceptions.EntityNotFoundException;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository repository;

	@Transactional(readOnly = true)
	public List<CategoryDTO> findAll() {
		List<Category> list = repository.findAll();
		List<CategoryDTO> listDto = list.stream().map(x -> new CategoryDTO(x)).collect(Collectors.toList());

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
	

}
