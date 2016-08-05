/**   
 * @(#)SqlHelper.java	2016年6月20日	下午4:27:35	   
 *     
 * Copyrights (C) 2016艺龙旅行网保留所有权利
 */
package com.elong.nb.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.elong.nb.common.util.CommonsUtil;
import com.mysql.jdbc.StringUtils;

/**
 * (类型功能说明描述)
 *
 * <p>
 * 修改历史:											<br>  
 * 修改日期    		修改人员   	版本	 		修改内容<br>  
 * -------------------------------------------------<br>  
 * 2016年6月20日 下午4:27:35   zhangyang.zhu     1.0    	初始化创建<br>
 * </p> 
 *
 * @author		zhangyang.zhu  
 * @version		1.0  
 * @since		JDK1.7
 */
public class SqlHelper {
	private static Logger LocalMsg=LogManager.getLogger(SqlHelper.class);
	private  String filePath,sqlDriverClassName,sqlUrl,sqlUserName,sqlPassword,sqlMaxActive,sqlInitialSize,sqlMinIdle,sqlMaxIdle,sqlMinEvictableIdleTimeMillis,sqlTimeBetweenEvictionRunsMillis,sqlTestOnBorrow;
	private static String defaultFileName="conf/custom/env/jobjdbc.properties";
//	private Lock lock = new ReentrantLock();
	private Connection _CONN=null;
	private static BasicDataSource basicDataSource=null;

	public SqlHelper(String fileName){
		this.filePath=StringUtils.isEmptyOrWhitespaceOnly(fileName)==true?defaultFileName:fileName;
		this.sqlDriverClassName=CommonsUtil.loadProperties(this.filePath).getProperty("jdbc.sql.driverClassName");
		this.sqlUrl=CommonsUtil.loadProperties(this.filePath).getProperty("jdbc.sql.url");
		this.sqlUserName=CommonsUtil.loadProperties(this.filePath).getProperty("jdbc.sql.username");
		this.sqlPassword=CommonsUtil.loadProperties(this.filePath).getProperty("jdbc.sql.password");
		this.sqlMaxActive=CommonsUtil.loadProperties(this.filePath).getProperty("jdbc.sql.maxactive");
		this.sqlInitialSize=CommonsUtil.loadProperties(this.filePath).getProperty("jdbc.sql.initialsize");
		this.sqlMinIdle=CommonsUtil.loadProperties(this.filePath).getProperty("jdbc.sql.minidle");
		this.sqlMaxIdle=CommonsUtil.loadProperties(this.filePath).getProperty("jdbc.sql.maxidle");
		this.sqlMinEvictableIdleTimeMillis=CommonsUtil.loadProperties(this.filePath).getProperty("jdbc.sql.minevictableidletimemillis");
		this.sqlTimeBetweenEvictionRunsMillis=CommonsUtil.loadProperties(this.filePath).getProperty("jdbc.sql.timebetweenevictionrunsmillis");
		this.sqlTestOnBorrow=CommonsUtil.loadProperties(this.filePath).getProperty("jdbc.sql.testonborrow");
			/*if(basicDataSource==null){
				this.filePath=StringUtils.isEmptyOrWhitespaceOnly(fileName)==true?defaultFileName:fileName;
				this.sqlDriverClassName=CommonsUtil.loadProperties(this.filePath).getProperty("jdbc.sql.driverClassName");
				this.sqlUrl=CommonsUtil.loadProperties(this.filePath).getProperty("jdbc.sql.url");
				this.sqlUserName=CommonsUtil.loadProperties(this.filePath).getProperty("jdbc.sql.username");
				this.sqlPassword=CommonsUtil.loadProperties(this.filePath).getProperty("jdbc.sql.password");
				this.sqlMaxActive=CommonsUtil.loadProperties(this.filePath).getProperty("jdbc.sql.maxactive");
				this.sqlInitialSize=CommonsUtil.loadProperties(this.filePath).getProperty("jdbc.sql.initialsize");
				this.sqlMinIdle=CommonsUtil.loadProperties(this.filePath).getProperty("jdbc.sql.minidle");
				this.sqlMaxIdle=CommonsUtil.loadProperties(this.filePath).getProperty("jdbc.sql.maxidle");
				this.sqlMinEvictableIdleTimeMillis=CommonsUtil.loadProperties(this.filePath).getProperty("jdbc.sql.minevictableidletimemillis");
				this.sqlTimeBetweenEvictionRunsMillis=CommonsUtil.loadProperties(this.filePath).getProperty("jdbc.sql.timebetweenevictionrunsmillis");
				this.sqlTestOnBorrow=CommonsUtil.loadProperties(this.filePath).getProperty("jdbc.sql.testonborrow");
				basicDataSource=new BasicDataSource();
				basicDataSource.setDriverClassName(this.sqlDriverClassName);
				basicDataSource.setUrl(this.sqlUrl);
				basicDataSource.setUsername(this.sqlUserName);
				basicDataSource.setPassword(this.sqlPassword);
				basicDataSource.setMaxActive(Integer.parseInt(this.sqlMaxActive));
				basicDataSource.setInitialSize(Integer.parseInt(this.sqlInitialSize));
				basicDataSource.setMinIdle(Integer.parseInt(this.sqlMinIdle));
				basicDataSource.setMaxIdle(Integer.parseInt(this.sqlMaxIdle));
				basicDataSource.setMinEvictableIdleTimeMillis(Long.parseLong(this.sqlMinEvictableIdleTimeMillis));
				basicDataSource.setTimeBetweenEvictionRunsMillis(Long.parseLong(this.sqlTimeBetweenEvictionRunsMillis));
				basicDataSource.setTestOnBorrow(Boolean.parseBoolean(this.sqlTestOnBorrow));
		}
		*/
	}
	
/*	private boolean getConnection() throws Exception{
		if(_CONN!=null) return true;
		if(lock.tryLock()){
			try {
				Class.forName(sqlDriverClassName);
				_CONN=DriverManager.getConnection(sqlUrl, sqlUserName, sqlPassword);

			} catch (ClassNotFoundException | SQLException e) {
				LocalMsg.error("getConnection Exception:"+e.getMessage());
				throw new Exception(e);
			}
		}
		return true;
	}*/
	private boolean getConnection() throws Exception{
		if(_CONN!=null) return true;
		synchronized (this) {
			if(_CONN==null){
			try {
				Class.forName(sqlDriverClassName);
				_CONN=DriverManager.getConnection(sqlUrl, sqlUserName, sqlPassword);
				//_CONN=basicDataSource.getConnection();

			} catch (SQLException e) {
				LocalMsg.error("getConnection Exception:"+e.getMessage());
				throw new Exception(e);
			}
			}
		}
		return true;
	}
	
	public void closeConnection() throws SQLException{
		try {
			if(_CONN!=null){
			_CONN.close();
			_CONN=null;
			}
		} catch (SQLException e) {
			LocalMsg.error("closeConnection Exception:"+e.getMessage());
			throw new SQLException();
		}
	}
	
	public ResultSet  getResultSet(String sql,Object[] params) throws Exception{
		ResultSet resultSet=null;
		try {
			if(getConnection()){
				PreparedStatement ps=_CONN.prepareStatement(sql);
				if (params!=null) {
					for(int i=0;i<params.length;i++){
						ps.setObject(i+1, params[i]);
					}
				}
				//resultSet=ps.executeQuery();
			boolean executResult=ps.execute();
			if(executResult){
				resultSet=ps.getResultSet();
				//resultSet.close();
			}
				return resultSet;
			}
		} catch (Exception e) {
			LocalMsg.error("getResultSet Exception:"+e.getMessage());
			closeConnection();
			throw  new Exception(e);
		}finally {
		/*	if(resultSet!=null)resultSet.close();
			if(_CONN!=null) closeConnection();*/
		}
		return resultSet;
	}

	
	public String getSingle(String sql,Object... params) throws Exception{
		try{
			if(getConnection()){
				PreparedStatement ps=_CONN.prepareStatement(sql);
				if(params!=null){
					for(int i=0;i<params.length;i++){
						ps.setObject(i+1, params[i]);
					}
				}
				ResultSet rs=ps.executeQuery();
				if(rs.next()){
					return rs.getString(1);
				}
			}
		}
		catch(Exception e){
			LocalMsg.error("getSingle Exception:"+e.getMessage());
			throw new Exception(e);
		}
		finally {
			closeConnection();
		}
		return null;
	}
}
