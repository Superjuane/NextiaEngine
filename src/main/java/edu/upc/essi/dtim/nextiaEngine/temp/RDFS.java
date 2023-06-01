package edu.upc.essi.dtim.nextiaEngine.temp;

import edu.upc.essi.dtim.Graph.URI;

public class RDFS {
    public static final String uri = "http://www.w3.org/2000/01/rdf-schema#";

    /** returns the URI for this schema
     @return the URI for this schema
     */


    protected static URI resource(String local)
    { return new URI( uri + local ); }

    public static final URI label = Init.label();
    public static final URI Class = Init.Class();
    public static final URI ContainerMembershipProperty = Init.containerMembershipProperty();

    public static final URI domain = Init.domain();
    public static final URI range = Init.range();

    public static class Init {
        public static URI label() {return resource("Label");}
        public static URI domain() {return resource ("Domain");}

        public static URI range() {return resource ("Range");}

        public static URI Class() {return resource("Class");}
        public static URI containerMembershipProperty() {return resource("ContainerMembershipProperty");}
    }

}
