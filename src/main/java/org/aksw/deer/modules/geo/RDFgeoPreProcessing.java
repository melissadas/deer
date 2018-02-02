package org.aksw.deer.modules.geo;

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
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;



public class RDFgeoPreProcessing {

	static String geo  = "http://www.w3.org/2003/01/geo/wgs84_pos#";
	static String rdf  = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	static String nuts = "http://nuts.geovocab.org/id/";
	static String ngeo = "http://geovocab.org/geometry#";
	static String owl  = "http://www.w3.org/2002/07/owl#";
	static String dc   = "http://purl.org/dc/elements/1.1/" ;

	static String stringLat1;
	static ArrayList<String>allStringLat1=new ArrayList<String>();

	static String stringLat2;
	static ArrayList<String>allStringLat2=new ArrayList<String>();

	static String stringLat3;
	static ArrayList<String>allStringLat3=new ArrayList<String>();

	static String stringLat4;
	static ArrayList<String>allStringLat4=new ArrayList<String>();

	static String stringLong1;
	static ArrayList<String>allStringLong1=new ArrayList<String>();

	static String stringLong2;
	static ArrayList<String>allStringLong2=new ArrayList<String>();

	static String stringLong3;
	static ArrayList<String>allStringLong3=new ArrayList<String>();

	static String stringLong4;
	static ArrayList<String>allStringLong4=new ArrayList<String>();


	public static Model processModel(Model m)
	{
		List<List<String>> list = new ArrayList<List<String>>();

		Iterator<Statement> iter = m.listStatements();
		while(iter.hasNext())
		{
			Statement st = iter.next();
			if(st.getPredicate().toString().contains("polygonMember"))
			{
				//System.out.println("Hello "+st.getObject()+" I am polygon member ring with values");
				List<Resource> out = explodeAnonymousResource(st.getObject().asResource());
				Iterator<Statement> polymemiter = m.listStatements(out.get(0)
						,ResourceFactory.createProperty(ngeo, "exterior"),(RDFNode)null);
				while(polymemiter.hasNext())
				{
					//System.out.println();
					List<String> list1 = new ArrayList<>();
					List<Resource> out2 = explodeAnonymousResource(polymemiter.next().getObject().asResource());
					System.out.println(" resources "+out2.get(0));
					System.out.println(" the size of out 2= "+ out2.size());
					Iterator<Statement> polymemiter1 = m.listStatements(out2.get(0)
							,ResourceFactory.createProperty(ngeo, "posList"),(RDFNode)null);


					while(polymemiter1.hasNext())
					{

						List<Resource> out3 = explodeAnonymousResource(polymemiter1.next().getObject().asResource());

						System.out.println(" the size of out 3 = "+ out3.size());
						for(int i=0; i<out3.size();i++)
						{
							Iterator<Statement> it1 = m.listStatements(out3.get(i), null, (RDFNode)null);

							while(it1.hasNext())
							{
								Statement pos = it1.next();

								//System.out.println(pos);

								if(pos.getPredicate().toString().contains("lat"))

								{  if(out3.size()==9) {
									stringLat1=pos.getObject().toString();
									allStringLat1.add(stringLat1);
									//System.out.println("Latitude1 "+allStringLat1);
								}


								if(out3.size()==24) {
									stringLat2=pos.getObject().toString();
									allStringLat2.add(stringLat2);
									//	System.out.println("Latitude2 "+allStringLat2);
								}

								if(out3.size()==48) {
									stringLat3=pos.getObject().toString();
									allStringLat3.add(stringLat3);
									//System.out.println("Latitude3 "+allStringLat3);
								}

								if(out3.size()==375) {
									stringLat4=pos.getObject().toString();
									allStringLat4.add(stringLat4);
									//System.out.println("Latitude4 "+allStringLat4);
								}

								}
								else
								{

									if(out3.size()==9) {
										stringLong1=pos.getObject().toString();
										allStringLong1.add(stringLong1);
										//System.out.println("Longititude1 "+allStringLong1);
									}


									if(out3.size()==24) {
										stringLong2=pos.getObject().toString();
										allStringLong2.add(stringLong2);
										//System.out.println("Longititude2 "+allStringLong2);
									}

									if(out3.size()==48) {
										stringLong3=pos.getObject().toString();
										allStringLong3.add(stringLong3);
										//System.out.println("Longititude3 "+allStringLong3);
									}

									if(out3.size()==375) {
										stringLong4=pos.getObject().toString();
										allStringLong4.add(stringLong4);
										//System.out.println("Longititude4 "+allStringLong4);
									}
								}

							}

						}						
					}
				}

			}

		}

		String string1=toWKT(allStringLat1, allStringLong1);
		String string2=toWKT(allStringLat2, allStringLong2);
		String string3=toWKT(allStringLat3, allStringLong3);
		String string4=toWKT(allStringLat4, allStringLong4);


		/*		Model model= ModelFactory.createDefaultModel();
		model.add(m);
		Property p1 = ResourceFactory.createProperty(ngeo,"PosList");
		Property p2 = ResourceFactory.createProperty(geo,"long");
		Property p3 = ResourceFactory.createProperty(geo,"lat");
		model.remove(null, p1, (RDFNode)null);
		model.remove(null, p2, (RDFNode)null);
		model.remove(null, p3, (RDFNode)null);
		 */

		/*		Node node1= NodeFactory.createBlankNode(string1);
		Node node2= NodeFactory.createBlankNode(string2);
		Node node3= NodeFactory.createBlankNode(string3);
		Node node4= NodeFactory.createBlankNode(string4);*/

		System.out.println("string one = "+string1);
		System.out.println("string one = "+string2);
		System.out.println("string one = "+string3);
		System.out.println("string one = "+string4);


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
		string="POLYGON"+string.substring(string.indexOf("("),string.indexOf(")")-2)+")";

		return string;


	}


	public static void main(String[] args) throws IOException {

		Model model= Reader.readModel("/home/abddatascienceadmin/deer/NUT_DATA/DE_geometry.ttl");
		Model outputModel=processModel(model);

		/*		String ngeo = "http://geovocab.org/geometry#";
		Resource ngeoMultiPolygon = ResourceFactory.createResource(ngeo + "MultiPolygon");
		model.listStatements(null, RDF.type, ngeoMultiPolygon);*/

		//RDFgeoPreProcessing rDFgeoPreProcessing= new RDFgeoPreProcessing();
		//	Resource mainSubject;

		//Model outputModel=rDFgeoPreProcessing.preProcessorModel(model);

		String outputFile= "/home/abddatascienceadmin/deer/NUT_DATA/DE_geometry_out.ttl";
		Writer.writeModel(outputModel, "TTL", outputFile);


	}

}
