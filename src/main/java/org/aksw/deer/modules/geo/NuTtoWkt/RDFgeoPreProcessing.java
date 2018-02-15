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

import com.vividsolutions.jts.io.ParseException;



public class RDFgeoPreProcessing {

	private static String ngeo = "http://geovocab.org/geometry#";
	//private static String geo="http://www.w3.org/2003/01/geo/wgs84_pos#";

	public static ArrayList<String>allStringLat1=new ArrayList<String>();
	public static ArrayList<String>allStringLong1=new ArrayList<String>();

	static String string1;
	static Resource subject;

	public static Model processModel(Model m) throws FileNotFoundException, ParseException
	{
		Property createProperty = ResourceFactory.createProperty(ngeo, "posList");
		StmtIterator iter = m.listStatements(null,createProperty,(RDFNode)null);

		while(iter.hasNext())

		{	Statement stmt = iter.nextStatement();
		subject=stmt.getSubject();

		//System.out.println("the subjects are "+subject);
		List<Resource> out3 = explodeAnonymousResource(stmt.getObject().asResource());
		for(int i=0; i<out3.size();i++)
		{
			Iterator<Statement> it1 = m.listStatements(out3.get(i), null, (RDFNode)null);

			while(it1.hasNext())
			{
				Statement pos = it1.next();


				if(pos.getPredicate().toString().contains("lat"))

				{  
					//System.out.println("the object is "+pos.getObject().toString());
					allStringLat1.add(pos.getObject().toString());

				}


				else
				{
					allStringLong1.add(pos.getObject().toString());

				}

			}

		}

		string1=toWKT(allStringLong1,allStringLat1);
		//String str=GeomSimplification.geomMean(string1);
		String str1=GeomSimplification.geomSimpilfication(string1, 1.9447);
		allStringLat1=new ArrayList<String>();
		allStringLong1=new ArrayList<String>();
		System.out.println("string is "+string1);
		m.add(subject,ResourceFactory.createProperty(ngeo, "toWKT"),ResourceFactory.createStringLiteral(str1));
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


	public static void main(String[] args) throws IOException, ParseException {

		String fileName = "/home/abddatascienceadmin/deer/NUT_DATA/nutsIn-91.ttl";
		Model model= Reader.readModel(fileName);
		Model newModel=processModel( model);

		String outputFile= "/home/abddatascienceadmin/deer/NUT_DATA/N_4_1out1234.ttl";
		Writer.writeModel(newModel, "TTL", outputFile);
	}
}
