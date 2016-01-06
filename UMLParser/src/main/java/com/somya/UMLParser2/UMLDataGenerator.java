package com.somya.UMLParser2;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.sourceforge.plantuml.SourceStringReader;

public class UMLDataGenerator {
	private Map<String, ClassInfoData> classNameToObjMap = new HashMap<String, ClassInfoData>();

	public UMLDataGenerator(Map<String, ClassInfoData> classNameToObjMap) {
		this.classNameToObjMap = classNameToObjMap;
	}

	public String populateUMLFormat() {
		String umlBody = insertStartTag();
		umlBody += "\n";

		for (Map.Entry<String, ClassInfoData> entry : classNameToObjMap.entrySet()) {
			String className = entry.getKey();
			ClassInfoData ci = entry.getValue();
			umlBody += formExtendsList(className, ci);
			umlBody += "\n";
			umlBody += formImplementsList(className, ci);
			umlBody += "\n";
			umlBody += formAssociationList(className, ci, classNameToObjMap);
			umlBody += "\n";
			umlBody += formDependencyList(className, ci);
			umlBody += "\n";
		}

		for (Map.Entry<String, ClassInfoData> entry : classNameToObjMap.entrySet()) {
			String className = entry.getKey();
			ClassInfoData ci = entry.getValue();
			umlBody += generateClassBody(className, ci);
		}

		umlBody += inserEndTag();
		return umlBody;
	}

	public void generateClassDiag(String umlFormat, String umlImagePath) throws FileNotFoundException {
		OutputStream png = new FileOutputStream(umlImagePath);
		SourceStringReader reader = null;
		reader = new SourceStringReader(umlFormat);

		try {
			String desc = reader.generateImage(png);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean isVariablePublic(String name, ClassInfoData ci) {
		int c = 0;
		Iterator<MethodInfo> itr = ci.m_obj.iterator();
		while (itr.hasNext()) {
			MethodInfo m = itr.next();
			if (m.methodName.toLowerCase().equals("get" + name.toLowerCase())) {
				c++;
			}
			if (m.methodName.toLowerCase().equals("set" + name.toLowerCase())) {
				c++;
			}
		}
		
		if (c >= 2) {
			// kind  of hacky way to remove the getters and setters
			itr = ci.m_obj.iterator();
			while (itr.hasNext()) {
				MethodInfo m = itr.next();
				if (m.methodName.toLowerCase().equals("get" + name.toLowerCase())) {
					itr.remove();
				}
				if (m.methodName.toLowerCase().equals("set" + name.toLowerCase())) {
					itr.remove();
				}
			}
		}
		
		return c >= 2;
	}

	private String generateClassBody(String className, ClassInfoData ci) {
		String src = "";
		// If this field is false, then it is a class, else interface
		if (ci.isInterface == false) {
			src = "\nclass " + className + "{\n";
		} else {
			src = "\ninterface " + className + "{\n";
		}
		// Extract the variable Information
		for (int i = 0; i < ci.v_obj_fil.size(); i++) {
			if (ci.v_obj_fil.get(i).scope == "public" || isVariablePublic(ci.v_obj_fil.get(i).varName, ci)) {
				src = src + "+" + ci.v_obj_fil.get(i).varName + ":";
				src += ci.v_obj_fil.get(i).dataType;
				src += "\n";
			} else if (ci.v_obj_fil.get(i).scope == "private") {
				src = src + "-" + ci.v_obj_fil.get(i).varName + ":";
				src += ci.v_obj_fil.get(i).dataType;
				src += "\n";
			}
		}

		// Extract the method information
		for (int j = 0; j < ci.m_obj.size(); j++) {
			
			if (ci.m_obj.get(j).scope == "public") {
				src = src + "+" + ci.m_obj.get(j).methodName;
			} 
//				else if (ci.m_obj.get(j).scope == "private"){
//				src = src + "-" + ci.m_obj.get(j).methodName;
//			}
			
			if ((ci.m_obj.get(j).scope == "public") /*|| (ci.m_obj.get(j).scope == "private")*/ ){
				// checking for parameters
				if (ci.m_obj.get(j).params.size() == 0) {
					src += "()" + ":"+ ci.m_obj.get(j).returnType;
				} else {
					src += "(";
					for (int id = 0; id < ci.m_obj.get(j).params.size(); id++) {
						src += ci.m_obj.get(j).params.get(id).paramName + ":";
						src += ci.m_obj.get(j).params.get(id).returnType;
						if (id != ci.m_obj.get(j).params.size() - 1) {
							src += ",";
						}
					}
					src += ")"+ ci.m_obj.get(j).returnType ;
				}
				src += "\n";
			}
		}

		src += "\n}";
		return src;
	}

	private String insertStartTag() {
		String src = "@startuml\n";
		src += "skinparam nodesep 100\n";
		src += "skinparam ranksep 500\n";
		return src;
	}

	private String inserEndTag() {
		return "\n@enduml";
	}

	private String formExtendsList(String className, ClassInfoData ci) {

		if (ci.extendsList.size() == 0) {
			return "";
		} else {
			String src = "";
			Iterator<String> it = ci.extendsList.iterator();
			while (it.hasNext()) {
				src += it.next();
				src += " <|-- ";
				src += className;
			}
			return src;
		}
	}

	private String formImplementsList(String className, ClassInfoData ci) {
		if (ci.implementsList.size() == 0) {
			return "";
		} else {
			String src = "";
			Iterator<String> it = ci.implementsList.iterator();
			while (it.hasNext()) {
				src += it.next();
				src += " <|.. ";
				// src += " ()- ";
				src += className;
				src += "\n";
			}
			return src;
		}
	}

	private String generateMultiplicity(String first_class, String second_class, Pair<Integer, Integer> p) {
		String src = "";
		if (p == null) {
			return src;
		}
		Integer lb = p.first;
		Integer up = p.second;
		if (lb == up) {
			if (lb == 0 || lb == 1) {
				src += "\"" + lb.toString() + "\"" + " ";
			} else {
				src += "\"" + "*" + "\"" + " ";
			}
		}
		// lb and up are not same, so put two different values
		else {
			if (lb >= 999) {
				src += "\"" + "*" + ".." + up.toString() + "\"" + " ";
			} else if (up >= 999) {
				src += "\"" + lb.toString() + ".." + "*" + "\"" + " ";
			} else if (lb >= 999 && up >= 999) {
				src += "\"" + "*" + ".." + "*" + "\"" + " ";
			} else {
				src += "\"" + lb.toString() + ".." + up.toString() + "\"" + " ";
			}
		}
		return src;
	}

	private String generateBiDirectionRelationString(String first_class, String second_class,
			Map<String, ClassInfoData> globalMap) {
		ClassInfoData first_c = globalMap.get(first_class);
		ClassInfoData second_c = globalMap.get(second_class);
		String src = first_class + " "
				+ generateMultiplicity(second_class, first_class, second_c.associationsList.get(first_class));		// for multiplicity, we give the target class first
		src += "-";
		src += generateMultiplicity(first_class, second_class, first_c.associationsList.get(second_class)) + " "
				+ second_class;
		src += "\n";
		return src;
	}

	private String formAssociationList(String className, ClassInfoData ci, Map<String, ClassInfoData> globalMap) {
		if (ci.associationsList.size() == 0) {
			//System.out.println("Association List is empty for class:" + className);
			return "";
		} else {
			String src = "";
			for (Map.Entry<String, Pair<Integer, Integer>> entry : ci.associationsList.entrySet()) {
				String second_class = entry.getKey();
				src += generateBiDirectionRelationString(className, second_class, globalMap);
				// delete the association from the other class since
				// bidirectional association has already been taken care of
				globalMap.get(second_class).associationsList.remove(className);
			}
			return src;
		}
	}

	private String formDependencyList(String className, ClassInfoData ci) {
		if (ci.dependencyList.size() == 0) {
			return "";
		} else {
			String src = "";
			Iterator<String> it = ci.dependencyList.iterator();
			while (it.hasNext()) {
				src += className;
				src += " .> ";
				src += it.next();
				src += "\n";
			}
			return src;
		}
	}
}
