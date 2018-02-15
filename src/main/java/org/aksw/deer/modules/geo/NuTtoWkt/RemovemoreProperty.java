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

public class RemovemoreProperty {


	public static Model removeMorePropertyProcess(Model m)
	{
		String rdf ="http://www.w3.org/1999/02/22-rdf-syntax-ns#";

		ArrayList <Resource> subjects= new ArrayList<Resource>() ;
		ArrayList <RDFNode> rdfNodes=new ArrayList <RDFNode>();
		ArrayList <Property> properties =new ArrayList <Property>();

		Property p1=	ResourceFactory.createProperty(rdf, "nil");
		Property p2=	ResourceFactory.createProperty(rdf, "first");
		Property p3=	ResourceFactory.createProperty(rdf, "rest");

		properties.add(p1);
		properties.add(p2);
		properties.add(p3);

		for(int j=0;j<properties.size();j++) {
			Iterator<Statement>iter = m.listStatements(null,properties.get(j),(RDFNode)null);
			while(iter.hasNext())
			{
				Statement st = iter.next();
				Resource sub =st.getSubject();
				RDFNode rdfNode=st.getObject();
				subjects.add(sub);
				rdfNodes.add(rdfNode);

			}

			for(int i=0;i<subjects.size();i++) {
				m.remove(subjects.get(i),properties.get(j) , rdfNodes.get(i));

			}
		}
		return m;

	}
	public static void main(String[] args) throws IOException {

		String inputtFile= "/home/abddatascienceadmin/deer/NUT_DATA/N_4_3out1234.ttl";

		Model model= Reader.readModel(inputtFile);

		Model newModel= removeMorePropertyProcess( model);

		String outputFile= "/home/abddatascienceadmin/deer/NUT_DATA/final_N_4_out1234.nt";

		Writer.writeModel(newModel, "NT", outputFile);


	}


}
