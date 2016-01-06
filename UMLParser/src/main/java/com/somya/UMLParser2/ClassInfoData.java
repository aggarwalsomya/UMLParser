package com.somya.UMLParser2;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ClassInfoData {
	public String className;
	public Set<String> extendsList = new HashSet<String>();
	public Set<String> implementsList = new HashSet<String>();
	public Map<String,Pair<Integer,Integer>> associationsList = new HashMap<String, Pair<Integer,Integer>>();
	public Set<String> dependencyList = new HashSet<String>();
	public List<VarInfo> v_obj_all = new ArrayList<VarInfo>();
	public Set<String> localVarTypes = new HashSet<String>();

	// filtered list of variables
	public List<VarInfo> v_obj_fil = new ArrayList<VarInfo>();	
	public List<MethodInfo> m_obj = new ArrayList<MethodInfo>();
	
	// true if its an interface
	public boolean isInterface;
}

class Pair<A, B> {
    public A first;
    public B second;

    public Pair(A first, B second) {
    	super();
    	this.first = first;
    	this.second = second;
    }
}