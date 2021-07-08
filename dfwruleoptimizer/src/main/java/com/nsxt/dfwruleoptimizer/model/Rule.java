package com.nsxt.dfwruleoptimizer.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Rule {

    public String description;
    public String id;
    public String display_name;
    public int sequence_number;
    public List<String> source_groups;
    public List<String> destination_groups;
    public List<String> scope;
    public String action;
    public List<String> services;
    public int _revision;
    public boolean disabled;
    public String direction;
    public String ip_protocol;

}
