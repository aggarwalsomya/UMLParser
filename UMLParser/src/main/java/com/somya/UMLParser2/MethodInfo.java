package com.somya.UMLParser2;

import java.util.ArrayList;
import java.util.List;

public class MethodInfo {
	String scope;
	String returnType;
	String methodName;
	List<ParamInfo> params = new ArrayList<ParamInfo>();
	List<VarInfo> method_var = new ArrayList<VarInfo>();
}
