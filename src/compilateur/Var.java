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

public class Var {
	public String name;
	public int pos;
	
	public int domain;
	public ArrayList<String> valeurs;
//	public int indValNeg=-1;			//au cas ou on est une val negative
	public boolean [] consVal;
	
	public Var(String n, int i){
		name=n;
		pos=i;
		valeurs=new ArrayList<String>();
	}
		
	
	public void ajout(ArrayList<String> liste){
		domain=liste.size();
		for(int i=0; i<domain; i++){
			valeurs.add(liste.get(i).trim());
		}
		consVal=new boolean[domain];
	}
	
	public void setBoolean(){
		domain=2;
		valeurs.add("0");
		valeurs.add("1");
		consVal=new boolean[domain];
	}
	
/*	public void ajout(ArrayList<String> liste, int indice){
		
		domain=liste.size();
		for(int i=0; i<domain; i++)
			valeurs.add(liste.get(i).trim());
		indValNeg=indice;
		consVal=new boolean[domain];
	}*/
	
	/**
	 * renvoie la valeur correspondante a son emplacement dans le domaine
	 * 
	 * @param val : une valeur du domain de la variable
	 * @return int : son emplacement dans le domaine
	 */
	public int conv(String val){
		val = val.trim();
		if(val.length()==0)
			return -1;
		
		for(int i=0; i<valeurs.size(); i++){
			{
			if(val.compareTo(valeurs.get(i))==0)
			{
//				System.out.println(val+" == "+valeurs.get(i));
				return i;
			}
//			System.out.println(val+" != "+valeurs.get(i));
			}			
		}
//		System.out.println(val+"========="+this.name + " "+ this.domain + " pos="+this.pos);
//		System.out.println("valeur non connue (Var.java)");
		return -1;
	}
	
	public int conv(int val){
		if(val!=-1)		//si une valeur a rechercher
			return conv(valeurs.get(val));
		return -1;
	}
	
	public void consValTofalse(){
		for(int i=0; i<consVal.length; i++)
			consVal[i]=false;
	}
	
	public boolean consistenceFull(){
		for(int i=0; i<this.consVal.length; i++){
			if(!this.consVal[i])
				return false;
		}
		return true;
	}
	
	public int consistenceSize(){
		int size=0;
		for(int i=0; i<this.consVal.length; i++){
			if(this.consVal[i])
				size++;
		}
		return size;
	}
	
	public int getDomainSize(){
		return domain;
	}
	
	public void setDefaultValues() {
		for(int i=0; i<domain; i++)
			valeurs.add(""+i);
	}
}
