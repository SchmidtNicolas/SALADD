import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import compilateur.*;


public class Main {

//exemple 1 compilation
	//compilation de un fichier unique
	public static void main1(String[] args) {
		
		SALADD s=new SALADD();
		
		ArrayList<String> files=new ArrayList<>();
		files.add("small.xml");
		//files.add("bigPrices.xml");

		//files.add("Benchmarcks\\cnf\\Handmade\\ais\\ais12d.cons");
		
		//big.xml et bigPrices.xml; nature additive; heuristique numero 4; heuristique de contraintes numero 2; affichage de texte niveau 2 sur 3
		s.compilation(files, true, 3, 0, false, 2);
	
		
		//s.suppressionNoeudsBegayants();
		
		
		s.countingValOnArc();
		
		s.save("small.dot");
		System.out.println(s.nb_models());
		
		System.out.println(s.nb_edges());
		System.out.println(s.nb_nodes());

		
	}
	
	
//exemple 2 compilations 
	//compilation de plusieurs fichier, avec sauvegarde (.dot et .xml) de la forme minimale (suppressionNoeudsBegayants)
	// -- 
	//chargement d'un fixhier compile (s2) puis comparaison d'equivalence

	public static void main2(String[] args) {
		
		
		SALADD s=new SALADD();

		s.compilation("big.xml", true, 3, 0, false, 2);
		
		
		
		/*
		SALADD s=new SALADD();
		
		ArrayList<String> files=new ArrayList<>();
		files.add("medium.xml");
		//files.add("mediumPrices.xml");
		
		//big.xml et bigPrices.xml; nature additive; heuristique numero 4; heuristique de contraintes numero 2; affichage de texte niveau 2 sur 3
		s.compilation(files, true, 4, 2, 2);
		
		s.suppressionNoeudsBegayants();
		
		s.save("sauvegarde.dot");
		s.saveToXml("sauvegarde.xml");
		
		System.out.println("fichier sauvegardé");
		System.out.println("--");

		
		SALADD s2=new SALADD();
		
		s2.chargement("sauvegarde.dot", 1);
		
		
		System.out.println(s2.equivalence(s));*/
		
	}
	
	
//exemple 3 configuration de produits, une configuration a la fois

	public static void maina(String[] args) {
			
		SALADD s=new SALADD();
			
		ArrayList<String> files=new ArrayList<>();
		files.add("big.xml");
		//files.add("bigPrices.xml");

		//compile et sauvegarde le problème si jamais compilé avant, le charge sinon.
		//si déj�  en memoire, le problème est juste réinitialisé.
		//s.readProblem(files);
		s.compilation(files, true, 3, 2, false, 2);

		s.save("b.dot");

		s.postTreatments(true);

		s.save("a.dot");
		
		s.propagation();
		System.out.println("cout minimal : "+s.minCost());
		
		System.out.println("variables non affectées : "+s.getFreeVariables());
		//selection variable "v73"
		System.out.println("v73 : "+s.getCurrentDomainOf("v73"));
		//selection alternative "1"
		s.assignAndPropagateTrue("v73", "1");
		System.out.println("assignation de v73 a 1");
		System.out.println("cout minimal : "+s.minCost());

		System.out.println("variables non affectées : "+s.getFreeVariables());
		//selection variable "v27"
		System.out.println("v27 : "+s.getCurrentDomainOf("v27"));	// on demande aussi le cout minimals associé a chaques alternatives (minCosts)
		//selection alternative "0"
		s.assignAndPropagateTrue("v27", "0");
		System.out.println("assignation de v27 a 0");
		System.out.println("cout minimal : "+s.minCost());
		
		System.out.println("variables non affectées : "+s.getFreeVariables());
		//selection variable "v95"
		System.out.println("v95 : "+s.getCurrentDomainOf("v95"));	
		//selection alternative "1"
		s.assignAndPropagateTrue("v95", "1");
		System.out.println("assignation de v95 a 1");
		System.out.println("cout minimal : "+s.minCost());




	}
	
	public static void main(String[] args) {
		
		SALADD s=new SALADD();
			
		ArrayList<String> files=new ArrayList<>();
		files.add("small.xml");
		files.add("smallPrices.xml");
		//files.add("cspplan-gripper-level2.xml");
		//files.add("3blocks.cons");
		
		boolean flag_learnUp = false;
		boolean flag_allInOne = false;
		//compile et sauvegarde le problème si jamais compilé avant, le charge sinon.
		//si déj�  en memoire, le problème est juste réinitialisé.
		//s.readProblem(files);
		s.compilation(files, true, 3, 2, flag_learnUp, flag_allInOne, 2);
		//s.compilation(files, true, 0, 0, 2);

		s.save("b.dot");
		
		//s.postTreatments(flag_allInOne, true);
		
		System.out.println(s.nb_nodes() + " noeuds et "+ s.nb_edges() + " arcs ("+s.nb_models()+"models)");
		System.out.println(s.nb_models());
		s.countRemovedVar(flag_allInOne);
		
		s.save("a.dot");

		s.propagation();
		
		ArrayList<String> setS;
		int r, i;
		String str="";
		String str2="";
		
		Set<String> alreadyDone=new HashSet<String>();

		setS=s.getFreeVariables();
		
		long start;
		ArrayList<Long> times=new ArrayList<Long>();
		
		Random generator = new Random(1);

		
		while(setS.size()>0) {
			start = System.currentTimeMillis();
			//selection variable
			System.out.println("variables non affectées : "+setS);
			r=generator.nextInt(setS.size());
			i=0; for(String st : setS){  if (i == r) {  str=st; break;} i++; }
			
			//selection domaine
			System.out.println(str + " : "+s.getCurrentDomainOf(str));
			setS=s.getCurrentDomainOf(str);
			r=generator.nextInt(setS.size());
			i=0; for(String st : setS){  if (i == r) {  str2=st; break;} i++; }
			System.out.println("assignation de "+str+" a "+str2);		
			s.assignAndPropagateTrue(str, str2);
			alreadyDone.add(str);
			
			s.save("cd_"+str+".dot");
			
			setS=s.getFreeVariables();
			setS.removeAll(alreadyDone);
			
			times.add(System.currentTimeMillis()-start);
		}
		
		System.out.println("variables non affectées : "+setS);
		
		for(long l:times)
			System.out.print(l+"\t");
		System.out.println();

		

		

	}
	
public static void mainz(String[] args) {
		
		SALADD s=new SALADD();
			
		ArrayList<String> files=new ArrayList<>();
		//files.add("big.xml");
		//files.add("bigPrices.xml");
		//files.add("cspplan-gripper-level2.xml");
		//files.add("3blocks.cons");

		s.chargementOBDD("outbdd.dot", 1);

		boolean flag_allInOne = true;
		s.postTreatments(flag_allInOne, true);

		
		System.out.println(s.nb_nodes() + " noeuds et "+ s.nb_edges() + " arcs ("+s.nb_models()+"models)");
		System.out.println(s.nb_models());
		s.countRemovedVar(flag_allInOne);
		System.out.println();
		
		s.save("a.dot");

		s.propagation();
		
		ArrayList<String> setS;
		int r, i;
		String str="";
		String str2="";
		
		Set<String> alreadyDone=new HashSet<String>();

		setS=s.getFreeVariables();
		
		long start;
		ArrayList<Long> times=new ArrayList<Long>();
		
		Random generator = new Random(1);

		
		while(setS.size()>0) {
			start = System.currentTimeMillis();
			//selection variable
			System.out.println("variables non affectées : "+setS);
			r=generator.nextInt(setS.size());
			i=0; for(String st : setS){  if (i == r) {  str=st; break;} i++; }
			
			//selection domaine
			System.out.println(str + " : "+s.getCurrentDomainOf(str));
			setS=s.getCurrentDomainOf(str);
			r=generator.nextInt(setS.size());
			i=0; for(String st : setS){  if (i == r) {  str2=st; break;} i++; }
			System.out.println("assignation de "+str+" a "+str2);		
			s.assignAndPropagateTrue(str, str2);
			alreadyDone.add(str);
			
			s.save("cd_"+str+".dot");
			
			setS=s.getFreeVariables();
			setS.removeAll(alreadyDone);
			
			times.add(System.currentTimeMillis()-start);
		}
		
		System.out.println("variables non affectées : "+setS);
		
		for(long l:times)
			System.out.print(l+"\t");
		System.out.println();

		

		

	}
	
	public static void main_old(String[] args) {
		
		SALADD s=new SALADD();
			
		ArrayList<String> files=new ArrayList<>();
		files.add("small.xml");
		//files.add("bigPrices.xml");

		//compile et sauvegarde le problème si jamais compilé avant, le charge sinon.
		//si déj�  en memoire, le problème est juste réinitialisé.
		//s.readProblem(files);
		s.compilation(files, true, 3, 2, false, 2);

		s.save("b.dot");

		s.postTreatments(true);

		s.save("a.dot");

		s.propagation();
		System.out.println("cout minimal : "+s.minCost());
		
		System.out.println("variables non affectées : "+s.getFreeVariables());
		//selection variable "v73"
		System.out.println("v20 : "+s.getCurrentDomainOf("v20"));
		//selection alternative "1"
		s.assignAndPropagateTrue("v20", "1");
		System.out.println("assignation de v20 a 1");
		System.out.println("cout minimal : "+s.minCost());

		System.out.println("variables non affectées : "+s.getFreeVariables());
		//selection variable "v27"
		System.out.println("v33 : "+s.getCurrentDomainOf("v33"));	// on demande aussi le cout minimals associé a chaques alternatives (minCosts)
		//selection alternative "0"
		s.assignAndPropagateTrue("v33", "1");
		System.out.println("assignation de v33 a 1");
		System.out.println("cout minimal : "+s.minCost());
		
		System.out.println("variables non affectées : "+s.getFreeVariables());

		
		//selection variable "v95"
		System.out.println("v12 : "+s.getCurrentDomainOf("v12"));	
		//selection alternative "0"
		s.assignAndPropagateTrue("v12", "1");
		System.out.println("assignation de v12 a 1");
		System.out.println("cout minimal : "+s.minCost());
		
		System.out.println("variables non affectées : "+s.getFreeVariables());
		//selection variable "v95"
		System.out.println("v24 : "+s.getCurrentDomainOf("v24"));	
		//selection alternative "3"
		s.assignAndPropagateTrue("v24", "0");
		System.out.println("assignation de v24 a 0");
		System.out.println("cout minimal : "+s.minCost());

		System.out.println("variables non affectées : "+s.getFreeVariables());
		//selection variable "v95"
		System.out.println("v19 : "+s.getCurrentDomainOf("v19"));	
		//selection alternative "1"
		s.assignAndPropagateTrue("v19", "0");
		System.out.println("assignation de v19 a 0");
		System.out.println("cout minimal : "+s.minCost());
		
		System.out.println("variables non affectées : "+s.getFreeVariables());
		//selection variable "v95"
		System.out.println("v0 : "+s.getCurrentDomainOf("v0"));	
		//selection alternative "1"
		s.assignAndPropagateTrue("v0", "1");
		System.out.println("assignation de v0 a 1");
		System.out.println("cout minimal : "+s.minCost());
		
		System.out.println("variables non affectées : "+s.getFreeVariables());

	}
	
	//exemple 4 configuration de produits, plusieurs configurations a  la fois

		public static void main4(String[] args) {
				
			SALADD s=new SALADD();
				
			ArrayList<String> files=new ArrayList<>();
			files.add("big.xml");
			files.add("bigPrices.xml");

			//compile et sauvegarde le problème si jamais compilé avant, le charge sinon.
			//si déj�  en memoire, le problème est juste réinitialisé.
			s.readProblem(files, false);
			
			
			Map<String, String> configurationClient1, configurationClient2;
			configurationClient1 = new HashMap<String, String>();
			configurationClient2 = new HashMap<String, String>();
			
			configurationClient1.put("v73", "1");
			configurationClient1.put("v27", "0");
			configurationClient1.put("v95", "1");
			
			s.reinitializeInState(configurationClient1);
			s.propagation();
			System.out.println("client1");
			System.out.println("variables non affectées : "+s.getFreeVariables());
			System.out.println("v10 : "+s.getCurrentDomainOf("v10"));

			s.reinitializeInState(configurationClient2);
			s.propagation();
			System.out.println("client2");
			System.out.println("variables non affectées : "+s.getFreeVariables());
			System.out.println("v26_5_Serie : "+s.getCurrentDomainOf("v26_5_Serie"));
			
			configurationClient1.put("v10", "0");
			s.reinitializeInState(configurationClient1);
			s.propagation();
			System.out.println("client1");
			System.out.println("variables non affectées : "+s.getFreeVariables());
			System.out.println("v72 : "+s.getCurrentDomainOf("v72")+" - " + s.minCosts("v72"));		// on demande aussi le cout minimals associé a chaques alternatives (minCosts)
			
			configurationClient1.put("v26_5_Serie", "1");
			s.reinitializeInState(configurationClient2);
			s.propagation();
			System.out.println("client2");
			System.out.println("variables non affectées : "+s.getFreeVariables());
			System.out.println("v42 : "+s.getCurrentDomainOf("v42")+" - " + s.minCosts("v42"));		// on demande aussi le cout minimals associé a chaques alternatives (minCosts)
			
			//...
			
		}
	
	//exemple 5 recommandation

		public static void main5(String[] args) {
				
			SALADD contraintes=new SALADD();
			SALADD historiques=new SALADD();

			
			ArrayList<String> files=new ArrayList<>();
			files.add("big.xml");
			//files.add("smallPrices.xml");
			
			contraintes.readProblem(files, false);
			
			historiques.compilationDHistorique("smallHistory.xml", 2);
			

			contraintes.propagation();
			historiques.propagation();
			
			
			System.out.println("variables non affectées : "+contraintes.getFreeVariables());
			//selection variable "v33"
			System.out.println("v33 : "+contraintes.getCurrentDomainOf("v33"));
			System.out.println("v33 : "+historiques.recomandation("v33", "small", null));
			//selection alternative "0"
			contraintes.assignAndPropagate("v33", "0");
			historiques.assignAndPropagate("v33", "0");
			System.out.println("assignation de v33 a 0");
			System.out.println("cout minimal : "+contraintes.minCost());
			
			System.out.println("variables non affectées : "+contraintes.getFreeVariables());
			//selection variable "v8"
			System.out.println("v8 : "+contraintes.getCurrentDomainOf("v8"));
			System.out.println("v8 : "+historiques.recomandation("v8", "small", null));
			//selection alternative "0"
			contraintes.assignAndPropagate("v8", "0");
			System.out.println("assignation de v8 a 0");
			System.out.println("cout minimal : "+contraintes.minCost());

			System.out.println("variables non affectées : "+contraintes.getFreeVariables());
			//selection variable "v8"
			System.out.println("v93 : "+contraintes.getCurrentDomainOf("v93"));
			System.out.println("v93 : "+historiques.recomandation("v93", "small", null));
			//selection alternative "0"
			contraintes.assignAndPropagate("v93", "14");
			System.out.println("assignation de v93 a 14");
			System.out.println("cout minimal : "+contraintes.minCost());

			
			//réinitialisation
			contraintes.reinitialisation();
			historiques.reinitialisation();
			
		}
		
		//exemple 1 compilation
		//compilation de un fichier unique
		public static void main_(String[] args) {
			
			SALADD s=new SALADD();
			
			ArrayList<String> files=new ArrayList<>();
			files.add("big.xml");
			//files.add("smallPrices.xml");

			//files.add("Benchmarcks\\cnf\\Handmade\\ais\\ais12d.cons");
			
			//big.xml et bigPrices.xml; nature additive; heuristique numero 4; heuristique de contraintes numero 2; affichage de texte niveau 2 sur 3
			s.compilation(files, true, 3, 2, false, 2);
			s.save("b.dot");
			
			//s.cn.graphAdjascenceSimpleDot("adjGS");
			//s.cn.graphAdjascenceDot("adjG", true, true);
			
			//s.suppressionNoeudsBegayants();
			s.postTreatments(true);
			s.save("b2.dot");

			
			
			System.out.println(s.nb_nodes() + " noeuds et "+ s.nb_edges() + " arcs ("+s.nb_models()+"models)");

			System.out.println(s.nb_models());

			
			

			
		}
		
		public static void mainxfgh(String[] args) {
			Random generator = new Random(1);
			int r;
			SALADD s=new SALADD();
			ArrayList<String> setS;
			String str = null, str2 = null;

			ArrayList<String> files=new ArrayList<>();
			files.add("ais6.cnf");
			//files.add("bigPrices.xml");

			//compile et sauvegarde le problème si jamais compilé avant, le charge sinon.
			//si déj�  en memoire, le problème est juste réinitialisé.
			//s.readProblem(files);
			//s.compilation(files, true, 3, 2, 2);
			s.compilation(files, true, 0, 0, false, 2);
/*
			s.save("b.dot");

			//s.postTreatments(true);
			
			System.out.println(s.nb_nodes() + " noeuds et "+ s.nb_edges() + " arcs ("+s.nb_models()+"models)");
			System.out.println(s.nb_models());
			s.countRemovedVar();
			
			s.propagation();

			InputStream ips;
			InputStreamReader ipsr=null;
			BufferedReader br=null;
			String ligne;
			String[] ar;
			int sucess=0;
			int fail=0;
			
			int weight;
			HashMap<String, String> relation=new HashMap<String, String>();
			ArrayList<String> relA = new ArrayList<String>();
			ArrayList<String> relB = new ArrayList<String>();

			try{
				ips=new FileInputStream("Small_History.txt"); 
				ipsr=new InputStreamReader(ips);
				br=new BufferedReader(ipsr);
			}catch (Exception e){
					System.out.println(e.toString());
			}
			try {
				while((ligne=br.readLine())!=null){
					ar=ligne.split("[,;_]");
					weight=Integer.parseInt(ar[1]);
					relation.clear();
					relA.clear();
					relB.clear();
					for(int i=2; i<ar.length; i+=2) {
						relation.put(ar[i], ar[i+1]);
						relA.add(ar[i]);
						relB.add(ar[i+1]);
					}
					
					for(int i=0; i<relA.size(); i++) {
						if(relA.get(i).compareTo("v44")!=0 && relA.get(i).compareTo("v24")!=0 && s.getFreeVariables().contains(relA.get(i))) {
							//System.out.println(s.getCurrentDomainOf(relA.get(i)));
							//System.out.println(relB.get(i));
							if(s.getCurrentDomainOf(relA.get(i)).contains(relB.get(i))) {
								
								s.assignAndPropagate(relA.get(i), relB.get(i));
							}else {
								System.out.println("fail on "+ relA.get(i));
								fail+=1;
								break;
							}
		
							}
						if(i==relA.size()-1) {
							sucess+=1;
							
							setS=s.getFreeVariables();
							while(setS.size()!=0) {
								r=generator.nextInt(setS.size());
								i=0; for(String st : setS){  if (i == r) {  str=st; break;} i++; }
								setS=s.getCurrentDomainOf(str);
								r=generator.nextInt(setS.size());
								i=0; for(String st : setS){  if (i == r) {  str2=st; break;} i++; }
								s.assignAndPropagate(str, str2);
								setS=s.getFreeVariables();
								//System.out.println(str+"->"+str2);
							}
							s.setWeight(weight);
						}
					}
					int tot=sucess+fail;
					if(tot%100==0)
						System.out.println(sucess+"/"+fail);
					s.reinitialisation();
					s.propagation();

					
				}
			}catch (Exception e) {
				// TODO: handle exception
			}
			s.save("a.dot");

			
		

			

			
*/
		}
}
