package org.aksw.deer.modules.geo.NuTtoWkt;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.aksw.deer.io.Reader;
import org.aksw.deer.io.Writer;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;



public class RDFgeoPreProcessing {

	private static String ngeo = "http://geovocab.org/geometry#";

	public static ArrayList<String>allStringLat1=new ArrayList<String>();
	public static ArrayList<String>allStringLong1=new ArrayList<String>();

	static String string1;
	static Resource subject;

	public static Model processModel(Model m) throws FileNotFoundException
	{
		StmtIterator iter = m.listStatements();
		while(iter.hasNext())
		{
			Statement st = iter.next();

			if(st.getSubject().hasProperty(RDF.type, ResourceFactory.createResource(ngeo+"Polygon")))
			{
				//System.out.println("Hello "+st.getObject()+" I am polygon member ring with values");
				List<Resource> out = explodeAnonymousResource(st.getSubject());
				StmtIterator polymemiter = m.listStatements(out.get(0)
						,ResourceFactory.createProperty(ngeo, "exterior"),(RDFNode)null);
				while(polymemiter.hasNext())
				{

					List<Resource> out2 = explodeAnonymousResource(polymemiter.next().getObject().asResource());
					StmtIterator polymemiter1 = m.listStatements(out2.get(0)
							,ResourceFactory.createProperty(ngeo, "posList"),(RDFNode)null);
					//System.out.println("polymemiter1 is "+ polymemiter1.toString());

					while(polymemiter1.hasNext())
					{ 
						Statement stm = polymemiter1.next();
						subject= stm.getSubject();
						List<Resource> out3 = explodeAnonymousResource(stm.getObject().asResource());

						for(int i=0; i<out3.size();i++)

						{ //System.out.println("i\n "+ i);
							Iterator<Statement> it1 = m.listStatements(out3.get(i), null, (RDFNode)null);

							while(it1.hasNext())
							{
								Statement pos = it1.next();

								if(pos.getPredicate().toString().contains("lat"))

								{  
									allStringLat1.add(pos.getObject().toString());
								}

								else
								{
									allStringLong1.add(pos.getObject().toString());

								}
							}

						}


						string1=toWKT(allStringLong1,allStringLat1);
						allStringLat1=new ArrayList<String>();
						allStringLong1=new ArrayList<String>();

					}

				}

				System.out.println("string is "+string1);
				m.add(subject,ResourceFactory.createProperty(ngeo, "toWKT"),ResourceFactory.createStringLiteral(string1));

			}

		}

		System.out.println(" it works");
		return m;

	}

	private static List<Resource> explodeAnonymousResource(Resource resource)
	{
		List<Property> collectionProperties = new LinkedList<Property>(Arrays.asList(OWL.unionOf,OWL.intersectionOf,RDF.first,RDF.rest));

		List<Resource> resources=new LinkedList<Resource>();
		Boolean needToTraverseNext=false;

		if(resource.isAnon())
		{
			for(Property cp:collectionProperties)
			{
				if(resource.hasProperty(cp) && !resource.getPropertyResourceValue(cp).equals(RDF.nil))
				{
					Resource nextResource=resource.getPropertyResourceValue(cp);
					resources.addAll(explodeAnonymousResource(nextResource));

					needToTraverseNext=true;
				}
			}

			if(!needToTraverseNext)
			{
				resources.add(resource);
			}
		}
		else
		{
			resources.add(resource);
		}

		return resources;
	}


	private static String toWKT(ArrayList<String> lat, ArrayList<String>lon) {
		String string ="";

		for(int i=0;i<lat.size();i++) {

			string+=lat.get(i)+" "+lon.get(i)+", ";
		}
		string="("+ string+")";
		string="POLYGON "+"("+string.substring(string.indexOf("("),string.indexOf(")")-2)+")"+")";

		return string;

	}


	public static void main(String[] args) throws IOException {

		String fileName = "/home/abddatascienceadmin/deer/NUT_DATA/nutsIn-91.ttl";
		Model model= Reader.readModel(fileName);
		Model newModel=processModel( model);

		String outputFile= "/home/abddatascienceadmin/deer/NUT_DATA/out1234.ttl";
		Writer.writeModel(newModel, "TTL", outputFile);
	}
}
