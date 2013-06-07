/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.compute.extensions;

import org.jclouds.compute.domain.IpProtocol;
import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.domain.SecurityGroupRule;
import org.jclouds.domain.Location;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * An extension to compute service to allow for the manipulation of {@link SecurityGroup}s. Implementation
 * is optional by providers.
 * 
 * @author Andrew Bayer
 */
public interface SecurityGroupExtension {

   /**
    * Create a new @{link SecurityGroup} from the parameters given.
    *
    * @param name
    *           The name of the security group
    * @param location
    *           The @{link Location} of the security group
    *
    * @return The SecurityGroup that has been created.
    */
   SecurityGroup createSecurityGroup(String name, Location location);

   /**
    * Add a @{link SecurityGroupRule} to an existing @{link SecurityGroup}. Applies the rule to the
    *   security group on the provider.
    *
    * @param rule
    *           The SecurityGroupRule to add.
    * @param group
    *           The SecurityGroup to add the rule to.
    *
    * @return The SecurityGroup with the new rule added, after the rule has been applied on the provider.
    */
   SecurityGroup addRule(SecurityGroupRule rule, SecurityGroup group);

   /**
    * Add a @{link SecurityGroupRule} to an existing @{link SecurityGroup}, based on the parameters given.
    *   Applies the rule to the security group on the provider.
    *
    * @param protocol
    *           The @{link IpProtocol} for the rule.
    * @param startPort
    *           The first port in the range to be opened, or -1 for ICMP.
    * @param endPort
    *           The last port in the range to be opened, or -1 for ICMP.
    * @param ipRanges
    *           An Iterable of Strings representing the IP range(s) the rule should allow.
    * @param groupIds
    *           An Iterable of @{link SecurityGroup} IDs this rule should allow.
    * @param group
    *           The SecurityGroup to add the rule to.
    *
    * @return The SecurityGroup with the new rule added, after the rule has been applied on the provider.
    */
   SecurityGroup addRule(IpProtocol protocol, int startPort, int endPort, Iterable<String> ipRanges,
                         Iterable<String> groupIds, SecurityGroup group);
   
}
