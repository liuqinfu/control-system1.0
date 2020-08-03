package com.aether.sos.wifi.interceptor;

import com.aether.sos.wifi.common.finals.PubFinals;
import com.aether.sos.wifi.common.utils.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * 
 * @description:分页拦截器
 * 
 * @version:v1.0.0 
 * @author:huanjun
 *
 *
 */
@Component
@Intercepts({ 
@Signature(type = Executor.class, method = "query", args = { 
    MappedStatement.class, Object.class, RowBounds.class,
    ResultHandler.class})})
@Slf4j
public class PageInterceptor implements Interceptor{

	/* (non-Javadoc)
	 * @see org.apache.ibatis.plugin.Interceptor#intercept(org.apache.ibatis.plugin.Invocation)
	 */
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		MappedStatement mappedStatement = (MappedStatement) invocation  
	                .getArgs()[0];  
	    String sqlId = mappedStatement.getId();
	  
	    //判断是否是分页sql
	    if(sqlId.endsWith(PubFinals.PAGE_SUFFIX)){
	       BoundSql boundSql = mappedStatement.getBoundSql(invocation.getArgs()[1]);
	  	   //查询参数
	  	   Object parameterObject = boundSql.getParameterObject();
	  	   //原始sql 
	  	   String orgSql = boundSql.getSql();
	  	   //重选总记录数
	  	   Long totpage = this.getTotalCount(mappedStatement, orgSql, boundSql, parameterObject);
    	   //如果有结果集
    	   if(totpage > 0){
    		 return this.getPageResult(parameterObject, orgSql, mappedStatement, invocation, totpage, boundSql); 
    	   } else{
    		  PageResult<Object> pageResult = new PageResult<Object>(new ArrayList<Object>(), totpage, PubFinals.PAGE_SIZE_DEFAULT);
 	   		  return pageResult;  
    	   }
	    }
	    //不是分页执行默认操作
	    return invocation.proceed();
	    
	}
	
	

	/**
	 * @Description:查询分页结果集
	 * @param invocation
	 * @param query_statement
	 * @return
	 *
	 * @version:v1.0
	 */
	private Object exeQuery(Invocation invocation, MappedStatement query_statement) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Object[] args = invocation.getArgs();
        return invocation.getMethod().invoke(invocation.getTarget(),
                new Object[] { query_statement, args[1], args[2], args[3] });
	}

	/* (non-Javadoc)
	 * @see org.apache.ibatis.plugin.Interceptor#plugin(java.lang.Object)
	 */
	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	/* (non-Javadoc)
	 * @see org.apache.ibatis.plugin.Interceptor#setProperties(java.util.Properties)
	 */
	@Override
	public void setProperties(Properties arg0) {
		
	}
	
	/**
	 * 
	 * 
	 * @Description:获取统计记录数sql
	 *
	 * @param orgSql
	 * @return
	 *
	 * @version:v1.0
	 */
	private String getCountSql(String orgSql){
		return "SELECT COUNT(1) FROM (" + orgSql + ") target";
	}
	
	/**
	 * @Description:生成分页sql
	 *
	 * @param orgSql
	 * @return
	 *
	 * @version:v1.0
	 */
	private String getPageSql(String orgSql, Map<String, Object> param){
		Integer start = (Integer.parseInt(param.get(PubFinals.PAGE_INDEX).toString())-1)* Integer.parseInt(param.get(PubFinals.PAGE_SIZE).toString());
//		Integer end = (Integer.parseInt(param.get(PubFinals.PAGE_INDEX).toString()) + 1)*Integer.parseInt(param.get(PubFinals.PAGE_SIZE).toString());
		StringBuilder pageSql = new StringBuilder(200);
	    pageSql.append("select temp.* from ( ");
	    pageSql.append(orgSql);  
	    pageSql.append(" ) temp limit ").append(start);
	    pageSql.append(",").append(param.get(PubFinals.PAGE_SIZE));
	    return pageSql.toString();
	}
	
	/**
	 * @Description:复制boundSql
	 *
	 * @param ms
	 * @param boundSql
	 * @param sql
	 * @return
	 *
	 * @version:v1.0
	 */
	private BoundSql copyFromBoundSql(MappedStatement ms, BoundSql boundSql, String sql) {
	    BoundSql newBoundSql = new BoundSql(ms.getConfiguration(),sql, boundSql.getParameterMappings(), boundSql.getParameterObject());  
	    for (ParameterMapping mapping : boundSql.getParameterMappings()) {  
	        String prop = mapping.getProperty();
	        if (boundSql.hasAdditionalParameter(prop)) {  
	            newBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));  
	        }  
	    }  
	    return newBoundSql;  
	}  
	
	/**
	 * @Description:获取总记录数
	 *
	 * @param mappedStatement
	 * @param orgSql
	 * @param boundSql
	 * @param parameterObject
	 * @return
	 *
	 * @version:v1.0
	 */
	private Long getTotalCount(MappedStatement mappedStatement, String orgSql, BoundSql boundSql, Object parameterObject){
		//查询总记录数
		String countSql = this.getCountSql(orgSql);
		//获取数据链接查询数据总记录数
 	   	Connection connection = null;
 	   	PreparedStatement countStmt = null;
 	   	ResultSet rs = null;
 	    long totpage=0; 
		try {
			connection = mappedStatement.getConfiguration().getEnvironment().getDataSource().getConnection();
			countStmt = connection.prepareStatement(countSql);
			BoundSql countBS = copyFromBoundSql(mappedStatement, boundSql, countSql);
//			PreparedStatementHandler preparedStatementHandler = new PreparedStatementHandler(null, mappedStatement, parameterObject, null, null, countBS);
			DefaultParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, parameterObject, countBS);
			parameterHandler.setParameters(countStmt);  
//	 	   	rs = countStmt.executeQuery();
	 	   	rs = countStmt.executeQuery();

	   		if (rs.next()) {    
	   			totpage = rs.getInt(1);    
	   		} 
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
			  if(null != rs)
				rs.close();
			  if(null != countStmt)
				countStmt.close();    
			  if(null != connection)  
		 	   	connection.close(); 
			} catch (SQLException e) {
				e.printStackTrace();
			}    
	 	 
		}            
 	   	return totpage;
	}
	
	/**
	 * @Description:查询结果集
	 *
	 * @param parameterObject
	 * @param orgSql
	 * @param mappedStatement
	 * @param invocation
	 * @param total
	 * @param boundSql
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 *
	 * @version:v1.0
	 */
	public PageResult<Object> getPageResult(Object parameterObject, String orgSql, MappedStatement mappedStatement, Invocation invocation, Long total, BoundSql boundSql) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		 if(parameterObject instanceof Map){
			  //获取分页参数
	   		 @SuppressWarnings("unchecked")
             Map<String, Object> params = (Map<String, Object>)parameterObject;
	   		 buildParam(params);
	   		  //查询分页结果集
	   		  String pageSql = this.getPageSql(orgSql,params);
	   		  //获取结果集
	   		  StaticSqlSource sqlsource = new StaticSqlSource(mappedStatement.getConfiguration(), pageSql,
	   	                boundSql.getParameterMappings());
	   		  MappedStatement.Builder builder = new MappedStatement.Builder(mappedStatement.getConfiguration(), "id_temp_result", sqlsource,
	   	                SqlCommandType.SELECT);
	   	      builder.resultMaps(mappedStatement.getResultMaps()).resultSetType(mappedStatement.getResultSetType())
	   	                .statementType(mappedStatement.getStatementType());
	   	      MappedStatement query_statement = builder.build();
	   	      @SuppressWarnings("rawtypes")
              List data = (List) exeQuery(invocation, query_statement);
	   	      int pageSize = PubFinals.PAGE_SIZE_DEFAULT;
	   	      if (!StringUtils.isEmpty(params.get(PubFinals.PAGE_SIZE))) {
	   	    	pageSize = Integer.parseInt(String.valueOf(params.get(PubFinals.PAGE_SIZE)));
	   	      }
	   	      PageResult<Object> pageResult = new PageResult<Object>(data, total, pageSize);
//	   	      Object pageResult = new PageResult<Object>(data, total, pageSize);
	   		  return pageResult;
		 }
		 return null;
	}
	
	private void buildParam(Map<String, Object> params) {
		if (params == null) {
			params = new HashMap<String, Object>();
		} 
		if (StringUtils.isEmpty(params.get(PubFinals.PAGE_INDEX))) {
			if (params.get("params") != null && ((Map)params.get("params")).get(PubFinals.PAGE_INDEX) != null){
				params.put(PubFinals.PAGE_INDEX, Integer.valueOf(((Map)params.get("params")).get(PubFinals.PAGE_INDEX).toString()));
			} else {
				params.put(PubFinals.PAGE_INDEX, 1);
			}
		} 
		if (StringUtils.isEmpty(params.get(PubFinals.PAGE_SIZE))) {
			if (params.get("params") != null && ((Map)params.get("params")).get(PubFinals.PAGE_SIZE) != null){
				params.put(PubFinals.PAGE_SIZE, Integer.valueOf(((Map)params.get("params")).get(PubFinals.PAGE_SIZE).toString()));
			} else {
				params.put(PubFinals.PAGE_SIZE, PubFinals.PAGE_SIZE_DEFAULT);
			}
		}
	}
	
}
