package com.nsxt.dfwruleoptimizer.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ServiceEntityResults {
    public String l4_protocol;
    public List<String> source_ports;
    public List<String> destination_ports;
    public String resource_type;
    public String id;
    public String display_name;
    public boolean marked_for_delete;
    public String _create_user;
}



