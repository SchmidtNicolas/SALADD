package compilateur.heuristique_variable;

import java.util.ArrayList;

import compilateur.Ordonnancement;
import compilateur.Var;


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

public class HeuristiqueVariableMCSinv implements HeuristiqueVariable {

	@Override
	public ArrayList<Var> reordoner(int[][] contraintes, ArrayList<Var> listeVariables, Ordonnancement ord) {
		ArrayList<Var> liste=new ArrayList<Var>();
		ord.constGraphAdj(contraintes);
		//constNbContraintes(contraintes);
		
		int score[]=new int[listeVariables.size()];
		int max=-1;
		int varmax=-1;
	
		for(int curr=0; curr<listeVariables.size(); curr++){
			for(int i=0; i<listeVariables.size(); i++){
				if(score[i]>max){
					max=score[i];
					varmax=i;
				}
			}
			//System.out.println("@Ord : "+varmax + "   " + variables.get(varmax).name);
			liste.add(listeVariables.get(varmax));
			
			score[varmax]=-1;		//faut plus qu'elle ressorte
			//mise a jours de score
			for(int i=0; i<listeVariables.size(); i++){
				if(score[i]!=-1 && ord.graphAdj[varmax][i]>0)
					score[i]++;
			}
			max=-1;
			varmax=-1;
				
		}
		return liste;
	}
}
