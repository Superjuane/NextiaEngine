package edu.upc.essi.dtim.nextiaEngine.temp;

import edu.upc.essi.dtim.Graph.URI;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public class RDF {
    public static final String uri = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    /** returns the URI for this schema
     @return the URI for this schema
     */

    protected static URI resource(String local)
    { return new URI( uri + local ); }

    public static final URI type = Init.type();

    public static final URI Property = Init.Property();


    public static class Init {
        public static URI type() {return resource("Type");}
        public static URI Property() {return resource ("Property");}
    }

}
