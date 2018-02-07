package org.aksw.deer.modules.geo.NuTtoWkt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.aksw.deer.io.Reader;
import org.aksw.deer.io.Writer;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;



public class RmovePropertyProcess {

	static String ngeo = "http://geovocab.org/geometry#";

	static ArrayList<Resource> subjects= new ArrayList<Resource>() ;
	static ArrayList <RDFNode> rdfNodes=new ArrayList <RDFNode>();

	public static Model removePropertyProcess(Model m)
	{


		Property p3=	ResourceFactory.createProperty(ngeo, "posList");

		Iterator<Statement>iter = m.listStatements(null,p3,(RDFNode)null);
		while(iter.hasNext())
		{
			Statement st = iter.next();
			Resource sub =st.getSubject();
			RDFNode rdfNode=st.getObject();
			subjects.add(sub);
			rdfNodes.add(rdfNode);

			System.out.println(" the subjects are "+rdfNode);

		}

		m.remove(subjects.get(0), p3, rdfNodes.get(0));
		m.remove(subjects.get(1), p3, rdfNodes.get(1));
		m.remove(subjects.get(2), p3, rdfNodes.get(2));
		m.remove(subjects.get(3), p3, rdfNodes.get(3));
		return m;

	}


	public static void main(String[] args) throws IOException {

		String inputtFile= "/home/abdullah/deer/NUT_DATA/DE_geometry_out.ttl";

		Model model= Reader.readModel(inputtFile);

		Model newModel= removePropertyProcess(model);

		String outputFile= "/home/abdullah/deer/NUT_DATA/DE_geometry_out1.ttl";

		Writer.writeModel(newModel, "TTL", outputFile);


	}

}
