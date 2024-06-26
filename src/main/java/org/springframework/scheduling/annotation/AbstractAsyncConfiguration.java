﻿/*
 * Copyright 2002-2013 the original author or authors.
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

import java.util.Collection;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * Abstract base {@code Configuration} class providing common structure for enabling
 * Spring's asynchronous method execution capability.
 *
 * @author Chris Beams
 * @since 3.1
 * @see EnableAsync
 */
@Configuration
public abstract class AbstractAsyncConfiguration implements ImportAware {

	protected AnnotationAttributes enableAsync;

	protected Executor executor;


	public void setImportMetadata(AnnotationMetadata importMetadata) {
		this.enableAsync = AnnotationAttributes.fromMap(
				importMetadata.getAnnotationAttributes(EnableAsync.class.getName(), false));
		Assert.notNull(this.enableAsync,
				"@EnableAsync is not present on importing class " + importMetadata.getClassName());
	}

	/**
	 * Collect any {@link AsyncConfigurer} beans through autowiring.
	 */
	@Autowired(required = false)
	void setConfigurers(Collection<AsyncConfigurer> configurers) {
		if (CollectionUtils.isEmpty(configurers)) {
			return;
		}
		if (configurers.size() > 1) {
			throw new IllegalStateException("Only one AsyncConfigurer may exist");
		}
		AsyncConfigurer configurer = configurers.iterator().next();
		this.executor = configurer.getAsyncExecutor();
	}


	/**
	 * The component that will apply async execution advice to beans annotated with
	 * the async annotation. Subclasses will provide either a BeanPostProcessor in
	 * the case of proxy-based advice, or an AspectJ aspect if weaving is preferred.
	 */
	public abstract Object asyncAdvisor();

}
