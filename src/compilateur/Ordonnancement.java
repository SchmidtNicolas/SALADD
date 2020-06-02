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


import java.util.ArrayList;

import compilateur.heuristique_variable.HeuristiqueVariable;

public class Ordonnancement {
	
	// Attributs
	protected ArrayList<Var> variables;
	public int[][] graphAdj;
	public int[] nbContraintes;
	protected int size;

	// constructeur
	protected Ordonnancement(ArrayList<Var> v){
		variables=v;
		graphAdj=new int[variables.size()][variables.size()];
		nbContraintes=new int[variables.size()];
		
		for(int i=0; i<variables.size(); i++){
			nbContraintes[i]=0;
			for(int j=0; j<variables.size(); j++)
				graphAdj[i][j]=0;
		}
		
	}
	
	protected Ordonnancement(){}//a utiliser avec addVariables
	//methodes
	protected void addVarialbes(ArrayList<Var> v){
		variables=v;
		graphAdj=new int[variables.size()][variables.size()];
		nbContraintes=new int[variables.size()];
		for(int i=0; i<variables.size(); i++){
			nbContraintes[i]=0;
			for(int j=0; j<variables.size(); j++)
				graphAdj[i][j]=0;
		}
		
		size=variables.size();
	}
	
	public void constGraphAdj(int[][] contraintes){
		int arite;
		for(int i=0; i<contraintes.length; i++){
			arite=contraintes[i].length;
			for(int j=0; j<contraintes[i].length; j++){
				for (int k=j+1; k<contraintes[i].length; k++){
					if(graphAdj[contraintes[i][j]][contraintes[i][k]]<arite){
						graphAdj[contraintes[i][j]][contraintes[i][k]]=arite;
						graphAdj[contraintes[i][k]][contraintes[i][j]]=arite;
					}
				}
			}
		}
	}
	
	protected void constGraphAdjOriente(int[][] contraintes){
		for(int i=0; i<contraintes.length; i++){
			for(int j=0; j<contraintes[i].length-1; j++){
				graphAdj[contraintes[i][j]][contraintes[i][contraintes[i].length-1]]++;
			}
		}
	}
	
	public void constNbContraintes(int[][] contraintes){
		for (int i=0; i<contraintes.length; i++){
			for(int j=0; j<contraintes[i].length; j++){
				nbContraintes[contraintes[i][j]]++;
			}
		}
	}
	
	protected void reordoner(int[][] contraintes, HeuristiqueVariable methode){
		reordoner(contraintes, methode, false);
	}
	
	//gros morceau !!!!!
	protected void reordoner(int[][] contraintes, HeuristiqueVariable methode, boolean reverse){
		boolean ok=true;
		
		ArrayList<Var> copie=new ArrayList<Var>();
		ArrayList<Var> listetriee=new ArrayList<Var>();
		
		for(int i=0; i<variables.size(); i++)
			copie.add(variables.get(i));
		
		
		listetriee=methode.reordoner(contraintes, copie, this);
		if(listetriee.size()!=variables.size()){
			System.out.println("Erreur heuristique variables : liste de variables retournée de mauvaise taille. taille liste : "+listetriee.size() +" / nombre de variables : "+variables.size());
			ok=false;
		}
		for(int i=0; i<listetriee.size(); i++){
			if(listetriee.get(i)==null){
				System.out.println("Erreur heuristique variables : valeure null dans liste retournée");
				ok=false;
				break;
			}
			for(int j=i+1; j<listetriee.size(); j++){
				if(listetriee.get(i).name.compareTo(listetriee.get(j).name)==0){
					System.out.println("Erreur heuristique variables : valeure "+listetriee.get(j).name+" en double dans la liste retournée");
					ok=false;
					break;
				}
			}
		}
		if(ok){
			variables.clear();
			for(int i=0; i<listetriee.size(); i++){
				variables.add(listetriee.get(i));
			}
		}else{
			System.out.println("pas d'ordonnancement de variables");
		}

		
		
		//on prend l'ordre a l'envers
		if(reverse){
			Var temp;
			int j;
			for(int i=0; i<(variables.size())/2; i++){
				j=variables.size()-i-1;
				temp=variables.get(j);
				variables.set(j, variables.get(i));
				variables.set(i, temp);
			}
		}
		
		//////fiiiiiiinnnn//////////
		for(int i=0; i<size; i++){
				variables.get(i).pos=i+1;
		}
	}
	
	protected void afficherOrdre(){
		System.out.println("ordre sur les variables : ");
		for (int i=0; i<variables.size(); i++)
			System.out.println(i + " : " + variables.get(i).name);

	}
	
	protected void getInfo(int[][] contraintes){
		int span=0;
		int bw=0;
		int max=-1;
		int min=size+1;
		for(int i=0; i<contraintes.length; i++){
			for(int j=0; j<contraintes[i].length; j++){
				if(contraintes[i][j]<min)
					min=contraintes[i][j];
				if(contraintes[i][j]>max)
					max=contraintes[i][j];
			}
			if(max!=-1 && min!=size+1){
				span+=(max-min);
				if((max-min)>bw)
					bw=max-min;
			}
			max=-1;
			min=size+1;
		}
		
		int spanv0=0;
		int bwv0=0;
		for(int i=0; i<contraintes.length; i++){
			for(int j=0; j<contraintes[i].length; j++){
				if(variables.get(contraintes[i][j]).name.compareTo("v0") != 0){
					if(contraintes[i][j]<min)
						min=contraintes[i][j];
					if(contraintes[i][j]>max)
						max=contraintes[i][j];
				}
			}
			if(max!=-1 || min!=size+1){
				spanv0+=(max-min);
				if((max-min)>bwv0)
					bwv0=max-min;
			}
			max=-1;
			min=size+1;
		}
		System.out.println("span=" + span + "  spanv0=" + spanv0 + "  bw=" + bw + "  bwv0=" + bwv0 );
				
	}
	
	protected void supprmonth(){
		variables.remove(0);
		size--;
		for(int i=0; i<variables.size(); i++)
			variables.get(i).pos=variables.get(i).pos-1;
	}

	
	//accesseurs
	protected int size(){
		return variables.size();
	}
	
	protected ArrayList<Var> getVariables(){
		return variables;
	}
}
