/*
 * Copyright 2011 Daisuke Miyamoto.
 * Created on 2011/11/13
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.springframework.data.mirage.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import jp.sf.amateras.mirage.ClasspathSqlResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * TODO daisuke
 * 
 * @since 1.0
 * @version $Id$
 * @author daisuke
 */
public class ScopeClasspathSqlResource extends ClasspathSqlResource {
	
	private static Logger logger = LoggerFactory.getLogger(ScopeClasspathSqlResource.class);
	
	
	private static boolean existsResource(String absolutePath) {
		if (absolutePath == null) {
			return false;
		}
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		return cl.getResource(absolutePath) != null;
	}
	
	private static String join(List<String> list) {
		StringBuilder sb = new StringBuilder();
		Iterator<String> parts = list.iterator();
		if (parts.hasNext()) {
			sb.append(parts.next());
			while (parts.hasNext()) {
				sb.append("/");
				sb.append(parts.next());
			}
		}
		return sb.toString();
	}
	
	private static String toAbsolutePath(Class<?> scope, String[] names) {
		Assert.notNull(scope);
		Assert.notNull(names);
		Assert.noNullElements(names);
		String packageName = scope != null ? scope.getPackage().getName() : "";
		
		String targetName = null;
		for (String name : names) {
			String currentPath = toAbsolutePath(packageName, name);
			if (existsResource(currentPath)) {
				targetName = name;
				break;
			} else {
				logger.debug("{} not exists", currentPath);
			}
		}
		if (targetName != null) {
			return toAbsolutePath(packageName, targetName);
		} else {
			throw new NoSuchSqlResourceException(scope, names);
		}
	}
	
	private static String toAbsolutePath(final String packageName, final String relativePath) {
		// Is path already absolute?
		if (relativePath.startsWith("/")) {
			return relativePath;
		} else {
			// Break package into list of package names
			List<String> absolutePath = new ArrayList<String>(Arrays.asList(packageName.split("\\.")));
			
			// Break path into folders
			final String[] folders = relativePath.split("[/\\\\]");
			
			// Iterate through folders
			for (String folder : folders) {
				// Up one?
				if ("..".equals(folder)) {
					// Pop off stack
					if (absolutePath.size() > 0) {
						absolutePath.remove(absolutePath.size() - 1);
					} else {
						throw new IllegalArgumentException("Invalid path " + relativePath);
					}
				} else {
					// Add to stack
					absolutePath.add(folder);
				}
			}
			
			// Return absolute path
			return join(absolutePath);
		}
	}
	
	/**
	 * インスタンスを生成する。
	 * 
	 * @param scope 
	 * @param name
	 * @throws NoSuchSqlResourceException 指定したリソースが見つからない場合
	 * @throws IllegalArgumentException 引数に{@code null}を与えた場合
	 * @since 1.0
	 */
	public ScopeClasspathSqlResource(Class<?> scope, String name) {
		this(scope, new String[] {
			name
		});
	}
	
	/**
	 * インスタンスを生成する。
	 * 
	 * @param scope 
	 * @param names
	 * @throws NoSuchSqlResourceException 指定したリソースが見つからない場合
	 * @throws IllegalArgumentException 引数に{@code null}を与えた場合
	 * @since 1.0
	 */
	public ScopeClasspathSqlResource(Class<?> scope, String[] names) {
		super(toAbsolutePath(scope, names));
	}
	
	@Override
	public String toString() {
		return "SimpleSqlResource " + super.toString().substring(20);
	}
}
