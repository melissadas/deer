package org.aksw.deer.modules.geo;

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

public class RemoveLatLongProperty {

	static String ngeo = "http://geovocab.org/geometry#";
	static String geo  = "http://www.w3.org/2003/01/geo/wgs84_pos#";

	static ArrayList<Resource> subjects= new ArrayList<Resource>() ;
	static ArrayList <RDFNode> rdfNodes=new ArrayList <RDFNode>();
	static ArrayList <Property> properties =new ArrayList <Property>();

	public static Model removePropertyProcess(Model m)
	{


		Property p1=	ResourceFactory.createProperty(geo, "lat");
		Property p2=	ResourceFactory.createProperty(geo, "long");
		properties.add(p1);
		properties.add(p2);
		for(int j=0;j<properties.size();j++) {
			Iterator<Statement>iter = m.listStatements(null,properties.get(j),(RDFNode)null);
			while(iter.hasNext())
			{
				Statement st = iter.next();
				Resource sub =st.getSubject();
				RDFNode rdfNode=st.getObject();
				subjects.add(sub);
				rdfNodes.add(rdfNode);

				//System.out.println(" the subjects are "+rdfNode);

			}
			System.out.println(" the size of subjects is "+subjects.size());
			System.out.println(" the size of nodes is "+rdfNodes.size());
			for(int i=0;i<subjects.size();i++) {
				m.remove(subjects.get(i),properties.get(j) , rdfNodes.get(i));
				//m.remove(subjects.get(1), p3, rdfNodes.get(1));
				//m.remove(subjects.get(2), p3, rdfNodes.get(2));
				//m.remove(subjects.get(3), p3, rdfNodes.get(3));
			}}
		return m;

	}


	public static void main(String[] args) throws IOException {

		String inputtFile= "/home/abdullah/deer/NUT_DATA/DE_geometry_out1.ttl";

		Model model= Reader.readModel(inputtFile);

		Model newModel= removePropertyProcess( model);

		String outputFile= "/home/abdullah/deer/NUT_DATA/DE_geometry_out2.ttl";

		Writer.writeModel(newModel, "TTL", outputFile);


	}


}
