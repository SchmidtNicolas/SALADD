package compilateur.test_independance;

import java.util.ArrayList;

import compilateur.VDD;
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

public class TestNul implements TestIndependance {

	@Override
	public double[][] getIndependancy(ArrayList<Var> v, VDD graph) {
		double[][] variance = new double[v.size()][v.size()];
		for(int i=0; i<v.size(); i++){
			for(int j=0; j<v.size(); j++){
				variance[i][j]=0;
			}
		}
		return variance;
	}

	public boolean estPlusIndependantQue(double valeur1, double valeur2)
	{
		return false;
	}
	
}
