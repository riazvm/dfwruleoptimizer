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
public class InventoryServiceGroup {
    public String resource_type;
    public List<InventoryServiceRow> children;
}
