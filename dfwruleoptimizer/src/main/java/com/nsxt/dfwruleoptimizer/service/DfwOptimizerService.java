package com.nsxt.dfwruleoptimizer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.nsxt.dfwruleoptimizer.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
/**
 * <h1>DfwOptimizerService</h1>
 * The DFW optimizer Service implements all services to optimize
 * combine services. The current implementations combines all TCP
 * and UDP ports.
 *
 * @author Riaz Mohamed, Sobana T
 * @version 1.0
 * @since 07/13/2021
 */
@Service
public class DfwOptimizerService implements IDfwOptimizerService {

    Logger logger = LoggerFactory.getLogger(DfwOptimizerService.class);

    @Autowired
    private WebClient webClient;

    @Autowired
    private RestTemplate myRestTemplate;
    /**
     * optimizeServiceByPolicy - Invoked when optimizing all services for a  policy
     * Calls optimizeAndCreateServices & updateServiceRule,
     *
     * @param policyID
     * @return void
     */
    public void optimizeServiceByPolicy(String policyID) throws Exception {

        Mono<PolicyRule> policy = getPolicy(policyID);
        PolicyRule policyRules = policy.block();
        policyRules.getRules().forEach(rules -> {
            List<String> ruleServices = new ArrayList<>();
            try {
                ruleServices = optimizeAndCreateServices(rules.getServices(), rules.getId());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            rules.setServices(ruleServices);
            updateServiceRule(policyID, rules);

        });

    }
    /**
     * optimizeServiceByPolicy - Invoked when optimizing services for a specific rule in a  policy
     * Calls optimizeAndCreateServices & updateServiceRule,
     *
     * @param  policyID
     * @param  ruleID
     * @return void
     */
    public void optimizeServiceByRule(String policyID, String ruleID) throws Exception {

        try {


            Mono<Rule> policyRule = getRule(policyID, ruleID);
            System.out.println(policyRule);
            Rule rule = policyRule.block();
            PolicyRuleResults ruleResults = new PolicyRuleResults(rule.getAction(), "Rule", ruleID,
                    rule.getDisplay_name(), rule.getDisplay_name()
                    , rule.getSource_groups(), rule.getDestination_groups(),
                    rule.getServices(), rule.getScope());
            //optimizeService(rule.getServices(),ruleID);
            //optimizeAndCreateServices(rule.getServices(),ruleID);
            List<String> ruleServices = new ArrayList<>();
            try {
                ruleServices = optimizeAndCreateServices(rule.getServices(), policyID);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            ruleResults.setServices(ruleServices);

            updateServiceRule(policyID, ruleResults);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * optimizeServiceByPolicy - Invoked when optimizing services for all policieds in the system
     * Calls optimizeAndCreateServices & updateServiceRule,
     * @return void
     */
    public void optimizeServiceAll() throws Exception {

        Mono<Policy> allPolicies = getAllPolicies();
        Policy policies = allPolicies.block();

        policies.getResults().forEach(policy -> {
            try {
                Mono<PolicyRule> allRules = getPolicy(policy.getId());
                PolicyRule policyRules = allRules.block();
                policyRules.getRules().forEach(rule ->
                {
                    List<String> ruleServices = new ArrayList<>();
                    try {
                        ruleServices = optimizeAndCreateServices(rule.getServices(), policyRules.getId());
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    rule.setServices(ruleServices);
                    updateServiceRule(policy.getId(), rule);

                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }
    /**
     * optimizeAndCreateServices - The service accepts a list of services for a rule and optimizes the
     * services by comibining all the udp entries together and the tcp entries together. Each udp and tcp array
     * size is 15 for individual tcp entries and 7 for ranges
     *
     * The service returns a List of services that have been created and ignores
     *
     * Calls optimizeService & createServiceEntitiesByProtocol
     * @param services
     * @param ruleID
     * @return List<String>
     */
    private List<String> optimizeAndCreateServices(List<String> services, String ruleID) throws JsonProcessingException {
        Map<String, Set<String>> finalServiceMap = optimizeService(services, ruleID);
        List<String> serviceIDList = new ArrayList<>();
        List<String> tcpServiceList = new ArrayList<>();
        List<String> udpServiceList = new ArrayList<>();

        try {


            logger.info("------create and optimize-----" + finalServiceMap.get("tcpSet"));

            tcpServiceList = createServiceEntitiesByProtocol(finalServiceMap.get("tcpSet"), ruleID, "TCP");
            udpServiceList = createServiceEntitiesByProtocol(finalServiceMap.get("udpSet"), ruleID, "UDP");

            tcpServiceList.forEach(tcpServices -> serviceIDList.add(tcpServices));
            udpServiceList.forEach(udpServices -> serviceIDList.add(udpServices));
            finalServiceMap.get("ignoredSvcSet").forEach(ignoredSvc -> serviceIDList.add(ignoredSvc));
            logger.info("in logger --------after creating entries --" + serviceIDList);
            //createUDPServiceEntities();
            //createIgnoredServiceEntities();

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return serviceIDList;
    }
    /**
     * optimizeService - The service accepts a list of services for a rule . Each udp and tcp array
     * size is 15 for individual tcp entries and 7 for ranges. All ignored services are added to a list
     * Sets are used here to avoid duplicates
     *
     * The service returns a List of services that have been created and ignored
     *
     * Calls getServiceEntity & optimizeServiceEntities
     * @param services
     * @param ruleID
     * @return Map<String, Set<String>>
     */
    private Map<String, Set<String>> optimizeService(List<String> services, String ruleID) {
        Set<String> ignoredServicesSet = new TreeSet<String>();

        Map<String, Set<String>> serviceMap = new TreeMap<String, Set<String>>();
        Set<String> tcpPortSet = new TreeSet<String>();
        Set<String> udpPortSet = new TreeSet<String>();
        Set<String> ignoredSveSet = new TreeSet<String>();
        serviceMap.put("ignoredSvcSet", ignoredServicesSet);
        serviceMap.put("tcpSet", tcpPortSet);
        serviceMap.put("udpSet", udpPortSet);
        serviceMap.put("ignoredSveSet", ignoredSveSet);

        try {
            services.forEach(service -> {
                String serviceID = service.substring(service.lastIndexOf("/") + 1);
                if (serviceID.equalsIgnoreCase("ANY")) {
                    ignoredServicesSet.add(serviceID);
                } else {
                    try {
                        Mono<ServiceEntity> serviceEntities = getServiceEntity(serviceID);
                        ServiceEntity serviceEntity = serviceEntities.block();
                        if (serviceEntity != null && serviceEntity.getResults() != null
                                && serviceEntity.getResults().size() > 0
                                && serviceEntity.getResults().get(0).
                                get_create_user().equalsIgnoreCase("system")) {
                            ignoredServicesSet.add("/infra/services/" + serviceID);
                        } else {
                            Map<String, Set<String>> tmpServiceMap = optimizeServiceEntities
                                    (serviceEntity.getResults(), serviceID, serviceMap);
                            serviceMap.get("tcpSet").addAll(tmpServiceMap.get("tcpSet"));
                            serviceMap.get("udpSet").addAll(tmpServiceMap.get("udpSet"));
                            serviceMap.get("ignoredSveSet").addAll(tmpServiceMap.get("ignoredSveSet"));
                            if (tmpServiceMap.get("ignoredSveSet").size() > 0) {
                                ignoredServicesSet.add("/infra/services/" + serviceID);
                            }

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            serviceMap.get("ignoredSvcSet").addAll(ignoredServicesSet);
            logger.info("tcpSet - for ruleID " + ruleID + serviceMap.get("tcpSet"));
            logger.info("udpSet - for ruleID " + ruleID + serviceMap.get("udpSet"));
            logger.info("ignoredSveSet - for ruleID " + ruleID + serviceMap.get("ignoredSveSet"));
            logger.info("ignoredSvcSet - for ruleID " + ruleID + serviceMap.get("ignoredSvcSet"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serviceMap;
    }
    /**
     * optimizeServiceEntities - The service accepts a list of service entries and combines all
     * udp , tcp and other service entries
     *
     * The service returns a map with a set of combined tcp, udp and other service sets
     *
     * @param serviceEntityResultsList
     * @param serviceID
     * @param serviceMap
     * @return Map<String, Set<String>>
     */
    private Map<String, Set<String>> optimizeServiceEntities
            (List<ServiceEntityResults> serviceEntityResultsList, String serviceID,
             Map<String, Set<String>> serviceMap) {

        List<List<String>> tcpDestinationList = new ArrayList<>();
        Set<String> udpDestinationSet = serviceMap.get("udpSet");
        Set<String> ignoredServiceEntitySet = serviceMap.get("ignoredSveSet");
        Set<String> tcpDestinationSet = serviceMap.get("tcpSet");


        try {

            serviceEntityResultsList.parallelStream().filter(ser ->
                    (ser.getL4_protocol() != null
                            && ser.getL4_protocol().equalsIgnoreCase("TCP")) &&
                            (!ser.marked_for_delete)).map(ser1 -> ser1.getDestination_ports()).
                    forEach(tcpPort -> tcpPort.forEach(port1 -> tcpDestinationSet.add(port1)));

            //logger.info("tcpDestinationSet--forserviceID---"+serviceID+"---tcpport----"+tcpDestinationSet);

            serviceEntityResultsList.parallelStream().filter(ser ->
                    (ser.getL4_protocol() != null
                            && ser.getL4_protocol().equalsIgnoreCase("UDP")) &&
                            (!ser.marked_for_delete)).map(ser1 -> ser1.getDestination_ports()).
                    forEach(tcpPort -> tcpPort.forEach(port1 -> udpDestinationSet.add(port1)));

            // logger.info("updDestinationSet--forserviceID---"+serviceID+"---udpport----"+udpDestinationSet);

            ignoredServiceEntitySet.addAll(serviceEntityResultsList.parallelStream().filter(ser ->
                    ((ser.getL4_protocol() == null || (ser.marked_for_delete)))).map(ser1 -> ser1.getId()).
                    collect(Collectors.toSet()));


            ignoredServiceEntitySet.addAll(serviceEntityResultsList.parallelStream().filter(ser ->
                    ((ser.getL4_protocol() == null || (ser.marked_for_delete)))).map(ser1 -> ser1.getId()).
                    collect(Collectors.toSet()));

           /* logger.info("ignoredServiceEntitySet--forserviceID---"+
                    serviceID+"---ignoredServiceEntitySet----"+ignoredServiceEntitySet);*/

            serviceMap.put("tcpSet", tcpDestinationSet);
            serviceMap.put("udpSet", udpDestinationSet);
            serviceMap.put("ignoredSveSet", ignoredServiceEntitySet);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return serviceMap;
    }
    /**
     * createServiceEntitiesByProtocol - The service accepts a set of protocol sets and splits the tcp and udp sets
     * into a set of 15 entries and 7 for ranges
     * Returns a List of services created
     *
     * @param protocolSet
     * @param ruleID
     * @param protocol
     * @return Map<String, Set<String>>
     */
    private List<String> createServiceEntitiesByProtocol(Set<String> protocolSet, String ruleID, String protocol) throws JsonProcessingException {
        List<List<String>> protocolFinalSubList = new ArrayList<>();
        try {
            if (!protocolSet.isEmpty()) {
                List<String> protocolRangePorts = new ArrayList<>();
                List<String> protocolPorts = new ArrayList<>();
                List<List<String>> protocolPortSubList = new ArrayList<>();
                List<List<String>> protocolPortRangeSubList = new ArrayList<>();


                logger.info("------in createProtocolServiceEntities----" + protocolSet);
                Map<Boolean, List<String>> partitionedTcpMap =
                        protocolSet.stream().collect(Collectors.partitioningBy(protocolPort -> protocolPort.contains("-")));
                protocolRangePorts = partitionedTcpMap.get(true);
                logger.info("------Range Sub with (-)----" + protocolRangePorts);
                protocolPorts = partitionedTcpMap.get(false);


                if (protocolPorts.size() > 0) {
                    protocolPortSubList = Lists.partition(protocolPorts, 15);
                    if (protocolPortSubList.size() > 0) {
                        protocolFinalSubList.addAll(protocolPortSubList);
                    }
                }
                if (protocolRangePorts.size() > 0) {
                    protocolPortRangeSubList = Lists.partition(protocolRangePorts, 7);
                    if (protocolPortRangeSubList.size() > 0) {
                        protocolFinalSubList.addAll(protocolPortRangeSubList);
                    }
                }

                logger.info("------RangeSub All----" + protocolFinalSubList);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return createInfraServices(protocolFinalSubList, ruleID, protocol);
    }
    /**
     * createInfraServices - The service accepts a List of and creates services and service
     * entities
     * Returns a List of services created
     *
     * @param protocolPortSubList
     * @param ruleID
     * @param protocol
     * @return Map<String, Set<String>>
     */
    private List<String> createInfraServices(List<List<String>> protocolPortSubList, String ruleID, String protocol) throws JsonProcessingException {

        AtomicInteger counter = new AtomicInteger();
        ObjectMapper mapper = new ObjectMapper();
        List<InventoryServiceRow> inventoryServiceRowList = new ArrayList<InventoryServiceRow>();
        List<String> serviceIDList = new ArrayList<>();
        protocolPortSubList.forEach(tcpDestPortList -> {
            try {
                List<InventoryService> serviceList = new ArrayList<InventoryService>();
                List<InventoryServiceEntryRow> serviceEntryRowList = new ArrayList<InventoryServiceEntryRow>();
                List<InventoryServiceEntry> serviceEntryList = new ArrayList<>();
                List<String> sourcePorts = new ArrayList<>();
                String serviceEntryID = ruleID + "-DST-" + protocol + "-SE-" + counter;
                String serviceID = ruleID + "-DST-" + protocol + "-" + counter;
                InventoryServiceEntry serviceEntry = new InventoryServiceEntry(protocol, sourcePorts,
                        tcpDestPortList, "L4PortSetServiceEntry", serviceEntryID,
                        serviceEntryID, false);
                //serviceEntryList.add(serviceEntry);

                InventoryServiceEntryRow serviceEntryRow = new InventoryServiceEntryRow
                        (serviceEntry, "ChildServiceEntry", false);
                serviceEntryRowList.add(serviceEntryRow);

                InventoryService service = new InventoryService
                        (false, "Service", serviceID, serviceID, serviceEntryRowList);
                //serviceList.add(service);

                InventoryServiceRow inventoryServiceRow = new InventoryServiceRow(service, "ChildService", false);
                inventoryServiceRowList.add(inventoryServiceRow);
                // logger.info("-----Invetory RowList List ----"+mapper.writeValueAsString(inventoryServiceRowList));
                counter.getAndIncrement();

                serviceIDList.add("/infra/services/" + serviceID);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        InventoryServiceGroup serviceGroup = new InventoryServiceGroup("Infra", inventoryServiceRowList);

        InventoryServiceGroup isg = Mono.just(serviceGroup).block();
        logger.info("-----Before calling webClient----" + mapper.writeValueAsString(isg));
        try {
            Mono<String> webclientResponse =
                    webClient.patch()
                            .uri("/")
                            //.body(Mono.just(serviceGroup),InventoryServiceGroup.class)
                            .bodyValue(serviceGroup)
                            .retrieve()
                            .bodyToMono(String.class).timeout(Duration.ofMillis(100000));

            logger.info("---After webclient call ----" + webclientResponse.block());


        } catch (Exception e) {
            e.printStackTrace();
        }
        return serviceIDList;
    }
    /**
     * updateServiceRule - Updates the existing rule with the newly created service and the ignored service list
     *
     * @param policyID
     * @param rule
     *
     */
    private void updateServiceRule(String policyID, PolicyRuleResults rule) {
        ObjectMapper mapper = new ObjectMapper();
        //rule.setServices(ruleServices);
        try {
            logger.info("---Before update webclient call ----" + mapper.writeValueAsString(rule));
            Mono<String> webclientResponse =
                    webClient.patch()
                            .uri("/domains/default/security-policies/" + policyID + "/rules/" + rule.getId())
                            //.body(Mono.just(serviceGroup),InventoryServiceGroup.class)
                            .bodyValue(rule)
                            .retrieve()
                            .bodyToMono(String.class).timeout(Duration.ofMillis(100000));

            logger.info("---After webclient call ----" + webclientResponse.block());


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void createUDPServiceEntities() {

    }

    private void createIgnoredServiceEntities() {
    }

    private void updateRule() {

    }



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

        return webClient.get()
                .uri("/domains/default/security-policies/" + policyID + "/rules/" + ruleID)
                .retrieve()
                .bodyToMono(Rule.class)
                .timeout(Duration.ofMillis(100000));
    }

    public Mono<ServiceEntity> getServiceEntity(String serviceID) throws Exception {

        return webClient.get()
                .uri("/services/" + serviceID + "/service-entries")
                .retrieve()
                .bodyToMono(ServiceEntity.class)
                .timeout(Duration.ofMillis(100000));
    }


}
