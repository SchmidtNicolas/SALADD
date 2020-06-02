package compilateur.test_independance;

import java.util.ArrayList;

import compilateur.VDD;
import compilateur.Var;


/*   (C) Copyright 2015, Gimenez Pierre-François
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

/**
 * Test d'indépendance basé sur l'information mutuelle
 * @author pgimenez
 *
 */

public class TestInformationMutuelle implements TestIndependance {
	
	public double[][] getIndependancy(ArrayList<Var> v, VDD graph)
	{
//		int count=graph.countingpondere();	
		
//		double[] probabilite1;
//		double[] probabilite2;
//		double probaTemp;
		Var var1, var2;
		int dom1, dom2;
//		int count2;
//		double facteur;
//		double distance;
		double[][] table;
		
		double[][] variance = new double[v.size()][v.size()];
				
		for(int i=0; i<v.size(); i++){
			var1=v.get(i);
			System.out.println();
			System.out.println(var1.name);
			for(int j=i+1; j<v.size(); j++){
//			for(int j=0; j<v.size(); j++){
				
				//---debut du calcul-----
				var2=v.get(j);

				dom1=var1.domain;
				dom2=var2.domain;
				table=new double[dom1][dom2];
	
				//calcul des proba au cas par cas
				for(int l=0; l<dom1; l++){
					graph.conditioner(var1, l);
					for(int k=0; k<dom2; k++){
						graph.conditioner(var2, k);
						table[l][k]=graph.countingpondere();
						graph.deconditioner(var2);

					}
					graph.deconditioner(var1);
				}

				double infmu = 0.;
				double[] som1 = new double[dom1];
				double[] som2 = new double[dom2];
				for(int m = 0; m < dom1; m++)
					som1[m] = 0.;
				for(int n = 0; n < dom2; n++)
					som2[n] = 0.;
				
				int sommeTotale = 0;
				
				for(int m = 0; m < dom1; m++)
					for(int n = 0; n < dom2; n++)
					{
						sommeTotale += table[m][n];
						som1[m] += table[m][n];
						som2[n] += table[m][n];
					}

				for(int m = 0; m < dom1; m++)
					for(int n = 0; n < dom2; n++)
						if(table[m][n] != 0)
							infmu += table[m][n]/sommeTotale*Math.log(table[m][n]*sommeTotale/(som1[m]*som2[n]));
				variance[i][j] = infmu;
				variance[j][i] = infmu;
				System.out.print(var2.name+"="+(double)(Math.round(variance[i][j]*100))/100+" ");
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
