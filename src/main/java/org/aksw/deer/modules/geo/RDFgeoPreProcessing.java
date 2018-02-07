package org.aksw.deer.modules.geo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.aksw.deer.io.Reader;
import org.aksw.deer.io.Writer;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
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
	static ArrayList<String>alngeolStringLat3=new ArrayList<String>();

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

	static ArrayList<Resource> allSubjects=new ArrayList<Resource>();
	static Resource sub;


	//	public static ArrayList<String> processModel(Model m)
	//	{
	//
	//		Iterator<Statement> iter = m.listStatements();
	//		while(iter.hasNext())
	//		{
	//			Statement st = iter.next();
	//
	//			if(st.getPredicate().toString().contains("polygonMember"))
	//			{
	//				//System.out.println("Hello "+st.getObject()+" I am polygon member ring with values");
	//				List<Resource> out = explodeAnonymousResource(st.getObject().asResource());
	//				Iterator<Statement> polymemiter = m. ngeo: <http://geovocab.org/geometry#>listStatements(out.get(0)
	//						,ResourceFactory.createProperty(ngeo, "exterior"),(RDFNode)null);
	//				while(polymemiter.hasNext())
	//				{
	//
	//					List<Resource> out2 = explodeAnonymousResource(polymemiter.next().getObject().asResource());
	//
	//					//System.out.println(" resources "+out2.get(0));
	//					//System.out.println(" the size of out 2= "+ out2.size());
	//
	//					Iterator<Statement> polymemiter1 = m.listStatements(out2.get(0)
	//							,ResourceFactory.createProperty(ngeo, "posList"),(RDFNode)null);
	//
	//
	//					while(polymemiter1.hasNext())
	//					{
	//
	//						List<Resource> out3 = explodeAnonymousResource(polymemiter1.next().getObject().asResource());
	//
	//						//System.out.println(" the size of out 3 = "+ out3.size());
	//
	//						for(int i=0; i<out3.size();i++)
	//						{
	//							Iterator<Statement> it1 = m.listStatements(out3.get(i), null, (RDFNode)null);
	//
	//							while(it1.hasNext())
	//							{
	//								Statement pos = it1.next();
	//
	//
	//								if(pos.getPredicate().toString().contains("lat"))
	//
	//								{  if(out3.size()==9) {
	//									stringLat1=pos.getObject().toString();
	//									allStringLat1.add(stringLat1);
	//
	//								}
	//
	//
	//								if(out3.size()==24) {
	//									stringLat2=pos.getObject().toString();
	//									allStringLat2.add(stringLat2);
	//
	//								}
	//
	//								if(out3.size()==48) {
	//									stringLat3=pos.getObject().toString();
	//									allStringLat3.add(stringLat3);
	//
	//								}
	//
	//								if(out3.size()==375) {
	//									stringLat4=pos.getObject().toString();
	//									allStringLat4.add(stringLat4);
	//
	//								}
	//
	//								}
	//								else
	//								{
	//
	//									if(out3.size()==9) {
	//										stringLong1=pos.getObject().toString();
	//										allStringLong1.add(stringLong1);
	//
	//									}
	//
	//
	//									if(out3.size()==24) {
	//										stringLong2=pos.getObject().toString();
	//										allStringLong2.add(stringLong2);
	//
	//									}
	//
	//									if(out3.size()==48) {
	//										stringLong3=pos.getObject().toString();
	//										allStringLong3.add(stringLong3);
	//
	//									}
	//
	//									if(out3.size()==375) {
	//										stringLong4=pos.getObject().toString();
	//										allStringLong4.add(stringLong4);
	//
	//									}
	//								}
	//
	//							}
	//
	//						}						
	//					}
	//				}
	//
	//			}
	//
	//		}
	//
	//		String string1=toWKT(allStringLat1, allStringLong1);
	//		String string2=toWKT(allStringLat2, allStringLong2);
	//		String string3=toWKT(allStringLat3, allStringLong3);
	//		String string4=toWKT(allStringLat4, allStringLong4);
	//
	//		ArrayList<String>allStrings=new ArrayList<String>();
	//
	//		allStrings.add(string1);
	//		allStrings.add(string2);
	//		allStrings.add(string3);
	//		allStrings.add(string4);
	//
	//
	//		/*	System.out.println("string one = "+string1);
	//		System.out.println("string one = "+string2);
	//		System.out.println("string one = "+string3);
	//		System.out.println("string one = "+string4);*/
	//
	//		return allStrings;
	//
	//	}






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

	public static Model postProcessModel(Model m)
	{
		Model m1= ModelFactory.createDefaultModel();
		m1.add(m);

		ArrayList<String>strings=new ArrayList<String>();
		//		strings=processModel(m1);
		List<RDFNode>nodes=new ArrayList<RDFNode>();


		Property p2=	ResourceFactory.createProperty(ngeo, "toWKT");
		Property p3=	ResourceFactory.createProperty(ngeo, "posList");
		Resource resource= ResourceFactory.createResource(ngeo+"LinearRing");
		for(int i=0;i<strings.size();i++) {

			RDFNode node1= ResourceFactory.createStringLiteral(strings.get(i));

			nodes.add(node1);}

		Iterator<Statement> iter = m.listStatements(null,RDF.type,resource);

		while(iter.hasNext())
		{
			Statement st = iter.next();

			Resource sub =st.getSubject();
			allSubjects.add(sub);
		}
		m.add(allSubjects.get(0), p2, nodes.get(0));
		m.add(allSubjects.get(1), p2, nodes.get(1));
		m.add(allSubjects.get(2), p2, nodes.get(2));
		m.add(allSubjects.get(3), p2, nodes.get(3));


		return m;

	}




	public static void main(String[] args) throws IOException {

		Model model= Reader.readModel("/home/abddatascienceadmin/deer/NUT_DATA/DE_geometry_out3.ttl");
		ArrayList<String> allSubjects= new ArrayList<String>();
		ArrayList<RDFNode> allPM= new ArrayList<RDFNode>();
		ArrayList<RDFNode> allEX= new ArrayList<RDFNode>();
		ArrayList<String> allPL= new ArrayList<String>();
		Model resultModel = ModelFactory.createDefaultModel();
		String sparqlQueryString =
				"SELECT ?s ?pm ?ex ?pl"+" "
						+ "WHERE{"
						+ "?s <" + ngeo+"polygonMember> ?pm . " 
						+ "?pm <" + ngeo+"exterior> ?ex . " 
						+ "?ex <" + ngeo+"toWKT> ?pl . " 
						//+ "?pl <"+ geo+"lat> ?e ."
						+ "}" ;
		QueryFactory.create(sparqlQueryString);
		QueryExecution qexec = QueryExecutionFactory.create(sparqlQueryString, model);
		ResultSet queryResults = qexec.execSelect();
		while(queryResults.hasNext()){
			QuerySolution qs=queryResults.nextSolution();
			System.out.println(qs.get("s").toString());
			System.out.println(qs.get("ex").toString());
			System.out.println(qs.get("pl").toString());
			//System.out.println(qs.get("e"));
			allSubjects.add(qs.get("s").toString());
			allPM.add(qs.get("pm"));
			allEX.add(qs.get("ex"));
			allPL.add(qs.get("pl").toString());

		}
		System.out.println(" all subjects are "+allSubjects);

		Model model1=ModelFactory.createDefaultModel();


		ArrayList<Resource> rootResources=new ArrayList<Resource>();
		rootResources.add(ResourceFactory.createResource(allSubjects.get(0)));
		rootResources.add(ResourceFactory.createResource(allSubjects.get(1)));
		rootResources.add(ResourceFactory.createResource(allSubjects.get(2)));
		rootResources.add(ResourceFactory.createResource(allSubjects.get(3)));
		//
		//		ArrayList<Resource> pmResources=new ArrayList<Resource>();
		//		pmResources.add(ResourceFactory.createResource(allPM.get(0)));
		//		pmResources.add(ResourceFactory.createResource(allPM.get(1)));
		//		pmResources.add(ResourceFactory.createResource(allPM.get(2)));
		//		pmResources.add(ResourceFactory.createResource(allPM.get(3)));
		//
		//		ArrayList<Resource> exResources=new ArrayList<Resource>();
		//		exResources.add(ResourceFactory.createResource(allEX.get(0)));
		//		exResources.add(ResourceFactory.createResource(allEX.get(1)));
		//		exResources.add(ResourceFactory.createResource(allEX.get(2)));
		//		exResources.add(ResourceFactory.createResource(allEX.get(3)));


		model1.add(rootResources.get(0),ResourceFactory.createProperty(dc, "right"),
				ResourceFactory.createStringLiteral("© EuroGeographics for the administrative boundaries." ));

		model1.add(rootResources.get(0),RDF.type,ResourceFactory.createResource(ngeo+"MultiPolygon"));

		model1.add(rootResources.get(0),ResourceFactory.createProperty(ngeo, "polygonMember"),allPM.get(0));
		model1.add((Resource) allPM.get(0),RDF.type,ResourceFactory.createResource(ngeo+"polygon"));
		model1.add((Resource) allPM.get(0),ResourceFactory.createProperty(ngeo, "exterior"),	allEX.get(0));

		model1.add((Resource) allEX.get(0),ResourceFactory.createProperty(ngeo, "toWKT"),ResourceFactory.createStringLiteral(allPL.get(0)));


		model1.add(rootResources.get(1),RDF.type,ResourceFactory.createResource(ngeo+"MultiPolygon"));
		model1.add((Resource) allPM.get(1),RDF.type,ResourceFactory.createResource(ngeo+"polygon"));

		model1.add(rootResources.get(1),ResourceFactory.createProperty(ngeo, "polygonMember"),allPM.get(1));
		model1.add((Resource) allPM.get(1),ResourceFactory.createProperty(ngeo, "exterior"),	allEX.get(1));

		model1.add((Resource) allEX.get(1),ResourceFactory.createProperty(ngeo, "toWKT"),ResourceFactory.createStringLiteral(allPL.get(1)));

		model1.add(rootResources.get(2),RDF.type,ResourceFactory.createResource(ngeo+"MultiPolygon"));
		model1.add((Resource) allPM.get(2),RDF.type,ResourceFactory.createResource(ngeo+"polygon"));
		model1.add(rootResources.get(2),ResourceFactory.createProperty(ngeo, "polygonMember"),allPM.get(2));
		model1.add((Resource) allPM.get(2),ResourceFactory.createProperty(ngeo, "exterior"),	allEX.get(2));

		model1.add((Resource) allEX.get(2),ResourceFactory.createProperty(ngeo, "toWKT"),ResourceFactory.createStringLiteral(allPL.get(2)));

		model1.add(rootResources.get(3),RDF.type,ResourceFactory.createResource(ngeo+"MultiPolygon"));
		model1.add((Resource) allPM.get(3),RDF.type,ResourceFactory.createResource(ngeo+"polygon"));
		model1.add(rootResources.get(3),ResourceFactory.createProperty(ngeo, "polygonMember"),allPM.get(3));
		model1.add((Resource) allPM.get(3),ResourceFactory.createProperty(ngeo, "exterior"),	allEX.get(3));

		model1.add((Resource) allEX.get(3),ResourceFactory.createProperty(ngeo, "toWKT"),ResourceFactory.createStringLiteral(allPL.get(3)));





		/*
		Model newModel= postProcessModel( model);


		String outputFile= "/home/abdullah/deer/NUT_DATA/DE_geometry_out.ttl";
		Writer.writeModel(newModel, "TTL", outputFile);
		 */
		//	Model model1= Reader.readModel(outputFile);
		//	Model newModel1= removeProcessProperty(model1);
		//		 ngeo: <http://geovocab.org/geometry#>
		String outputFile1= "/home/abddatascienceadmin/deer/NUT_DATA/DE_geometry_out8.nt";
		Writer.writeModel(model1, "NT", outputFile1);


	}



}
