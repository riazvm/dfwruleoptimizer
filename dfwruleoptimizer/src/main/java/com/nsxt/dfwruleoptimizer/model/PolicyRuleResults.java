package com.nsxt.dfwruleoptimizer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PolicyRuleResults {
    public String action;
    public String resource_type;
    public String id;
    public String display_name;
    public String sequence_number;
    public List<String> source_groups;
    public List<String> destination_groups;
    public List<String> services;
    //public List<String> profiles;
    public List<String> scope;

}



