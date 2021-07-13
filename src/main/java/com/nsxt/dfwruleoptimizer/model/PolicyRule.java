package com.nsxt.dfwruleoptimizer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PolicyRule {
    public List<PolicyRuleResults> rules;
    public String id;
    public String display_name;
    public String category;

}
