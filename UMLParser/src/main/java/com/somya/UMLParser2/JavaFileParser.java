package com.somya.UMLParser2;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.w3c.dom.ProcessingInstruction;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.body.VariableDeclaratorId;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class JavaFileParser extends VoidVisitorAdapter<ClassInfoData> {
	private CompilationUnit cu;
	String fp = "";

	public JavaFileParser(String filePath) throws ParseException, IOException {
		FileInputStream in = new FileInputStream(filePath);
		fp = filePath;

		// parse the file
		cu = JavaParser.parse(in);
		in.close();
	}

	public ClassInfoData populateClassInfo() {
		ClassInfoData cdata = new ClassInfoData();
		try {
			visit(cu, cdata);
		} catch (Exception e) {
			System.out.println("Got some error while parsing file: " + fp);
		}
		return cdata;
	}

	private String printModifiers(final int modifiers) {
		if (ModifierSet.isPrivate(modifiers)) {
			return "private";
		}
		if (ModifierSet.isProtected(modifiers)) {
			return "protected";
		}
		if (ModifierSet.isPublic(modifiers)) {
			return "public";
		}
		if (ModifierSet.isAbstract(modifiers)) {
			return "abstract";
		}
		if (ModifierSet.isStatic(modifiers)) {
			return "static";
		}
		if (ModifierSet.isFinal(modifiers)) {
			return "final";
		}
		if (ModifierSet.isNative(modifiers)) {
			return "native";
		}
		if (ModifierSet.isStrictfp(modifiers)) {
			return "strictfp";
		}
		if (ModifierSet.isSynchronized(modifiers)) {
			return "synchronized";
		}
		if (ModifierSet.isTransient(modifiers)) {
			return "transient";
		}
		if (ModifierSet.isVolatile(modifiers)) {
			return "volatile";
		} else {
			return "package";
		}
	}

	@Override
	public void visit(VariableDeclarationExpr n, ClassInfoData arg) {
		// here you can access the attributes of the method.
		// this method will be called for all methods in this 
		// CompilationUnit, including inner class methods
		
		arg.localVarTypes.add(n.getType().toString());
		//System.out.println("type is : " + n.getType().toString());
		super.visit(n, arg);
	}

	@Override
	public void visit(MethodDeclaration n, ClassInfoData cid) {
		MethodInfo m = new MethodInfo();

		// get method name
		m.methodName = n.getName();

		// get return type
		m.returnType = n.getType().toString();

		// get the parameters of the function in form of a list
		java.util.List<Parameter> ret = n.getParameters();
		for (Parameter elem : ret) {
			ParamInfo p = new ParamInfo();
			p.paramName = elem.getId().toString();
			p.returnType = elem.getType().toString();
			m.params.add(p);
		}
		m.scope = printModifiers(n.getModifiers());
		// Get the list of variable in method
		cid.m_obj.add(m);
		super.visit(n, cid);
	}

	@Override
	public void visit(ClassOrInterfaceDeclaration n, ClassInfoData cid) {

		// prints current class Name if its not null
		
		if (cid.className == null)
			cid.className = n.getName();
		
		//System.out.println("className="+cid.className);
		if (n.isInterface()) {
			cid.isInterface = true;
		} else {
			cid.isInterface = false;
		}

		if (n.getExtends() != null) {
			// prints its parent class. Returns List
			for (int i = 0; i < n.getExtends().size(); i++) {
				cid.extendsList.add(n.getExtends().get(i).toString());
			}
		}

		// returns list of all interfaces implemented
		if (n.getImplements() != null) {
			for (int i = 0; i < n.getImplements().size(); i++) {
				cid.implementsList.add(n.getImplements().get(i).toString());
			}
		}
		super.visit(n, cid);
	}

	@Override
	public void visit(ConstructorDeclaration n, ClassInfoData arg) {
		MethodInfo m = new MethodInfo();

		// get method name
		m.methodName = n.getName();
		m.returnType = "";

		// get the parameters of the function in form of a list
		java.util.List<Parameter> ret = n.getParameters();
		for (Parameter elem : ret) {
			ParamInfo p = new ParamInfo();
			p.paramName = elem.getId().toString();
			p.returnType = elem.getType().toString();
			m.params.add(p);
		}
		m.scope = printModifiers(n.getModifiers());

		// Get the list of variable in method
		arg.m_obj.add(m);
		super.visit(n, arg);
	}

	@Override
	public void visit(FieldDeclaration n, ClassInfoData cid) {

		for (VariableDeclarator elem : n.getVariables()) {
			VarInfo vinfo = new VarInfo();
			vinfo.varName = elem.getId().toString();
			vinfo.dataType = n.getType().toString();
			vinfo.scope = printModifiers(n.getModifiers());
			//System.out.println(vinfo.varName+ " "+ vinfo.dataType+" " +vinfo.scope);
			cid.v_obj_all.add(vinfo);
		}
	}

	static public String getActualDataTypeFromGenerics(String datatype) {
		if (datatype.startsWith("Collection")|| datatype.startsWith("ArrayList") || datatype.startsWith("List")) {
			datatype = datatype.substring(datatype.indexOf("<") + 1, datatype.indexOf(">"));
			return datatype;
		} else
			return "";

		// TODO : handle generics types also
	}

	static public void populateAssociations(ClassInfoData c_obj, Map<String, ClassInfoData> classNameToObjMap) {
		// check for variables here
		int size = c_obj.v_obj_all.size();
		for (int i = 0; i < size; i++) {
			String datatype = c_obj.v_obj_all.get(i).dataType;
			String collDatatype = getActualDataTypeFromGenerics(datatype);

			if (collDatatype.isEmpty()) { // means it was a normal data type
				//If an association to itself is there, do not add it
				if (classNameToObjMap.containsKey(datatype) && (!datatype.equals(c_obj.className))) {
					// Association exists in this case
					if (c_obj.associationsList.containsKey(datatype)) {
						Pair<Integer, Integer> val = c_obj.associationsList.get(datatype);
						val.first += 1;
						val.second += 1;
						c_obj.associationsList.put(datatype, val);
					} else {
						c_obj.associationsList.put(datatype, new Pair<Integer, Integer>(1, 1));
					}
				} else {
					c_obj.v_obj_fil.add(c_obj.v_obj_all.get(i));
				}
			} else {
				// it was a collection type datatype
				if (classNameToObjMap.containsKey(collDatatype)) {
					if (c_obj.associationsList.containsKey(collDatatype) && (!collDatatype.equals(c_obj.className))) {
						// Association exists in this case
						Pair<Integer, Integer> val = c_obj.associationsList.get(collDatatype);
						val.second = 999;
						c_obj.associationsList.put(collDatatype, val);
					} else { // no key exists till now
						c_obj.associationsList.put(collDatatype, new Pair<Integer, Integer>(0, 999));
					}
				} else {
					c_obj.v_obj_fil.add(c_obj.v_obj_all.get(i));
				}
			}
		}
	}
	
	static public void parseDependencyFromType(String datatype, ClassInfoData c_obj, Map<String, ClassInfoData> classNameToObjMap, boolean takeConcreteClassToo) {
		String collDatatype = getActualDataTypeFromGenerics(datatype);
		if (collDatatype.isEmpty()) {
			if (classNameToObjMap.containsKey(datatype) && (takeConcreteClassToo || classNameToObjMap.get(datatype).isInterface)) {
				c_obj.dependencyList.add(datatype);
				// remove as association
//				c_obj.associationsList.remove(datatype);
			}
		} else {
			// check if dependency is an interface or not.
			if (classNameToObjMap.containsKey(collDatatype)
					&& (takeConcreteClassToo || classNameToObjMap.get(collDatatype).isInterface)) {
				c_obj.dependencyList.add(collDatatype);
				// remove as association
//				c_obj.associationsList.remove(collDatatype);
			}
		}
	}

	static public void populateDependencies(ClassInfoData c_obj, Map<String, ClassInfoData> classNameToObjMap) {
		if (c_obj.isInterface)
			return;
		// check for method params here
		for (int i = 0; i < c_obj.m_obj.size(); i++) {
			for (int j = 0; j < c_obj.m_obj.get(i).params.size(); j++) {
				parseDependencyFromType(c_obj.m_obj.get(i).params.get(j).returnType, c_obj, classNameToObjMap, false);
			}
		}
		
		// check local vars also for dependencies
		for (String type: c_obj.localVarTypes) {
			parseDependencyFromType(type, c_obj, classNameToObjMap, true);
		}
	}
	
	static public void checkForAssociationsAndDependency(Map<String, ClassInfoData> classNameToObjMap) {
		// iterating over classObjects - Variables, methods
		for (ClassInfoData c_obj : classNameToObjMap.values()) {
			populateAssociations(c_obj, classNameToObjMap);
			populateDependencies(c_obj, classNameToObjMap);
		}
	}
}
