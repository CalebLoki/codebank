package com.jfbank.cicada.export.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jfbank.cicada.cons.Constant;
import com.jfbank.cicada.export.ExportService;
import com.jfbank.cicada.mapper.export.ExportDataToFileMapper;
import com.jfbank.cicada.model.TIndAdvance;
import com.jfbank.cicada.model.TIndApplication;
import com.jfbank.cicada.model.TIndCalculateFee;
import com.jfbank.cicada.model.TIndOfferSucc;
import com.jfbank.cicada.model.TIndPayback;
import com.jfbank.cicada.model.TIndRepayment;
import com.jfbank.cicada.model.TIndRepaymentChange;
import com.jfbank.cicada.model.TIndRepaymentDetail;
import com.jfbank.cicada.model.TIndVoluntary;
import com.jfbank.cicada.querymapper.QueryTIndAdvanceMapper;
import com.jfbank.cicada.querymapper.QueryTIndApplicationMapper;
import com.jfbank.cicada.querymapper.QueryTIndCalculateFeeMapper;
import com.jfbank.cicada.querymapper.QueryTIndOfferSuccMapper;
import com.jfbank.cicada.querymapper.QueryTIndPaybackMapper;
import com.jfbank.cicada.querymapper.QueryTIndRepaymentChangeMapper;
import com.jfbank.cicada.querymapper.QueryTIndRepaymentDetailMapper;
import com.jfbank.cicada.querymapper.QueryTIndRepaymentMapper;
import com.jfbank.cicada.querymapper.QueryTIndVoluntaryMapper;
import com.jfbank.cicada.querymapper.export.QueryExportDataToFileMapper;
import com.jfbank.cicada.utils.DateUtilManager;
import com.jfbank.cicada.utils.export.ExportDataToTextUtil;

/**
 * 类的描述：文件数据导出实现类；
 * @version 1.0
 * @author 叶跃新
 * @date 2015年11月22日
 */
@Service
public class ExportServiceImpl implements ExportService{
  
  @Autowired
  private QueryTIndOfferSuccMapper queryTIndOfferSuccMapper;
  @Autowired
  private QueryTIndAdvanceMapper queryTIndAdvanceMapper;
  @Autowired
  private QueryTIndVoluntaryMapper queryTIndVoluntaryMapper;
  @Autowired
  private QueryTIndRepaymentDetailMapper queryTIndRepaymentDetailMapper;
  @Autowired
  private QueryTIndRepaymentMapper queryTIndRepaymentMapper;
  @Autowired
  private QueryTIndRepaymentChangeMapper queryTIndRepaymentChangeMapper;
  @Autowired
  private QueryTIndApplicationMapper queryTIndApplicationMapper;
  @Autowired
  private QueryTIndPaybackMapper queryTIndPaybackMapper;
  @Autowired
  private QueryTIndCalculateFeeMapper queryTIndCalculateFeeMapper;
  @Autowired
  private QueryExportDataToFileMapper queryExportDataToFileMapper;
  @Autowired
  private ExportDataToFileMapper exportDataToFileMapper;
  
  /**
   * 函数说明：一次导出所有文件；
   *  
   * @author 叶跃新
   * @date 2015年11月22日
   * @throws Exception
   */
  @Override
  public void exportDataToTextByTimeAll() {
    
    HashMap<String,Object> condition= new HashMap<String,Object>();
    
    //获取系统当前日期；
    String systimeStr= ExportDataToTextUtil.dateToString(DateUtilManager.getSystemDate(), Constant.DATE_FORMAT);
    
    //设定文件导出日期范围；
    condition.put("startTime", DateUtilManager.addDay(DateUtilManager.toDate(systimeStr+" 01:00:00"), -1));
    condition.put("endTime", DateUtilManager.toDate(systimeStr+" 01:00:00"));
    
    //根据条件进行文件导出；
    AllToTextMethod(condition);
  }

  @Override
  public void exportDataToTextByTime(List<T> list, String titleName) {
    
  }
  
  private void AllToTextMethod(Map<String,Object> condition){
    
    //获取系统当前日期；
    String systimeStr= ExportDataToTextUtil.dateToString(
        DateUtilManager.getSystemDate(), Constant.MINI_DATE_FORMAT)+"_";
    
    // 代扣回盘流水文件导出 >>>
    List<TIndOfferSucc> osList= queryTIndOfferSuccMapper.selectOfferSuccExportFileByDate(condition);
    
    //处理导出金额和日期格式；
    ExportDataToTextUtil.dataFormat(osList, TIndOfferSucc.class, "OfferDate", Constant.DATETIME_FORMAT);
    ExportDataToTextUtil.dataFormat(osList, TIndOfferSucc.class, "ReturnDate", Constant.DATETIME_FORMAT);
    ExportDataToTextUtil.numFormat(osList, TIndOfferSucc.class, "OfferAmt");
    ExportDataToTextUtil.numFormat(osList, TIndOfferSucc.class, "ReturnAmt");
    
    //生成导出文本；
    String osListStr=ExportDataToTextUtil.beanListToString(osList, 
        "appId,batchId,offerAmtStr,returnAmtStr,offerDateStr,returnDateStr,bankName,bankCard,bankCardName,"+
    "serialNo,orderNo,returnCode,returnMsg,createdby");
    
    // 主动还款流水文件导出 >>> 
    List<TIndVoluntary> vList= queryTIndVoluntaryMapper.selectVoluntaryExportFileByDate(condition);
    
    //处理导出金额和日期格式；
    ExportDataToTextUtil.dataFormat(vList, TIndVoluntary.class, "TradTime", Constant.TIME_FORMT);
    ExportDataToTextUtil.numFormat(vList, TIndVoluntary.class, "TradAmt");
    
    //生成导出文本；
    String vListStr= ExportDataToTextUtil.beanListToString(vList, "appId,tradDateStr,tradTimeStr,tradAmtStr,abstracts,"+
    "firmCord,acctName,acctNo,bankNo,bankName,identityCard,serialNo,outflow,orderNo");
    
    // 冲账明细数据文件导出 >>> 
    List<TIndRepaymentDetail> rdList= queryTIndRepaymentDetailMapper.selectRepaymentDetailExportFileByDate(condition);
    
    //处理导出金额和日期格式；
    ExportDataToTextUtil.dataFormat(rdList, TIndRepaymentDetail.class, "Createdon", Constant.DATETIME_FORMAT);
    ExportDataToTextUtil.dataFormat(rdList, TIndRepaymentDetail.class, "RepayDate", Constant.DATE_FORMAT);
    ExportDataToTextUtil.dataFormat(rdList, TIndRepaymentDetail.class, "RepayTime", Constant.TIME_FORMT);
    ExportDataToTextUtil.numFormat(rdList, TIndRepaymentDetail.class, "RepayAmt");
    
    //生成导出文本；
    String rdListStr= ExportDataToTextUtil.beanListToString(rdList, "repaymentId,appId,repayStage,subCode,repayAmtStr,"+
    "createdonStr,repayDateStr,repayTimeStr,serialNo,orderNo,busFlag,createdby");
    
    // 还款计划文件导出 >>> 
    List<TIndRepayment> rList= queryTIndRepaymentMapper.selectRepaymentExportFileByDate(condition);
    
    //处理导出金额和日期格式；
    ExportDataToTextUtil.dataFormat(rList, TIndRepayment.class, "PlanDate", Constant.DATE_FORMAT);
    ExportDataToTextUtil.dataFormat(rList, TIndRepayment.class, "RepayDate", Constant.DATE_FORMAT);
    ExportDataToTextUtil.numFormat(rList, TIndRepayment.class, "RepayAmt");
    ExportDataToTextUtil.numFormat(rList, TIndRepayment.class, "PlanAmt");
    
    //生成导出文本；
    String rListStr= ExportDataToTextUtil.beanListToString(rList, "id,appId,subCode,repayStage,repayType,planDateStr,"+
    "planAmtStr,repayAmtStr,repayStatus,repayDateStr");
    
    // 还款计划调整文件导出 >>> 
    List<TIndRepaymentChange> rcList= queryTIndRepaymentChangeMapper.selectRepaymentChangeExportFileByDate(condition);
    
    //处理导出金额和日期格式；
    ExportDataToTextUtil.dataFormat(rcList, TIndRepaymentChange.class, "PlanDate", Constant.DATE_FORMAT);
    ExportDataToTextUtil.dataFormat(rcList, TIndRepaymentChange.class, "BeginDate", Constant.DATE_FORMAT);
    ExportDataToTextUtil.dataFormat(rcList, TIndRepaymentChange.class, "EndDate", Constant.DATE_FORMAT);
    ExportDataToTextUtil.numFormat(rcList, TIndRepaymentChange.class, "ChgAmt");
    
    //生成导出文本；
    String rcListStr= ExportDataToTextUtil.beanListToString(rcList, "appId,subCode,batchNo,repayStage,repayType,planDateStr,"+
    "chgAmtStr,beginDateStr,endDateStr,repayStatus,chgDesc");
    
    // 工单变更数据文件导出 >>> 
    List<TIndApplication> aList= queryTIndApplicationMapper.selectApplicationExportByDate(condition);
    
    //处理导出金额和日期格式；
    ExportDataToTextUtil.dataFormat(aList, TIndApplication.class, "OverdueDate", Constant.DATE_FORMAT,true);
    ExportDataToTextUtil.dataFormat(aList, TIndApplication.class, "SettleDate", Constant.DATE_FORMAT,true);
    
    //生成导出文本；
    String aListStr= ExportDataToTextUtil.beanListToString(aList, "appId,overdueDateStr,overdueDay,settleStatus,settleDateStr,settleType");
    
    // 预存款数据文件导出 >>> 
    List<TIndAdvance> adList= queryTIndAdvanceMapper.selectAdvanceExportFileByDate(condition);
    
    //处理导出金额和日期格式；
    ExportDataToTextUtil.dataFormat(adList, TIndAdvance.class, "TradeDate", Constant.DATE_FORMAT);
    ExportDataToTextUtil.dataFormat(adList, TIndAdvance.class, "Createdon", Constant.DATETIME_FORMAT);
    ExportDataToTextUtil.numFormat(adList, TIndAdvance.class, "TradeAmt");
    
    //生成导出文本；
    String adListStr= ExportDataToTextUtil.beanListToString(adList, "appId,tradeAmtStr,tradeDateStr,serialNo,orderNo,createdonStr");
    
    // BUS回款数据文件导出 >>> 
    List<TIndPayback> pList= queryTIndPaybackMapper.selectPaybackExportFileByDate(condition);
    
    //处理导出金额和日期格式；
    ExportDataToTextUtil.dataFormat(pList, TIndPayback.class, "RepayDate", Constant.DATE_FORMAT);
    ExportDataToTextUtil.numFormat(pList, TIndPayback.class, "RepayAmt");
    
    
    //生成导出文本；
    String pListStr= ExportDataToTextUtil.beanListToString(pList, "appId,repayType,repayAmtStr,repayDateStr,busFlag");
    
    //>>> 费用计提文件导出；
    List<TIndCalculateFee> cList= queryTIndCalculateFeeMapper.selectCalculateExportFileByDate(condition);
    
    //处理导出金额和日期格式；
    ExportDataToTextUtil.dataFormat(cList, TIndCalculateFee.class, "PlanDate", Constant.DATE_FORMAT);
    ExportDataToTextUtil.dataFormat(cList, TIndCalculateFee.class, "Createdon", Constant.DATETIME_FORMAT);
    ExportDataToTextUtil.numFormat(cList, TIndCalculateFee.class, "Amt");
    
    
    //生成导出文本；
    String cListStr= ExportDataToTextUtil.beanListToString(cList, "appId,subCode,stage,planDateStr,amtStr,createdonStr");
    
    //根据实际地址生成导出文件；
    ExportDataToTextUtil.toFile(
        ExportDataToTextUtil.EXPORT_FILEPATH+ 
        Constant.CHANNEL_NO_CN+
        ExportDataToTextUtil.EXPORT_FILENAME_OFFER+
        systimeStr+
        ExportDataToTextUtil.numToString(
        getSequence(ExportDataToTextUtil.EXPORT_FILENAME_OFFER), "0000")+
        ".txt", osListStr);
    
    ExportDataToTextUtil.toFile(
        ExportDataToTextUtil.EXPORT_FILEPATH+
        Constant.CHANNEL_NO_CN+
        ExportDataToTextUtil.EXPORT_FILENAME_VOLUNTARY+
        systimeStr+
        ExportDataToTextUtil.numToString(
        getSequence(ExportDataToTextUtil.EXPORT_FILENAME_VOLUNTARY), "0000")+
        ".txt", vListStr);
    
    ExportDataToTextUtil.toFile(
        ExportDataToTextUtil.EXPORT_FILEPATH+
        Constant.CHANNEL_NO_CN+
        ExportDataToTextUtil.EXPORT_FILENAME_PLANREPAYMENT+
        systimeStr+
        ExportDataToTextUtil.numToString(
        getSequence(ExportDataToTextUtil.EXPORT_FILENAME_PLANREPAYMENT), "0000")+
        ".txt", rListStr);
    
    ExportDataToTextUtil.toFile(
        ExportDataToTextUtil.EXPORT_FILEPATH+
        Constant.CHANNEL_NO_CN+
        ExportDataToTextUtil.EXPORT_FILENAME_PLANREPAYMENTCHG+
        systimeStr+
        ExportDataToTextUtil.numToString(
        getSequence(ExportDataToTextUtil.EXPORT_FILENAME_PLANREPAYMENTCHG), "0000")+
        ".txt", rcListStr);
    
    ExportDataToTextUtil.toFile(
        ExportDataToTextUtil.EXPORT_FILEPATH+
        Constant.CHANNEL_NO_CN+
        ExportDataToTextUtil.EXPORT_FILENAME_REPAYMENTDETAIL+
        systimeStr+
        ExportDataToTextUtil.numToString(
        getSequence(ExportDataToTextUtil.EXPORT_FILENAME_REPAYMENTDETAIL), "0000")+
        ".txt", rdListStr);
    
    ExportDataToTextUtil.toFile(
        ExportDataToTextUtil.EXPORT_FILEPATH+
        Constant.CHANNEL_NO_CN+
        ExportDataToTextUtil.EXPORT_FILENAME_APPLICATION+
        systimeStr+
        ExportDataToTextUtil.numToString(
        getSequence(ExportDataToTextUtil.EXPORT_FILENAME_APPLICATION), "0000")+
        ".txt", aListStr);
    
    ExportDataToTextUtil.toFile(
        ExportDataToTextUtil.EXPORT_FILEPATH+
        Constant.CHANNEL_NO_CN+
        ExportDataToTextUtil.EXPORT_FILENAME_ADVANCE+
        systimeStr+
        ExportDataToTextUtil.numToString(
        getSequence(ExportDataToTextUtil.EXPORT_FILENAME_ADVANCE), "0000")+
        ".txt", adListStr);
    
    ExportDataToTextUtil.toFile(
        ExportDataToTextUtil.EXPORT_FILEPATH+
        Constant.CHANNEL_NO_CN+
        ExportDataToTextUtil.EXPORT_FILENAME_PAYBACKINFO+
        systimeStr+
        ExportDataToTextUtil.numToString(
        getSequence(ExportDataToTextUtil.EXPORT_FILENAME_PAYBACKINFO), "0000")+
        ".txt", pListStr);
    
    ExportDataToTextUtil.toFile(
        ExportDataToTextUtil.EXPORT_FILEPATH+
        Constant.CHANNEL_NO_CN+
        ExportDataToTextUtil.EXPORT_FILENAME_OVERDUEFEE+
        systimeStr+
        ExportDataToTextUtil.numToString(
        getSequence(ExportDataToTextUtil.EXPORT_FILENAME_OVERDUEFEE), "0000")+
            ".txt", cListStr);/**/
  }

  
  /**
   * 函数说明：获取文件名序号；
   * @param fileName
   *            文件名
   * @return 在原序号基础上自增1的值；
   * @author 叶跃新
   * @date 2015年11月24日
   * @throws Exception
   */
  private synchronized int getSequence(String fileName){
    int start= queryExportDataToFileMapper.getSequence(fileName);
    start+=1;
    
    exportDataToFileMapper.updateSequence(start,fileName);
    return queryExportDataToFileMapper.getSequence(fileName);
  }
  
  /**
   * 函数说明：根据文件名对文件序号进行归零操作；
   * @param fileName 
   *            文件名
   * @author 叶跃新
   * @date 2015年11月24日
   * @throws Exception
   */
  @Override
  public void cleanExportDataToText(String fileName) {
    exportDataToFileMapper.updateSequence(-1,fileName);
  }

}
