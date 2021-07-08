package com.nsxt.dfwruleoptimizer.service;

import com.nsxt.dfwruleoptimizer.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Service
public class DfwOptimizerService implements IDfwOptimizerService {

    /*WebClient client = WebClient.builder()
            .baseUrl("https://10.25.0.181/policy/api/v1/infra/domains/default")
            .defaultHeaders(header -> header.setBasicAuth("admin", "QOtB!4QpnYZ!fogcUtkE"))
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

     */

    @Autowired
    private WebClient webClient;

    @Autowired
    private RestTemplate myRestTemplate;

    @Value("${policy.url}")
    private String restUrl;


    public Mono<Policy> getAllPolicies() throws Exception {
        return webClient.get()
                .uri("/domains/default/security-policies")
                .retrieve()
                .bodyToMono(Policy.class)
                .timeout(Duration.ofMillis(100000));
    }

    public Mono<PolicyRule> getPolicy(String policyID) throws Exception {
        return webClient.get()
                .uri("/domains/default/security-policies/" + policyID)
                .retrieve()
                .bodyToMono(PolicyRule.class)
                .timeout(Duration.ofMillis(100000));
    }


    public Mono<Rule> getRule(String policyID, String ruleID) throws Exception {

        https:
//10.25.0.181/policy/api/v1/infra/domains/default/security-policies/9a3010c6-9bed-4665-9c85-69134344fa1b/rules/1443

        return webClient.get()
                .uri("/domains/default/security-policies/" + policyID + "/rules/" + ruleID)
                .retrieve()
                .bodyToMono(Rule.class)
                .timeout(Duration.ofMillis(100000));
    }

    public Mono<ServiceEntity> getServiceEntity(String serviceID) throws Exception {

        //10.25.0.181/policy/api/v1/infra/services/DST-TCP-3389/service-entries

        return webClient.get()
                .uri("/services/" + serviceID + "/service-entries")
                .retrieve()
                .bodyToMono(ServiceEntity.class)
                .timeout(Duration.ofMillis(100000));
    }


    public Policy findAll() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth("admin", "QOtB!4QpnYZ!fogcUtkE");
        HttpEntity entity = new HttpEntity(headers);

        ResponseEntity<Policy> response = myRestTemplate.exchange(
                restUrl, HttpMethod.GET, entity, Policy.class);
        //var policy = myRestTemplate.getForObject(restUrl, Policy.class);

        return response.getBody();

    }

    public void optimizeServiceByPolicy(String policyID) throws Exception {
        Mono<PolicyRule> policy = getPolicy(policyID);
        PolicyRule policyRules = policy.block();
        policyRules.getRules().forEach(rules -> optimizeService(rules.getServices()));

    }

    public void optimizeServiceByRule(String policyID, String ruleID) throws Exception {

        System.out.println("Inside optimize");
        try {
            Mono<Rule> policyRule = getRule(policyID, ruleID);
            System.out.println(policyRule);
            Rule rule = policyRule.block();
            optimizeService(rule.getServices());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void optimizeServiceAll() throws Exception {
        Mono<Policy> allPolicies = getAllPolicies();
        Policy policies = allPolicies.block();
        policies.getResults().forEach(policy -> {
                    try {
                        Mono<PolicyRule> allRules = getPolicy(policy.getId());
                        PolicyRule rules = allRules.block();
                        rules.getRules().forEach(rule -> optimizeService(rule.getServices()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );

    }

    public void optimizeService(List<String> services) {

        System.out.println("----------------------");
        Set<String> tcpList = new TreeSet<String>();
        Set<String> udpList = new TreeSet<String>();
        Set<String> ignoredList = new TreeSet<String>();

        services.forEach(service -> {
                    String serviceID = service.substring(service.lastIndexOf("/") + 1);
                    try {
                        if (serviceID.equalsIgnoreCase("ANY")) {
                            ignoredList.add(serviceID);
                        } else {
                            Mono<ServiceEntity> serviceEntities = getServiceEntity(serviceID);
                            ServiceEntity serviceEntity = serviceEntities.block();
                            List<ServiceEntityResults> serviceEntityResultsList = serviceEntity.getResults();

                            if ((!serviceEntityResultsList.isEmpty()) &&
                                    serviceEntityResultsList.size() > 0) {

                                if (serviceEntityResultsList.get(0).get_create_user().
                                        equalsIgnoreCase("system")) {
                                    ignoredList.add(serviceID);
                                } else {

                                    serviceEntityResultsList.forEach(serviceEntityResults -> {
                                        List<String> sourcePortList = new ArrayList<String>();
                                        List<String> destPortList = new ArrayList<String>();
                                        destPortList = serviceEntityResults.getDestination_ports();
                                        String protocol = serviceEntityResults.getL4_protocol();
                                        sourcePortList = serviceEntityResults.getSource_ports();
                                        boolean isMarkedForDeletion = serviceEntityResults.isMarked_for_delete();
                                        if (protocol != null && (!isMarkedForDeletion) &&
                                                ((protocol.equalsIgnoreCase("TCP") ||
                                                        (protocol.equalsIgnoreCase("UDP"))))) {

                                            if (sourcePortList.size() == 0) {
                                                if (destPortList.size() > 0) {
                                                    if (protocol.equalsIgnoreCase("UDP")) {
                                                        udpList.addAll(destPortList);
                                                    }
                                                    if (protocol.equalsIgnoreCase("TCP")) {
                                                        tcpList.addAll(destPortList);
                                                    }
                                                }
                                            }

                                        }else{
                                            ignoredList.add(serviceID+""+serviceEntityResults.getId());
                                            System.out.println("Ignored----"+serviceEntityResults.getId());
                                        }
                                    });
                                }

                            }

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }


        );

        tcpList.forEach(n -> System.out.println("tcp----" + n));
        udpList.forEach(n -> System.out.println("udp----" + n));
        ignoredList.forEach(n -> System.out.println("ignoredList----" + n));
    }


}
