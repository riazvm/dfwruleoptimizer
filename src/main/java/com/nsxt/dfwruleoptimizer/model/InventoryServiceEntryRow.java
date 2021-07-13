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
public class InventoryServiceEntryRow {

    @JsonProperty("ServiceEntry")
    public InventoryServiceEntry serviceEntry;
    public String resource_type;
    public boolean marked_for_delete;

}
