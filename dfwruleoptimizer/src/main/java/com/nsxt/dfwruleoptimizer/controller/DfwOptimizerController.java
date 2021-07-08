package com.nsxt.dfwruleoptimizer.controller;

import com.nsxt.dfwruleoptimizer.model.Policy;
import com.nsxt.dfwruleoptimizer.model.PolicyRule;
import com.nsxt.dfwruleoptimizer.model.PolicyRuleResults;
import com.nsxt.dfwruleoptimizer.model.ServiceEntity;
import com.nsxt.dfwruleoptimizer.service.DfwOptimizerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class DfwOptimizerController {
    @Autowired
    private DfwOptimizerService dfwOptimizerService;

    @RequestMapping("/health")
    public String getHealth() throws InterruptedException {
        return "Ok";
    }
    /*
    @RequestMapping("/findAll")
    @ResponseStatus(HttpStatus.OK)
    public Policy findAll() {
        System.out.println("hello 1");
        return dfwOptimizerService.findAll();

    }*/

    @RequestMapping("/getAllPolicies")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Policy> getAllPolicies() throws Exception {
        Mono<Policy> policy = dfwOptimizerService.getAllPolicies();
        return policy;

    }



    @RequestMapping("/getPolicy")
    @ResponseStatus(HttpStatus.OK)
    public Mono<PolicyRule> getPolicy(@RequestParam String policyID) throws Exception {
        Mono<PolicyRule> policyRules = dfwOptimizerService.getPolicy(policyID);
        return policyRules;

    }

    /*@RequestMapping("/getServiceEntity")
    @ResponseStatus(HttpStatus.OK)
    public  Mono<ServiceEntity> getServiceEntity(@RequestParam String serviceID) throws Exception {
        Mono<ServiceEntity> serviceEntity = dfwOptimizerService.getServiceEntity(serviceID);
        return serviceEntity;

    }*/

    @RequestMapping("/optimizeServiceByPolicy")
    @ResponseStatus(HttpStatus.OK)
    public void optimizeServiceByPolicy(@RequestParam String policyID) throws Exception {
        dfwOptimizerService.optimizeServiceByPolicy(policyID);

    }

    @RequestMapping("/optimizeServiceByRule")
    @ResponseStatus(HttpStatus.OK)
    public void optimizeServiceByRule(@RequestParam String policyID, @RequestParam String ruleID) throws Exception {
        System.out.println("optimizeServiceByRule"+policyID+"----"+ruleID);
        dfwOptimizerService.optimizeServiceByRule(policyID,ruleID);

    }

    @RequestMapping("/optimizeServiceAll")
    @ResponseStatus(HttpStatus.OK)
    public void optimizeServiceAll() throws Exception {

        dfwOptimizerService.optimizeServiceAll();


    }


}