//TODO: A class can only extend one class, so remove the implements list
//TODO: Add support for LIST, SET, UNORDERED SET, MAP and QUEUE
//TODO: Check on lollipop interfaces from Plant UML

package com.somya.UMLParser2;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ParseException;

public class UMLParserDriver {

	public static void main(String [] args) throws ParseException, IOException {
		if (args.length < 2) {
            System.out.println("no or very few arguments were given.");
            return;
        }
		
		//String classPath = args[0];
		//String umlImagePath = args[1];

		String classPath = "E:\\EclipseWorkspace\\OnlineShoppingCommand\\src";
		String umlImagePath = "E:\\EclipseWorkspace\\OnlineShoppingCommand\\src\\class.png";

		//obtain all the java files path in a list
		JavaFilesIterator iter = new JavaFilesIterator(classPath);
		List<String> javaFilePaths = new ArrayList<String>();
		javaFilePaths = iter.iterateClassFolder();
		
		ClassInfoData cinfo = new ClassInfoData();
		Map<String, ClassInfoData> classNameToObjMap = new HashMap <String, ClassInfoData>();
		
		for (int i = 0; i < javaFilePaths.size(); i++) {
			String filePath = javaFilePaths.get(i);			
			JavaFileParser jp_obj = new JavaFileParser(filePath);
			ClassInfoData c_obj = new ClassInfoData();
			
			c_obj = jp_obj.populateClassInfo();
			
			//Fill the map for className to class Info
			if (c_obj.className != null) 
				classNameToObjMap.put(c_obj.className, c_obj);
		}
		
		JavaFileParser.checkForAssociationsAndDependency(classNameToObjMap);
		for (ClassInfoData c: classNameToObjMap.values()) {
//			printClassInfo(c);
		}

		UMLDataGenerator uml = new UMLDataGenerator(classNameToObjMap);
		String umlFormat = uml.populateUMLFormat();
		//System.out.println(umlFormat);
	
		uml.generateClassDiag(umlFormat, umlImagePath);
	}
	
	static public void printClassInfo(ClassInfoData c_obj){
		//System.out.println("\n\n\nClassName:"+c_obj.className);

		System.out.println("\n\nExtends List:");
		for(String str: c_obj.extendsList) {
			//System.out.println(str);
		}

//		System.out.println("\n\nAssociation List:");
//		for(String str: c_obj.associationsList) {
//			System.out.println(str);
//		}
		
		System.out.println("\n\nImplements List:");
		for(String str: c_obj.implementsList) {
			System.out.println(str);
		}

		System.out.println("\n\nMethod Info:");
		for(int i=0; i<c_obj.m_obj.size();i++){
			System.out.println("Method Name:"+ c_obj.m_obj.get(i).methodName);
			System.out.println("Return Type:"+ c_obj.m_obj.get(i).returnType);
			System.out.println("Scope:"+ c_obj.m_obj.get(i).scope);
			for(int j=0; j< c_obj.m_obj.get(i).params.size();j++) {
				System.out.println("ParamName:"+ c_obj.m_obj.get(i).params.get(j).paramName +
						"; Param Return Type:"+ c_obj.m_obj.get(i).params.get(j).returnType);
			}
			System.out.println("-------");
			}
		
		System.out.println("\n\nAttributes Info:");
		for(int i=0; i<c_obj.v_obj_fil.size();i++){
			System.out.println("Name:"+ c_obj.v_obj_fil.get(i).varName);
			System.out.println("Return Type:"+ c_obj.v_obj_fil.get(i).dataType);
			System.out.println("Scope:"+ c_obj.v_obj_fil.get(i).scope);
			System.out.println("----------");
			}
		
		}
	}
