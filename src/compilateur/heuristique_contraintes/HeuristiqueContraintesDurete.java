package compilateur.heuristique_contraintes;

import java.util.ArrayList;

import compilateur.Var;
import compilateur.LecteurXML.Constraint;


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

public class HeuristiqueContraintesDurete implements HeuristiqueContraintes {

	public ArrayList<Integer> reorganiseContraintes(ArrayList<Var> var, Constraint[] cons)
	{
		int nbContraintes = cons.length;
		ArrayList<Integer> reorga=new ArrayList<Integer>();

		int proddomain=1;
		
		double[] score=new double[nbContraintes];
		for(int i=0; i<nbContraintes; i++)
			score[i]=-1;
		double min=2;
		int minVal=-1;
	
			
		for(int i=0; i<nbContraintes; i++){
			score[i]=2;
			if(cons[i]!=null){
				score[i]=cons[i].relation.nbTuples;
				proddomain=1;
				for(int j=0; j<cons[i].scopeID.length; j++)
					proddomain*=var.get(cons[i].scopeID[j]).domain;
				
				score[i]=score[i]/proddomain;
			}
		}
		
		
		min=2;
		minVal=-1;
		int j=0;
		for(j=0; j<nbContraintes; j++){
			for(int i=0; i<nbContraintes; i++){
				if(cons[i]!=null && !cons[i].relation.softConstraint){
					if(score[i]<min){
						min=score[i];
						minVal=i;
					}
				}
			}
			if(minVal!=-1){
				reorga.add(minVal);
				score[minVal]=2;
				min=2;
				minVal=-1;
			}else{			//reste plus que des contraintes souples
				min=2;
				minVal=-1;
				break;
			}
		}
		
		for(; j<nbContraintes; j++){
			for(int i=0; i<nbContraintes; i++){
				if(cons[i]!=null && cons[i].relation.softConstraint){
					if(score[i]<min){
						min=score[i];
						minVal=i;
					}
				}
			}
			if(minVal!=-1){
				reorga.add(minVal);
				score[minVal]=2;
				min=2;
				minVal=-1;
			}else{			//reste plus que des contraintes nulles
				for(int i=0; i<nbContraintes; i++){
					if(cons[i]==null){
						reorga.add(i);
					}
				}
				break;
			}
			
		}
		return reorga;
	}			
	
}
