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
 
public class LecteurDot {
		
	//class Variable { public Domain domain; public String name;}
	public int nbVariables;
	public ArrayList<Var> var;
	public FileReader fR;
	public String nomFichier;
	public boolean flag_plus;
	public boolean flag_times;
	public VDD x;
	public Arc first;
	public UniqueHashTable uht;
	public ArrayList<NodeDD> listTemp; // en attendant de pouvoir les ajouter e la uht
	public NodeDD last;
	public int curr=0;
	public int maxid=0;
	
	
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
	
	
	public LecteurDot(String nF){
		
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

		
		try{
			if((ligne=br.readLine())!=null){
				if(ligne.contains("//AADD")){
					flag_plus=true;
					flag_times=true;
				}
				if(ligne.contains("//SLDDp")){
					flag_plus=true;
					flag_times=false;
				}
				if(ligne.contains("//SLDDt")){
					flag_plus=false;
					flag_times=true;
				}
				if(ligne.contains("//ADD")){
					flag_plus=false;
					flag_times=false;
				}
			}
			int i=0;
			ligne=br.readLine();
			while(!ligne.contains("digraph")){
								
				args=ligne.split(" ");
				ArrayList<String> dom = new ArrayList<String>();
				
				Var v=new Var(args[2], i+1);
				for(int j=3; j<args.length; j++)
					dom.add(args[j]);
				v.ajout(dom);
				var.add(v);
				
				i++;
				ligne=br.readLine();
			}
			uht=new UniqueHashTable(var.size());
			
			String charvar;
			String charid;
			Var v=null;
			int id;
			double val=-1;

			i=0;

			while((ligne=br.readLine())!=null){
				boolean finalnode=false;
				if (!ligne.contains("->") && !ligne.contains("nada") && !ligne.contains("}")){
					
					//decalration d'un noeud.
					charvar=deb_fin(ligne, "[label=", "]");
					if (charvar.contains("shape=box")){				//il est un neud finale
						charvar=deb_fin(charvar, null, ",");
						finalnode=true;
					}else{											//neud classique
						v=findvar(charvar);
					}
					charid=deb_fin(ligne, "n", " ");
					id=Integer.parseInt(charid);
					
					NodeDD node;

					if(!finalnode){		//noeud final
						node=new NodeDD(v, id);
					}
					else{
						node=new NodeDDlast(0);
						last=node;
					}
					if(id>maxid)
						maxid=id;
					
					
					listTemp.add(node);

					if(!node.isLeaf()){
						for(int j=0; j<node.variable.domain; j++){			//on met tous les arcs à bottom

							if(flag_plus && !flag_times)
								new Arc(node,null, j, true, new Sp());	
							if(!flag_plus && flag_times)
								new Arc(node, null, j, true, new St());
							if(!flag_plus && !flag_times)
								System.out.println("@lecturedot : add non implemente");
							if(flag_plus && flag_times)
								System.out.println("@lecturedot : aadd non implemente");

						}
					}

				}

			}
			
			br.close();
			//registre
			int corresp[]=new int[maxid+1];

			for(int j=0; j<listTemp.size(); j++){
				corresp[listTemp.get(j).id]=j;
			}

			ips=new FileInputStream(nomFichier); 
			ipsr=new InputStreamReader(ips);
			br=new BufferedReader(ipsr);

			ligne=br.readLine();
			while(!ligne.contains("digraph")){
				ligne=br.readLine();
			}
	
			//last.fathers.clear();
			while((ligne=br.readLine())!=null){

				if (ligne.contains("->")){			//arc

					val=0;
					String arg;
					NodeDD v1, v2;
					int pos;
//					double coef=1;

					//neud 2
					arg=deb_fin(ligne, " -> n", " [");
					v2=listTemp.get(corresp[Integer.parseInt(arg)]);
					if(!ligne.contains("nada")){
						//val
						if(flag_plus && flag_times){			//cas AADD
							arg=deb_fin(ligne, "<", ",");
							val=Double.parseDouble(arg);
							arg=deb_fin(ligne, "<", null);		//
							arg=deb_fin(arg, ",", ">");		//pour etre sur de pas rater le debut avec une autre virgule
//							coef=Double.parseDouble(arg);
						}else{		//autres cas
							arg=deb_fin(ligne, "label=", ",");
							if(arg!=null)
								val=Double.parseDouble(arg);
							else{

								arg=deb_fin(ligne, "label=", "]");
								if(arg!=null)
									val=Double.parseDouble(arg);
								else
									val=0;
							}
						}
						//neud 1
						arg=deb_fin(ligne, "n", " -> ");
						v1=listTemp.get(corresp[Integer.parseInt(arg)]);
						//pos
						if(ligne.contains(","))
							arg=deb_fin(ligne, "pos=", ", ");
						else
							arg=deb_fin(ligne, "pos=", "]");
						pos=Integer.parseInt(arg);
						if(flag_plus && !flag_times){
							//v1.kids.get(pos).changerFilsRapide(v2);
							v1.kids.get(pos).changerFils(v2);
							v1.kids.get(pos).bottom=0;
							v1.kids.get(pos).operationS(new Sp((int)val));
							//new Arc(v1, v2, pos, new Sp((int)val));	
						}
						if(!flag_plus && flag_times)
							new Arc(v1, v2, pos, new St(val));
						if(!flag_plus && !flag_times)
							System.out.println("@lecturedot : add non implemente");
						if(flag_plus && flag_times)
							System.out.println("@lecturedot : aadd non implemente");
						
					}else{								//si nada
						//val
						if(flag_plus && !flag_times){
							arg=deb_fin(ligne, "label=", "]");
							if(arg!=null)
								val=Double.parseDouble(arg);
							else
								val=0;
							first=new Arc(v2, true, val);
						}
						if(!flag_plus && flag_times){
							arg=deb_fin(ligne, "label=", "]");
							if(arg!=null)
								val=Double.parseDouble(arg);
							else
								val=0;
							val=Double.parseDouble(arg);
							first=new Arc(v2, false, val);
						}

						if(!flag_plus && !flag_times)
							System.out.println("@lecturedot : add non implemente");
						if(flag_plus && flag_times)
							System.out.println("@lecturedot : aadd non implemente");

							
					}
	
					
					
				}
			}
			
			/*ArrayList<Arc> tmp=new ArrayList<Arc>();
			for(int j=0; j<last.fathers.size(); j++){
				if(last.fathers.get(j).bottom==0){
					tmp.add(last.fathers.get(j));
				}
			}
			last.fathers=tmp;*/

			//maintenant on ajoute tout a la uht.
			for(int j=0; j<listTemp.size(); j++){
				uht.ajoutSansNormaliser(listTemp.get(j));
			}

		}catch (Exception e) {
			System.out.println("err buffer lectureDot");
		}
		
		x=new VDD(first, uht, var);
		x.flagMult=this.flag_times;
		x.flagPlus=this.flag_plus;
		
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
	
	
	
