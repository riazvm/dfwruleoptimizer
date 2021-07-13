package com.nsxt.dfwruleoptimizer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryService {
    @JsonProperty("is_default")
    public boolean is_default;
    public String resource_type;
    public String id;
    public String display_name;
    public List<InventoryServiceEntryRow> children;

}
