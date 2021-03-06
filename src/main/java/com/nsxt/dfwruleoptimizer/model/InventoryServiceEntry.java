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
public class InventoryServiceEntry {
        public String l4_protocol;
        public List<String> source_ports;
        public List<String> destination_ports;
        public String resource_type;
        public String id;
        public String display_name;
        public boolean marked_for_delete;

}
