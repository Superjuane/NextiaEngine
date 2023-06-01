package edu.upc.essi.dtim.nextiaEngine.temp;

import edu.upc.essi.dtim.Graph.URI;

public class XSD {
    public static final String uri = "http://www.w3.org/2001/XMLSchema#";

    /** returns the URI for this schema
     @return the URI for this schema
     */


    protected static URI resource(String local)
    { return new URI( uri + local ); }

    public static final URI xstring = Init.xstring();
    public static final URI xint = Init.xint();

    public static class Init {
        public static URI xstring() {return resource("String");}
        public static URI xint() {return resource("int");}
    }

}
