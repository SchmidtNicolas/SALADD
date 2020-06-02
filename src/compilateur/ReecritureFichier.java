package compilateur;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

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

public class ReecritureFichier {
	
	public ReecritureFichier(){
		
	}
	
	public void reecritureALaLigne(String file, String fileout){
    	FileWriter fW;
//		FileReader fR;
		InputStream ips;
		InputStreamReader ipsr=null;
		BufferedReader br=null;

		try{
//			fR = new FileReader(file);
			fW = new FileWriter(fileout);
		
			ips=new FileInputStream(file); 
			ipsr=new InputStreamReader(ips);
			br=new BufferedReader(ipsr);

		String ligne;
		
			int i;
			while((ligne=br.readLine())!=null){
				while(ligne.contains("|")){
					i=ligne.indexOf("|");
					fW.write(ligne.substring(0, i+1)+"\n");
					ligne=ligne.substring(i+1);
				}
				if(ligne.length()>0)
					fW.write(ligne+"\n");

			}
	    	fW.close(); 

			}catch (Exception e){
				System.out.println(e.toString());
		}
	}
	
	public void reecritureMonth(int a, int b, String file, String fileout){
    	FileWriter fW;
//		FileReader fR;
		InputStream ips;
		InputStreamReader ipsr=null;
		BufferedReader br=null;

		try{
//			fR = new FileReader(file);
			fW = new FileWriter(fileout);
		
			ips=new FileInputStream(file); 
			ipsr=new InputStreamReader(ips);
			br=new BufferedReader(ipsr);

		String ligne;
		String lignefinale;
		
			while((ligne=br.readLine())!=null){
				lignefinale="";
				if(ligne.contains(":")){		//alors ligne de contrainte
					for(int i=a; i<=b; i++){
						if(ligne.contains(":"+i+" ")){		//alors ligne de contrainte du bon mois
							int id=ligne.indexOf(":");
							lignefinale=ligne.substring(0, id+1);
							if(i<10){
								lignefinale+=ligne.substring(id+2);
							}else{
								lignefinale+=ligne.substring(id+3);
							}
						}
					}
					
				}else{
					lignefinale=ligne;
				}
				//write
				if(lignefinale.length()>0)
					fW.write(lignefinale+"\n");

			}
	    	fW.close(); 

			}catch (Exception e){
				System.out.println(e.toString());
		}
	}
	
	public void reecritureTxtSmall(String file, String fileout){
    	FileWriter fW;
//		FileReader fR;
		InputStream ips;
		InputStreamReader ipsr=null;
		BufferedReader br=null;

		try{
//			fR = new FileReader(file);
			fW = new FileWriter(fileout);
		
			ips=new FileInputStream(file); 
			ipsr=new InputStreamReader(ips);
			br=new BufferedReader(ipsr);

		String ligne;
		String lignefinale;
		int id, id2;
		
		while((ligne=br.readLine())!=null){
		
			lignefinale="?? p";
			id=ligne.indexOf(":");
			if(id==1){
				lignefinale+=ligne.substring(0, 1);
				lignefinale+="   ";
			}
			if(id==2){
				lignefinale+=ligne.substring(0, 2);
				lignefinale+="  ";
			}
			if(id==3){
				lignefinale+=ligne.substring(0, 3);
				lignefinale+=" ";
			}
			
			lignefinale+="m";
			id2=ligne.indexOf(" ");
			lignefinale+=ligne.substring(id+1, id2);
			if(id2-id==2)
				lignefinale+=" ";
			lignefinale+="   : ";
			
			ligne=ligne.substring(id2+2);
			//les choses serieuses commencent.
			int [] v=new int[48];
			v[0]=1;
			v[1]=2;
			v[2]=3;
			v[3]=4;
			v[4]=5;
			v[5]=6;
			v[6]=7;
			v[7]=8;
			v[8]=9;
			v[9]=10;
			v[10]=11;
			v[11]=12;
			v[12]=13;
			v[13]=14;
			v[14]=15;
			v[15]=16;
			v[16]=17;
			v[17]=18;
			v[18]=19;
			v[19]=21;
			v[20]=23;
			v[21]=24;
			v[22]=25;
			v[23]=26;
			v[24]=27;
			v[25]=28;
			v[26]=29;
			v[27]=30;
			v[28]=31;
			v[29]=32;
			v[30]=33;
			v[31]=34;
			v[32]=35;
			v[33]=36;
			v[34]=37;
			v[35]=38;
			v[36]=39;
			v[37]=40;
			v[38]=41;
			v[39]=42;
			v[40]=43;
			v[41]=44;
			v[42]=45;
			v[43]=46;
			v[44]=93;
			v[45]=51;
			v[46]=52;
			v[47]=53;		
					
			for(int i=0; i<v.length-1; i++){
				id=ligne.indexOf(" ");
				lignefinale+="v"+v[i]+"="+ligne.substring(0, id)+" ";
				ligne=ligne.substring(id+1);
			}
			lignefinale+="v"+v[v.length-1]+"="+ligne;
			
			fW.write(lignefinale+"\n");
		}
    	fW.close(); 


		}catch (Exception e){
			System.out.println(e.toString());
		}
	}
	
	public void reecritureTxtMedium(String file, String fileout){
    	FileWriter fW;
//		FileReader fR;
		InputStream ips;
		InputStreamReader ipsr=null;
		BufferedReader br=null;

		try{
//			fR = new FileReader(file);
			fW = new FileWriter(fileout);
		
			ips=new FileInputStream(file); 
			ipsr=new InputStreamReader(ips);
			br=new BufferedReader(ipsr);

		String ligne;
		String lignefinale;
		int id, id2;
		
		while((ligne=br.readLine())!=null){
		
			lignefinale="?? p";
			id=ligne.indexOf(":");
			if(id==1){
				lignefinale+=ligne.substring(0, 1);
				lignefinale+="   ";
			}
			if(id==2){
				lignefinale+=ligne.substring(0, 2);
				lignefinale+="  ";
			}
			if(id==3){
				lignefinale+=ligne.substring(0, 3);
				lignefinale+=" ";
			}
			
			lignefinale+="m";
			id2=ligne.indexOf(" ");
			lignefinale+=ligne.substring(id+1, id2);
			if(id2-id==2)
				lignefinale+=" ";
			lignefinale+="   : ";
			
			ligne=ligne.substring(id2+1);
			//les choses serieuses commencent.
			int [] v=new int[44];
			
			v[0]=1;
			v[1]=2;
			v[2]=3;
			v[3]=4;
			v[4]=5;
			v[5]=6;
			v[6]=8;
			v[7]=9;
			v[8]=10;
			v[9]=11;
			v[10]=13;
			v[11]=14;
			v[12]=15;
			v[13]=16;
			v[14]=17;
			v[15]=18;
			v[16]=23;
			v[17]=24;
			v[18]=25;
			v[19]=26;
			v[20]=27;
			v[21]=28;
			v[22]=29;
			v[23]=30;
			v[24]=31;
			v[25]=32;
			v[26]=33;
			v[27]=34;
			v[28]=35;
			v[29]=36;
			v[30]=37;
			v[31]=38;
			v[32]=39;
			v[33]=40;
			v[34]=41;
			v[35]=44;
			v[36]=46;
			v[37]=47;
			v[38]=48;
			v[39]=49;
			v[40]=50;
			v[41]=118;
			v[42]=53;
			v[43]=54;		
					
			for(int i=0; i<v.length-1; i++){
				id=ligne.indexOf(" ");
				lignefinale+="v"+v[i]+"="+ligne.substring(0, id)+" ";
				ligne=ligne.substring(id+1);
			}
			lignefinale+="v"+v[v.length-1]+"="+ligne;
			
			fW.write(lignefinale+"\n");
		}
    	fW.close(); 


		}catch (Exception e){
			System.out.println(e.toString());
		}
	}
	
	public void reecritureTxtBig(String file, String fileout){
	   	FileWriter fW;
//			FileReader fR;
			InputStream ips;
			InputStreamReader ipsr=null;
			BufferedReader br=null;

			try{
//				fR = new FileReader(file);
				fW = new FileWriter(fileout);
			
				ips=new FileInputStream(file); 
				ipsr=new InputStreamReader(ips);
				br=new BufferedReader(ipsr);

			String ligne;
			String lignefinale;
			int id, id2;
			
			while((ligne=br.readLine())!=null){
			
				lignefinale="?? p";
				id=ligne.indexOf(":");
				if(id==1){
					lignefinale+=ligne.substring(0, 1);
					lignefinale+="   ";
				}
				if(id==2){
					lignefinale+=ligne.substring(0, 2);
					lignefinale+="  ";
				}
				if(id==3){
					lignefinale+=ligne.substring(0, 3);
					lignefinale+=" ";
				}
				
				lignefinale+="m";
				id2=ligne.indexOf(" ");
				lignefinale+=ligne.substring(id+1, id2);
				if(id2-id==2)
					lignefinale+=" ";
				lignefinale+="   : ";
				
				ligne=ligne.substring(id2+1);
				//les choses serieuses commencent.
			int [] v=new int[87];
			v[0]=1;
			v[1]=2;
			v[2]=3;
			v[3]=4;
			v[4]=5;
			v[5]=6;
			v[6]=7;
			v[7]=8;
			v[8]=9;
			v[9]=10;
			v[10]=11;
			v[11]=12;
			v[12]=13;
			v[13]=14;
			v[14]=15;
			v[15]=16;
			v[16]=17;
			v[17]=18;
			v[18]=19;
			v[19]=20;
			v[20]=21;
			v[21]=22;
			v[22]=23;
			v[23]=24;
			v[24]=25;
			v[25]=26;
			v[26]=32;
			v[27]=33;
			v[28]=36;
			v[29]=37;
			v[30]=38;
			v[31]=39;
			v[32]=40;
			v[33]=41;
			v[34]=42;
			v[35]=44;
			v[36]=45;
			v[37]=46;
			v[38]=47;
			v[39]=48;
			v[40]=49;
			v[41]=50;
			v[42]=51;
			v[43]=52;
			v[44]=53;
			v[45]=54;
			v[46]=55;
			v[47]=56;
			v[48]=57;	
			v[49]=58;
			v[50]=59;
			v[51]=60;
			v[52]=61;
			v[53]=62;
			v[54]=63;
			v[55]=64;
			v[56]=65;
			v[57]=66;
			v[58]=67;	
			v[59]=68;
			v[60]=69;
			v[61]=70;
			v[62]=71;
			v[63]=72;
			v[64]=73;
			v[65]=79;
			v[66]=80;
			v[67]=82;
			v[68]=83;	
			v[69]=84;
			v[70]=85;
			v[71]=86;
			v[72]=87;
			v[73]=88;
			v[74]=89;
			v[75]=92;
			v[76]=93;
			v[77]=94;
			v[78]=95;	
			v[79]=96;
			v[80]=97;
			v[81]=99;
			v[82]=100;
			v[83]=101;
			v[84]=102;
			v[85]=103;
			v[86]=190;
					
			for(int i=0; i<v.length-1; i++){
				id=ligne.indexOf(" ");
				lignefinale+="v"+v[i]+"="+ligne.substring(0, id)+" ";
				ligne=ligne.substring(id+1);
			}
			lignefinale+="v"+v[v.length-1]+"="+ligne;
			
			fW.write(lignefinale+"\n");
		}
    	fW.close(); 


		}catch (Exception e){
			System.out.println(e.toString());
		}
	}

public void test_training(){
	
	FileWriter fW, fW2;
//	FileReader fR;
	InputStream ips;
	InputStreamReader ipsr=null;
	BufferedReader br=null;
	
	ArrayList<String> all=new ArrayList<String>();
	
	int prem;
	int deus;
	
	float pourcent=80; //80%
	int train;
	int test;
	
	int totax=0;
	ArrayList<String> variables=new ArrayList<String>();

	ArrayList<Integer> autorMonth=new ArrayList<Integer>();
	autorMonth.add(1);
	autorMonth.add(2);
	autorMonth.add(3);
	autorMonth.add(4);
	autorMonth.add(5);
	autorMonth.add(6);
	autorMonth.add(7);
	autorMonth.add(8);
	autorMonth.add(9);
	autorMonth.add(10);
	autorMonth.add(11);
	autorMonth.add(12);

	
	try{
//		fR = new FileReader("big_History.csv");
		//fW = new FileWriter("hist/smallHistory_all.csv");
	
		ips=new FileInputStream("big_History.csv"); 
		ipsr=new InputStreamReader(ips);
		br=new BufferedReader(ipsr);

		String ligne;

		
//		fW.write(entete);
		
		ligne=br.readLine();					//1er ligne
		prem=ligne.indexOf(',');
		deus=ligne.indexOf(',',(prem+1));
		ligne=ligne.substring(deus+1);
		String lignevar=ligne+"\n";
		
		prem=ligne.indexOf(',');
		while(prem!=-1){
			variables.add(ligne.substring(0, prem));
			ligne=ligne.substring(prem+1);
			prem=ligne.indexOf(',');
		}
		variables.add(ligne);
		
		while((ligne=br.readLine())!=null){
			prem=ligne.indexOf(',');
			deus=ligne.indexOf(',',(prem+1));
			int freq=Integer.parseInt(ligne.substring(0, prem));
			int mois=Integer.parseInt(ligne.substring(prem+1, deus));
			
			if(autorMonth.contains(mois)){
				totax+=freq;
				ligne=ligne.substring(deus+1);
				ligne+="\n";
				for(int i=0; i<freq; i++)
					//fW.write(ligne);
					all.add(ligne);
			}
		}
			

	
		float prop=pourcent/100;
		
		train=Math.round(totax*prop);
		test=totax-train;
	
	
		fW = new FileWriter("hist/bigHistory_test.csv");
		fW2 = new FileWriter("hist/bigHistory_train.csv");
		
		fW.write(lignevar);
		for(int i=0; i<test; i++){
			int rand=(int) Math.floor(Math.random()*all.size());
			fW.write(all.get(rand));
			all.remove(rand);
		}
		fW.close(); 
				
		fW2.write(lignevar);
		for(int i=0; i<all.size(); i++){
			fW2.write(all.get(i));
		}
		fW2.close(); 

		fW = new FileWriter("hist/bigHistory_testOrdre.csv");
		for(int i=0; i<test; i++){
			ArrayList<String> var2=new ArrayList<String>();
			for(int j=0; j<variables.size(); j++)
				var2.add(variables.get(j));
				
			String s="";
			for(int j=0; j<variables.size(); j++){
				int rand=(int) Math.floor(Math.random()*var2.size());
				s+=var2.get(rand)+",";
				var2.remove(rand);
			}
			s=s.substring(0, s.length()-1);
			fW.write(s+"\n");
			
		}
		fW.close(); 
		
		
		}catch (Exception e){
			System.out.println(e.toString());
		}
	
	
	}
}