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
 * ����������ļ����������ࣻ
 * @version 1.0
 * @author ҶԾ��
 * @date 2015��11��24��
 */
public class ExportDataToTextUtil {

  /** �ļ����Ƴ���*/
  public static final String EXPORT_FILENAME_OFFER ="_offerinfo_"; //���ۻ�����ˮ��
  public static final String EXPORT_FILENAME_VOLUNTARY ="_voluntaryinfo_"; //����������ˮ��
  public static final String EXPORT_FILENAME_PLANREPAYMENT ="_planrepayment_"; //����ƻ���ˮ��
  public static final String EXPORT_FILENAME_PLANREPAYMENTCHG ="_planrepaymentchg_"; //����ƻ�������ˮ��
  public static final String EXPORT_FILENAME_REPAYMENTDETAIL ="_repaymentdetail_";//������ϸ��ˮ��
  public static final String EXPORT_FILENAME_APPLICATION ="_application_";//���������ˮ��
  public static final String EXPORT_FILENAME_ADVANCE ="_advance_";//Ԥ�����ˮ��
  public static final String EXPORT_FILENAME_PAYBACKINFO ="_paybackinfo_";//BUS�ؿ���ˮ��
  public static final String EXPORT_FILENAME_OVERDUEFEE ="_overduefee_";//BUS�ؿ���ˮ��
  
  
  /** �ļ�·������*/
  public static PropUtil propUtil =PropUtil.getInstance();
  public static final String EXPORT_FILEPATH = propUtil.getPropertyValue( "textTmpPath" );
  
  /**
   * ����˵������������������ת��Ϊ��ʽ���ı���
   * @param list
   *          ��Ҫת���Ľ������
   * @param titleName
   *          ��Ҫת����������
   * @return ��ʽ������ı���
   * @author ҶԾ��
   * @date 2015��11��24��
   * @throws Exception
   */
  public static String toText(LinkedList<LinkedHashMap<String, Object>> list, String[] titleName) {

    try {
      //�����ʽ��������
      Consoleprinttable c = new Consoleprinttable(titleName.length, true);
      
      LinkedHashMap<String, Object> lhm=null;
      
      //��һ�У���¼�ļ�������������
      c.appendRow();
      c.appendColum(list.size());
      
      //������ݣ�
      for(int i=0; i< list.size(); i++){
        c.appendRow();
        
        lhm=list.get(i);
        for(int j=0; j<titleName.length; j++){
          c.appendColum(lhm.get(titleName[j]));
        }
      }
      
      //���һ�У�������ʶ����
      c.appendRow();
      c.appendColum("END");
      
      MSLog.info(">>> �ļ�����ת���ɹ������ݽ�������ȣ�["+list.size()+"]");
      return c.toString();
    } catch (Exception e) {
      MSLog.error("@@@���ϸ�ʽ��Ϊ�ı�ʱ����",e);
      return null;
    }
  }
  
  /**
   * ����˵������Pojoת��ΪMap
   * @param t
   *        ��Ҫת����Pojo����
   * @return ת������Map��
   * @author ҶԾ��
   * @date 2015��11��24��
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
      MSLog.error("@@@POJOת��ΪMAPʱ����", e);
    }
    return null;
  }
  
  /**
   * ����˵������һ����ѯ��Ľ�������м�ֵ�Թ�����
   * @param list
   *          ��Ҫ�����Ľ������
   * @return ������ɺ���½������
   * @author ҶԾ��
   * @date 2015��11��24��
   * @throws Exception
   */
  public static <T> LinkedList<LinkedHashMap<String, Object>> listToLinkedMap(List<T> list){
    try {
      LinkedList<LinkedHashMap<String, Object>> linkmap= new LinkedList<LinkedHashMap<String,Object>>();
      //��������pojoת��Ϊmap��ʽ��
      for (int i = 0; i < list.size(); i++) {
        linkmap.add(beanToMap(list.get(i)));
      }
      return linkmap;
    } catch (Exception e) {
      MSLog.error("@@@��ѯ�����ת��ΪLinkedHashMapʱ����", e);
      return null;
    }   
  }
  
  /**
   * ����˵���������б��ֶ�����ļ��в���Ҫչʾ���ֶΣ�
   * @param map 
   *          ��Ҫ���������POJOת�����Map
   *          
   * @param nameArray
   *          ��Ҫչʾ���б��ֶΣ�
   *          
   * @return ������Map
   * @author ҶԾ��
   * @date 2015��11��24��
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
        MSLog.error("@@@���������δչʾ���ֶγ���", e);
        return null;
    }
  }
  
  /**
   * ����˵�����������Ҫ���ļ���չʾ�������ֶΣ�
   * @param source
   *          Դ���ϣ�
   * @param nameArray
   *          ��Ҫչʾ�������ֶΣ�
   * @return �����ļ��ϣ�
   * @author ҶԾ��
   * @date 2015��11��24��
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
      MSLog.error("@@@����ļ���δչʾ�ֶγ���", e);
      return null;
    }
  }
  
  /**
   * ����˵��������·�����ı�ת��Ϊ�ļ����������
   * @param filePath
   *            �����ļ����Ƶ����·����
   * @param content 
   *            �ı����ݣ�
   * @author ҶԾ��
   * @date 2015��11��24��
   * @throws Exception
   */
  public static void toFile(String filePath,String content){
    FileOutputStream os= null;
    try {
       os= new FileOutputStream(new File(filePath), true);
      os.write((content).getBytes());
      os.close();
      MSLog.info(">>>  �ļ�������ɣ�����·��Ϊ��["+filePath+"]");
     } catch (Exception e) {
      MSLog.error("@@@���ݵ��������ļ�ʱ����", e);
     }
  }
  
  /**
   * ����˵�������ݴ������������ϸ�ʽ��Ϊ�ı���
   * @param list
   *          ��Ҫ��ʽ���ļ��ϣ�
   * @param titlName
   *          �ļ����������
   * @return ��ʽ����ɺ���ı���
   * @author ҶԾ��
   * @date 2015��11��24��
   * @throws Exception
   */
  public static<T> String beanListToString(List<T> list,String titlName){
    try {
      MSLog.info(">>> �ļ����ݵ����ӿڵ��óɹ�����ʼ��������ת��..."+
      "������["+titlName+"] ,���ݽ�������ȣ�["+list.size()+"]");
      
      String[] strArray= titlName.split(",");
      
      //�����Ͻ��д��������ɼ����ж�������Լ�������ֵ��ɵ��¼��ϣ�
      LinkedList<LinkedHashMap<String, Object>> lmap= listToLinkedMap(list);
      
        //�������Ҫչʾ���ı��ļ��е����ԣ�
        lmap=linkedHMapListToCelanLinkedHMapList(lmap, strArray);
        
        return toText(lmap, strArray);
    } catch (Exception e) {
      MSLog.error("@@@�������ʽ��Ϊ�ı��ļ���ʱ����δ��ȡ���蹹���ļ���");
      return null;
    }
  }
  
  /**
   * ����˵�������ָ�ʽ�����ߣ����ڶ��������͵Ľ�����ݽ��и�ʽ����
   * @param bd
   *         ��Ҫ��ʽ�������ֶ���
   * @return ָ����ʽ�����ֶ���
   * @author ҶԾ��
   * @date 2015��11��23��
   * @throws Exception
   */
  public static String numToString(Object bd){
     try {
       return new DecimalFormat("0.00").format(bd);
      } catch (Exception e) {
        MSLog.error("@@@����ʽ���쳣����������ֶ��Ƿ�Ϊ�գ�",e);
        return null;
      }
  }
  
  /**
   * ����˵�������ָ�ʽ�����ߣ����ڶ��������͵Ľ�����ݽ��и�ʽ����
   * @param bd
   *         ��Ҫ��ʽ�������ֶ���
   * @param numFormat
   *         ��Ҫ����ĸ�ʽ���ͣ�
   * @return ָ����ʽ�����ֶ���
   * @author ҶԾ��
   * @date 2015��11��23��
   * @throws Exception
   */
  public static String numToString(Object bd,String numFormat){
    try {
      return new DecimalFormat(numFormat).format(bd);
    } catch (Exception e) {
      MSLog.error("@@@���ָ�ʽ���쳣����������ֶ��Ƿ�Ϊ�գ�",e);
      return null;
    }
  }
  
  /**
   * ����˵�������ڸ�ʽ�����ߣ������ڸ�ʽ��Ϊָ����ʽ���ַ�����
   * @param d 
   *          ���ڶ���
   * @param formatStr
   *          ָ����ʽ��
   * @return ָ����ʽ�������ַ�����
   * @author ҶԾ��
   * @date 2015��11��23��
   * @throws Exception
   */
  public static String dateToString(Date d,String formatStr){
    try {
      return new SimpleDateFormat(formatStr).format(d);
    } catch (Exception e) {
      MSLog.error("@@@���ڸ�ʽ���쳣����������ֶ��Ƿ�Ϊ�գ�",e);
      return null;
    }
  }
 
  /**
   * ����˵��������������е��������Խ��и�ʽ����
   *          ��Ҫ�������ṩһ��ͬ���Һ�׺ΪStr��String�������ԣ�
   *          ͬʱ�ṩgetseter��
   *          ����������Ҫ�ṩ String planDateStr;����getseter��
   *          
   * @param objList
   *          ���󼯺ϣ�
   * @param clazz
   *          �������ͣ�
   * @param field
   *          getset�����ֶ����ƣ�ȡ��ʽ�������������
   *          ���磺
   *              ��������Date planDate;
   *              getset����setPlanDate(Date planDate);/getPlanDate();
   *              ���ֶ�ȡֵ��PlanDate
   *          
   * @param format 
   *          ���ڸ�ʽ��
   * @author ҶԾ��
   * @date 2015��11��26��
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
     MSLog.error("@@@���ڸ�ʽ��Ϊ�ַ���ʱ�쳣��", e);
   }
  }
  
  /**
   * ����˵��������������е��������Խ��и�ʽ��,���ܷǱ����ֶ�У�飻
   *          ��Ҫ�������ṩһ��ͬ���Һ�׺ΪStr��String�������ԣ�
   *          ͬʱ�ṩgetseter��
   *          ����������Ҫ�ṩ String planDateStr;����getseter��
   *          
   * @param objList
   *          ���󼯺ϣ�
   * @param clazz
   *          �������ͣ�
   * @param field
   *          getset�����ֶ����ƣ�ȡ��ʽ�������������
   *          ���磺
   *              ��������Date planDate;
   *              getset����setPlanDate(Date planDate);/getPlanDate();
   *              ���ֶ�ȡֵ��PlanDate
   *          
   * @param format 
   *          ���ڸ�ʽ��
   * @param flag 
                            �Ƿ����������ֶ�Ϊ�գ�
   * @author ҶԾ��
   * @date 2015��11��26��
   * @throws Exception
   */
  @SuppressWarnings({"rawtypes","unchecked"})
  public static void dataFormat( List objList,Class clazz,String field,String format,boolean flag){
      String getStr="get"+field;
      String setStr="set"+field+"Str";
      try {
       Method metGet=clazz.getMethod(getStr);
       Method metSet=clazz.getMethod(setStr, String.class);
       
       //�Ƿ����������ֶ�Ϊ�գ�
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
       MSLog.error("@@@���ڸ�ʽ��Ϊ�ַ���ʱ�쳣", e);
     }
  }
  
  /**
   * ����˵��������������еĽ�����Խ��и�ʽ����
   *          ��Ҫ�������ṩһ��ͬ���Һ�׺ΪStr��String�������ԣ�
   *          ͬʱ�ṩgetseter��
   *          ����������Ҫ�ṩ String chgAmtStr;����getseter��
   *          
   * @param objList
   *          ���󼯺ϣ�
   * @param clazz
   *          �������ͣ�
   * @param field
   *          getset�����ֶ����ƣ�ȡ��ʽ�������������
   *          ���磺
   *              ��������BigDecimal chgAmt;
   *              getset����setChgAmt(BigDecimal chgAmt);/getChgAmt();
   *              ���ֶ�ȡֵ��ChgAmt
   *          
   * @author ҶԾ��
   * @date 2015��11��26��
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
      MSLog.error("@@@����ʽ��Ϊ�ַ���ʱ�쳣", e);
    }
  }
  
  /**
   *  ����˵��������������еĽ�����Խ��и�ʽ�������ܷǱ����ֶ�У�飻
   *  ��Ҫ�������ṩһ��ͬ���Һ�׺ΪStr��String�������ԣ�
   *          ͬʱ�ṩgetseter��
   *          ����������Ҫ�ṩ String chgAmtStr;����getseter��
   * @param objList
   *          ���󼯺ϣ�
   * @param clazz
   *          �������ͣ�
   * @param field
   *          getset�����ֶ����ƣ�ȡ��ʽ�������������
   *          ���磺
   *              ��������BigDecimal chgAmt;
   *              getset����setChgAmt(BigDecimal chgAmt);/getChgAmt();
   *              ���ֶ�ȡֵ��ChgAmt
   *              
   * @param flag 
                            �Ƿ��������ֶ�Ϊ�գ�
   * @author ҶԾ��
   * @date 2015��11��26��
   * @throws Exception
   */
  @SuppressWarnings({"rawtypes","unchecked"})
  public static void numFormat( List objList,Class clazz,String field,boolean flag){
    
    String getStr="get"+field;
    String setStr="set"+field+"Str";
    try {
      Method metGet=clazz.getMethod(getStr);
      Method metSet=clazz.getMethod(setStr, String.class);
      
      //�Ƿ��������ֶ�Ϊ�գ�
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
      MSLog.error("@@@����ʽ��Ϊ�ַ���ʱ�쳣", e);
    }
  }
}
