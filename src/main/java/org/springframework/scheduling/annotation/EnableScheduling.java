﻿/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.scheduling.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.Executor;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * Enables Spring's scheduled task execution capability, similar to
 * functionality found in Spring's {@code <task:*>} XML namespace. To be used
 * on @{@link Configuration} classes as follows:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableScheduling
 * public class AppConfig {
 *     // various &#064;Bean definitions
 * }</pre>
 *
 * This enables detection of @{@link Scheduled} annotations on any Spring-managed
 * bean in the container.  For example, given a class {@code MyTask}
 *
 * <pre class="code">
 * package com.myco.tasks;
 *
 * public class MyTask {
 *     &#064;Scheduled(fixedRate=1000)
 *     public void work() {
 *         // task execution logic
 *     }
 * }</pre>
 *
 * the following configuration would ensure that {@code MyTask.work()} is called
 * once every 1000 ms:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableScheduling
 * public class AppConfig {
 *     &#064;Bean
 *     public MyTask task() {
 *         return new MyTask();
 *     }
 * }</pre>
 *
 * Alternatively, if {@code MyTask} were annotated with {@code @Component}, the
 * following configuration would ensure that its {@code @Scheduled} method is
 * invoked at the desired interval:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;ComponentScan(basePackages="com.myco.tasks")
 * public class AppConfig {
 * }</pre>
 *
 * Methods annotated with {@code @Scheduled} may even be declared directly within
 * {@code @Configuration} classes:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableScheduling
 * public class AppConfig {
 *     &#064;Scheduled(fixedRate=1000)
 *     public void work() {
 *         // task execution logic
 *     }
 * }</pre>
 *
 * In all of the above scenarios, a default single-threaded task executor is used.
 * When more control is desired, a {@code @Configuration} class may implement
 * {@link SchedulingConfigurer}. This allows access to the underlying
 * {@link ScheduledTaskRegistrar} instance. For example, the following example
 * demonstrates how to customize the {@link Executor} used to execute scheduled
 * tasks:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableScheduling
 * public class AppConfig implements SchedulingConfigurer {
 *     &#064;Override
 *     public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
 *         taskRegistrar.setScheduler(taskExecutor());
 *     }
 *
 *     &#064;Bean(destroyMethod="shutdown")
 *     public Executor taskExecutor() {
 *         return Executors.newScheduledThreadPool(100);
 *     }
 * }</pre>
 *
 * Note in the example above the use of {@code @Bean(destroyMethod="shutdown")}. This
 * ensures that the task executor is properly shut down when the Spring application
 * context itself is closed.
 *
 * Implementing {@code SchedulingConfigurer} also allows for fine-grained
 * control over task registration via the {@code ScheduledTaskRegistrar}.
 * For example, the following configures the execution of a particular bean
 * method per a custom {@code Trigger} implementation:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableScheduling
 * public class AppConfig implements SchedulingConfigurer {
 *     &#064;Override
 *     public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
 *         taskRegistrar.setScheduler(taskScheduler());
 *         taskRegistrar.addTriggerTask(
 *             new Runnable() {
 *                 public void run() {
 *                     myTask().work();
 *                 }
 *             },
 *             new CustomTrigger()
 *         );
 *     }
 *
 *     &#064;Bean(destroyMethod="shutdown")
 *     public Executor taskScheduler() {
 *         return Executors.newScheduledThreadPool(42);
 *     }
 *
 *     &#064;Bean
 *     public MyTask myTask() {
 *         return new MyTask();
 *     }
 * }</pre>
 *
 * <p>For reference, the example above can be compared to the following Spring XML
 * configuration:
 * <pre class="code">
 * {@code
 * <beans>
 *     <task:annotation-driven scheduler="taskScheduler"/>
 *     <task:scheduler id="taskScheduler" pool-size="42"/>
 *     <task:scheduled ref="myTask" method="work" fixed-rate="1000"/>
 *     <bean id="myTask" class="com.foo.MyTask"/>
 * </beans>
 * }</pre>
 * the examples are equivalent save that in XML a <em>fixed-rate</em> period is used
 * instead of a custom <em>{@code Trigger}</em> implementation; this is because the
 * {@code task:} namespace {@code scheduled} cannot easily expose such support. This is
 * but one demonstration how the code-based approach allows for maximum configurability
 * through direct access to actual componentry.<p>
 *
 * @author Chris Beams
 * @since 3.1
 * @see Scheduled
 * @see SchedulingConfiguration
 * @see SchedulingConfigurer
 * @see ScheduledTaskRegistrar
 * @see Trigger
 * @see ScheduledAnnotationBeanPostProcessor
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(SchedulingConfiguration.class)
@Documented
public @interface EnableScheduling {

}
