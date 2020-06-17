package compilateur;

/*   (C) Copyright 2013, Schmidt Nicolas
 * 
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import compilateur.heuristique_contraintes.HeuristiqueContraintes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
 
public class LecteurXML {
	
	public ArrayList<Integer> reorga;
	@SuppressWarnings("unused")
	private boolean bif=false;
	
	class Domain {public int nbValues; public String name; public ArrayList<String> elem;}
	public int nbDomains;
	public Domain[] dom;
	
	public int nbVariables;
	
	public class Relation { public String name; public int arity; public int nbTuples; public Structure defaultCost; public boolean softConstraint; public boolean conflictsConstraint; public int[][] relation; public String[][] relationS; public Structure[] poid;}
	public int nbRelations;
	public ArrayList<Relation> rel;
	
	public class ConstraintXML { public String name; public int arity; public String scope; public String reference; public int[] scopeID; public Relation relation;}
	public int nbConstraints;
	//public int maximalCost;
	public ConstraintXML[] cons;
	
	public ConstraintsNetwork cn;
	
	public LecteurXML(){
		nbDomains=0;
		nbVariables=0;
		nbRelations=0;
		nbConstraints=0;
	}

	//retourne le domain s, -1 si echec
	public Domain getdomain(String s){
		for(int i=0; i<nbDomains; i++){
			if(dom[i].name.compareTo(s)==0)
				return dom[i];
		}
		
		return null;
	}
	
	//lecture d'une relation s (dans la class r)
	public void interpretationRelation(String s, Relation r){
		
		String phrase="";
		char curr;
		int currWeight=0;
		
		int arite=0;
		int tuple=0;
		
		for(int j=0; j<s.length(); j++){
			curr=s.charAt(j);
			switch (curr){
	  		case ' ' : 		if(phrase.length()>0){
	  							r.relationS[tuple][arite]=phrase;
	  							arite++;
	  						}
	  						phrase="";
	  			break;
	  		case '\n' : 	if(phrase.length()>0){
	  							r.relationS[tuple][arite]=phrase;
	  							arite++;
	  						}
	  						phrase="";
	  			break;
	  		case '|' : 	if(phrase.length()>0){
			   				r.relationS[tuple][arite]=phrase;
	  					}
			   			phrase="";
			   			r.poid[tuple]=new Sp(currWeight);
			   			arite=0; 
			   			tuple++;
			   			
			   	break;
	  		case ':' :  if(phrase.charAt(0)!='-'){
	  						currWeight=Integer.parseInt(phrase);
	  					}else{
	  						currWeight=Integer.parseInt(phrase.substring(1));
	  						currWeight*=-1;
	  					}
   						phrase="";
	  			break;
	  		default:
	  				phrase+=curr;
	  		}
		}
		
		//fin (car pas de | final
		if(phrase.length()>0){
			r.relationS[tuple][arite]=phrase;
		}
		r.poid[tuple]=new Sp(currWeight);
	}
 
	//lecture d'un domaine (dans la class r)
	public void lectureDomaine(String s, Domain d){
		if(!s.contains("..")){
			String phrase="";
			char curr;
			
			for(int j=0; j<s.length(); j++){
				curr=s.charAt(j);
				if(curr==' ' || curr=='\n'){
					if(phrase.length()>0)
						d.elem.add(phrase);
		  			phrase="";
				}
		  		else	
		  			phrase+=curr;
			
			}
			//au cas ou on fini pas par un espace
			if(phrase.length()!=0)
				d.elem.add(phrase);
			
			if(d.elem.size()!=d.nbValues){
				System.out.println("erreur de taille de domaine contradictoires  sizeElem="+d.elem.size()+"  nbval="+d.nbValues);
				System.out.println(d.name);
			}
		}else{
			int id;
			int id2, id3;
			int s1, s2;
			
			while((s.startsWith(" ")) || (s.startsWith("\n"))){
				s=s.substring(1);
			}
			System.out.println(s);
			
			String substring;
			
			id=s.indexOf('.');
			substring=s.substring(id);
			id2=substring.indexOf(' ');
			id3=substring.indexOf('\n');

			s1=Integer.parseInt(s.substring(0, id));
			if(id2==-1)
				id2=99999;
			if(id3==-1)
				id3=99999;
			if(id3<id2)
				id2=id3;
			if(s.length()<id2)
				id2=s.length();
			s2=Integer.parseInt(s.substring(id+2, id2+id));
			
			
			if((s2-s1)+1!=d.nbValues){
				System.out.println("erreur de taille de domaine contradictoires  sizeElem="+d.elem.size()+"  nbval="+d.nbValues);
				System.out.println(d.name);
			}
			
			for(int i=s1; i<=s2; i++){
				d.elem.add(String.valueOf(i));
			}
			
		}
				
	}
	
	public void lecture(String nomFichier, ConstraintsNetwork cn) {
		
		NodeList nList;
		try {
		File fXmlFile = new File("./"+nomFichier);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();


		
		//////domains///////
		nList = doc.getElementsByTagName("domains");
		for (int temp = 0; temp < nList.getLength(); temp++) {	
			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {			
				Element eElement = (Element) nNode;
				
				nbDomains=Integer.parseInt(eElement.getAttribute("nbDomains"));
			}
		}
		
		dom=new Domain[nbDomains];
		
		//////domain//////
		nList = doc.getElementsByTagName("domain");
		for (int temp = 0; temp < nbDomains; temp++) {
			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;

				dom[temp]=new Domain();
				dom[temp].elem = new ArrayList<String>();
				
				dom[temp].name=eElement.getAttribute("name");
				dom[temp].nbValues=Integer.parseInt(eElement.getAttribute("nbValues"));
				
				String s=nNode.getTextContent();
				lectureDomaine(s, dom[temp]);
				
			}
		}
		
			//////variables///////
			nList = doc.getElementsByTagName("variables");
			for (int temp = 0; temp < nList.getLength(); temp++) {	
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {			
					Element eElement = (Element) nNode;
					
					nbVariables=Integer.parseInt(eElement.getAttribute("nbVariables"));
				}
			}
			
			ArrayList<Var> var=new ArrayList<Var>();
			//////variable//////
			nList = doc.getElementsByTagName("variable");
			for (int temp = 0; temp < nbVariables; temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;

					Var v=new Var(eElement.getAttribute("name"), temp+1);			
					var.add(v);
					
					Domain d=this.getdomain(eElement.getAttribute("domain"));
					if(d!=null) 											// on cherche le domain, et si il existe...
						var.get(temp).ajout(d.elem);
					else
						System.out.println(var.get(temp).name + " : domain inexistant!" );
					
				}
			}
			cn.setVar(var);
			
			//////Relations///////
			nList = doc.getElementsByTagName("relations");
			for (int temp = 0; temp < nList.getLength(); temp++) {	
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {			
					Element eElement = (Element) nNode;
					
					nbRelations=Integer.parseInt(eElement.getAttribute("nbRelations"));
				}
			}
			
			rel=new ArrayList<LecteurXML.Relation>(nbRelations);
			//////relation//////
			nList = doc.getElementsByTagName("relation");
			for (int temp = 0; temp < nbRelations; temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;


					rel.add(new Relation());
					
					rel.get(temp).name=eElement.getAttribute("name");
					rel.get(temp).arity=Integer.parseInt(eElement.getAttribute("arity"));
					rel.get(temp).nbTuples=Integer.parseInt(eElement.getAttribute("nbTuples"));
					rel.get(temp).softConstraint=(eElement.getAttribute("semantics").compareTo("soft")==0);
					rel.get(temp).conflictsConstraint=(eElement.getAttribute("semantics").compareTo("conflicts")==0);
					if(rel.get(temp).softConstraint)
						rel.get(temp).defaultCost= new Sp(Integer.parseInt(eElement.getAttribute("defaultCost")));
					
					if(Integer.parseInt(eElement.getAttribute("nbTuples"))!=0)
					{										//evite les erreurs lorsque nombre de tuples = 0
						rel.get(temp).relation=new int[rel.get(temp).nbTuples][rel.get(temp).arity];
						rel.get(temp).relationS=new String[rel.get(temp).nbTuples][rel.get(temp).arity];
						rel.get(temp).poid=new Sp[rel.get(temp).nbTuples];
						String r=nNode.getTextContent();
						interpretationRelation(r, rel.get(temp));
					}
				}
			}
			
			//////Constraints///////
			nList = doc.getElementsByTagName("constraints");
			for (int temp = 0; temp < nList.getLength(); temp++) {	
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {			
					Element eElement = (Element) nNode;
					
					nbConstraints=Integer.parseInt(eElement.getAttribute("nbConstraints"));
					//maximalCost=Integer.parseInt(eElement.getAttribute("maximalCost"));
				}
			}
			
			cons=new ConstraintXML[nbConstraints];
			//////Constraint//////
			nList = doc.getElementsByTagName("constraint");
			for (int temp = 0; temp < nbConstraints; temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;

					Constraint c = new Constraint();
					
					c.name=eElement.getAttribute("name");
					c.arity=Integer.parseInt(eElement.getAttribute("arity"));
					
					//obtenir la liste des variables impliques
					String scope = eElement.getAttribute("scope");
					String subStrings[] = scope.split(" ");
					for(int i=0; i<subStrings.length; i++){
						if(subStrings[i].length()!=0){
							for (int j=0; j<cn.nbVariables; j++){
								if(cn.getVar(j).name.compareTo(subStrings[i])==0){
									c.scopeID.add(j);
									break;
								}
							}
						}
					}
					
					//obtenir relation
					String ref= eElement.getAttribute("reference");	
					Relation currentRel=null;
					ref=ref.trim();
					for (int i=0; i<rel.size(); i++){
						if(rel.get(i).name.compareTo(ref)==0){
							currentRel=rel.get(i);						// on trouve la relation, c'est la numero j
							break;
						}
					}
					c.nbTuples=currentRel.nbTuples;
					for(int i=0; i<currentRel.nbTuples; i++) {
						ArrayList<Integer> newTuple = new ArrayList<Integer>();
						for(int j=0; j<currentRel.arity; j++) 
							newTuple.add(cn.getVar(c.scopeID.get(j)).conv(currentRel.relationS[i][j]));
						c.cons.add(newTuple);
					}
					c.defaultCost=currentRel.defaultCost; 
					c.conflictsConstraint=currentRel.conflictsConstraint;
					c.softConstraint=currentRel.softConstraint;
					for(int i=0; i<currentRel.poid.length; i++) {
						c.poid.add(currentRel.poid[i].copie());
					}
					
					cn.addCons(c);				
				}
			}
	
		
	  } catch (Exception e) {
		e.printStackTrace();
	  }
	}
	
	//lecture en metant les contraintes au bout
public void lectureSuite(String nomFichier, ConstraintsNetwork cn) {
		
		NodeList nList;
		int nbRelations2=0;
		int nbConstraints2=0;
		ArrayList<Relation> rel2;
		
		try {
		File fXmlFile = new File("./"+nomFichier);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();



			
			//////Relations///////
			nList = doc.getElementsByTagName("relations");
			for (int temp = 0; temp < nList.getLength(); temp++) {	
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {			
					Element eElement = (Element) nNode;
					
					nbRelations2=Integer.parseInt(eElement.getAttribute("nbRelations"));
				}
			}
			
			rel2=new ArrayList<LecteurXML.Relation>(nbRelations2);
			//////relation//////
			nList = doc.getElementsByTagName("relation");
			for (int temp = 0; temp < nbRelations2; temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;


					rel2.add(new Relation());
					
					rel2.get(temp).name=eElement.getAttribute("name");
					rel2.get(temp).arity=Integer.parseInt(eElement.getAttribute("arity"));
					rel2.get(temp).nbTuples=Integer.parseInt(eElement.getAttribute("nbTuples"));
					rel2.get(temp).softConstraint=(eElement.getAttribute("semantics").compareTo("soft")==0);
					rel2.get(temp).conflictsConstraint=(eElement.getAttribute("semantics").compareTo("conflicts")==0);
					if(rel2.get(temp).softConstraint)
						rel2.get(temp).defaultCost= new Sp(Integer.parseInt(eElement.getAttribute("defaultCost")));
					
					if(Integer.parseInt(eElement.getAttribute("nbTuples"))!=0)
					{										//evite les erreurs lorsque nombre de tuples = 0
						rel2.get(temp).relation=new int[rel2.get(temp).nbTuples][rel2.get(temp).arity];
						rel2.get(temp).relationS=new String[rel2.get(temp).nbTuples][rel2.get(temp).arity];
						rel2.get(temp).poid=new Sp[rel2.get(temp).nbTuples];
						String r=nNode.getTextContent();
						interpretationRelation(r, rel2.get(temp));
					}
				}
			}
			
			//////Constraints///////
			nList = doc.getElementsByTagName("constraints");
			for (int temp = 0; temp < nList.getLength(); temp++) {	
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {			
					Element eElement = (Element) nNode;
					
					nbConstraints2=Integer.parseInt(eElement.getAttribute("nbConstraints"));
					//maximalCost=Integer.parseInt(eElement.getAttribute("maximalCost"));
				}
			}

			//////Constraint//////
			nList = doc.getElementsByTagName("constraint");
			for (int temp = 0; temp < nbConstraints; temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;

					Constraint c = new Constraint();
					
					c.name=eElement.getAttribute("name");
					c.arity=Integer.parseInt(eElement.getAttribute("arity"));
					
					//obtenir la liste des variables impliques
					String scope = eElement.getAttribute("scope");
					String subStrings[] = scope.split(" ");
					for(int i=0; i<subStrings.length; i++){
						if(subStrings[i].length()!=0){
							for (int j=0; j<cn.nbVariables; j++){
								if(cn.getVar(j).name.compareTo(subStrings[i])==0){
									c.scopeID.add(j);
									break;
								}
							}
						}
					}
					
					//obtenir relation
					String ref= eElement.getAttribute("reference");	
					Relation currentRel=null;
					ref=ref.trim();
					for (int i=0; i<rel2.size(); i++){
						if(rel2.get(i).name.compareTo(ref)==0){
							currentRel=rel2.get(i);						// on trouve la relation, c'est la numero j
							break;
						}
					}
					c.nbTuples=currentRel.nbTuples;
					for(int i=0; i<currentRel.nbTuples; i++) {
						ArrayList<Integer> newTuple = new ArrayList<Integer>();
						for(int j=0; j<currentRel.arity; j++) 
							newTuple.add(cn.getVar(j).conv(currentRel.relation[i][j]));
						c.cons.add(newTuple);
					}
					c.defaultCost=currentRel.defaultCost; 
					c.conflictsConstraint=currentRel.conflictsConstraint;
					c.softConstraint=currentRel.softConstraint;
					for(int i=0; i<currentRel.poid.length; i++) {
						c.poid.add(currentRel.poid[i].copie());
					}
					
					cn.addCons(c);				
				}
			}
		
	  } catch (Exception e) {
		e.printStackTrace();
	  }
		
		
		
	}
	

//poid fort - for, given n, ..., given 2, given 1 -- poid faible 
public void lectureBIFfaux(String nomFichier, boolean arg_plus, ConstraintsNetwork cn) {
		
	bif=true;
	
		NodeList nList;
		try {
		File fXmlFile = new File("./"+nomFichier);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
	
			
			//////variable//////
		ArrayList<Var> var=new ArrayList<Var>();
			nList = doc.getElementsByTagName("VARIABLE");
			nbVariables=nList.getLength();							//nombre de variables
			
			//on parcourt les varialbes
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					//dans une variable
						
					//on parcour le name
					NodeList nList2 = eElement.getElementsByTagName("NAME");
					String stringName="";
				    stringName = nList2.item(0).getTextContent();
				    Var v=new Var(stringName, temp+1);
					var.add(v);
					
					//on parcourt les Values
					nList2 = eElement.getElementsByTagName("VALUE");
					ArrayList<String> values = new ArrayList<String>();
				    for (int i = 0; i < nList2.getLength(); ++i)
				        values.add(nList2.item(i).getTextContent().trim());
				    var.get(temp).ajout(values);
					
				}
			}
			cn.setVar(var);

			//////Relations//////
			nList = doc.getElementsByTagName("PROBABILITY");
			nbRelations=nList.getLength();							//nombre de variables
			nbConstraints=nList.getLength();
			rel=new ArrayList<LecteurXML.Relation>(nbRelations);
			cons=new ConstraintXML[nbConstraints];		
			//on parcourt les relations
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					//dans une relation
					
					//init
					rel.add(new Relation());
					cons[temp]=new ConstraintXML();
					cons[temp].relation=rel.get(temp);
					rel.get(temp).name="r"+temp;
					cons[temp].name="c"+temp;
					cons[temp].reference="r"+temp;
					if(arg_plus)
						rel.get(temp).defaultCost=new Sp(0);				//pas de cout par defaut (ici le 1 c'est le neutre... oui mais on l'additionne apres, alors 0)
					else
						rel.get(temp).defaultCost=new St(1);
					rel.get(temp).softConstraint=true;			//on a que du soft !
					rel.get(temp).conflictsConstraint=false;	
						
//partie qui change					
					//on parcour les givens
					NodeList nList2 = eElement.getElementsByTagName("GIVEN");
					rel.get(temp).arity=nList2.getLength()+1;			//l'arite c'est le nombre de given + le for
					cons[temp].arity=nList2.getLength()+1;
					
					String stringScope="";
				    for (int i = 0; i < nList2.getLength(); ++i)				//on met bout a bout les givens...
				        stringScope += nList2.item(i).getTextContent() + " ";
				    nList2 = eElement.getElementsByTagName("FOR");
				    stringScope += nList2.item(0).getTextContent();				//et le for
				    cons[temp].scope=stringScope;
//
					//on veut recuperer les ScopeID
					String string=cons[temp].scope+" ";
					String subString="";
					int k=0;
					cons[temp].scopeID=new int[cons[temp].arity];

					for(int i=0; i<string.length(); i++){
						if(string.charAt(i)!=' ')
							subString+=string.charAt(i);
						else{
							if(subString.length()!=0){
								for (int j=0; j<var.size(); j++){
									if(var.get(j).name.compareTo(subString)==0){
										cons[temp].scopeID[k]=j;
										k++;
										subString="";
										break;
									}
								}
							}
						}
					}
					///fin de la recherche du scopeID
					
					//on va faire notre table, mais pour ca on doit connaitre le domaine de chacun
					int[] scopeDom=new int[cons[temp].arity];
					int[] curr=new int[cons[temp].arity+1];		//+1 sinon on deborde plus bas
					rel.get(temp).nbTuples=1;					//init (element neutre)
					for(int i=0; i<cons[temp].arity; i++){
						scopeDom[i]=var.get(cons[temp].scopeID[i]).domain;
						curr[i]=0;
						rel.get(temp).nbTuples*=scopeDom[i];			//on multipli pour avoir le nombre de scope, vu que tout est defini
					}
					
					rel.get(temp).relation=new int[rel.get(temp).nbTuples][rel.get(temp).arity];
					for(int i=0; i<rel.get(temp).nbTuples; i++){
						for(int j=0; j<rel.get(temp).arity; j++){
							rel.get(temp).relation[i][j]=curr[j];
						}
						curr[0]++;					//on incrémente
						for(int j=0; j<rel.get(temp).arity; j++){		//on verifie si on a pas dépassé
							if(curr[j]>=scopeDom[j]){				//la on a dépassé
								curr[j]=0;
								curr[j+1]++;
							}else{
								break;							//pas la peine de tout se taper
							}
						}
					}
					
					
					//class Relation {   public int nbTuples; public int[][] relation; public int[] poid;}
					String stringTable;
					nList2 = eElement.getElementsByTagName("TABLE");
				    stringTable = nList2.item(0).getTextContent()+" ";					//table
				    
				    //on decoupe la table en int
					subString="";
					k=0;
					if(arg_plus)
						rel.get(temp).poid=new Sp[rel.get(temp).nbTuples];
					else
						rel.get(temp).poid=new St[rel.get(temp).nbTuples];

					for(int i=0; i<stringTable.length(); i++){
						if(stringTable.charAt(i)!=' ')
							subString+=stringTable.charAt(i);
						else{
							if(subString.length()!=0){
								if(arg_plus)
									rel.get(temp).poid[k]=new Sp((int) Math.round(-1000*Math.log(Double.parseDouble(subString))));				//-404 : on a besoin d'une fraction (que dans le mult)
								else
									rel.get(temp).poid[k]=new St(Double.parseDouble(subString));				//-404 : on a besoin d'une fraction (que dans le mult)
								k++;
								subString="";
							}
						}
					}
				}
			}
		
			
		
		
		
	  } catch (Exception e) {
		e.printStackTrace();
	  }
	}

	public HashMap<String, ArrayList<String>>[] lectureReseauBayesien(String path)
	{
		int parents = 0;
		int enfants = 1;
				
		@SuppressWarnings("unchecked")
		HashMap<String, ArrayList<String>>[] reseau = (HashMap<String, ArrayList<String>>[]) new HashMap[2];
		
		reseau[0] = new HashMap<String, ArrayList<String>>();
		reseau[1] = new HashMap<String, ArrayList<String>>();
		
		bif=true;
		
		NodeList nList;
		try {
			File fXmlFile = new File("./"+path);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
				
			nList = doc.getElementsByTagName("VARIABLE");
			
			//on parcourt les varialbes
			for (int temp = 0; temp < nList.getLength(); temp++) {
				String s = ((Element)nList.item(temp)).getElementsByTagName("NAME").item(0).getTextContent();
				System.out.println("Var: "+s);
				reseau[parents].put(s, new ArrayList<String>());
				reseau[enfants].put(s, new ArrayList<String>());
			}
	
			//////Relations//////
			nList = doc.getElementsByTagName("PROBABILITY");
			//on parcourt les relations
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
	
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					//dans une relation
					
					String fils = eElement.getElementsByTagName("FOR").item(0).getTextContent();

					NodeList nList2 = eElement.getElementsByTagName("GIVEN");
					
					System.out.println("Fils: "+fils);
				    for(int i = 0; i < nList2.getLength(); ++i)
				    {
				    	String parent = nList2.item(i).getTextContent();
				    	System.out.println("Parent: "+parent);
				    	reseau[parents].get(fils).add(parent);
				    	reseau[enfants].get(parent).add(fils);
				    }
				    

				}
			}		
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reseau;
	}

//poid fort - Given n, ...Given 2, given 1, for -- poid faible 
public void lectureBIFpifi(String nomFichier, boolean arg_plus) {
	
	bif=true;
		
		NodeList nList;
		try {
		File fXmlFile = new File("./"+nomFichier);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
	
			
			//////variable//////
			ArrayList<Var> var=new ArrayList<Var>();
			nList = doc.getElementsByTagName("VARIABLE");
			nbVariables=nList.getLength();							//nombre de variables
			
			//on parcourt les varialbes
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					//dans une variable
						
					//on parcour le name
					NodeList nList2 = eElement.getElementsByTagName("NAME");
					String stringName="";
				    stringName = nList2.item(0).getTextContent();
				    Var v=new Var(stringName, temp+1);
					var.add(v);
					
					//on parcourt les Values
					nList2 = eElement.getElementsByTagName("VALUE");
					ArrayList<String> values = new ArrayList<String>();
				    for (int i = 0; i < nList2.getLength(); ++i)
				        values.add(nList2.item(i).getTextContent().trim());
				    var.get(temp).ajout(values);
					
				}
			}

			//////Relations//////
			nList = doc.getElementsByTagName("PROBABILITY");
			nbRelations=nList.getLength();							//nombre de variables
			nbConstraints=nList.getLength();
			rel=new ArrayList<LecteurXML.Relation>(nbRelations);
			cons=new ConstraintXML[nbConstraints];		
			//on parcourt les relations
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					//dans une relation
					
					//init
					rel.add(new Relation());
					cons[temp]=new ConstraintXML();
					cons[temp].relation=rel.get(temp);
					rel.get(temp).name="r"+temp;
					cons[temp].name="c"+temp;
					cons[temp].reference="r"+temp;
					if(arg_plus)
						rel.get(temp).defaultCost=new Sp(0);				//pas de cout par defaut (ici le 1 c'est le neutre... oui mais on l'additionne apres, alors 0)
					else
						rel.get(temp).defaultCost=new St(1);
					rel.get(temp).softConstraint=true;			//on a que du soft !
					rel.get(temp).conflictsConstraint=false;	
						
					//on parcour les givens
//partie qui change					
					String stringScope="";
					NodeList nList2 = eElement.getElementsByTagName("FOR");
					    stringScope += nList2.item(0).getTextContent() + " ";				//le for
					    
					nList2 = eElement.getElementsByTagName("GIVEN");
					rel.get(temp).arity=nList2.getLength()+1;			//l'arite c'est le nombre de given + le for
					cons[temp].arity=nList2.getLength()+1;
					
				    for (int i = 0; i < nList2.getLength(); ++i)				//on met bout a bout les givens...
				        stringScope += nList2.item(i).getTextContent() + " ";
				    
				    cons[temp].scope=stringScope;
//
					//on veut recuperer les ScopeID
					String string=cons[temp].scope+" ";
					String subString="";
					int k=0;
					cons[temp].scopeID=new int[cons[temp].arity];

					for(int i=0; i<string.length(); i++){
						if(string.charAt(i)!=' ')
							subString+=string.charAt(i);
						else{
							if(subString.length()!=0){
								for (int j=0; j<var.size(); j++){
									if(var.get(j).name.compareTo(subString)==0){
										cons[temp].scopeID[k]=j;
										k++;
										subString="";
										break;
									}
								}
							}
						}
					}
					///fin de la recherche du scopeID
					
					//on va faire notre table, mais pour ca on doit connaitre le domaine de chacun
					int[] scopeDom=new int[cons[temp].arity];
					int[] curr=new int[cons[temp].arity+1];		//+1 sinon on deborde plus bas
					rel.get(temp).nbTuples=1;					//init (element neutre)
					for(int i=0; i<cons[temp].arity; i++){
						scopeDom[i]=var.get(cons[temp].scopeID[i]).domain;
						curr[i]=0;
						rel.get(temp).nbTuples*=scopeDom[i];			//on multipli pour avoir le nombre de scope, vu que tout est defini
					}
					
					rel.get(temp).relation=new int[rel.get(temp).nbTuples][rel.get(temp).arity];
					for(int i=0; i<rel.get(temp).nbTuples; i++){
						for(int j=0; j<rel.get(temp).arity; j++){
							rel.get(temp).relation[i][j]=curr[j];
						}
						curr[0]++;					//on incrémente
						for(int j=0; j<rel.get(temp).arity; j++){		//on verifie si on a pas dépassé
							if(curr[j]>=scopeDom[j]){				//la on a dépassé
								curr[j]=0;
								curr[j+1]++;
							}else{
								break;							//pas la peine de tout se taper
							}
						}
					}
					
					
					//class Relation {   public int nbTuples; public int[][] relation; public int[] poid;}
					String stringTable;
					nList2 = eElement.getElementsByTagName("TABLE");
				    stringTable = nList2.item(0).getTextContent()+" ";					//table
				    
				    //on decoupe la table en int
					subString="";
					k=0;
					if(arg_plus)
						rel.get(temp).poid=new Sp[rel.get(temp).nbTuples];
					else
						rel.get(temp).poid=new St[rel.get(temp).nbTuples];

					for(int i=0; i<stringTable.length(); i++){
						if(stringTable.charAt(i)!=' ')
							subString+=stringTable.charAt(i);
						else{
							if(subString.length()!=0){
								if(arg_plus)
									rel.get(temp).poid[k]=new Sp((int) Math.round(-1000*Math.log(Double.parseDouble(subString))));				//-404 : on a besoin d'une fraction (que dans le mult)
								else
									rel.get(temp).poid[k]=new St(Double.parseDouble(subString));				//-404 : on a besoin d'une fraction (que dans le mult)
								k++;
								subString="";
							}
						}
					}
				}
			}
		
			
		
		
		
	  } catch (Exception e) {
		e.printStackTrace();
	  }
	}
	
//poid fort - Given 1, Given 2, ..., given n, for -- poid faible 
public void lectureBIF(String nomFichier, boolean arg_plus) {
	
	bif=true;
	
		NodeList nList;
		try {
		File fXmlFile = new File("./"+nomFichier);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
	
			
			//////variable//////
			ArrayList<Var> var=new ArrayList<Var>();
			nList = doc.getElementsByTagName("VARIABLE");
			nbVariables=nList.getLength();							//nombre de variables
			
			//on parcourt les varialbes
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					//dans une variable
						
					//on parcour le name
					NodeList nList2 = eElement.getElementsByTagName("NAME");
					String stringName="";
				    stringName = nList2.item(0).getTextContent();
				    Var v=new Var(stringName, temp+1);
					var.add(v);
					
					//on parcourt les Values
					nList2 = eElement.getElementsByTagName("VALUE");
					ArrayList<String> values = new ArrayList<String>();
				    for (int i = 0; i < nList2.getLength(); ++i)
				        values.add(nList2.item(i).getTextContent().trim());
				    var.get(temp).ajout(values);
					
				}
			}

			//////Relations//////
			nList = doc.getElementsByTagName("PROBABILITY");
			nbRelations=nList.getLength();							//nombre de variables
			nbConstraints=nList.getLength();
			rel=new ArrayList<LecteurXML.Relation>(nbRelations);
			cons=new ConstraintXML[nbConstraints];		
			//on parcourt les relations
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					//dans une relation
					
					//init
					rel.add(new Relation());
					cons[temp]=new ConstraintXML();
					cons[temp].relation=rel.get(temp);
					rel.get(temp).name="r"+temp;
					cons[temp].name="c"+temp;
					cons[temp].reference="r"+temp;
					if(arg_plus)
						rel.get(temp).defaultCost=new Sp(0);				//pas de cout par defaut (ici le 1 c'est le neutre... oui mais on l'additionne apres, alors 0)
					else
						rel.get(temp).defaultCost=new St(1);
					rel.get(temp).softConstraint=true;			//on a que du soft !
					rel.get(temp).conflictsConstraint=false;	
						
//partie qui change					
					//on parcour les givens
					NodeList nList2 = eElement.getElementsByTagName("GIVEN");
					rel.get(temp).arity=nList2.getLength()+1;			//l'arite c'est le nombre de given + le for
					cons[temp].arity=nList2.getLength()+1;
					
					String stringScope="";
				    for (int i = 0; i < nList2.getLength(); ++i)				//on met bout a bout les givens...
				        stringScope = nList2.item(i).getTextContent() + " " + stringScope;
				    
				    nList2 = eElement.getElementsByTagName("FOR");
				    stringScope = nList2.item(0).getTextContent() + " " + stringScope;				//et le for
				    cons[temp].scope=stringScope;
//
				    
					//on veut recuperer les ScopeID
					String string=cons[temp].scope+" ";
					String subString="";
					int k=0;
					cons[temp].scopeID=new int[cons[temp].arity];

					for(int i=0; i<string.length(); i++){
						if(string.charAt(i)!=' ')
							subString+=string.charAt(i);
						else{
							if(subString.length()!=0){
								for (int j=0; j<var.size(); j++){
									if(var.get(j).name.compareTo(subString)==0){
										cons[temp].scopeID[k]=j;
										k++;
										subString="";
										break;
									}
								}
							}
						}
					}
					///fin de la recherche du scopeID
					
					//on va faire notre table, mais pour ca on doit connaitre le domaine de chacun
					int[] scopeDom=new int[cons[temp].arity];
					int[] curr=new int[cons[temp].arity+1];		//+1 sinon on deborde plus bas
					rel.get(temp).nbTuples=1;					//init (element neutre)
					for(int i=0; i<cons[temp].arity; i++){
						scopeDom[i]=var.get(cons[temp].scopeID[i]).domain;
						curr[i]=0;
						rel.get(temp).nbTuples*=scopeDom[i];			//on multipli pour avoir le nombre de scope, vu que tout est defini
					}
					
					rel.get(temp).relation=new int[rel.get(temp).nbTuples][rel.get(temp).arity];
					for(int i=0; i<rel.get(temp).nbTuples; i++){
						for(int j=0; j<rel.get(temp).arity; j++){
							rel.get(temp).relation[i][j]=curr[j];
						}
						curr[0]++;					//on incrémente
						for(int j=0; j<rel.get(temp).arity; j++){		//on verifie si on a pas dépassé
							if(curr[j]>=scopeDom[j]){				//la on a dépassé
								curr[j]=0;
								curr[j+1]++;
							}else{
								break;							//pas la peine de tout se taper
							}
						}
					}
					
					
					//class Relation {   public int nbTuples; public int[][] relation; public int[] poid;}
					String stringTable;
					nList2 = eElement.getElementsByTagName("TABLE");
				    stringTable = nList2.item(0).getTextContent()+" ";					//table
				    
				    //on decoupe la table en int
					subString="";
					k=0;
					if(arg_plus)
						rel.get(temp).poid=new Sp[rel.get(temp).nbTuples];
					else
						rel.get(temp).poid=new St[rel.get(temp).nbTuples];

					for(int i=0; i<stringTable.length(); i++){
						if(stringTable.charAt(i)!=' ')
							subString+=stringTable.charAt(i);
						else{
							if(subString.length()!=0){
								if(arg_plus)
									rel.get(temp).poid[k]=new Sp((int) Math.round(-1000*Math.log(Double.parseDouble(subString))));				//-404 : on a besoin d'une fraction (que dans le mult)
								else
									rel.get(temp).poid[k]=new St(Double.parseDouble(subString));				//-404 : on a besoin d'une fraction (que dans le mult)
								k++;
								subString="";
							}
						}
					}
				}
			}
	
	  } catch (Exception e) {
		e.printStackTrace();
	  }
	}


public void lectureCNF(String nomFichier, ConstraintsNetwork cn){
	
	FileReader fR;
	InputStream ips;
	InputStreamReader ipsr=null;
	BufferedReader br=null;

	String ligne;
	String[] args;

	if(!nomFichier.contains(".cnf"))
		nomFichier+=".cnf";

	try{
		fR = new FileReader(nomFichier);
	}catch (Exception e) {
		System.out.println("err lecture fichier cnf");
	}
	

	try{
		ips=new FileInputStream(nomFichier); 
		ipsr=new InputStreamReader(ips);
		br=new BufferedReader(ipsr);
	}catch (Exception e){
			System.out.println(e.toString());
	}

	boolean asStarted=false;
	ArrayList<Integer> contraiteBrute= new ArrayList<Integer>();

	int nbConstraintsfinals;
	try {
		while((ligne=br.readLine())!=null){
			
			if(ligne.length()>0 && ligne.charAt(0)=='c')
				break;						//comment
			
			if(ligne.charAt(0)=='p'){
				args=ligne.split(" ");		//begining
				if(args[1].compareToIgnoreCase("cnf")!=0)
					break;
				
				nbConstraintsfinals = Integer.parseInt(args[3]);
								
				cn.setVar(addAllVariablesAsBoolean(nbVariables));
				
				asStarted=true;
			}
			else if(asStarted){
				args=ligne.split(" ");
				for(String element : args) {
					int elemint = Integer.parseInt(element);
					if(elemint!=0) {
						contraiteBrute.add(elemint);
					}
					else {
						addContraintCNF(contraiteBrute);
						contraiteBrute.clear();
					}
				}
				
				
			}
			
		}
	} catch (NumberFormatException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	int i=0;
	

}


	public ArrayList<Var> addAllVariablesAsBoolean(int nbVar) {
		ArrayList<Var> var=new ArrayList<Var>();
		
		for(int i=1; i<=nbVar; i++) {
			Var v=new Var("v"+String.valueOf(i), i);
			v.setBoolean();
			var.add(v);
		}
		return var;
	}
	
	public void addContraintCNF(ArrayList<Integer> contraiteBrute){

		int id=nbConstraints;
		
		Constraint c=new Constraint("c"+id, contraiteBrute.size(), 1);
		
		c.softConstraint = false;
		c.conflictsConstraint = true;
		c.defaultCost=new Sp(0);
		ArrayList<Integer> tuple=new ArrayList<Integer>();
		for(int i=0; i<c.arity; i++){
			if(contraiteBrute.get(i)>0)
				tuple.add(1);
			else
				tuple.add(0);
		}
		c.poid.add(new Sp(0));
		
		for(int i=0; i<c.arity; i++){
			c.scopeID.add(Math.abs(contraiteBrute.get(i)));
		}
		
		nbConstraints++;
	}



	

	
	
	/*public void month(int deb, int fin) {
		int idMonth=-1;
		
		for(int i=0; i<cons[0].arity; i++){
			if(var.get(cons[0].scopeID[i]).name.compareTo("vmois")==0){
				idMonth=i;
				break;
			}
		}
		
		if(idMonth==-1){
			System.out.println("erreur LecteurXML.month() : pas de variable vmois");
		}
		
		int cptmonth=0;
		for(int i=0; i<cons[0].relation.nbTuples; i++){
			if(Integer.parseInt(cons[0].relation.relationS[i][idMonth])>=deb && Integer.parseInt(cons[0].relation.relationS[i][idMonth])<=fin)
				cptmonth++;
		}
		System.out.println("arite : " + cons[0].relation.nbTuples +" -> " + cptmonth);
		
		
		Relation newRel=new Relation();
		newRel.name=""+cons[0].relation.name;
		newRel.arity=cons[0].relation.arity-1;
		newRel.nbTuples=cptmonth;		//<<compter les tuples
		newRel.defaultCost=cons[0].relation.defaultCost;
		newRel.softConstraint=cons[0].relation.softConstraint;
		newRel.conflictsConstraint=cons[0].relation.conflictsConstraint;

		newRel.relation=new int[newRel.nbTuples][newRel.arity] ;
		newRel.relationS=new String[newRel.nbTuples][newRel.arity] ;
		newRel.poid=new Structure[newRel.nbTuples];
		
		int cpt=0;
		for(int i=0; i<cons[0].relation.nbTuples; i++){
			if(Integer.parseInt(cons[0].relation.relationS[i][idMonth])>=deb && Integer.parseInt(cons[0].relation.relationS[i][idMonth])<=fin){	//on la garde
				newRel.poid[cpt]=cons[0].relation.poid[i];
				for(int j=0; j<newRel.arity; j++){
					if(j<idMonth)
						newRel.relationS[cpt][j]=cons[0].relation.relationS[i][j];
					if(j>=idMonth)
						newRel.relationS[cpt][j]=cons[0].relation.relationS[i][j+1];
				}
				cpt++;
			}
		}
			
		cons[0].relation=newRel;
		cons[0].arity=cons[0].arity-1;
		int newScopeID[] = new int[newRel.arity];
		for(int j=0; j<newRel.arity; j++){
			if(j<idMonth)
				newScopeID[j]=cons[0].scopeID[j];
			if(j>=idMonth)
				newScopeID[j]=cons[0].scopeID[j+1];
			}
		cons[0].scopeID=newScopeID;
		int debutstring;
		debutstring=cons[0].scope.indexOf("vmois");
		if(debutstring>0)
			cons[0].scope=cons[0].scope.substring(0, debutstring).concat(cons[0].scope.substring(debutstring+7));
		else
			cons[0].scope=cons[0].scope.substring(6);
		
		//var.remove(0);
		nbVariables--;

	}*/
	

	
}