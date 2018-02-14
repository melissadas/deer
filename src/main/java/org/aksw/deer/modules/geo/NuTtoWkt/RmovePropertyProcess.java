package org.aksw.deer.modules.geo.NuTtoWkt;

import java.util.ArrayList;
import java.util.Iterator;

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
		for(int i=0;i<subjects.size();i++)
			m.removeAll(subjects.get(i), p3, rdfNodes.get(i));

		return m;

	}


//	public static void main(String[] args) throws IOException {
//
//		String inputtFile= "/home/abddatascienceadmin/deer/NUT_DATA/out1234.ttl";
//
//		Model model= Reader.readModel(inputtFile);
//
//		Model newModel= removePropertyProcess(model);
//
//		String outputFile= "/home/abddatascienceadmin/deer/NUT_DATA/1out1234.ttl";
//
//		Writer.writeModel(newModel, "TTL", outputFile);
//
//
//	}

}
