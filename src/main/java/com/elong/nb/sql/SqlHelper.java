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
	private  String filePath,sqlDriverClassName,sqlUrl,sqlUserName,sqlPassword;
	private static String defaultFileName="conf/custom/env/jobjdbc.properties";
	Connection _CONN=null;
	
	public String getSqlDriverClassName() {
		return sqlDriverClassName;
	}

	public void setSqlDriverClassName(String sqlDriverClassName) {
		this.sqlDriverClassName = sqlDriverClassName;
	}

	public String getSqlUrl() {
		return sqlUrl;
	}

	public void setSqlUrl(String sqlUrl) {
		this.sqlUrl = sqlUrl;
	}

	public String getSqlUserName() {
		return sqlUserName;
	}

	public void setSqlUserName(String sqlUserName) {
		this.sqlUserName = sqlUserName;
	}

	public String getSqlPassword() {
		return sqlPassword;
	}

	public void setSqlPassword(String sqlPassword) {
		this.sqlPassword = sqlPassword;
	}

	public SqlHelper(String fileName){

		this.filePath=StringUtils.isEmptyOrWhitespaceOnly(fileName)==true?defaultFileName:fileName;
		this.sqlDriverClassName=CommonsUtil.loadProperties(this.filePath).getProperty("jdbc.sql.driverClassName");
		this.sqlUrl=CommonsUtil.loadProperties(this.filePath).getProperty("jdbc.sql.url");
		this.sqlUserName=CommonsUtil.loadProperties(this.filePath).getProperty("jdbc.sql.username");
		this.sqlPassword=CommonsUtil.loadProperties(this.filePath).getProperty("jdbc.sql.password");
	}
	
	private boolean getConnection(){
		if(_CONN!=null) return true;
			try {
				Class.forName(sqlDriverClassName);
				_CONN=DriverManager.getConnection(sqlUrl, sqlUserName, sqlPassword);

			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
				LocalMsg.error("getConnection Exception:"+e.getMessage());
				return false;
			}
		return true;
	}
	
	private void closeConnection(){
		try {
			_CONN.close();
			_CONN=null;
		} catch (SQLException e) {
			LocalMsg.error("closeConnection Exception:"+e.getMessage());
		}
	}
	
	public ResultSet  getResultSet(String sql,Object[] params){
		ResultSet resultSet=null;
		try {
			if(getConnection()){
				PreparedStatement ps=_CONN.prepareStatement(sql);
				if (params!=null) {
					for(int i=0;i<params.length;i++){
						ps.setObject(i+1, params[i]);
					}
				}
				resultSet=ps.executeQuery();
			}
		} catch (Exception e) {
			LocalMsg.error("getResultSet Exception:"+e.getMessage());
			e.printStackTrace();
			closeConnection();
		}
		return resultSet;
	}
	
	public Object getSingle(String sql,Object... params){
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
			e.printStackTrace();
		}
		finally {
			closeConnection();
		}
		return null;
	}
}
