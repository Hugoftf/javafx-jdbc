package model.dao.impl;

import java.util.List;

import model.entities.Category;
import model.entities.Product;

public interface ProductDao {

	void insert(Product product);
	void update(Product product);
	void delete(Integer id);
	Product findById(Integer id);
	List<Product> findAllByCategory(Category category);
	List<Product> findAll();
}
