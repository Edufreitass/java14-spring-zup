package com.loiane.service;

import java.math.BigInteger;
import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;

import com.loiane.enums.ProductStatus;
import com.loiane.record.ProductRecord;

@Service
public class ProductService {

	private final JdbcTemplate template;
	
	// Text Blocks
	private final String findByIdSql = """
			SELECT * FROM Product \
			WHERE id = ?
			""";
	
	private final String insertSql = """
			INSERT INTO Product (name, status)
			VALUES (?, ?)
			""";
	
	// RowMapper, used to map the columns (without Lambda)
	/* private final RowMapper<ProductRecord> productRowMapper = new RowMapper<ProductRecord>() {

		@Override
		public ProductRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new ProductRecord(
					rs.getInt("id"),
					rs.getString("name"),
					rs.getInt("status"));
		}
	}; */
	
	// RowMapper with Lambda
	private final RowMapper<ProductRecord> productRowMapper  = (rs, rowNum) -> new ProductRecord (
			rs.getInt("id"),
			rs.getString("name"),
			rs.getInt("status"));

	public ProductService(JdbcTemplate template) {
		this.template = template;
	}
	
	public ProductRecord findById(Integer id) {
		return this.template.queryForObject(findByIdSql, new Object[] {id}, productRowMapper);
	}
	
	public ProductRecord create(String name, ProductStatus status) {
		// using new switch case with lambda expression
		var statusCode = switch (status) {
			case ACTIVE -> 1; // return 1
			case INACTIVE -> 0; // return 0
		};
		
		var params = List.of(
				new SqlParameter(Types.VARCHAR, "name"),
				new SqlParameter(Types.INTEGER, "status")
				);
		
		// pscf = PreparedStatementCreatorFactory
		var pscf = new PreparedStatementCreatorFactory(insertSql, params) {
			{
			setReturnGeneratedKeys(true);
			setGeneratedKeysColumnNames("id");
			}
		};
		
		var psc = pscf.newPreparedStatementCreator(List.of(name, statusCode));
		var generatedKey = new GeneratedKeyHolder();
		this.template.update(psc, generatedKey);
		if (generatedKey.getKey() instanceof BigInteger id) {
			// id was passed as parameter
//			BigInteger id = (BigInteger) generatedKey.getKey();
			return findById(id.intValue());
		}
		throw new IllegalArgumentException("Could not create product " + name + ".");
	}

}
