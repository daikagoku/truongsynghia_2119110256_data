package com.example.admin.template;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import com.example.admin.dao.CategoryDao;
import com.example.admin.mapper.CategoryMapper;
import com.example.admin.mapper.ProductMapper;
import com.example.core.Convert;
import com.example.core.entity.CategoryEntity;
import com.example.core.entity.ProductEntity;

public class CategoryTemplate implements CategoryDao {

	private DataSource dataSource;
	private JdbcTemplate jdbcTemplateObject;
	private Convert convert = new Convert();

	@Override
	public void setDataSource(DataSource ds) {
		this.dataSource = ds;
		this.jdbcTemplateObject = new JdbcTemplate(ds);
	}

	@Override
	public int getCount() {
		String SQL = "select count(*) as count " + "from category c";
		Map<String, Object> count = jdbcTemplateObject.queryForMap(SQL);
		return (int) count.get("count");
	}

	@Override
	public int put(CategoryEntity category) {
		category.setAlias(convert.getAlias(category.getTitle()));
		String SQL = "update category " + " set title = ?" + " set alias = ?" + " set parent_id = ?" + "  where id = ?";
		int count = jdbcTemplateObject.update(SQL, category.getTitle(), category.getAlias(), category.getParentId(),
				category.getId());
		return count;
	}

	public int[] puts(List<CategoryEntity> categorys) {
		String SQL = "update category " + " set title = ?" + " set alias = ?" + " set parent_id = ?" + "  where id = ?";

		int[] counts = jdbcTemplateObject.batchUpdate(SQL, new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				categorys.get(i).setAlias(convert.getAlias(categorys.get(i).getTitle()));
				ps.setString(1, categorys.get(i).getTitle());
				ps.setString(2, categorys.get(i).getAlias());
				ps.setLong(4, categorys.get(i).getParentId());
				ps.setLong(5, categorys.get(i).getId());
				;
			}

			public int getBatchSize() {
				return categorys.size();
			}
		});
		return counts;
	}

	@Override
	public int delete(long id) {
		String SQL = "delete from category "
				+"  where id = ?";
		int count = jdbcTemplateObject.update(SQL, id);
		return count;
	}
	public int[] deletes(long[] ids) {
		String SQL = "delete from category "
				+"  where id = ?";		
		int[] counts = jdbcTemplateObject.batchUpdate(SQL,
				new BatchPreparedStatementSetter() {
				   public void setValues(PreparedStatement ps, int i) throws SQLException {
					   ps.setLong(1, ids[i]);
				   }
				   public int getBatchSize() {
				      return ids.length;
				   }
				});
		return counts;
	}

	@Override
	public int post(CategoryEntity category) {
		category.setAlias(convert.getAlias(category.getTitle()));
		String SQL = "insert into category "
				+"(title,alias,parent_id)"
				+"values(?,?,?)";
		int count = jdbcTemplateObject.update(SQL, 
				category.getTitle(),
				category.getAlias(),
				category.getParentId());
		return count;
	}
	public int[] posts(List<CategoryEntity> categorys) {
		String SQL = "insert into category "
				+"(title,alias,parent)"
				+"values(?,?,?)";		
		int[] counts = jdbcTemplateObject.batchUpdate(SQL,
				new BatchPreparedStatementSetter() {
				   public void setValues(PreparedStatement ps, int i) throws SQLException {
					   categorys.get(i).setAlias(convert.getAlias(categorys.get(i).getTitle()));						
					    ps.setString(1, categorys.get(i).getTitle());
					    ps.setString(2, categorys.get(i).getAlias());
					    if(categorys.get(i).getParentId() instanceof Long) {
						    ps.setLong(3,categorys.get(i).getParentId());
					    }else {
					    	ps.setNull(3, Types.NULL);
					    }
				   }
				   public int getBatchSize() {
				      return categorys.size();
				   }
				});
		return counts;
	}
	public List<CategoryEntity> get(long id) {
		String SQL = "select * "
				+"from category c"
				+" where c.id = ?";
		List<CategoryEntity> categorys = jdbcTemplateObject.query(SQL, new CategoryMapper(),new Object[] { id});
		return categorys;
	}
	public List<CategoryEntity> get(String alias) {
		String SQL = "select * "
				+"from category c"
				+" where c.alias = ?";
		List<CategoryEntity> categorys = jdbcTemplateObject.query(SQL, new CategoryMapper(),new Object[] { alias});
		return categorys;
	}
	public List<CategoryEntity> getAll() {
		String SQL = "select * "
				+" from category ";
		List<CategoryEntity> categorys = jdbcTemplateObject.query(SQL, new CategoryMapper());
		return categorys;
	}

}