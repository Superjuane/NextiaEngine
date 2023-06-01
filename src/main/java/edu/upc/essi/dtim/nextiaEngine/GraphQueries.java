package edu.upc.essi.dtim.nextiaEngine;

import edu.upc.essi.dtim.Queries.*;
import edu.upc.essi.dtim.Graph.*;

public interface GraphQueries {
    public QueryResult executeQuery (Query q, Graph g);
    //public void executeQuery(String q, Graph g);
}

