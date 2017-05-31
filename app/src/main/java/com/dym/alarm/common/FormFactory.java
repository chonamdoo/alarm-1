/* create my 17 */
package com.dym.alarm.common;
import com.dym.alarm.forms.*;
public class FormFactory{
  public static Class getForm(Event event){
      switch(event){
          case FORM_MAIN: return FormMain.class;
          case FORM_FLASH: return FormFlash.class;
          case FORM_EDIT: return FormEdit.class;
          }
      return null;
  }
  public static String getFormName(Event event){
      switch(event){
          case FORM_MAIN: return "首页";
          case FORM_FLASH: return "启动页";
          case FORM_EDIT: return "提醒编辑页";
          }
      return "";
  }
}
