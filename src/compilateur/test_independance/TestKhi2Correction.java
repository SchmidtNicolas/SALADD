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

public class TestKhi2Correction implements TestIndependance {
	
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
		int[][] table;
		
		double[][] variance = new double[v.size()][v.size()];
		
		for(int i=0; i<v.size(); i++){
			var1=v.get(i);
			System.out.println();
			System.out.println(var1.name);
//			for(int j=i+1; j<v.size(); j++){
			for(int j=0; j<v.size(); j++){
				
				//---debut du calcul-----
				var2=v.get(j);

				dom1=var1.domain;
				dom2=var2.domain;
				table=new int[dom1][dom2];
	
				//calcul des proba au cas par cas
				for(int l=0; l<dom1; l++){
					for(int k=0; k<dom2; k++){
						graph.conditioner(var1, l);
						graph.conditioner(var2, k);
						table[l][k]=graph.countingpondere();
						graph.deconditioner(var2);

					}
					graph.deconditioner(var1);
				}
				
				variance[i][j]=khi2(dom1, dom2, table);
				
				System.out.print(var2.name+"="+variance[i][j]+" ");
				//System.out.print(var2.name+"="+distance+" ");


			}
		}
		return variance;
	}
	
    public double khi2(int tailleV1, int tailleV2, int[][] table2)
	{
    	double[][] table = new double[tailleV1][tailleV2];
    	for(int i = 0; i < tailleV1; i++)
    		for(int j = 0; j < tailleV2; j++)
    			table[i][j] = ((double)table2[i][j]);
		double statistique = 0;
		double[] sommeV1 = new double[tailleV1];
		double[] sommeV2 = new double[tailleV2];
		boolean[] suppr1 = new boolean[tailleV1];
		boolean[] suppr2 = new boolean[tailleV2];
		int newTailleV1 = tailleV1;
		int newTailleV2 = tailleV2;
		
		for(int i = 0; i < tailleV1; i++)
		{
			int somme = 0;
			for(int j = 0; j < tailleV2; j++)
				somme += table[i][j];
			sommeV1[i] = somme;
			if(somme == 0)
			{
				suppr1[i] = true;
				newTailleV1--;
			}
			else
				suppr1[i] = false;
		}
		
		for(int i = 0; i < tailleV2; i++)
		{
			int somme = 0;
			for(int j = 0; j < tailleV1; j++)
				somme += table[j][i];
			sommeV2[i] = somme;
			if(somme == 0)
			{
				suppr2[i] = true;
				newTailleV2--;
			}
			else
				suppr2[i] = false;
		}

		double[][] newTable = new double[newTailleV1][newTailleV2];
		double[] newSommeV1 = new double[newTailleV1];
		double[] newSommeV2 = new double[newTailleV2];

		int a = 0, b = 0;
		for(int i = 0; i < tailleV1; i++)
		{
			if(sommeV1[i] == 0)
				continue;
			int somme = 0;
			for(int j = 0; j < tailleV2; j++)
				somme += table[i][j];
			newSommeV1[a] = somme;
			a++;
		}
		
		for(int i = 0; i < tailleV2; i++)
		{
			if(sommeV2[i] == 0)
				continue;
			int somme = 0;
			for(int j = 0; j < tailleV1; j++)
				somme += table[j][i];
			newSommeV2[b] = somme;
			b++;
		}
		
		a = 0;
		b = 0;
		
		for(int i = 0; i < tailleV1; i++)
			if(suppr1[i])
				continue;
			else
			{
				b = 0;
				for(int j = 0; j < tailleV2; j++)
				{
					if(suppr2[j])
						continue;
					else
						newTable[a][b++] = table[i][j];
				}
				a++;
			}
		
		int N = 0;
		for(int i = 0; i < tailleV1; i++)
			for(int j = 0; j < tailleV2; j++)
				N += table[i][j];

		for(int i = 0; i < newTailleV1; i++)
			for(int j = 0; j < newTailleV2; j++)
			{
				double Eij = ((double)newSommeV1[i]*newSommeV2[j])/N;
				if(Eij >= 10)
					statistique += (newTable[i][j]-Eij)*(newTable[i][j]-Eij)/Eij;
				else
					// Correction de Yates
					statistique += (Math.abs(newTable[i][j]-Eij)-0.5)*(Math.abs(newTable[i][j]-Eij)-0.5)/Eij;
			}
//		System.out.println("statistique: "+statistique);
//		System.out.println("dl: "+(newTailleV1-1)*(newTailleV2-1));
		return pochisq(statistique, (newTailleV1-1)*(newTailleV2-1));
	}
	
	
    private static final double LOG_SQRT_PI = Math.log(Math.sqrt(Math.PI));
    private static final double I_SQRT_PI = 1 / Math.sqrt(Math.PI);
    private static final int MAX_X = 20; // max value to represent exp(x)
 
   /* POCHISQ -- probability of chi-square value
        Adapted from:
        Hill, I. D. and Pike, M. C. Algorithm 299
        Collected Algorithms for the CACM 1967 p. 243
        Updated for rounding errors based on remark in
        ACM TOMS June 1985, page 185
    */
    private double pochisq(double x, int df) {
        double a, s;
        double e, c, z;
 
        if (x <= 0.0 || df < 1) {
            return 1.0;
        }
        a = 0.5 * x;
        boolean even = (df & 1) == 0;
        double y = 0;
        if (df > 1) {
            y = Math.exp(-a);
        }
        s = (even ? y : (2.0 * poz(-Math.sqrt(x))));
        if (df > 2) {
            x = 0.5 * (df - 1.0);
            z = (even ? 1.0 : 0.5);
            if (a > MAX_X) {
                e = (even ? 0.0 : LOG_SQRT_PI);
                c = Math.log(a);
                while (z <= x) {
                    e = Math.log(z) + e;
                    s += Math.exp(c * z - a - e);
                    z += 1.0;
                }
                return s;
            } else {
                e = (even ? 1.0 : (I_SQRT_PI / Math.sqrt(a)));
                c = 0.0;
                while (z <= x) {
                    e = e * (a / z);
                    c = c + e;
                    z += 1.0;
                }
                return c * y + s;
            }
        } else {
            return s;
        }
    }
 
 
    private double poz(double z) {
        double y, x, w;
        double Z_MAX = 6.0; // Maximum meaningful z value
        if (z == 0.0) {
            x = 0.0;
        } else {
            y = 0.5 * Math.abs(z);
            if (y >= (Z_MAX * 0.5)) {
                x = 1.0;
            } else if (y < 1.0) {
                w = y * y;
                x = ((((((((0.000124818987 * w
                        - 0.001075204047) * w + 0.005198775019) * w
                        - 0.019198292004) * w + 0.059054035642) * w
                        - 0.151968751364) * w + 0.319152932694) * w
                        - 0.531923007300) * w + 0.797884560593) * y * 2.0;
            } else {
                y -= 2.0;
                x = (((((((((((((-0.000045255659 * y
                        + 0.000152529290) * y - 0.000019538132) * y
                        - 0.000676904986) * y + 0.001390604284) * y
                        - 0.000794620820) * y - 0.002034254874) * y
                        + 0.006549791214) * y - 0.010557625006) * y
                        + 0.011630447319) * y - 0.009279453341) * y
                        + 0.005353579108) * y - 0.002141268741) * y
                        + 0.000535310849) * y + 0.999936657524;
            }
        }
        return z > 0.0 ? ((x + 1.0) * 0.5) : ((1.0 - x) * 0.5);
    }

	@Override
	public boolean estPlusIndependantQue(double valeur1, double valeur2) {
		return valeur1 > valeur2;
	}
	
}
