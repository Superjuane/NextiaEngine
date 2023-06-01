package edu.upc.essi.dtim.nextiaEngine.temp;

import edu.upc.essi.dtim.Graph.URI;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public class DataFrame_MM {
    /**
     * The namespace of the vocabulary as a string
     */
    public static final String uri="https://www.essi.upc.edu/dtim/dataframe-metamodel#";

    protected static final URI resource(String local )
    { return new URI( uri + local ); }

    protected static final URI property (String local )
    { return new URI( uri + local ); }



    public static final URI DataSource = Init.DataSource();
    public static final URI DataFrame = Init.DataFrame();
    public static final URI Data = Init.Data();
    public static final URI DataType = Init.DataType();
    public static final URI Array = Init.Array();
    public static final URI Primitive = Init.Primitive();
    public static final URI String = Init.String();
    public static final URI Number = Init.Number();


    public static final URI hasData     = Init.hasData();
    public static final URI hasDataType     = Init.hasDataType();




    public static class Init {
        public static URI DataSource() { return resource("DataSource"); }
        public static URI DataFrame() { return resource("DataFrame"); } //or object
        public static URI Data() { return resource("Data"); }
        public static URI DataType() { return resource("DataType"); }
        public static URI Array() { return resource("Array"); }
        public static URI Primitive() { return resource("Primitive"); }
        //NOT object <-- Strings, Arrays and Objects. DataFrames are also Python::Objects
        //int64, float64, datetime64, bool
        public static URI String() { return resource("String"); }
        public static URI Number() { return resource("Number"); }

        public static URI hasData() { return property( "hasData"); }

        public static URI hasDataType() { return property( "hasDataType"); }
    }

    /**
     returns the URI for this schema
     @return the URI for this schema
     */
    public static String getURI() {
        return uri;
    }
}
