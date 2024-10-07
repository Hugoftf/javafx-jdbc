package db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public DbException(String msg) {
		super(msg);
	}
	
}
