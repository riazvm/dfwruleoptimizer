package com.nsxt.dfwruleoptimizer.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ServiceEntryRows {

    public List serviceEntry;
    public String resource_type;
    public boolean marked_for_delete;

}
