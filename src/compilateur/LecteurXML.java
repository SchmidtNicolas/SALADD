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
	
	//class Variable { public Domain domain; public String name;}
	public int nbVariables;
	public ArrayList<Var> var;
	
	public class Relation { public String name; public int arity; public int nbTuples; public Structure defaultCost; public boolean softConstraint; public boolean conflictsConstraint; public int[][] relation; public String[][] relationS; public Structure[] poid;}
	public int nbRelations;
	public Relation[] rel;
	
	public class Constraint { public String name; public int arity; public String scope; public String reference; public int[] scopeID; public Relation relation;}
	public int nbConstraints;
	//public int maximalCost;
	public Constraint[] cons;
	
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
	
	public void lecture(String nomFichier) {
		
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
			
			var=new ArrayList<Var>();
			//////variable//////
			nList = doc.getElementsByTagName("variable");
			for (int temp = 0; temp < nbVariables; temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;

					Var v=new Var(eElement.getAttribute("name"), temp+1);			//+1??????
					var.add(v);
					
					Domain d=this.getdomain(eElement.getAttribute("domain"));
					if(d!=null) 											// on cherche le domain, et si il existe...
						var.get(temp).ajout(d.elem);
					else
						System.out.println(var.get(temp).name + " : domain inexistant!" );
					
				}
			}
			
			//////Relations///////
			nList = doc.getElementsByTagName("relations");
			for (int temp = 0; temp < nList.getLength(); temp++) {	
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {			
					Element eElement = (Element) nNode;
					
					nbRelations=Integer.parseInt(eElement.getAttribute("nbRelations"));
				}
			}
			
			rel=new Relation[nbRelations];
			//////relation//////
			nList = doc.getElementsByTagName("relation");
			for (int temp = 0; temp < nbRelations; temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;


					rel[temp]=new Relation();
					
					rel[temp].name=eElement.getAttribute("name");
					rel[temp].arity=Integer.parseInt(eElement.getAttribute("arity"));
					rel[temp].nbTuples=Integer.parseInt(eElement.getAttribute("nbTuples"));
					rel[temp].softConstraint=(eElement.getAttribute("semantics").compareTo("soft")==0);
					rel[temp].conflictsConstraint=(eElement.getAttribute("semantics").compareTo("conflicts")==0);
					if(rel[temp].softConstraint)
						rel[temp].defaultCost= new Sp(Integer.parseInt(eElement.getAttribute("defaultCost")));
					
					if(Integer.parseInt(eElement.getAttribute("nbTuples"))!=0)
					{										//evite les erreurs lorsque nombre de tuples = 0
						rel[temp].relation=new int[rel[temp].nbTuples][rel[temp].arity];
						rel[temp].relationS=new String[rel[temp].nbTuples][rel[temp].arity];
						rel[temp].poid=new Sp[rel[temp].nbTuples];
						String r=nNode.getTextContent();
						interpretationRelation(r, rel[temp]);
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
			
			cons=new Constraint[nbConstraints];
			//////Constraint//////
			nList = doc.getElementsByTagName("constraint");
			for (int temp = 0; temp < nbConstraints; temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;

					cons[temp]=new Constraint();
					
					cons[temp].name=eElement.getAttribute("name");
					cons[temp].arity=Integer.parseInt(eElement.getAttribute("arity"));
					cons[temp].scope=eElement.getAttribute("scope");
					cons[temp].reference=eElement.getAttribute("reference");					
				}
			}
		
			
	// traitement des variables impliqués dans les contraintes (scope -> scopeID[] avec ID : emplacement dans var)
			
			for(int cpt=0; cpt<cons.length; cpt++){		//on parcourt toutes les contraintes

				cons[cpt].relation=null;
				//obtenir l'emplacement de la relation
				String string="", subString="";
				string=cons[cpt].reference;
				for(int i=0; i<string.length(); i++){
					if(string.charAt(i)!=' ')
						subString+=string.charAt(i);		//on recupere la referance (sans espace)
				}
				for (int j=0; j<rel.length; j++){
					if(rel[j].name.compareTo(subString)==0){
						cons[cpt].relation=rel[j];						// on trouve la relation, c'est la numero j
						break;
					}
				}
				if(cons[cpt].relation==null)
					System.out.println("erreur de nom de relation!");
				
			
				//obtenir la liste des variables impliques
				string=cons[cpt].scope+" ";
				subString="";
				int k=0;
				cons[cpt].scopeID=new int[cons[cpt].arity];

				
				for(int i=0; i<string.length(); i++){
					if(string.charAt(i)!=' ')
						subString+=string.charAt(i);
					else{
						if(subString.length()!=0){
							for (int j=0; j<var.size(); j++){
								if(var.get(j).name.compareTo(subString)==0){
									cons[cpt].scopeID[k]=j;
									k++;
									subString="";
									break;
								}
							}
						}
					}
				}
			}
				
				
		
		
		
	  } catch (Exception e) {
		e.printStackTrace();
	  }
	}
	
	//lecture en metant les contraintes au bout
public void lectureSuite(String nomFichier) {
		
		NodeList nList;
		int nbRelations2=0;
		int nbConstraints2=0;
		Constraint[] cons2;
		Relation[] rel2;
		
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
			
			rel2=new Relation[nbRelations2];
			//////relation//////
			nList = doc.getElementsByTagName("relation");
			for (int temp = 0; temp < nbRelations2; temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;


					rel2[temp]=new Relation();
					
					rel2[temp].name=eElement.getAttribute("name");
					rel2[temp].arity=Integer.parseInt(eElement.getAttribute("arity"));
					rel2[temp].nbTuples=Integer.parseInt(eElement.getAttribute("nbTuples"));
					rel2[temp].softConstraint=(eElement.getAttribute("semantics").compareTo("soft")==0);
					rel2[temp].conflictsConstraint=(eElement.getAttribute("semantics").compareTo("conflicts")==0);
					if(rel2[temp].softConstraint)
						rel2[temp].defaultCost= new Sp(Integer.parseInt(eElement.getAttribute("defaultCost")));
					
					if(Integer.parseInt(eElement.getAttribute("nbTuples"))!=0)
					{										//evite les erreurs lorsque nombre de tuples = 0
						rel2[temp].relation=new int[rel2[temp].nbTuples][rel2[temp].arity];
						rel2[temp].relationS=new String[rel2[temp].nbTuples][rel2[temp].arity];
						rel2[temp].poid=new Sp[rel2[temp].nbTuples];
						String r=nNode.getTextContent();
						interpretationRelation(r, rel2[temp]);
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
			
			cons2=new Constraint[nbConstraints2];
			//////Constraint//////
			nList = doc.getElementsByTagName("constraint");
			for (int temp = 0; temp < nbConstraints2; temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;

					cons2[temp]=new Constraint();
					
					cons2[temp].name=eElement.getAttribute("name");
					cons2[temp].arity=Integer.parseInt(eElement.getAttribute("arity"));
					cons2[temp].scope=eElement.getAttribute("scope");
					cons2[temp].reference=eElement.getAttribute("reference");					
				}
			}
		
			
	// traitement des variables impliqués dans les contraintes (scope -> scopeID[] avec ID : emplacement dans var)
			
			for(int cpt=0; cpt<cons2.length; cpt++){		//on parcourt toutes les contraintes

				cons2[cpt].relation=null;
				//obtenir l'emplacement de la relation
				String string="", subString="";
				string=cons2[cpt].reference;
				for(int i=0; i<string.length(); i++){
					if(string.charAt(i)!=' ')
						subString+=string.charAt(i);		//on recupere la referance (sans espace)
				}
				for (int j=0; j<rel2.length; j++){
					if(rel2[j].name.compareTo(subString)==0){
						cons2[cpt].relation=rel2[j];						// on trouve la relation, c'est la numero j
						break;
					}
				}
				if(cons2[cpt].relation==null)
					System.out.println("erreur de nom de relation!");
				
			
				//obtenir la liste des variables impliques
				string=cons2[cpt].scope+" ";
				subString="";
				int k=0;
				cons2[cpt].scopeID=new int[cons2[cpt].arity];

				
				for(int i=0; i<string.length(); i++){
					if(string.charAt(i)!=' ')
						subString+=string.charAt(i);
					else{
						if(subString.length()!=0){
							for (int j=0; j<var.size(); j++){
								if(var.get(j).name.compareTo(subString)==0){
									cons2[cpt].scopeID[k]=j;
									k++;
									subString="";
									break;
								}
							}
						}
					}
				}
			}
				
			//mix	
			Relation[] reltemp=new Relation[nbRelations+nbRelations2] ;
			Constraint[] constemp=new Constraint[nbConstraints+nbConstraints2];
			
			for(int i=0; i<nbConstraints; i++){
				constemp[i]=cons[i];
			}
			for(int i=0; i<nbConstraints2; i++){
				constemp[i+nbConstraints]=cons2[i];
			}
			for(int i=0; i<nbRelations; i++){
				reltemp[i]=rel[i];
			}
			for(int i=0; i<nbRelations2; i++){
				reltemp[i+nbRelations]=rel2[i];
			}
			cons=constemp;
			rel=reltemp;
			
			nbConstraints+=nbConstraints2;
			nbRelations+=nbRelations2;
		
		
	  } catch (Exception e) {
		e.printStackTrace();
	  }
		
		
		
	}
	

//poid fort - for, given n, ..., given 2, given 1 -- poid faible 
public void lectureBIFfaux(String nomFichier, boolean arg_plus) {
		
	bif=true;
	
		NodeList nList;
		try {
		File fXmlFile = new File("./"+nomFichier);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
	
			
			//////variable//////
			var=new ArrayList<Var>();
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
			rel=new Relation[nbRelations];
			cons=new Constraint[nbConstraints];		
			//on parcourt les relations
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					//dans une relation
					
					//init
					rel[temp]=new Relation();
					cons[temp]=new Constraint();
					cons[temp].relation=rel[temp];
					rel[temp].name="r"+temp;
					cons[temp].name="c"+temp;
					cons[temp].reference="r"+temp;
					if(arg_plus)
						rel[temp].defaultCost=new Sp(0);				//pas de cout par defaut (ici le 1 c'est le neutre... oui mais on l'additionne apres, alors 0)
					else
						rel[temp].defaultCost=new St(1);
					rel[temp].softConstraint=true;			//on a que du soft !
					rel[temp].conflictsConstraint=false;	
						
//partie qui change					
					//on parcour les givens
					NodeList nList2 = eElement.getElementsByTagName("GIVEN");
					rel[temp].arity=nList2.getLength()+1;			//l'arite c'est le nombre de given + le for
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
					rel[temp].nbTuples=1;					//init (element neutre)
					for(int i=0; i<cons[temp].arity; i++){
						scopeDom[i]=var.get(cons[temp].scopeID[i]).domain;
						curr[i]=0;
						rel[temp].nbTuples*=scopeDom[i];			//on multipli pour avoir le nombre de scope, vu que tout est defini
					}
					
					rel[temp].relation=new int[rel[temp].nbTuples][rel[temp].arity];
					for(int i=0; i<rel[temp].nbTuples; i++){
						for(int j=0; j<rel[temp].arity; j++){
							rel[temp].relation[i][j]=curr[j];
						}
						curr[0]++;					//on incrémente
						for(int j=0; j<rel[temp].arity; j++){		//on verifie si on a pas dépassé
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
						rel[temp].poid=new Sp[rel[temp].nbTuples];
					else
						rel[temp].poid=new St[rel[temp].nbTuples];

					for(int i=0; i<stringTable.length(); i++){
						if(stringTable.charAt(i)!=' ')
							subString+=stringTable.charAt(i);
						else{
							if(subString.length()!=0){
								if(arg_plus)
									rel[temp].poid[k]=new Sp((int) Math.round(-1000*Math.log(Double.parseDouble(subString))));				//-404 : on a besoin d'une fraction (que dans le mult)
								else
									rel[temp].poid[k]=new St(Double.parseDouble(subString));				//-404 : on a besoin d'une fraction (que dans le mult)
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
			var=new ArrayList<Var>();
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
			rel=new Relation[nbRelations];
			cons=new Constraint[nbConstraints];		
			//on parcourt les relations
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					//dans une relation
					
					//init
					rel[temp]=new Relation();
					cons[temp]=new Constraint();
					cons[temp].relation=rel[temp];
					rel[temp].name="r"+temp;
					cons[temp].name="c"+temp;
					cons[temp].reference="r"+temp;
					if(arg_plus)
						rel[temp].defaultCost=new Sp(0);				//pas de cout par defaut (ici le 1 c'est le neutre... oui mais on l'additionne apres, alors 0)
					else
						rel[temp].defaultCost=new St(1);
					rel[temp].softConstraint=true;			//on a que du soft !
					rel[temp].conflictsConstraint=false;	
						
					//on parcour les givens
//partie qui change					
					String stringScope="";
					NodeList nList2 = eElement.getElementsByTagName("FOR");
					    stringScope += nList2.item(0).getTextContent() + " ";				//le for
					    
					nList2 = eElement.getElementsByTagName("GIVEN");
					rel[temp].arity=nList2.getLength()+1;			//l'arite c'est le nombre de given + le for
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
					rel[temp].nbTuples=1;					//init (element neutre)
					for(int i=0; i<cons[temp].arity; i++){
						scopeDom[i]=var.get(cons[temp].scopeID[i]).domain;
						curr[i]=0;
						rel[temp].nbTuples*=scopeDom[i];			//on multipli pour avoir le nombre de scope, vu que tout est defini
					}
					
					rel[temp].relation=new int[rel[temp].nbTuples][rel[temp].arity];
					for(int i=0; i<rel[temp].nbTuples; i++){
						for(int j=0; j<rel[temp].arity; j++){
							rel[temp].relation[i][j]=curr[j];
						}
						curr[0]++;					//on incrémente
						for(int j=0; j<rel[temp].arity; j++){		//on verifie si on a pas dépassé
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
						rel[temp].poid=new Sp[rel[temp].nbTuples];
					else
						rel[temp].poid=new St[rel[temp].nbTuples];

					for(int i=0; i<stringTable.length(); i++){
						if(stringTable.charAt(i)!=' ')
							subString+=stringTable.charAt(i);
						else{
							if(subString.length()!=0){
								if(arg_plus)
									rel[temp].poid[k]=new Sp((int) Math.round(-1000*Math.log(Double.parseDouble(subString))));				//-404 : on a besoin d'une fraction (que dans le mult)
								else
									rel[temp].poid[k]=new St(Double.parseDouble(subString));				//-404 : on a besoin d'une fraction (que dans le mult)
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
			var=new ArrayList<Var>();
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
			rel=new Relation[nbRelations];
			cons=new Constraint[nbConstraints];		
			//on parcourt les relations
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					//dans une relation
					
					//init
					rel[temp]=new Relation();
					cons[temp]=new Constraint();
					cons[temp].relation=rel[temp];
					rel[temp].name="r"+temp;
					cons[temp].name="c"+temp;
					cons[temp].reference="r"+temp;
					if(arg_plus)
						rel[temp].defaultCost=new Sp(0);				//pas de cout par defaut (ici le 1 c'est le neutre... oui mais on l'additionne apres, alors 0)
					else
						rel[temp].defaultCost=new St(1);
					rel[temp].softConstraint=true;			//on a que du soft !
					rel[temp].conflictsConstraint=false;	
						
//partie qui change					
					//on parcour les givens
					NodeList nList2 = eElement.getElementsByTagName("GIVEN");
					rel[temp].arity=nList2.getLength()+1;			//l'arite c'est le nombre de given + le for
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
					rel[temp].nbTuples=1;					//init (element neutre)
					for(int i=0; i<cons[temp].arity; i++){
						scopeDom[i]=var.get(cons[temp].scopeID[i]).domain;
						curr[i]=0;
						rel[temp].nbTuples*=scopeDom[i];			//on multipli pour avoir le nombre de scope, vu que tout est defini
					}
					
					rel[temp].relation=new int[rel[temp].nbTuples][rel[temp].arity];
					for(int i=0; i<rel[temp].nbTuples; i++){
						for(int j=0; j<rel[temp].arity; j++){
							rel[temp].relation[i][j]=curr[j];
						}
						curr[0]++;					//on incrémente
						for(int j=0; j<rel[temp].arity; j++){		//on verifie si on a pas dépassé
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
						rel[temp].poid=new Sp[rel[temp].nbTuples];
					else
						rel[temp].poid=new St[rel[temp].nbTuples];

					for(int i=0; i<stringTable.length(); i++){
						if(stringTable.charAt(i)!=' ')
							subString+=stringTable.charAt(i);
						else{
							if(subString.length()!=0){
								if(arg_plus)
									rel[temp].poid[k]=new Sp((int) Math.round(-1000*Math.log(Double.parseDouble(subString))));				//-404 : on a besoin d'une fraction (que dans le mult)
								else
									rel[temp].poid[k]=new St(Double.parseDouble(subString));				//-404 : on a besoin d'une fraction (que dans le mult)
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


public void readContraintes(String nomFichier){
	
	FileReader fR;
	InputStream ips;
	InputStreamReader ipsr=null;
	BufferedReader br=null;

	String ligne;
	String[] args;

	var=new ArrayList<Var>();
	int idCons=0;
	
	if(!nomFichier.contains(".cons"))
		nomFichier+=".cons";
System.out.println(nomFichier);
	try{
		fR = new FileReader(nomFichier);
	}catch (Exception e) {
		System.out.println("err lecture fichier cons");
	}
	

	try{
		ips=new FileInputStream(nomFichier); 
		ipsr=new InputStreamReader(ips);
		br=new BufferedReader(ipsr);
	}catch (Exception e){
			System.out.println(e.toString());
	}

	try {
		while((ligne=br.readLine())!=null){
			
			if(ligne.length()>0 && ligne.charAt(0)=='#')
				break;						//comment
			
			if(ligne.length()>0 && ligne.charAt(0)=='p'){
				args=ligne.split(" ");		//begining
				if(args[1].compareToIgnoreCase("contraintes")!=0)
					break;
				
				nbVariables = Integer.parseInt(args[2]);
				nbConstraints = Integer.parseInt(args[3]);
				
				rel=new Relation[nbConstraints];
				cons=new Constraint[nbConstraints];
			}
			
			if(ligne.length()>0 && ligne.charAt(0)=='v') {
				args=ligne.split(" ");		//begining
				Var newvar=new Var(args[1], var.size()+1);
				newvar.domain=Integer.parseInt(args[2]);
				if(args.length>3){
					ArrayList<String> dom=new ArrayList<String>();
					for(int i=0; i<newvar.domain; i++)
						dom.add(args[i+3]);
					newvar.ajout(dom);
				}
				else {
					newvar.setDefaultValues();
				}
				var.add(newvar);
			}
			
			if(ligne.length()>0 && ligne.charAt(0)=='c') {
				args=ligne.split(" ");		//begining
				cons[idCons] = new Constraint();
				rel[idCons] = new Relation();
				cons[idCons].arity=Integer.parseInt(args[2]);
				cons[idCons].name="c"+idCons;
				cons[idCons].relation=rel[idCons];
				cons[idCons].scopeID=new int[cons[idCons].arity];
				cons[idCons].scope="";

				rel[idCons].arity=cons[idCons].arity;
				rel[idCons].conflictsConstraint=(args[1].compareTo("conflict")==0);
				rel[idCons].softConstraint=false;
				rel[idCons].defaultCost=new Sp(0);
				rel[idCons].nbTuples=Integer.parseInt(args[3]);
				rel[idCons].poid=new Sp[rel[idCons].nbTuples];
				rel[idCons].relation=new int[rel[idCons].nbTuples][rel[idCons].arity];
				rel[idCons].relationS=new String[rel[idCons].nbTuples][rel[idCons].arity];

				//scope
				ligne=br.readLine();
				if(ligne != null){
					args=ligne.split(" ");
					for(int i=0; i<cons[idCons].arity; i++) {
						cons[idCons].scopeID[i]=getVar(args[i]).pos;
						cons[idCons].scope+=args[i]+" ";
					}
				}
				
				for(int i=0; i<rel[idCons].nbTuples; i++){
					ligne=br.readLine();
					rel[idCons].poid[i]=new Sp(0);
					if(ligne != null){
						args=ligne.split(" ");
						for(int j=0; j<rel[idCons].arity; j++) {
							rel[idCons].relationS[i][j]=args[j];
							if(args[j].startsWith("!"))					//tag2020
								rel[idCons].relation[i][j]=-1-Integer.parseInt(args[j].substring(1));
							else
								rel[idCons].relation[i][j]=Integer.parseInt(args[j]);
						}
					}
				}
				idCons++;

				
			}
				
		}
	} catch (NumberFormatException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	System.out.println(nbConstraints);
	
}


public void lectureCNF(String nomFichier){
	
	FileReader fR;
	InputStream ips;
	InputStreamReader ipsr=null;
	BufferedReader br=null;

	String ligne;
	String[] args;

	var=new ArrayList<Var>();

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
				
				nbVariables = Integer.parseInt(args[2]);
				nbConstraintsfinals = Integer.parseInt(args[3]);
				nbConstraints = 0;
				
				rel=new Relation[nbConstraintsfinals];
				cons=new Constraint[nbConstraintsfinals];
				
				addAllVariablesAsBoolean(nbVariables);
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


	public void addAllVariablesAsBoolean(int nbVar) {
		var=new ArrayList<Var>();
		
		for(int i=1; i<=nbVar; i++) {
			Var v=new Var("v"+String.valueOf(i), i);
			v.setBoolean();
			var.add(v);
		}
	}
	
	public void addContraintCNF(ArrayList<Integer> contraiteBrute){

		int id=nbConstraints;
		
		rel[id]=new Relation();
		rel[id].name="r"+String.valueOf(id);
		rel[id].arity=contraiteBrute.size();
		rel[id].nbTuples=1;
		rel[id].defaultCost=new Sp(0);
		rel[id].softConstraint = false;
		rel[id].conflictsConstraint = true;
		
		rel[id].relation=new int[rel[id].nbTuples][rel[id].arity];
		rel[id].relationS=new String[rel[id].nbTuples][rel[id].arity];
		rel[id].poid=new Sp[rel[id].nbTuples];
		
		for(int i=0; i<rel[id].arity; i++){
			if(contraiteBrute.get(i)>0) {
				rel[id].relationS[0][i]="1";
				rel[id].relation[0][i]=1;
			}
			else {
				rel[id].relationS[0][i]="0";
				rel[id].relation[0][i]=0;
			}
		}
		rel[id].poid[0]=new Sp(0);

		
		cons[id]=new Constraint();
		cons[id].name="c"+String.valueOf(id);
		cons[id].arity=contraiteBrute.size();
		cons[id].reference="r"+String.valueOf(id);	
		cons[id].relation=rel[id];
		cons[id].scope="";
		cons[id].scopeID=new int[cons[id].arity];
		
		for(int i=0; i<rel[id].arity; i++){
			cons[id].scope+="v"+Math.abs(contraiteBrute.get(i))+" ";
			cons[id].scopeID[i]=Math.abs(contraiteBrute.get(i));
		}
		
		nbConstraints++;
	}


	public void actualiseVariables(){
		// traitement des variables impliqués dans les contraintes (scope -> scopeID[] avec ID : emplacement dans var)
		for(int cpt=0; cpt<cons.length; cpt++){		//on parcourt toutes les contraintes
			//obtenir la liste des variables impliques
			String string=cons[cpt].scope+" ";
			String subString="";
			int k=0;
			cons[cpt].scopeID=new int[cons[cpt].arity];

			
			for(int i=0; i<string.length(); i++){
				if(string.charAt(i)!=' ')
					subString+=string.charAt(i);
				else{
					if(subString.length()!=0){
						for (int j=0; j<var.size(); j++){
							if(var.get(j).name.compareTo(subString)==0){
								cons[cpt].scopeID[k]=j;
								k++;
								subString="";
								break;
							}
						}
					}
				}
			}
		}
	}
	
	/*public void variablesUtiles(){
		ArrayList<Var> liste=new ArrayList<Var>();
		for(int i=0; i<var.size(); i++){
			if(isVariableUtile(var.get(i))){
				liste.add(var.get(i));
			}
		}
		var.clear();
		nbVariables=liste.size();
		for(int i=0; i<liste.size(); i++){
			var.add(liste.get(i));
			liste.get(i).pos=i+1;
		}
	}*/
	
	public ArrayList<Var> getVariables() {
		return var;
	}
	
	public int[] getDomains() {
		int[] tabDomains=new int[nbVariables];
		for(int i=0; i<nbVariables; i++)
			tabDomains[i]=var.get(i).domain;
		
		return tabDomains;
	}
	
	public int getDomain(int i) {
		return var.get(i).domain;
	}

	public int getNbVariables() {
		return nbVariables;
	}

	public int getNbConstraints() {
		return nbConstraints;
	}

	//forme : [poid, v0=-1, v1=3, v2=2; v3=-1][poid, v0=-1, v1=4, v2=3; v3=-1] => (1,2) poid:3 2|4 3
	public Structure getDefaultCost(int num){	
		return cons[num].relation.defaultCost;
	}
	
	public boolean getSoftConstraint(int num){
		return cons[num].relation.softConstraint;
	}
	public boolean getConflictsConstraint(int num){
		return cons[num].relation.conflictsConstraint;
	}
	
	public int[][] getConstraint(int num) {
		if(cons[num]==null){
			//System.out.println("sautee");
			return null;
		}
		
		//init
		//on commence les variable a 1 pour correspondre avec la position des variables
		int[][] contrainteComplette=new int[cons[num].relation.nbTuples][nbVariables+1];
		for(int i=1; i<nbVariables+1; i++)
			for(int j=0; j<cons[num].relation.nbTuples; j++)
				contrainteComplette[j][i]=-1;
		
		//suite
		for(int i=0; i<cons[num].relation.nbTuples; i++)
			for(int j=0; j<cons[num].scopeID.length; j++){
				contrainteComplette[i][cons[num].scopeID[j]+1]=cons[num].relation.relation[i][j];  //variables commencent a 0(xml), et a 1(ut)
			}
		return contrainteComplette;
	}
	
	public String[][] getConstraintS(int num) {
		if(cons[num]==null){
			//System.out.println("sautee");
			return null;
		}
		
		//init
		//on commence les variable a 1 pour correspondre avec la position des variables
		String[][] contrainteComplette=new String[cons[num].relation.nbTuples][nbVariables+1];
		for(int i=1; i<nbVariables+1; i++)
			for(int j=0; j<cons[num].relation.nbTuples; j++)
				contrainteComplette[j][i]="";
		
		//suite
		for(int i=0; i<cons[num].relation.nbTuples; i++)
			for(int j=0; j<cons[num].scopeID.length; j++){
				if(cons[num].relation.relationS!=null){
					contrainteComplette[i][cons[num].scopeID[j]+1]=cons[num].relation.relationS[i][j];  //variables commencent a 0(xml), et a 1(ut)
				}else{
					contrainteComplette[i][cons[num].scopeID[j]+1]=var.get(cons[num].scopeID[j]).valeurs.get(cons[num].relation.relation[i][j]);
//					contrainteComplette[i][cons[num].scopeID[j]+1]=String.valueOf(cons[num].relation.relation[i][j]);  //variables commencent a 0(xml), et a 1(ut)
				}
			}
		return contrainteComplette;
	}
	
	public Structure[] getPoid(int num) {
		Structure[] s=new Structure[cons[num].relation.nbTuples];
		for(int i=0; i<cons[num].relation.nbTuples; i++){
			s[i]=cons[num].relation.poid[i];
		}
		return s;

	}

	
	public void compactConstraint() {
		boolean egal=true;
		
		for(int i=0; i<cons.length; i++){
			if(cons[i]!=null){
				for(int j=i+1; j<cons.length; j++){
					if(cons[j]!=null){
						if(cons[i].relation.defaultCost!=null){
							if(cons[i].arity==cons[j].arity && 
							   cons[i].relation.defaultCost.equals(cons[j].relation.defaultCost) && 
							   cons[i].relation.softConstraint==cons[j].relation.softConstraint &&
							   cons[i].relation.conflictsConstraint==cons[j].relation.conflictsConstraint){
								for(int k=0; k<cons[i].arity; k++){
									if(cons[i].scopeID[k]!=cons[j].scopeID[k]){
										egal=false;
										break;
									}
								}
								if(egal){
									fusionConstraintS(cons[i], cons[j]);		//on garde i et on supprime j
									cons[j]=null;
								}else{
									egal=true;
								}
							}
						}
					}
				}
			}
		}
	}

	//pinceMi et pinceMoi sont deux contraintes de meme scope sur un bateau, pinceMi est supprimer, ques qu'il reste? 
	public void fusionConstraint(Constraint pinceMoi, Constraint pinceMi) {
		Relation newRel=new Relation();
		newRel.name=""+pinceMoi.relation.name+"_"+pinceMi.relation.name;
		newRel.arity=pinceMoi.relation.arity;
		newRel.nbTuples=pinceMoi.relation.nbTuples+pinceMi.relation.nbTuples;
		newRel.defaultCost=pinceMoi.relation.defaultCost;
		newRel.softConstraint=pinceMoi.relation.softConstraint;
		newRel.conflictsConstraint=pinceMoi.relation.conflictsConstraint;

		
		newRel.relation=new int[newRel.nbTuples][newRel.arity] ;
		newRel.poid=new Structure[newRel.nbTuples];
		for(int i=0; i<pinceMoi.relation.nbTuples; i++){
			newRel.poid[i]=pinceMoi.relation.poid[i];
			for(int j=0; j<newRel.arity; j++){
				newRel.relation[i][j]=pinceMoi.relation.relation[i][j];
			}
		}
		for(int i=pinceMoi.relation.nbTuples; i<newRel.nbTuples; i++){
			newRel.poid[i]=pinceMi.relation.poid[i-pinceMoi.relation.nbTuples];
			for(int j=0; j<newRel.arity; j++){
				newRel.relation[i][j]=pinceMi.relation.relation[i-pinceMoi.relation.nbTuples][j];
			}
		}
		
		pinceMoi.relation=newRel;
	}
	
	//pinceMi et pinceMoi sont deux contraintes de meme scope sur un bateau, pinceMi est supprimer, ques qu'il reste? 
	public void fusionConstraintS(Constraint pinceMoi, Constraint pinceMi) {
		Relation newRel=new Relation();
		newRel.name=""+pinceMoi.relation.name+"_"+pinceMi.relation.name;
		newRel.arity=pinceMoi.relation.arity;
		newRel.nbTuples=pinceMoi.relation.nbTuples+pinceMi.relation.nbTuples;
		newRel.defaultCost=pinceMoi.relation.defaultCost;
		newRel.softConstraint=pinceMoi.relation.softConstraint;
		newRel.conflictsConstraint=pinceMoi.relation.conflictsConstraint;
		
		newRel.relation=new int[newRel.nbTuples][newRel.arity] ;
		newRel.relationS=new String[newRel.nbTuples][newRel.arity] ;
		newRel.poid=new Structure[newRel.nbTuples];
		for(int i=0; i<pinceMoi.relation.nbTuples; i++){
			newRel.poid[i]=pinceMoi.relation.poid[i];
			for(int j=0; j<newRel.arity; j++){
				newRel.relationS[i][j]=pinceMoi.relation.relationS[i][j];
			}
		}
		for(int i=pinceMoi.relation.nbTuples; i<newRel.nbTuples; i++){
			newRel.poid[i]=pinceMi.relation.poid[i-pinceMoi.relation.nbTuples];
			for(int j=0; j<newRel.arity; j++){
				newRel.relationS[i][j]=pinceMi.relation.relationS[i-pinceMoi.relation.nbTuples][j];
			}
		}
		
		pinceMoi.relation=newRel;
	}
	
	//renvoie une table comptenant toutes les variables impliques dans chacunes des variables
	//table[i][j] -> i : permet de changer de contrainte; j -> permet de parcourir les variables dans la contrainte
	// /!\ taille des lignes variables
	
	//variables avant changement
	public int[][] getInvolvedVariablesEntree() {
		int[][] tableInvolvedVariable;
		tableInvolvedVariable=new int[cons.length][];
		
		for(int i=0; i<cons.length; i++){		//parcourt des contraintes
			tableInvolvedVariable[i]=new int[cons[i].arity];
			for(int j=0; j<cons[i].arity; j++){		//parcourt des scopeID (variables implique dans la contrainte)
				tableInvolvedVariable[i][j]=cons[i].scopeID[j];
			}
		}
			
		return tableInvolvedVariable;
	}
	
	public int[][] getScopeID(){
		int[][] tableScopeID=new int[nbConstraints][];
		for(int i=0; i<nbConstraints; i++){
			tableScopeID[i]=new int[cons[i].scopeID.length];
			for(int j=0; j<cons[i].scopeID.length; j++)
				tableScopeID[i][j]=cons[i].scopeID[j];
		}
		return tableScopeID;
	}

	public int[][] getHardInvolvedVariablesEntree() {
		int taille=0;
		for(int i=0; i<cons.length; i++)
			if (!cons[i].relation.softConstraint)
				taille++;
			
		int[][] tableInvolvedVariable;
		tableInvolvedVariable=new int[taille][];
		
		for(int i=0; i<cons.length; i++){		//parcourt des contraintes
			if (!cons[i].relation.softConstraint){
				tableInvolvedVariable[i]=new int[cons[i].arity];
				for(int j=0; j<cons[i].arity; j++){		//parcourt des scopeID (variables implique dans la contrainte)
					tableInvolvedVariable[i][j]=cons[i].scopeID[j];
				}
			}
		}
			
		return tableInvolvedVariable;
	}
	
	
	//renvoie vrai si cette variable est inclue dans au moins une des contraintes
	///form s to e (inclue) les contraintes commencent a 1
	public boolean isVariableUtile(Var v, int s, int e){
		//System.out.println(v.name);
		for(int i=s; i<e; i++){
			if(cons[i]!=null){
				for(int j=0; j<cons[i].scopeID.length; j++){
					if(cons[i].scopeID[j]==v.pos-1)
						return true;
				}
			}
		}
		//System.out.println("variable refusee : "+v.pos);
		return false;
	}
	
	public boolean isVariableUtile(Var v){
		for(int i=0; i<cons.length; i++){
			if(cons[i]!=null){
				for(int j=0; j<cons[i].scopeID.length; j++){
					if(cons[i].scopeID[j]==v.pos-1)
						return true;
				}
			}
		}
		//System.out.println("variable refusee : "+v.pos);
		return false;	}
	
	public void month(int deb, int fin) {
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

	}
	
	//choix de l'heuristique d'organisation des contraintes.
	public void reorganiseContraintes(HeuristiqueContraintes heuristique){
		reorga=heuristique.reorganiseContraintes(var, cons);		
	}
	
	//donne le num de contrainte correspondant à la place demandee
	public int equiv(int place){
		if(reorga!=null)
			return reorga.get(place);
		else
			return place;
	}
	
	public void afficheOrdreContraintes(){
		System.out.print("ordre des contraintes : ");
		for(int i=0; i<reorga.size(); i++){
			if(cons[reorga.get(i)]!=null)
				System.out.println(i+" : "+cons[reorga.get(i)].name);
		}
	}

	public void viderReorga(){
		reorga.clear();
	}
	
	public Var getVar(String name) {
		for(int i=0; i<var.size(); i++) {
			if(var.get(i).name.compareTo(name)==0)
				return var.get(i);
		}
		return null;
	}
	
	
	public void graphAdjascence(String s, boolean valued, boolean hard) {
		FileWriter fW;
//    	File f;
    	
    	String nomFichier=s;

		//fichier
    	if(nomFichier.endsWith(".dot"))
    		nomFichier=nomFichier.substring(0, nomFichier.length()-4);
    	
		String name_file= "./" + nomFichier + ".dot";
		String name_pdf= "./" + nomFichier + ".pdf";
		try{
			fW = new FileWriter(name_file);
		
	    	//entete
	    	s="graph "+nomFichier+" {\n";
	    	fW.write(s);
	    	
	    	//constraints
	    	s="{node [width=.1,height=.1,label=\"\"]";
	    	for(int cpt=0; cpt<nbConstraints; cpt++)
	    	{
	    		if(cons[cpt].arity>2)
	    			s+=" "+cons[cpt].name;
	    	}
	    	s+="}\n";
	    	fW.write(s);

	    	
	    	
	    	for(int cpt=0; cpt<nbConstraints; cpt++)
	    	{
	    		if((cons[cpt].relation.softConstraint && valued) || (!cons[cpt].relation.softConstraint && hard)){
		    		if(cons[cpt].arity>2) {
			    		for(int i=0; i<cons[cpt].arity; i++){
				    		s=cons[cpt].name + " -- " + var.get(cons[cpt].scopeID[i]).name + ";\n";
				    	    fW.write(s);
			    		}
		    		}else {
		    			if(cons[cpt].arity==2) {
		    				s=var.get(cons[cpt].scopeID[0]).name + " -- " + var.get(cons[cpt].scopeID[1]).name + ";\n";
				    	    fW.write(s);
		    			}
		    		}
	    		}
	    	}
	    	
	    	//end
	    	s="}\n";
	    	fW.write(s);
	    	fW.close(); 


		}catch(java.io.IOException exc){System.out.println("pb de fichier: " + exc);}
		
	}
	
	public void graphAdjascenceSimple(String s) {
		FileWriter fW;
//    	File f;
    	
    	String nomFichier=s;

		//fichier
    	if(nomFichier.endsWith(".dot"))
    		nomFichier=nomFichier.substring(0, nomFichier.length()-4);
    	
		String name_file= "./" + nomFichier + ".dot";
		String name_pdf= "./" + nomFichier + ".pdf";
		try{
			fW = new FileWriter(name_file);
		
	    	//entete
	    	s="graph "+nomFichier+" {\n";
	    	fW.write(s);
		    	
	    	
	    	for(int cpt=0; cpt<nbConstraints; cpt++)
	    	{
	    		for(int i=0; i<cons[cpt].arity; i++)
	    		{
		    		for(int j=i+1; j<cons[cpt].arity; j++)
		    		{
		    			s=var.get(cons[cpt].scopeID[i]).name + " -- " + var.get(cons[cpt].scopeID[j]).name + ";\n";
		    	    	fW.write(s);
		    		}	    			
	    		}
	    	}
	    	
	    	//end
	    	s="}\n";
	    	fW.write(s);
	    	fW.close(); 


		}catch(java.io.IOException exc){System.out.println("pb de fichier: " + exc);}
		
	}
	
/*	public int[][] getInvolvedVariablesConstraints() {
		for (int i=0; i<cons.length; i++){		//on parcourt l'ensemble des contraintes
		
		String string=cons[i].scope+" ";
		String subString="";
		int[] variablesImpliques=new int[cons[num].arity]; int k=0;
		
		//obtenir la liste des variables
		for(int i=0; i<string.length(); i++){
			if(string.charAt(i)!=' ')
				subString+=string.charAt(i);
			else{
				if(subString.length()!=0){
					for (int j=0; j<var.length; j++){
						if(var[j].name.compareTo(subString)==0){
							variablesImpliques[k]=j+1;			//variables commencent a 1
							k++;
							subString="";
							break;
						}
					}
				}
			}
			
		}
		
		int emplacement=0;
		//obtenir l'enplacement de la relation
		string=cons[num].reference;
		for(int i=0; i<string.length(); i++){
			if(string.charAt(i)!=' ')
				subString+=string.charAt(i);
		}
		for (int j=0; j<rel.length; j++){
			if(rel[j].name.compareTo(subString)==0){
				emplacement=j;
				break;
			}
		}
		
		//init
		int[][] contrainteComplette=new int[rel[emplacement].nbTuples][nbVariables+1];
		for(int i=1; i<nbVariables+1; i++)
			for(int j=0; j<rel[emplacement].nbTuples; j++)
				contrainteComplette[j][i]=-1;
		
		//suite
		for(int i=0; i<rel[emplacement].nbTuples; i++)
			for(int j=0; j<variablesImpliques.length; j++){
				contrainteComplette[i][variablesImpliques[j]]=rel[emplacement].relation[i][j];
				contrainteComplette[i][0]=(rel[emplacement].poid[i]-rel[emplacement].defaultCost);	//ne pas oublier d'enlever le poid par defaut
			}
	
		return contrainteComplette;
	}*/
	
	/*public int[] getConstraintPoid(int num) {
		String string=cons[num].scope;
		String subString="";

		int emplacement=0;
		//obtenir l'emplacement de la relation
		string=cons[num].reference;
		for(int i=0; i<string.length(); i++){
			if(string.charAt(i)!=' ')
				subString+=string.charAt(i);
		}
		for (int j=0; j<rel.length; j++){
			if(rel[j].name.compareTo(subString)==0){
				emplacement=j;
				break;
			}
		}
		
		//init
		int[] poidComplet=new int[rel[emplacement].nbTuples];	
		//suite
		for(int i=0; i<rel[emplacement].nbTuples; i++)
			poidComplet=rel[emplacement].poid;
	
		return poidComplet;
	}*/

/*  private static String getTagValue(String sTag, Element eElement) {
	NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
 
        Node nValue = (Node) nlList.item(0);
 
	return nValue.getNodeValue();
  }*/
	
}