package compilateur;

	/*   (C) Copyright 2014, Schmidt Nicolas
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



import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.io.*;

import compilateur.heuristique_contraintes.HeuristiqueContraintes;
import compilateur.heuristique_contraintes.HeuristiqueContraintesBCF;
import compilateur.heuristique_contraintes.HeuristiqueContraintesDomaineMaxDomaineMaxEcartMaxHardFirst;
import compilateur.heuristique_contraintes.HeuristiqueContraintesDurete;
import compilateur.heuristique_contraintes.HeuristiqueContraintesInversion;
import compilateur.heuristique_contraintes.HeuristiqueContraintesRandom;
import compilateur.heuristique_contraintes.HeuristiqueContraintesRien;
import compilateur.heuristique_variable.HeuristiqueVariable;
import compilateur.heuristique_variable.HeuristiqueVariableBW;
import compilateur.heuristique_variable.HeuristiqueVariableForce;
import compilateur.heuristique_variable.HeuristiqueVariableMCF;
import compilateur.heuristique_variable.HeuristiqueVariableMCSinv;
import compilateur.heuristique_variable.HeuristiqueVariableMCSinvPlusUn;
import compilateur.heuristique_variable.HeuristiqueVariableOrdreChoisi;
import compilateur.heuristique_variable.HeuristiqueVariableOrdreRandom;
import compilateur.test_independance.TestEcartMax;
import compilateur.test_independance.TestIndependance;

	
public class SALADD {		
	
	private VDD x;//testVDD;
	public ConstraintsNetwork cn;
	private boolean isHistorique;
	private String inX;
	
	private MethodeOubli methode=null;
	
	private HashMap<String, String> historiqueOperations;	// key:variable - valeur:valeur

	/** 
	 * Constructeur
	 */
	public SALADD(){
		x=null;
		historiqueOperations=new HashMap<String, String>();
		inX="";

		isHistorique=false;
	}
	
	/**
	 * Compilation du fichier de contraintes file_name
	 * 
	 * heuristiques d'ordonnancement des variables : -1=alÃ©atoire; 0=ordre naturel; 1=MCF; 2=BW; 3=MCS; 4=MCS+1; 5=Force
	 * heuristiques d'ordonnancement des contraintes : -1=alÃ©atoire; 0=ordre naturel; 1=BCF; 2=tri par difficultÃ©; 3=tri par duretÃ©
	 * 
	 * @param file_name : chemin/nom du fichier a compiler (extention incluse)
	 * @param arg_plus : nature du probleme. TRUE si additif, FALSE si multiplicatif
	 * @param arg_heuristique : heuristique d'ordonnancement des variables a utiliser (valeur conseillÃ©e : '3' ou '4')
	 * @param arg_heuristique_cons : heuristique d'ordonnancement des cointraintes a utiliser (valeur conseillÃ©e : '2')
	 * @param arg_affich_text : niveau d'affichage de texte sur la sortie standard. De 0 (pas de texte) Ã  3 (beaucoup de texte)
	 */
	public void compilation(String file_name, boolean arg_plus, int arg_heuristique, int arg_heuristique_cons, boolean flag_learnup, boolean flag_allInOne, int arg_affich_text){
		ArrayList<String> s=new ArrayList<String>();
		s.add(file_name);
		compilation(s, arg_plus, arg_heuristique, arg_heuristique_cons, flag_learnup, flag_allInOne, arg_affich_text);
	}
	/**
	 * Compilation du fichier de contraintes file_name avec votre heuristique d'ordonnancement de variables perso
	 * Votre heuristique personnelle doit implÃ©menter la classe "HeuristiqueVariable" 
	 * 
	 * heuristiques d'ordonnancement des variables : -1=alÃ©atoire; 0=ordre naturel; 1=MCF; 2=BW; 3=MCS; 4=MCS+1; 5=Force
	 * heuristiques d'ordonnancement des contraintes : -1=alÃ©atoire; 0=ordre naturel; 1=BCF; 2=tri par difficultÃ©; 3=tri par duretÃ©
	 * 
	 * @param file_name : chemin/nom du fichier a compiler (extention incluse)
	 * @param arg_plus : nature du probleme. TRUE si additif, FALSE si multiplicatif
	 * @param arg_heuristique : votre heuristique personnelle
	 * @param arg_heuristique_cons : heuristique d'ordonnancement des cointraintes a utiliser (valeur conseillÃ©e : '2')
	 * @param arg_affich_text : niveau d'affichage de texte sur la sortie standard. De 0 (pas de texte) Ã  3 (beaucoup de texte)
	 */
	public void compilation(String file_name, boolean arg_plus, HeuristiqueVariable arg_heuristique, int arg_heuristique_cons, boolean flag_learnup, boolean flag_allInOne, int arg_affich_text){
		ArrayList<String> s=new ArrayList<String>();
		s.add(file_name);
		compilation(s, arg_plus, arg_heuristique, arg_heuristique_cons, flag_learnup, flag_allInOne, arg_affich_text);
	}
	/**
	 * Compilation du fichier de contraintes file_name avec votre heuristique d'ordonnancement de contraintes perso
	 * Votre heuristique personnelle doit implÃ©menter la classe "HeuristiqueContraintes" 
	 *
	 * heuristiques d'ordonnancement des variables : -1=alÃ©atoire; 0=ordre naturel; 1=MCF; 2=BW; 3=MCS; 4=MCS+1; 5=Force
	 * 
	 * @param file_name : chemin/nom du fichier a compiler (extention incluse)
	 * @param arg_plus : nature du probleme. TRUE si additif, FALSE si multiplicatif
	 * @param arg_heuristique : heuristique d'ordonnancement des variables a utiliser (valeur conseillÃ©e : '3' ou '4')
	 * @param arg_heuristique_cons : votre heuristique personnelle
	 * @param arg_affich_text : niveau d'affichage de texte sur la sortie standard. De 0 (pas de texte) Ã  3 (beaucoup de texte)
	 */
	public void compilation(String file_name, boolean arg_plus, int arg_heuristique, HeuristiqueContraintes arg_heuristique_cons, boolean flag_learnup, boolean flag_allInOne, int arg_affich_text){
		ArrayList<String> s=new ArrayList<String>();
		s.add(file_name);
		compilation(s, arg_plus, arg_heuristique, arg_heuristique_cons, flag_learnup, flag_allInOne, arg_affich_text);
	}
	/**
	 * Compilation du fichier de contraintes file_name avec votre heuristique d'ordonnancement de variables et de contraintes
	 * Votre heuristique personnelle doit implÃ©menter la classe "HeuristiqueVariable" 
	 * Votre heuristique personnelle doit implÃ©menter la classe "HeuristiqueContraintes" 
	 * 
	 * @param file_name : chemin/nom du fichier a compiler (extention incluse)
	 * @param arg_plus : nature du probleme. TRUE si additif, FALSE si multiplicatif
	 * @param arg_heuristique : votre heuristique personnelle
	 * @param arg_heuristique_cons : votre heuristique personnelle
	 * @param arg_affich_text : niveau d'affichage de texte sur la sortie standard. De 0 (pas de texte) Ã  3 (beaucoup de texte)
	 */
	public void compilation(String file_name, boolean arg_plus, HeuristiqueVariable arg_heuristique, HeuristiqueContraintes arg_heuristique_cons, boolean flag_learnup, boolean flag_allInOne, int arg_affich_text){
		ArrayList<String> s=new ArrayList<String>();
		s.add(file_name);
		compilation(s, arg_plus, arg_heuristique, arg_heuristique_cons, flag_learnup, flag_allInOne, arg_affich_text);
	}
	/**
	 * Compilation du (ou des) fichier(s) de contraintes file_names
	 * 
	 * heuristiques d'ordonnancement des variables : -1=alÃ©atoire; 0=ordre naturel; 1=MCF; 2=BW; 3=MCS; 4=MCS+1; 5=Force
	 * heuristiques d'ordonnancement des contraintes : -1=alÃ©atoire; 0=ordre naturel; 1=BCF; 2=tri par difficultÃ©; 3=tri par duretÃ©
	 * 
	 * @param file_names : chemin/nom des fichiers a compiler (extention incluse)
	 * @param arg_plus : nature du probleme. TRUE si additif, FALSE si multiplicatif
	 * @param arg_heuristique : heuristique d'ordonnancement des variables a utiliser (valeur conseillÃ©e : '3' ou '4')
	 * @param arg_heuristique_cons : heuristique d'ordonnancement des cointraintes a utiliser (valeur conseillÃ©e : '2')
	 * @param arg_affich_text : niveau d'affichage de texte sur la sortie standard. De 0 (pas de texte) Ã  3 (beaucoup de texte)
	 */
	public void compilation(ArrayList<String> file_names, boolean arg_plus, int arg_heuristique, int arg_heuristique_cons, boolean flag_learnup, boolean flag_allInOne, int arg_affich_text){
		HeuristiqueVariable[] heuristiquesVariables = {
				new HeuristiqueVariableOrdreRandom(),
				new HeuristiqueVariableOrdreChoisi(),
				new HeuristiqueVariableMCF(),
				new HeuristiqueVariableBW(),
				new HeuristiqueVariableMCSinv(),
				new HeuristiqueVariableMCSinvPlusUn(),
				new HeuristiqueVariableForce()};
		HeuristiqueContraintes[] heuristiquesContraintes = {
				new HeuristiqueContraintesInversion(), 
				new HeuristiqueContraintesRandom(),
				new HeuristiqueContraintesRien(),
				new HeuristiqueContraintesBCF(),
				new HeuristiqueContraintesDomaineMaxDomaineMaxEcartMaxHardFirst(),
				new HeuristiqueContraintesDurete()};
		compilation(file_names, arg_plus, heuristiquesVariables[arg_heuristique+1], heuristiquesContraintes[arg_heuristique_cons+2], flag_learnup, flag_allInOne, arg_affich_text);

	}
	/**
	 * Compilation du (ou des) fichier(s) de contraintes file_names avec votre heuristique d'ordonnancement de variables perso
	 * Votre heuristique personnelle doit implÃ©menter la classe "HeuristiqueVariable" 
	 *
	 * heuristiques d'ordonnancement des contraintes : -1=alÃ©atoire; 0=ordre naturel; 1=BCF; 2=tri par difficultÃ©; 3=tri par duretÃ©
	 * 
	 * @param file_names : chemin/nom des fichiers a compiler (extention incluse)
	 * @param arg_plus : nature du probleme. TRUE si additif, FALSE si multiplicatif
	 * @param arg_heuristique : votre heuristique personnelle
	 * @param arg_heuristique_cons : heuristique d'ordonnancement des cointraintes a utiliser (valeur conseillÃ©e : '2')
	 * @param arg_affich_text : niveau d'affichage de texte sur la sortie standard. De 0 (pas de texte) Ã  3 (beaucoup de texte)
	 */
	public void compilation(ArrayList<String> file_names, boolean arg_plus, HeuristiqueVariable arg_heuristique, int arg_heuristique_cons, boolean flag_learnup, boolean flag_allInOne, int arg_affich_text){
		HeuristiqueContraintes[] heuristiquesContraintes = {
				new HeuristiqueContraintesInversion(), 
				new HeuristiqueContraintesRandom(),
				new HeuristiqueContraintesRien(),
				new HeuristiqueContraintesBCF(),
				new HeuristiqueContraintesDomaineMaxDomaineMaxEcartMaxHardFirst(),
				new HeuristiqueContraintesDurete()};
		compilation(file_names, arg_plus, arg_heuristique, heuristiquesContraintes[arg_heuristique_cons+2], flag_learnup, flag_allInOne, arg_affich_text);

	}
	/**
	 * Compilation du (ou des) fichier(s) de contraintes file_names avec votre heuristique d'ordonnancement de contraintes perso
	 * Votre heuristique personnelle doit implÃ©menter la classe "HeuristiqueContraintes" 
	 *
	 * heuristiques d'ordonnancement des variables : -1=alÃ©atoire; 0=ordre naturel; 1=MCF; 2=BW; 3=MCS; 4=MCS+1; 5=Force
	 * 
	 * @param file_names : chemin/nom des fichiers a compiler (extention incluse)
	 * @param arg_plus : nature du probleme. TRUE si additif, FALSE si multiplicatif
	 * @param arg_heuristique : heuristique d'ordonnancement des variables a utiliser (valeur conseillÃ©e : '3' ou '4')
	 * @param arg_heuristique_cons : votre heuristique personnelle
	 * @param arg_affich_text : niveau d'affichage de texte sur la sortie standard. De 0 (pas de texte) Ã  3 (beaucoup de texte)
	 */
	public void compilation(ArrayList<String> file_names, boolean arg_plus, int arg_heuristique, HeuristiqueContraintes arg_heuristique_cons, boolean flag_learnup, boolean flag_allInOne, int arg_affich_text){
		HeuristiqueVariable[] heuristiquesVariables = {
				new HeuristiqueVariableOrdreRandom(),
				new HeuristiqueVariableOrdreChoisi(),
				new HeuristiqueVariableMCF(),
				new HeuristiqueVariableBW(),
				new HeuristiqueVariableMCSinv(),
				new HeuristiqueVariableMCSinvPlusUn(),
				new HeuristiqueVariableForce()};
		compilation(file_names, arg_plus, heuristiquesVariables[arg_heuristique+1], arg_heuristique_cons, flag_learnup, flag_allInOne, arg_affich_text);
	}


	/**
	 * Compilation du (ou des) fichier(s) de contraintes file_names avec votre heuristique d'ordonnancement de variables et de contraintes
	 * Votre heuristique personnelle doit implÃ©menter la classe "HeuristiqueVariable" 
	 * Votre heuristique personnelle doit implÃ©menter la classe "HeuristiqueContraintes" 
	 * 
	 * @param file_names : chemin/nom des fichiers a compiler (extention incluse)
	 * @param arg_plus : nature du probleme. TRUE si additif, FALSE si multiplicatif
	 * @param arg_heuristique : votre heuristique personnelle
	 * @param arg_heuristique_cons : votre heuristique personnelle
	 * @param arg_affich_text : niveau d'affichage de texte sur la sortie standard. De 0 (pas de texte) Ã  3 (beaucoup de texte)
	 */
	public void compilation(ArrayList<String> file_names, boolean arg_plus, HeuristiqueVariable arg_heuristique, HeuristiqueContraintes arg_heuristique_cons, boolean flag_learnup, boolean flag_allInOne,int arg_affich_text){
		
		isHistorique=false;
		
		long start= System.currentTimeMillis();
		long end;
		
		cn = new ConstraintsNetwork();
		LecteurXML xml=new LecteurXML();
		if(arg_plus){
			if(file_names.get(0).contains(".xml"))
				xml.lecture(file_names.get(0), cn);
			else if(file_names.get(0).contains(".cnf"))
				xml.lectureCNF(file_names.get(0), cn);
			//else if(file_names.get(0).contains(".cons"))
			//	xml.readContraintes(file_names.get(0));
			else
				xml.lecture(file_names.get(0), cn);

		}else{	
			//xml.lectureBIFpifi(file_names.get(0), arg_plus, cn);
		}

		for(int i=1; i<file_names.size(); i++){
			xml.lectureSuite(file_names.get(i), cn);
		}
		
		System.out.println("//actualise");
		cn.actualise();
		System.out.println("//removeImplications");
		cn.removeImplications();
		System.out.println("//compactConstraint");
		cn.compactConstraint();
		System.out.println("//removeUselessConstraints");
		cn.removeUselessConstraints();
		System.out.println("//removeUselessVariables");
		cn.removeUselessVariables();
		//System.out.println("//removeVariablesInOnlyOneConstraint");
		//cn.removeVariablesInOnlyOneConstraint();
		System.out.println("//reordoner");
		cn.reordoner(arg_heuristique, false);			//<---
		System.out.println("//compileNotUsedVariables");
		cn.compileNotUsedVariables();
		System.out.println("//fin pretraintement");
		
		
		
		UniqueHashTable uht=new UniqueHashTable(cn.nbVariables);
		x =new VDD(cn.getVarPos(), uht, arg_plus);

		uht.ellagage(cn);
			
		x.flagMult=(!arg_plus);											//<---
		x.flagPlus=arg_plus;											//<---
			
	
		int contraintes[][];
		Constraint c;
		Structure Poids[];
		Structure defaultCost;
		boolean softConstraint;
		boolean conflictsConstraint;
		
		System.out.println();
		cn.reorganiseContraintes(arg_heuristique_cons);
		
		if(arg_affich_text==3){
			cn.afficherOrdre();
		}
		
		ArrayList<Var> ordre=cn.getVarPos();
		for(int i=0; i<ordre.size(); i++)
			System.out.print(" "+ordre.get(i).name);
		System.out.println();
	
		start= System.currentTimeMillis();
		
		ArrayList<Var> unlearnableVar=new ArrayList<Var>();
    	UniqueHashTable newUht = new UniqueHashTable(x.variables.size());
    	
		for(int i=0; i<cn.nbConstraints; i++){
			
			//if(i==15)
			//	c=null;
			//else
				c=cn.getCons(i);
			
			
			
			if(c!=null){
				/////////////////////////////
				/*
				Poids=c.getPoidTab();
				if(c.arity!=0){
					defaultCost=c.defaultCost;
					softConstraint=c.softConstraint;
					conflictsConstraint=c.conflictsConstraint;				

					contraintes=cn.getFullCons(i);
			
					x.valeurChemin(contraintes, Poids, defaultCost, softConstraint, conflictsConstraint);
					
					//uht.detect();
					if(arg_affich_text>=2){
						end=System.currentTimeMillis();
						System.out.println(i+":sldd"+(i+1)+"/"+xml.nbConstraints+"  nbnoeuds:" + x.uht.size() + " (" + x.uht.sizeArcs() + ")   " + (end-start)/1000+","+(end-start)%1000 + "s " + c.name + " "+ c.computPercentOfRefusedTuples() +"%");
					}
				}*/
				
				 ///////////////////
				c.toVDD(true, null);
				c.vdd.toDot("a"+i+"_"+c.name, false);
				//x.merge_init(c.vdd);
				x.merge(c.vdd);						
				
				
				//x.toDot("a"+i+"_"+c.name+"_", false);
				if(arg_affich_text>=2){
					end=System.currentTimeMillis();
					System.out.print(i+":sldd"+(i+1)+"/"+xml.nbConstraints+"  nbnoeuds:" + x.uht.size() + " (" + x.uht.sizeArcs() + ")   " + (end-start)/1000+","+(end-start)%1000 + "s " + c.name + " "+ c.computPercentOfRefusedTuples() +"%");
					countRemovedVar(flag_allInOne);
					System.out.println();
					//save(c.name);
				}
				//x.toDot("a"+i+"_"+c.name+"_", false);
				
				//x.toDot("a"+i+"_"+c.name+"_", false);
				if(flag_learnup) {
					ArrayList<Integer> ocp = x.getOnlyChildParents(false);
					for(Var v:c.scopeVar){
						if(ocp.contains(v.pos) && !unlearnableVar.contains(v)) {
							boolean removable=true;
							for(int j=i+1; j<cn.nbConstraints; j++){
								if(cn.getCons(j).scopeVar.contains(v)) {
									removable = false;
									break;
								}
							}
							if(removable) {
								x.minMaxConsistance();
								if(flag_allInOne)
									v.saveLearnedGraph(x.learnUp_all(v, null, newUht));
								else
									v.saveLearnedGraph(x.learnUp_all(v, null, null));

				        		unlearnableVar.addAll(v.graphLearned.variables);
				        		v.inGraph=false;
				    			x.forgetOnlyChildParents(v.pos);
							}
						}
					}
					//x.toDot("a"+i+"_"+c.name+"__", false);
				}
				///////////////////////////
			}
			
			
			
//			System.gc();
		}
		x.affichageResultats(arg_affich_text, start);
		
		
		//get variables de decision
		//get variables a decider
		
		//pour chaque variable a decider
			//pour chaque valeur	
				//affecter val
				//propage up
				//get variables de decisions impliqués
			//pour cahque variable de decision
				//
		
		//
		
		
		
		
	}
	
	
	/**
	 * Suppression des noeuds begayants (redondants, inutiles)
	 * Ces noeuds sont pourtant necessaires pour la quasi totalitÃ© des fonctions proposÃ©es par cette bibliothÃ¨que
	 * 
	 * @param arg_affich_text : niveau d'affichage de texte sur la sortie standard. De 0 (pas de texte) Ã  2 (beaucoup de texte)
	 */
	public void getGraphAdjContraints(ArrayList<String> file_names){
		ConstraintsNetwork cn = new ConstraintsNetwork();
		LecteurXML xml=new LecteurXML();
		if(file_names.get(0).contains(".xml"))
			xml.lecture(file_names.get(0), cn);
		else if(file_names.get(0).contains(".cnf"))
			xml.lectureCNF(file_names.get(0), cn);
		//else if(file_names.get(0).contains(".cons"))
			//xml.readContraintes(file_names.get(0));
		else
			xml.lecture(file_names.get(0), cn);

		for(int i=1; i<file_names.size(); i++){
			xml.lectureSuite(file_names.get(i), cn);
		}
		
		cn.graphAdjascenceSimpleDot(file_names.get(0)+"adjGS");
		cn.graphAdjascenceDot(file_names.get(0)+"adjG", true, true);
	}
	
	/**
	 * Suppression des noeuds begayants (redondants, inutiles)
	 * Ces noeuds sont pourtant necessaires pour la quasi totalitÃ© des fonctions proposÃ©es par cette bibliothÃ¨que
	 * 
	 * @param arg_affich_text : niveau d'affichage de texte sur la sortie standard. De 0 (pas de texte) Ã  2 (beaucoup de texte)
	 */
	public void suppressionNoeudsBegayants(int arg_affich_text){
		if(arg_affich_text>=2)
    		System.out.println(this.x.uht.size() + " noeuds et " + this.x.uht.sizeArcs() + " arcs avant suppression");
		this.x.uht.rechercheNoeudInutile();
    	if(arg_affich_text>=1){
    		System.out.println(this.x.uht.size() + " noeuds et " + this.x.uht.sizeArcs() + " arcs apres suppression des noeuds begayants");
    		System.out.println("vous ne pourrez plus utiliser correctement les fonctions de configuration de produits");
    	}

	}
	
	/**
	 * Suppression des noeuds begayants (redondants, inutiles)
	 * Ces noeuds sont pourtant necessaires pour la quasi totalitÃ© des fonctions proposÃ©es par cette bibliothÃ¨que
	 */
	public void suppressionNoeudsBegayants(){
		this.x.uht.rechercheNoeudInutile();
	}

	/**
	 * Compilation d'un fichier d'historique en vue de la recomandation
	 * 
	 * @param file_name : chemin/nom du fichier d'historique a compiler (extention incluse)
	 * @param arg_affich_text : niveau d'affichage de texte sur la sortie standard. De 0 (pas de texte) Ã  3 (beaucoup de texte)
	 */
	public void compilationDHistorique(String file_name, int arg_affich_text)
	{
		ArrayList<String> file_names = new ArrayList<String>();
		file_names.add(file_name);
		compilationDHistorique(file_names, arg_affich_text);
	}

	/**
	 * Compilation du (ou des) fichier(s) d'historique en vue de la recomandation
	 * Attention : Si plusieurs fichiers, ceux ci doivent porter sur un meme ensemble de variables
	 * 
	 * @param file_names : chemin/nom des fichiers a compiler (extention incluse)
	 * @param arg_affich_text : niveau d'affichage de texte sur la sortie standard. De 0 (pas de texte) Ã  3 (beaucoup de texte)
	 */
	public void compilationDHistorique(ArrayList<String> file_names, int arg_affich_text){

		isHistorique=true;
		
		long start= System.currentTimeMillis();
		long end;
		
		LecteurXML xml=new LecteurXML();
		ConstraintsNetwork cn = new ConstraintsNetwork();

		
/*		for(int i=0; i<file_name.size(); i++)
		{
			String v = file_name.get(i);
			if(v.charAt(v.length()-4) != '.')
				v = v+".xml";
			else if(!v.endsWith(".xml"))
				v = v.substring(0, v.length()-4)+".xml";
			file_name.set(i, v);
		}
		
		xml.lecture(file_name.get(0));
		for(int i=1; i<file_name.size(); i++){
			xml.lectureSuite(file_name.get(i));
		}*/
		xml.lecture(file_names.get(0), cn);

		for(int i=1; i<file_names.size(); i++){
			xml.lectureSuite(file_names.get(i), cn);
		}
		
		
		
		
//			ord.reordoner(xml.getInvolvedVariablesEntree(), 0, false);			//<---
		cn.actualise();
//			xml.compactConstraint();
		
		UniqueHashTable uht=new UniqueHashTable(cn.nbVariables);
		x =new VDD(cn.getVarPos(), uht, true);

			
		x.flagMult=false;											//<---
		x.flagPlus=true;											//<---
			
	
		int contraintes[][];
		Constraint c;
		Structure Poids[];
		Structure defaultCost;
		boolean softConstraint;
		boolean conflictsConstraint;
		
//			xml.reorganiseContraintes(0);
		
	
		/*
		for(int i=0; i<cn.nbConstraints; i++){
			
			c=cn.getCons(i);
			
			
			
			if(c!=null){
				Poids=c.getPoidTab();
				if(c.arity!=0){
					defaultCost=c.defaultCost;
					softConstraint=c.softConstraint;
					conflictsConstraint=c.conflictsConstraint;				

					contraintes=c.getConsTab();
			
					x.valeurChemin(contraintes, Poids, defaultCost, softConstraint, conflictsConstraint);


					
					//uht.detect();
					if(arg_affich_text>=2){
						end=System.currentTimeMillis();
						System.out.println(i+":sldd"+(i+1)+"/"+xml.nbConstraints+"  nbnoeuds:" + x.uht.size() + " (" + x.uht.sizeArcs() + ")   " + (end-start)/1000+","+(end-start)%1000 + "s");
					}
				}
			}
		}
		x.affichageResultats(arg_affich_text, start);
		*/
		
		
		
		for(int i=0; i<cn.nbConstraints; i++){
			
			c=cn.getCons(i);
			
			c.toVDD(true, null);
			
			x.merge(c.vdd);
			
			x.toDot(c.name+"_", false);
					
			if(arg_affich_text>=2){
				end=System.currentTimeMillis();
				System.out.println(i+":sldd"+(i+1)+"/"+xml.nbConstraints+"  nbnoeuds:" + x.uht.size() + " (" + x.uht.sizeArcs() + ")   " + (end-start)/1000+","+(end-start)%1000 + "s");
			}
			
		}
		x.affichageResultats(arg_affich_text, start);
		
	}
	
	
	protected void procedureCompilation(ArrayList<String> FichiersACompiler, boolean arg_plus, int arg_heuristique, int arg_heuristique_cons, String arg_formefinale, String arg_FichierSortie, boolean flag_fichierSortie, boolean flag_beg, boolean flag_learnup, boolean flag_allInOne, int arg_affich_text){
		
		long start= System.currentTimeMillis();
//			long end;
		

		compilation(FichiersACompiler, arg_plus, arg_heuristique, arg_heuristique_cons, flag_learnup, flag_allInOne, arg_affich_text);
		
		//affiche les resultats, es supprim les noeuds beg si besoin
		
		x.affichageResultats(arg_affich_text, start);
		if(!flag_beg){
			this.x.uht.rechercheNoeudInutile();
	    	if(arg_affich_text>=1)
	    		System.out.println(this.x.uht.size() + " noeuds et " + this.x.uht.sizeArcs() + " arcs apres suppression des noeuds begayants (option noskip non activee)");
		}

		
		x.transformation(arg_formefinale, arg_affich_text);


		if(flag_fichierSortie){
			x.toDot(arg_FichierSortie, false);
		}
		
		
	}
	
	/**
	 * chargement d'un fichier dot representant un diagram de decision
	 * 
	 * @param file_name : chemin/nom du fichier d'historique a compiler (extention incluse)
	 * @param arg_affich_text : niveau d'affichage de texte sur la sortie standard. De 0 (pas de texte) Ã  3 (beaucoup de texte)
	 */
	public void chargement(String file_name, int arg_affich_text){
		LecteurDot l=new LecteurDot(file_name);
		x=l.getVDD();
		
		if(arg_affich_text>=1)
			System.out.println("chargement " + x.uht.size() + " (" + x.uht.sizeArcs() + ")");
	}
	
	/**
	 * chargement d'un fichier dot representant un diagram de decision issue de cnf2obdd
	 * 
	 * @param file_name : chemin/nom du fichier d'historique a compiler (extention incluse)
	 * @param arg_affich_text : niveau d'affichage de texte sur la sortie standard. De 0 (pas de texte) Ã  3 (beaucoup de texte)
	 */
	public void chargementOBDD(String file_name, int arg_affich_text){
		LecteurDotOBDD l=new LecteurDotOBDD(file_name);
		x=l.getVDD();
		cn = new ConstraintsNetwork();
		cn.setVar(x.variables);
		if(arg_affich_text>=1)
			System.out.println("chargement " + x.uht.size() + " (" + x.uht.sizeArcs() + ")");
	}


	protected VDD procedureChargement(String arg_FichierACharger, String arg_formefinale, String arg_FichierSortie, boolean flag_fichierSortie, boolean flag_beg, int arg_affich_text){

		///////////////lecture fichier////////////////
		chargement(arg_FichierACharger, arg_affich_text);

		
		x.transformation(arg_formefinale, arg_affich_text);

		return x;
	}
	
	/**
	 * restaure le diagramme dans sa situation initiale
	 */
	public void reinitialisation(){
		x.deconditionerAll();
		if(isHistorique)
			historiqueOperations.clear();
	}
	/**
	 * transformation du SLDD courant en un autre langage
	 * 
	 * @param arg_formefinale : valeurs possibles : {"AADD", "SLDDp", "SLDDt", "ADD"} (avec SLDDp et SLDDt un SLDD de nature respectivement additive et multiplicative).
	 */
	public void transformation(String arg_formefinale){
		x.transformation(arg_formefinale, 0);
	}
	
	/**
	 * donne le nombre de noeuds dans le diagramme
	 * 
	 * @return nombre de noeuds dans le diagramme
	 */
	public int nb_nodes(){
		return x.uht.size();
	}
	
	/**
	 * donne le nombre d'arcs dans le diagramme
	 * 
	 * @return nombre d'arcs dans le diagramme
	 */
	public int nb_edges(){
		return x.uht.sizeArcs();
	}
	
	/**
	 * donne le nombre de modeles du probleme
	 * /!\ si nombre nÃ©gatif : dÃ©passement d'entier
	 * 
	 * @return nombre de modeles
	 */
	public long nb_models(){
		return x.counting();
	}
	
	public void countingValOnArc(){
		x.countingValOnArc();
	}
	
	/**
	 * donne le nombre d'echantillons dans l'historique correspondant a la configuration en cours
	 * 
	 * @return nombre de modeles
	 */
	public int nb_echantillonsHistorique(){
		if(isHistorique=true)
			return x.countingpondere();
		else
			System.out.println("la fonction nb_echantillonsHistorique() ne conscerne que le traitement des historiques");
		return -1;
	}
	
	/**
	 * calcule la variance existant entre les differentes variables de d'un historique
	 * 
	 * @param methode : methode de calcule de variance utilise. a faire vous meme
	 * @param prefix_file_name : nom de lecture / sauvegarde (suivant l'existance) du fichier de sauvegarde de la variance
	 */
	public Variance calculerVarianceHistorique(TestIndependance methode, String prefix_file_name){
		if(isHistorique==true){
			return x.variance(methode, prefix_file_name);
		}else{
			System.out.println("la fonction calculerVariance() ne conscerne que le traitement des historiques");
			return null;
		}
	}
	
	/**
	 * calcule la variance existant entre les differentes variables de d'un historique. la methode utilisÃ©e est fixÃ©e
	 * normalement, c'est fonction est innutile car le calcul de variance est gÃ©rÃ© automatiquement par la fonction recommandation
	 * 
	 * @param prefix_file_name : nom de lecture / sauvegarde (suivant l'existance) du fichier de sauvegarde de la variance
	 */
	public Variance calculerVarianceHistorique(String prefix_file_name){
		if(isHistorique==true){
			return x.variance(new TestEcartMax(), prefix_file_name);
		}else{
			System.out.println("la fonction calculerVariance() ne conscerne que le traitement des historiques");
			return null;
		}
	}
	
	/**
	 * enregistre le diagramme au format .dot
	 * notez que le format .dot peut etre lu par la bibliotheque graphitz afin d'afficher le diagramme. commande : $ dot -Tpdf file_name.dot -o file_name.pdf
	 * 
	 * @param file_name : chemin/nom du fichier de sauvegarde
	 */
	public void save(String file_name){
		x.toDot(file_name, false);
	}

	/**
	 * enregistre le diagramme au format .dot, puis affiche le graph dans un fichier pdf
	 * il est vivement dÃ©conseillÃ© d'utiliser cette fonction sur un diagramme de plusieurs miliers de noeuds
	 * 
	 * @param file_name : chemin/nom du fichier de sauvegarde
	 */
	public void saveToPdf(String file_name){
		x.toDot(file_name, true);
	}

	
	/**
	 * enregistre le diagramme au format .xml
	 * 
	 * @param file_name : chemin/nom du fichier de sauvegarde
	 */
	public void saveToXml(String file_name){
		x.toXML(file_name);
	}

	
	/**
	 * recomandation sur une variable
	 * 
	 * @param var : nom de la variable a recomander
	 * @param methodeOubli : Methode d'oubli Ã  utiliser (cette methode doit implementer l'interface MethodeOubli).
	 * @param possibles : liste des alternatives que l'on considÃ¨re lors de la recommandation. si possible=null, alors on considÃ¨re toutes les valeurs
	 * @return un association valeur->probabilite pour la recomandation
	 */
	public Map<String, Double> recomandation(String var, MethodeOubli methodeOubli, ArrayList<String> possibles){
		if(isHistorique){
			Var v=cn.getVar(var);
			return methodeOubli.recommandation(v, historiqueOperations, x, possibles);
		}else{
			System.out.println("la fonction recomandation() ne conscerne que le traitement des historiques");
			return null;
		}
	}
	
	/**
	 * recomandation sur une variable
	 * 
	 * @param var : nom de la variable a recomander
	 * @param prefix_file_name : nom du fichier d'enregistrement de la variance. doit etre le meme tout au long de la recommandation
	 * @param possibles : liste des alternatives que l'on considÃ¨re lors de la recommandation. si possible=null, alors on considÃ¨re toutes les valeurs
	 * @return un association valeur->probabilite pour la recomandation
	 */
	public Map<String, Double> recomandation(String var, String prefix_file_name, ArrayList <String> possibles){
		if(methode==null){
			methode=new OubliNico(new TestEcartMax());
			methode.learn(this, prefix_file_name);
		}
		
		if(possibles == null)
		{
			possibles = new ArrayList<String>();
			possibles.addAll(cn.getVar(var).valeurs);
		}
		
		if(isHistorique){
			Var v=cn.getVar(var);
			return methode.recommandation(v, historiqueOperations, x, possibles);
		}else{
			System.out.println("la fonction recomandation() ne conscerne que le traitement des historiques");
			return null;
		}
	}

	
	/**
	 * Calcul d'infÃ©rence, Ã  utiliser sur un SLDDx appris d'un rÃ©seau bayÃ©sien
	 * Les valeurs renvoyÃ©es sont des probabilitÃ©s Ã  une constante multiplicative prÃ¨s.
	 * 
	 * @param var
	 * @param possibles : liste des alternatives que l'on considÃ¨re lors de la recommandation. si possible=null, alors on considÃ¨re toutes les valeurs
	 * @return une associassion valeurs probabilitÃ©
	 */
	public Map<String, Double> calculeDistributionAPosteriori(String var, ArrayList<String> possibles){
		Var v=cn.getVar(var);
//		return x.calculeDistributionAPosteriori(v, historiqueOperations, values);
		if(possibles!=null)
			return x.inferenceOnPossibleDomain(v, possibles);
		else
			return x.inferenceOnFullDomain(v);
	}
	
	/**
	 * donne l'ensemble des variables du diagramme
	 * 
	 * @return ArrayList<Var> , une arrayList de l'ensemble des variables Var
	 */
	public ArrayList<Var> getAllVar()
	{
		return x.variables;
	}
	
	/**
	 * donne l'objet Var, la variable portant le nom "var"
	 * 
	 * @return Var , une variables 'Var'
	 */
	public Var getVar(String var)
	{
		return cn.getVar(var);
	}
	
	/**
	 * donne vrai si les deux diagrammes this et s sont Ã©quivalent. 
	 * Cette fonction ne marche pas si l'un des diagrammes a encore ses noeuds begayants alors que l'autre non.
	 * 
	 * @param s : un autre diagramme de type SALADD
	 * @return true si this est equivalent Ã  s
	 */
	public boolean equivalence(SALADD s){
		return this.x.equivalence(s.x);
	}
	
	/**
	 * rÃ©initialise et conditionne le diagramme dans une configuration prÃ©cise
	 * utile pour que plusieurs utilisateur utilisent le configurateur simultanÃ©ment
	 * Cette fonction doit Ãªtre suivie de la fonction propagation()
	 * 
	 * @param state : une Map<String, String> associant Ã  chaque variable (K) que l'on souhaite instancier une valeur (V) Ã  instancier.
	 * @return true si this est equivalent Ã  s
	 */	
	public void reinitializeInState(Map<String, String> state){
		x.reinitializeInState(state);
	}
	
    //////////////
    // Protocol //
    //////////////
    


	/**
     * procÃ©dure automatisÃ©e de chargement de probleme.
	 * Si le problÃ¨me n'a jamais Ã©tÃ© compilÃ©, il est compilÃ© est sauvegardÃ©.
	 * Si le problÃ¨me a dÃ©jÃ  Ã©tÃ© compilÃ©, mais n'est pas chargÃ© en mÃ©moire, il le charge.
	 * Si le problÃ¨me est dÃ©jÃ  chargÃ© en mÃ©moire, il le rÃ©initialise
	 * 
	 * @param problemName : chemin/nom du fichier a compiler (extention incluse)
	 */
    public void readProblem(String problemName, boolean flag_learnup, boolean flag_allInOne){
    	ArrayList<String> list=new ArrayList<String>();
    		list.add(problemName);
    	readProblem(list, flag_learnup, flag_allInOne);
    }

    /**
     * procÃ©dure automatisÃ©e de chargement de probleme.
	 * Si le problÃ¨me n'a jamais Ã©tÃ© compilÃ©, il est compilÃ© est sauvegardÃ©.
	 * Si le problÃ¨me a dÃ©jÃ  Ã©tÃ© compilÃ©, mais n'est pas chargÃ© en mÃ©moire, il le charge.
	 * Si le problÃ¨me est dÃ©jÃ  chargÃ© en mÃ©moire, il le rÃ©initialise
     * 
     * @param problemName : chemin/nom des fichiers a compiler (extention incluse)
     */
    public void readProblem(ArrayList<String> problemName, boolean flag_learnup, boolean flag_allInOne){
    	String filename=""; 
    	for(int i=0; i<problemName.size(); i++)		
    		filename+=problemName.get(i)+"_";

    			
    	if(x==null || inX.compareTo(filename)!=0){
    			File f=new File(filename+"_compiled.dot");
    			if(f.canRead()){
    				System.out.println("lecture du fichier compilÃ© \""+filename+"_compiled.dot\"");
    				this.chargement(filename+"_compiled", 0);
    				inX=filename;
    				}else{
    					System.out.println("compilation (attention, cette operation peut prendre plusieurs minutes)");																				//sinon heuristique 5
    					procedureCompilation(problemName, true,  3, 2, "", (filename+"_compiled"), true, true, flag_learnup, flag_allInOne, 0);
    					inX=filename;
    				}
    			}else{
    			//	System.out.println("RÃ©initialisation du problÃ¨me");
    				x.deconditionerAll();
    			}
    	}
    
    	public void propagationOnRemovedVar() {
    		x.uht.copieToNull();
			for(Var v:cn.getVar()) {
				if(!v.inGraph) {
					v.consValTofalse();
					for(int i=0; i<v.domain; i++) {
						//v.consVal[i]=x.modelChecking_init(v.graphPerDomainvalue[i]);
						v.consVal[i]=x.modelChecking_init(v.graphLearned, i);
					}
				}
			}
			
			for(Var v:cn.getRemovedVar()) {
				if(!v.inGraph) {
					v.consValTofalse();
					for(int i=0; i<v.domain; i++) {
						//v.consVal[i]=x.modelChecking_init(v.graphPerDomainvalue[i]);
						v.consVal[i]=x.modelChecking_init(v.graphLearned, i);
					}
				}
			}
    	}
    
    	/**
    	 * propagation complette.
    	 * cette methode doit Ãªtre appelÃ©e apres les fonctions readProblem() compilation() reinitializeInState() et reinitialisation()
    	 */
		public void propagation(){
			x.minMaxConsistance();
			
			propagationOnRemovedVar();
		}

		//public void assignAndPropagate(String var, String val){
    	protected void assignAndPropagateNoMaj(String var, String val){
    		if(!isPresentInCurrentDomain(var, val))
    			System.out.println(val+" non presente dans "+var+". aucune operation effectue...");
    		else{
	    		Var v=cn.getVar(var);
	    		if(v.inGraph) {
	    			x.conditioner(v, v.conv(val));
	    		}else {
	    			//x.merge(v.graphPerDomainvalue[v.conv(val)]);
	    			x.merge(v.graphLearned, v.conv(val));
	    			v.consValTofalse();
	    			v.consVal[v.conv(val)]=true;
	    		}
	    		propagation();
    		}
    	}
    	
    	/**
    	 * Assign a specific value to a variable.
    	 * 
    	 * prerequis getCurrentDomainOf(var).contains(val)
    	 * 
    	 * @param var
    	 * @param val
    	 */
    	public void assignAndPropagate(String var, String val){
//    		System.out.println(var+" "+val+"------"+isPresentInCurrentDomain(var, val));
    		if(!isPresentInCurrentDomain(var, val) && !isHistorique)
    		{
    			System.out.println(val+" non presente dans "+var+". aucune operation effectue.");
    			int z = 0;
    			z = 1/z;
    		}
    		else{
	    		Var v=cn.getVar(var);
	    		if(v.inGraph) {
	    			x.conditioner(v, v.conv(val));
	    			x.minMaxConsistanceMaj(v.pos, true);
	    		}else {
	    			//x.merge(v.graphPerDomainvalue[v.conv(val)]);
	    			x.merge(v.graphLearned, v.conv(val));
	    			v.consValTofalse();
	    			v.consVal[v.conv(val)]=true;
	    			//todo opt
	    			propagation();
	    			propagationOnRemovedVar();
	    		}
    		}
  
    		
    		if(isHistorique){
    			historiqueOperations.put(var, val);
    		}
    	}
    	
    	public void assignAndPropagateTrue(String var, String val){
//    		System.out.println(var+" "+val+"------"+isPresentInCurrentDomain(var, val));
    		if(!isPresentInCurrentDomain(var, val) && !isHistorique)
    		{
    			System.out.println(val+" non presente dans "+var+". aucune operation effectue.");
    			int z = 0;
    			z = 1/z;
    		}
    		else{
	    		Var v=cn.getVar(var);
	    		if(v.inGraph) {
	    			x.conditionerTrue(v, v.conv(val));
	    			x.minMaxConsistance();
	    			x.GICup();
	    		}else {
	    			//x.merge(v.graphPerDomainvalue[v.conv(val)]);
	    			x.merge(v.graphLearned, v.conv(val));
	    			v.consValTofalse();
	    			v.consVal[v.conv(val)]=true;
	    			//todo opt
	    			propagation();
	    		}
    			propagationOnRemovedVar();
    		}
  
    		
    		if(isHistorique){
    			historiqueOperations.put(var, val);
    		}
    	}
    	
    	protected void assignAndPropagateOpt(String var, String val){
    		if(!isPresentInCurrentDomain(var, val))
    			System.out.println(val+" non presente dans "+var+". aucune operation effectue..");
    		else{
	    		Var v=cn.getVar(var);
	    		if(v.inGraph) {
					x.conditioner(v, v.conv(val));
					x.minMaxConsistanceMajopt(v.pos, true);
	    		}else {
	    			//x.merge(v.graphPerDomainvalue[v.conv(val)]);
	    			x.merge(v.graphLearned, v.conv(val));
	    			v.consValTofalse();
	    			v.consVal[v.conv(val)]=true;
	    			propagationOnRemovedVar();

	    		}
				
				//x.GICup();
    		}
    	}
    	
    	protected void unassignAndRestoreNoMaj(String var){
    		Var v=cn.getVar(var);
    		x.deconditioner(v);
    		x.minMaxConsistance();
    	}
    	
    	/**
    	 * Unassign a specific variable
    	 * 
    	 * @param var
    	 */
    	public void unassignAndRestore(String var){
    		Var v=cn.getVar(var);
    		x.deconditioner(v);
    		x.minMaxConsistanceMaj(v.pos, false);
    		
    		if(isHistorique){
    			historiqueOperations.remove(var);
    		}
    	}
    	
    	protected void unassignAndRestoreOpt(String var){
    		Var v=cn.getVar(var);
    		x.deconditioner(v);
    		x.minMaxConsistanceMajopt(v.pos, false);
    	}

    	/**
    	 * Get the minimal price of the configurations compatible with the current
    	 * choices.
    	 * 
    	 * @return the cost of the configuration
    	 */
    	public int minCost(){
    		return (int)x.min.getvaldouble();
    	}

    	/**
    	 * Provide a full configuration of minimal cost.
    	 * 
    	 * @return a full assignment var->value of minimal cost (given by {@link #minCost()}
    	 */
    	public Map<String, String> minCostConfiguration(){
    		return x.minCostConfiguration();
    	}

    	/**
    	 * Get the maximal price of the configurations compatible with the current
    	 * choices.
    	 * 
    	 * @return the cost of the configuration
    	 */
    	public int maxCost(){
    		return (int)x.max.getvaldouble();
    	}    	
    	/**
    	 * Provide a full configuration of maximal cost.
    	 * 
    	 * @return a full assignment var->value of maximal cost (given by {@link #maxCost()}
    	 */
    	public Map<String, String> maxCostConfiguration(){
    		return x.maxCostConfiguration();
    	}

    	/**
    	 * getSizeOfCurrentDomain(var) == getCurrentDomainOf(var).size()
    	 */
    	public int getSizeOfCurrentDomainOf(String var){
    		return cn.getVar(var).consistenceSize();    		
    	}
    	
    	/**
    	 * getSizeOfDomain(var) == getDomainOf(var).size()
    	 */
    	public int getSizeOfDomainOf(String var){
    		return cn.getVar(var).getDomainSize();
    	}


    	/**
    	 * isCurrentInCurrentDomain(var,val)==getCurrentDomainOf(var).contains(val)
    	 *      
    	 * @param var
    	 * @param val
    	 * @return true si la valeur val appartient au domain courant de la variable var
    	 */
    	public boolean isPresentInCurrentDomain(String var, String val){
    		Var v=cn.getVar(var);
    		if(v.conv(val) == -1)
    			return false;
    		return v.consVal[v.conv(val)];
    	}

    	public ArrayList<String> getCurrentDomainOf(String var){
    		ArrayList<String> s=new ArrayList<String>();
    		Var v=cn.getVar(var);
    		for(int i=0; i<v.domain; i++){
    			if(v.consVal[i])
    				s.add(v.valeurs.get(i));
    			
    		}
    		return s;
    	}
    	
    	public Set<String> getDomainOf(String var){
    		Set<String> s=new HashSet<String>();
    		if(x!=null){
	    		Var v=cn.getVar(var);
	    		for(int i=0; i<v.domain; i++){
	    			s.add(v.valeurs.get(i));
	    		}
    		}
    		return s;
    	}

    	/**
    	 * Retrieve for each valid value of the variable the minimal cost of the
    	 * configuration.
    	 * 
    	 * @param var
    	 *            a variable id
    	 * @return a map value->mincost
    	 */
    	public Map<String, Integer> minCosts(String var){
    		Var v=cn.getVar(var);
    		Map<String, Integer> m;
    		m=x.minCosts(v.pos);
    		x.minMaxConsistanceMaj(v.pos, true);
    		return m;
    	} 

    	/**
    	 * Retrieve for each valid value of the variable the maximal cost of the
    	 * configuration.
    	 * 
    	 * @param var
    	 *            a variable id
    	 * @return a map value->maxcost
    	 */
    	public Map<String, Integer> maxCosts(String var){
    		Var v=cn.getVar(var);
    		Map<String, Integer> m;
    		m=x.maxCosts(v.pos);
    		x.minMaxConsistanceMaj(v.pos, true);
    		return m;
    	} 

    	/**
    	 * Get all unassigned variables.
    	 * 
    	 * @return a set of non assigned variables.
    	 */
    	public ArrayList<String> getFreeVariables(){
    		ArrayList<String> s=new ArrayList<String>();
    		for(int i=0; i<cn.nbVariables; i++){
    			if(cn.getVar().get(i).consistenceSize()>1)
    				s.add(cn.getVar().get(i).name);
    		}
    		return s;
    	}

    	/**
    	 * Check that there is no more choice for the user.
    	 * 
    	 * @return true iff there is exactly one value left per variable.
    	 */
    	public boolean isConfigurationComplete(){
    		return getFreeVariables().size()==0;	
    	}

    	/**
    	 * Check there there is at least one value in each domain. Note that
    	 * depending of the level of consistency used, the configuration may of may
    	 * not be finally consistent.
    	 * 
    	 * @return true iff there is at least one value left per variable.
    	 */
    	public boolean isPossiblyConsistent(){
    		System.out.println("m&m : "+x.max.getvaldouble()+" "+x.min.getvaldouble());
    		return x.min.getvaldouble()!=-1;
    	}
	
    	protected void infos(String var){
    		Var v=cn.getVar(var);
    		x.countingpondereOnFullDomain(v);
    	}
    	
    	public void updatePassage(Map<String, String> mapVarnameDom){
    		x.updatePassage(mapVarnameDom);
    	} 
    	
    	public void postTreatments(boolean allInOne, boolean verbose) {
    		long start= System.currentTimeMillis();
    		forgetOnlychildVariables(allInOne, verbose);
    		long end= System.currentTimeMillis();
			//System.out.println("postTreatments time : "+(end-start)/1000+","+(end-start)%1000 + "s ");

    	}
    	
    	public void forgetOnlychildVariables(boolean allInOne, boolean verbose) {
    		//forgetting
    		/*ArrayList<Integer> listOnlyChild;
    		listOnlyChild=x.getOnlyChildParents(verbose);
    		for(int i:listOnlyChild) {
    			x.forgetOnlyChildParents(i);
    		}*/
    		
    		propagation();
    		
    		
    		ArrayList<Integer> listOnlyChild;
    		listOnlyChild=x.getOnlyChildParents(verbose);
    		//System.out.print("start forgetOnlychildVariables" + " ");
    		//for(int i=0; i<listOnlyChild.size(); i++) {
    		//	System.out.print(x.variables.get(listOnlyChild.get(i)).name+" ");
    		//}
    		//System.out.println();
    		
        	UniqueHashTable newUht = new UniqueHashTable(x.variables.size());

    		
    		Var v;
    		for(int i=0; i<listOnlyChild.size(); i++) {
    			v=x.variables.get(listOnlyChild.get(i));
    			//x.learnUp_all(v, null, true);
    			if(allInOne)
    				v.saveLearnedGraph(x.learnUp_all(v, null, newUht));
    			else
    				v.saveLearnedGraph(x.learnUp_all(v, null, null));

        		v.inGraph=false;
        		
    			x.forgetOnlyChildParents(listOnlyChild.get(i));

    		}
    		
    		//forgetting
    		
    		//for(int i:listOnlyChild) {
    		//	x.forgetOnlyChildParents(i);	
    		//}
    	}
    	
    	public void updateVarNotInGraph() {
    		//for(Var v : cn.vars)
    	}
    	
    	public void countRemovedVar(boolean allInOne) {
    		int sizeN=0;
    		int sizeA=0;
    		
			for(Var v:cn.getVar()) {
				if(!v.inGraph) {
					sizeN+=v.graphLearned.uht.size();
					sizeA+=v.graphLearned.uht.sizeArcs();
					if(allInOne)
						break;
				}
			}
			//System.out.print("taille nodes : "+sizeN+"   ");
			//System.out.print("taille arcs : "+sizeA);
    		System.out.println("satellites " + sizeN +" ("+ sizeA + ")");

    	}
    	
    	public void setWeight(int weight) {
    		x.setWeight(weight);
    	}
   
}