package compilateur.heuristique_contraintes;

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

public class HeuristiqueContraintesBCF implements HeuristiqueContraintes {
	
	public ArrayList<Integer> reorganiseContraintes(ArrayList<Var> var, ConstraintsNetwork cn)
	{
		int nbContraintes = cn.nbConstraints;
		ArrayList<Integer> reorga=new ArrayList<Integer>();
		int max=0;
		int maxVal=-1;
		for(int j=0; j<nbContraintes; j++){
			for(int i=0; i<nbContraintes; i++){
	
				if(cn.getCons(i)!=null){
					if(!reorga.contains(i) && cn.getCons(i).arity>max){
						max=cn.getCons(i).arity;
						maxVal=i;
					}
				}
			}
			if(maxVal!=-1){
				reorga.add(maxVal);
				max=0;
				maxVal=-1;
			}else{			//reste plus que des contraintes supprimes
				for(int i=0; i<nbContraintes; i++){
					if(cn.getCons(i)==null)
						reorga.add(i);
				}
				break;
			}
			
		}
		return reorga;
	}		
}
