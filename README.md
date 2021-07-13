# NSX-T DFW Rule Optimizer

Contributors : Riaz Mohamed (@riazvm) & Sobana Thirunavukkarasu

## What does this do? 
The application consolidates service entrys in a rule within a security policy which helps reduce rule explosion in 
the Data Pane in NSX. This results in lowering performance issues resulting from multiple rule evaluations. NSX-T allows
a maximum  of 15 service entries for tcp and udp in a single service entity.  We constantly see customer situations where 
this feature is not used and mostly see multiple services being configured for a rule with only one service entry each. 
NSX-T 3x evaluates and correct's there internally but with prior versions of NSX this needs to be done manually. Our 
code helps with consolidating the TCP and UDP entries within a rule. New Services are created with the following ID 
ruleID + "-DST-" + protocol + "-SE-" + counter

NOTE: tcp and udp entries are consolidated into 15 entries each for a rule , ranges are consolidated as a seperate Service 
with 7 entries each.

## Code 
The code is writen in Java Spring boot and containerized as a docker image. 

## Running the application
 
We have build the application and uploaded to docker hub. You could run the public version of the image or build your own and run 

To run the public image which has already been build


1. Clone the github repository

```
https://github.com/riazvm/dfwruleoptimizer

```

2. Install docker and docker-compose if you do not have it already

   
```
sudo apt install docker.io
sudo apt install docker-compose

```

3. Modify the docker-compose.yml file to update the nsx ip , username and password
   

4. Run the application
```
docker-compose up
```

## Modifying and building the application

1. Clone the github repository

```
https://github.com/riazvm/dfwruleoptimizer

```

2. The application was used build with the IntelliJ IDE, you could import the project to the IDE of your choice


Modify as per your requirements and build using the following commands

```
maven clean
maven compile
maven install

docker build -t dfwruleoptimizer:latest 
```

3. Modify the docker-compose.yml file to update the nsx ip , username and password

If change to the image name is required change that as well.
   
4. Run the application
```
docker-compose up
```
## Testing the application

We will be testing the applciation with a Postman client. 



1. OptimizeServiceByPolicy : Optimizes all rules in a policy
   Operation : optimizeServiceByPolicy
   Method : POST
   Param :  policyID 


    ![](./media/image1.png)

2. OptimizeServiceByRule : Optimizes a specific rule in a policy
   Operation : optimizeServiceByRule
   Method : POST
   Param :  policyID 
   Param: ruleID
   
   
   ![](./media/image2.png)

 1. optimizeServiceAll : Optimizes all policies in the system
   Operation : optimizeServiceAll
   Method : POST

