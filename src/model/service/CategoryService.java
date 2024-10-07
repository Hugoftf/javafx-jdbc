package model.service;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.impl.CategoryDao;
import model.entities.Category;

public class CategoryService {

	private CategoryDao categoryDao = DaoFactory.createCategoryDao();
	
	public List<Category> findAll() {
		return categoryDao.findAll();
	}
	
	public void saveOrUpdate(Category category) {
		if (category.getId() == null) {
			categoryDao.insert(category);
		}
		else {
			categoryDao.update(category);
		}
	}
	
	public void remove(Category category) {
		categoryDao.delete(category.getId());
	}
}
