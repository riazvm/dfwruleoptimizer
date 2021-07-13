package com.nsxt.dfwruleoptimizer.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ServiceEntity {
    public List<ServiceEntityResults> results;
    public int result_count;
    public String sort_by;
    public boolean sort_ascending;
}
