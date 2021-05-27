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

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
 
public class LecteurDotOBDD {
		
	//class Variable { public Domain domain; public String name;}
	public int nbVariables;
	public ArrayList<Var> var;
	public FileReader fR;
	public String nomFichier;
	public VDD x;
	public Arc first;
	public UniqueHashTable uht;
	public ArrayList<NodeDD> listTemp; // en attendant de pouvoir les ajouter e la uht
	public Map<String, NodeDD> dico;
	public NodeDD last;
	public int curr=0;
	public int maxid=0;
	public String id1, id0;
	public int nbNodes;
	
/*	public NodeDD getWithIndex(int index){
		if(index==0)
			return last;
		for(int i=curr; i<listTemp.size(); i++){
			if(listTemp.get(i).id==index){
				if(changecurr)
					curr=i;
				return listTemp.get(i);
			}
		}
		return null;
	}*/
	
	
	public LecteurDotOBDD(String nF){
		
		InputStream ips;
		InputStreamReader ipsr=null;
		BufferedReader br=null;
		listTemp=new ArrayList<NodeDD>();
		
		String ligne;
		String[] args;
		
		var=new ArrayList<Var>();
		
		nomFichier=nF;
		if(!nomFichier.contains(".dot"))
			nomFichier+=".dot";
		
		try{
			fR = new FileReader(nomFichier);
		}catch (Exception e) {
			System.out.println("err lectur fichier dot");
		}
		
		
		try{
			ips=new FileInputStream(nomFichier); 
			ipsr=new InputStreamReader(ips);
			br=new BufferedReader(ipsr);
		}catch (Exception e){
				System.out.println(e.toString());
		}

		//first reading to find 0 id and 1 id
		try{
			
			ligne=br.readLine();

			nbNodes=1;
			int numNode;
			while((ligne=br.readLine())!=null){
				if(ligne.contains("shape=box")) {
					if(ligne.contains("label = 1")) {
						args=ligne.split(" ");
						id1=args[0];
					}
					if(ligne.contains("label = 0")) {
						args=ligne.split(" ");
						id0=args[0];
					}
				}else{
				
					if(ligne.contains("label")){
						args=ligne.split(" |]");
						numNode=Integer.parseInt(args[3]);
						if(numNode>nbNodes)
							nbNodes=numNode;
					}
				}

				
			}
			br.close();
			ips=new FileInputStream(nomFichier); 
			ipsr=new InputStreamReader(ips);
			br=new BufferedReader(ipsr);
			
			
			
			for(int vi=1; vi<=nbNodes; vi++) {
				ArrayList<String> dom = new ArrayList<String>();
				dom.add("0"); dom.add("1");
				Var v=new Var("v"+vi, vi-1);
				v.ajout(dom);
				var.add(v);
			}
			
			
			listTemp=new ArrayList<NodeDD>();
			dico=new HashMap<String, NodeDD>();

			uht=new UniqueHashTable(var.size());
    		x = new VDD(null, uht, var);
			listTemp.add(x.last);

			Var v=null;
			int id=1;
			int val;
			String nodeid;
			int varNum;
			NodeDD node1, node2;

			while((ligne=br.readLine())!=null){
				if (ligne.contains("->")){	//arc
					args=ligne.split(" ");
					node1=dico.get(args[0]);
					node2=dico.get(args[2]);

					boolean bottom=false;
					if(args[2].compareTo(id0)==0) {
						bottom=true;
						node2=x.last;
					}
					if(args[2].compareTo(id1)==0) {
						bottom=false;
						node2=x.last;
					}	
						
					if (ligne.contains("dotted"))
						val=0;
					else
						val=1;

					new Arc(node1, node2, val, bottom, new Sp());
					
				}
				if (ligne.contains("label = ") && !ligne.contains("shape=box")) {//node
					args=ligne.split(" |]");
					nodeid=args[0];
					varNum=Integer.parseInt(args[3]);
					v=var.get(varNum-1);
					node1=new NodeDD(v, id);
					new Arc(node1, x.last, 0, true, new Sp());
					new Arc(node1, x.last, 1, true, new Sp());
					id++;
					dico.put(nodeid, node1);
					listTemp.add(node1);
				}
			}
					
			br.close();

			//maintenant on ajoute tout a la uht.
			for(int j=0; j<listTemp.size(); j++){
				uht.ajoutSansNormaliser(listTemp.get(j));
			}
			x.first = new Arc(listTemp.get(listTemp.size()-1), true);
			
		}catch (Exception e) {
			System.out.println("err buffer lectureDot : "+e.getMessage());
		}
		
		
	}
		
	public VDD getVDD(){
		return x;
	}


		
		//renvoie la string de s comprise entre deb et fin (exclue)
		// si void : debut ou fin
		public String deb_fin(String s, String deb, String fin){
			String chaine=s;
				int inddeb=0;
				int indfin=s.length();
			

			if(deb!=null){
					inddeb=s.indexOf(deb);
					if(inddeb!=-1)
						inddeb+=deb.length();
				}
			
			if(fin!=null){
				indfin=s.indexOf(fin, inddeb);
			}
					
			if(indfin!=-1 && inddeb!=-1){		//reussite
				chaine=s.substring(inddeb, indfin);
			}else{
				chaine=null;
			}
			
			return chaine;
		}
		
		public Var findvar(String s){
			for(int i=0; i<var.size(); i++){
				if(var.get(i).name.compareTo(s)==0){
					return var.get(i);
				}
			}
			
			return null;
		}

	
		public UniqueHashTable getuht(){
			return uht;
		}
	
}
	
	
	
