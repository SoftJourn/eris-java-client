//package com.softjourn.eris.transaction.parser;
//
//import com.softjourn.eris.ErisVersion;
//import com.softjourn.eris.body.parser.v11.ErisCallTransactionParser11;
//import com.softjourn.eris.body.parser.v12.ErisCallTransactionParser12;
//import com.softjourn.eris.body.pojo.ErisTransactionType;
//
//public class ErisTransactionParserFactory {
//    public ErisTransactionParser getParser(ErisVersion version, ErisTransactionType type){
//
//        if(type == ErisTransactionType.CALL){
//            switch (version){
//                case V11: return new ErisCallTransactionParser11();
//                case V12: return new ErisCallTransactionParser12();
//
//                default: throw new IllegalArgumentException("Version is not supported");
//            }
//        } else {
//            throw new IllegalArgumentException("Version is not supported");
//        }
//    }
//}
