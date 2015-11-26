package com.jfbank.cicada.utils.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import ms.platform.base.cache.ParameterPub;
import ms.platform.base.log.MSLog;
import ms.platform.base.pub.SystemParam;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jfbank.cicada.cons.Constant;
import com.jfbank.cicada.utils.PropUtil;

/**
 * 类的描述：文件导出工具类；
 * @version 1.0
 * @author 叶跃新
 * @date 2015年11月24日
 */
public class ExportDataToTextUtil {

  /** 文件名称常量*/
  public static final String EXPORT_FILENAME_OFFER ="_offerinfo_"; //代扣回盘流水；
  public static final String EXPORT_FILENAME_VOLUNTARY ="_voluntaryinfo_"; //主动还款流水；
  public static final String EXPORT_FILENAME_PLANREPAYMENT ="_planrepayment_"; //还款计划流水；
  public static final String EXPORT_FILENAME_PLANREPAYMENTCHG ="_planrepaymentchg_"; //还款计划调整流水；
  public static final String EXPORT_FILENAME_REPAYMENTDETAIL ="_repaymentdetail_";//冲账明细流水；
  public static final String EXPORT_FILENAME_APPLICATION ="_application_";//工单变更流水；
  public static final String EXPORT_FILENAME_ADVANCE ="_advance_";//预存款流水；
  public static final String EXPORT_FILENAME_PAYBACKINFO ="_paybackinfo_";//BUS回款流水；
  public static final String EXPORT_FILENAME_OVERDUEFEE ="_overduefee_";//BUS回款流水；
  
  
  /** 文件路径常量*/
  public static PropUtil propUtil =PropUtil.getInstance();
  public static final String EXPORT_FILEPATH = propUtil.getPropertyValue( "textTmpPath" );
  
  /**
   * 函数说明：根据列名将集合转换为格式化文本；
   * @param list
   *          需要转换的结果集；
   * @param titleName
   *          需要转换的列名；
   * @return 格式化后的文本；
   * @author 叶跃新
   * @date 2015年11月24日
   * @throws Exception
   */
  public static String toText(LinkedList<LinkedHashMap<String, Object>> list, String[] titleName) {

    try {
      //定义格式化表格对象；
      Consoleprinttable c = new Consoleprinttable(titleName.length, true);
      
      LinkedHashMap<String, Object> lhm=null;
      
      //第一行，记录文件中数据总数；
      c.appendRow();
      c.appendColum(list.size());
      
      //填充数据；
      for(int i=0; i< list.size(); i++){
        c.appendRow();
        
        lhm=list.get(i);
        for(int j=0; j<titleName.length; j++){
          c.appendColum(lhm.get(titleName[j]));
        }
      }
      
      //最后一行，结束标识符；
      c.appendRow();
      c.appendColum("END");
      
      MSLog.info(">>> 文件数据转换成功！数据结果集长度：["+list.size()+"]");
      return c.toString();
    } catch (Exception e) {
      MSLog.error("@@@集合格式化为文本时出错",e);
      return null;
    }
  }
  
  /**
   * 函数说明：将Pojo转换为Map
   * @param t
   *        需要转换的Pojo对象
   * @return 转换完后的Map；
   * @author 叶跃新
   * @date 2015年11月24日
   * @throws Exception
   */
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
      MSLog.error("@@@POJO转换为MAP时出错", e);
    }
    return null;
  }
  
  /**
   * 函数说明：对一个查询后的结果集进行键值对构建；
   * @param list
   *          需要构建的结果集；
   * @return 构建完成后的新结果集；
   * @author 叶跃新
   * @date 2015年11月24日
   * @throws Exception
   */
  public static <T> LinkedList<LinkedHashMap<String, Object>> listToLinkedMap(List<T> list){
    try {
      LinkedList<LinkedHashMap<String, Object>> linkmap= new LinkedList<LinkedHashMap<String,Object>>();
      //将集合中pojo转换为map形式；
      for (int i = 0; i < list.size(); i++) {
        linkmap.add(beanToMap(list.get(i)));
      }
      return linkmap;
    } catch (Exception e) {
      MSLog.error("@@@查询结果集转换为LinkedHashMap时出错", e);
      return null;
    }   
  }
  
  /**
   * 函数说明：根据列表字段清除文件中不需要展示的字段；
   * @param map 
   *          需要进行清除的POJO转换后的Map
   *          
   * @param nameArray
   *          需要展示的列表字段；
   *          
   * @return 清除后的Map
   * @author 叶跃新
   * @date 2015年11月24日
   * @throws Exception
   */
  public static LinkedHashMap<String, Object> cleanBeanToMap(LinkedHashMap<String, Object> map,String[] nameArray){
    
    LinkedHashMap<String, Object> newMap= new LinkedHashMap<String, Object>();
    try {
      for (int i = 0; i < map.size(); i++) {
        for (int j = 0; j < nameArray.length; j++) {
          if(map.containsKey(nameArray[j])){
            newMap.put(nameArray[j], map.get(nameArray[j]));
          }
          map.remove(i);
        }
      }
      return newMap;
    } catch (Exception e) {
        MSLog.error("@@@清除集合中未展示的字段出错", e);
        return null;
    }
  }
  
  /**
   * 函数说明：清除不需要在文件中展示的属性字段；
   * @param source
   *          源集合；
   * @param nameArray
   *          需要展示的属性字段；
   * @return 清除后的集合；
   * @author 叶跃新
   * @date 2015年11月24日
   * @throws Exception
   */
  public static LinkedList<LinkedHashMap<String, Object>> linkedHMapListToCelanLinkedHMapList(
      LinkedList<LinkedHashMap<String, Object>> source,String[] nameArray){
    
    LinkedList<LinkedHashMap<String, Object>> target= new LinkedList<LinkedHashMap<String,Object>>();
    try {
      for (int i = 0; i < source.size(); i++) {
        target.add(cleanBeanToMap(source.get(i), nameArray));
      }
      return target;
    } catch (Exception e) {
      MSLog.error("@@@清除文件中未展示字段出错", e);
      return null;
    }
  }
  
  /**
   * 函数说明：根据路径将文本转换为文件进行输出；
   * @param filePath
   *            包含文件名称的输出路径；
   * @param content 
   *            文本内容；
   * @author 叶跃新
   * @date 2015年11月24日
   * @throws Exception
   */
  public static void toFile(String filePath,String content){
    FileOutputStream os= null;
    try {
       os= new FileOutputStream(new File(filePath), true);
      os.write((content).getBytes());
      os.close();
      MSLog.info(">>>  文件导出完成！导出路径为：["+filePath+"]");
     } catch (Exception e) {
      MSLog.error("@@@数据导出生成文件时出错", e);
     }
  }
  
  /**
   * 函数说明：根据传入列名将集合格式化为文本；
   * @param list
   *          需要格式话的集合；
   * @param titlName
   *          文件表格列名；
   * @return 格式化完成后的文本；
   * @author 叶跃新
   * @date 2015年11月24日
   * @throws Exception
   */
  public static<T> String beanListToString(List<T> list,String titlName){
    try {
      MSLog.info(">>> 文件数据导出接口调用成功，开始进行数据转换..."+
      "列名：["+titlName+"] ,数据结果集长度：["+list.size()+"]");
      
      String[] strArray= titlName.split(",");
      
      //将集合进行处理，构建由集合中对象的属性及其属性值组成的新集合；
      LinkedList<LinkedHashMap<String, Object>> lmap= listToLinkedMap(list);
      
        //清除不需要展示在文本文件中的属性；
        lmap=linkedHMapListToCelanLinkedHMapList(lmap, strArray);
        
        return toText(lmap, strArray);
    } catch (Exception e) {
      MSLog.error("@@@构建需格式化为文本的集合时出错，未获取到需构建的集合");
      return null;
    }
  }
  
  /**
   * 函数说明：数字格式化工具，用于对任意类型的金额数据进行格式化；
   * @param bd
   *         需要格式化的数字对象；
   * @return 指定格式的数字对象；
   * @author 叶跃新
   * @date 2015年11月23日
   * @throws Exception
   */
  public static String numToString(Object bd){
     try {
       return new DecimalFormat("0.00").format(bd);
      } catch (Exception e) {
        MSLog.error("@@@金额格式化异常，请检查必填字段是否为空；",e);
        return null;
      }
  }
  
  /**
   * 函数说明：数字格式化工具，用于对任意类型的金额数据进行格式化；
   * @param bd
   *         需要格式化的数字对象；
   * @param numFormat
   *         需要定义的格式类型；
   * @return 指定格式的数字对象；
   * @author 叶跃新
   * @date 2015年11月23日
   * @throws Exception
   */
  public static String numToString(Object bd,String numFormat){
    try {
      return new DecimalFormat(numFormat).format(bd);
    } catch (Exception e) {
      MSLog.error("@@@数字格式化异常，请检查必填字段是否为空；",e);
      return null;
    }
  }
  
  /**
   * 函数说明：日期格式化工具，将日期格式化为指定格式的字符串；
   * @param d 
   *          日期对象；
   * @param formatStr
   *          指定格式；
   * @return 指定格式的日期字符串；
   * @author 叶跃新
   * @date 2015年11月23日
   * @throws Exception
   */
  public static String dateToString(Date d,String formatStr){
    try {
      return new SimpleDateFormat(formatStr).format(d);
    } catch (Exception e) {
      MSLog.error("@@@日期格式化异常，请检查必填字段是否为空；",e);
      return null;
    }
  }
 
  /**
   * 函数说明：对任意对象中的日期属性进行格式化；
   *          需要该属性提供一个同名且后缀为Str的String类型属性；
   *          同时提供getseter；
   *          如下例中需要提供 String planDateStr;及其getseter；
   *          
   * @param objList
   *          对象集合；
   * @param clazz
   *          对象类型；
   * @param field
   *          getset方法字段名称，取格式化后的属性名；
   *          例如：
   *              属性名：Date planDate;
   *              getset名：setPlanDate(Date planDate);/getPlanDate();
   *              该字段取值：PlanDate
   *          
   * @param format 
   *          日期格式；
   * @author 叶跃新
   * @date 2015年11月26日
   * @throws Exception
   */
  @SuppressWarnings({"rawtypes","unchecked"})
  public static void dataFormat( List objList,Class clazz,String field,String format){
    
    String getStr="get"+field;
    String setStr="set"+field+"Str";
    try {
     Method metGet=clazz.getMethod(getStr);
     Method metSet=clazz.getMethod(setStr, String.class);
     
     for(int i=0; i<objList.size(); i++){
       metSet.invoke(objList.get(i),dateToString( (Date) metGet.invoke(objList.get(i)), format));
     }
   } catch (Exception e) {
     MSLog.error("@@@日期格式化为字符串时异常；", e);
   }
  }
  
  /**
   * 函数说明：对任意对象中的日期属性进行格式化,接受非必填字段校验；
   *          需要该属性提供一个同名且后缀为Str的String类型属性；
   *          同时提供getseter；
   *          如下例中需要提供 String planDateStr;及其getseter；
   *          
   * @param objList
   *          对象集合；
   * @param clazz
   *          对象类型；
   * @param field
   *          getset方法字段名称，取格式化后的属性名；
   *          例如：
   *              属性名：Date planDate;
   *              getset名：setPlanDate(Date planDate);/getPlanDate();
   *              该字段取值：PlanDate
   *          
   * @param format 
   *          日期格式；
   * @param flag 
                            是否允许日期字段为空；
   * @author 叶跃新
   * @date 2015年11月26日
   * @throws Exception
   */
  @SuppressWarnings({"rawtypes","unchecked"})
  public static void dataFormat( List objList,Class clazz,String field,String format,boolean flag){
      String getStr="get"+field;
      String setStr="set"+field+"Str";
      try {
       Method metGet=clazz.getMethod(getStr);
       Method metSet=clazz.getMethod(setStr, String.class);
       
       //是否允许日期字段为空；
       if(flag){
           for(int i=0; i<objList.size(); i++){
             if((Date) metGet.invoke(objList.get(i))==null){
             metSet.invoke(objList.get(i),"");
           }else{
             metSet.invoke(objList.get(i),dateToString( (Date) metGet.invoke(objList.get(i)), format));
           }
         }
       }else{
         for (int i = 0; i < objList.size(); i++) {
           metSet.invoke(objList.get(i),dateToString( (Date) metGet.invoke(objList.get(i)), format));
        }
       }
     } catch (Exception e) {
       MSLog.error("@@@日期格式化为字符串时异常", e);
     }
  }
  
  /**
   * 函数说明：对任意对象中的金额属性进行格式化；
   *          需要该属性提供一个同名且后缀为Str的String类型属性；
   *          同时提供getseter；
   *          如下例中需要提供 String chgAmtStr;及其getseter；
   *          
   * @param objList
   *          对象集合；
   * @param clazz
   *          对象类型；
   * @param field
   *          getset方法字段名称，取格式化后的属性名；
   *          例如：
   *              属性名：BigDecimal chgAmt;
   *              getset名：setChgAmt(BigDecimal chgAmt);/getChgAmt();
   *              该字段取值：ChgAmt
   *          
   * @author 叶跃新
   * @date 2015年11月26日
   * @throws Exception
   */
  @SuppressWarnings({"rawtypes","unchecked"})
  public static void numFormat( List objList,Class clazz,String field){
    
    String getStr="get"+field;
    String setStr="set"+field+"Str";
    try {
      Method metGet=clazz.getMethod(getStr);
      Method metSet=clazz.getMethod(setStr, String.class);
      
      for(int i=0; i<objList.size(); i++){
        metSet.invoke(objList.get(i),numToString(metGet.invoke(objList.get(i))) );
      }
    } catch (Exception e) {
      MSLog.error("@@@金额格式化为字符串时异常", e);
    }
  }
  
  /**
   *  函数说明：对任意对象中的金额属性进行格式化，接受非必填字段校验；
   *  需要该属性提供一个同名且后缀为Str的String类型属性；
   *          同时提供getseter；
   *          如下例中需要提供 String chgAmtStr;及其getseter；
   * @param objList
   *          对象集合；
   * @param clazz
   *          对象类型；
   * @param field
   *          getset方法字段名称，取格式化后的属性名；
   *          例如：
   *              属性名：BigDecimal chgAmt;
   *              getset名：setChgAmt(BigDecimal chgAmt);/getChgAmt();
   *              该字段取值：ChgAmt
   *              
   * @param flag 
                            是否允许金额字段为空；
   * @author 叶跃新
   * @date 2015年11月26日
   * @throws Exception
   */
  @SuppressWarnings({"rawtypes","unchecked"})
  public static void numFormat( List objList,Class clazz,String field,boolean flag){
    
    String getStr="get"+field;
    String setStr="set"+field+"Str";
    try {
      Method metGet=clazz.getMethod(getStr);
      Method metSet=clazz.getMethod(setStr, String.class);
      
      //是否允许金额字段为空；
        if(flag){
            for(int i=0; i<objList.size(); i++){
              if(metGet.invoke(objList.get(i))==null){
              metSet.invoke(objList.get(i),"");
            }else{
              metSet.invoke(objList.get(i),numToString(metGet.invoke(objList.get(i))) );
            }
          }
        }else{
          for(int i=0; i<objList.size(); i++){
            metSet.invoke(objList.get(i),numToString(metGet.invoke(objList.get(i))) );
          }
        }
    } catch (Exception e) {
      MSLog.error("@@@金额格式化为字符串时异常", e);
    }
  }
}
