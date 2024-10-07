package model.dao.impl;

import java.util.List;

import model.entities.Category;
import model.entities.Product;

public interface CategoryDao {
	
	void insert(Category category);
	void update(Category category);
	void delete(Integer id);
	Category findById(Integer id);
	List<Category> findAll();
}
