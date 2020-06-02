package compilateur;

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

public class NodeDDlast extends NodeDD {	
	
	
	public NodeDDlast(){
		super(null);
		//add=new ArrayList<Structure>();
	}
	
	public NodeDDlast(int id){
		super(null, id);
		//add=new ArrayList<Structure>();
	}
	
	public boolean isLeaf(){
		return true;
	}	
	
	public int count(){
		
		if(fathers.get(0).s.printstr().compareTo("S")!=0){	//pas add
			return 1;
		}else{												//add
			int cpt=fathers.size();
			
			for(int i=0; i<fathers.size(); i++){
				for(int j=0; j<i; j++){
					if(fathers.get(i).s.equals(fathers.get(j).s)){
						cpt--;
						break;
					}
				}
			}
			return cpt;
		}
	}
	
	public String toDot(){
	
		if(fathers.size()>0){
		if(fathers.get(0).s.printstr().compareTo("S")!=0){	//pas add
	   		String s;
	   		
	   		boolean isst=false;
	   		if(fathers.get(0).s.printstr().compareTo("St")==0)
	   			isst=true;
	   		
			
			//name label form
	   		s="n"+this.id + " [label=";
	    	if(this.isLeaf()){
	    		if(isst)
	    			s+="1, shape=box";
	    		else
	    			s+="0, shape=box";
	    	}
	    	else
	    		s+=this.variable.name;//+"_"+this.id;//+"_"+this.kidsdiffbottom();
	    	
	   		s+="];\n";
	    		if(!this.isLeaf()){
	   			for(int i=(this.kids.size()-1); i>=0; i--)
	   				s+=this.kids.get(i).toDot();
	   			//for(int i=(this.fathers.size()-1); i>=0; i--)
	   			//	s+=this.fathers.get(i).toDot2();
	
	   		}
	   		//for(int i=(this.fathers.size()-1); i>=0; i--)
	    		//	s+=this.fathers.get(i).toDot(binary, true);
	   		
	   		return s;
		}else{				//cas add

			int redirect;
			Arc a;
	   		String s="";
			for(int i=0; i<fathers.size(); i++){
				redirect=-1;
				for(int j=0; j<i; j++){
					if(fathers.get(i).s.equals(fathers.get(j).s)){
						redirect=j;
						break;
					}
				}
				if(redirect==-1){	//n'existe pas, on le cree
					//////////todot node/////////
					//name label form
			   		s+="n"+this.id +"_"+((S)fathers.get(i).s).last.toDot()+ " [label="+fathers.get(i).s.toDot()+", shape=box];\n";
					/////////////////////////////
					redirect=i;
				}
				//////////todot arc ////////
						a=this.fathers.get(i);
						if(a.pere!=null){			//si c'est pas le premier arc
							s+="n" + a.pere.id + " -> n" + a.fils.id+"_"+redirect + " [pos="+a.pos;
							if(a.pos==0)
								s+= ", style=dotted";
							if(a.pos==(a.pere.variable.domain-1))
								s+= ", style=dashed";
							s+="];\n";
						}else{					// si c'est le premier arc
							s+="nada -> n" + a.fils.id+"_"+redirect + ";\n";
							s+="nada [label=\" \",shape=plaintext];\n";
						}
					
				}
			return s;
		}
		}else
			return "";
				/////////////////////////////
				
				

	}
		
}
