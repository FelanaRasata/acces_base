package base;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connexion implements Serializable {
    
    public Connection getConnection()  throws ClassNotFoundException, SQLException  {	

	    Class.forName("org.postgresql.Driver");
	    
        String url = "jdbc:postgresql://localhost/energieRenouvelable";
        String user = "postgres";
        String pwd = "mdp";
	    
        Connection connection = DriverManager.getConnection(url, user, pwd);
        
        return connection;		
    
    }

}
