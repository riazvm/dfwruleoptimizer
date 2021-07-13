package com.nsxt.dfwruleoptimizer.service;

import com.nsxt.dfwruleoptimizer.model.Policy;
import com.nsxt.dfwruleoptimizer.model.PolicyRule;
import com.nsxt.dfwruleoptimizer.model.ServiceEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * <h1>IDfwOptimizerService</h1>
 * Interface for DfwOptimizerService Implementation
 *
 * @author Riaz Mohamed, Sobana T
 * @version 1.0
 * @since 07/13/2021
 */
public interface IDfwOptimizerService {

   //void findAll();

   public Mono<Policy> getAllPolicies() throws Exception;

   public Mono<PolicyRule> getPolicy(String policyID) throws Exception;

   public Mono<ServiceEntity> getServiceEntity(String serviceID) throws Exception;

   public void optimizeServiceByRule(String policyID, String ruleID) throws Exception;

   public void optimizeServiceByPolicy(String policyID) throws Exception;

   public void optimizeServiceAll() throws Exception;
}
