package compilateur;

import java.util.ArrayList;
import java.util.Collections;

public class Tuple {
	public ArrayList<Integer> tuple;
	public Structure poid;
	
	public Tuple() {
		tuple=new ArrayList<Integer>();
	}
	public Tuple(ArrayList<Integer> t, Structure p) {
		tuple=t;
		poid=p;
	}
	
	public void add(Integer element) {
		tuple.add(element);
	}
	
	public Tuple Copie() {
		Tuple t=new Tuple((ArrayList<Integer>)tuple.clone(), poid.copie());
		return t;
	}
	
	public boolean equal(Tuple t) {
		if(t.tuple.size()!=this.tuple.size())
			return false;
		
		for(int i=0; i<this.tuple.size(); i++) {
			if(!this.tuple.get(i).equals(t.tuple.get(i))) {
				return false;
			}
		}
		
		return true;
	}
	
	public void swap(int i, int j) {
		Collections.swap(tuple, i, j);
	}
	
	public void set(int index, Integer element) {
		tuple.set(index, element);
	}
	
}
