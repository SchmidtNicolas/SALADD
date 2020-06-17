package compilateur.heuristique_variable;

import java.util.ArrayList;

import compilateur.ConstraintsNetwork;
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

public class HeuristiqueVariableMCF implements HeuristiqueVariable {

	@Override
	public ArrayList<Var> reordoner(int[][] contraintes, ArrayList<Var> listeVariables, ConstraintsNetwork cn) {
		
		ArrayList<Var> liste=new ArrayList<Var>();
		
		cn.actualise();
		
		int max=-1;
		int varmax=-1;
		
		for(int curr=0; curr<listeVariables.size(); curr++){
			for(int i=0; i<listeVariables.size(); i++){
				if(cn.OccurenceVariableDansContraintes.get(i)>max){
					max=cn.OccurenceVariableDansContraintes.get(i);
					varmax=i;
				}
			}
			//System.out.println(varmax + "   " + variables.get(varmax).name);
			liste.add(listeVariables.get(varmax));
			
			cn.OccurenceVariableDansContraintes.set(varmax, cn.OccurenceVariableDansContraintes.get(varmax)+1);	//++	//faut plus qu'elle ressorte
			max=-1;
			varmax=-1;
		}

		return liste;
		
		
	}

}
