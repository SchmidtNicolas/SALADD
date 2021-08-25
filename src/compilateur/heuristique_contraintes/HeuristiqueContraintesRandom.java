package compilateur.heuristique_contraintes;

import java.util.ArrayList;

import compilateur.Var;
import compilateur.ConstraintsNetwork;


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

public class HeuristiqueContraintesRandom implements HeuristiqueContraintes {

	public ArrayList<Integer> reorganiseContraintes(ArrayList<Var> var, ConstraintsNetwork cn)
	{
		System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		ArrayList<Integer> reorga=new ArrayList<Integer>();
		
		for(int i=var.size()-1; i>=0; i--) {
			for(int j=0; j<cn.nbConstraints; j++) {
				if(cn.getCons(j)!=null){
					if(cn.getCons(j).scopeVar.contains(var.get(i)) && !reorga.contains(j)) {
						reorga.add(j);
					}
				}
			}
		}
		
		for(int i=0; i<cn.nbConstraints; i++){
			if(cn.getCons(i)==null)
				reorga.add(i);
		}
		
		return reorga;
		/*
		int nbContraintes = cn.nbConstraints;
		ArrayList<Integer> reorga=new ArrayList<Integer>();
	reorga.add(0);
	int random;
	for(int i=1; i<nbContraintes; i++){
		random=(int)Math.floor(Math.random()*(reorga.size()+1));
		reorga.add(random, i);
	}
		return reorga;*/
	}	
	
}
