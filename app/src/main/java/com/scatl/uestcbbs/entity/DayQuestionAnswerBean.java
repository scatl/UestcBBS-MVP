package com.scatl.uestcbbs.entity;

public class DayQuestionAnswerBean {

    /**
     * returnCode : 1
     * returnMsg : 获取答案成功
     * returnData : {"question":"111","answer":"222","id":"1"}
     */

    public Integer returnCode;
    public String returnMsg;
    public ReturnDataDTO returnData;

    public static class ReturnDataDTO {
        /**
         * question : 111
         * answer : 222
         * id : 1
         */

        public String question;
        public String answer;
        public String id;
    }
}
