package base;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Base implements Serializable  {

    String table;


    public Base(){}


    public Base(String table){
        this.table = table;
    }


    public void setTable(String table){
        this.table = table;
    }


    public String getTable(){
        return this.table;
    }


    // requete select dans une bdd
    // @param filter(this) : élément pour prendre les condition
    // @param plus : les conditions : order by , limit , ...
    public ArrayList<Object> find (String plus ,Connection connection) throws SQLException {
        
        ArrayList<String> column = this.getColumnTable(connection);
        
        String request = this.findRequest( plus,column, connection);
        
        Class<?> classe = this.getClass();
        
        return this.find(request, classe,connection);
    
    }
    
    public ArrayList<Object> find (String request ,Class<?> classe,Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet;
        try {
            resultSet = statement.executeQuery(request);
            ArrayList<Object> list = new ArrayList<>();
            while(resultSet.next()) {
                try {
                    Object object = createObject(classe,resultSet);
                    list.add(object);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            resultSet.close();
            return list;
        } catch (SQLException e) {
            throw new SQLException("Error : " + request);
        } finally{
            statement.close();
        }
    }


    // création du requete de select
    // @param filter(this) : élément pour prendre les condition
    // @param plus : les conditions : order by , limit , ...
    public String findRequest (String plus,ArrayList<String> column ,Connection connection) throws SQLException {

        String table = this.getTable();
        String condition = this.getCondition(column);

        String request = "SELECT * FROM " + table + condition;
        request = request + plus;
        
        System.out.println(request);

        return request;

    }


    //Maka ny colonne any @base
    public ArrayList<String> getColumnTable(Connection connection) throws SQLException{

        String request = "SELECT * FROM " + this.getTable() + " LIMIT 1 " ;
        System.out.println(request);
        Statement statement = connection.createStatement();
        ResultSet resultSet;
        
        try {
        
            resultSet = statement.executeQuery(request);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int nbColumn = resultSetMetaData.getColumnCount();

            ArrayList<String> list = new ArrayList<>();
        
            for (int i = 0; i < nbColumn; i++) {
        
                list.add(resultSetMetaData.getColumnName(i+1));
        
            }

            resultSet.close();

            return list;

        } catch (SQLException e) {

            throw new SQLException("Error : " + request);

        } finally{

            statement.close();

        }

    } 


    //Maka ny condition ny SELECT
    public String getCondition(ArrayList<String> column){
        
        Reflect reflect = new Reflect();
        Tool tool = new Tool();
        
        ArrayList<String> conditionList = new ArrayList<>() ;
        
        for (int i = 0; i < column.size(); i++) {
        
            try {
        
                Object object = reflect.executeGetter(this, column.get(i));
        
                if (!object.equals(0.0) && !object.equals(0) && !object.equals(null) && !object.equals("")) {
        
                    String value = object.toString();
        
                    if (!tool.isNumber(value)) {
        
                        value = " '" + value + "' ";
        
                    }

                    String condition = column.get(i) + " = " + value;
                    conditionList.add(condition);

                }
            } catch (Exception e) {}
        
        }

        String condition = "";
        
        for (int i = 0; i < conditionList.size(); i++) {
        
            condition = condition + conditionList.get(i);
        
            if (i < conditionList.size() - 1 || conditionList.get(i).isEmpty()) {
        
                condition += " AND ";
        
            }
        
        }
        
        if (!condition.trim().equals("")) {
        
            condition = " WHERE " + condition;
        
        }
        
        return condition;
    
    }

    
    // transformer une ligne de table en object
    // @param resultSet ligne de la table retournée
    // @param classe(this.getClass()) classe de l'objet à créer
    public Object createObject (Class<?> classe,ResultSet resultSet) throws  Exception {
        
        Reflect reflect = new Reflect();
        
        Object object = reflect.createObject(classe);
        ArrayList<Field> fieldList = reflect.getFields(classe);
        
        for (int i = 0; i < fieldList.size(); i++) {
        
            try {
            
                String attribut = fieldList.get(i).getName();
                String attributValue = resultSet.getString(attribut);
                
                if(attributValue == null)
                    continue;
                
                reflect.executeSetter(object, attribut, attributValue);
            
            } catch (SQLException e) {
                //e.printStackTrace();
            }
        
        }
        
        return  object;
    
    }


    // Insertion de nouveau objet
    // @param object(this) objet à inserer
    // @param connection
    public void insert(Connection connection) throws Exception {

        String request = this.insertRequest(connection);
        System.out.println("request insert : " + request );
        Statement statement = connection.createStatement();
        
        try {
        
            statement.executeUpdate(request);
        
        } catch (SQLException e) {
        
            throw new SQLException("Error : " + request);
        
        } finally {
        
            statement.close();
        
        }
    
    }


    // Création de la requete d'insertion
    // @param object(this) objet à inserer
    public String insertRequest (Connection connection) throws Exception {
        
        Reflect reflect = new Reflect();
        Tool tool = new Tool();
        
        ArrayList<String> listColumn = this.getColumnTable(connection);
        
        String attribut = "";
        String valueAttribut = "";
        
        for (int i = 0; i < listColumn.size(); i++) {
        
            if (listColumn.get(i).trim().equalsIgnoreCase("id")) {
        
                continue;
        
            }
            
            String tempAttribut = listColumn.get(i);
            
            Object valueObject = reflect.executeGetter(this, tempAttribut);
            
            System.out.println(tempAttribut);
            
            if(valueObject != null) {
                String value = valueObject.toString();
                
                if (!tool.isNumber(value)) {

                    value = " '" + value + "' ";

                }

                attribut = attribut + tempAttribut;
                valueAttribut = valueAttribut + value;

                if (i < listColumn.size() -1 ) {

                    attribut = attribut + " , ";
                    valueAttribut = valueAttribut + " , ";

                }
            }
        }

        String request = "INSERT INTO " + this.getTable() + "(" + attribut + ")" + " VALUES (" + valueAttribut + ")";

        return request;

    }

    
    public void update(Connection connection) throws Exception{

        String request = this.updateRequest(connection);
        
        Statement statement = connection.createStatement();
        
        try {
        
            statement.executeUpdate(request);
        
        } catch (SQLException e) {
        
            e.printStackTrace();
        
        } finally {
        
            statement.close();
        
        }
    
    }

    
    public String updateRequest ( Connection connection) throws Exception {

        Reflect reflect = new Reflect();
        Tool tool = new Tool();

        ArrayList<String> listColumn = this.getColumnTable( connection);

        String update = "";

        for (int i = 0; i < listColumn.size(); i++) {

            if (listColumn.get(i).trim().equalsIgnoreCase("id")) {

                continue;

            }

            Object valueObject = reflect.executeGetter(this, listColumn.get(i));

            if(valueObject != null) {
                
                String value = valueObject.toString();
                
                if (!tool.isNumber(value)) {

                    value = " '" + value + "' ";

                }

                update = update + listColumn.get(i) + " = " + value;

                if (i < listColumn.size() - 1) {

                    update += " , ";

                }
            }
        }

        Object id = reflect.executeGetter(this, "id");
        String request = "UPDATE " + this.getTable() + " SET " + update + " WHERE id = " + id.toString();;

        return request;

    }


    public void delete(Connection connection) throws Exception{

        Reflect reflect = new Reflect();
        
        Object id = reflect.executeGetter(this, "id");
        String request = "DELETE FROM " + this.getTable() + " WHERE id = " + id.toString();
        
        Statement statement = connection.createStatement();
        
        try {
        
            statement.executeUpdate(request);
        
        } catch (SQLException e) {
        
            e.printStackTrace();
        
        } finally {
        
            statement.close();
        
        }
    
    }


    //Mi-vérifier hoe any @base ilay objet
    public void ifExist(Connection connection) throws Exception {

        Reflect reflect = new Reflect();

        String id = reflect.executeGetter(this, "id").toString();
        String request = "SELECT * FROM " + this.getTable() + " WHERE id = " + id;

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(request);

        if (resultSet.next()) {

            resultSet.close();
            statement.close();
            return;

        }

        resultSet.close();
        statement.close();

        throw new Exception("Objet non existant");

    }

}

