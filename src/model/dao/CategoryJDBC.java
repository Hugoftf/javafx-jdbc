package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.impl.CategoryDao;
import model.entities.Category;

public class CategoryJDBC implements CategoryDao {

	private Connection conn;
	
	public CategoryJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void insert(Category category) {
		PreparedStatement st = null;
		try {
			conn.setAutoCommit(false);
			st = conn.prepareStatement(
					"INSERT INTO projectfxjdbc.category (Name) "
					+ " VALUES (?)", Statement.RETURN_GENERATED_KEYS);
					
			st.setString(1, category.getName());
			
			
			int rowsAffected = st.executeUpdate();
			
			if (rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					category.setId(id);
				}
				DB.closeResultSet(rs);
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
	public void update(Category category) {
		PreparedStatement st = null;
		try {
			conn.setAutoCommit(false);
			st = conn.prepareStatement(
					"UPDATE projectfxjdbc.category SET "
							+ "Name = ? "
							+ "WHERE ID = ?");
			st.setString(1, category.getName());
			st.setInt(2, category.getId());
			
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
			st = conn.prepareStatement("DELETE FROM projectfxjdbc.category WHERE id = ?");
			
			st.setInt(1, id);
			
			int rownsAffected = st.executeUpdate();
			
			if (rownsAffected == 0) {
				throw new DbException("No rows Affcted");
			}
			
			
			conn.commit();
		}
		
		
		catch (SQLException e) {
			DB.rollbackIntegruty(e);
		}
		
	}

	@Override
	public Category findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT projectfxjdbc.category.*,category.name "
					+ "FROM category  WHERE Id = ?");
			
			st.setInt(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				Category category = instanciationCategory(rs);
				return category;
			}
			else {
				return null;
			}
			
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		
	}

	private Category instanciationCategory(ResultSet rs) throws SQLException {
		Category category = new Category();
		category.setId(rs.getInt("Id"));
		category.setName(rs.getString("Name"));
		return category;
	}

	@Override
	public List<Category> findAll() {
	    PreparedStatement st = null;
	    ResultSet rs = null;
	    try {
	        st = conn.prepareStatement("SELECT * FROM projectfxjdbc.category");
	        rs = st.executeQuery();

	        List<Category> list = new ArrayList<>();
	        while (rs.next()) {
	            Category category = instanciationCategory(rs);
	            list.add(category);
	        }
	        return list;
	    } catch (SQLException e) {
	        throw new DbException(e.getMessage());
	    } finally {
	        DB.closeResultSet(rs);
	        DB.closeStatement(st);
	    }
	}


}
