package model.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.impl.ProductDao;
import model.entities.Category;
import model.entities.Product;

public class ProductJDBC implements ProductDao {

	private Connection conn;
	
	public ProductJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void insert(Product product) {
		PreparedStatement st = null;
		try {
			conn.setAutoCommit(false);
			
			st = conn.prepareStatement(
					"INSERT INTO product (Name, price, Manufacturing, Made, categoryId) VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			
			st.setString(1, product.getName());
			st.setDouble(2, product.getPrice());
			st.setDate(3, new Date(product.getManufacturing().getTime()));
			st.setString(4, product.getMade());
			st.setInt(5, product.getCategory().getId());
			int rownsAffected = st.executeUpdate();
			if (rownsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if(rs.next()) {
					int id = rs.getInt(1);
					product.setId(id);
				}
				DB.closeResultSet(rs);
			}
			else {
				throw new DbException("No rows Affected");
			}
			
			conn.commit(); 			
		}
		catch (SQLException e) {
			DB.rollback(e);
		}
		finally {
			DB.closeStatement(st);
		}
		
	}

	@Override
	public void update(Product product) {
		PreparedStatement st = null;
		try {
			conn.setAutoCommit(false);
			st = conn.prepareStatement(
					"UPDATE projectfxjdbc.product SET "
					+ "Name = ?, price = ?, Manufacturing = ?, Made = ?, categoryId = ? "
					+ "WHERE ID = ?");
			
			st.setString(1, product.getName());
			st.setDouble(2, product.getPrice());
			st.setDate(3, new Date(product.getManufacturing().getTime()));
			st.setString(4, product.getMade());
			st.setInt(5, product.getCategory().getId());
			st.setInt(6, product.getId());
			
			st.executeUpdate();
			
			conn.commit();
		}
		catch (SQLException e) {
			DB.rollback(e);
		}
		finally {
			DB.closeStatement(st);
		}
		
	}

	@Override
	public void delete(Integer id) {
		PreparedStatement st = null;
		try {
			conn.setAutoCommit(false);
			st = conn.prepareStatement("DELETE FROM projectfxjdbc.product WHERE id = ?");
			st.setInt(1, id);
			
			int rownsAffected = st.executeUpdate();
			
			if (rownsAffected == 0) {
				throw new DbException("No row affected");
			}
			
			conn.commit();
		}
		catch (SQLException e) {
			DB.rollbackIntegruty(e);
		}
		finally {
			DB.closeStatement(st);
		}
		
	}

	@Override
	public Product findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null; 
		try {
			st = conn.prepareStatement("SELECT projectfxjdbc.product.*,projectfxjdbc.category.name AS categoryName "
					+ "FROM projectfxjdbc.product "
					+ "INNER JOIN projectfxjdbc.category "
					+ "ON projectfxjdbc.product.categoryId = projectfxjdbc.category.id "
					+ "WHERE projectfxjdbc.category.id = ? "
					+ "ORDER BY name");
			
			st.setInt(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				Category category = instanciationCategory(rs);
				Product product = instanciationProduct(rs, category);
				return product;
			}
			else {
				return null;
			
			}
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}	
	}

	private Category instanciationCategory(ResultSet rs) throws SQLException {
		Category category = new Category();
		category.setId(rs.getInt("categoryId"));
		category.setName(rs.getString("CategoryName"));
		return category;
	}

	private Product instanciationProduct(ResultSet rs, Category category) throws SQLException {
		Product product = new Product();
		product.setId(rs.getInt("Id"));
		product.setName(rs.getString("Name"));
		product.setPrice(rs.getDouble("Price"));
		product.setMade(rs.getString("Made"));
		product.setManufacturing((new java.util.Date(rs.getTimestamp("Manufacturing").getTime())));
		product.setCategory(category);
		return product;
	}

	@Override
	public List<Product> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement( 	
					"SELECT projectfxjdbc.product.*,projectfxjdbc.category.Name as CategoryName " 
					+ "FROM projectfxjdbc.product INNER JOIN projectfxjdbc.category "
					+ "ON product.categoryId = category.Id "
					+ "ORDER BY Name");
			
			rs = st.executeQuery();
			
			List<Product> list = new ArrayList<>();
			Map<Integer, Category> map = new HashMap<>();
			
			
			while (rs.next()) {
				Category category = map.get(rs.getInt("Id"));
				if (category == null ) {
					category = instanciationCategory(rs);
					map.put(rs.getInt("categoryId"), category);
				}
				
				Product product = instanciationProduct(rs, category);
				list.add(product);
			}
			return list;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
	}

	@Override
	public List<Product> findAllByCategory(Category category) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement( 	
					"SELECT projectfxjdbc.product.*,category.Name as categoryName " 
					+ "FROM projectfxjdbc.product INNER JOIN projectfxjdbc.category "
					+ "ON projectfxjdbc.product.categoryId = projectfxjdbc.category.Id "
					+ "WHERE categoryId = ? "
					+ "ORDER BY Name");
			
			st.setInt(1, category.getId());
			
			rs = st.executeQuery();
			
			List<Product> list = new ArrayList<>();
			Map<Integer, Category> map = new HashMap<>();
			
			
			while (rs.next()) {
				Category dep = map.get(rs.getInt("CategoryId"));
				if (dep == null ) {
					dep = instanciationCategory(rs);
					map.put(rs.getInt("CategoryId"), dep);
				}
				
				Product product = instanciationProduct(rs, dep);
				list.add(product);
			}
			return list;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
	}
}
