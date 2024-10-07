	package model.dao;

import db.DB;
import model.dao.impl.ProductDao;

public class DaoFactory {
	
	public static ProductDao createProductDao() {
		return new ProductJDBC(DB.geConnection());
	}
	
	public static CategoryJDBC createCategoryDao() {
		return new CategoryJDBC(DB.geConnection());
	}

}
