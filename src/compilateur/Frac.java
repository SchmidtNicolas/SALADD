/*package br4cp;


public class Frac {
	long d,n;
	
	public Frac(long nume, long deno){
		n=nume;
		d=deno;
		
		reduce();
		
	}
	
	public Frac add(Frac f){
		
		Frac res=new Frac(1,1);
		
		res.n=d*f.n+n*f.d;
		res.d=d*f.d;
		
		res.reduce();
		
		return res;
	}
	
	public Frac mult(Frac f){

		Frac res=new Frac(1,1);
		
		res.d=d*f.d;
		res.n=n*f.n;
		
		res.reduce();
		
		return res;
	}
	
	public Frac sous(Frac f){

		
		Frac res=new Frac(1,1);
		
		res.n=n*f.d-d*f.n;
		res.d=d*f.d;
		
		res.reduce();
		
		return res;
	}
	
	public Frac div(Frac f){

		
		Frac res=new Frac(1,1);
		
		res.d=d*f.n;
		res.n=n*f.d;
		
		res.reduce();
		
		return res;
	}
	
	public Frac max(Frac f){
		Frac res=new Frac(1,1);
		
		if((double)n/d < (double)f.n/f.d ){
			res.d=f.d;
			res.n=f.n;
		}else{
			res.d=d;
			res.n=n;
		}
		
		return res;
	}
	
	public Frac min(Frac f){
		Frac res=new Frac(1,1);
		
		if((double)n/d > (double)f.n/f.d ){
			res.d=f.d;
			res.n=f.n;
		}else{
			res.d=d;
			res.n=n;
		}
		
		return res;
	}
	
	public long pgcd(){
		long a,b,c;
		
		if(d==0 || n==0)
			return 1;
		
		if(d>n){
			a=d; b=n;
		}else{
			a=n; b=d;
		}
		
		while (a!=1 && b!=1){
			if(a%b==0)
				return b;
			else{
				c=a%b;
				a=b;
				b=c;
			}
		}
		return 1;
	}
	
	public void reduce(){
		if (d<-1 || n<-1){
			System.out.println("@frac : depasse long n="+n+" d="+d);
		}
		
		if(n==0){
			d=1;
		}
		
		
		long a;
		
		a=pgcd();
		while(a!=1){
			n=n/a;
			d=d/a;
			a=pgcd();
		}
	}
	
	public double val(){
		return (double)n/d;
	}
	
	public boolean equal(Frac f){
		if(f.d==d && f.n==n)
			return true;
		else
			return false;
	}
	
	public String ts(){
		return(n+"/"+d);
	}
	
	public Frac copie(){
		return new Frac(n, d);
	}
}*/

/***********************/

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

package compilateur;

import java.math.BigDecimal;
//import java.math.BigInteger;


public class Frac {
	private BigDecimal d,n;
	
	public Frac(long nume, long deno){
		n=new BigDecimal(Long.toString(nume));
		d=new BigDecimal(Long.toString(deno));
		
		reduce();
		
	}
	
	public Frac(BigDecimal nume, BigDecimal deno){
		n=nume;
		d=deno;
		
		reduce();
		
	}
	
	public Frac add(Frac f){
		
		Frac res=new Frac(1,1);
		
		res.n=d.multiply(f.n).add(n.multiply(f.d));
		res.d=d.multiply(f.d);
		
		res.reduce();
		
		return res;
	}
	
	public Frac mult(Frac f){

		Frac res=new Frac(1,1);
		
		res.d=d.multiply(f.d);
		res.n=n.multiply(f.n);
		
		res.reduce();
		
		return res;
	}
	
	public Frac sous(Frac f){

		
		Frac res=new Frac(1,1);
		
		res.n=n.multiply(f.d).subtract(d.multiply(f.n));
		res.d=d.multiply(f.d);
		
		res.reduce();
		
		return res;
	}
	
	public Frac div(Frac f){

		
		Frac res=new Frac(1,1);
		
		res.d=d.multiply(f.n);
		res.n=n.multiply(f.d);
		
		res.reduce();
		
		return res;
	}
	
	public Frac max(Frac f){
		Frac res=new Frac(1,1);
		
		if((n.doubleValue()/d.doubleValue()) < (f.n.doubleValue()/f.d.doubleValue()) ){
			res.d=f.d;
			res.n=f.n;
		}else{
			res.d=d;
			res.n=n;
		}
		
		return res;
	}
	
	public Frac min(Frac f){
		Frac res=new Frac(1,1);
		
		if(this.val() > f.val() ){
			res.d=f.d;
			res.n=f.n;
		}else{
			res.d=d;
			res.n=n;
		}
		
		return res;
	}
	
	public BigDecimal pgcd(){
		BigDecimal a,b,c;
		
		if(d.compareTo(BigDecimal.ZERO)==0 || n.compareTo(BigDecimal.ZERO)==0)
			return BigDecimal.ONE;
		
		if(d.compareTo(n)>0){
			a=d; b=n;
		}else{
			a=n; b=d;
		}
		
		while (a.compareTo(BigDecimal.ONE)!=0 && b.compareTo(BigDecimal.ONE)!=0){
			if(a.remainder(b).compareTo(BigDecimal.ZERO)==0)
				return b;
			else{
				c=a.remainder(b);
				a=b;
				b=c;
			}
		}
		return BigDecimal.ONE;
	}
	
/*	while (a!=1 && b!=1){
		if(a%b==0)
			return b;
		else{
			c=a%b;
			a=b;
			b=c;
		}
	}
	return 1;*/
	
	public void reduce(){
		if (d.compareTo(BigDecimal.ONE.negate())<0 || n.compareTo(BigDecimal.ONE.negate())<0){
			System.out.println("@frac : depasse long n="+n+" d="+d);
		}
		
		if(n.compareTo(BigDecimal.ZERO)==0){
			d=new BigDecimal("1");
		}
		
		
		BigDecimal a;
		
		a=new BigDecimal(pgcd().toString());
		while(a.compareTo(BigDecimal.ONE)!=0){
			n=n.divide(a);
			d=d.divide(a);
			a=new BigDecimal(pgcd().toString());
		}
	}
	
	public double val(){
		return n.doubleValue()/d.doubleValue();
	}
	
	public boolean equal(Frac f){
		if(f.d==d && f.n==n)
			return true;
		else
			return false;
	}
	
	public String ts(){
		return(n+"/"+d);
	}
	
	public Frac copie(){
		return new Frac(new BigDecimal(n.toString()), new BigDecimal(d.toString()));
	}
	
	public void toOne(){
		n=new BigDecimal("1");
		d=new BigDecimal("1");
	}
	
	public void toOneMoins(){
		n=new BigDecimal("1");
		n=n.negate();
		d=new BigDecimal("1");
	}
}


/******************/
/*
package br4cp;
import java.math.BigDecimal;


public class Frac {
	long d,n;
	BigDecimal v;
	
	public Frac(long nume, long deno){
		
		n=nume;
		d=deno;
		
		reduce();
		
	}
	
	public Frac add(Frac f){
		
		Frac res=new Frac(1,1);
		
		res.n=d*f.n+n*f.d;
		res.d=d*f.d;
		
		res.reduce();
		
		return res;
	}
	
	public Frac mult(Frac f){

		Frac res=new Frac(1,1);
		
		res.d=d*f.d;
		res.n=n*f.n;
		
		res.reduce();
		
		return res;
	}
	
	public Frac sous(Frac f){

		
		Frac res=new Frac(1,1);
		
		res.n=n*f.d-d*f.n;
		res.d=d*f.d;
		
		res.reduce();
		
		return res;
	}
	
	public Frac div(Frac f){

		
		Frac res=new Frac(1,1);
		
		res.d=d*f.n;
		res.n=n*f.d;
		
		res.reduce();
		
		return res;
	}
	
	public Frac max(Frac f){
		Frac res=new Frac(1,1);
		
		if((double)n/d < (double)f.n/f.d ){
			res.d=f.d;
			res.n=f.n;
		}else{
			res.d=d;
			res.n=n;
		}
		
		return res;
	}
	
	public Frac min(Frac f){
		Frac res=new Frac(1,1);
		
		if((double)n/d > (double)f.n/f.d ){
			res.d=f.d;
			res.n=f.n;
		}else{
			res.d=d;
			res.n=n;
		}
		
		return res;
	}
	
	public long pgcd(){
		long a,b,c;
		
		if(d==0 || n==0)
			return 1;
		
		if(d>n){
			a=d; b=n;
		}else{
			a=n; b=d;
		}
		
		while (a!=1 && b!=1){
			if(a%b==0)
				return b;
			else{
				c=a%b;
				a=b;
				b=c;
			}
		}
		return 1;
	}
	
	public void reduce(){
		if (d<-1 || n<-1){
			System.out.println("@frac : depasse long n="+n+" d="+d);
		}
		
		if(n==0){
			d=1;
		}
		
		
		long a;
		
		a=pgcd();
		while(a!=1){
			n=n/a;
			d=d/a;
			a=pgcd();
		}
	}
	
	public double val(){
		return (double)n/d;
	}
	
	public boolean equal(Frac f){
		if(f.d==d && f.n==n)
			return true;
		else
			return false;
	}
	
	public String ts(){
		return(n+"/"+d);
	}
	
	public Frac copie(){
		return new Frac(n, d);
	}
}*/
