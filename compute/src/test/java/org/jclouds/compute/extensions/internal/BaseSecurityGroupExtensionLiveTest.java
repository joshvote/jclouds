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
package org.jclouds.compute.extensions.internal;

import static java.util.concurrent.TimeUnit.SECONDS;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.filter;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.domain.SecurityGroupBuilder;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.extensions.SecurityGroupExtension;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.net.domain.IpProtocol;
import org.jclouds.ssh.SshClient;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;


/**
 * Base test for {@link SecurityGroupExtension} implementations.
 * 
 * @author David Alves
 * 
 */
public abstract class BaseSecurityGroupExtensionLiveTest extends BaseComputeServiceContextLiveTest {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected String groupId;

   /**
    * Returns the template for the base node, override to test different templates.
    * 
    * @return
    */
   public Template getNodeTemplate() {
      return view.getComputeService().templateBuilder().build();
   }

      

   @Test(groups = { "integration", "live" }, singleThreaded = true)
   public void testCreateSecurityGroup() throws RunNodesException, InterruptedException, ExecutionException {

      ComputeService computeService = view.getComputeService();

      Location location = getNodeTemplate().getLocation();
      
      Optional<SecurityGroupExtension> securityGroupExtension = computeService.getSecurityGroupExtension();

      assertTrue(securityGroupExtension.isPresent(), "security extension was not present");

      SecurityGroup group = securityGroupExtension.get().createSecurityGroup("test-create-security-group", location);

      logger.info("Group created: %s", group);

      assertEquals("test-create-security-group", group.getName());

      groupId = group.getId();
   }

   @Test(groups = { "integration", "live" }, singleThreaded = true, dependsOnMethods = "testCreateSecurityGroup")
   public void testAddIpPermission() {

      ComputeService computeService = view.getComputeService();

      Optional<SecurityGroupExtension> securityGroupExtension = computeService.getSecurityGroupExtension();
      assertTrue(securityGroupExtension.isPresent(), "security group extension was not present");

      Optional<SecurityGroup> optGroup = getGroup(securityGroupExtension.get());

      assertTrue(optGroup.isPresent());

      SecurityGroup group = optGroup.get();

      IpPermission.Builder builder = IpPermission.builder();

      builder.ipProtocol(IpProtocol.TCP);
      builder.fromPort(10);
      builder.toPort(20);
      builder.cidrBlock("0.0.0.0/0");

      IpPermission perm = builder.build();

      SecurityGroup newGroup = securityGroupExtension.get().addIpPermission(perm, group);

      assertEquals(perm, Iterables.getOnlyElement(newGroup.getIpPermissions())); 
   }
   
   @Test(groups = { "integration", "live" }, singleThreaded = true, dependsOnMethods = "testAddIpPermission")
   public void testRemoveIpPermission() {

      ComputeService computeService = view.getComputeService();

      Optional<SecurityGroupExtension> securityGroupExtension = computeService.getSecurityGroupExtension();
      assertTrue(securityGroupExtension.isPresent(), "security group extension was not present");

      Optional<SecurityGroup> optGroup = getGroup(securityGroupExtension.get());

      assertTrue(optGroup.isPresent());

      SecurityGroup group = optGroup.get();

      IpPermission.Builder builder = IpPermission.builder();

      builder.ipProtocol(IpProtocol.TCP);
      builder.fromPort(10);
      builder.toPort(20);
      builder.cidrBlock("0.0.0.0/0");

      IpPermission perm = builder.build();

      SecurityGroup newGroup = securityGroupExtension.get().removeIpPermission(perm, group);

      assertEquals(0, Iterables.size(newGroup.getIpPermissions())); 
   }

   @Test(groups = { "integration", "live" }, singleThreaded = true, dependsOnMethods = "testRemoveIpPermission")
   public void testAddIpPermissionsFromSpec() {

      ComputeService computeService = view.getComputeService();

      Optional<SecurityGroupExtension> securityGroupExtension = computeService.getSecurityGroupExtension();
      assertTrue(securityGroupExtension.isPresent(), "security group extension was not present");

      Optional<SecurityGroup> optGroup = getGroup(securityGroupExtension.get());

      assertTrue(optGroup.isPresent());

      SecurityGroup group = optGroup.get();

      IpPermission.Builder builder = IpPermission.builder();

      builder.ipProtocol(IpProtocol.TCP);
      builder.fromPort(10);
      builder.toPort(20);
      builder.cidrBlock("0.0.0.0/0");

      IpPermission perm = builder.build();

      IpPermission.Builder secondBuilder = IpPermission.builder();

      secondBuilder.ipProtocol(IpProtocol.TCP);
      secondBuilder.fromPort(50);
      secondBuilder.toPort(100);
      secondBuilder.groupId("abc-123");

      IpPermission secondPerm = secondBuilder.build();

      SecurityGroup newGroup = securityGroupExtension.get().addIpPermission(IpProtocol.TCP,
                                                                            10,
                                                                            20,
                                                                            emptyMultimap(),
                                                                            ImmutableSet.of("0.0.0.0/0"),
                                                                            emptyStringSet(),
                                                                            group);

      assertTrue(newGroup.getIpPermissions().contains(perm)); 

      SecurityGroup secondNewGroup = securityGroupExtension.get().addIpPermission(IpProtocol.TCP,
                                                                                  50,
                                                                                  100,
                                                                                  emptyMultimap(),
                                                                                  emptyStringSet(),
                                                                                  ImmutableSet.of("abc-123"),
                                                                                  newGroup);

      assertTrue(secondNewGroup.getIpPermissions().contains(secondPerm)); 
   }
   
   @Test(groups = { "integration", "live" }, singleThreaded = true, dependsOnMethods = "testAddIpPermissionsFromSpec")
   public void testCreateNodeWithSecurityGroup() throws RunNodesException, InterruptedException, ExecutionException {

      ComputeService computeService = view.getComputeService();

      Optional<SecurityGroupExtension> securityGroupExtension = computeService.getSecurityGroupExtension();

      assertTrue(securityGroupExtension.isPresent(), "security group extension was not present");

      Template template = view.getComputeService().templateBuilder()
         .options(TemplateOptions.Builder.securityGroups(groupId))
         .build();
      
      NodeMetadata node = Iterables.getOnlyElement(computeService.createNodesInGroup("test-create-node-with-group", 1, template));

      Set<SecurityGroup> groups = securityGroupExtension.get().listSecurityGroupsForNode(node.getId());

      assertTrue(groups.size() > 0, "node has no groups");
      
      Optional<SecurityGroup> secGroup = Iterables.tryFind(securityGroupExtension.get().listSecurityGroupsForNode(node.getId()),
                                                           new Predicate<SecurityGroup>() {
                                                              @Override
                                                              public boolean apply(SecurityGroup input) {
                                                                 return input.getId().equals(groupId);
                                                              }
                                                           });

      assertTrue(secGroup.isPresent());

      computeService.destroyNode(node.getId());
   }

   @Test(groups = { "integration", "live" }, singleThreaded = true, dependsOnMethods = "testCreateNodeWithSecurityGroup")
   public void testDeleteSecurityGroup() {

      ComputeService computeService = view.getComputeService();

      Optional<SecurityGroupExtension> securityGroupExtension = computeService.getSecurityGroupExtension();
      assertTrue(securityGroupExtension.isPresent(), "security group extension was not present");

      Optional<SecurityGroup> optGroup = getGroup(securityGroupExtension.get());

      assertTrue(optGroup.isPresent());

      SecurityGroup group = optGroup.get();

      assertTrue(securityGroupExtension.get().removeSecurityGroup(group.getId()));
   }

   private Multimap<String, String> emptyMultimap() {
      return LinkedHashMultimap.create();
   }

   private Set<String> emptyStringSet() {
      return Sets.newLinkedHashSet();
   }
   
   private Optional<SecurityGroup> getGroup(SecurityGroupExtension ext) {
      return Iterables.tryFind(ext.listSecurityGroups(), new Predicate<SecurityGroup>() {
         @Override
         public boolean apply(SecurityGroup input) {
            return input.getId().equals(groupId);
         }
      });
   }
}
