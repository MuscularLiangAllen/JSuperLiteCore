package com.liangtee.jsuperlite.core.service.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Allen on 2017/4/17.
 */

public class BaseService<T, ID extends Serializable> {

    private static final String SIMPLE_SELECT_SQL = "SELECT * FROM %s WHERE %s";

    private static final String SELECT_SQL_LIMIT = "SELECT * FROM %s WHERE %s LIMIT %d, %d";

    private static final String SELECT_SQL_ORDER = "SELECT * FROM %s WHERE %s ORDER BY %s %s";

    private static final String SELECT_SQL_LIMIT_ORDER = "SELECT * FROM %s WHERE %s ORDER BY %s %s LIMIT %d, %d";

    public static final int AESC = 1;

    public static final int DESC = -1;

    protected final static Logger logger = LoggerFactory.getLogger(BaseService.class);

    protected final JdbcTemplate jdbcTemplate;

    protected final Class<T> entityClass;

    private String tableName = null;

    @Autowired
    public BaseService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        entityClass =  (Class)params[0];
        this.tableName = entityClass.getAnnotation(Table.class).name();
    }

    public boolean isExist(String conditions, Object ...params) {
//        String tableName = entityClass.getAnnotation(Table.class).name();

        String SQL = "SELECT COUNT(*) FROM " + tableName + " WHERE " + conditions;

        return jdbcTemplate.queryForObject(SQL, Integer.class, params) > 0 ? true : false;
    }


    public int update(String cols, String conditions, Object ...params) {
//        String tableName = entityClass.getAnnotation(Table.class).name();

        String SQL = "UPDATE " + tableName + " SET " + cols + " WHERE " + conditions;

        return jdbcTemplate.update(SQL, params);

    }

    public void batchUpdate(String cols, String conditions, List<Object[]> values) {
//        String tableName = entityClass.getAnnotation(Table.class).name();

        String SQL = "UPDATE " + tableName + " SET " + cols + " WHERE " + conditions;

        PreparedStatement psmt = null;

        try {
            psmt = jdbcTemplate.getDataSource().getConnection().prepareStatement(SQL);

            for(int i=0; i<values.size(); i++) {
                for(int j=0; j<values.get(i).length; j++) {
                    psmt.setObject(j+1, values.get(i)[j]);
                }
                psmt.addBatch();
            }
            psmt.executeBatch();
            psmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                psmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public boolean delete(String conditions, Object ...params) {
//        String tableName = entityClass.getAnnotation(Table.class).name();

        String SQL = "DELETE FROM " + tableName + " WHERE " + conditions;

        return jdbcTemplate.update(SQL, params) > 0 ? true : false;
    }

    public List<T> findAll(int startPos, int endPos, String sortProperty, int sequence,
                               String conditions, Object ...params) {
        String SQL = null;

        if(startPos < 0 && sortProperty == null) {
            SQL = String.format(SIMPLE_SELECT_SQL, tableName, conditions);
        }
        if(startPos < 0 && sortProperty != null) {
            SQL = String.format(SELECT_SQL_ORDER, tableName, conditions, sortProperty, sequence > 0 ? "AESC" : "DESC");
        }
        if(startPos >= 0 && sortProperty == null) {
            SQL = String.format(SELECT_SQL_LIMIT, tableName, conditions, startPos, endPos);
        }
        if(startPos >= 0 && sortProperty != null) {
            SQL = String.format(SELECT_SQL_LIMIT_ORDER, tableName, conditions, sortProperty, sequence > 0 ? "AESC" : "DESC", startPos, endPos);
        }

        Field[] fields = entityClass.getDeclaredFields();
        List<T> resuList = jdbcTemplate.query(SQL, new RowMapper<T>(){
            @Override
            public T mapRow(ResultSet rs, int rowNum) throws SQLException {
                T entity = null;
                try {
                    entity = entityClass.newInstance();
                    for(Field field : fields) {
                        Field privateField = entity.getClass().getDeclaredField(field.getName());
                        privateField.setAccessible(true);
                        if(privateField.getDeclaredAnnotation(Transient.class) != null)
                            continue;

                        if(privateField.getDeclaredAnnotation(Column.class) != null && !privateField.getDeclaredAnnotation(Column.class).name().isEmpty())
                            privateField.set(entity, rs.getObject(field.getAnnotation(Column.class).name(), field.getType()));
                        else privateField.set(entity, rs.getObject(field.getName(), field.getType()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }

                return entity;
            }

        }, params);

        return resuList;
    }

    public List<T> findAll(String conditions, Object ...params) {
        return findAll(-1, -1, null, -1, conditions, params);
    }

    public List<T> findByPage(PageModel pageModel, String sortProperty, int sequence,
                                  String conditions, Object ...params) {
        return findAll(pageModel.getStartIndex(), pageModel.getPageSize(), sortProperty, sequence, conditions, params);
    }


}
