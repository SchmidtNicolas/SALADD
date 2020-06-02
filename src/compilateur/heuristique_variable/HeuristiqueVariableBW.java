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

public class HeuristiqueVariableBW implements HeuristiqueVariable {

	@Override
	public ArrayList<Var> reordoner(int[][] contraintes, ArrayList<Var> listeVariables, Ordonnancement ord) {
		ArrayList<Var> liste=new ArrayList<Var>();
	int[] bandWidth=new int[listeVariables.size()];
	for(int i=0; i<bandWidth.length; i++)
		bandWidth[i]=0;
	
	int max;
	int varmax;
	
	ord.constGraphAdj(contraintes);
	ord.constNbContraintes(contraintes);
	
	//init : v0    (a changer)
	liste.add(listeVariables.get(0));
	bandWidth[0]=-1;
	
	for(int cpt=1; cpt<listeVariables.size(); cpt++ ){
		//actualisation bandwidth
		for(int i=0; i<bandWidth.length; i++){
			if(bandWidth[i]>=0)
				bandWidth[i]=0;							//réinit
		}
		for(int i=0; i<liste.size(); i++){
			for(int j=0; j<listeVariables.size(); j++){
				if(bandWidth[j]>=0){					//pas deja passee
					if(ord.graphAdj[i][j]>0)
						bandWidth[j]+=(int)Math.pow((liste.size()-i), 2);
				}
			}
		}
		
		//recherche meilleur
		varmax=-1;
		max=-1;
		for(int i=0; i<bandWidth.length; i++){
			if (bandWidth[i]>max){
				varmax=i;
				max=bandWidth[i];
			}
		}
		
		//System.out.println(varmax + "   " + variables.get(varmax).name);
		liste.add(listeVariables.get(varmax));
			
		bandWidth[varmax]=-1;		//faut plus qu'elle ressorte
	}
	
	return liste;
	}
	
}
