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
package org.jclouds.compute.domain;

import java.util.Set;

import org.jclouds.compute.domain.internal.SecurityGroupRuleImpl;
import org.jclouds.javax.annotation.Nullable;

import com.google.inject.ImplementedBy;

/**
 * The port range, IP ranges, protocol and instance groups for a rule in a @{link SecurityGroup}.
 * 
 * @author Andrew Bayer
 */
@ImplementedBy(SecurityGroupRuleImpl.class)
public interface SecurityGroupRule {

   /**
    * @return Unique identifier.
    *
    */
   @Nullable
   String getId();

   /**
    * @return The IP protocol for the rule.
    */
   IpProtocol getProtocol();

   /**
    *
    * @return the start port.
    */
   int getStartPort();

   /**
    *
    * @return the end port.
    */
   int getEndPort();

   /**
    * 
    * @return The set of IP ranges for this rule.
    */
   Set<String> getIpRanges();

   /**
    * 
    * @return The set of instance group IDs for this rule.
    */
   Set<String> getGroupIds();


}
