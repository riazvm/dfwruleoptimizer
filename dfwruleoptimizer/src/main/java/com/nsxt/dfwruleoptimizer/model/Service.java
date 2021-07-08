package com.nsxt.dfwruleoptimizer.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Service {
    public boolean is_default;
    public String resource_type;
    public String id;
    public String display_name;
    public List<ServiceEntryRows> children;

}
