package dml.team5;
import java.sql.*;


public class Extractor {
	public void main (Connection connection, String input) throws SQLException {
    	int getName=1;
    	String columnName[];
    	String recordInfo[][];
        
            Connection conn = connection;

            Statement stmt = conn.createStatement ();
            ResultSet rset = stmt.executeQuery(input);
            ResultSetMetaData rsmd = rset.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            columnName = new String[columnsNumber];	//creates an array with the attribute names
            int count = 0;

            //while and if statement to figure out the row length
            while (rset.next()) 
            {
            	++count; // Get data from the current row and use it
            }

            if (count == 0) 
            {
            	System.out.println("No records found");
            }
            
            int newCount=0;
            
            //prints out the attribute names and also add them to the columnName array for later use for XML
            while(getName<=columnsNumber)
            {
            	System.out.print(rsmd.getColumnLabel(getName)+ " ");
            	columnName[newCount] = rsmd.getColumnLabel(getName);
            	getName++;
            	newCount++;
            }
           System.out.println(" ");
           //creates the recordInfo array size
           recordInfo= new String[count][columnsNumber];
           rset = stmt.executeQuery(input);
           newCount = 0;
           
           //gets the records from the system call to oracle.
           //sets array with its the records information
           while(rset.next())
           {
        	   int temp=1;
        	   while(temp <= columnsNumber)
        	   {
        		   System.out.print(rset.getString(temp) + " " );
        		   recordInfo[newCount][temp-1] = rset.getString(temp).trim();
        		   temp++;
        		   
        	   }
        	   
        	   System.out.println("");
        	   newCount++;

           }
           //closes statement
   // stmt.close();
    // Eric Gurvitz Start Here
    System.out.println("<Query>");
    for(int i = 0; i < count; i++)
    {
        System.out.println("<Record" + i +">");
        for(int j = 0; j < columnsNumber; j++)
        {
            System.out.println("\t<"+ columnName[j] + " table=\""+ tableName(input) + "\" name=\""   +"\">");
            System.out.println("\t\t"+ recordInfo[i][j]);
            System.out.println("\t</"+ columnName[j] + "> ");
        }
        System.out.println("</Record"+ i +">");
    }
    System.out.println("</Query"); // Eric Gurvitz Ends Here
    stmt.close();
    }
        
    public String tableName(String s) // added by Eric Gurvitz. Get table name as string following From
    {
        String delims = "[\\s]+"; // use space delimiter
        String[] temp = s.split(delims);
        String r = null;
        for(int i = 0; i < temp.length; i++)
        {
            if(temp[i].equals("from"))
            {
                r = temp[i+1];
            }
        }
        return r; 
    }
    


}
