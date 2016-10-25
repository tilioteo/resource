/*
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

package org.hypothesis.resource.context;

import org.hypothesis.resource.core.env.EnvironmentCapable;
import org.hypothesis.resource.core.io.support.ResourcePatternResolver;

/**
 * Central interface to provide configuration for an application. This is
 * read-only while the application is running, but may be reloaded if the
 * implementation supports this.
 *
 * <p>
 * An ApplicationContext provides:
 * <ul>
 * <li>Bean factory methods for accessing application components. Inherited from
 * {@link org.springframework.beans.factory.ListableBeanFactory}.
 * <li>The ability to load file resources in a generic fashion. Inherited from
 * the {@link org.springframework.core.io.ResourceLoader} interface.
 * <li>The ability to publish events to registered listeners. Inherited from the
 * {@link ApplicationEventPublisher} interface.
 * <li>The ability to resolve messages, supporting internationalization.
 * Inherited from the {@link MessageSource} interface.
 * <li>Inheritance from a parent context. Definitions in a descendant context
 * will always take priority. This means, for example, that a single parent
 * context can be used by an entire web application, while each servlet has its
 * own child context that is independent of that of any other servlet.
 * </ul>
 *
 * <p>
 * In addition to standard {@link org.springframework.beans.factory.BeanFactory}
 * lifecycle capabilities, ApplicationContext implementations detect and invoke
 * {@link ApplicationContextAware} beans as well as {@link ResourceLoaderAware},
 * {@link ApplicationEventPublisherAware} and {@link MessageSourceAware} beans.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see ConfigurableApplicationContext
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.core.io.ResourceLoader
 */
public interface ApplicationContext extends EnvironmentCapable, MessageSource, ResourcePatternResolver {

	/**
	 * Return the unique id of this application context.
	 * 
	 * @return the unique id of the context, or {@code null} if none
	 */
	String getId();

	/**
	 * Return a name for the deployed application that this context belongs to.
	 * 
	 * @return a name for the deployed application, or the empty String by
	 *         default
	 */
	String getApplicationName();

	/**
	 * Return a friendly name for this context.
	 * 
	 * @return a display name for this context (never {@code null})
	 */
	String getDisplayName();

	/**
	 * Return the timestamp when this context was first loaded.
	 * 
	 * @return the timestamp (ms) when this context was first loaded
	 */
	long getStartupDate();

	/**
	 * Return the parent context, or {@code null} if there is no parent and this
	 * is the root of the context hierarchy.
	 * 
	 * @return the parent context, or {@code null} if there is no parent
	 */
	ApplicationContext getParent();

}
