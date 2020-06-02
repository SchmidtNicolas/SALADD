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

public class TestMethode3 implements TestIndependance {

	@Override
	public double[][] getIndependancy(ArrayList<Var> v, VDD graph) {
		double[][] variance = new double[v.size()][v.size()];
		int count=graph.countingpondere();	
		
		double[] probabilite1;
		double[] probabilite2;
		double probaTemp;
		Var var1, var2;
		int dom1, dom2;
//		int count2;
		double facteur;
		double distance;
		for(int i=0; i<v.size(); i++){
			var1=v.get(i);
			System.out.println();
			System.out.println(var1.name);
//			for(int j=i+1; j<v.size(); j++){
			for(int j=0; j<v.size(); j++){				
				//---debut du calcul-----
				var2=v.get(j);
					
				distance=0;
				dom1=var1.domain;
				dom2=var2.domain;
				probabilite1=new double[dom1];
				probabilite2=new double[dom2];
				
				//calcul des probab initiales
//				for(int k=0; k<dom1; k++){
//					probabilite1[k]=graph.countingpondereOnVal(var1, k);
//				}
				//calcul des probab initiales
				for(int k=0; k<dom2; k++){
					probabilite2[k]=graph.countingpondereOnVal(var2, k);
				}				
				//calcul des proba au cas par cas
				for(int l=0; l<dom1; l++){
					probabilite1[l]=graph.countingpondereOnVal(var1, l);
					for(int k=0; k<dom2; k++){
						graph.conditioner(var1, l);

						//graph.conditioner(var1, l);
						probaTemp=graph.countingpondereOnVal(var2, k);
						facteur=probabilite1[l]*probabilite2[k];
						if(facteur>5){
							facteur=facteur/count;
							distance+=Math.pow(probaTemp-facteur, 2)/facteur;
						}
						}
					graph.deconditioner(var1);
				}
				variance[i][j]=distance;
				System.out.print(var2.name+"="+(double)(Math.round(distance*100))/100+" ");
				//System.out.print(var2.name+"="+distance+" ");


			}
		}
		return variance;
	}

	public boolean estPlusIndependantQue(double valeur1, double valeur2)
	{
		return valeur1 < valeur2;
	}
	
}
