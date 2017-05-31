/* create my 17 */
#import "event.h"
@implementation EventUtil

+(Event) event_by_str:(NSString *)eventstr{
  if( [eventstr isEqual:@"REQ_NONE"] ) return REQ_NONE;
  if( [eventstr isEqual:@"REQ_FORM_BACK"] ) return REQ_FORM_BACK;
  if( [eventstr isEqual:@"REQ_TOAST"] ) return REQ_TOAST;
  if( [eventstr isEqual:@"REQ_WAITTING_SHOW"] ) return REQ_WAITTING_SHOW;
  if( [eventstr isEqual:@"REQ_WAITTING_HIDE"] ) return REQ_WAITTING_HIDE;
  if( [eventstr isEqual:@"REQ_DIALOG_INFO"] ) return REQ_DIALOG_INFO;
  if( [eventstr isEqual:@"REQ_DIALOG_SURE"] ) return REQ_DIALOG_SURE;
  if( [eventstr isEqual:@"REQ_FROM_CLEAR"] ) return REQ_FROM_CLEAR;
  if( [eventstr isEqual:@"FORM_MAIN"] ) return FORM_MAIN;
  if( [eventstr isEqual:@"FORM_FLASH"] ) return FORM_FLASH;
  if( [eventstr isEqual:@"REQ_PUSH_TO_AND_CLEAR_ALL"] ) return REQ_PUSH_TO_AND_CLEAR_ALL;
  if( [eventstr isEqual:@"FORM_EDIT"] ) return FORM_EDIT;
  if( [eventstr isEqual:@"REQ_ALARM_LIST"] ) return REQ_ALARM_LIST;
  if( [eventstr isEqual:@"REP_ALARM_LIST"] ) return REP_ALARM_LIST;
  if( [eventstr isEqual:@"REQ_ALARM_SAVE"] ) return REQ_ALARM_SAVE;
  if( [eventstr isEqual:@"REP_ALARM_SAVE_SUCCESS"] ) return REP_ALARM_SAVE_SUCCESS;
  if( [eventstr isEqual:@"REP_ALARM_SAVE_FAIL"] ) return REP_ALARM_SAVE_FAIL;
  return -1;
}

+(NSString*)str_by_event:(Event)event{
  switch(event){

  case REQ_NONE : return @"REQ_NONE";
  case REQ_FORM_BACK : return @"REQ_FORM_BACK";
  case REQ_TOAST : return @"REQ_TOAST";
  case REQ_WAITTING_SHOW : return @"REQ_WAITTING_SHOW";
  case REQ_WAITTING_HIDE : return @"REQ_WAITTING_HIDE";
  case REQ_DIALOG_INFO : return @"REQ_DIALOG_INFO";
  case REQ_DIALOG_SURE : return @"REQ_DIALOG_SURE";
  case REQ_FROM_CLEAR : return @"REQ_FROM_CLEAR";
  case FORM_MAIN : return @"FORM_MAIN";
  case FORM_FLASH : return @"FORM_FLASH";
  case REQ_PUSH_TO_AND_CLEAR_ALL : return @"REQ_PUSH_TO_AND_CLEAR_ALL";
  case FORM_EDIT : return @"FORM_EDIT";
  case REQ_ALARM_LIST : return @"REQ_ALARM_LIST";
  case REP_ALARM_LIST : return @"REP_ALARM_LIST";
  case REQ_ALARM_SAVE : return @"REQ_ALARM_SAVE";
  case REP_ALARM_SAVE_SUCCESS : return @"REP_ALARM_SAVE_SUCCESS";
  case REP_ALARM_SAVE_FAIL : return @"REP_ALARM_SAVE_FAIL";
}

  return @"not define";
}

@end
