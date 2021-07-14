package com.nsxt.dfwruleoptimizer.service;

import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String ar[]) {
        String ruleID = "d71fd6a0";
        if (ruleID.indexOf("-") != -1) {
            System.out.println(ruleID.substring(0, ruleID.indexOf("-")));
        }

        List<String> serviceEntryRow=new ArrayList<>();
        serviceEntryRow.add("123-1234");
        serviceEntryRow.add("456");
        serviceEntryRow.add("789");
        serviceEntryRow.add("1011");
        serviceEntryRow.add("111");
        serviceEntryRow.add("1111");
        StringBuffer tmpprotocolID = new StringBuffer();
        String test="";
        String test2 ="";
        boolean range = false;
        if(serviceEntryRow.get(0).contains("-")) {
            System.out.println(serviceEntryRow.get(0).indexOf("-"));
            tmpprotocolID.append(serviceEntryRow.get(0), 0, serviceEntryRow.get(0).indexOf("-"));
            test = serviceEntryRow.get(0).substring(0, serviceEntryRow.get(0).indexOf("-"));
            test2= serviceEntryRow.get(serviceEntryRow.size()-1).substring(serviceEntryRow.get(serviceEntryRow.size()-1).indexOf("-")+1);
            tmpprotocolID.append("-");
            tmpprotocolID.append(serviceEntryRow.get(0).substring(serviceEntryRow.get(0).indexOf("-")+1));
             range = true;
        }
        /*if(serviceEntryRow.size()>1)
        {
            tmpprotocolID.append("-");
            tmpprotocolID.append(serviceEntryRow.get(serviceEntryRow.size()-1));
        }*/
        System.out.println(tmpprotocolID.toString());
        System.out.println(test+"-----"+test2+"-----"+range);
    }
}
