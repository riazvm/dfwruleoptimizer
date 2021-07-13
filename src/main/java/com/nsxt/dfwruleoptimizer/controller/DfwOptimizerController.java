package com.nsxt.dfwruleoptimizer.controller;

import com.nsxt.dfwruleoptimizer.model.Policy;
import com.nsxt.dfwruleoptimizer.model.PolicyRule;
import com.nsxt.dfwruleoptimizer.service.DfwOptimizerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
/**
 * <h1>DfwOptimizerController</h1>
 *
 * @author Riaz Mohamed, Sobana T
 * @version 1.0
 * @since 07/13/2021
 */

@RestController
public class DfwOptimizerController {

    Logger logger = LoggerFactory.getLogger(DfwOptimizerController.class);

    @Autowired
    private DfwOptimizerService dfwOptimizerService;

    /**
     * getHealth - To be replaced by actuator
     */
    @RequestMapping("/health")
    public String getHealth() throws InterruptedException {
        return "Ok";
    }
    /**
     * getAllPolicies - Call NSX to get all policies
     *
     * @return All policies
     */
    @RequestMapping("/getAllPolicies")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Policy> getAllPolicies() throws Exception {
        Mono<Policy> policy = dfwOptimizerService.getAllPolicies();
        return policy;
    }
    /**
     * getPolicy - Get a Specific policy
     * @param policyID
     * @return All rules for the Policy
     */
    @RequestMapping("/getPolicy")
    @ResponseStatus(HttpStatus.OK)
    public Mono<PolicyRule> getPolicy(@RequestParam String policyID) throws Exception {
        Mono<PolicyRule> policyRules = dfwOptimizerService.getPolicy(policyID);
        return policyRules;
    }
    /**
     * optimizeServiceByPolicy - Optimize service by a policy ID
     * @param policyID
     *
     */
    @PostMapping
    @RequestMapping("/optimizeServiceByPolicy")
    @ResponseStatus(HttpStatus.OK)
    public void optimizeServiceByPolicy(@RequestParam String policyID) throws Exception {
        dfwOptimizerService.optimizeServiceByPolicy(policyID);
    }
    /**
     * optimizeServiceByRule - Optimize service in a rule of a policy
     * @param policyID
     * @param ruleID
     *
     */
    @PostMapping
    @RequestMapping("/optimizeServiceByRule")
    @ResponseStatus(HttpStatus.OK)
    public void optimizeServiceByRule(@RequestParam String policyID, @RequestParam String ruleID) throws Exception {
        System.out.println("optimizeServiceByRule"+policyID+"----"+ruleID);
        dfwOptimizerService.optimizeServiceByRule(policyID,ruleID);
    }
    /**
     * optimizeServiceAll - Optimize all services
     *
     */
    @PostMapping
    @RequestMapping("/optimizeServiceAll")
    @ResponseStatus(HttpStatus.OK)
    public void optimizeServiceAll() throws Exception {
        dfwOptimizerService.optimizeServiceAll();
    }
}