package com.aiop.lda.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConfigNeo4jDBUtil {
    public Connection getNeo4jConnection() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:neo4j:bolt://127.0.0.1:7687","neo4j","Hf7589935");
        return conn;
    }
    
    public static void main(String[] args) {
    	  ConfigNeo4jDBUtil configNeo4jDBUtil = new ConfigNeo4jDBUtil();
    	   
    	    try{
    	    	 Connection conn = configNeo4jDBUtil.getNeo4jConnection();
    	        Statement statement = conn.createStatement();
    	        ResultSet resultSet = statement.executeQuery("MATCH p=(()<-[:旧爱]-()-[:旧爱]->()) RETURN p LIMIT 50");
    	        while (resultSet.next()){
    	            System.out.println(resultSet.getString(1));
    	        }
    	    }catch (Exception e){
    	        e.printStackTrace();
    	    }
	}
}
