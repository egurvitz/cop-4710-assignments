/**
 * @author Matthew Boyette (N00868808@ospreys.unf.edu)
 * @version 1.0
 * 
 *          This class converts the relational database output of unmodified Oracle PL/SQL selection statements into XML.
 */

package dml.team5;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.tree.ParseTree;
import dml.team5.antlr.PLSQLLexer;
import dml.team5.antlr.PLSQLParser;

public class PLSQL2XMLConverter
{
    /**
     * Lexically analyze the input {@link java.lang.String} as an Oracle PL/SQL query and return the {@link dml.team5.antlr.PLSQLParser}.
     * 
     * @param input
     *            the input {@link java.lang.String}.
     * @return the {@link dml.team5.antlr.PLSQLParser}.
     * @since 1.0
     */
    protected static final PLSQLParser getParser(final String input)
    {
        ANTLRInputStream inputStream = new ANTLRInputStream(input);
        PLSQLLexer lexer = new PLSQLLexer(inputStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        return new PLSQLParser(tokens);
    }

    /**
     * Console-based command-line test driver. Each argument is an input {@link java.lang.String}.
     * 
     * @param args
     *            the command-line arguments.
     * @since 1.0
     */
    public static final void main(final String[] args)
    {
        if ( args.length > 0 )
        {
            for ( int i = 0; i < args.length; i++ )
            {
                if ( i > 0 )
                {
                    System.out.println();
                }

                System.out.println(new PLSQL2XMLConverter(args[i]));
            }
        }
    }

    /**
     * Prints out each row's fields delimited by whitespace characters on separate lines.
     * 
     * @param results
     *            the {@link java.sql.ResultSet} populated with the output of a SQL query.
     * @param rsmd
     *            the {@link java.sql.ResultSetMetaData} that corresponds to the results.
     * @throws SQLException
     *             if a database access error occurs.
     * @since 1.0
     */
    public static final void printSQLResults(final ResultSet results, final ResultSetMetaData rsmd) throws SQLException
    {
        // Loop through rows.
        while ( results.next() )
        {
            // Buffer to store retrieved data.
            StringBuffer printData = new StringBuffer();

            // Loop through columns.
            for ( int i = 0; i < rsmd.getColumnCount(); i++ )
            {
                // Store field data in buffer.
                printData.append(results.getString(i + 1) + " ");
            }

            // Print buffer on new line.
            System.out.println(printData.toString().trim());
        }
    }

    /**
     * The input {@link java.lang.String}.
     * 
     * @since 1.0
     */
    private String input = "";

    /**
     * The output {@link java.lang.StringBuffer}.
     * 
     * @since 1.0
     */
    private StringBuffer output = new StringBuffer();

    /**
     * Constructs a new instance of {@link dml.team5.PLSQL2XMLConverter} based on an input {@link java.lang.String}.
     * 
     * @param input
     *            the input {@link java.lang.String}.
     * @since 1.0
     */
    public PLSQL2XMLConverter(final String input)
    {
        // Trim the input.
        this.setInput(input.trim());
        // Convert the input.
        this.convert();
    }

    /**
     * Constructs the output {@link java.lang.StringBuffer} based on the input {@link java.lang.String}.
     * 
     * @since 1.0
     */
    protected void convert()
    {
        // Declare variables to store the parser, the parse tree, the connection, and the query results.
        PLSQLParser parser = null;
        ParseTree tree = null;
        Connection connection = null;
        ResultSet results = null;

        try
        {
            Extractor extracts = new Extractor();
            // Parse the input query.
            parser = PLSQL2XMLConverter.getParser(this.getInput());
            tree = parser.sql_script();
            String command = this.getInput();
            try
            {
                // Execute the input query.
                connection = Utility.getConnection();
                results = Utility.executeSQLStatement(connection, this.getInput());

                // Is the input query a select statement?
                if ( this.getInput().toLowerCase().startsWith("select ") )
                {
                    if ( results != null )
                    {
                        // Construct the output XML.
                        extracts.main(connection, command);
                       // this.setOutput(this.getXMLResults(results, results.getMetaData()));
                        ResultSetMetaData rsmd = results.getMetaData();
                      //  System.out.println(getInput());
                    // TODO: Convert the relational database output of the unmodified Oracle PL/SQL selection statement into XML.
                        //include a scope conter when doing definition 3 
                   /* System.out.println("<Query>");
                    while(results.next())
                    {
                        System.out.println("<Record>");
                        System.out.println(rsmd.getTableName(0));
                        for(int i = 0; i < rsmd.getColumnCount(); i++)  
                        {
                           // System.out.println(rsmd.getTableName(i+1));
                        //    if(getInput().contains("as"))
                          //  {
                                System.out.print("<" + rsmd.getColumnLabel(i+1) +" table=\"" + rsmd.getTableName(i+1) +"\" name=\"" + rsmd.getColumnName(i+1) +"\"> ");
                                System.out.print(results.getString(i+1) + " ");
                                System.out.println("</" + rsmd.getColumnLabel(i+1) +"> ");
                           // }
                           /* else
                            {
                            System.out.print("<" + rsmd.getColumnName(i+1) +" 2 name=\"" + rsmd.getColumnName(i+1) +"\"> ");
                            System.out.print(results.getString(i+1) + " ");
                            System.out.println("</" + rsmd.getColumnName(i+1) +"> ");
                            }
                        }  
                        System.out.println("</Record>");
                    }
                    System.out.println("</Query>");*/
                    }
                }
            }
            catch ( final SQLException sqle )
            {
                // Failed to execute the input query.
                sqle.printStackTrace();
            }
            finally
            {
                try
                {
                    // Try to close the connection.
                    connection.close();
                }
                catch ( SQLException sqle )
                {
                    // Failed to close the connection.
                    sqle.printStackTrace();
                }

                connection = null;
            }
        }
        catch ( final RecognitionException re )
        {
            // Failed to parse the input query.
            re.printStackTrace();
        }
    }

    /**
     * Returns the input {@link java.lang.String}.
     * 
     * @return the input {@link java.lang.String}.
     * @since 1.0
     */
    public final String getInput()
    {
        return this.input;
    }

    /**
     * Returns the output {@link java.lang.StringBuffer}.
     * 
     * @return the output {@link java.lang.StringBuffer}.
     * @since 1.0
     */
    protected final StringBuffer getOutput()
    {
        return this.output;
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

    /**
     * Construct an output {@link java.lang.StringBuffer} containing the XML.
     * 
     * @param results
     *            the {@link java.sql.ResultSet} populated with the output of a SQL query.
     * @param rsmd
     *            the {@link java.sql.ResultSetMetaData} that corresponds to the results.
     * @return a {@link java.lang.StringBuffer} containing the output XML.
     * @throws SQLException
     *             if a database access error occurs.
     * @since 1.0
     */
    protected StringBuffer getXMLResults(final ResultSet results, final ResultSetMetaData rsmd) throws SQLException
    {
        // TODO: Convert the relational database output of the unmodified Oracle PL/SQL selection statement into XML.
        StringBuffer output = new StringBuffer();

        // Loop through rows.
        while ( results.next() )
        {
            // Loop through columns.
            for ( int i = 0; i < rsmd.getColumnCount(); i++ )
            {
                //
            }
        }

        return output;
    }

    /**
     * Allows the input {@link java.lang.String} to be modified.
     * 
     * @param input
     *            the input {@link java.lang.String}.
     * @since 1.0
     */
    protected final void setInput(final String input)
    {
        this.input = input;
    }

    /**
     * Allows the output {@link java.lang.StringBuffer} to be modified.
     * 
     * @param output
     *            the output {@link java.lang.StringBuffer}.
     * @since 1.0
     */
    protected final void setOutput(final StringBuffer output)
    {
        this.output = output;
    }

    /**
     * Returns a {@link java.lang.String} representation of the output {@link java.lang.StringBuffer}.
     * 
     * @return the output {@link java.lang.String} of the conversion process.
     * @since 1.0
     * @see java.lang.Object#toString()
     */
    @Override
    public final String toString()
    {
        return this.getOutput().toString();
    }
}
