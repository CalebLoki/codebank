package com.jfbank.cicada.utils.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;


public class ExportDataToTextUtil {

  public static String toText(LinkedList<LinkedHashMap<String, Object>> list, String[] titleName) {

    Consoleprinttable c = new Consoleprinttable(list.get(0).size(), true);
    
    LinkedHashMap<String, Object> lhm=null;
    c.appendRow();
    c.appendColum(list.size());
    
    c.appendRow();
    for (int i = 0; i < titleName.length; i++) {
      c.appendColum(titleName[i]);
    }
    
    for(int i=0; i< list.size(); i++){
      c.appendRow();
      
      int mapSize=list.get(i).size();
      lhm=list.get(i);
      for(int j=0; j<mapSize; j++){
        
        c.appendColum(lhm.get(titleName[j]));
        /*if(list.get(i).equals(titleName[j])){
          
          c.appendColum(list.get(i).get(titleName[j]).toString());
          
        }*/
      }
    }
    c.appendRow();
    c.appendColum("END");
    System.out.println(c);
   return c.toString();
  }
  
  
  public static <T> LinkedHashMap<String,Object> beanToMap(T t){
    
    ObjectMapper mapper= new ObjectMapper();
    
    StringWriter sw= new StringWriter();
    
    LinkedHashMap< String, Object> lmap= new LinkedHashMap<String, Object>();
    
    try {
      mapper.writeValue(sw, t);
      String str= sw.toString();
      JsonNode node= mapper.readTree(str);
     Iterator<String> it=node.getFieldNames();
      while( it.hasNext()){
       
        String key=it.next();
        String value=node.get(key).asText();
        
        lmap.put(key, value);
        
       // System.out.println("key= "+key+" value= "+value);
      }
      return lmap;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
  
  public static <T> LinkedList<LinkedHashMap<String, Object>> listToLinkedMap(List<T> list){
    
    LinkedList<LinkedHashMap<String, Object>> linkmap= new LinkedList<LinkedHashMap<String,Object>>();
    
    for (int i = 0; i < list.size(); i++) {
      linkmap.add(beanToMap(list.get(i)));
    }
    return linkmap;   
  }
  
  public static LinkedHashMap<String, Object> cleanBeanToMap(LinkedHashMap<String, Object> map,String tName){
    
    String[] nameArray= tName.split(",");
    LinkedHashMap<String, Object> newMap= new LinkedHashMap<String, Object>();
    
    for (int i = 0; i < map.size(); i++) {
      
      for (int j = 0; j < nameArray.length; j++) {
        
        if(map.containsKey(nameArray[j])){
          newMap.put(nameArray[j], map.get(nameArray[j]));
        }
        map.remove(i);
      }
    }
    return newMap;
  }
  
  public static LinkedList<LinkedHashMap<String, Object>> linkedHMapListToCelanLinkedHMapList(
      LinkedList<LinkedHashMap<String, Object>> source,String tName){
    
    LinkedList<LinkedHashMap<String, Object>> target= new LinkedList<LinkedHashMap<String,Object>>();
    
    for (int i = 0; i < source.size(); i++) {
      target.add(ExportDataToTextUtil.cleanBeanToMap(source.get(i), tName));
    }
    
    return target;
  }
  
  public static void toFile(String filePath,String content){
    try {
      FileOutputStream os = new FileOutputStream(new File(filePath), true);
      os.write((content).getBytes());
     } catch (Exception e) {
      e.printStackTrace();
     }
  }
  
  public static String numToString(Object bd){
     try {
       return new DecimalFormat("0.00").format(bd);
      } catch (Exception e) {
        e.printStackTrace();
      }
    return null;
  }
  
  public static String dateToString(Date d,String formatStr){
    try {
      return new SimpleDateFormat(formatStr).format(d);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
