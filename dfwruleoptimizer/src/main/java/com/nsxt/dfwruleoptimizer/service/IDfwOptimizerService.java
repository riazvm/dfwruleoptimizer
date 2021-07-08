package com.nsxt.dfwruleoptimizer.service;

import com.nsxt.dfwruleoptimizer.model.Policy;
import com.nsxt.dfwruleoptimizer.model.PolicyRule;
import com.nsxt.dfwruleoptimizer.model.ServiceEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IDfwOptimizerService {

   //void findAll();

   public Mono<Policy> getAllPolicies() throws Exception;

   public Policy findAll();

   public Mono<PolicyRule> getPolicy(String policyID) throws Exception;

   public Mono<ServiceEntity> getServiceEntity(String serviceID) throws Exception;

   public void optimizeServiceByRule(String policyID, String ruleID) throws Exception;

   public void optimizeServiceByPolicy(String policyID) throws Exception;

   public void optimizeServiceAll() throws Exception;
}
