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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.jclouds.compute.domain.IpProtocol;
import org.jclouds.compute.domain.SecurityGroupRule;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;

/**
 * The port range, IP ranges, protocol and instance groups for a rule in a @{link SecurityGroup}.
 * 
 * @author Andrew Bayer
 */
public class SecurityGroupRule {

   @Nullable
   private final String id;
   private final IpProtocol protocol;
   private final int startPort;
   private final int endPort;
   private final Set<String> ipRanges;
   private final Set<String> groupIds;
   
   public SecurityGroupRule(@Nullable String id, IpProtocol protocol, int startPort, int endPort,
                                Iterable<String> ipRanges, Iterable<String> groupIds) {
      this.id = id;
      this.protocol = checkNotNull(protocol, "protocol");
      this.startPort = startPort;
      this.endPort = endPort;
      this.ipRanges = ImmutableSet.copyOf(checkNotNull(ipRanges, "ipRanges"));
      this.groupIds = ImmutableSet.copyOf(checkNotNull(groupIds, "groupIds"));
   }

   /**
    * @return Unique identifier.
    *
    */
   @Nullable
   public String getId() {
      return id;
   }

   /**
    * @return The IP protocol for the rule.
    */
   public IpProtocol getProtocol() {
      return protocol;
   }

   /**
    *
    * @return the start port.
    */
   public int getStartPort() {
      return startPort;
   }

   /**
    *
    * @return the end port.
    */
   public int getEndPort() {
      return endPort;
   }

   /**
    * 
    * @return The set of IP ranges for this rule.
    */
   public Set<String> getIpRanges() {
      return ipRanges;
   }

   /**
    * 
    * @return The set of @{link SecurityGroup} ids for this rule.
    */
   public Set<String> getGroupIds() {
      return groupIds;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      SecurityGroupRule that = SecurityGroupRule.class.cast(o);
      return equal(this.id, that.id) && equal(this.getProtocol(), that.getProtocol()) && equal(this.startPort, that.startPort)
         && equal(this.endPort, that.endPort) && equal(this.getIpRanges(), that.getIpRanges())
         && equal(this.getGroupIds(), that.getGroupIds());
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, protocol, startPort, endPort, ipRanges, groupIds);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("").omitNullValues().add("id", id).add("protocol", getProtocol()).add("startPort", startPort)
         .add("endPort", endPort).add("ipRanges", getIpRanges()).add("groupIds", getGroupIds());
   }

}
