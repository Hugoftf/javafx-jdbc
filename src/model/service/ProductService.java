package model.service;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.impl.ProductDao;
import model.entities.Product;

public class ProductService {

	private ProductDao categoryDao = DaoFactory.createProductDao();
	
	public List<Product> findAll() {
		return categoryDao.findAll();
	}
	
	public void saveOrUpdate(Product category) {
		if (category.getId() == null) {
			categoryDao.insert(category);
		}
		else {
			categoryDao.update(category);
		}
	}
	
	public void remove(Product category) {
		categoryDao.delete(category.getId());
	}
}
