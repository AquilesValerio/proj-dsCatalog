package com.dvsuperior.dscatalog.services;

import com.dvsuperior.dscatalog.DTO.CategoryDTO;
import com.dvsuperior.dscatalog.DTO.ProductDTO;
import com.dvsuperior.dscatalog.entities.Category;
import com.dvsuperior.dscatalog.entities.Product;
import com.dvsuperior.dscatalog.repository.CategoryRepository;
import com.dvsuperior.dscatalog.repository.ProductRepository;
import com.dvsuperior.dscatalog.services.exceptions.DatabaseException;
import com.dvsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllPaged(PageRequest pageRequest) {
        Page<Product> list = repository.findAll(pageRequest);
        return list.map(x -> new ProductDTO(x));
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Optional<Product> obj = repository.findById(id);
        Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
        return new ProductDTO(entity, entity.getCategories());
    }

    @Transactional
    public ProductDTO insertProduct(ProductDTO dto) {
        Product entity = new Product();
       // entity.setName(dto.getName());
        entity = repository.save(entity);
        return new ProductDTO(entity);
    }

    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO dto) {
        try {
            Product entity = repository.getReferenceById(id);
          //  entity.setName(dto.getName());
            entity = repository.save(entity);
            return new ProductDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id not found " + id);
        }
    }

    public void deleteProduct(Long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException(("Id not found " + id));
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity violation");
        }
    }

}
